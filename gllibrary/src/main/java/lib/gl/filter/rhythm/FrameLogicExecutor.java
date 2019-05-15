package lib.gl.filter.rhythm;

import androidx.annotation.FloatRange;

public class FrameLogicExecutor {

    private float mTempScale = 1f;
    private float mTempTranslationX;
    private float mTempTranslationY;
    private float mTempDegree;

    private TextureInfo mTextureInfo;
    private FrameInfo mFrameInfo;

    private final Object object_lock = new Object();

    public FrameLogicExecutor() {
        mTextureInfo = new TextureInfo();
        mFrameInfo = new FrameInfo();
    }

    /**
     * 执行逻辑计算
     * @param viewportW 视口宽
     * @param viewportH 视口高
     * @param textureW  纹理宽
     * @param textureH  纹理高
     * @param frameType 画幅类型 {@link FrameSizeType}
     * @param baseInfo  当前纹理的额外信息，ex 旋转角度、手势处理数据
     */
    public void executeLogic(int viewportW, int viewportH, int textureW, int textureH, int frameType, FrameBase baseInfo) {
        if (mRequestAnim) {
            float primitiveDegree = baseInfo.getDegree();
            float newDegree = primitiveDegree + mAnimSweptAngle;
            int primitiveScaleType = baseInfo.getScaleType();
            int newScaleType = mAnimNextScaleType == 0 ? baseInfo.getScaleType() : mAnimNextScaleType;

            float primitiveScale = performScaleLogic(viewportW, viewportH, textureW, textureH, frameType, primitiveDegree, primitiveScaleType);
            float newScale = performScaleLogic(viewportW, viewportH, textureW, textureH, frameType, newDegree, newScaleType);

            float factor = mAnimFactor;
            mTempScale = primitiveScale + (newScale - primitiveScale) * factor;
            mTempTranslationX = 0;
            mTempTranslationY = 0;
            mTempDegree = primitiveDegree + (newDegree - primitiveDegree) * factor;
        } else {
            float degree = baseInfo.getDegree();
            int scaleType = baseInfo.getScaleType();
            float translationX = baseInfo.getTranslationX();
            float translationY = baseInfo.getTranslationY();
            float scale = performScaleLogic(viewportW, viewportH, textureW, textureH, frameType, degree, scaleType);
            mTempScale = scale;
            mTempTranslationX = translationX;
            mTempTranslationY = translationY;
            mTempDegree = degree;
        }
    }

    private float performScaleLogic(int viewportW, int viewportH, int textureW, int textureH, int frameType, float degree, int scaleType) {
        /*
        逻辑流程：
            1、计算原图基于 viewport 的缩放比例，基于纹理长边缩放 St1
            2、计算原图在 baseInfo 的旋转角度下的宽高
            3、计算画幅大小，同时计算画幅基于 viewport 的缩放比例，基于长边缩放 Sf
            4、基于步骤3的状态下，再计算纹理缩放到画幅区域的比例 St3
            5、汇总原图在步骤3、5之后的总缩放量 St = St1 * St3
         */

        float out = 0;

        // step one
        mTextureInfo.setWidthHeight(textureW, textureH);
        float textureX = 1f;
        float textureY = mTextureInfo.getAspectRatio();
        float primitiveTextureX = textureX;
        float primitiveTextureY = textureY;

        float vx = 1f;
        float vy = (float) viewportH / viewportW;

        float scale = 1f;
        float st1 = Math.min(vx / textureX, vy / textureY);
        scale *= st1;

        // step two
        degree = Math.abs(degree) % 360;
        if (degree == 90 || degree == 270) {
            primitiveTextureX = textureY;
            primitiveTextureY = textureX;
        }

        // step three
        mFrameInfo.frameSizeCalculation(viewportW, viewportH, frameType);
        float frameX = 1f;
        float frameY = (float) mFrameInfo.height / mFrameInfo.width;
        float sf = Math.min(vx / frameX, vy / frameY);

        // step four

        // 确定画幅区域
        frameX *= sf;
        frameY *= sf;

        // 确定纹理区域
        primitiveTextureX *= scale;
        primitiveTextureY *= scale;

        float min = Math.min(frameX / primitiveTextureX, frameY / primitiveTextureY);
        float max = Math.max(frameX / primitiveTextureX, frameY / primitiveTextureY);

        if (scaleType == FrameBase.scale_type_full_in) {
            out = scale * max;
        } else if (scaleType == FrameBase.scale_type_not_full_in) {
            out = scale * min;
        }
        return out;
    }

    public float getScale() {
        return mTempScale;
    }

    public float getTranslationX() {
        return mTempTranslationX;
    }

    public float getTranslationY() {
        return mTempTranslationY;
    }

    public float getDegree() {
        return mTempDegree;
    }

    private volatile boolean mRequestAnim;

    public void requestAnim() {
        mRequestAnim = true;
    }

    private volatile float mAnimFactor;

    public void updateAnimFactor(float factor) {
        mAnimFactor = factor;
    }

    public void releaseAnim() {
        mRequestAnim = false;

//        synchronized (object_lock) {
//            if (mFrameBase != null) {
//                mFrameBase.setDegree((mFrameBase.getDegree() + mAnimSweptAngle) % 360f);
//                mFrameBase.setScaleType(mAnimNextScaleType);
//            }
//        }

        mAnimSweptAngle = 0;
    }

    private volatile float mAnimSweptAngle;
    private volatile int mAnimNextScaleType;

    /**
     * 顺时针方向为正
     */
    public void setSweptAngle(@FloatRange(from = 0) float sweptAngle) {
        mAnimSweptAngle = sweptAngle;
    }

    /**
     * 设置图片缩放方式
     * @param type {@link FrameBase#scale_type_full_in}
     */
    public void setScaleType(int type) {
        mAnimNextScaleType = type;
    }

    /**
     * 记录texture信息
     */
    private static class TextureInfo {
        // 不一定是图片的真实宽高，但比例一定不能错
        int width;
        int height;

        public void setWidthHeight(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public float getAspectRatio() {
            if (width == 0 || height == 0) {
                return 0;
            }

            return (float) height / width;
        }
    }

    /**
     * 记录画幅信息
     */
    private static class FrameInfo {
        int width; // 真实宽
        int height; // 真实高

        public void frameSizeCalculation(int viewportW, int viewportH, int frameSizeType) {
            this.width = viewportW;
            this.height = viewportH;
            float frameSizeAspectRatio = FrameSizeType.getAspectRatio(frameSizeType);
            if (frameSizeAspectRatio != 0) {
                // 计算画幅宽高
                height = (int) (viewportW / frameSizeAspectRatio);
            }
        }
    }
}
