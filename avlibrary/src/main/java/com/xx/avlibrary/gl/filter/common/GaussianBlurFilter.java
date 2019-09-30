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

import javax.microedition.khronos.egl.EGLConfig;

public class GaussianBlurFilter extends GPUImageFilter {

    private VaryFilter mReduceFilter;// 缩小
//    private VaryFilter mExpandFilter;// 扩大
    private boolean drawX;
    private int uOffsetCoordinateLoc;
    private float mRadius = 1.2f;
    private float mTempRadius = 1.2f;
    private int mSize = 2;

    public GaussianBlurFilter(Context context) {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_blur_gaussian),
                GLUtil.readShaderFromRaw(context, R.raw.fragment_blur_gaussian));
        mReduceFilter = new VaryFilter(context);
//        mExpandFilter = new VaryFilter(context);
    }

    @Override
    protected void onInitBufferData() {
        mVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.VERTEX_SQUARE);
        mVertexIndexBuffer = ByteBufferUtil.getNativeShortBuffer(GLConstant.VERTEX_INDEX);
        mTextureIndexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.TEXTURE_INDEX_V2);
    }

    @Override
    protected boolean onInitTaskMgr() {
        return false;
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
    public void onSurfaceCreated(EGLConfig config) {
        super.onSurfaceCreated(config);
        mReduceFilter.onSurfaceCreated(config);
//        mExpandFilter.onSurfaceCreated(config);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        super.onSurfaceChanged(width, height);
        mReduceFilter.onSurfaceChanged(width, height);
//        mExpandFilter.onSurfaceChanged(width, height);
    }

    @Override
    protected int createFrameBufferSize() {
        return 2;
    }

    @Override
    public void initFrameBuffer(int width, int height) {
        super.initFrameBuffer(width/2, height/2);
        mReduceFilter.initFrameBuffer(width/2, height/2);
//        mExpandFilter.initFrameBuffer(width, height);
    }

    @Override
    public void setTextureWH(int width, int height) {
        super.setTextureWH(width, height);
        mReduceFilter.setTextureWH(width, height);
//        mExpandFilter.setTextureWH(width, height);
    }

    @Override
    protected void onInitProgramHandle() {
        super.onInitProgramHandle();
        uOffsetCoordinateLoc = GLES20.glGetUniformLocation(getProgram(), "offsetCoordinate");
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

        float texture_x_scale = 1;
        float texture_y_scale = (float) mTextureH / mTextureW;
        float scale = Math.max(1f / texture_x_scale, vs / texture_y_scale);

        matrix.pushMatrix();
        matrix.scale(scale, scale,1f);
        matrix.scale(texture_x_scale, texture_y_scale, 1f);
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer) {
        if (!drawX) {
            GLES20.glUniform2f(uOffsetCoordinateLoc, mRadius * 2.4f * 2f / getTextureW(), 0.f);
        } else {
            GLES20.glUniform2f(uOffsetCoordinateLoc, 0.f, mRadius * 2.4f * 2f / getTextureH());
        }
    }

    @Override
    public void onDrawFrame(int textureID) {
        initFrameBufferOfTextureSize();
        super.onDrawFrame(onDrawBuffer(textureID));
    }

    @Override
    public int onDrawBuffer(int textureID) {
        if (!GLES20.glIsProgram(getProgram()))
        {
            return textureID;
        }

        if (mFrameBufferMgr != null)
        {
            textureID = mReduceFilter.onDrawBuffer(textureID);

            for (int i = 0; i < mSize; i++) {
                float radius = mTempRadius + i*0.2f;
                textureID = drawEffectBuffer(textureID, true, radius);
                textureID = drawEffectBuffer(textureID, false, radius);
            }

//            textureID = mExpandFilter.onDrawBuffer(textureID);

            return textureID;
        }

        return textureID;
    }

    private int drawEffectBuffer(int textureID, boolean x, float radius) {
        mFrameBufferMgr.bindNext();
        mFrameBufferMgr.clearColor(true, true, true, true, true);
        mFrameBufferMgr.clearDepth(true, true);
        mFrameBufferMgr.clearStencil(true, true);
        drawX = x;
        mRadius = radius;
        draw(textureID, true);
        mFrameBufferMgr.unbind();
        return mFrameBufferMgr.getCurrentTextureId();
    }

    public void setBlurParams(int size, float radius) {
        if (size > 2) {
            size = 2;
        } else if (size < 0) {
            size = 1;
        }

        if (radius > 1.4f) {
            radius = 1.4f;
        } else if (radius < 0.f) {
            radius = 1.2f;
        }

        mSize = size;
        mTempRadius = radius;
    }

    @Override
    public void destroy() {
        super.destroy();
        if (mReduceFilter != null) {
            mReduceFilter.destroy();
        }
//        if (mExpandFilter != null) {
//            mExpandFilter.destroy();
//        }
    }
}
