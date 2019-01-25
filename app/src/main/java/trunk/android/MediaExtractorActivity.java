package trunk.android;

import androidx.annotation.Nullable;
import filter.common.DisplayImageFilter;
import filter.common.DisplayOESFilter;
import trunk.BaseActivity;
import util.GLUtil;
import util.PxUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MediaExtractorActivity extends BaseActivity implements View.OnClickListener, GLSurfaceView.Renderer
{
    private Button btn;
    private GLSurfaceView mGlSurfaceView;
    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;
    private DisplayOESFilter mDisplayFilter;
    private DisplayImageFilter mDisplayImgFilter;
    private int[] texture;
    private int mVideoWidth;
    private int mVideoHeight;

    @Override
    public void onCreateBaseData() throws Exception
    {

    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mGlSurfaceView = new GLSurfaceView(context);
        mGlSurfaceView.setEGLContextClientVersion(GLUtil.getGlSupportVersionInt(context));
        mGlSurfaceView.setRenderer(this);
        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        params = new FrameLayout.LayoutParams(PxUtil.sU_1080p(1080), PxUtil.sU_1080p(1920));
        params.gravity = Gravity.CENTER;
        parent.addView(mGlSurfaceView, params);

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

                        if (btn != null)
                        {
                            btn.setVisibility(View.GONE);
                        }
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(path);
                        String sRotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
                        String sWidth = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                        String sHeight = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                        int rotation = Integer.parseInt(sRotation);
                        if (rotation == 0)
                        {
                            mVideoWidth = Integer.parseInt(sWidth);
                            mVideoHeight = Integer.parseInt(sHeight);
                        }
                        retriever.release();
                        // decodeVideo(this, path);
                        decodeVideoV2(path);
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

    public void decodeVideoV2(String path)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                MediaExtractor extractor = new MediaExtractor();
                MediaCodec decoder = null;

                try
                {
                    extractor.setDataSource(path);

                    int trackCount = extractor.getTrackCount();

                    int videoTrackIndex = -1;

                    for (int i = 0;i < trackCount;i++)
                    {
                        MediaFormat trackFormat = extractor.getTrackFormat(i);
                        if (trackFormat.getString(MediaFormat.KEY_MIME).startsWith("video/"))
                        {
                            videoTrackIndex = i;
                            break;
                        }
                    }

                    if (videoTrackIndex >= 0)
                    {
                        extractor.selectTrack(videoTrackIndex);

                        MediaFormat trackFormat = extractor.getTrackFormat(videoTrackIndex);
                        MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.REGULAR_CODECS);
                        String decoderForFormat = mediaCodecList.findDecoderForFormat(trackFormat);

                        if (!TextUtils.isEmpty(decoderForFormat))
                        {
                            decoder = MediaCodec.createByCodecName(decoderForFormat);
                        }
                        else
                        {
                            decoder = MediaCodec.createDecoderByType(trackFormat.getString(MediaFormat.KEY_MIME));
                        }

                        decoder.configure(trackFormat, mSurface, null, 0);
                        decoder.start();
                        deExtract(extractor, decoder);
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void deExtract(MediaExtractor extractor, MediaCodec decoder)
    {
        if (extractor == null || decoder == null) return;

        final long TIMEOUT_ESC = 10000;
        long firstFrameTime = -1;

        boolean outputDone = false;
        boolean inputDone = false;
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

        while (!outputDone)
        {
            if (!inputDone)
            {
                int inputBufferStatus = decoder.dequeueInputBuffer(TIMEOUT_ESC);

                if (inputBufferStatus >= 0)
                {
                    ByteBuffer inputBuffer = decoder.getInputBuffer(inputBufferStatus);

                    if (inputBuffer != null)
                    {
                        int checkSize = extractor.readSampleData(inputBuffer, 0);

                        if (checkSize <= 0)
                        {
                            decoder.queueInputBuffer(inputBufferStatus, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            inputDone = true;
                        }
                        else
                        {
                            long presentationTimeUs = extractor.getSampleTime();
                            decoder.queueInputBuffer(inputBufferStatus, 0, checkSize, presentationTimeUs, 0);
                            extractor.advance();
                        }
                    }

                }
            }

            if (!outputDone)
            {
                int outputBufferStatus = decoder.dequeueOutputBuffer(bufferInfo, TIMEOUT_ESC);

                if (outputBufferStatus == MediaCodec.INFO_TRY_AGAIN_LATER)
                {
                    Log.d("xxxx", "deExtract: no output from decoder available");
                }
                else if (outputBufferStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED)
                {
                    MediaFormat outputFormat = decoder.getOutputFormat();

                    Log.d("xxxx", "deExtract: output format changed, new format is == "+ outputFormat.toString());
                }
                else if (outputBufferStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    // not important for us, since we're using Surface
                    Log.d("xxxx", "decoder output buffers changed");
                }
                else
                {
                    if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
                    {
                        outputDone = true;
                    }

                    boolean doRender = bufferInfo.size != 0;

                    if (firstFrameTime == -1)
                    {
                        firstFrameTime = System.nanoTime();
                    }
                    else
                    {
                        long dNs = bufferInfo.presentationTimeUs * 1000L - (System.nanoTime() - firstFrameTime);

                        long sleepMs = dNs / 1000000L;
                        int sleepNs = (int) (dNs % 1000000L);
                        try
                        {
                            if (sleepMs >= 0 || sleepNs >= 0)
                            {
                                Thread.sleep(sleepMs, sleepNs);
                            }
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    decoder.releaseOutputBuffer(outputBufferStatus, doRender);
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
                        MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.REGULAR_CODECS);
                        String decoderForFormat = mediaCodecList.findDecoderForFormat(format);

                        if (decoderForFormat != null)
                        {
                            decoder = MediaCodec.createByCodecName(decoderForFormat);
                        }
                        else
                        {
                            String mime = format.getString(MediaFormat.KEY_MIME);
                            decoder = MediaCodec.createDecoderByType(mime);
                        }

                        decoder.configure(format, mSurface, null, 0);
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

                    Log.d("xxxx", "onActivityResult-finally-解码结束");
                }
            }
        }).start();
    }

    public void doExtract(MediaExtractor extractor, int trackIndex, MediaCodec decoder)
    {
        if (extractor == null || trackIndex < 0 || decoder == null) return;

        final int TIMEOUT_USEC = 10000;

        ByteBuffer[] decoderinputBuffers = decoder.getInputBuffers();

        int inputChunk = 0;
        long firstInputTimeNsec = -1;
        long lastFrameTime = 0;

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
                        Log.d("xxxx", "submitted frame " + inputChunk + " to dec, size=" + chunkSize);
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

                    try
                    {
                        if (firstInputTimeNsec == -1)
                        {
                            firstInputTimeNsec = System.nanoTime();
                        }
                        else
                        {
                            Log.d("xxx", "doExtract: 当前帧时间戳/ms == " + (mBufferInfo.presentationTimeUs / 1000f));
                            long dtNs = mBufferInfo.presentationTimeUs * 1000L - (System.nanoTime() - firstInputTimeNsec);
                            long sleepMs = dtNs / 1000000;
                            int sleepNs = (int) (dtNs % 1000000);
                            Log.d("xxx", "doExtract: 睡眠时间(帧率/ms) == " + sleepMs + "." + sleepNs);
                            if (sleepMs > 0 && sleepNs > 0)
                            {
                                Thread.sleep(sleepMs, sleepNs);
                            }
                        }

                        boolean doRender = (mBufferInfo.size != 0);
                        /**
                         * 只要调用了decoder.releaseOutputBuffer(),
                         * 就会把输出队列的数据全部输出到Surface上显示,并且释放输出队列的数据
                         */
                        decoder.releaseOutputBuffer(decoderStatus, doRender);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        mSurfaceTexture = new SurfaceTexture(texture[0]);
        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener()
        {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture)
            {
                mGlSurfaceView.requestRender();
            }
        });

        mSurface = new Surface(mSurfaceTexture);

        mDisplayFilter = new DisplayOESFilter(mGlSurfaceView.getContext());
        mDisplayFilter.onSurfaceCreated(config);

        mDisplayImgFilter = new DisplayImageFilter(mGlSurfaceView.getContext());
        mDisplayImgFilter.onSurfaceCreated(config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        mDisplayFilter.onSurfaceChanged(width, height);
        mDisplayImgFilter.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        mSurfaceTexture.updateTexImage();

        mDisplayFilter.setTextureWH(mVideoWidth, mVideoHeight);
        mDisplayFilter.initFrameBufferOfTextureSize();
        int i = mDisplayFilter.onDrawBuffer(texture[0]);

        mDisplayImgFilter.setTextureWH(mVideoWidth, mVideoHeight);
        mDisplayImgFilter.onDrawFrame(i);
    }
}
