package com.xx.avlibrary.gl.filter.transitions;

import android.content.Context;
import android.opengl.GLES20;
import com.xx.avlibrary.gl.util.GLUtil;
import com.xx.avlibrary.gl.util.GlMatrixTools;
import com.xx.avlibrary.R;
import com.xx.avlibrary.gl.filter.GPUImageTransitionFilter;
import com.xx.avlibrary.gl.filter.GPUTransitionFilterType;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/3.
 */
public class PerlinTransitionFilter extends GPUImageTransitionFilter
{
    private int scaleHandle;
    private int smoothnessHandle;

    public PerlinTransitionFilter(Context context)
    {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default), GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_perlin));
    }

    @Override
    protected boolean onInitTaskMgr()
    {
        return false;
    }

    @Override
    public GPUTransitionFilterType getFilterType()
    {
        return GPUTransitionFilterType.PERLIN;
    }

    @Override
    protected void onInitProgramHandle()
    {
        super.onInitProgramHandle();

        scaleHandle = GLES20.glGetUniformLocation(getProgram(), "scale");
        smoothnessHandle = GLES20.glGetUniformLocation(getProgram(), "smoothness");
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

        GLES20.glUniform1f(scaleHandle, 4.0f);
        GLES20.glUniform1f(smoothnessHandle, 0.5f);
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
