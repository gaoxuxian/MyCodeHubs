package lib.gl.filter.transitions;

import android.content.Context;
import android.opengl.GLES20;

import lib.gl.filter.GPUImageTransitionFilter;
import lib.gl.filter.GPUTransitionFilterType;
import lib.gl.R;
import lib.gl.util.GLUtil;
import lib.gl.util.GlMatrixTools;

/**
 * 画面抖动-rgb分离 转场
 * @author Gxx
 * Created by Gxx on 2019/1/3.
 */
public class ShakeTransitionFilter extends GPUImageTransitionFilter
{
    public ShakeTransitionFilter(Context context)
    {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default), GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_shake));
    }

    @Override
    public GPUTransitionFilterType getFilterType()
    {
        return GPUTransitionFilterType.SHAKE;
    }

    @Override
    protected boolean onInitTaskMgr()
    {
        return false;
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
