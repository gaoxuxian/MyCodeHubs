package filter;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import library.R;
import util.ByteBufferUtil;
import util.GLUtil;
import util.GlMatrixTools;

/**
 * 单纹理类 filter 基类
 * @author Gxx
 * Created by Gxx on 2018/12/05.
 * <p>
 * 生命周期: onSurfaceCreated() --> onSurfaceChanged()
 * 如需管理 FBO : initFrameBuffer()
 */
public abstract class GPUImageAnimFilter extends AbsFilter<GPUAnimFilterType>
{
    // 句柄
    protected int vPositionHandle;
    protected int vCoordinateHandle;
    protected int vMatrixHandle;
    protected int vTextureHandle;
    private int iTimeHandle;

    protected FloatBuffer mVertexBuffer;
    protected ShortBuffer mVertexIndexBuffer;
    protected FloatBuffer mTextureIndexBuffer;
    protected long mStartTime;
    protected float mITimeValue;

    public GPUImageAnimFilter(Context context)
    {
        this(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default), GLUtil.readShaderFromRaw(context, R.raw.fragment_anim_image_default));
    }

    public GPUImageAnimFilter(Context context, String vertex, String fragment)
    {
        super(context, vertex, fragment);
    }

    @Override
    protected void onInitProgramHandle()
    {
        vPositionHandle = GLES20.glGetAttribLocation(getProgram(), "vPosition");
        vCoordinateHandle = GLES20.glGetAttribLocation(getProgram(), "vCoordinate");
        vMatrixHandle = GLES20.glGetUniformLocation(getProgram(), "vMatrix");
        vTextureHandle = GLES20.glGetUniformLocation(getProgram(), "vTexture");
        iTimeHandle = GLES20.glGetUniformLocation(getProgram(), "iTime");
    }

    @Override
    protected void onInitBufferData()
    {
        mVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.VERTEX_SQUARE);
        mVertexIndexBuffer = ByteBufferUtil.getNativeShortBuffer(GLConstant.VERTEX_INDEX);
        mTextureIndexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.TEXTURE_INDEX);
    }

    protected void preDrawSteps1DataBuffer()
    {
        // 绑定顶点坐标缓冲
        mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(vPositionHandle, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(vPositionHandle);

        // 绑定纹理坐标缓冲
        mTextureIndexBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, mTextureIndexBuffer);
        GLES20.glEnableVertexAttribArray(vCoordinateHandle);
    }

    protected void preDrawSteps2BindTexture(int textureID)
    {
        // 绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(getTextureType(), textureID);
        GLES20.glUniform1i(vTextureHandle, 0);
    }

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
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }

    protected boolean isViewPortAvailable(boolean drawBuffer)
    {
        return (drawBuffer && getFrameBufferH() != 0 && getFrameBufferW() != 0) || (!drawBuffer && getSurfaceW() != 0 && getSurfaceH() != 0);
    }

    protected void preDrawSteps4Other(boolean drawBuffer)
    {
        GLES20.glUniform1f(iTimeHandle, mITimeValue);
    }

    protected final void draw(int textureID, boolean drawBuffer)
    {
        GLES20.glUseProgram(getProgram());

        preDrawSteps1DataBuffer();
        preDrawSteps2BindTexture(textureID);
        preDrawSteps3Matrix(drawBuffer);
        preDrawSteps4Other(drawBuffer);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

        afterDraw();
    }

    protected void afterDraw()
    {
        GLES20.glDisableVertexAttribArray(vPositionHandle);
        GLES20.glDisableVertexAttribArray(vCoordinateHandle);
        GLES20.glBindTexture(getTextureType(), 0);
    }

    public void onDrawFrame(int textureID)
    {
        if (!GLES20.glIsProgram(getProgram()))
        {
            return;
        }
        draw(textureID, false);
    }

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

            draw(textureID, true);
            mFrameBufferMgr.unbind();
            return mFrameBufferMgr.getCurrentTextureId();
        }

        return textureID;
    }

    protected int getTextureType()
    {
        return GLES20.GL_TEXTURE_2D;
    }

    public void setStartTimeValue(long start)
    {
        mStartTime = start;
    }

    public void setTimeValue(long time)
    {
        float dt = (time - mStartTime) / getEffectTimeCycle();
        int dtInt = (int) dt;
        mITimeValue = dt - dtInt;
        if (!isEffectCycle() && dtInt > 0)
        {
            mITimeValue = 1;
        }
    }

    protected float getEffectTimeCycle()
    {
        return 1200f;
    }

    protected boolean isEffectCycle()
    {
        return false;
    }
}
