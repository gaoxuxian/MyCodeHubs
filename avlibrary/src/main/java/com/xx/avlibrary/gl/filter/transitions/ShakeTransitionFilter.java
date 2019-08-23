package com.xx.avlibrary.gl.filter.transitions;

import android.content.Context;
import android.opengl.GLES20;
import com.xx.avlibrary.gl.util.GLUtil;
import com.xx.avlibrary.gl.util.GlMatrixTools;
import com.xx.avlibrary.R;
import com.xx.avlibrary.gl.filter.GPUImageTransitionFilter;
import com.xx.avlibrary.gl.filter.GPUTransitionFilterType;

/**
 * 画面抖动-rgb分离 转场
 *
 * @author Gxx
 * Created by Gxx on 2019/1/3.
 */
public class ShakeTransitionFilter extends GPUImageTransitionFilter {
    public ShakeTransitionFilter(Context context) {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default),
                GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_shake));
    }

    @Override
    public GPUTransitionFilterType getFilterType() {
        return GPUTransitionFilterType.SHAKE;
    }

    @Override
    protected boolean onInitTaskMgr() {
        return false;
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
