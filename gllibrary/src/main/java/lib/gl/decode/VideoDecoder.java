package lib.gl.decode;

import android.media.*;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import util.FileUtil;
import util.ThreadUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

/**
 * @author Gxx
 * Created by Gxx on 2019/2/11.
 */
public class VideoDecoder implements AbsDecoder {
    private final String TAG = getClass().getName();
    private final Object mFirstFrameLock = new Object();
    private final Object mReadyLock = new Object();

    private MediaExtractor mMediaExtractor;
    private MediaCodec mMediaDecoder;

    private volatile int mVideoWidth;
    private volatile int mVideoHeight;
    private volatile int mVideoRotation;
    private volatile int mVideoDuration;

    private String mPath;
    private WeakReference<Surface> mSurface;
    private VideoDecoder mDecoder;

    private volatile boolean mReady;
    private volatile boolean mRepeat;
    private volatile boolean mNextFrame;
    private volatile boolean mPause;
    private volatile boolean mFirstFrameHandle;
    private volatile boolean mRelease;

    private volatile long mPauseDuration;
    private volatile long mPauseStartTime;

    private Handler mAsyncHandler;
    private VideoDecoderPrepareListener mPrepareListener;
    private VideoDecoderFristFrameListener mFirstFrameListener;
    private VideoDecoderEndListener mEndListener;
    private VideoDecoderDestroyListener mDestroyListener;
    private Runnable mPrepareRunnable;
    private Runnable mFirstFrameRunnable;
    private Runnable mEndRunnable;
    private Runnable mDestroyRunnable;

    public VideoDecoder(String path, Surface surface) {
        super();
        mPath = path;
        mSurface = new WeakReference<>(surface);
        mNextFrame = true;
        mDecoder = this;
    }

    private void waitForReady() {
        synchronized (mReadyLock) {
            if (!mReady) {
                try {
                    mReadyLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void readyForRender() {
        synchronized (mReadyLock) {
            mReady = true;
            mReadyLock.notify();
        }
    }

    /**
     * 判断下一帧是否需要被渲染
     */
    public void nextFrame(boolean render) {
        mNextFrame = render;
    }

    public void setAsyncHandler(Handler handler){
        mAsyncHandler = handler;
    }

    public void setPrepareListener(VideoDecoderPrepareListener listener){
        mPrepareListener = listener;

        if (listener != null) {
            mPrepareRunnable = new Runnable() {
                @Override
                public void run() {
                    if (mPrepareListener != null) {
                        mPrepareListener.onPrepare(mDecoder);
                    }
                }
            };
        }
    }

    public void setFirstFrameListener(VideoDecoderFristFrameListener listener){
        mFirstFrameListener = listener;

        if (listener != null) {
            mFirstFrameRunnable = new Runnable() {
                @Override
                public void run() {
                    if (mFirstFrameListener != null) {
                        mFirstFrameListener.onFirstFrameAvailable(mDecoder);
                        synchronized (mFirstFrameLock) {
                            mFirstFrameHandle = true;
                            mFirstFrameLock.notify();
                        }
                    }
                }
            };
        }
    }

    public void setDecodeEndListener(VideoDecoderEndListener listener){
        mEndListener = listener;

        if (listener != null) {
            mEndRunnable = new Runnable() {
                @Override
                public void run() {
                    if (mEndListener != null) {
                        mEndListener.onDecodeEnd(mDecoder);
                    }
                }
            };
        }
    }

    public void setDestroyListener(VideoDecoderDestroyListener listener){
        mDestroyListener = listener;

        if (listener != null) {
            mDestroyRunnable = new Runnable() {
                @Override
                public void run() {
                    if (mDestroyListener != null) {
                        mDestroyListener.onDestroy();
                    }
                }
            };
        }
    }

    @Override
    public void run() {
        if (init(mPath, mSurface.get())) {
            waitForReady();
            doExtract();
        }
    }

    public boolean init(String path, Surface surface) {
        if (mMediaDecoder != null && mMediaExtractor != null && !mRepeat) return false;
        if (mReady) return true;
        if (FileUtil.isFileExists(path) && surface != null) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(path);
            String s = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);

            if (TextUtils.isEmpty(s)) {
                Log.e(TAG, "init: path is not video path!");
                retriever.release();
                return false;
            }

            // extract basic information
            synchronized (this) {
                String sWidth = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                mVideoWidth = Integer.parseInt(sWidth);

                String sHeight = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                mVideoHeight = Integer.parseInt(sHeight);

                String sRotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
                mVideoRotation = Integer.parseInt(sRotation);

                String sDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                mVideoDuration = Integer.parseInt(sDuration);
            }

            retriever.release();

            // start init extractor
            mMediaExtractor = new MediaExtractor();
            boolean extractorSetDataSucceed = false;

            try {
                mMediaExtractor.setDataSource(path);
                extractorSetDataSucceed = true;
            } catch (IOException e) {
                e.printStackTrace();
                mMediaExtractor.release();
                mMediaExtractor = null;
            }

            if (!extractorSetDataSucceed) {
                Log.e(TAG, "init: extractor set data failure!");
                return false;
            }

            // search video track
            int videoTrackIndex = -1;
            MediaFormat videoFormat = null;
            int trackCount = mMediaExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mMediaExtractor.getTrackFormat(i);
                String mime = trackFormat.getString(MediaFormat.KEY_MIME);

                if (!TextUtils.isEmpty(mime) && mime.startsWith("video/")) {
                    videoFormat = trackFormat;
                    videoTrackIndex = i;
                }

                mMediaExtractor.unselectTrack(i);
            }

            if (videoTrackIndex < 0) {
                Log.e(TAG, "init: do not find video track!");
                return false;
            }

            mMediaExtractor.selectTrack(videoTrackIndex);

            MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.REGULAR_CODECS);
            String decoderName = mediaCodecList.findDecoderForFormat(videoFormat);
            if (decoderName != null) {
                // start init decoder
                try {
                    mMediaDecoder = MediaCodec.createByCodecName(decoderName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (mMediaDecoder != null) {
                    mMediaDecoder.configure(videoFormat, surface, null, 0);
                    mMediaDecoder.start();

                    if (mAsyncHandler != null && mPrepareRunnable != null) {
                        ThreadUtil.runOnHandler(mAsyncHandler, mPrepareRunnable, 0);
                    } else if (mPrepareRunnable != null) {
                        mPrepareRunnable.run();
                    }
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 根据视频帧率, 持续解帧, 直至 EOS
     */
    private void doExtract() {
        if (mMediaExtractor == null || mMediaDecoder == null) {
            return;
        }

        MediaCodec decoder = mMediaDecoder;
        MediaExtractor extractor = mMediaExtractor;
        MediaCodec.BufferInfo outputBufferInfo = new MediaCodec.BufferInfo();
        long firstFrameTime = 0;

        final int TIME_OUT_ESC = 10000;
        boolean inputDone = false;
        boolean outputDone = false;
        boolean startExtract = true;

        long currentFrameTime = 0;
        boolean playBack = false;

        while (!outputDone) {
            synchronized (this) {
                if (mPause) {
                    continue;
                }
                if (mRelease) {
                    break;
                }
            }
            if (!inputDone) {
                if (startExtract) {
                    decoder.flush();
                    extractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
                    mPauseDuration = 0; // 暂停时间需要清零
                    startExtract = false;
                } else if (!playBack && currentFrameTime > 7 * 1000 * 1000) {
                    decoder.flush();
                    extractor.seekTo(5 * 1000 * 1000, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
                    playBack = true;
                    mPauseDuration = 0;
                    firstFrameTime = 0;
                    Log.d(TAG, "doExtract: currentFrameTime == " + currentFrameTime);
                }
                int inputBufferIndex = decoder.dequeueInputBuffer(TIME_OUT_ESC);

                if (inputBufferIndex >= 0) {
                    ByteBuffer inputBuffer = decoder.getInputBuffer(inputBufferIndex);

                    if (inputBuffer != null) {
                        int sampleDataSize = extractor.readSampleData(inputBuffer, 0);

                        if (sampleDataSize > 0) {
                            long presentationTimeUs = extractor.getSampleTime();
                            decoder.queueInputBuffer(inputBufferIndex, 0, sampleDataSize, presentationTimeUs, 0);
                            extractor.advance();
                        } else {
                            decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            inputDone = true;
                        }
                    }
                }
            }

            if (!outputDone) {
                int outputBufferIndex = decoder.dequeueOutputBuffer(outputBufferInfo, TIME_OUT_ESC);

                if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    Log.d(TAG, "deExtract: no output from decoder available");
                } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    MediaFormat outputFormat = decoder.getOutputFormat();

                    Log.d(TAG, "deExtract: output format changed, new format is == " + outputFormat.toString());
                } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    // not important for us, since we're using Surface
                    Log.d(TAG, "deExtract: decoder output buffers changed");
                } else if (outputBufferIndex < 0) {
                    Log.d(TAG, "deExtract: decoder output buffers < 0");
                } else {
                    if (firstFrameTime == 0) {
                        firstFrameTime = System.nanoTime();
                    }

                    if (outputBufferInfo.presentationTimeUs == 0) {
                        if (!mFirstFrameHandle)
                        {
                            if (mAsyncHandler != null && mFirstFrameRunnable != null) {
                                ThreadUtil.runOnHandler(mAsyncHandler, mFirstFrameRunnable, 0);
                                waitForHandleFirstFrame();
                            } else if (mFirstFrameRunnable != null) {
                                mFirstFrameRunnable.run();
                            } else {
                                mFirstFrameHandle = true;
                            }
                        }
                    }

                    boolean render = outputBufferInfo.size != 0 && mNextFrame;

                    synchronized (this) {
                        if (mPause && !render) {
                            continue;
                        }
                        long dNs = 0;
                        if (playBack) {
                            dNs = outputBufferInfo.presentationTimeUs * 1000L / 2 - (System.nanoTime() - mPauseDuration - firstFrameTime + 5L * 1000 * 1000 * 1000);
                        } else {
                            dNs = outputBufferInfo.presentationTimeUs * 1000L - (System.nanoTime() - mPauseDuration - firstFrameTime);
                        }
                        long millis = dNs / 1000000L;
                        int nanos = (int) (dNs & 1000L);

                        if (millis > 0 && nanos > 0) {
                            try {
                                Thread.sleep(millis, nanos);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        if (mRelease) {
                            break;
                        }

                        currentFrameTime = outputBufferInfo.presentationTimeUs;
                    }

                    decoder.releaseOutputBuffer(outputBufferIndex, render);

                    if ((outputBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        outputDone = true;

                        if (!mRepeat) {
                            if (mAsyncHandler != null && mEndRunnable != null) {
                                ThreadUtil.runOnHandler(mAsyncHandler, mEndRunnable, 0);
                            } else if (mEndRunnable != null) {
                                mEndRunnable.run();
                            }
                        }
                    }
                }
            }
        }
    }

    private void waitForHandleFirstFrame() {
        synchronized (mFirstFrameLock) {
            if (!mFirstFrameHandle) {
                try {
                    mFirstFrameLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void destroy() {
        if (mMediaExtractor != null) {
            mMediaExtractor.release();
        }

        if (mMediaDecoder != null) {
            mMediaDecoder.release();
        }

        ThreadUtil.clearOnHandler(mAsyncHandler);

        if (mAsyncHandler != null && mDestroyRunnable != null) {
            ThreadUtil.runOnHandler(mAsyncHandler, mDestroyRunnable, 0);
        } else if (mDestroyRunnable != null) {
            mDestroyRunnable.run();
        }

        mAsyncHandler = null;
        mPrepareListener = null;
        mPrepareRunnable = null;
        mFirstFrameListener = null;
        mFirstFrameRunnable = null;
        mEndListener = null;
        mEndRunnable = null;

        if (mSurface != null) {
            mSurface.clear();
        }
    }

    @Override
    public void release() {
        synchronized (this) {
            mRelease = true;
        }
    }

    @Override
    public void setPause(boolean pause) {
        synchronized (this) {
            if (pause) {
                mPauseStartTime = System.nanoTime();
            }
            if (mPause && !pause) {
                mPauseDuration += System.nanoTime() - mPauseStartTime;
            }
            mPause = pause;
        }
    }

    @Override
    public void setRepeat(boolean repeat) {
        mRepeat = repeat;
    }

    @Override
    public int getVideoWidth() {
        synchronized (this) {
            return mVideoWidth;
        }
    }

    @Override
    public int getVideoHeight() {
        synchronized (this) {
            return mVideoHeight;
        }
    }

    @Override
    public int getVideoRotation() {
        synchronized (this) {
            return mVideoRotation;
        }
    }

    @Override
    public int getVideoDuration() {
        synchronized (this) {
            return mVideoDuration;
        }
    }
}
