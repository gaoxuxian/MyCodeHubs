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
public class TranslationTransitionFilter extends GPUImageTransitionFilter {
    private int directionHandle;
    private float[] directionValue; // 平移方向 -1\0\1

    public TranslationTransitionFilter(Context context) {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default),
                GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_translation));
    }

    @Override
    protected boolean onInitTaskMgr() {
        return false;
    }

    @Override
    protected void onInitBaseData() {
        super.onInitBaseData();

        directionValue = new float[2];
        directionValue[0] = 1;
        directionValue[1] = 1;
    }

    @Override
    public GPUTransitionFilterType getFilterType() {
        return GPUTransitionFilterType.TRANSLATION;
    }

    @Override
    protected void onInitProgramHandle() {
        super.onInitProgramHandle();

        directionHandle = GLES20.glGetUniformLocation(getProgram(), "direction");
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer) {
        super.preDrawSteps4Other(drawBuffer);

        GLES20.glUniform2fv(directionHandle, 1, directionValue, 0);
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
