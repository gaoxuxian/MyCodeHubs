package trunk.gles.view;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import filter.BmpToTextureFilter;
import filter.innovation.FuzzyFilter;
import trunk.R;
import util.GLUtil;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/29.
 */
public class FuzzyView extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private BmpToTextureFilter mBmpToTextureFilter;
    private FuzzyFilter mFuzzyFilter;

    public FuzzyView(Context context)
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

        mFuzzyFilter = new FuzzyFilter(getContext());
        GLUtil.checkGlError("AAA");
        mFuzzyFilter.onSurfaceCreated(config);
        GLUtil.checkGlError("AAA");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        mBmpToTextureFilter.onSurfaceChanged(width, height);
        mFuzzyFilter.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        mBmpToTextureFilter.setBitmapRes(R.drawable.open_test_5);
        mBmpToTextureFilter.initFrameBufferOfTextureSize();
        int textureID = mBmpToTextureFilter.onDrawBuffer(0);

        mFuzzyFilter.onDrawFrame(textureID);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (mBmpToTextureFilter != null)
        {
            mBmpToTextureFilter.destroy();
        }

        if (mFuzzyFilter != null)
        {
            mFuzzyFilter.destroy();
        }
    }
}
