package com.xx.avlibrary.gl.filter.transitions;

import android.content.Context;
import android.opengl.GLES20;
import com.xx.avlibrary.R;
import com.xx.avlibrary.gl.filter.GPUImageTransitionFilter;
import com.xx.avlibrary.gl.filter.GPUTransitionFilterType;
import com.xx.avlibrary.gl.util.GLUtil;

public class MotionZoomTransitionFilter extends GPUImageTransitionFilter {

    private int order;
    private int orderHandle;
    private int scaleHandle;

    public MotionZoomTransitionFilter(Context context, int order) {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default),
                GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_motion_zoom));
        this.order = order;
    }

    @Override
    protected boolean onInitTaskMgr() {
        return false;
    }

    @Override
    public GPUTransitionFilterType getFilterType() {
        if (order == 1) {
            return GPUTransitionFilterType.MOTION_ZOOM_OUT_ZOOM_IN;
        } else if (order == -1) {
            return GPUTransitionFilterType.MOTION_ZOOM_IN_ZOOM_OUT;
        }
        return null;
    }

    @Override
    protected void onInitProgramHandle() {
        super.onInitProgramHandle();
        orderHandle = GLES20.glGetUniformLocation(getProgram(), "order");
        scaleHandle = GLES20.glGetUniformLocation(getProgram(), "scale");
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer) {
        super.preDrawSteps4Other(drawBuffer);
        GLES20.glUniform1f(orderHandle, order);
        GLES20.glUniform1f(scaleHandle, 0.3f);
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
