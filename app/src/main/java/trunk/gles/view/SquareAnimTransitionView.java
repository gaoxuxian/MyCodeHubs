package trunk.gles.view;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import filter.common.BmpToTextureFilter;
import filter.common.DisplayImageFilter;
import filter.transitions.SquareAnimTransitionFilter;
import trunk.R;
import util.GLUtil;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/2.
 */
public class SquareAnimTransitionView extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private BmpToTextureFilter mFrontTextureFilter;
    private BmpToTextureFilter mBackTextureFilter;
    private SquareAnimTransitionFilter mSquareAnimTransitionFilter;
    private DisplayImageFilter mDisplayFilter;

    private long mStartTime;

    public SquareAnimTransitionView(Context context)
    {
        super(context);

        setEGLContextClientVersion(GLUtil.getGlSupportVersionInt(context));
        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        mFrontTextureFilter = new BmpToTextureFilter(getContext());
        mFrontTextureFilter.onSurfaceCreated(config);

        mBackTextureFilter = new BmpToTextureFilter(getContext());
        mBackTextureFilter.onSurfaceCreated(config);

        mSquareAnimTransitionFilter = new SquareAnimTransitionFilter(getContext());
        mSquareAnimTransitionFilter.onSurfaceCreated(config);

        mDisplayFilter = new DisplayImageFilter(getContext());
        mDisplayFilter.onSurfaceCreated(config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        mFrontTextureFilter.onSurfaceChanged(width, height);
        mFrontTextureFilter.setBitmapRes(R.drawable.open_test);
        mFrontTextureFilter.initFrameBufferOfTextureSize();

        mBackTextureFilter.onSurfaceChanged(width, height);
        mBackTextureFilter.setBitmapRes(R.drawable.open_test_3);
        mBackTextureFilter.initFrameBufferOfTextureSize();

        mSquareAnimTransitionFilter.setTextureWH(mFrontTextureFilter.getTextureW(), mFrontTextureFilter.getTextureH());
        mSquareAnimTransitionFilter.initFrameBufferOfTextureSize();
        mSquareAnimTransitionFilter.onSurfaceChanged(width, height);

        mDisplayFilter.setTextureWH(mFrontTextureFilter.getTextureW(), mFrontTextureFilter.getTextureH());
        mDisplayFilter.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        int front = mFrontTextureFilter.onDrawBuffer(0);

        int back = mBackTextureFilter.onDrawBuffer(0);

        if (mStartTime == 0)
        {
            mStartTime = System.currentTimeMillis();
        }

        GLES20.glClearColor(1, 1, 1, 1);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        mSquareAnimTransitionFilter.setStartTimeValue(mStartTime);
        mSquareAnimTransitionFilter.setTimeValue(System.currentTimeMillis());
        int id = mSquareAnimTransitionFilter.onDrawBuffer(0, front, back);

        mDisplayFilter.onDrawFrame(id);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (mFrontTextureFilter != null)
        {
            mFrontTextureFilter.destroy();
        }

        if (mBackTextureFilter != null)
        {
            mBackTextureFilter.destroy();
        }

        if (mSquareAnimTransitionFilter != null)
        {
            mSquareAnimTransitionFilter.destroy();
        }

        if (mDisplayFilter != null)
        {
            mDisplayFilter.destroy();
        }
    }
}
