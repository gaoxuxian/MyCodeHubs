package trunk.gles.view;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import lib.gl.filter.common.DisplayImageFilter;
import lib.gl.filter.innovation.SplitScreenFilterV3;
import trunk.R;
import lib.gl.util.GLUtil;

/**
 * @author Gxx 菱形扩散转场滤镜效果
 * Created by Gxx on 2018/12/21.
 */
public class SplitScreenViewV3 extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private SplitScreenFilterV3 mSplitScreenFilter;
    private DisplayImageFilter mDisplayFilter;
    private long mStartTime;

    public SplitScreenViewV3(Context context)
    {
        super(context);

        setEGLContextClientVersion(GLUtil.getGlSupportVersionInt(context));
        setEGLConfigChooser(8, 8, 8, 8, 16, 8);
        setRenderer(this);
        // setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        // requestRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        GLUtil.checkGlError("AAA");
        mSplitScreenFilter = new SplitScreenFilterV3(getContext());
        GLUtil.checkGlError("AAA");
        mDisplayFilter = new DisplayImageFilter(getContext());

        GLUtil.checkGlError("AAA");
        mSplitScreenFilter.onSurfaceCreated(config);
        GLUtil.checkGlError("AAA");
        mDisplayFilter.onSurfaceCreated(config);
        GLUtil.checkGlError("AAA");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        mSplitScreenFilter.onSurfaceChanged(width, height);
        mSplitScreenFilter.setTextureRes(R.drawable.open_test_6);
        mSplitScreenFilter.initFrameBufferOfTextureSize();

        mDisplayFilter.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        if (mStartTime == 0)
        {
            mStartTime = System.currentTimeMillis();
        }

        GLES30.glClearColor(0, 0, 0, 1);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        mSplitScreenFilter.setTime(mStartTime, System.currentTimeMillis());
        int displayID = mSplitScreenFilter.onDrawBuffer(0);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
        GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);

        mDisplayFilter.setTextureWH(mDisplayFilter.getSurfaceW(), mDisplayFilter.getSurfaceH());
        mDisplayFilter.onDrawFrame(displayID);

        GLES20.glDisable(GLES20.GL_BLEND);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (mSplitScreenFilter != null)
        {
            mSplitScreenFilter.destroy();
            mSplitScreenFilter = null;
        }

        if (mDisplayFilter != null)
        {
            mDisplayFilter.destroy();
            mDisplayFilter = null;
        }
    }
}
