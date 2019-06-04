package trunk.android;

import android.content.Context;
import android.media.*;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.xx.avlibrary.gl.util.GLUtil;
import com.xx.avlibrary.gl.egl.EglCore;
import com.xx.avlibrary.gl.egl.EglSurfaceBase;
import com.xx.avlibrary.gl.filter.common.BmpToTextureFilter;
import com.xx.avlibrary.gl.filter.common.DisplayImageFilter;
import trunk.R;
import util.PxUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class RhythmTestActivity extends AppCompatActivity {

    private FrameLayout mOutsideLayout;
    private GLSurfaceView mGLView;

    private String mPath = Environment.getExternalStorageDirectory() + File.separator + "test_music.aac";
    private RhythmRenderer mRenderer;

    private AudioDecodeRunnable mAudioDecodeRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getBaseContext();

        mRenderer = new RhythmRenderer(context);

        mOutsideLayout = new FrameLayout(context);
        setContentView(mOutsideLayout);
        {
            Button btn = new Button(context);
            btn.setText("开始");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRenderer.setStart(true);
                    mGLView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                    mGLView.requestRender();
                    playMusicWithAudioTrack(mPath);
                }
            });
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mOutsideLayout.addView(btn, params);

            btn = new Button(context);
            btn.setText("暂停");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRenderer.setStart(false);
                    mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                    releaseMedia();
                }
            });
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.END;
            mOutsideLayout.addView(btn, params);

            mGLView = new GLSurfaceView(context);
            mGLView.setEGLContextClientVersion(GLUtil.getGlSupportVersionInt(context));
            mGLView.setRenderer(mRenderer);
            mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxUtil.sU_1080p(1080));
            params.topMargin = PxUtil.sV_1080p(200);
            mOutsideLayout.addView(mGLView, params);
        }
    }

    private static class RhythmRenderer implements GLSurfaceView.Renderer {
        private BmpToTextureFilter bmpToTextureFilter;
        private DisplayImageFilter displayImageFilter;

        private Context mContext;
        private volatile boolean mStart;

        int[] bmpRes = new int[]{R.drawable.open_test, R.drawable.open_test_2, R.drawable.open_test_3,
                R.drawable.open_test_4, R.drawable.open_test_5, R.drawable.open_test_6,
        };

        volatile long firstDrawTime;

        int index = -1;

        public RhythmRenderer(Context context) {
            mContext = context;
        }

        public void setStart(boolean start) {
            synchronized (this) {
                mStart = start;
                if (!mStart) {
                    firstDrawTime = 0;
                }
            }
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            bmpToTextureFilter = new BmpToTextureFilter(mContext);
            bmpToTextureFilter.onSurfaceCreated(config);

            displayImageFilter = new DisplayImageFilter(mContext);
            displayImageFilter.onSurfaceCreated(null);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            bmpToTextureFilter.onSurfaceChanged(width, height);
            bmpToTextureFilter.initFrameBuffer(width, height);

            displayImageFilter.onSurfaceChanged(width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            if (mStart) {
                try {
                    if (!Proxy.getInstance().isAudioDecoderReady()) {
                        Log.d("xxx", "onDrawFrame: lock start time = " + System.currentTimeMillis());
                        synchronized (Proxy.audio_ready_lock) {
                            Proxy.audio_ready_lock.wait();
                        }
                        Log.d("xxx", "onDrawFrame: lock end time = " + System.currentTimeMillis());
                    }
                } catch (Exception e) {

                } finally {

                }

                if (firstDrawTime == 0) {
                    firstDrawTime = System.currentTimeMillis();
                }

                int i = checkIndex();
                if (i != index) {
                    index = i;
                    Log.d("xxx", "onDrawFrame: 图片解码 start time = " + System.currentTimeMillis());
                    bmpToTextureFilter.setBitmapRes(bmpRes[i]);
                    Log.d("xxx", "onDrawFrame: 图片解码 end time = " + System.currentTimeMillis());
                }
                int textID = bmpToTextureFilter.onDrawBuffer(0);

                displayImageFilter.setTextureWH(bmpToTextureFilter.getTextureW(), bmpToTextureFilter.getTextureH());
                displayImageFilter.onDrawFrame(textID);
            }
        }

        private int checkIndex() {
            int index = (int) ((System.currentTimeMillis() - firstDrawTime) / 1000 % bmpRes.length);
            return index;
        }
    }

    private static class RhythmCompoundRunnable implements Runnable {

        Context mContext;
        MediaMuxer mMediaMuxer;
        VideoEncodeRunnable mVideoEncodeRunnable;
        AudioEncodeRunnable mAudioEncodeRunnable;

        public RhythmCompoundRunnable(Context context, String outputPath) {
            mContext = context;
            try {
                mMediaMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            mVideoEncodeRunnable = new VideoEncodeRunnable(mContext, new RhythmRenderer(mContext), mMediaMuxer, 0, 0);
            new Thread(mVideoEncodeRunnable).start();

            mAudioEncodeRunnable = new AudioEncodeRunnable(mMediaMuxer);
//            new Thread(mAudioEncodeRunnable).start();
            mAudioEncodeRunnable.run();

            // lock
//            mMediaMuxer.stop();
//            mMediaMuxer.release();
        }
    }

    public static class VideoEncodeRunnable implements Runnable {

        Context mContext;
        Surface mSurface;
        GLSurfaceView.Renderer mRenderer;
        MediaMuxer mMediaMuxer;

        int mWidth;
        int mHeight;

        EglCore mEglCore;
        EglSurfaceBase mEglSurfaceBase;

        int redSize;
        int greenSize;
        int blueSize;
        int alphaSize;
        int depthSize;
        int stencilSize;
        boolean recordable;

        public VideoEncodeRunnable(Context context, GLSurfaceView.Renderer renderer, MediaMuxer mediaMuxer,
                                      int width, int height) {
            mContext = context;
            mRenderer = renderer;
            mMediaMuxer = mediaMuxer;
            this.mWidth = width;
            this.mHeight = height;
            setEglConfig(8, 8, 8, 8, 0, 0, true);
        }

        public void setEglConfig(int redSize, int greenSize, int blueSize, int alphaSize, int depthSize, int stencilSize, boolean recordable) {
            this.redSize = redSize;
            this.greenSize = greenSize;
            this.blueSize = blueSize;
            this.alphaSize = alphaSize;
            this.depthSize = depthSize;
            this.stencilSize = stencilSize;
            this.recordable = recordable;
        }

        public void setEglConfig(int depthSize, int stencilSize, boolean recordable) {
            this.setEglConfig(redSize, greenSize, blueSize, alphaSize, depthSize, stencilSize, recordable);
        }

        @Override
        public void run() {
            if (mRenderer == null) {
                throw new RuntimeException("renderer is null");
            }

            try {
                // 初始化 EGL 环境
                mEglCore = new EglCore();
                mEglCore.setConfig(mContext, redSize, greenSize, blueSize, alphaSize, depthSize, stencilSize, recordable);
                mEglCore.initEglContext(null);

                mEglSurfaceBase = new EglSurfaceBase(mEglCore);
                mEglSurfaceBase.createWindowSurface(mSurface);
                mEglSurfaceBase.makeCurrent();

                mRenderer.onSurfaceCreated(null, null);
                mRenderer.onSurfaceChanged(null, mWidth, mHeight);
                mRenderer.onDrawFrame(null);
                mEglSurfaceBase.swapBuffers();
            } catch (Throwable e) {
              e.printStackTrace();
            } finally {

            }
        }
    }

    private void playMusicWithAudioTrack(String path) {
        releaseMedia();

        if (mAudioDecodeRunnable == null) {
            mAudioDecodeRunnable = new AudioDecodeRunnable();
        }

        mAudioDecodeRunnable.setDataSource(path);

        new Thread(mAudioDecodeRunnable).start();
    }

    private void releaseMedia() {
        if (mAudioDecodeRunnable != null) {
            mAudioDecodeRunnable.releaseMedia();
        }
    }

    private static class AudioDecodeRunnable implements Runnable {

        private AudioTrack mAudioTrack;
        private String mPath;
        private boolean mBreakThread;

        public void setDataSource(String path) {
            releaseMedia();
            mPath = path;
        }

        private void releaseMedia() {
            if (mAudioTrack != null) {
                int playState = mAudioTrack.getPlayState();
                if (playState == AudioTrack.PLAYSTATE_PLAYING) {
                    mBreakThread = true;
                }
            }
        }

        @Override
        public void run() {
            MediaExtractor mediaExtractor = new MediaExtractor();
            try {
                mediaExtractor.setDataSource(mPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int trackIndex = -1;
            MediaFormat selTrackFormat = null;
            int trackCount = mediaExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                if (trackFormat.getString(MediaFormat.KEY_MIME).startsWith("audio/")){
                    trackIndex = i;
                    selTrackFormat = trackFormat;
                }
                mediaExtractor.unselectTrack(i);
            }

            if (trackIndex >= 0) {
                mediaExtractor.selectTrack(trackIndex);
                String track_mime = selTrackFormat.getString(MediaFormat.KEY_MIME);
                int sample_rate = selTrackFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                int channel_count = selTrackFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);

                int minBufferSize = AudioTrack.getMinBufferSize(sample_rate, channel_count == 2 ? AudioFormat.CHANNEL_OUT_STEREO : AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

                mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sample_rate, channel_count == 2 ? AudioFormat.CHANNEL_OUT_STEREO : AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);
                mAudioTrack.flush();

                MediaCodec decoder = null;
                try {
                    decoder = MediaCodec.createDecoderByType(track_mime);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (decoder != null) {
                    decoder.configure(selTrackFormat, null, null, 0);
                    decoder.start();

                    long TIME_OUT_ESC = 10000;
                    boolean outputDone = false;
                    boolean inputDone = false;
                    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

                    while (!outputDone) {
                        if (mBreakThread) {
                            break;
                        }
                        if (!inputDone) {
                            int inputBufferIndex = decoder.dequeueInputBuffer(TIME_OUT_ESC);

                            if (inputBufferIndex >= 0) {
                                ByteBuffer inputBuffer = decoder.getInputBuffer(inputBufferIndex);

                                if (inputBuffer != null) {
                                    int size = mediaExtractor.readSampleData(inputBuffer, 0);
                                    if (size > 0) {
                                        long presentationTimeUs = mediaExtractor.getSampleTime();
                                        decoder.queueInputBuffer(inputBufferIndex, 0, size, presentationTimeUs, 0);
                                        mediaExtractor.advance();
                                    }
                                    else {
                                        decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                                        inputDone = true;
                                    }
                                }
                            }
                        }

                        if (!outputDone) {
                            int outputBufferIndex = decoder.dequeueOutputBuffer(bufferInfo, TIME_OUT_ESC);
                            if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {

                            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {

                            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {

                            } else if (outputBufferIndex < 0) {

                            } else {
                                ByteBuffer outputBuffer = decoder.getOutputBuffer(outputBufferIndex);
                                if (outputBuffer != null) {
                                    if (bufferInfo.size > 0) {
                                        mAudioTrack.write(outputBuffer, bufferInfo.size, AudioTrack.WRITE_BLOCKING);
                                        if (bufferInfo.presentationTimeUs == 0) {
                                            Proxy.getInstance().setAudioDecoderReady();
                                        }
                                        mAudioTrack.play();
                                        Log.d("xxx", "播放时间 == " + bufferInfo.presentationTimeUs);
                                    }

                                    decoder.releaseOutputBuffer(outputBufferIndex, false);
                                }

                                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                                    outputDone = true;
                                }
                            }
                        }
                    }

                    mediaExtractor.release();

                    decoder.stop();
                    decoder.release();

                    mAudioTrack.stop();
                    mAudioTrack.release();

                    mBreakThread = false;
                }
            }
        }
    }

    private static class AudioEncodeRunnable implements Runnable {

        private String mPath;
        private boolean mBreakThread;

        private MediaMuxer mMediaMuxer;
        private int mMuxerAudioIndex = -1;

        public AudioEncodeRunnable(MediaMuxer mediaMuxer) {
            this.mMediaMuxer = mediaMuxer;
        }

        public void setDataSource(String path) {
            mPath = path;
        }

        @Override
        public void run() {
            MediaExtractor mediaExtractor = new MediaExtractor();
            try {
                mediaExtractor.setDataSource(mPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int trackIndex = -1;
            MediaFormat selTrackFormat = null;
            int trackCount = mediaExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                if (trackFormat.getString(MediaFormat.KEY_MIME).startsWith("audio/")){
                    trackIndex = i;
                    selTrackFormat = trackFormat;
                }
                mediaExtractor.unselectTrack(i);
            }

            if (trackIndex >= 0) {
                mediaExtractor.selectTrack(trackIndex);
                String track_mime = selTrackFormat.getString(MediaFormat.KEY_MIME);

                MediaCodec decoder = null;
                try {
                    decoder = MediaCodec.createDecoderByType(track_mime);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (decoder != null) {
                    decoder.configure(selTrackFormat, null, null, 0);
                    decoder.start();

                    long TIME_OUT_ESC = 10000;
                    boolean outputDone = false;
                    boolean inputDone = false;
                    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

                    while (!outputDone) {
                        if (mBreakThread) {
                            break;
                        }
                        if (!inputDone) {
                            int inputBufferIndex = decoder.dequeueInputBuffer(TIME_OUT_ESC);

                            if (inputBufferIndex >= 0) {
                                ByteBuffer inputBuffer = decoder.getInputBuffer(inputBufferIndex);

                                if (inputBuffer != null) {
                                    int size = mediaExtractor.readSampleData(inputBuffer, 0);
                                    if (size > 0) {
                                        long presentationTimeUs = mediaExtractor.getSampleTime();
                                        decoder.queueInputBuffer(inputBufferIndex, 0, size, presentationTimeUs, 0);
                                        mediaExtractor.advance();
                                    }
                                    else {
                                        decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                                        inputDone = true;
                                    }
                                }
                            }
                        }

                        if (!outputDone) {
                            int outputBufferIndex = decoder.dequeueOutputBuffer(bufferInfo, TIME_OUT_ESC);
                            if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {

                            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                                if (mMediaMuxer != null && !Proxy.getInstance().isAudioMuxerReady()) {
                                    MediaFormat outputFormat = decoder.getOutputFormat();
                                    mMuxerAudioIndex = mMediaMuxer.addTrack(outputFormat);
                                    Proxy.getInstance().setAudioMuxerReady();
                                    synchronized (Proxy.media_muxer_lock) {
                                        if (Proxy.getInstance().isVideoMuxerReady() && !Proxy.getInstance().isMediaMuxerStarted()) {
                                            Proxy.getInstance().setMediaMuxerStart();
                                            mMediaMuxer.start();
                                        }
                                    }
                                }
                            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {

                            } else if (outputBufferIndex < 0) {

                            } else {
                                ByteBuffer outputBuffer = decoder.getOutputBuffer(outputBufferIndex);
                                if (outputBuffer != null) {
                                    if (bufferInfo.size > 0) {
                                        if (mMediaMuxer != null && Proxy.getInstance().isMediaMuxerStarted()) {
                                            mMediaMuxer.writeSampleData(mMuxerAudioIndex, outputBuffer, bufferInfo);
                                        }
                                    }
                                    decoder.releaseOutputBuffer(outputBufferIndex, false);
                                }

                                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                                    outputDone = true;
                                }
                            }
                        }
                    }

                    mediaExtractor.release();

                    decoder.stop();
                    decoder.release();

                    mBreakThread = false;
                }
            }
        }
    }

    private static class Proxy {

        private static Proxy sInstance = new Proxy();

        private Proxy() {}

        public static Proxy getInstance() {
            return sInstance;
        }

        public static final Object audio_ready_lock = new Object();

        // 音频解码首包是否成功
        private volatile boolean mAudioDecoderReady;

        public boolean isAudioDecoderReady() {
            return mAudioDecoderReady;
        }

        public void setAudioDecoderReady() {
            synchronized (audio_ready_lock) {
                mAudioDecoderReady = true;
                audio_ready_lock.notify();
            }
        }

        // 音频解码时间
        private volatile long mAudioPresentationTimeUs;

        public long getAudioPresentationTimeUs() {
            return mAudioPresentationTimeUs;
        }

        public void setAudioPresentationTimeUs(long timeUs) {
            mAudioPresentationTimeUs = timeUs;
        }


        private volatile boolean mVideoMuxerReady;

        public boolean isVideoMuxerReady() {
            return mVideoMuxerReady;
        }

        public void setVideoMuxerReady() {
            mVideoMuxerReady = true;
        }

        private volatile boolean mAudioMuxerReady;

        public boolean isAudioMuxerReady() {
            return mAudioMuxerReady;
        }

        public void setAudioMuxerReady() {
            mAudioMuxerReady = true;
        }

        public static final Object media_muxer_lock = new Object();

        private volatile boolean mMediaMuxerStart;

        public boolean isMediaMuxerStarted() {
            return mMediaMuxerStart;
        }

        public void setMediaMuxerStart() {
            mMediaMuxerStart = true;
        }
    }
}
