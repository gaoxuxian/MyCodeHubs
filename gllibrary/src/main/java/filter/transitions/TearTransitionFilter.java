package filter.transitions;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;

import filter.GPUFilterType;
import filter.GPUImageTransitionFilter;
import filter.GPUTransitionFilterType;
import library.R;
import util.GLUtil;
import util.GlMatrixTools;

/**
 * 撕裂转场
 * @author Gxx
 * Created by Gxx on 2019/1/10.
 */
public class TearTransitionFilter extends GPUImageTransitionFilter
{
    private int vStrengthHandle;

    public TearTransitionFilter(Context context)
    {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default), GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_tear));
    }

    @Override
    protected boolean onInitTaskMgr()
    {
        return false;
    }

    @Override
    public GPUTransitionFilterType getFilterType()
    {
        return GPUTransitionFilterType.TEAR;
    }

    @Override
    protected void onInitProgramHandle()
    {
        super.onInitProgramHandle();

        vStrengthHandle = GLES30.glGetUniformLocation(getProgram(), "vStrength");
    }

    @Override
    protected void preDrawSteps3Matrix(boolean drawBuffer)
    {
        if (!isViewPortAvailable(drawBuffer)) return;

        GLES20.glViewport(0, 0, drawBuffer ? getFrameBufferW() : getSurfaceW(), drawBuffer ? getFrameBufferH() : getSurfaceH());

        GlMatrixTools matrix = getMatrix();
        float vs = drawBuffer ? (float) getFrameBufferH() / getFrameBufferW() : (float) getSurfaceH() / getSurfaceW();
        matrix.frustum(-1, 1, -vs, vs, 3, 5);
        matrix.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);

        matrix.pushMatrix();
        matrix.scale(1f, (float) mTextureH / mTextureW, 1f);
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer)
    {
        super.preDrawSteps4Other(drawBuffer);

        GLES20.glUniform1f(vStrengthHandle, 0.6f);
    }

    @Override
    protected float getEffectTimeCycle()
    {
        return 1200f;
    }

    @Override
    protected boolean isEffectCycle()
    {
        return false;
    }
}
