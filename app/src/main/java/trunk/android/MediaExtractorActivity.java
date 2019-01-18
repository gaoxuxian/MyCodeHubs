package trunk.android;

import androidx.annotation.Nullable;
import trunk.BaseActivity;
import util.PxUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class MediaExtractorActivity extends BaseActivity implements View.OnClickListener
{
    private Button btn;
    private SurfaceView mSurfaceView;
    // private String mVideoFilePath;

    @Override
    public void onCreateBaseData() throws Exception
    {

    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mSurfaceView = new SurfaceView(context);
        params = new FrameLayout.LayoutParams(PxUtil.sU_1080p(1080), PxUtil.sU_1080p(1920));
        params.gravity = Gravity.CENTER;
        parent.addView(mSurfaceView, params);

        btn = new Button(context);
        btn.setText("选视频");
        btn.setOnClickListener(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        parent.addView(btn, params);
    }

    @Override
    public void onCreateFinish()
    {

    }

    @Override
    public void onClick(View v)
    {
        if (v == btn)
        {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("video/*");
            startActivityForResult(intent, 2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == Activity.RESULT_OK)
        {
            Log.d("xxxx", "onActivityResult");

            Cursor cursor = null;
            try
            {
                if (data != null && data.getData() != null)
                {
                    cursor = getContentResolver().query(data.getData(), new String[]{MediaStore.Video.Media.DATA}, null, null, null);

                    if (cursor != null && cursor.moveToFirst())
                    {
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        Log.d("xxxx", "onActivityResult , 选中的视频绝对路径 == " + path);
                        // if (FileUtil.isFileExists(path))
                        // {
                        //     mVideoFilePath = path;
                        // }
                        if (btn != null)
                        {
                            btn.setVisibility(View.GONE);
                        }
                        decodeVideo(this, path);
                    }
                }
            }
            catch (Exception e)
            {

            }
            finally
            {
                if (cursor != null && !cursor.isClosed())
                {
                    cursor.close();
                }
            }
        }
    }

    public void decodeVideo(Context context, String path)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                MediaExtractor extractor = null;
                MediaCodec decoder = null;

                try
                {
                    extractor = new MediaExtractor();

                    extractor.setDataSource(path);
                    // HashMap<String, String> headers = new HashMap<>();
                    // extractor.setDataSource(context, Uri.parse(path), headers);

                    int trackCount = extractor.getTrackCount();

                    int trackIndex = -1;

                    for (int i = 0; i < trackCount; i++)
                    {
                        MediaFormat trackFormat = extractor.getTrackFormat(i);
                        String mime = trackFormat.getString(MediaFormat.KEY_MIME);
                        if (mime.startsWith("video/"))
                        {
                            Log.d("xxxx", "Extractor selected track " + i + " (" + mime + "): " + trackFormat);
                            trackIndex = i;
                            break;
                        }
                    }

                    if (trackIndex >= 0)
                    {
                        extractor.selectTrack(trackIndex);

                        MediaFormat format = extractor.getTrackFormat(trackIndex);
                        String mime = format.getString(MediaFormat.KEY_MIME);

                        decoder = MediaCodec.createDecoderByType(mime);

                        decoder.configure(format, mSurfaceView.getHolder().getSurface(), null, 0);
                        decoder.start();

                        doExtract(extractor, trackIndex, decoder);
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if (decoder != null) {
                        decoder.stop();
                        decoder.release();
                    }

                    if (extractor != null)
                    {
                        extractor.release();
                    }
                }
            }
        }).start();
    }

    public void doExtract(MediaExtractor extractor, int trackIndex, MediaCodec decoder)
    {
        final int TIMEOUT_USEC = 10000;

        ByteBuffer[] decoderinputBuffers = decoder.getInputBuffers();

        int inputChunk = 0;
        long firstInputTimeNsec = -1;

        boolean outputDone = false;
        boolean inputDone = false;
        MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();

        while (!outputDone)
        {
            if (!inputDone)
            {
                int inputBufferIndex = decoder.dequeueInputBuffer(TIMEOUT_USEC);

                if (inputBufferIndex >= 0)
                {
                    if (firstInputTimeNsec == -1)
                    {
                        firstInputTimeNsec = System.nanoTime();
                    }

                    ByteBuffer inputBuf = decoderinputBuffers[inputBufferIndex];

                    int chunkSize = extractor.readSampleData(inputBuf, 0);

                    if (chunkSize < 0)
                    {
                        // end of stream
                        decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        inputDone = true;
                        Log.d("xxxx", "end of stream");
                    }
                    else
                    {
                        if (extractor.getSampleTrackIndex() != trackIndex)
                        {
                            Log.w("xxxx", "WEIRD: got sample from track " +
                                    extractor.getSampleTrackIndex() + ", expected " + trackIndex);
                        }

                        long presentationTimeUs = extractor.getSampleTime();
                        decoder.queueInputBuffer(inputBufferIndex, 0, chunkSize, presentationTimeUs, 0);
                        Log.d("xxxx", "submitted frame " + inputChunk + " to dec, size=" +
                                chunkSize);
                        inputChunk++;
                        extractor.advance();
                    }
                }
                else {
                    Log.d("xxxx", "input buffer not available");
                }
            }

            if (!outputDone)
            {
                int decoderStatus = decoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
                if (decoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    // no output available yet
                    Log.d("xxxx", "no output from decoder available");
                } else if (decoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    // not important for us, since we're using Surface
                    Log.d("xxxx", "decoder output buffers changed");
                } else if (decoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    MediaFormat newFormat = decoder.getOutputFormat();
                    Log.d("xxxx", "decoder output format changed: " + newFormat);
                } else if (decoderStatus < 0) {
                    throw new RuntimeException(
                            "unexpected result from decoder.dequeueOutputBuffer: " + decoderStatus);
                } else {
                    if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
                    {
                        Log.d("xxxx", "output EOS");
                        outputDone = true;
                    }
                    boolean doRender = (mBufferInfo.size != 0);
                    /**
                     * 只要我们调用了decoder.releaseOutputBuffer(),
                     * 就会把输出队列的数据全部输出到Surface上显示,并且释放输出队列的数据
                     */
                    decoder.releaseOutputBuffer(decoderStatus, doRender);
                }
            }
        }
    }
}
