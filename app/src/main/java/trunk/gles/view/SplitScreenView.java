package trunk.gles.view;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import filter.common.BmpToTextureFilter;
import filter.common.DisplayImageFilter;
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
        mBmpToTextureFilter.onSurfaceChanged(width, height);
        mBmpToTextureFilter.setBitmapRes(R.drawable.open_test_2);
        mBmpToTextureFilter.initFrameBufferOfTextureSize();

        mSplitScreenFilter.onSurfaceChanged(width, height);
        mSplitScreenFilter.setTextureRes(R.drawable.open_test_5);
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

        int bgID = mBmpToTextureFilter.onDrawBuffer(0);

        mDisplayFilter.onDrawFrame(bgID);

        mSplitScreenFilter.setTime(mStartTime, System.currentTimeMillis());
        mSplitScreenFilter.onDrawFrame(0);
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
