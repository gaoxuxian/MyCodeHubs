package com.xx.avlibrary.gl.filter.transitions;

import android.content.Context;
import android.opengl.GLES20;
import com.xx.avlibrary.R;
import com.xx.avlibrary.gl.filter.GPUImageTransitionFilter;
import com.xx.avlibrary.gl.filter.GPUTransitionFilterType;
import com.xx.avlibrary.gl.util.GLUtil;

public class RadialBlurTransitionFilter extends GPUImageTransitionFilter {

    private int order;
    private int orderHandle;

    public RadialBlurTransitionFilter(Context context, int order) {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default),
                GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_radial_blur));
        this.order = order;
    }

    @Override
    protected boolean onInitTaskMgr() {
        return false;
    }

    @Override
    public GPUTransitionFilterType getFilterType() {
        if (order == 1) {
            return GPUTransitionFilterType.RADIAL_BLUR_ZOOM_OUT;
        } else if (order == -1) {
            return GPUTransitionFilterType.RADIAL_BLUR_ZOOM_IN;
        }
        return null;
    }

    @Override
    protected void onInitProgramHandle() {
        super.onInitProgramHandle();
        orderHandle = GLES20.glGetUniformLocation(getProgram(), "order");
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer) {
        super.preDrawSteps4Other(drawBuffer);
        GLES20.glUniform1f(orderHandle, order);
    }

    @Override
    protected float getEffectTimeCycle() {
        return 1000;
    }

    @Override
    protected boolean isEffectCycle() {
        return false;
    }
}
