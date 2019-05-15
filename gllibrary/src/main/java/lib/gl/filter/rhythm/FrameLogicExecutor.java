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
        handleAnimScaleLogic(viewportW, viewportH, textureW, textureH, frameType, baseInfo);
        handleGestureScaleLogic(viewportW, viewportH, textureW, textureH, frameType, baseInfo, mGestureScale, mGestureTransX, mGestureTransY);
        handleStaticScaleLogic(viewportW, viewportH, textureW, textureH, frameType, baseInfo);
    }

    private void handleAnimScaleLogic(int viewportW, int viewportH, int textureW, int textureH, int frameType, FrameBase baseInfo) {
        if (mRequestAnim) {
            float primitiveDegree = baseInfo.getDegree();
            float newDegree = primitiveDegree + mAnimSweptAngle;
            int primitiveScaleType = baseInfo.getScaleType();
            int newScaleType = mAnimNextScaleType == 0 ? baseInfo.getScaleType() : mAnimNextScaleType;
            float primitiveScale;
            if (primitiveScaleType == FrameBase.scale_type_gesture) {
                primitiveScale = baseInfo.getScale(); // FIXME: 2019/5/15 涉及手势处理的，需要重新计算
            } else {
                primitiveScale = performScaleLogic(viewportW, viewportH, textureW, textureH, frameType, primitiveDegree, primitiveScaleType);
            }
            float newScale = performScaleLogic(viewportW, viewportH, textureW, textureH, frameType, newDegree, newScaleType);

            float factor = mAnimFactor;
            mTempScale = primitiveScale + (newScale - primitiveScale) * factor;
            mTempTranslationX = 0;
            mTempTranslationY = 0;
            mTempDegree = primitiveDegree + (newDegree - primitiveDegree) * factor;
        }
    }

    private void handleGestureScaleLogic(int viewportW, int viewportH, int textureW, int textureH, int frameType, FrameBase baseInfo,
                                         float gestureScale, float gestureTransX, float gestureTransY) {
        if (mRequestGesture) {
            /*
            逻辑流程：
                1、计算出原图基于画幅的最大、最小缩放比例
                2、根据 baseInfo 记录的以往手势数据 + 当前的手势数据，重新计算位置
             */

            // step one
            float degree = baseInfo.getDegree();
            int scaleType = FrameBase.scale_type_not_full_in;
            float min = performScaleLogic(viewportW, viewportH, textureW, textureH, frameType, degree, scaleType);
            scaleType = FrameBase.scale_type_full_in;
            float max = performScaleLogic(viewportW, viewportH, textureW, textureH, frameType, degree, scaleType);

            // step two
            float requestScale = baseInfo.getScale() * gestureScale;
            float requestTranslationX = baseInfo.getTranslationX() + gestureTransX;
            float requestTranslationY = baseInfo.getTranslationY() + gestureTransY;

            // 由于FrameBase 是默认铺满, 每次的手势缩放比例，都是基于铺满来计算的

        }
    }

    private void handleStaticScaleLogic(int viewportW, int viewportH, int textureW, int textureH, int frameType, FrameBase baseInfo) {
        if (!mRequestGesture && !mRequestAnim) {
            float degree = baseInfo.getDegree();
            int scaleType = baseInfo.getScaleType();
            float translationX = baseInfo.getTranslationX();
            float translationY = baseInfo.getTranslationY();
            float scale;
            if (scaleType == FrameBase.scale_type_gesture) {
                scale = baseInfo.getScale(); // FIXME: 2019/5/15 涉及手势处理的，需要重新计算
            } else {
                scale = performScaleLogic(viewportW, viewportH, textureW, textureH, frameType, degree, scaleType);
            }
            mTempScale = scale;
            mTempTranslationX = translationX;
            mTempTranslationY = translationY;
            mTempDegree = degree;
        }
    }

    private float performScaleLogic(int viewportW, int viewportH, int textureW, int textureH, int frameType, float degree, int scaleType) {
        /*
        逻辑流程：
            1、计算原图基于 viewport 的缩放比例，基于纹理长边缩放 St1 （目的是为了建立一个基准）
            2、计算原图在 baseInfo 的旋转角度下的宽高 （旋转后，铺满与不铺满变换的关键操作）
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

    private volatile float mGestureTransX;
    private volatile float mGestureTransY;
    private volatile float mGestureScale = 1f;

    public void updateGestureScale(float scale) {
        mGestureScale = scale;
    }

    public void updateGestureTranslation(float transX, float transY) {
        mGestureTransX = transX;
        mGestureTransY = transY;
    }

    private volatile boolean mRequestGesture;

    public void requestGesture() {
        mRequestGesture = true;
    }

    public void syncGestureScale(FrameBase base) {
        if (mRequestGesture) {
            synchronized (object_lock) {
                if (base != null) {
                    base.setScale(mTempScale);
                    base.setScaleType(FrameBase.scale_type_gesture);
                }
                mGestureScale = 1f;
            }
        }
    }

    public void syncGestureTranslation(FrameBase base) {
        if (mRequestGesture) {
            synchronized (object_lock) {
                if (base != null) {
                    base.setTranslation(mTempTranslationX, mTempTranslationY);
                    base.setScaleType(FrameBase.scale_type_gesture);
                }
                mGestureTransX = 0;
                mGestureTransY = 0;
            }
        }
    }

    public void releaseGesture(boolean sync, FrameBase base) {
        synchronized (object_lock) {
            if (sync) {
                if (base != null) {
                    base.setTranslation(mTempTranslationX, mTempTranslationY);
                    base.setScale(mTempScale);
                    base.setScaleType(FrameBase.scale_type_gesture);
                }
                mGestureScale = 1f;
                mGestureTransX = 0;
                mGestureTransY = 0;
            }
            mRequestGesture = false;
        }
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
