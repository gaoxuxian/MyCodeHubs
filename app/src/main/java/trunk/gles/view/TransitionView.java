package trunk.gles.view;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import lib.gl.filter.GPUImageTransitionFilter;
import lib.gl.filter.GPUTransitionFilterType;
import lib.gl.filter.common.BmpToTextureFilter;
import trunk.R;
import lib.gl.util.FilterFactory;
import lib.gl.util.GLUtil;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/2.
 */
public class TransitionView extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private final Object LOCK;
    private static final float CUT_PICTURE_TIME = 2.5f;

    private volatile float mRatio = 16f/9f;
    private int[] m11BmpRes;
    private int[] m169BmpRes;
    private volatile int[] mFrontBackBmpRes;

    private BmpToTextureFilter mTextureFilter;
    private volatile GPUImageTransitionFilter mTransitionFilter;
    private volatile GPUImageTransitionFilter mTempFilter;

    private long mStartTime;
    private long mFilterStartTime;
    private int mLastFloor;

    private int mSurfaceW, mSurfaceH;

    private volatile Runnable mSetFilterRunnable;
    private volatile Runnable mChangeRatioRunnable;

    public TransitionView(Context context)
    {
        super(context);

        LOCK = new Object();

        mSetFilterRunnable = () ->
        {
            synchronized (LOCK)
            {
                if (mTempFilter != null)
                {
                    if (mTransitionFilter != null)
                    {
                        mTransitionFilter.destroy();
                    }

                    mTempFilter.onSurfaceCreated(null);
                    mTempFilter.onSurfaceChanged(mSurfaceW, mSurfaceH);

                    if (mTextureFilter != null)
                    {
                        mTempFilter.setTextureWH(mTextureFilter.getTextureW(), mTextureFilter.getTextureH());
                    }
                    mTransitionFilter = mTempFilter;
                    mTempFilter = null;
                }
            }
        };

        mChangeRatioRunnable = () ->
        {
            synchronized (LOCK)
            {
                if (mRatio == 1f)
                {
                    mRatio = 16f/9f;
                }
                else if (mRatio == 16f/9f)
                {
                    mRatio = 1f;
                }
            }
        };

        m11BmpRes = new int[]{R.drawable.open_test, R.drawable.open_test_2, R.drawable.open_test_3, R.drawable.open_test_4,
                                R.drawable.open_test_5, };

        m169BmpRes = new int[]{R.drawable.open_test_9, R.drawable.open_test_10, R.drawable.open_test_11, R.drawable.open_test_12,
                                R.drawable.open_test_13, R.drawable.open_test_14};

        mFrontBackBmpRes = new int[2];

        setEGLContextClientVersion(GLUtil.getGlSupportVersionInt(context));
        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        mTextureFilter = new BmpToTextureFilter(getContext());
        mTextureFilter.onSurfaceCreated(config);

        mTransitionFilter = new GPUImageTransitionFilter(getContext());
        mTransitionFilter.onSurfaceCreated(config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        mSurfaceW = width;
        mSurfaceH = height;

        mTextureFilter.onSurfaceChanged(width, height);
        mTransitionFilter.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        synchronized (LOCK)
        {
            if (mStartTime == 0)
            {
                mStartTime = System.currentTimeMillis();
                mFilterStartTime = mStartTime;
            }

            float dt = (System.currentTimeMillis() - mStartTime) / 1000f;

            if (mRatio == 1f)
            {
                int floor = (int) (dt / CUT_PICTURE_TIME) % m11BmpRes.length;
                int ceil = (floor + 1) % m11BmpRes.length;
                if (mLastFloor != floor)
                {
                    mLastFloor = floor;
                    mFilterStartTime = System.currentTimeMillis();
                }
                mFrontBackBmpRes[0] = m11BmpRes[floor];
                mFrontBackBmpRes[1] = m11BmpRes[ceil];
            }
            else if (mRatio == 16f/9f)
            {
                int floor = (int) (dt / CUT_PICTURE_TIME) % m169BmpRes.length;
                int ceil = (floor + 1) % m169BmpRes.length;
                if (mLastFloor != floor)
                {
                    mLastFloor = floor;
                    mFilterStartTime = System.currentTimeMillis();
                }
                mFrontBackBmpRes[0] = m169BmpRes[floor];
                mFrontBackBmpRes[1] = m169BmpRes[ceil];
            }

            mTextureFilter.setBitmapRes(mFrontBackBmpRes[0]);
            mTextureFilter.initFrameBufferOfTextureSize();
            int front = mTextureFilter.onDrawBuffer(0);

            mTextureFilter.setBitmapRes(mFrontBackBmpRes[1]);
            int back = mTextureFilter.onDrawBuffer(0);

            GLES20.glClearColor(.96f, .96f, .96f, 1);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            mTransitionFilter.setStartTimeValue(mFilterStartTime);
            mTransitionFilter.setTextureWH(mTextureFilter.getTextureW(), mTextureFilter.getTextureH());
            mTransitionFilter.setTimeValue(System.currentTimeMillis());
            mTransitionFilter.onDrawFrame(front, back);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (mTextureFilter != null)
        {
            mTextureFilter.destroy();
        }

        if (mTransitionFilter != null)
        {
            mTransitionFilter.destroy();
        }

        if (mTempFilter != null)
        {
            mTempFilter.destroy();
            mTempFilter = null;
        }
    }

    public void setTransitionFilter(GPUTransitionFilterType type)
    {
        GPUImageTransitionFilter filter = FilterFactory.createTransitionFilter(getContext(), type);

        if (filter != null)
        {
            mTempFilter = filter;
            this.queueEvent(mSetFilterRunnable);
        }
    }

    public void changePreviewBmpRatio()
    {
        this.queueEvent(mChangeRatioRunnable);
    }
}
