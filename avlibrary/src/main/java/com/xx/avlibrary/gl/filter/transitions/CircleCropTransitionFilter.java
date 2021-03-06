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
 * Created by Gxx on 2019/1/4.
 */
public class CircleCropTransitionFilter extends GPUImageTransitionFilter {
    private int ratioHandle;
    private int bgColorHandle;

    private float[] bgColorValue;

    public CircleCropTransitionFilter(Context context) {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default),
                GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_circle_crop));
    }

    @Override
    protected boolean onInitTaskMgr() {
        return false;
    }

    @Override
    protected void onInitBaseData() {
        super.onInitBaseData();

        bgColorValue = new float[4];
        bgColorValue[0] = 0;
        bgColorValue[1] = 0;
        bgColorValue[2] = 0;
        bgColorValue[3] = 1;
    }

    @Override
    public GPUTransitionFilterType getFilterType() {
        return GPUTransitionFilterType.CIRCLE_CROP;
    }

    @Override
    protected void onInitProgramHandle() {
        super.onInitProgramHandle();

        ratioHandle = GLES20.glGetUniformLocation(getProgram(), "ratio");
        bgColorHandle = GLES20.glGetUniformLocation(getProgram(), "bgColor");
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer) {
        super.preDrawSteps4Other(drawBuffer);

        GLES20.glUniform1f(ratioHandle, (float) mTextureW / mTextureH);
        GLES20.glUniform4fv(bgColorHandle, 1, bgColorValue, 0);
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
