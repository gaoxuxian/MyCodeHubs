package com.xx.avlibrary.gl.filter.transitions;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import com.xx.avlibrary.R;
import com.xx.avlibrary.gl.filter.GPUImageTransitionFilter;
import com.xx.avlibrary.gl.filter.GPUTransitionFilterType;
import com.xx.avlibrary.gl.util.GLUtil;

public class RotationTransitionFilter extends GPUImageTransitionFilter {
    private int degreeHandle;
    private int textureWHandle;
    private int textureHHandle;
    private int jitterRangeHHandle;

    public RotationTransitionFilter(Context context) {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default),
                GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_rotation));
    }

    @Override
    protected boolean onInitTaskMgr() {
        return false;
    }

    @Override
    public GPUTransitionFilterType getFilterType() {
        return GPUTransitionFilterType.ROTATE_ZOOM;
    }

    @Override
    protected void onInitProgramHandle() {
        super.onInitProgramHandle();

        degreeHandle = GLES30.glGetUniformLocation(getProgram(), "degree");
        textureWHandle = GLES30.glGetUniformLocation(getProgram(), "textureW");
        textureHHandle = GLES30.glGetUniformLocation(getProgram(), "textureH");
        jitterRangeHHandle = GLES30.glGetUniformLocation(getProgram(), "jitterRange");
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer) {
        super.preDrawSteps4Other(drawBuffer);

        GLES20.glUniform1f(degreeHandle, 20);
        GLES20.glUniform1f(jitterRangeHHandle, 3);
        GLES20.glUniform1f(textureWHandle, getTextureW());
        GLES20.glUniform1f(textureHHandle, getTextureH());
    }

    @Override
    protected float getEffectTimeCycle() {
        return 800f;
    }

    @Override
    protected boolean isEffectCycle() {
        return false;
    }
}
