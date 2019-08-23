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
public class ColorGhostingTransitionFilter extends GPUImageTransitionFilter {
    private int iTimeHandle;
    private int vOffsetHandle;
    private int powerHandle;
    private float iTimeValue;

    public ColorGhostingTransitionFilter(Context context) {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default),
                GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_color_ghosting));
    }

    @Override
    protected boolean onInitTaskMgr() {
        return false;
    }

    @Override
    public GPUTransitionFilterType getFilterType() {
        return GPUTransitionFilterType.COLOR_GHOSTING;
    }

    @Override
    protected void onInitProgramHandle() {
        super.onInitProgramHandle();

        iTimeHandle = GLES20.glGetUniformLocation(getProgram(), "iTime");
        vOffsetHandle = GLES20.glGetUniformLocation(getProgram(), "vOffset");
        powerHandle = GLES20.glGetUniformLocation(getProgram(), "power");
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer) {
        super.preDrawSteps4Other(drawBuffer);

        GLES20.glUniform1f(powerHandle, 2.f);
        GLES20.glUniform1f(vOffsetHandle, 0.05f);
        GLES20.glUniform1f(iTimeHandle, iTimeValue);
    }

    @Override
    public void setTimeValue(long time) {
        float dt = (time - mStartTime) / getEffectTimeCycle();
        int dtInt = (int) dt;
        mProgressValue = dt - dtInt;

        if (!isEffectCycle() && dtInt > 0) {
            mProgressValue = 1;
        }
        iTimeValue = mProgressValue * 2f;
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
