package trunk.gles.view;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import filter.common.BmpToTextureFilter;
import filter.transitions.SpreadRoundTransitionFilter;
import trunk.R;
import util.GLUtil;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/2.
 */
public class SpreadRoundTransitionView extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private BmpToTextureFilter mFrontTextureFilter;
    private BmpToTextureFilter mBackTextureFilter;
    private SpreadRoundTransitionFilter mSpreadRoundTransitionFilter;

    private long mStartTime;

    public SpreadRoundTransitionView(Context context)
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

        mSpreadRoundTransitionFilter = new SpreadRoundTransitionFilter(getContext());
        mSpreadRoundTransitionFilter.onSurfaceCreated(config);
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

        mSpreadRoundTransitionFilter.setTextureWH(mFrontTextureFilter.getTextureW(), mFrontTextureFilter.getTextureH());
        mSpreadRoundTransitionFilter.onSurfaceChanged(width, height);
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

        mSpreadRoundTransitionFilter.setStartTimeValue(mStartTime);
        mSpreadRoundTransitionFilter.setTimeValue(System.currentTimeMillis());
        mSpreadRoundTransitionFilter.onDrawFrame(front, back);
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

        if (mSpreadRoundTransitionFilter != null)
        {
            mSpreadRoundTransitionFilter.destroy();
        }
    }
}
