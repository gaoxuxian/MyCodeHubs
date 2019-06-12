package com.xx.avlibrary.player;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.TextureView;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.xx.avlibrary.player.impl.DecodeHandler;
import com.xx.avlibrary.player.entry.AudioDecodeInfo;
import com.xx.avlibrary.player.entry.VideoDecodeInfo;
import com.xx.avlibrary.player.impl.DecodeTask;
import com.xx.avlibrary.player.port.IControl;
import com.xx.avlibrary.player.port.IPlayer;
import com.xx.avlibrary.player.port.decode.IAudioDecoder;
import com.xx.avlibrary.player.port.decode.IVideoDecoder;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class PluginVideoView extends ConstraintLayout implements TextureView.SurfaceTextureListener, IPlayer {
    public static final String prepare_key_audio_path = "audio_path";
    public static final String prepare_key_video_path = "video_path";
    private final Object state_lock;
    private volatile Status mPlayerState;
    private volatile DrawInfo mDrawInfo;
    private volatile AudioInfo mAudioInfo;

    private static final int handle_prepare = 7776;
    private static final int handle_pause = 7777;
    private static final int handle_progress = 7778;
    private static final int handle_complete = 7779;

    private final Object decode_lock;
    private volatile boolean mVideoCompleted;
    private volatile boolean mAudioCompleted;

    private CyclicBarrier mPrepareBarrier;
    private CyclicBarrier mPausedBarrier;

    public PluginVideoView(Context context) {
        super(context);
        state_lock = new Object();
        decode_lock = new Object();
        initView(context);
    }

    private void setPlayerState(Status state) {
        synchronized (state_lock) {
            mPlayerState = state;
        }
    }

    private Status getPlayerState() {
        return mPlayerState;
    }

    public void initPlayerRelated() {
        mDrawInfo = new DrawInfo();
        mAudioInfo = new AudioInfo();
        initThreadRelated();
    }

    private volatile RenderThread mRendererThread;
    private VideoDecodeTask mVideoDecodeTask;
    private AudioDecodeTask mAudioDecodeTask;
    private Handler mHandler;

    private void initThreadRelated() {
        mPrepareBarrier = new CyclicBarrier(2, () -> {
            setPlayerState(Status.prepared);
            Message.obtain(mHandler, handle_prepare).sendToTarget();
        });

        mPausedBarrier = new CyclicBarrier(2, () -> {
            setPlayerState(Status.paused);
            Message.obtain(mHandler, handle_pause).sendToTarget();
        });

        mHandler = new Handler(msg -> {
            PlayerListener listener = accessPlayerListener();
            if (listener != null) {
                switch (msg.what) {
                    case handle_prepare: {
                        listener.onPrepared();
                        break;
                    }
                    case handle_progress: {
                        listener.onProgress();
                        break;
                    }
                    case handle_pause: {
                        listener.onPaused();
                        break;
                    }
                    case handle_complete: {
                        listener.onCompleted();
                        break;
                    }
                }
            }
            return true;
        });

        mVideoDecodeTask = new VideoDecodeTask(this, new DecodeListener<VideoDecodeInfo>() {
            @Override
            public void prepared() {
                awaitCyclicBarrier(mPrepareBarrier);
            }

            @Override
            public void accessData(VideoDecodeInfo info) {
                if (info != null) {
                    mDrawInfo.fgFrame = info.fgFrame;
                    mDrawInfo.preFrame = info.preFrame;
                    mDrawInfo.curFrame = info.curFrame;
                    mDrawInfo.pts = info.pts;
                    ITimestamp timestampMgr = accessTimestampMgr();
                    if (timestampMgr != null) {
                        long syncTime = timestampMgr.syncAVTimestamp(info.pts);
                        if (syncTime > 30){ // 视频超前
                            try {
                                if ((syncTime - 30) > 5) {
                                    Thread.sleep(syncTime - 30 - 5);
                                } else {
                                    Thread.sleep(syncTime - 30);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    requestRenderer(mDrawInfo);
                }
                mVideoDecodeTask.play();
            }

            @Override
            public void paused() {
                awaitCyclicBarrier(mPausedBarrier);
            }

            @Override
            public void completed() {
                synchronized (decode_lock) {
                    mVideoCompleted = true;
                    if (mVideoCompleted && mAudioCompleted) {
                        setPlayerState(Status.complete);
                        Message.obtain(mHandler, handle_complete).sendToTarget();
                    }
                }
            }
        });
        mAudioDecodeTask = new AudioDecodeTask(this, new DecodeListener<AudioDecodeInfo>() {
            @Override
            public void prepared() {
                awaitCyclicBarrier(mPrepareBarrier);
            }

            @Override
            public void accessData(AudioDecodeInfo info) {
                setPlayerState(Status.playing);
                if (info != null) {
                    mAudioInfo.data = info.data;
                    mAudioInfo.channels = info.channels;
                    mAudioInfo.sampleRate = info.sampleRate;
                    mAudioInfo.pts = info.pts;
                    ITimestamp timestampMgr = accessTimestampMgr();
                    if (timestampMgr != null) {
                        timestampMgr.updateAudioTimestamp(info.pts);
                    }
                    playAudio(mAudioInfo);
                }
                mAudioDecodeTask.play();
                mHandler.removeMessages(handle_progress);
                Message.obtain(mHandler, handle_progress, info != null ? info.pts : 0).sendToTarget();
            }

            @Override
            public void paused() {
                awaitCyclicBarrier(mPausedBarrier);
            }

            @Override
            public void completed() {
                synchronized (decode_lock) {
                    mAudioCompleted = true;
                    if (mVideoCompleted && mAudioCompleted) {
                        setPlayerState(Status.complete);
                        Message.obtain(mHandler, handle_complete).sendToTarget();
                    }
                }
            }
        });

        new Thread(mVideoDecodeTask).start();
        new Thread(mAudioDecodeTask).start();

        mRendererThread = new RenderThread(this);
        mRendererThread.setPreserveEGLOnPause(true);
        mRendererThread.start();
    }

    private void awaitCyclicBarrier(CyclicBarrier cyclicBarrier) {
        if (cyclicBarrier != null) {
            try {
                cyclicBarrier.await();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private PlayerListener accessPlayerListener() {
        IControl control = getPlayerController();
        if (control != null) {
            return control.getPlayerListener();
        }
        return null;
    }

    private ITimestamp accessTimestampMgr() {
        IControl control = getPlayerController();
        if (control != null) {
            return control.getTimestampManager();
        }
        return null;
    }

    public void requestRenderer() {
        requestRenderer(mDrawInfo);
    }

    void requestRenderer(DrawInfo info) {
        if (mRendererThread != null) {
            mRendererThread.requestRender(info);
        }
    }

    private AudioTrack mAudioPlayer;

    void playAudio(AudioInfo info) {
        if (info != null) {
            byte[] data = info.data;
            int sampleRate = info.sampleRate;
            int channels = info.channels;
            if (mAudioPlayer == null) {
                int minBufferSize = AudioTrack.getMinBufferSize(sampleRate,
                        channels > 1 ? AudioFormat.CHANNEL_OUT_STEREO : AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
                // 传入 AudioTrack 的 minBufferSize 改成 minBufferSize * 2 为了解决重复播放时，尾音可能出现 杂音、卡 的问题
                mAudioPlayer = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
                        channels > 1 ? AudioFormat.CHANNEL_OUT_STEREO : AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 2, AudioTrack.MODE_STREAM);
                mAudioPlayer.play();
            }
            mAudioPlayer.write(data, 0, data.length);
        }
    }

    private TextureView mVideoPlayer;

    private void initView(Context context) {
        mVideoPlayer = new TextureView(context);
        mVideoPlayer.setId(View.generateViewId());
        mVideoPlayer.setSurfaceTextureListener(this);
        ConstraintLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_CONSTRAINT, LayoutParams.MATCH_CONSTRAINT);
        this.addView(mVideoPlayer, params);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mRendererThread != null) {
            mRendererThread.onSurfaceCreated(surface);
            mRendererThread.onSurfaceChanged(width, height);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (mRendererThread != null) {
            mRendererThread.onSurfaceChanged(width, height);
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void prepare(HashMap<String, Object> params) {
        if (params != null && canPrepare() && mPlayerController != null) {
            mPrepareBarrier.reset();
            setPlayerState(Status.prepare);
            Object value;
            value = params.get(prepare_key_audio_path);
            mAudioDecodeTask.prepare(value);
            value = params.get(prepare_key_video_path);
            mVideoDecodeTask.prepare(value);
        }
    }

    private boolean canPrepare() {
        Status state = getPlayerState();
        return state == Status.idle || state == Status.paused;
    }

    @Override
    public boolean isPreparing() {
        Status state = getPlayerState();
        return state == Status.prepare;
    }

    @Override
    public void play() {
        if (canPlay() && mPlayerController != null) {
            mAudioDecodeTask.play();
            mVideoDecodeTask.play();
        }
    }

    private boolean canPlay() {
        Status state = getPlayerState();
        return state == Status.prepared || state == Status.paused || state == Status.stopped;
    }

    @Override
    public boolean isPlaying() {
        Status state = getPlayerState();
        return state == Status.playing;
    }

    @Override
    public void pause() {
        if (canPause() && mPlayerController != null) {
            mPausedBarrier.reset();
            mAudioDecodeTask.pause();
            mVideoDecodeTask.pause();
        }
    }

    private boolean canPause() {
        Status state = getPlayerState();
        return state == Status.playing;
    }

    @Override
    public boolean isPaused() {
        Status state = getPlayerState();
        return state == Status.paused;
    }

    @Override
    public void stop() {
        mAudioDecodeTask.end();
        mVideoDecodeTask.end();
    }

    @Override
    public boolean isStopped() {
        Status state = getPlayerState();
        return state == Status.stopped;
    }

    private volatile IControl mPlayerController;

    /**
     *
     */
    public void setPlayerController(IControl controller) {
        mPlayerController = controller;
    }

    IControl getPlayerController() {
        return mPlayerController;
    }

    public interface PlayerListener {
        void onPrepared();
        void onProgress();
        void onPaused();
        void onCompleted();
        void onStopped();
    }

    public interface Renderer {
        void onSurfaceCreated();
        void onSurfaceChanged(int width, int height);
        void onDrawFrame(@NonNull DrawInfo info);
        void onSurfaceDestroyed();
    }

    public static class DrawInfo {
        public byte[] fgFrame;
        public byte[] preFrame;
        public byte[] curFrame;
        int pts;
    }

    static class AudioInfo {
        byte[] data;
        int sampleRate;
        int channels;
        int pts;
    }

    static class AudioDecodeTask extends DecodeTask<AudioDecodeHandler> {
        private WeakReference<PluginVideoView> mRendererViewWRF;
        private WeakReference<DecodeListener<AudioDecodeInfo>> mListenerWRF;

        AudioDecodeTask(PluginVideoView rendererView, DecodeListener<AudioDecodeInfo> listener) {
            this.mRendererViewWRF = new WeakReference<>(rendererView);
            this.mListenerWRF = new WeakReference<>(listener);
        }

        @Override
        protected AudioDecodeHandler initHandler() {
            return new AudioDecodeHandler(Looper.myLooper(), mRendererViewWRF, mListenerWRF);
        }
    }

    static class AudioDecodeHandler extends DecodeHandler {
        private WeakReference<PluginVideoView> mRendererView;
        private WeakReference<DecodeListener<AudioDecodeInfo>> mListener;

        AudioDecodeHandler(Looper looper, WeakReference<PluginVideoView> rendererView, WeakReference<DecodeListener<AudioDecodeInfo>> listener) {
            super(looper);
            this.mRendererView = rendererView;
            this.mListener = listener;
        }

        @Override
        protected void handlePrepare(Message msg) {
            IAudioDecoder decoder = accessDecoder();
            if (decoder != null) {
                decoder.prepare(msg.obj);
            }
            DecodeListener<AudioDecodeInfo> listener = mListener.get();
            if (listener != null) {
                listener.prepared();
            }
        }

        @Override
        protected void handlePlay() {
            IAudioDecoder decoder = accessDecoder();
            if (decoder != null) {
                AudioDecodeInfo decodeInfo = decoder.performDecodeOperation();
                DecodeListener<AudioDecodeInfo> listener = mListener.get();
                if (listener != null) {
                    listener.accessData(decodeInfo);
                }
            }
        }

        @Override
        protected void handlePause() {
            removeMessages(play);
            DecodeListener<AudioDecodeInfo> listener = mListener.get();
            if (listener != null) {
                listener.paused();
            }
        }

        @Override
        protected void handleEnd() {

        }

        private IAudioDecoder accessDecoder() {
            PluginVideoView videoView = mRendererView.get();
            if (videoView != null) {
                IControl control = videoView.getPlayerController();
                if (control != null) {
                    return control.getAudioDecoder();
                }
            }
            return null;
        }
    }

    static class VideoDecodeTask extends DecodeTask<VideoDecodeHandler> {
        private WeakReference<PluginVideoView> mRendererViewWRF;
        private WeakReference<DecodeListener<VideoDecodeInfo>> mListenerWRF;

        VideoDecodeTask(PluginVideoView rendererView, DecodeListener<VideoDecodeInfo> listener) {
            this.mRendererViewWRF = new WeakReference<>(rendererView);
            this.mListenerWRF = new WeakReference<>(listener);
        }

        @Override
        protected VideoDecodeHandler initHandler() {
            return new VideoDecodeHandler(Looper.myLooper(), mRendererViewWRF, mListenerWRF);
        }
    }

    static class VideoDecodeHandler extends DecodeHandler {
        private WeakReference<PluginVideoView> mRendererView;
        private WeakReference<DecodeListener<VideoDecodeInfo>> mListener;

        VideoDecodeHandler(Looper looper, WeakReference<PluginVideoView> rendererView, WeakReference<DecodeListener<VideoDecodeInfo>> listener) {
            super(looper);
            this.mRendererView = rendererView;
            this.mListener = listener;
        }

        @Override
        protected void handlePrepare(Message msg) {
            IVideoDecoder decoder = accessDecoder();
            if (decoder != null) {
                decoder.prepare(msg.obj);
            }
            DecodeListener<VideoDecodeInfo> listener = mListener.get();
            if (listener != null) {
                listener.prepared();
            }
        }

        @Override
        protected void handlePlay() {
            IVideoDecoder decoder = accessDecoder();
            if (decoder != null) {
                VideoDecodeInfo decodeInfo = decoder.performDecodeOperation();
                DecodeListener<VideoDecodeInfo> listener = mListener.get();
                if (listener != null) {
                    listener.accessData(decodeInfo);
                }
            }
        }

        @Override
        protected void handlePause() {
            removeMessages(play);
            DecodeListener<VideoDecodeInfo> listener = mListener.get();
            if (listener != null) {
                listener.paused();
            }
        }

        @Override
        protected void handleEnd() {

        }

        private IVideoDecoder accessDecoder() {
            PluginVideoView videoView = mRendererView.get();
            if (videoView != null) {
                IControl control = videoView.getPlayerController();
                if (control != null) {
                    return control.getVideoDecoder();
                }
            }
            return null;
        }
    }
}
