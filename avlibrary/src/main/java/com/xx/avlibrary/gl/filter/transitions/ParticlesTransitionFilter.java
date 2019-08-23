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
 * 颗粒感转场
 *
 * @author Gxx
 * Created by Gxx on 2019/1/10.
 */
public class ParticlesTransitionFilter extends GPUImageTransitionFilter {
    private int vStrengthHandle;

    public ParticlesTransitionFilter(Context context) {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default),
                GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_particles));
    }

    @Override
    protected boolean onInitTaskMgr() {
        return false;
    }

    @Override
    public GPUTransitionFilterType getFilterType() {
        return GPUTransitionFilterType.PARTICLES;
    }

    @Override
    protected void onInitProgramHandle() {
        super.onInitProgramHandle();

        vStrengthHandle = GLES30.glGetUniformLocation(getProgram(), "vStrength");
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer) {
        super.preDrawSteps4Other(drawBuffer);

        GLES20.glUniform1f(vStrengthHandle, 0.6f);
    }

    @Override
    protected float getEffectTimeCycle() {
        return 1200;
    }

    @Override
    protected boolean isEffectCycle() {
        return false;
    }
}
