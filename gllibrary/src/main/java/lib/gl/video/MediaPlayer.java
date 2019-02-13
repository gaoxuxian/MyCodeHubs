package lib.gl.video;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import lib.gl.filter.common.DisplayImageFilter;
import lib.gl.filter.common.DisplayOESFilter;
import lib.gl.util.GLUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MediaPlayer extends FrameLayout implements GLSurfaceView.Renderer {

    private GLSurfaceView mSurfaceView;
    private VideoDecoder mVideoDecoder;
    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;
    private int[] mSurfaceTextureID;
    private MediaPlayer mMediaPlayer;

    private DisplayOESFilter mOESFilter;
    private DisplayImageFilter mDisplayFilter;

    private boolean mUpdateTexture;
    private boolean mCanDraw;

    public MediaPlayer(@NonNull Context context) {
        super(context);
        mMediaPlayer = this;
        init(context);
    }

    private void init(Context context) {
        mSurfaceView = new GLSurfaceView(context);
        mSurfaceView.setEGLContextClientVersion(GLUtil.getGlSupportVersionInt(context));
        mSurfaceView.setRenderer(this);
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mSurfaceView, params);
    }

    public void setMediaPath(String path, boolean refitWH) {
        mVideoDecoder = new VideoDecoder(path, mSurface);
        mVideoDecoder.setDecodeListener(new Handler(Looper.getMainLooper()), new VideoDecodeListener() {
            @Override
            public void onPrepareSucceed(VideoDecoder decoder) {
                mCanDraw = true;
                if (refitWH)
                {
                    ViewGroup.LayoutParams layoutParams = mMediaPlayer.getLayoutParams();
                    if (layoutParams.width != decoder.getVideoWidth() || layoutParams.height != decoder.getVideoHeight()) {
                        layoutParams.width = decoder.getVideoWidth();
                        layoutParams.height = decoder.getVideoHeight();
                        mMediaPlayer.requestLayout();
                    }
                }
            }

            @Override
            public void onFirstFrameAutoRender(VideoDecoder decoder) {
                decoder.setPause(false);
                decoder.nextFrame(true);
            }

            @Override
            public void onEnd(VideoDecoder decoder) {

            }
        });
        mVideoDecoder.start();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mSurfaceTextureID = new int[1];
        GLES20.glGenTextures(mSurfaceTextureID.length, mSurfaceTextureID, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mSurfaceTextureID[0]);
        GLUtil.bindTextureOESParams();
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);

        mSurfaceTexture = new SurfaceTexture(mSurfaceTextureID[0]);
        mSurfaceTexture.setOnFrameAvailableListener(surfaceTexture -> {
            mUpdateTexture = true;
            mSurfaceView.requestRender();
        });

        mSurface = new Surface(mSurfaceTexture);

        mOESFilter = new DisplayOESFilter(mSurfaceView.getContext());
        mOESFilter.onSurfaceCreated(config);

        mDisplayFilter = new DisplayImageFilter(mSurfaceView.getContext());
        mDisplayFilter.onSurfaceCreated(config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (mOESFilter != null) {
            mOESFilter.onSurfaceChanged(width, height);
        }

        if (mDisplayFilter != null){
            mDisplayFilter.onSurfaceChanged(width, height);
        }

        if (mVideoDecoder != null) {
            mVideoDecoder.readyForRender();
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mUpdateTexture) {
            mSurfaceTexture.updateTexImage();
            mUpdateTexture = false;
        }

        if (mCanDraw) {
            int width = 0;
            int height = 0;
            if (mVideoDecoder != null) {
                width = mVideoDecoder.getVideoWidth();
                height = mVideoDecoder.getVideoHeight();
            }
            mOESFilter.setTextureWH(width, height);
            mOESFilter.initFrameBufferOfTextureSize();
            int texture = mOESFilter.onDrawBuffer(mSurfaceTextureID[0]);

            mDisplayFilter.setTextureWH(width, height);
            mDisplayFilter.onDrawFrame(texture);
        }
    }

    public void pause(boolean pause) {
        if (mVideoDecoder != null) {
            mVideoDecoder.setPause(pause);
        }
    }

    public void destroy(){
        if (mVideoDecoder != null) {
            mVideoDecoder.release();
        }

        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
        }

        if (mSurface != null && mSurface.isValid()) {
            mSurface.release();
        }

        if (mSurfaceTextureID != null)
        {
            GLES20.glDeleteTextures(mSurfaceTextureID.length, mSurfaceTextureID, 0);
        }

        if (mDisplayFilter != null) {
            mDisplayFilter.destroy();
        }

        if (mOESFilter != null) {
            mOESFilter.destroy();
        }
    }
}
