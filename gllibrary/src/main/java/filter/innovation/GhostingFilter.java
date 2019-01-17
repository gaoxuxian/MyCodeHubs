package filter.innovation;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;

import filter.GLConstant;
import filter.GPUAnimFilterType;
import filter.GPUImageAnimFilter;
import library.R;
import util.ByteBufferUtil;
import util.GLUtil;
import util.GlMatrixTools;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/7.
 */
public class GhostingFilter extends GPUImageAnimFilter
{
    private static final float DEFAULT_CYCLE_TIME = 2;

    private int vOffsetHandle;

    public GhostingFilter(Context context)
    {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default), GLUtil.readShaderFromRaw(context, R.raw.fragment_ghosting));
    }

    @Override
    protected boolean onInitTaskMgr()
    {
        return false;
    }

    @Override
    public GPUAnimFilterType getFilterType()
    {
        return GPUAnimFilterType.GHOSTING;
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

        vOffsetHandle = GLES20.glGetUniformLocation(getProgram(), "vOffset");
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
        float vs = drawBuffer ? (float) getFrameBufferH() / getFrameBufferW() : (float) getSurfaceH() / getSurfaceW();
        matrix.frustum(-1, 1, -vs, vs, 3, 7);

        matrix.pushMatrix();
        matrix.scale(1f, (float) mTextureH / mTextureW, 1f);
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer)
    {
        super.preDrawSteps4Other(drawBuffer);
        GLES30.glUniform1f(vOffsetHandle, 0.1f);
    }

    @Override
    public void onDrawFrame(int textureID)
    {
        GLES30.glClearColor(1,1,1,1);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        super.onDrawFrame(textureID);
    }

    @Override
    public void setTimeValue(long time)
    {
        float dt = (time - mStartTime) / getEffectTimeCycle();
        int dtInt = (int) dt;
        mITimeValue = (dt - dtInt) * DEFAULT_CYCLE_TIME;
        if (!isEffectCycle() && dtInt > 0)
        {
            mITimeValue = 0;
        }
    }

    @Override
    protected float getEffectTimeCycle()
    {
        return 2000f;
    }

    @Override
    protected boolean isEffectCycle()
    {
        return true;
    }
}
