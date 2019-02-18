package lib.gl.encode;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;
import android.view.Surface;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Encoder {

    private MediaCodec.BufferInfo mBufferInfo;
    private boolean mMuxerStarted;
    private int mTrackIndex;
    private MediaMuxer mediaMuxer;
    private MediaCodec encoder;
    private Surface inputSurface;
    private String TAG = getClass().getName();

    public Encoder(int width, int height, int bitrate, File file) {
        mBufferInfo = new MediaCodec.BufferInfo();

        MediaFormat format = MediaFormat.createVideoFormat("video/avc", width, height);

        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
//        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatRGBAFlexible);
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);

        try {
            encoder = MediaCodec.createEncoderByType("video/avc");
            encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            inputSurface = encoder.createInputSurface();
        } catch (IOException e) {
            e.printStackTrace();
        }

        encoder.start();

        try {
            mediaMuxer = new MediaMuxer(file.toString(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mTrackIndex = -1;
        mMuxerStarted = false;
    }

    public Surface getInputSurface(){
        return inputSurface;
    }

    public void drainEncoder(boolean endOfStream){
        final int TIMEOUT_USEC = 10000;

        if (endOfStream){
            encoder.signalEndOfInputStream();
        }

        while (true) {
            int outputBufferIndex = encoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
            if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER){
                if (!endOfStream) {
                    break;
                } else if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    break;
                }
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                if (mMuxerStarted) {
                    throw new RuntimeException("format changed twice");
                }

                MediaFormat outputFormat = encoder.getOutputFormat();
                mTrackIndex = mediaMuxer.addTrack(outputFormat);
                mediaMuxer.start();
                mMuxerStarted = true;
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                Log.d(TAG, "output buffers changed");
            } else if (outputBufferIndex < 0) {
                Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: " +
                        outputBufferIndex);
            } else {
                ByteBuffer outputBuffer = encoder.getOutputBuffer(outputBufferIndex);

                if (outputBuffer == null) {
                    throw new RuntimeException("encoderOutputBuffer " + outputBufferIndex +
                            " was null");
                }

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
                    mBufferInfo.size = 0;
                }

                if (mBufferInfo.size != 0) {
                    if (!mMuxerStarted) {
                        throw new RuntimeException("muxer hasn't started");
                    }

                    outputBuffer.position(mBufferInfo.offset);
                    outputBuffer.limit(mBufferInfo.offset + mBufferInfo.size);

                    mediaMuxer.writeSampleData(mTrackIndex, outputBuffer, mBufferInfo);
                    Log.d(TAG, "sent " + mBufferInfo.size + " bytes to muxer, ts=" +
                            mBufferInfo.presentationTimeUs);

                    encoder.releaseOutputBuffer(outputBufferIndex, false);

                    if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        if (!endOfStream) {
                            Log.w(TAG, "reached end of stream unexpectedly");
                        } else {
                            Log.d(TAG, "end of stream reached");
                        }
                        break;
                    }
                }
            }
        }
    }

    public void release() {
        Log.d(TAG, "releasing encoder objects");
        if (encoder != null) {
            encoder.stop();
            encoder.release();
            encoder = null;
        }
        if (mediaMuxer != null) {
            // TODO: stop() throws an exception if you haven't fed it any data.  Keep track
            //       of frames submitted, and don't call stop() if we haven't written anything.
            mediaMuxer.stop();
            mediaMuxer.release();
            mediaMuxer = null;
        }
    }
}
