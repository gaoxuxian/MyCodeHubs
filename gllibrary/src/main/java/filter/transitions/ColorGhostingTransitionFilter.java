package filter.transitions;

import android.content.Context;
import android.opengl.GLES20;

import filter.GPUFilterType;
import filter.GPUImageTransitionFilter;
import library.R;
import util.GLUtil;
import util.GlMatrixTools;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/7.
 */
public class ColorGhostingTransitionFilter extends GPUImageTransitionFilter
{
    private int iTimeHandle;
    private int vOffsetHandle;
    private int powerHandle;
    private float iTimeValue;

    public ColorGhostingTransitionFilter(Context context)
    {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default), GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_color_ghosting));
    }

    @Override
    protected boolean onInitTaskMgr()
    {
        return false;
    }

    @Override
    public GPUFilterType getFilterType()
    {
        return null;
    }

    @Override
    protected void onInitProgramHandle()
    {
        super.onInitProgramHandle();

        iTimeHandle = GLES20.glGetUniformLocation(getProgram(), "iTime");
        vOffsetHandle = GLES20.glGetUniformLocation(getProgram(), "vOffset");
        powerHandle = GLES20.glGetUniformLocation(getProgram(), "power");
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

        GLES20.glUniform1f(powerHandle, 2.f);
        GLES20.glUniform1f(vOffsetHandle, 0.1f);
        GLES20.glUniform1f(iTimeHandle, iTimeValue);
    }

    @Override
    public void setTimeValue(long time)
    {
        float dt = (time - mStartTime) / getEffectTimeCycle();
        int dtInt = (int) dt;
        mProgressValue = dt - dtInt;

        if (!isEffectCycle() && dtInt > 0)
        {
            mProgressValue = 1;
        }
        iTimeValue = mProgressValue * 2f;
    }

    @Override
    protected float getEffectTimeCycle()
    {
        return 2000f;
    }

    @Override
    protected boolean isEffectCycle()
    {
        return false;
    }
}
