package com.xx.avlibrary.gl.filter.innovation;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import com.xx.avlibrary.gl.util.ByteBufferUtil;
import com.xx.avlibrary.gl.util.GLUtil;
import com.xx.avlibrary.gl.util.GlMatrixTools;
import com.xx.avlibrary.R;
import com.xx.avlibrary.gl.filter.GLConstant;
import com.xx.avlibrary.gl.filter.GPUFilterType;
import com.xx.avlibrary.gl.filter.GPUImageFilter;
import com.xx.avlibrary.gl.task.SplitScreenTask;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/24.
 */
public class SplitScreenFilterV1 extends GPUImageFilter
{
    private int vFactorHandle;
    private int vOffsetHandle;
    private int vFlipHandle;
    private int vFuzzyRangHandle;

    private float mOffsetValue;
    private SplitScreenTask mTask;
    private int[] mTextureIDs;

    public SplitScreenFilterV1(Context context)
    {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default), GLUtil.readShaderFromRaw(context, R.raw.fragment_oblique_split_screen));
    }

    public void setTextureRes(Object res)
    {
        if (mTask != null)
        {
            mTextureW = 0;
            mTextureH = 0;

            mTask.setBitmapRes(res);
            queueRunnable(mTask);
            runTask(true); // 卡线程
        }
    }

    /**
     * 绘制前需要输入时间
     */
    public void setTime(long start, long current)
    {
        float percent = (current - start) / 2000f;
        if (percent < 1)
        {
            mOffsetValue = -percent * 4f + 2;
        }
        else
        {
            mOffsetValue = -2f;
        }
    }

    @Override
    public GPUFilterType getFilterType()
    {
        return GPUFilterType.OBLIQUE_PLUS;
    }

    @Override
    protected void onInitBaseData()
    {
        super.onInitBaseData();

        mTextureIDs = new int[1];

        mTask = new SplitScreenTask(getContext(), new SplitScreenTask.Listener()
        {
            @Override
            public void onBitmapSucceed(Bitmap bitmap)
            {
                if (mTextureW != bitmap.getWidth() || mTextureH != bitmap.getHeight())
                {
                    mTextureW = bitmap.getWidth();
                    mTextureH = bitmap.getHeight();

                    if (GLES30.glIsTexture(mTextureIDs[0]))
                    {
                        GLES30.glDeleteTextures(1, mTextureIDs, 0);
                        mTextureIDs[0] = 0;
                    }

                    GLES30.glGenTextures(1, mTextureIDs, 0);
                    GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureIDs[0]);
                    GLUtil.bindTexture2DParams();
                    GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
                    GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
                }
                else if (!GLES30.glIsTexture(mTextureIDs[0]))
                {
                    GLES30.glGenTextures(1, mTextureIDs, 0);
                    GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureIDs[0]);
                    GLUtil.bindTexture2DParams();
                    GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
                    GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
                }
                else
                {
                    GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureIDs[0]);
                    GLUtil.bindTexture2DParams();
                    GLUtils.texSubImage2D(GLES30.GL_TEXTURE_2D, 0, 0, 0, bitmap);
                    GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
                }
            }
        });
    }

    @Override
    protected void onInitBufferData()
    {
        mVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.VERTEX_SQUARE);
        mVertexIndexBuffer = ByteBufferUtil.getNativeShortBuffer(GLConstant.VERTEX_INDEX);
        mTextureIndexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.TEXTURE_INDEX_V2);
    }

    @Override
    protected void onInitProgramHandle()
    {
        super.onInitProgramHandle();
        vFactorHandle = GLES30.glGetUniformLocation(getProgram(), "vFactor");
        vOffsetHandle = GLES30.glGetUniformLocation(getProgram(), "vOffset");
        vFlipHandle = GLES30.glGetUniformLocation(getProgram(), "vFlip");
        vFuzzyRangHandle = GLES30.glGetUniformLocation(getProgram(), "vFuzzyRang");
    }

    @Override
    protected int createFrameBufferSize()
    {
        return 1;
    }

    @Override
    protected boolean needInitMsaaFbo()
    {
        return false;
    }

    @Override
    protected void preDrawSteps3Matrix(boolean drawBuffer)
    {
        if (!isViewPortAvailable(drawBuffer)) return;

        // 视口区域大小(归一化映射范围)
        GLES20.glViewport(0, 0, drawBuffer ? getFrameBufferW() : getSurfaceW(), drawBuffer ? getFrameBufferH() : getSurfaceH());
        // 矩阵变换
        GlMatrixTools matrix = getMatrix();
        matrix.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
        matrix.frustum(-1, 1, -1, 1, 3, 7);

        matrix.pushMatrix();
        matrix.scale(1f, -1f, 1f);
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer)
    {
        if (mTextureH != 0 || mTextureW != 0)
        {
            GLES30.glUniform1f(vFactorHandle, 1);
            GLES30.glUniform1f(vFlipHandle, 1);
            GLES30.glUniform1f(vOffsetHandle, mOffsetValue);
            // GLES30.glUniform1f(vOffsetHandle, 0.2f);
            GLES30.glUniform1f(vFuzzyRangHandle, 0.22f);
        }
    }

    @Override
    protected void afterDraw()
    {
        super.afterDraw();
    }

    @Override
    public int onDrawBuffer(int textureID)
    {
        if (!GLES20.glIsProgram(getProgram()))
        {
            return textureID;
        }

        if (mFrameBufferMgr != null)
        {
            mFrameBufferMgr.bindNext();
            mFrameBufferMgr.clearColor(true, true, true, true, true);
            mFrameBufferMgr.clearDepth(true, true);
            mFrameBufferMgr.clearStencil(true, true);

            draw(mTextureIDs[0], true);
            mFrameBufferMgr.unbind();
            return mFrameBufferMgr.getCurrentTextureId();
        }

        return textureID;
    }

    @Override
    public void onDrawFrame(int textureID)
    {
        if (!GLES20.glIsProgram(getProgram()))
        {
            return;
        }

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
        GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);

        draw(mTextureIDs[0], false);

        GLES20.glDisable(GLES20.GL_BLEND);
    }

    @Override
    public void destroy()
    {
        super.destroy();

        if (mTask != null)
        {
            mTask.destroy();
        }
    }
}
