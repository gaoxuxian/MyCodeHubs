package com.xx.avlibrary.gl.filter.rhythm;

import android.content.Context;
import android.opengl.GLES20;
import com.xx.avlibrary.gl.util.ByteBufferUtil;
import com.xx.avlibrary.gl.util.GlMatrixTools;
import com.xx.avlibrary.gl.filter.GLConstant;
import com.xx.avlibrary.gl.filter.GPUFilterType;
import com.xx.avlibrary.gl.filter.GPUImageFilter;

/**
 * @author Gxx
 * Created by Gxx on 2019/4/08.
 */
public class DisplayFilter extends GPUImageFilter {

    public DisplayFilter(Context context) {
        super(context);
    }

    @Override
    protected boolean onInitTaskMgr() {
        return false;
    }

    @Override
    protected void onInitBufferData() {
        mVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.VERTEX_SQUARE);
        mVertexIndexBuffer = ByteBufferUtil.getNativeShortBuffer(GLConstant.VERTEX_INDEX);
        mTextureIndexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.TEXTURE_INDEX_V2);
    }

    @Override
    public GPUFilterType getFilterType() {
        return null;
    }

    @Override
    protected boolean needInitMsaaFbo() {
        return false;
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

        float degree = Math.abs(-mDegree) % 360;
        boolean change = (degree >= 90 && degree < 180) || degree >= 270;

        // 缩放方向上，非等比缩放，所以需要先旋转再缩放
//        if (change) {
//            sw = getSurfaceH();
//            sh = getSurfaceW();
//            vs = (float) sh / sw;
//            matrix.frustum(-1, 1, -vs, vs, 3, 5);
//        }
//
//        float x_scale = 1f;
//        float y_scale = change ? (float) getTextureW() / getTextureH() : (float) getTextureH() / getTextureW();
//        float scale = Math.min(1f / x_scale, vs / y_scale);
//
//        matrix.pushMatrix();
//        matrix.scale(x_scale * scale, y_scale * scale, 1f);
//        matrix.rotate(-mDegree, 0, 0, 1);
//
//        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
//        matrix.popMatrix();

        // 基于纹理原比例，做的后续变换，旋转、缩放顺序并不影响纹理内容
        float x_scale = 1f;
        float x_scale_1 = x_scale;
        float y_scale = (float) getTextureH() / getTextureW();
        float y_scale_1 = y_scale;

        if (change) {
            x_scale = y_scale;
            y_scale = x_scale_1;
            sw = getSurfaceH();
            sh = getSurfaceW();
            vs = (float) sh / sw;
            matrix.frustum(-1, 1, -vs, vs, 3, 5);
        }

        float scale = Math.min(1f / x_scale, vs / y_scale);

        matrix.pushMatrix();

        matrix.scale(scale, scale, 1);//后续缩放
        matrix.rotate(-mDegree, 0, 0, 1);//后续旋转
        matrix.scale(x_scale_1, y_scale_1, 1f);//原比例

        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }

    @Override
    public void onDrawFrame(int textureID) {
        GLES20.glClearColor(0, 0, 0, 1);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        super.onDrawFrame(textureID);
    }

    private float mDegree;

    public void setDegree(float degree) {
        mDegree = degree;
    }
}
