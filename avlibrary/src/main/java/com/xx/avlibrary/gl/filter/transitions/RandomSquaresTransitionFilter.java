package com.xx.avlibrary.gl.filter.transitions;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import com.xx.avlibrary.gl.util.GLUtil;
import com.xx.avlibrary.gl.util.GlMatrixTools;
import com.xx.avlibrary.R;
import com.xx.avlibrary.gl.filter.GPUImageTransitionFilter;
import com.xx.avlibrary.gl.filter.GPUTransitionFilterType;

/**
 * 随机方块转场
 *
 * @author Gxx
 * Created by Gxx on 2019/1/10.
 */
public class RandomSquaresTransitionFilter extends GPUImageTransitionFilter {
    private int sizeHandle;
    private int smoothnessHandle;

    private int[] sizeValue;

    public RandomSquaresTransitionFilter(Context context) {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default),
                GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_random_square));
    }

    @Override
    protected boolean onInitTaskMgr() {
        return false;
    }

    @Override
    protected void onInitBaseData() {
        super.onInitBaseData();

        sizeValue = new int[2];
        sizeValue[0] = 5;
        sizeValue[1] = 5;
    }

    @Override
    public GPUTransitionFilterType getFilterType() {
        return GPUTransitionFilterType.RANDOM_SQUARE;
    }

    @Override
    protected void onInitProgramHandle() {
        super.onInitProgramHandle();

        sizeHandle = GLES30.glGetUniformLocation(getProgram(), "size");
        smoothnessHandle = GLES30.glGetUniformLocation(getProgram(), "smoothness");
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer) {
        super.preDrawSteps4Other(drawBuffer);

        GLES20.glUniform1f(smoothnessHandle, 0.5f);
        GLES20.glUniform2iv(sizeHandle, 1, sizeValue, 0);
    }

    @Override
    protected float getEffectTimeCycle() {
        return 1200f;
    }

    @Override
    protected boolean isEffectCycle() {
        return false;
    }
}
