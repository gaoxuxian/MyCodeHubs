package lib.gl.filter.rhythm;

import android.content.Context;
import android.opengl.GLES20;
import lib.gl.filter.GLConstant;
import lib.gl.filter.GPUFilterType;
import lib.gl.filter.GPUImageFilter;
import lib.gl.util.ByteBufferUtil;
import lib.gl.util.GLUtil;
import lib.gl.util.GlMatrixTools;

public class FrameSizeFilterV2 extends GPUImageFilter {

    private int[] mFrameSizeTextureID;
    private FrameLogicExecutor mHelper;
    private FrameBase mFrameBase;

    public FrameSizeFilterV2(Context context, FrameLogicExecutor executor) {
        super(context);
        mHelper = executor;
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
        float vs = (float) height / width;
        matrix.frustum(-1, 1, -vs, vs, 3, 7);

        // 根据画幅, 调整纹理的顶点坐标
        performFrameSizeCalculation();

        int textureW = getTextureW();
        int textureH = getTextureH();

        matrix.pushMatrix();
        float x_scale = 1f;
        float y_scale = (float) textureH / textureW;

        mHelper.executeLogic(getFrameBufferW(), getFrameBufferH(), textureW, textureH, mVideoFrameSize, mFrameBase);
        float scale = mHelper.getScale();
        float transX = mHelper.getTranslationX();
        float transY = mHelper.getTranslationY();
        float degree = -mHelper.getDegree();

        matrix.translate(transX, transY, 0);
        matrix.scale(scale, scale, 1f);
        matrix.rotate(degree, 0, 0, 1);
        matrix.scale(x_scale, y_scale, 1f);
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
//            GLES20.glClearColor(1, 1, 1, 1);
//            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

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

    @Override
    public void onSurfaceChanged(int width, int height) {
        super.onSurfaceChanged(width, height);

        mRecopy = true;
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

    private volatile boolean mCutFrameSize;

    /**
     * 设置是否需要裁剪画幅区域
     *
     * @param needToCut true --> 根据画幅, 裁剪出具体区域, false -->
     */
    public void setFrameSizeCut(boolean needToCut) {
        mCutFrameSize = needToCut;
    }

    public void setFrameBase(FrameBase base) {
        mFrameBase = base;
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
