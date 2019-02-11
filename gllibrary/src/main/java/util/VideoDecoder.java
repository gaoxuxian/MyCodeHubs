package util;

import android.media.MediaCodec;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

/**
 * @author Gxx
 * Created by Gxx on 2019/2/11.
 */
public class VideoDecoder extends Thread
{
    private final String TAG = getClass().getName();
    private final Object mLock = new Object();

    private MediaExtractor mMediaExtractor;
    private MediaCodec mMediaDecoder;

    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoRotation;
    private int mVideoDuration;
    private int mVideoFrameCount;

    private String mPath;
    private WeakReference<Surface> mSurface;
    private volatile boolean mAutoDecode;

    private boolean mReady;
    private volatile boolean mNextFrame;

    private VideoDecodeListener mListener;
    private Handler mListenerHandler;
    private Runnable mPrepareRunnable;
    private Runnable mEndRunnable;
    private volatile boolean mRelease;

    public VideoDecoder(String path, Surface surface)
    {
        super();
        mPath = path;
        mSurface = new WeakReference<>(surface);
        mAutoDecode = true;
    }

    public void setAutoDecode(boolean autoDecode)
    {
        mAutoDecode = autoDecode;
    }

    public void setDecodeListener(Handler handler, VideoDecodeListener listener)
    {
        mListener = listener;
        mListenerHandler = handler;

        mPrepareRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                if (mListener != null)
                {
                    mListener.onPrepareSucceed();
                }
            }
        };

        mEndRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                if (mListener != null)
                {
                    mListener.onEnd();
                }
            }
        };
    }

    @Override
    public void run()
    {
        if (init(mPath, mSurface.get()))
        {
            doExtract();
        }

        if (mRelease)
        {
            if (mMediaExtractor != null)
            {
                mMediaExtractor.release();
            }

            if (mMediaDecoder != null)
            {
                mMediaDecoder.release();
            }

            ThreadUtil.clearOnHandler(mListenerHandler);

            mListenerHandler = null;
            mListener = null;
        }
    }

    public boolean init(String path, Surface surface)
    {
        if (FileUtil.isFileExists(path) && surface != null)
        {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(path);
            String s = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);

            if (TextUtils.isEmpty(s))
            {
                Log.e(TAG, "init: path is not video path!");
                retriever.release();
                return false;
            }

            // extract basic information
            String sWidth = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String sHeight = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String sRotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            String sDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String sFrameCount = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT);
            retriever.release();

            mVideoWidth = Integer.parseInt(sWidth);
            mVideoHeight = Integer.parseInt(sHeight);
            mVideoRotation = Integer.parseInt(sRotation);
            mVideoDuration = Integer.parseInt(sDuration);
            mVideoFrameCount = Integer.parseInt(sFrameCount);

            // start init extractor
            mMediaExtractor = new MediaExtractor();
            boolean extractorSetDataSucceed = false;

            try
            {
                mMediaExtractor.setDataSource(path);
                extractorSetDataSucceed = true;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                mMediaExtractor.release();
                mMediaExtractor = null;
            }

            if (!extractorSetDataSucceed)
            {
                Log.e(TAG, "init: extractor set data failure!");
                return false;
            }

            // search video track
            int videoTrackIndex = -1;
            MediaFormat videoFormat = null;
            int trackCount = mMediaExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++)
            {
                MediaFormat trackFormat = mMediaExtractor.getTrackFormat(i);
                String mime = trackFormat.getString(MediaFormat.KEY_MIME);

                if (!TextUtils.isEmpty(mime) && mime.startsWith("video/"))
                {
                    videoFormat = trackFormat;
                    videoTrackIndex = i;
                }

                mMediaExtractor.unselectTrack(i);
            }

            if (videoTrackIndex < 0)
            {
                Log.e(TAG, "init: do not find video track!");
                return false;
            }

            mMediaExtractor.selectTrack(videoTrackIndex);

            MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.REGULAR_CODECS);
            String decoderName = mediaCodecList.findDecoderForFormat(videoFormat);
            if (decoderName != null)
            {
                // start init decoder
                try
                {
                    mMediaDecoder = MediaCodec.createByCodecName(decoderName);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                if (mMediaDecoder != null)
                {
                    mMediaDecoder.configure(videoFormat, surface, null, 0);
                    mMediaDecoder.start();
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 逐帧提取
     */
    public void nextFrame()
    {
        synchronized (mLock)
        {
            mNextFrame = true;
            mLock.notifyAll();
        }
    }

    /**
     * 根据视频帧率, 持续解帧, 直至 EOS
     */
    private void doExtract()
    {
        if (mMediaExtractor == null || mMediaDecoder == null)
        {
            return;
        }

        MediaCodec decoder = mMediaDecoder;
        MediaExtractor extractor = mMediaExtractor;
        MediaCodec.BufferInfo outputBufferInfo = new MediaCodec.BufferInfo();
        long firstFrameTime = 0;

        final int TIME_OUT_ESC = 10000;
        boolean inputDone = false;
        boolean outputDone = false;

        while (!mRelease)
        {
            if (!inputDone)
            {
                int inputBufferIndex = decoder.dequeueInputBuffer(TIME_OUT_ESC);

                if (inputBufferIndex >= 0)
                {
                    ByteBuffer inputBuffer = decoder.getInputBuffer(inputBufferIndex);

                    if (inputBuffer != null)
                    {
                        int sampleDataSize = extractor.readSampleData(inputBuffer, 0);

                        if (sampleDataSize > 0)
                        {
                            long presentationTimeUs = extractor.getSampleTime();
                            decoder.queueInputBuffer(inputBufferIndex, 0, sampleDataSize, presentationTimeUs, 0);
                            extractor.advance();
                        }
                        else
                        {
                            decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            inputDone = true;
                        }
                    }
                }
            }

            if (!outputDone)
            {
                int outputBufferIndex = decoder.dequeueOutputBuffer(outputBufferInfo, TIME_OUT_ESC);

                if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER)
                {
                    Log.d(TAG, "deExtract: no output from decoder available");
                }
                else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED)
                {
                    MediaFormat outputFormat = decoder.getOutputFormat();

                    Log.d(TAG, "deExtract: output format changed, new format is == " + outputFormat.toString());
                }
                else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED)
                {
                    // not important for us, since we're using Surface
                    Log.d(TAG, "deExtract: decoder output buffers changed");
                }
                else if (outputBufferIndex < 0)
                {
                    Log.d(TAG, "deExtract: decoder output buffers < 0");
                }
                else
                {
                    if (mAutoDecode)
                    {
                        if (!mReady)
                        {
                            mReady = true;
                            ThreadUtil.runOnHandler(mListenerHandler, mPrepareRunnable, 0);

                            synchronized (mLock)
                            {
                                if (!mNextFrame)
                                {
                                    try
                                    {
                                        mLock.wait();
                                    }
                                    catch (InterruptedException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }

                        if (firstFrameTime == 0)
                        {
                            firstFrameTime = System.nanoTime();
                        }

                        long dNs = outputBufferInfo.presentationTimeUs * 1000L - (System.nanoTime() - firstFrameTime);

                        long millis = dNs / 1000000L;
                        int nanos = (int) (dNs & 1000L);

                        if (millis >= 0 && nanos >= 0)
                        {
                            try
                            {
                                Thread.sleep(millis, nanos);
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    else
                    {
                        if (mListenerHandler != null)
                        {
                            if (!mReady)
                            {
                                mReady = true;
                                ThreadUtil.runOnHandler(mListenerHandler, mPrepareRunnable, 0);
                            }
                        }

                        synchronized (mLock)
                        {
                            if (!mNextFrame)
                            {
                                try
                                {
                                    mLock.wait();
                                }
                                catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    boolean render = outputBufferInfo.size != 0;

                    if (!mAutoDecode)
                    {
                        mNextFrame = false;
                    }

                    decoder.releaseOutputBuffer(outputBufferIndex, render);

                    if ((outputBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
                    {
                        outputDone = true;
                        mReady = false;

                        ThreadUtil.runOnHandler(mListenerHandler, mEndRunnable, 0);
                    }
                }
            }
        }
    }

    public void release()
    {
        mRelease = true;
    }
}
