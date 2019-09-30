package com.xx.avlibrary.gl.filter.common;

import android.content.Context;
import android.opengl.GLES20;
import com.xx.avlibrary.gl.filter.GLConstant;
import com.xx.avlibrary.gl.filter.GPUFilterType;
import com.xx.avlibrary.gl.filter.GPUImageFilter;
import com.xx.avlibrary.gl.util.ByteBufferUtil;
import com.xx.avlibrary.gl.util.GlMatrixTools;

public class VaryFilter extends GPUImageFilter {

    public VaryFilter(Context context) {
        super(context);
    }

    @Override
    protected boolean onInitTaskMgr() {
        return false;
    }

    @Override
    protected boolean needInitMsaaFbo() {
        return false;
    }

    @Override
    public GPUFilterType getFilterType() {
        return null;
    }

    @Override
    protected void onInitBufferData() {
        mVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.VERTEX_SQUARE);
        mVertexIndexBuffer = ByteBufferUtil.getNativeShortBuffer(GLConstant.VERTEX_INDEX);
        mTextureIndexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.TEXTURE_INDEX_V2);
    }

    @Override
    protected void preDrawSteps3Matrix(boolean drawBuffer) {
        if (!isViewPortAvailable(drawBuffer)) return;

        // 视口区域大小(归一化映射范围)
        GLES20.glViewport(0, 0, drawBuffer ? getFrameBufferW() : getSurfaceW(), drawBuffer ? getFrameBufferH() : getSurfaceH());

        // 矩阵变换
        GlMatrixTools matrix = getMatrix();
        matrix.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
        int sw = getSurfaceW();
        int sh = getSurfaceH();
        float vs = (float) sh / sw;
        matrix.frustum(-1, 1, -vs, vs, 3, 5);

        // 基于纹理原比例，做的后续变换，旋转、缩放顺序并不影响纹理内容
        float x_scale = 1f;
        float y_scale = (float) getTextureH() / getTextureW();
        float scale = Math.max(1f / x_scale, vs / y_scale);

        matrix.pushMatrix();
        matrix.scale(scale, scale, 1);//后续缩放
        matrix.scale(x_scale, y_scale, 1f);//原比例
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }
}
