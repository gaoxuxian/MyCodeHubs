package trunk.gles.view;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import lib.gl.filter.common.BmpToTextureFilter;
import lib.gl.filter.innovation.GhostingFilter;
import trunk.R;
import lib.gl.util.GLUtil;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/29.
 */
public class GhostingView extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private BmpToTextureFilter mBmpToTextureFilter;
    private GhostingFilter mGhostingFilter;

    private long mStartTime;

    public GhostingView(Context context)
    {
        super(context);

        setEGLContextClientVersion(GLUtil.getGlSupportVersionInt(context));
        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        mBmpToTextureFilter = new BmpToTextureFilter(getContext());
        mBmpToTextureFilter.onSurfaceCreated(config);

        mGhostingFilter = new GhostingFilter(getContext());
        mGhostingFilter.onSurfaceCreated(config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        mBmpToTextureFilter.onSurfaceChanged(width, height);
        mGhostingFilter.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        mBmpToTextureFilter.setBitmapRes(R.drawable.open_test_7);
        mBmpToTextureFilter.initFrameBufferOfTextureSize();
        int textureID = mBmpToTextureFilter.onDrawBuffer(0);

        if (mStartTime == 0)
        {
            mStartTime = System.currentTimeMillis();
        }

        GLUtil.checkGlError("SAAA");
        mGhostingFilter.setTextureWH(mBmpToTextureFilter.getTextureW(), mBmpToTextureFilter.getTextureH());
        mGhostingFilter.setStartTimeValue(mStartTime);
        mGhostingFilter.setTimeValue(System.currentTimeMillis());
        mGhostingFilter.onDrawFrame(textureID);
        GLUtil.checkGlError("SAAA");
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (mBmpToTextureFilter != null)
        {
            mBmpToTextureFilter.destroy();
        }

        if (mGhostingFilter != null)
        {
            mGhostingFilter.destroy();
        }
    }
}
