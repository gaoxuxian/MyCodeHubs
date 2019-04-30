package lib.gl.filter.rhythm;

import android.content.Context;
import android.opengl.GLES20;
import androidx.annotation.FloatRange;
import lib.gl.filter.GLConstant;
import lib.gl.filter.GPUFilterType;
import lib.gl.filter.GPUImageFilter;
import lib.gl.util.ByteBufferUtil;
import lib.gl.util.GlMatrixTools;

public class FrameSizeFilter extends GPUImageFilter {

    public FrameSizeFilter(Context context) {
        super(context);
    }

    @Override
    public GPUFilterType getFilterType() {
        return null;
    }

    @Override
    protected void onInitBufferData()
    {
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

        float vs = drawBuffer ? (float) getFrameBufferH() / getFrameBufferW() : (float) getSurfaceH() / getSurfaceW();
        matrix.frustum(-1, 1, -vs, vs, 3, 7);

        // 计算画幅
        float aspectRatio = FrameSizeType.getAspectRatio(mVideoFrameSize); // 宽高比
        int width = drawBuffer ? getFrameBufferW() : getSurfaceW();
        int height = drawBuffer ? getFrameBufferH() : getSurfaceH();
        int tempHeight = (int) (width / aspectRatio);
        if (tempHeight > height) {
            width = (int) (height * aspectRatio);
        } else {
            height = tempHeight;
        }

        float us = 1f;
        if (height <= width) {
            vs = (float) height / width;
        } else {
            us = (float) width / height;
        }

        matrix.pushMatrix();
        float x_scale = 1f;
        float y_scale = getTextureH() != 0 && getTextureW() != 0 ? (float) getTextureH() / getTextureW() : 1f;
        float scale = mScaleFullIn ? Math.max(us, vs / y_scale) : Math.min(us, vs / y_scale);
        matrix.scale(x_scale * scale, y_scale * scale, 1f);
        matrix.rotate(-mDegree, 0, 0, 1);
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
    protected boolean needInitMsaaFbo() {
        return false;
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

    // 记录当前画幅比例
    private int mVideoFrameSize;

    /**
     * 设置画幅比例
     * @param size {@link FrameSizeType}
     */
    public void setVideoFrameSize(int size) {
        mVideoFrameSize = size;
    }

    // 记录当前旋转角度
    private float mDegree;

    /**
     * 顺时针方向为正，不能为负数
     */
    public void setRotation(@FloatRange(from = 0) float degree) {
        mDegree = (degree % 360f);
    }
}
