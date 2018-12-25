package trunk.gles.view;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import filter.BmpToTextureFilter;
import filter.innovation.SplitScreenFilterV2;
import trunk.R;
import util.GLUtil;

/**
 * @author Gxx 四角分屏
 * Created by Gxx on 2018/12/21.
 */
public class SplitScreenViewV2 extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private BmpToTextureFilter mBmpToTextureFilter;
    private BmpToTextureFilter mBmpToTextureFilter2;
    private BmpToTextureFilter mBmpToTextureFilter3;
    private BmpToTextureFilter mBmpToTextureFilter4;
    private BmpToTextureFilter mBmpToTextureFilter5;
    private SplitScreenFilterV2 mSplitScreenFilter;

    private int[] mBmpResArr;
    private int[] mTextureIDArr;

    public SplitScreenViewV2(Context context)
    {
        super(context);

        mBmpResArr = new int[]{R.drawable.open_test, R.drawable.open_test_2, R.drawable.open_test_3, R.drawable.open_test_4, R.drawable.open_test_5};
        mTextureIDArr = new int[mBmpResArr.length];

        setEGLContextClientVersion(GLUtil.getGlSupportVersionInt(context));
        setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        requestRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        mBmpToTextureFilter = new BmpToTextureFilter(getContext());
        mBmpToTextureFilter2 = new BmpToTextureFilter(getContext());
        mBmpToTextureFilter3 = new BmpToTextureFilter(getContext());
        mBmpToTextureFilter4 = new BmpToTextureFilter(getContext());
        mBmpToTextureFilter5 = new BmpToTextureFilter(getContext());

        mSplitScreenFilter = new SplitScreenFilterV2(getContext());

        mBmpToTextureFilter.onSurfaceCreated(config);
        mBmpToTextureFilter2.onSurfaceCreated(config);
        mBmpToTextureFilter3.onSurfaceCreated(config);
        mBmpToTextureFilter4.onSurfaceCreated(config);
        mBmpToTextureFilter5.onSurfaceCreated(config);
        mSplitScreenFilter.onSurfaceCreated(config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        mBmpToTextureFilter.onSurfaceChanged(width, height);
        mBmpToTextureFilter.setBitmapRes(mBmpResArr[0]);
        mBmpToTextureFilter.initFrameBuffer(width, height);

        mBmpToTextureFilter2.onSurfaceChanged(width, height);
        mBmpToTextureFilter2.setBitmapRes(mBmpResArr[1]);
        mBmpToTextureFilter2.initFrameBuffer(width, height);

        mBmpToTextureFilter3.onSurfaceChanged(width, height);
        mBmpToTextureFilter3.setBitmapRes(mBmpResArr[2]);
        mBmpToTextureFilter3.initFrameBuffer(width, height);

        mBmpToTextureFilter4.onSurfaceChanged(width, height);
        mBmpToTextureFilter4.setBitmapRes(mBmpResArr[3]);
        mBmpToTextureFilter4.initFrameBuffer(width, height);

        mBmpToTextureFilter5.onSurfaceChanged(width, height);
        mBmpToTextureFilter5.setBitmapRes(mBmpResArr[4]);
        mBmpToTextureFilter5.initFrameBuffer(width, height);

        mSplitScreenFilter.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {

        int id = mBmpToTextureFilter.onDrawBuffer(0);
        mTextureIDArr[0] = id;

        id = mBmpToTextureFilter2.onDrawBuffer(0);
        mTextureIDArr[1] = id;

        id = mBmpToTextureFilter3.onDrawBuffer(0);
        mTextureIDArr[2] = id;

        id = mBmpToTextureFilter4.onDrawBuffer(0);
        mTextureIDArr[3] = id;

        id = mBmpToTextureFilter5.onDrawBuffer(0);
        mTextureIDArr[4] = id;

        mSplitScreenFilter.setTextures(mTextureIDArr);
        mSplitScreenFilter.onDrawFrame(0);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (mBmpToTextureFilter != null)
        {
            mBmpToTextureFilter.destroy();
        }

        if (mBmpToTextureFilter2 != null)
        {
            mBmpToTextureFilter2.destroy();
        }

        if (mBmpToTextureFilter3 != null)
        {
            mBmpToTextureFilter3.destroy();
        }

        if (mBmpToTextureFilter4 != null)
        {
            mBmpToTextureFilter4.destroy();
        }

        if (mBmpToTextureFilter5 != null)
        {
            mBmpToTextureFilter5.destroy();
        }

        if (mSplitScreenFilter != null)
        {
            mSplitScreenFilter.destroy();
        }

    }
}
