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
 * Created by Gxx on 2019/1/7.
 */
public class ColourDistanceTransitionFilter extends GPUImageTransitionFilter {
    private int powerHandle;

    public ColourDistanceTransitionFilter(Context context) {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default),
                GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_color_distance));
    }

    @Override
    protected boolean onInitTaskMgr() {
        return false;
    }

    @Override
    public GPUTransitionFilterType getFilterType() {
        return GPUTransitionFilterType.COLOR_DISTANCE;
    }

    @Override
    protected void onInitProgramHandle() {
        super.onInitProgramHandle();

        powerHandle = GLES20.glGetUniformLocation(getProgram(), "power");
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer) {
        super.preDrawSteps4Other(drawBuffer);

        GLES20.glUniform1f(powerHandle, 20);
    }

    @Override
    protected float getEffectTimeCycle() {
        return 600f;
    }

    @Override
    protected boolean isEffectCycle() {
        return false;
    }
}
