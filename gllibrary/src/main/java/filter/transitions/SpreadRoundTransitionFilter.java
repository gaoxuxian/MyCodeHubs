package filter.transitions;

import android.content.Context;
import android.opengl.GLES20;

import filter.GPUFilterType;
import filter.GPUImageTransitionFilter;
import library.R;
import util.GLUtil;
import util.GlMatrixTools;

/**
 * 扩散圆转场-- 固定圆、可变圆之间的比较算法, 固定圆算法：固定圆心、固定半径, 可变圆算法：指定圆心, 半径随时间变大
 * @author Gxx
 * Created by Gxx on 2019/1/3.
 */
public class SpreadRoundTransitionFilter extends GPUImageTransitionFilter
{
    private int dotsHandle;
    private int centerHandle;

    private float[] centerValue;

    public SpreadRoundTransitionFilter(Context context)
    {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default), GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_spread_round));
    }

    @Override
    protected boolean onInitTaskMgr()
    {
        return false;
    }

    @Override
    public GPUFilterType getFilterType()
    {
        return GPUFilterType.TRANSITION_SPREAD_ROUND;
    }

    @Override
    protected void onInitBaseData()
    {
        super.onInitBaseData();

        centerValue = new float[2];
        centerValue[0] = 0.5f;
        centerValue[1] = 0.5f;
    }

    @Override
    protected void onInitProgramHandle()
    {
        super.onInitProgramHandle();

        dotsHandle = GLES20.glGetUniformLocation(getProgram(), "dots");
        centerHandle = GLES20.glGetUniformLocation(getProgram(), "center");
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

        GLES20.glUniform1f(dotsHandle, 4);
        GLES20.glUniform2fv(centerHandle, 1, centerValue, 0);
    }

    @Override
    protected float getEffectTimeCycle()
    {
        return 1200f;
    }

    @Override
    protected boolean isEffectCycle()
    {
        return true;
    }
}
