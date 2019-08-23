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
public class LuminanceMeltTransitionFilter extends GPUImageTransitionFilter {
    private int directionHandle;
    private int l_thresholdHandle;
    private int aboveHandle;

    private float mUpDown = 1;

    public LuminanceMeltTransitionFilter(Context context) {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default),
                GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_luminance_melt));
    }

    @Override
    protected boolean onInitTaskMgr() {
        return false;
    }

    @Override
    public GPUTransitionFilterType getFilterType() {
        return GPUTransitionFilterType.LUMINANCE_MELT;
    }

    @Override
    protected void onInitProgramHandle() {
        super.onInitProgramHandle();

        directionHandle = GLES20.glGetUniformLocation(getProgram(), "direction");
        l_thresholdHandle = GLES20.glGetUniformLocation(getProgram(), "l_threshold");
        aboveHandle = GLES20.glGetUniformLocation(getProgram(), "above");
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer) {
        super.preDrawSteps4Other(drawBuffer);

        GLES20.glUniform1f(l_thresholdHandle, 1f);
        GLES20.glUniform1f(directionHandle, mUpDown);
        GLES20.glUniform1f(aboveHandle, 0);
    }

    @Override
    protected float getEffectTimeCycle() {
        return 1200f;
    }

    @Override
    protected boolean isEffectCycle() {
        return false;
    }

    public void setDirection(boolean upDown) {
        mUpDown = upDown ? 1 : 0;
    }
}
