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
public class ColorScanTransitionFilterV2 extends GPUImageTransitionFilter {
    public ColorScanTransitionFilterV2(Context context) {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default),
                GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_color_translation_v2));
    }

    @Override
    protected boolean onInitTaskMgr() {
        return false;
    }

    @Override
    public GPUTransitionFilterType getFilterType() {
        return GPUTransitionFilterType.COLOR_SCAN_V2;
    }

    @Override
    public void setTimeValue(long time) {
        float dt = (time - mStartTime) / getEffectTimeCycle();
        int dtInt = (int) dt;
        mProgressValue = dt - dtInt;
        if (!isEffectCycle() && dtInt > 0) {
            mProgressValue = 1;
        }
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
