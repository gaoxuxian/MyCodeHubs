package trunk.gles.view;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import filter.common.BmpToTextureFilter;
import filter.transitions.ColorGhostingTransitionFilter;
import filter.transitions.ZoomTransitionFilter;
import trunk.R;
import util.GLUtil;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/2.
 */
public class ColorGhostingTransitionView extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private BmpToTextureFilter mFrontTextureFilter;
    private BmpToTextureFilter mBackTextureFilter;
    private ColorGhostingTransitionFilter mColorGhostingTransitionFilter;

    private long mStartTime;

    public ColorGhostingTransitionView(Context context)
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

        mColorGhostingTransitionFilter = new ColorGhostingTransitionFilter(getContext());
        mColorGhostingTransitionFilter.onSurfaceCreated(config);
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

        mColorGhostingTransitionFilter.setTextureWH(mFrontTextureFilter.getTextureW(), mFrontTextureFilter.getTextureH());
        mColorGhostingTransitionFilter.onSurfaceChanged(width, height);
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

        mColorGhostingTransitionFilter.setStartTimeValue(mStartTime);
        mColorGhostingTransitionFilter.setTimeValue(System.currentTimeMillis());
        mColorGhostingTransitionFilter.onDrawFrame(front, back);
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

        if (mColorGhostingTransitionFilter != null)
        {
            mColorGhostingTransitionFilter.destroy();
        }
    }
}
