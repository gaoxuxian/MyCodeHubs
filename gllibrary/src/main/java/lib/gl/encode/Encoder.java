package lib.gl.encode;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import androidx.annotation.NonNull;

import java.io.*;
import java.nio.ByteBuffer;

public class Encoder {

    private MediaCodec.BufferInfo mBufferInfo;
    private boolean mMuxerStarted;
    private int mTrackIndex;
    private MediaMuxer mediaMuxer;
    private MediaCodec encoder;
    private Surface inputSurface;
    private String TAG = getClass().getName();
    private RandomAccessFile mFileWriter;
    private byte[] mSpsVpps;
    private MediaFormat format;

    public Encoder(int width, int height, int bitrate, File file) {
        mBufferInfo = new MediaCodec.BufferInfo();

        format = MediaFormat.createVideoFormat("video/avc", width, height);

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

        try {
            mFileWriter = new RandomAccessFile(file.getParent() + "/test_h264.h264", "rw");
        } catch (FileNotFoundException e) {
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
                setSPSPPSData(outputFormat.getByteBuffer("csd-0"), outputFormat.getByteBuffer("csd-1"));
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

                    byte[] bytes = new byte[outputBuffer.limit()];
                    outputBuffer.get(bytes);

                    try {
//                        // flags 利用位操作，定义的 flag 都是 2 的倍数
//                        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) { // 配置相关的内容，也就是 SPS，PPS
//                            bytes = addSPSPPSData(bytes);
//                            mFileWriter.write(bytes, 0, bytes.length);
//                        } else if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_KEY_FRAME) != 0) { // 关键帧
//                            mFileWriter.write(bytes, 0, bytes.length);
//                        } else {
//                            // 非关键帧和SPS、PPS,直接写入文件，可能是B帧或者P帧
//                            bytes = addSPSPPSData(bytes);
//                            mFileWriter.write(bytes, 0, bytes.length);
//                        }
                        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_KEY_FRAME) != 0) {
                            bytes = addSPSPPSData(bytes);
                            mFileWriter.write(bytes, 0, bytes.length);
                        } else {
                            mFileWriter.write(bytes, 0, bytes.length);
                        }

//                        if(bytes[0] == 0 && bytes[1] == 0 && bytes[2] == 0 && bytes[3] == 1 && (bytes[4]&0x1f) == 5)
//                        {
//                            // I 帧
////                            Log.d(TAG, "drainEncoder: 关键帧");
////                            mFileWriter.write(mSpsVpps);
//                            bytes = addSPSPPSData(bytes);
//                        }
//                        mFileWriter.write(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

    public void setSPSPPSData(ByteBuffer sps, ByteBuffer pps)
    {
        if(sps == null || pps == null)
        {
            return;
        }
        byte[] spsData = new byte[sps.limit()];
        sps.get(spsData);
        sps.clear();
        byte[] ppsData = new byte[pps.limit()];
        pps.get(ppsData);
        pps.clear();

        int size = spsData.length + ppsData.length;
        byte[] data = new byte[size];
        System.arraycopy(spsData, 0, data, 0, spsData.length);
        System.arraycopy(ppsData, 0, data, spsData.length, ppsData.length);
        mSpsVpps = data;
    }

    private byte[] addSPSPPSData(byte[] src)
    {
        if (src == null) {
            return null;
        }

        byte[] out = new byte[src.length + mSpsVpps.length];
        System.arraycopy(mSpsVpps, 0, out, 0, mSpsVpps.length);
        System.arraycopy(src, 0, out, mSpsVpps.length, src.length);
        return out;
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
        if (mFileWriter != null) {
            try {
                mFileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void reset(String path)
    {
        // 190ms
        encoder.reset();
        // 120 ms
        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        inputSurface = encoder.createInputSurface();

        encoder.start();

        try {
            // 20 - 30 ms
            mediaMuxer.release();
            mediaMuxer = new MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mTrackIndex = -1;
        mMuxerStarted = false;
    }
}
