package com.xx.avlibrary.gl.filter.transitions;

import android.content.Context;
import android.opengl.GLES20;
import com.xx.avlibrary.gl.util.GLUtil;
import com.xx.avlibrary.gl.util.GlMatrixTools;
import com.xx.avlibrary.R;
import com.xx.avlibrary.gl.filter.GPUImageTransitionFilter;
import com.xx.avlibrary.gl.filter.GPUTransitionFilterType;

/**
 * 方块自身翻转
 *
 * @author Gxx
 * Created by Gxx on 2019/1/8.
 */
public class SquareAnimTransitionFilter extends GPUImageTransitionFilter {
    private int sizeHandle;
    private int[] sizeValue;

    public SquareAnimTransitionFilter(Context context) {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default),
                GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_square_anim));
    }

    @Override
    protected boolean onInitTaskMgr() {
        return false;
    }

    @Override
    protected void onInitBaseData() {
        super.onInitBaseData();

        sizeValue = new int[2];
        sizeValue[0] = 4;
        sizeValue[1] = 4;
    }

    @Override
    public GPUTransitionFilterType getFilterType() {
        return GPUTransitionFilterType.SQUARE_ANIM;
    }

    @Override
    protected void onInitProgramHandle() {
        super.onInitProgramHandle();

        sizeHandle = GLES20.glGetUniformLocation(getProgram(), "size");
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer) {
        super.preDrawSteps4Other(drawBuffer);

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
