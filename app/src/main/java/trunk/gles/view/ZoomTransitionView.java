package trunk.gles.view;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import filter.BmpToTextureFilter;
import filter.transitions.ZoomTransitionFilter;
import trunk.R;
import util.GLUtil;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/2.
 */
public class ZoomTransitionView extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private BmpToTextureFilter mFrontTextureFilter;
    private BmpToTextureFilter mBackTextureFilter;
    private ZoomTransitionFilter mZoomTransitionFilter;

    public ZoomTransitionView(Context context)
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

        mZoomTransitionFilter = new ZoomTransitionFilter(getContext());
        mZoomTransitionFilter.onSurfaceCreated(config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        mFrontTextureFilter.onSurfaceChanged(width, height);
        mBackTextureFilter.onSurfaceChanged(width, height);
        mZoomTransitionFilter.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        mFrontTextureFilter.setBitmapRes(R.drawable.open_test);
        mFrontTextureFilter.initFrameBuffer(0, 0);
        int front = mFrontTextureFilter.onDrawBuffer(0);

        mBackTextureFilter.setBitmapRes(R.drawable.open_test_3);
        mBackTextureFilter.initFrameBuffer(0, 0);
        int back = mBackTextureFilter.onDrawBuffer(0);

        mZoomTransitionFilter.setTextureID(front, back, mFrontTextureFilter.getTextureW(), mFrontTextureFilter.getTextureH());
        mZoomTransitionFilter.onDrawFrame(0);
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

        if (mZoomTransitionFilter != null)
        {
            mZoomTransitionFilter.destroy();
        }
    }
}
