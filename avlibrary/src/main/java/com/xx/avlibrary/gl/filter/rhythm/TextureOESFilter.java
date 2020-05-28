package com.xx.avlibrary.gl.filter.rhythm;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.xx.avlibrary.R;
import com.xx.avlibrary.gl.filter.GLConstant;
import com.xx.avlibrary.gl.filter.GPUFilterType;
import com.xx.avlibrary.gl.filter.GPUImageFilter;
import com.xx.avlibrary.gl.util.ByteBufferUtil;
import com.xx.avlibrary.gl.util.GLUtil;
import com.xx.avlibrary.gl.util.GlMatrixTools;

import java.nio.ByteBuffer;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/23.
 */
public class TextureOESFilter extends GPUImageFilter {
    private int[] mTextureIds;

    public TextureOESFilter(Context context) {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default), GLUtil.readShaderFromRaw(context, R.raw.fragment_texture_oes));
    }

    @Override
    protected void onInitBaseData() {
        super.onInitBaseData();

        mTextureIds = new int[1];
    }

    @Override
    protected void onInitBufferData() {
        mVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.VERTEX_SQUARE);
        mVertexIndexBuffer = ByteBufferUtil.getNativeShortBuffer(GLConstant.VERTEX_INDEX);
        mTextureIndexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.TEXTURE_INDEX);
    }

    @Override
    protected void preDrawSteps3Matrix(boolean drawBuffer) {
        int width = getFrameBufferW();
        int height = getFrameBufferH();

        // 视口区域大小(归一化映射范围)
        GLES20.glViewport(0, 0, width, height);
        // 矩阵变换
        GlMatrixTools matrix = getMatrix();
        matrix.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);

        float vs = (float) height / width;
        matrix.frustum(-1, 1, -vs, vs, 3, 7);

        width = getTextureW();
        height = getTextureH();

        matrix.pushMatrix();
        float degree = Math.abs(mVideoRotation) % 180;
        float scale_y = height * 1f / width;
        float scale = (degree == 90) ? Math.min(1f / scale_y, vs / 1f) : Math.min(1f, vs / scale_y);
        matrix.scale(scale, scale, 1f);
        matrix.rotate(mVideoRotation * 1.f, 0, 0, -1);
        matrix.scale(1f, height * 1f / width, 1f);
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer) {
        super.preDrawSteps4Other(drawBuffer);
    }

    /**
     * 先通过 createGlTexture() 生成纹理, 再绘制正确方向
     *
     * @param textureID
     * @return
     */
    @Override
    public int onDrawBuffer(int textureID) {
        return super.onDrawBuffer(textureID);
    }

    /**
     * 先通过 createGlTexture() 生成纹理, 再绘制正确方向
     *
     * @param textureID
     */
    @Override
    public void onDrawFrame(int textureID) {
        super.onDrawFrame(textureID);
    }

    @Override
    public GPUFilterType getFilterType() {
        return null;
    }

    @Override
    protected boolean needInitMsaaFbo() {
        return false;
    }

    private int mVideoRotation;

    public void setRotation(int degree) {
        mVideoRotation = degree;
    }

    @Override
    public void destroy() {
        super.destroy();

        if (mTextureIds != null && mTextureIds.length > 0) {
            if (GLES20.glIsTexture(mTextureIds[0])) {
                GLES20.glDeleteTextures(1, mTextureIds, 0);
                mTextureIds[0] = GLES20.GL_NONE;
            }
        }
    }

    // 记录是否铺满显示
    private boolean mScaleFullIn;

    /**
     * 设置图片缩放方式
     *
     * @param fullIn true-铺满(最短边顶边适配)，false-居中(最长边顶边适配)
     */
    public void setScaleFullIn(boolean fullIn) {
        mScaleFullIn = fullIn;
    }
}
