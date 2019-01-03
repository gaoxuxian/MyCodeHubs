package trunk.gles.view;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import filter.common.BmpToTextureFilter;
import filter.transitions.SmoothnessTransitionFilter;
import trunk.R;
import util.GLUtil;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/2.
 */
public class SmoothnessTransitionView extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private BmpToTextureFilter mFrontTextureFilter;
    private BmpToTextureFilter mBackTextureFilter;
    private SmoothnessTransitionFilter mSmoothnessFilter;

    private long mStartTime;

    public SmoothnessTransitionView(Context context)
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

        mSmoothnessFilter = new SmoothnessTransitionFilter(getContext());
        mSmoothnessFilter.onSurfaceCreated(config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        mFrontTextureFilter.onSurfaceChanged(width, height);
        mFrontTextureFilter.setBitmapRes(R.drawable.open_test_2);
        mFrontTextureFilter.initFrameBufferOfTextureSize();

        mBackTextureFilter.onSurfaceChanged(width, height);
        mBackTextureFilter.setBitmapRes(R.drawable.open_test_3);
        mBackTextureFilter.initFrameBufferOfTextureSize();

        mSmoothnessFilter.setTextureWH(mFrontTextureFilter.getTextureW(), mFrontTextureFilter.getTextureH());
        mSmoothnessFilter.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        if (mStartTime == 0)
        {
            mStartTime = System.currentTimeMillis();
        }

        int front = mFrontTextureFilter.onDrawBuffer(0);

        int back = mBackTextureFilter.onDrawBuffer(0);

        mSmoothnessFilter.setStartTimeValue(mStartTime);
        mSmoothnessFilter.setTimeValue(System.currentTimeMillis());
        mSmoothnessFilter.onDrawFrame(front, back);
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

        if (mSmoothnessFilter != null)
        {
            mSmoothnessFilter.destroy();
        }
    }
}
