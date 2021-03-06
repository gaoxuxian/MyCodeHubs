package com.xx.avlibrary.gl.filter.rhythm;

import android.content.Context;
import android.opengl.GLES20;
import androidx.annotation.FloatRange;
import com.xx.avlibrary.gl.util.ByteBufferUtil;
import com.xx.avlibrary.gl.util.GLUtil;
import com.xx.avlibrary.gl.util.GlMatrixTools;
import com.xx.avlibrary.gl.filter.GLConstant;
import com.xx.avlibrary.gl.filter.GPUFilterType;
import com.xx.avlibrary.gl.filter.GPUImageFilter;

public class FrameSizeFilter extends GPUImageFilter {

    private int[] mFrameSizeTextureID;
    private FrameSizeHelper mHelper;

    public FrameSizeFilter(Context context) {
        super(context);
    }

    @Override
    public GPUFilterType getFilterType() {
        return null;
    }

    @Override
    protected void onInitBaseData() {
        super.onInitBaseData();

        mFrameSizeTextureID = new int[1];
        GLES20.glGenTextures(1, mFrameSizeTextureID, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameSizeTextureID[0]);
        GLUtil.bindTexture2DParams();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        mHelper = new FrameSizeHelper();
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
        GLES20.glViewport(0, 0, getFrameBufferW(), getFrameBufferH());
        // 矩阵变换
        GlMatrixTools matrix = getMatrix();
        matrix.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);

        int width = getFrameBufferW();
        int height = getFrameBufferH();
        if (width >= height) {
            float vs = (float) height / width;
            matrix.frustum(-1, 1, -vs, vs, 3, 7);
        } else {
            float us = (float) width / height;
            matrix.frustum(-us, us, -1, 1, 3, 7);
        }

        // 根据画幅, 调整纹理的顶点坐标
        performFrameSizeCalculation();

        int textureW = getTextureW();
        int textureH = getTextureH();

        matrix.pushMatrix();
        float x_scale = textureW >= textureH ? 1f : (float) textureW / textureH;
        float y_scale = textureH > textureW ? 1f : (float) textureH / textureW;
        // 根据画幅的近平面的顶点坐标, 计算纹理顶点坐标的缩放比例
        /*
        逻辑：顶点坐标的范围，就是纹理在三维坐标世界的绘制区域，那么换一个角度想，顶点与顶点之间的距离，就是纹理在三维坐标世界的宽高，
            所以这里的缩放关系是，### 原纹理的区域 要缩放到 近平面的区域 ###
         */
        float scale = 1f;
        float transX = 0f;
        float transY = 0f;
        if (mRequestAnim) {
            scale = mHelper.handleScaleFullInAnimation(getFrameBufferW(), getFrameBufferH(),
                    getTextureW(), getTextureH(), mVideoFrameSize, mCurrentScaleType, mNextScaleType, mDegree, mNextDegree);
        } else if (mRequestGesture) {
            mHelper.handleGesture(getFrameBufferW(), getFrameBufferH(), getTextureW(), getTextureH(), mVideoFrameSize, mCurrentScaleType, mDegree,
                    mTempGestureScale, mTempGestureTransX, mTempGestureTransY);
            scale = mHelper.getTempGestureScale();
            transX = mHelper.getTempGestureTransX();
            transY = mHelper.getTempGestureTransY();
        } else {
            scale = mHelper.handleStaticScale(getFrameBufferW(), getFrameBufferH(),
                    getTextureW(), getTextureH(), mVideoFrameSize, mCurrentScaleType, mDegree);
            transX = mHelper.handleStaticTranslationX(mCurrentScaleType);
            transY = mHelper.handleStaticTranslationY(mCurrentScaleType);
        }

        // GL 矩阵是前乘关系（粗暴理解，后写的代码先执行），旋转要考虑缩放问题
        /*
        逻辑：如何理解旋转要考虑缩放问题？要清楚旋转之前，纹理的宽高缩放比例是基于什么角度的！！！
            假设，原纹理属性 (基于0°时) textureW = 1, textureH = 0.5，而且原纹理需要被填充到(w = 3, h = 4)的区域中，非铺满填充 mScaleFullIn = false，
            现在要将原纹理 顺时针旋转 90°，那么旋转后纹理的属性应该发生变化 textureW = 0.5， textureH = 1，
            那么此时，应该1、先旋转后缩放，还是2、先缩放后旋转呢，其实选择1和2都可以，但是要根据不同的纹理属性来考虑：

            基于1、先旋转后缩放：正确代码应该是 基于旋转后的纹理属性(textureW = 0.5， textureH = 1)来做缩放
            matrix.scale();
            matrix.rotate();

            基于2、先缩放后旋转：正确代码应该是 基于旋转前的纹理属性(textureW = 1, textureH = 0.5)来做缩放
            matrix.rotate();
            matrix.scale();
         */
        matrix.translate(transX, transY, 0);
        matrix.rotate(-mDegree, 0, 0, 1);
        matrix.scale(x_scale * scale, y_scale * scale, 1f);
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }

    /**
     * 先通过 createGlTexture() 生成纹理, 再绘制正确方向
     *
     * @param textureID 要被绘制的纹理id
     * @return 绘制好的纹理ID
     */
    @Override
    public int onDrawBuffer(int textureID) {
        int outId = textureID;

        if (!GLES20.glIsProgram(getProgram())) {
            return outId;
        }
        if (mFrameBufferMgr != null) {
            mFrameBufferMgr.bindNext();
            mFrameBufferMgr.clearColor(true, true, true, true, true);
            mFrameBufferMgr.clearDepth(true, true);
            mFrameBufferMgr.clearStencil(true, true);

            draw(textureID, true);
            // 需要裁剪画幅
            if (mCutFrameSize) {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameSizeTextureID[0]);
                // 需要copy的区域
                int width = getFrameSizeW();
                int height = getFrameSizeH();
                // 开始copy的坐标起点
                int x = (getFrameBufferW() - width) / 2;
                int y = (getFrameBufferH() - height) / 2;
                // 从fbo中copy部分区域出来，copy到当前绑定的 texture2d 纹理
                if (mRecopy) {
                    GLES20.glCopyTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, x, y, width, height, 0);
                    mRecopy = false;
                } else {
                    GLES20.glCopyTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, x, y, width, height);
                }
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                setTextureWH(width, height);
                outId = mFrameSizeTextureID[0];
            } else {
                setTextureWH(getFrameBufferW(), getFrameBufferH());
                outId = mFrameBufferMgr.getCurrentTextureId();
            }
            mFrameBufferMgr.unbind();
            return outId;
        }
        return outId;
    }

    /**
     * 在比例不相等的情况下，无法直接绘制
     */
    @Override
    public void onDrawFrame(int textureID) {
    }

    @Override
    protected boolean needInitMsaaFbo() {
        return false;
    }

    @Override
    public void destroy() {
        super.destroy();

        if (mFrameSizeTextureID != null) {
            if (GLES20.glIsTexture(mFrameSizeTextureID[0])) {
                GLES20.glDeleteTextures(1, mFrameSizeTextureID, 0);
                mFrameSizeTextureID[0] = GLES20.GL_NONE;
            }
        }
    }

    private volatile int mNextScaleType;
    private volatile int mCurrentScaleType;

    public void requestToDoScaleAnim(int nextScaleType, float factor) {
        mRequestAnim = true;
        mNextScaleType = nextScaleType;
        if (mHelper != null) {
            mHelper.setAnimFactor(factor);
        }
    }

    /**
     * 设置图片缩放方式
     * <p>
     * //     * @param fullIn true-铺满(最短边顶边适配)，false-居中(最长边顶边适配)
     */
    public void setScaleType(int type) {
        mCurrentScaleType = type;
        mNextScaleType = type;
    }

    // 记录当前画幅比例
    private int mVideoFrameSize;
    private boolean mRecopy;

    /**
     * 设置画幅比例
     *
     * @param size {@link FrameSizeType}
     */
    public void setVideoFrameSize(int size) {
        if (mVideoFrameSize != size) {
            mVideoFrameSize = size;
            mRecopy = true;
        }
    }

    private volatile boolean mRequestAnim;
    private volatile boolean mRequestGesture;

    // 记录当前旋转角度
    private volatile float mDegree;

    /**
     * 顺时针方向为正，不能为负数
     */
    public void setRotation(@FloatRange(from = 0) float degree) {
        mDegree = (degree % 360f);
        mNextDegree = mDegree;
    }

    private volatile float mNextDegree;

    public void requestToRotateAnim(float nextDegree, float currentDegree, float factor) {
        mRequestAnim = true;
        mNextDegree = (nextDegree % 360f);
        mDegree = (currentDegree % 360f);
        if (mHelper != null) {
            mHelper.setAnimFactor(factor);
        }
    }

    public void releaseAnim() {
        mRequestAnim = false;
    }

    public void requestGesture() {
        mRequestGesture = true;
    }

    private volatile float mTempGestureScale = 1f;
    private volatile float mTempGestureTransX = 0f;
    private volatile float mTempGestureTransY = 0f;

    public void updateGestureData(float scale, float transX, float transY) {
        mTempGestureScale = scale;
        mTempGestureTransX = transX;
        mTempGestureTransY = transY;
    }

    public void releaseGesture() {
        mRequestGesture = false;
    }

    public void syncGestureData() {
        // TODO: 2019/5/3 更新手势处理后的数据, 方便下次使用
        mHelper.syncGestureHandledData();
        setScaleType(FrameSizeHelper.scale_type_gesture);
    }

    private volatile boolean mCutFrameSize;

    /**
     * 设置是否需要裁剪画幅区域
     *
     * @param needToCut true --> 根据画幅, 裁剪出具体区域, false -->
     */
    public void setFrameSizeCut(boolean needToCut) {
        mCutFrameSize = needToCut;
    }

    /**
     * 基于FrameBuffer宽高, 计算画幅宽高
     */
    private void performFrameSizeCalculation() {
        if (mFrameBufferMgr != null && mRecopy) {
            // 计算画幅
            float aspectRatio = FrameSizeType.getAspectRatio(mVideoFrameSize); // 宽高比
            int width = getFrameBufferW();
            int height = getFrameBufferH();
            int tempHeight = (int) (width / aspectRatio);
            if (tempHeight > height) {
                width = (int) (height * aspectRatio);
            } else {
                height = tempHeight;
            }
            setFrameSizeWH(width, height);
        }
    }

    private int mFrameSizeW; // 记录画幅宽
    private int mFrameSizeH; // 记录画幅高

    /**
     * 记录画幅宽高
     */
    private void setFrameSizeWH(int width, int height) {
        mFrameSizeW = width;
        mFrameSizeH = height;
    }

    /**
     * @return 画幅宽
     */
    private int getFrameSizeW() {
        return mFrameSizeW;
    }

    /**
     * @return 画幅高
     */
    private int getFrameSizeH() {
        return mFrameSizeH;
    }
}
