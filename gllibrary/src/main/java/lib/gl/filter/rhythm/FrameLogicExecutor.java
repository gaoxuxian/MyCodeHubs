package lib.gl.filter.rhythm;

import android.graphics.Matrix;
import androidx.annotation.FloatRange;

public class FrameLogicExecutor {

    // 提供给 filter 绘制的临时参数
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
        mMatrix2 = new Matrix();
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
        handleGestureScaleLogic(viewportW, viewportH, textureW, textureH, frameType, baseInfo);
        handleStaticScaleLogic(viewportW, viewportH, textureW, textureH, frameType, baseInfo);
    }

    private void handleAnimScaleLogic(int viewportW, int viewportH, int textureW, int textureH, int frameType, FrameBase baseInfo) {
        if (mRequestAnim) {
            int primitiveScaleType = baseInfo.getScaleType();
            float primitiveDegree = baseInfo.getDegree();
            float primitiveGestureOrgScale = baseInfo.getGestureScale();
            float primitiveGestureTranslationX = baseInfo.getGestureTranslationX();
            float primitiveGestureTranslationY = baseInfo.getGestureTranslationY();

            float primitiveScale = performScaleLogicV2(viewportW, viewportH, textureW, textureH, frameType,
                    primitiveScaleType, primitiveDegree, primitiveGestureOrgScale, 1f);
            float[] primitiveTrans = performTranslationLogic(viewportW, viewportH, textureW, textureH, frameType, primitiveDegree,
                    primitiveScale, primitiveGestureTranslationX, 0,
                    primitiveGestureTranslationY, 0);

            float newDegree = primitiveDegree + mAnimSweptAngle;
            int newScaleType = mAnimNextScaleType == 0 ? baseInfo.getScaleType() : mAnimNextScaleType;
            float newGestureOrgScale = 1f;
            float newGestureTranslationX = -primitiveGestureTranslationX;
            float newGestureTranslationY = -primitiveGestureTranslationY;

            float newScale = performScaleLogicV2(viewportW, viewportH, textureW, textureH, frameType,
                    newScaleType, newDegree, newGestureOrgScale, 1f);
            float[] newTrans = performTranslationLogic(viewportW, viewportH, textureW, textureH, frameType,
                    newDegree, newScale, primitiveGestureTranslationX, newGestureTranslationX,
                    primitiveGestureTranslationY, newGestureTranslationY);

            float factor = mAnimFactor;
            mTempScale = primitiveScale + (newScale - primitiveScale) * factor;
            mTempTranslationX = primitiveTrans[0] + (newTrans[0] - primitiveTrans[0]) * factor;
            mTempTranslationY = primitiveTrans[1] + (newTrans[1] - primitiveTrans[1]) * factor;
            mTempDegree = primitiveDegree + (newDegree - primitiveDegree) * factor;
        }
    }

    private void handleGestureScaleLogic(int viewportW, int viewportH, int textureW, int textureH, int frameType, FrameBase baseInfo) {
        if (mRequestGesture) {
            /*
            逻辑流程：
                1、计算出原图基于画幅的最大、最小缩放比例
                2、根据 baseInfo 记录的以往手势数据 + 当前的手势数据，重新计算位置
             */

            float gestureUpdateScale = mGestureScale;
            float gestureTransX = mGestureTransX;
            float gestureTransY = mGestureTransY;

            int scaleType = baseInfo.getScaleType();
            float degree = baseInfo.getDegree();
            float gestureOrgScale = baseInfo.getGestureScale();
            float gestureOrgTranslationX = baseInfo.getGestureTranslationX();
            float gestureOrgTranslationY = baseInfo.getGestureTranslationY();
            mTempScale = performScaleLogicV2(viewportW, viewportH, textureW, textureH, frameType, scaleType,
                    degree, gestureOrgScale, gestureUpdateScale);
            float[] trans = performTranslationLogic(viewportW, viewportH, textureW, textureH, frameType,
                    degree, mTempScale, gestureOrgTranslationX, gestureTransX, gestureOrgTranslationY, gestureTransY);
            mTempTranslationX = trans[0];
            mTempTranslationY = trans[1];
        }
    }

    private void handleStaticScaleLogic(int viewportW, int viewportH, int textureW, int textureH, int frameType, FrameBase baseInfo) {
        if (!mRequestGesture && !mRequestAnim) {
            float degree = baseInfo.getDegree();
            int scaleType = baseInfo.getScaleType();
            float gestureOrgScale = baseInfo.getGestureScale();
            float gestureTranslationX = baseInfo.getGestureTranslationX();
            float gestureTranslationY = baseInfo.getGestureTranslationY();
            float scale = performScaleLogicV2(viewportW, viewportH, textureW, textureH, frameType, scaleType,
                    degree, gestureOrgScale, 1f);
            float[] trans = performTranslationLogic(viewportW, viewportH, textureW, textureH, frameType, degree, scale,
                    gestureTranslationX, 0, gestureTranslationY, 0);
            mTempScale = scale;
            mTempTranslationX = trans[0];
            mTempTranslationY = trans[1];
            mTempDegree = degree;
        }
    }

    private Matrix mMatrix2;
    // 最终需要被记录的手势缩放量
    private float mDstGestureScale = 1;

    private float performScaleLogicV2(int viewportW, int viewportH, int textureW, int textureH, int frameType, int scaleType,
                                      float degree, float gestureOrgScale, float gestureUpdateScale) {
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

        float vx = 1f;
        float vy = (float) viewportH / viewportW;

        // step two
        degree = Math.abs(degree) % 360;
        if (degree == 90 || degree == 270) {
            float x = textureX;
            textureX = textureY;
            textureY = x;
        }

        // step three
        mFrameInfo.frameSizeCalculation(viewportW, viewportH, frameType);
        float frameX = 1f;
        float frameY = mFrameInfo.getAspectRatio();
        float sf = Math.min(vx / frameX, vy / frameY);

        // step four

        // 确定画幅区域
        frameX *= sf;
        frameY *= sf;

        float min = Math.min(frameX / textureX, frameY / textureY);
        float max = Math.max(frameX / textureX, frameY / textureY);

        if (scaleType == FrameBase.scale_type_full_in) {
            out = max;
        } else if (scaleType == FrameBase.scale_type_not_full_in) {
            out = min;
        }

        // 额外缩放 -- 手势处理
        mDstGestureScale = gestureOrgScale * gestureUpdateScale;
        float temp = out * mDstGestureScale;
        if (temp > max * 2f) {
            out = max * 2f;
            mDstGestureScale *= (max * 2f) / temp;
        } else if (temp < min) {
            out = min;
            mDstGestureScale *= min / temp;
        } else {
            out = temp;
        }
        return out;
    }

    // 最终需要被记录的手势平移量
    private float mDstGestureTransX;
    private float mDstGestureTransY;

    private float[] performTranslationLogic(int viewportW, int viewportH, int textureW, int textureH, int frameType,
                                          float degree, float extraScale, float orgTransX, float extraTransX,
                                            float orgTransY, float extraTransY) {
        float[] out = new float[2];

        float vx = 1f;
        float vy = (float) viewportH / viewportW;

        mFrameInfo.frameSizeCalculation(viewportW, viewportH, frameType);
        float frameX = 1f;
        float frameY = mFrameInfo.getAspectRatio();
        float sf = Math.min(vx / frameX, vy / frameY);

        // 确定画幅区域
        frameX *= sf;
        frameY *= sf;

        mTextureInfo.setWidthHeight(textureW, textureH);
        float textureX = 1f;
        float textureY = mTextureInfo.getAspectRatio();

        degree = Math.abs(degree) % 360;
        if (degree == 90 || degree == 270) {
            float x = textureX;
            textureX = textureY;
            textureY = x;
        }

        float x = textureX;
        float y = textureY;

        float[] leftTop = new float[]{-x, y};
        float[] rightBottom = new float[]{x, -y};

        float transX = orgTransX + extraTransX;
        float transY = orgTransY + extraTransY;
        float scale = extraScale;

        mMatrix2.reset();
        mMatrix2.postScale(scale, scale);
        mMatrix2.postTranslate(transX, transY);
        mMatrix2.mapPoints(leftTop);
        mMatrix2.mapPoints(rightBottom);

        float left = leftTop[0];
        float top = leftTop[1];
        float right = rightBottom[0];
        float bottom = rightBottom[1];

        float width = (right - left) / 2;
        float height = (top - bottom) / 2;

        if (width > frameX) {
            // 控制边界
            if (transX > 0 && left > -frameX) {
                out[0] = (transX - (left + frameX));
            } else if (transX < 0 && right < frameX) {
                out[0] = (transX - (right - frameX));
            } else {
                out[0] = transX;
            }
        } else {
            // 判断点是否在区域内, 居中显示
            float t = (left + right) / 2f;
            out[0] = (transX - t);
        }

        if (height > frameY) {
            // 控制边界
            if (transY < 0 && top < frameY) {
                out[1] = (transY - (top - frameY));
            } else if (transY > 0 && bottom > -frameY) {
                out[1] = (transY - (bottom + frameY));
            } else {
                out[1] = transY;
            }
        } else {
            // 判断点是否在区域内, 居中显示
            float t = (top + bottom) / 2f;
            out[1] = (transY - t);
        }

        mDstGestureTransX = out[0];
        mDstGestureTransY = out[1];
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
        mGestureTransY = -transY; // android view touch y差值 和 gl y方向 相反
    }

    private volatile boolean mRequestGesture;

    public void requestGesture() {
        mRequestGesture = true;
    }

    public void syncGestureScale(FrameBase base) {
        if (mRequestGesture) {
            synchronized (object_lock) {
                if (base != null) {
                    base.setGestureScale(mDstGestureScale);
                }
                mGestureScale = 1f;
            }
        }
    }

    public void syncGestureTranslation(FrameBase base) {
        if (mRequestGesture) {
            synchronized (object_lock) {
                if (base != null) {
                    base.setGestureTranslation(mDstGestureTransX, mDstGestureTransY);
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
                    base.setGestureTranslation(mDstGestureTransX, mDstGestureTransY);
                    base.setGestureScale(mDstGestureScale);
                }
                mGestureScale = 1f;
                mGestureTransX = 0;
                mGestureTransY = 0;

                mDstGestureScale = 1f;
                mDstGestureTransX = 0;
                mDstGestureTransY = 0;
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

        void setWidthHeight(int width, int height) {
            this.width = width;
            this.height = height;
        }

        float getAspectRatio() {
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

        void frameSizeCalculation(int viewportW, int viewportH, int frameSizeType) {
            this.width = viewportW;
            this.height = viewportH;
            float frameSizeAspectRatio = FrameSizeType.getAspectRatio(frameSizeType);
            if (frameSizeAspectRatio != 0) {
                // 计算画幅宽高
                height = (int) (viewportW / frameSizeAspectRatio);
            }
        }

        float getAspectRatio() {
            if (width == 0 || height == 0) {
                return 0;
            }

            return (float) height / width;
        }
    }
}
