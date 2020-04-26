package com.xx.avlibrary.gl.filter.common;

import android.content.Context;
import android.opengl.GLES20;

import com.xx.avlibrary.R;
import com.xx.avlibrary.gl.filter.GLConstant;
import com.xx.avlibrary.gl.filter.GPUFilterType;
import com.xx.avlibrary.gl.filter.GPUImageFilter;
import com.xx.avlibrary.gl.util.ByteBufferUtil;
import com.xx.avlibrary.gl.util.GLUtil;
import com.xx.avlibrary.gl.util.GlMatrixTools;

public class GPUCircleImageFilter extends GPUImageFilter {

    protected int widthHandle;
    protected int heightHandle;
    protected int maskColorHandle;

    public GPUCircleImageFilter(Context context) {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_cycle),
                GLUtil.readShaderFromRaw(context, R.raw.fragment_image_cycle));
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
    protected void onInitProgramHandle() {
        super.onInitProgramHandle();
        widthHandle = GLES20.glGetUniformLocation(getProgram(), "vTextureWidth");
        heightHandle = GLES20.glGetUniformLocation(getProgram(), "vTextureHeight");
        maskColorHandle = GLES20.glGetUniformLocation(getProgram(), "maskColor");
    }

    @Override
    protected void preDrawSteps3Matrix(boolean drawBuffer) {
        if (!isViewPortAvailable(drawBuffer)) return;

        // 视口区域大小(归一化映射范围)
        GLES20.glViewport(0, 0, drawBuffer ? getFrameBufferW() : getSurfaceW(), drawBuffer ? getFrameBufferH() : getSurfaceH());
        // 矩阵变换
        GlMatrixTools matrix = getMatrix();
        matrix.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
        float vs = drawBuffer ? (float) getFrameBufferH() / getFrameBufferW() : (float) getSurfaceH() / getSurfaceW();
        matrix.frustum(-1, 1, -vs, vs, 3, 7);

        matrix.pushMatrix();
        matrix.scale(1f, (float) mTextureH / mTextureW, 1f);
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer) {
        GLES20.glUniform1f(widthHandle, mTextureW);
        GLES20.glUniform1f(heightHandle, mTextureH);
        GLES20.glUniform4fv(maskColorHandle, 1, new float[]{1, 1, 1, 1}, 0);
    }
}
