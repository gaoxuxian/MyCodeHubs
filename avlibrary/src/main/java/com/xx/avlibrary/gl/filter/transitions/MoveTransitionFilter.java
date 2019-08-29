package com.xx.avlibrary.gl.filter.transitions;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import com.xx.avlibrary.R;
import com.xx.avlibrary.gl.filter.GPUImageTransitionFilter;
import com.xx.avlibrary.gl.filter.GPUTransitionFilterType;
import com.xx.avlibrary.gl.util.GLUtil;

public class MoveTransitionFilter extends GPUImageTransitionFilter {

    private int directionHandle;
    private float[] direction;

    public MoveTransitionFilter(Context context, int xDir, int yDir) {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default),
                GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_move_blur));
        direction = new float[]{xDir, yDir};
    }

    @Override
    protected boolean onInitTaskMgr() {
        return false;
    }

    @Override
    public GPUTransitionFilterType getFilterType() {
        if (direction != null) {
            if (direction[0] == 1) {
                return GPUTransitionFilterType.MOVE_X_RIGHT;
            } else if (direction[0] == -1) {
                return GPUTransitionFilterType.MOVE_X_LEFT;
            } else if (direction[1] == 1) {
                return GPUTransitionFilterType.MOVE_Y_UP;
            } else if (direction[1] == -1) {
                return GPUTransitionFilterType.MOVE_Y_DOWN;
            }
        }
        return null;
    }

    @Override
    protected void onInitProgramHandle() {
        super.onInitProgramHandle();
        directionHandle = GLES20.glGetUniformLocation(getProgram(), "direction");
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer) {
        super.preDrawSteps4Other(drawBuffer);
        GLES20.glUniform2fv(directionHandle, 1, direction, 0);
    }

    @Override
    protected float getEffectTimeCycle() {
        return 800;
    }

    @Override
    protected boolean isEffectCycle() {
        return false;
    }
}
