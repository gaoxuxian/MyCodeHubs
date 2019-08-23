package com.xx.avlibrary.gl.filter.transitions;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import com.xx.avlibrary.R;
import com.xx.avlibrary.gl.filter.GPUImageTransitionFilter;
import com.xx.avlibrary.gl.filter.GPUTransitionFilterType;
import com.xx.avlibrary.gl.util.GLUtil;

/**
 * 旋转闪白
 */
public class RotatingWhiteTransitionFilter extends GPUImageTransitionFilter {

    private int rotationHandle;
    private int scaleHandle;
    private int textureWHandle;
    private int textureHHandle;

    public RotatingWhiteTransitionFilter(Context context) {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default),
                GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_rotate_white));
    }

    @Override
    protected boolean onInitTaskMgr() {
        return false;
    }

    @Override
    public GPUTransitionFilterType getFilterType() {
        return GPUTransitionFilterType.ROTATE_WHITE;
    }

    @Override
    protected void onInitProgramHandle() {
        super.onInitProgramHandle();

        rotationHandle = GLES30.glGetUniformLocation(getProgram(), "rotation");
        scaleHandle = GLES30.glGetUniformLocation(getProgram(), "scale");
        textureWHandle = GLES30.glGetUniformLocation(getProgram(), "textureW");
        textureHHandle = GLES30.glGetUniformLocation(getProgram(), "textureH");
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer) {
        super.preDrawSteps4Other(drawBuffer);

        GLES20.glUniform1f(rotationHandle, 6);
        GLES20.glUniform1f(scaleHandle, 1.2f);
        GLES20.glUniform1f(textureWHandle, getTextureW());
        GLES20.glUniform1f(textureHHandle, getTextureH());
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
