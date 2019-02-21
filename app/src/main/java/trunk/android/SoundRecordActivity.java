package trunk.android;

import android.content.Context;
import android.media.*;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import trunk.BaseActivity;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class SoundRecordActivity extends BaseActivity implements View.OnClickListener {

    private MediaRecorder mMediaRecorder;

    private AudioRecord mAudioRecord;

    private Button mMediaRecorderBtn;

    private Button mAudioRecordBtn;

    private boolean mRecording;

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
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 4);
        mAudioRecord.startRecording();
        ByteBuffer byteBuffer = ByteBuffer.allocate(minBufferSize);

        new Thread(new Runnable() {
            @Override
            public void run() {

                MediaCodec encoder = null;
                try {
                    encoder = MediaCodec.createEncoderByType("audio/mp4a-latm");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (encoder != null) {
                    MediaFormat format = new MediaFormat();
                    format.setInteger(MediaFormat.KEY_SAMPLE_RATE, 44100);
                    format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 2);
                    format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, minBufferSize * 2);
                    format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
                    format.setInteger(MediaFormat.KEY_BIT_RATE, (int) (44100 * 16 * 2 * 0.25f));
                    format.setString(MediaFormat.KEY_MIME, "audio/mp4a-latm");

                    encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
                    encoder.start();

                    long TIME_OUT_ESC = 10000;

                    while (true) {
                        byteBuffer.position(0);
                        int size = mAudioRecord.read(byteBuffer, minBufferSize);
                        if (size > 0) {
                            int inputBufferIndex = encoder.dequeueInputBuffer(TIME_OUT_ESC);

                            if (inputBufferIndex >= 0) {
                                ByteBuffer inputBuffer = encoder.getInputBuffer(inputBufferIndex);
                                if (inputBuffer != null) {
                                    inputBuffer.put(byteBuffer);
                                    encoder.queueInputBuffer(inputBufferIndex, 0, size, System.nanoTime(), 0);
                                }
                            }
                        }
                    }
                }
            }
        }).start();
    }

    private void stopRecordVoiceWithAudioRecord() {

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
                mMediaRecorderBtn.setText("MediaRecorder 正在录制");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
