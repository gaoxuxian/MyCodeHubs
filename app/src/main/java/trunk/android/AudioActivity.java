package trunk.android;

import android.content.Context;
import android.media.*;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import trunk.BaseActivity;

import java.io.IOException;
import java.nio.ByteBuffer;

public class AudioActivity extends BaseActivity implements View.OnClickListener {

    private String mPath;

    private MediaPlayer mMediaPlayer;
    private SoundPool mSoundPool;
    private int mSoundID = -1;
    private AudioTrack mAudioTrack;

    private Button mMediaPlayerBtn;
    private Button mSoundPoolBtn;
    private Button mAudioTrackBtn;

    private volatile boolean mBreakThread;

    @Override
    public void onCreateBaseData() throws Exception {
//        mPath = getFilesDir() + "/music_test_1.mp3";
//        mPath = getFilesDir() + "/test_record_voice.aac";
        mPath = getFilesDir() + "/test_record_audio_v2.aac";
    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params) {
        mMediaPlayerBtn = new Button(context);
        mMediaPlayerBtn.setAllCaps(false);
        mMediaPlayerBtn.setText("MediaPlayer 播放音频");
        mMediaPlayerBtn.setOnClickListener(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP | Gravity.START;
        parent.addView(mMediaPlayerBtn, params);

        mSoundPoolBtn = new Button(context);
        mSoundPoolBtn.setAllCaps(false);
        mSoundPoolBtn.setText("SoundPool 播放音频");
        mSoundPoolBtn.setOnClickListener(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP | Gravity.END;
        parent.addView(mSoundPoolBtn, params);

        mAudioTrackBtn = new Button(context);
        mAudioTrackBtn.setAllCaps(false);
        mAudioTrackBtn.setText("AudioTrack 播放音频");
        mAudioTrackBtn.setOnClickListener(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        parent.addView(mAudioTrackBtn, params);
    }

    @Override
    public void onCreateFinish() {

    }

    @Override
    public void onClick(View v) {
        if (v == mMediaPlayerBtn) {
            playMusicWithMediaPlayer(mPath);
        }
        else if (v == mSoundPoolBtn) {
            playMusicWithSoundPool(mPath);
        }
        else if (v == mAudioTrackBtn) {
            playMusicWithAudioTrack(mPath);
        }
    }

    private void playMusicWithAudioTrack(String path) {
        releaseMedia();

        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaExtractor mediaExtractor = new MediaExtractor();
                try {
                    mediaExtractor.setDataSource(path);
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
        }).start();
    }

    private void playMusicWithSoundPool(String path) {
        releaseMedia();

        SoundPool.Builder builder = new SoundPool.Builder();

        AudioAttributes.Builder attributesBuilder = new AudioAttributes.Builder();
        attributesBuilder.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC);
        attributesBuilder.setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED);
        attributesBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
        attributesBuilder.setUsage(AudioAttributes.USAGE_MEDIA);
        AudioAttributes attributes = attributesBuilder.build();

        builder.setAudioAttributes(attributes);
        mSoundPool = builder.build();

        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mSoundID = sampleId;
                soundPool.play(sampleId, 1f, 1f, 0, 0, 1);
            }
        });
        mSoundPool.load(path, 1);
    }

    private void playMusicWithMediaPlayer(String path) {
        releaseMedia();

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        try {
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void releaseMedia() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
            mMediaPlayer = null;
        }

        if (mSoundPool != null) {
            mSoundPool.unload(mSoundID);
            mSoundPool.release();
            mSoundPool = null;
        }

        if (mAudioTrack != null) {
            int playState = mAudioTrack.getPlayState();
            if (playState == AudioTrack.PLAYSTATE_PLAYING) {
                mBreakThread = true;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        releaseMedia();
    }
}
