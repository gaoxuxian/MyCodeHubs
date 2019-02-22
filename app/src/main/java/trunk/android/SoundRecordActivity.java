package trunk.android;

import android.content.Context;
import android.media.*;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import trunk.BaseActivity;

import java.io.*;
import java.nio.ByteBuffer;

public class SoundRecordActivity extends BaseActivity implements View.OnClickListener {

    private MediaRecorder mMediaRecorder;

    private AudioRecord mAudioRecord;

    private Button mMediaRecorderBtn;

    private Button mAudioRecordBtn;

    private boolean mRecording;

    private boolean mStopRecording;

    private volatile MediaCodec encoder;

    private volatile boolean initEncoder;

    private boolean mNeedEncode;

    private final Object mLock = new Object();

    @Override
    public void onCreateBaseData() throws Exception {

    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params) {
        mMediaRecorderBtn = new Button(context);
        mMediaRecorderBtn.setAllCaps(false);
        mMediaRecorderBtn.setText("MediaRecorder 开始录制音频");
        mMediaRecorderBtn.setOnClickListener(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP | Gravity.START;
        parent.addView(mMediaRecorderBtn, params);

        mAudioRecordBtn = new Button(context);
        mAudioRecordBtn.setAllCaps(false);
        mAudioRecordBtn.setText("AudioRecord 开始录制音频");
        mAudioRecordBtn.setOnClickListener(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
        parent.addView(mAudioRecordBtn, params);
    }

    @Override
    public void onCreateFinish() {

    }

    @Override
    public void onClick(View v) {
        if (v == mMediaRecorderBtn) {
            if (mRecording) {
                stopRecordVoiceWithMediaRecorder();
            } else {
                startRecordVoiceWithMediaRecorder();
            }
        } else if (v == mAudioRecordBtn) {
            if (mRecording) {
                stopRecordVoiceWithAudioRecord();
            } else {
                startRecordVoiceWithAudioRecord();
            }
        }
    }

    private void startRecordVoiceWithAudioRecord() {
        int minBufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
        mAudioRecord.startRecording();
        mAudioRecordBtn.setText("AudioRecord 正在录制，点击结束录制");

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    File file = new File(getFilesDir(), "test_record_audio_v2.aac");
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                    BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file));

                    MediaFormat format = new MediaFormat();
                    format.setString(MediaFormat.KEY_MIME, MediaFormat.MIMETYPE_AUDIO_AAC);
                    format.setInteger(MediaFormat.KEY_SAMPLE_RATE, 44100);
                    format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 2);
                    format.setInteger(MediaFormat.KEY_BIT_RATE, (int) (44100 * 16 * 2 * 0.25f));
                    format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, minBufferSize);
                    format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);

                    encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
                    encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
                    encoder.start();

                    initEncoder = true;

                    long TIME_OUT_ESC = 10000;
                    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

                    while (true) {
                        synchronized (mLock) {
                            if (!mNeedEncode) {
                                try {
                                    mLock.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        int outputBufferIndex = encoder.dequeueOutputBuffer(bufferInfo, TIME_OUT_ESC);

                        if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {

                        } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {

                        } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {

                        } else if (outputBufferIndex < 0) {

                        } else {
                            ByteBuffer outputBuffer = encoder.getOutputBuffer(outputBufferIndex);
                            if (outputBuffer != null) {
                                int packetSize = bufferInfo.size + 7;
                                byte[] packet = new byte[packetSize];
                                addADTStoPacket(packet, packetSize);
                                outputBuffer.position(bufferInfo.offset);
                                outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
                                outputBuffer.get(packet, 7, bufferInfo.size);
                                writer.write(packet, 0, packetSize);
                            }
                            encoder.releaseOutputBuffer(outputBufferIndex, false);

                            if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                                mStopRecording = false;
                                mRecording = false;
                                break;
                            }
                        }
                    }

                    if (encoder != null) {
                        encoder.stop();
                        encoder.release();
                        encoder = null;
                        initEncoder = false;
                    }

                    writer.close();
                    Log.d("xxx", "录制结束");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ByteBuffer byteBuffer = ByteBuffer.allocateDirect(minBufferSize);

                while (true) {
                    if (!mRecording) {
                        mRecording = true;
                    }
                    byteBuffer.clear();
                    if (mAudioRecord != null) {
                        // 读录音数据要跟编码分开线程
                        int size = mAudioRecord.read(byteBuffer, minBufferSize);
                        if (mStopRecording) {
                            putPCMData(null, 0);
                            Log.d("xxx", "通知录制结束");
                            if (mAudioRecord != null) {
                                mAudioRecord.stop();
                                mAudioRecord.release();
                                mAudioRecord = null;
                            }
                            break;
                        } else if (size > 0) {
                            putPCMData(byteBuffer, size);
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 添加ADTS头
     *
     * @param packet
     * @param packetLen
     */
    private void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 2; // AAC LC
        int freqIdx = 4; // 44.1KHz
        int chanCfg = 2; // CPE

        // fill in ADTS data
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }

    public void putPCMData(ByteBuffer byteBuffer, int size) {
        if (initEncoder && encoder != null) {
            long TIME_OUT_ESC = 1000;
            int inputBufferIndex = encoder.dequeueInputBuffer(TIME_OUT_ESC);
            if (inputBufferIndex >= 0) {
                if (byteBuffer != null) {
                    ByteBuffer inputBuffer = encoder.getInputBuffer(inputBufferIndex);
                    if (inputBuffer != null) {
                        inputBuffer.put(byteBuffer);
                        encoder.queueInputBuffer(inputBufferIndex, 0, size, System.nanoTime() / 1000L, 0);
                    }
                } else {
                    encoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                }

                synchronized (mLock) {
                    mNeedEncode = true;
                    mLock.notify();
                }
            }
        }
    }

    private void stopRecordVoiceWithAudioRecord() {
        mStopRecording = true;
        mAudioRecordBtn.setText("AudioRecord 开始录制音频");
        Toast.makeText(this, "AudioRecord 已停止", Toast.LENGTH_SHORT).show();
    }

    private void stopRecordVoiceWithMediaRecorder() {
        if (mMediaRecorder != null && mRecording) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mMediaRecorderBtn.setText("MediaRecorder 开始录制音频");
            Toast.makeText(this, "MediaRecorder 已停止", Toast.LENGTH_SHORT).show();
        }
    }

    private void startRecordVoiceWithMediaRecorder() {
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioChannels(2);
            mMediaRecorder.setOutputFile(getFilesDir() + "/test_record_voice.aac");
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mMediaRecorder.setAudioSamplingRate(44100);
            mMediaRecorder.setAudioEncodingBitRate((int) (44100 * 16 * 2 * 0.25f));
            try {
                mMediaRecorder.prepare();
                mMediaRecorder.start();
                mRecording = true;
                mMediaRecorderBtn.setText("MediaRecorder 正在录制，点击结束录制");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
