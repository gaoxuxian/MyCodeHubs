package com.xx.avlibrary.gl.filter.transitions;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import com.xx.avlibrary.R;
import com.xx.avlibrary.gl.filter.GPUImageTransitionFilter;
import com.xx.avlibrary.gl.filter.GPUTransitionFilterType;
import com.xx.avlibrary.gl.util.GLUtil;

public class NoiseBlurZoomTransitionFilter extends GPUImageTransitionFilter {

    public NoiseBlurZoomTransitionFilter(Context context) {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default),
                GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_noise_blur_zoom));
    }

    @Override
    protected boolean onInitTaskMgr() {
        return false;
    }

    @Override
    public GPUTransitionFilterType getFilterType() {
        return GPUTransitionFilterType.JUST_EXTEND;
    }

    @Override
    protected void onInitProgramHandle() {
        super.onInitProgramHandle();
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer) {
        super.preDrawSteps4Other(drawBuffer);
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
