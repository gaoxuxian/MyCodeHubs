package trunk.gles.view;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import filter.BmpToTextureFilter;
import filter.DisplayImageFilter;
import filter.innovation.SplitScreenFilterV1;
import trunk.R;
import util.GLUtil;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/21.
 */
public class SplitScreenView extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private BmpToTextureFilter mBmpToTextureFilter;
    private SplitScreenFilterV1 mSplitScreenFilter;
    private DisplayImageFilter mDisplayFilter;
    private long mStartTime;

    public SplitScreenView(Context context)
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
        mSplitScreenFilter = new SplitScreenFilterV1(getContext());
        mBmpToTextureFilter = new BmpToTextureFilter(getContext());
        mDisplayFilter = new DisplayImageFilter(getContext());

        mSplitScreenFilter.onSurfaceCreated(config);
        mBmpToTextureFilter.onSurfaceCreated(config);
        mDisplayFilter.onSurfaceCreated(config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        mSplitScreenFilter.onSurfaceChanged(width, height);

        mBmpToTextureFilter.onSurfaceChanged(width, height);
        mDisplayFilter.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        if (mStartTime == 0)
        {
            mStartTime = System.currentTimeMillis();
        }

        mBmpToTextureFilter.setBitmapRes(R.drawable.open_test_2);
        mBmpToTextureFilter.initFrameBuffer(0, 0);
        int bgID = mBmpToTextureFilter.onDrawBuffer(0);

        mDisplayFilter.onDrawFrame(bgID);

        mSplitScreenFilter.setTextureRes(R.drawable.open_test_5);
        mSplitScreenFilter.initFrameBuffer(0, 0);
        mSplitScreenFilter.setTime(mStartTime, System.currentTimeMillis());
        mSplitScreenFilter.onDrawFrame(0);

        // mBmpToTextureFilter.setBitmapRes(R.drawable.open_test_5);
        // int frontID = mBmpToTextureFilter.onDrawBuffer(0);

        // mSplitScreenFilter.setTextureRes(R.drawable.open_test_5);
        // mSplitScreenFilter.initFrameBuffer(0, 0);
        // mSplitScreenFilter.setTime(mStartTime, System.currentTimeMillis());
        // int displayID = mSplitScreenFilter.onDrawBuffer(0);

        // GLES20.glEnable(GLES20.GL_BLEND);
        // GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
        // GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);
        //
        // mDisplayFilter.onDrawFrame(displayID);
        //
        // GLES20.glDisable(GLES20.GL_BLEND);
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

        if (mBmpToTextureFilter != null)
        {
            mBmpToTextureFilter.destroy();
            mBmpToTextureFilter = null;
        }
    }
}
