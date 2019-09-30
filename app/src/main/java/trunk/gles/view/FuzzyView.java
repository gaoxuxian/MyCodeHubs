package trunk.gles.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import com.xx.avlibrary.gl.filter.common.GaussianBlurFilter;
import com.xx.avlibrary.gl.filter.rhythm.DisplayFilter;
import com.xx.avlibrary.gl.util.GLUtil;
import com.xx.avlibrary.gl.filter.common.BmpToTextureFilter;
import com.xx.avlibrary.gl.filter.innovation.FuzzyFilter;
import trunk.R;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/29.
 */
public class FuzzyView extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private BmpToTextureFilter mBmpToTextureFilter;
//    private FuzzyFilter mFuzzyFilter;
    private GaussianBlurFilter mGaussianFilter;
    private DisplayFilter mDisplayFilter;

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

//        mFuzzyFilter = new FuzzyFilter(getContext());
//        mFuzzyFilter.onSurfaceCreated(config);

        mGaussianFilter = new GaussianBlurFilter(getContext());
        mGaussianFilter.onSurfaceCreated(config);

        mDisplayFilter = new DisplayFilter(getContext());
        mDisplayFilter.onSurfaceCreated(config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        mBmpToTextureFilter.onSurfaceChanged(width, height);
//        mFuzzyFilter.onSurfaceChanged(width, height);

        mGaussianFilter.onSurfaceChanged(width, height);
        mDisplayFilter.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        mBmpToTextureFilter.setBitmapRes(R.drawable.open_test_7);
        mBmpToTextureFilter.initFrameBufferOfTextureSize();
        int textureID = mBmpToTextureFilter.onDrawBuffer(0);

        long start = System.currentTimeMillis();
        mGaussianFilter.setTextureWH(mBmpToTextureFilter.getFrameBufferW(), mBmpToTextureFilter.getFrameBufferH());
        mGaussianFilter.initFrameBufferOfTextureSize();
        mGaussianFilter.setBlurParams(2, 1.2f);
        textureID = mGaussianFilter.onDrawBuffer(textureID);

        Log.e("***", "onDrawFrame: 第一次高斯模糊耗时：" + (System.currentTimeMillis() - start));

        mDisplayFilter.setTextureWH(mGaussianFilter.getTextureW(), mGaussianFilter.getTextureH());
        mDisplayFilter.onDrawFrame(textureID);

//        mFuzzyFilter.onDrawFrame(textureID);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (mBmpToTextureFilter != null)
        {
            mBmpToTextureFilter.destroy();
        }

//        if (mFuzzyFilter != null)
//        {
//            mFuzzyFilter.destroy();
//        }
    }
}
