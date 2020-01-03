package trunk.android;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.xx.commonlib.PxUtil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import trunk.BaseActivity;

public class RewindTestActivity extends BaseActivity {

    private SurfaceView mSurfaceView;

    @Override
    public void onCreateBaseData() throws Exception {

    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params) {
        mSurfaceView = new SurfaceView(context);
        params = new FrameLayout.LayoutParams(PxUtil.sU_1080p(544), PxUtil.sU_1080p(960));
        params.gravity = Gravity.CENTER;
        parent.addView(mSurfaceView, params);
    }

    @Override
    public void onCreateFinish() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        mSurfaceView.postDelayed(new Runnable() {
            @Override
            public void run() {
                startToPlay();
            }
        }, 1000);
    }

    private void startToPlay() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String path = getFilesDir() + File.separator + "video_reedit_test_v2.mp4";
                    MediaExtractor extractor = new MediaExtractor();
                    extractor.setDataSource(path);

                    int videoTrackIndex = -1;
                    MediaFormat videoFormat = null;
                    int trackCount = extractor.getTrackCount();
                    for (int i = 0; i < trackCount; i++) {
                        MediaFormat trackFormat = extractor.getTrackFormat(i);
                        String mime = trackFormat.getString(MediaFormat.KEY_MIME);

                        if (!TextUtils.isEmpty(mime) && mime.startsWith("video/")) {
                            videoFormat = trackFormat;
                            videoTrackIndex = i;
                        }
                        extractor.unselectTrack(i);
                    }

                    ArrayList<Long> sampleTimeArray = new ArrayList<>();

                    if (videoTrackIndex > -1) {
                        extractor.selectTrack(videoTrackIndex);

                        long start = System.currentTimeMillis();

                        while (true) {
                            long sampleTime = extractor.getSampleTime();
                            if (sampleTime >= 0) {
                                sampleTimeArray.add(sampleTime);
                            }
                            boolean advance = extractor.advance();
                            if (!advance) {
                                extractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
                                break;
                            }
                        }

                        Log.d("xxx", "run: 遍历全部时间戳 耗时：" + (System.currentTimeMillis() - start));
                        for (long time : sampleTimeArray) {
                            Log.d("xxx", "run: 时间戳 ：" + time);
                        }

                        MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.REGULAR_CODECS);
                        String decoderName = mediaCodecList.findDecoderForFormat(videoFormat);
                        MediaCodec codec = MediaCodec.createByCodecName(decoderName);
                        codec.configure(videoFormat, mSurfaceView.getHolder().getSurface(), null, 0);
                        codec.start();

                        boolean inputDone = false;
                        long TIME_OUT = 10000;
                        long firstFrameTime = 0;
                        long currentFrameTime = 0;
                        boolean rewind = false;
                        boolean rewindEnd = false;
                        long rewindEndTime = 0;
                        boolean updateTime = false;


                        long lastFrameTime = 0;

                        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

                        while (true) {
                            if (!inputDone) {
                                if (!rewindEnd && !rewind && (currentFrameTime >= 7025000)) {
                                    currentFrameTime = 7025000;
                                    lastFrameTime = currentFrameTime;
                                    rewind = true;
                                    codec.flush();
                                }

                                if (!rewindEnd && rewind) {
                                    for (int i = 0; i < sampleTimeArray.size(); i++) {
                                        Long time = sampleTimeArray.get(i);
                                        if (time == currentFrameTime && i != 0) {
                                            time = sampleTimeArray.get(i - 1);
                                            currentFrameTime = time;
                                            Log.d("xxx", "run: rewind 时间戳 == " + time);
                                            long seek_start_time = System.currentTimeMillis();
                                            extractor.seekTo(time, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
                                            Log.d("xxx", "run: seek 耗时 == " + (System.currentTimeMillis() - seek_start_time));
                                            break;
                                        }
                                    }

                                    if (currentFrameTime <= 5l * 1000 * 1000) {
                                        rewindEnd = true;
                                        rewind = false;
                                        updateTime = true;
                                    }
                                }

                                int inputBufferIndex = codec.dequeueInputBuffer(TIME_OUT);

                                if (inputBufferIndex >= 0) {
                                    ByteBuffer inputBuffer = codec.getInputBuffer(inputBufferIndex);
                                    if (inputBuffer != null) {
                                        int size = extractor.readSampleData(inputBuffer, 0);
                                        long sampleTime = extractor.getSampleTime(); // 微秒

                                        if (size > 0) {
                                            codec.queueInputBuffer(inputBufferIndex, 0, size, sampleTime, 0);
                                            extractor.advance();
                                        } else {
                                            codec.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                                            inputDone = true;
                                        }
                                    }
                                }
                            }

                            int outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo, TIME_OUT);
                            if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {

                            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {

                            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {

                            } else if (outputBufferIndex < 0) {

                            } else {
                                if (firstFrameTime == 0 || updateTime) {
                                    updateTime = false;
                                    firstFrameTime = System.nanoTime();
                                }

                                if (rewindEnd) {
                                    rewindEndTime = 5008000 * 1000l;
                                }

                                boolean render = bufferInfo.size != 0;

                                long dNs = 0;
                                if (rewind) {
                                    dNs = Math.abs(bufferInfo.presentationTimeUs * 1000L - lastFrameTime * 1000l) / 2l;
                                } else if (rewindEnd) {
                                    dNs = bufferInfo.presentationTimeUs * 1000L - (System.nanoTime() - firstFrameTime + rewindEndTime);
                                } else {
                                    dNs = bufferInfo.presentationTimeUs * 1000L - (System.nanoTime() - firstFrameTime);
                                }

//                                dNs = bufferInfo.presentationTimeUs * 1000L - (System.nanoTime() - firstFrameTime - rewindStartTime);

                                Log.d("xxx", "run: 睡眠时间 == " + dNs);
                                long millis = dNs / 1000000L;
                                int nanos = (int) (dNs & 1000L);

                                if (millis > 0 && nanos > 0) {
                                    try {
                                        Thread.sleep(millis, nanos);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (!rewind) {
                                    currentFrameTime = bufferInfo.presentationTimeUs;
                                } else {
                                    lastFrameTime = bufferInfo.presentationTimeUs;
                                }

                                codec.releaseOutputBuffer(outputBufferIndex, render);

                                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                                    break;
                                }
                            }
                        }

                        extractor.release();

                        codec.release();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
