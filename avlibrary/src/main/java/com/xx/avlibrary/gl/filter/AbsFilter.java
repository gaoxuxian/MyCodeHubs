package com.xx.avlibrary.gl.filter;

import android.content.Context;
import android.opengl.GLES20;
import com.xx.avlibrary.gl.util.AbsTask;
import com.xx.avlibrary.gl.util.GLUtil;
import com.xx.avlibrary.gl.util.GlMatrixTools;
import com.xx.avlibrary.gl.util.TaskWrapper;
import com.xx.avlibrary.gl.fbo.AbsFboMgr;
import com.xx.avlibrary.gl.fbo.FrameBufferMgr;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/2.
 */
public abstract class AbsFilter<Y extends FilterType> implements FilterIF<Y>
{
    private Context mContext;
    private final String mVertexStr;
    private final String mFragmentStr;
    private final GlMatrixTools mMatrixTools;
    private TaskWrapper mTasksMgr;

    private int mProgram;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    protected int mTextureW;
    protected int mTextureH;
    protected AbsFboMgr mFrameBufferMgr;

    private int mVertexShader;
    private int mFragmentShader;

    private final Object mLockObj;

    public AbsFilter(Context context, String vertex, String fragment)
    {
        if (!GLUtil.checkSupportGlVersion(context, 2.0f))
        {
            throw new RuntimeException("手机系统所支持的 Open GL ES 版本低于 2.0, Filter 创建失败!!!");
        }

        mContext = context;
        mVertexStr = vertex;
        mFragmentStr = fragment;
        mMatrixTools = new GlMatrixTools();
        mLockObj = new Object();

        if (onInitTaskMgr())
        {
            mTasksMgr = new TaskWrapper();
        }
        onInitBaseData();
    }

    protected boolean onInitTaskMgr()
    {
        return true;
    }

    protected void onInitBaseData()
    {

    }

    protected abstract void onInitBufferData();

    protected abstract void onInitProgramHandle();

    public Context getContext()
    {
        return mContext;
    }

    public GlMatrixTools getMatrix()
    {
        return mMatrixTools;
    }

    protected int getProgram()
    {
        return mProgram;
    }

    public int getSurfaceW()
    {
        return mSurfaceWidth;
    }

    public int getSurfaceH()
    {
        return mSurfaceHeight;
    }

    public int getFrameBufferW()
    {
        return mFrameBufferMgr != null ? mFrameBufferMgr.getBufferWidth() : 0;
    }

    public int getFrameBufferH()
    {
        return mFrameBufferMgr != null ? mFrameBufferMgr.getBufferHeight() : 0;
    }

    @Override
    public void onSurfaceCreated(EGLConfig config)
    {
        if (GLES20.glIsProgram(mProgram))
        {
            if (GLES20.glIsShader(mVertexShader))
            {
                GLES20.glDetachShader(mProgram, mVertexShader);
                GLES20.glDeleteShader(mVertexShader);
                mVertexShader = 0;
            }

            if (GLES20.glIsShader(mFragmentShader))
            {
                GLES20.glDetachShader(mProgram, mFragmentShader);
                GLES20.glDeleteShader(mFragmentShader);
                mFragmentShader = 0;
            }
            GLES20.glDeleteProgram(mProgram);
            mProgram = 0;
        }

        onInitBufferData();

        mVertexShader = GLUtil.createShader(GLES20.GL_VERTEX_SHADER, mVertexStr);
        mFragmentShader = GLUtil.createShader(GLES20.GL_FRAGMENT_SHADER, mFragmentStr);
        mProgram = GLUtil.createAndLinkProgram(mVertexShader, mFragmentShader);
        onInitProgramHandle();
    }

    @Override
    public void onSurfaceChanged(int width, int height)
    {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
    }

    public void setTextureWH(int width, int height)
    {
        mTextureW = width;
        mTextureH = height;
    }

    public int getTextureW()
    {
        return mTextureW;
    }

    public int getTextureH()
    {
        return mTextureH;
    }

    public final void initFrameBufferOfTextureSize()
    {
        initFrameBuffer(mTextureW, mTextureH);
    }

    @Override
    public void initFrameBuffer(int width, int height)
    {
        this.initFrameBuffer(width, height, true, true, true);
    }

    /**
     * 3.0 可选择生成 抗锯齿fbo 或者 普通fbo, 2.0 只生成 普通fbo
     * <p>
     * needInitMsaaFbo() true --> 抗锯齿 render buffer
     * <p>
     * needInitMsaaFbo() false --> 普通 2d 纹理, 本身不能抗锯齿
     * @param width
     * @param height
     */
    protected final void initFrameBuffer(int width, int height, boolean color, boolean depth, boolean stencil)
    {
        if (width == 0 || height == 0)
        {
            if (mFrameBufferMgr != null)
            {
                mFrameBufferMgr.destroy();
                mFrameBufferMgr = null;
            }
        }
        else if (mFrameBufferMgr != null)
        {
            if (width != mFrameBufferMgr.getBufferWidth() || height != mFrameBufferMgr.getBufferHeight())
            {
                checkFrameBufferReMount(width, height);
            }
        }
        else
        {
            mFrameBufferMgr = new FrameBufferMgr(getContext(), width, height, createFrameBufferSize(), color, depth, stencil, needInitMsaaFbo());
        }
    }

    protected boolean needInitMsaaFbo()
    {
        return true;
    }

    protected int createFrameBufferSize()
    {
        return 1;
    }

    /**
     * 手动检查当前 FrameBuffer 里的缓冲区是否需要重新挂载
     * @param width
     * @param height
     */
    private void checkFrameBufferReMount(int width, int height)
    {
        if (mFrameBufferMgr != null)
        {
            mFrameBufferMgr.reMount(width, height);
        }
    }

    public void blendEnable(boolean enable)
    {
        if (enable)
        {
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
            GLES20.glBlendFuncSeparate(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);
        }
        else
        {
            GLES20.glDisable(GLES20.GL_BLEND);
        }
    }

    protected void queueRunnable(final AbsTask runnable)
    {
        synchronized (mLockObj)
        {
            mTasksMgr.queueRunnable(runnable);
        }
    }

    /**
     * 同步task
     *
     * @param runAll 是否将队列内任务全部执行
     */
    protected void runTask(boolean runAll)
    {
        if (mTasksMgr != null && !mTasksMgr.isClear())
        {
            if (runAll)
            {
                while (!mTasksMgr.isClear() && mTasksMgr.getTaskSize() != 0)
                {
                    mTasksMgr.runTask();
                }
            }
            else
            {
                mTasksMgr.runTask();
            }
        }
    }

    /**
     * 异步task
     */
    protected void startTask()
    {
        if (mTasksMgr != null && !mTasksMgr.isClear())
        {
            mTasksMgr.startTask();
        }
    }

    public void destroy()
    {
        mContext = null;

        if (GLES20.glIsProgram(mProgram))
        {
            if (GLES20.glIsShader(mVertexShader))
            {
                GLES20.glDetachShader(mProgram, mVertexShader);
                GLES20.glDeleteShader(mVertexShader);
                mVertexShader = 0;
            }

            if (GLES20.glIsShader(mFragmentShader))
            {
                GLES20.glDetachShader(mProgram, mFragmentShader);
                GLES20.glDeleteShader(mFragmentShader);
                mFragmentShader = 0;
            }

            GLES20.glDeleteProgram(mProgram);
            mProgram = 0;
        }

        if (mFrameBufferMgr != null)
        {
            mFrameBufferMgr.destroy();
            mFrameBufferMgr = null;
        }

        if (mTasksMgr != null)
        {
            mTasksMgr.clearTask();
        }
    }
}
