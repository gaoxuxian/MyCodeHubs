package lib.gl.filter.rhythm;

import android.graphics.Matrix;
import androidx.annotation.FloatRange;

/**
 * 额外做缩放、平移处理的类
 */
public final class FrameSizeHelper {

    public static final int scale_type_full_in = 9999;
    public static final int scale_type_not_full_in = 8888;
    public static final int scale_type_gesture = 7777;

    private Matrix mMatrix;
    private FrameSizeInfo mFrameSizeInfo;

    private static final float DEFAULT_MAX_SCALE_FACTOR = 1.5f;

    private float mMaxScaleFactor = DEFAULT_MAX_SCALE_FACTOR;

    public FrameSizeHelper() {
        mMatrix = new Matrix();
        mFrameSizeInfo = new FrameSizeInfo();
    }

    public void setMaxScaleFactor(@FloatRange(from = 1f) float factor) {
        mMaxScaleFactor = factor;
    }

    private volatile float mAnimFactor;

    public void setAnimFactor(float factor) {
        mAnimFactor = factor;
    }

    public float handleStaticScale(int viewportW, int viewportH, int textureW, int textureH, int frameSizeType,
                                   int currentScaleType, float currentDegree) {

        mFrameSizeInfo.frameSizeCalculation(viewportW, viewportH, frameSizeType);
        // 由于画幅永远都在viewport范围内，同步画幅在三维世界的坐标
        float frameSizeUS = mFrameSizeInfo.vertexUs;
        float frameSizeVS = mFrameSizeInfo.vertexVs;

        int textureWidth = textureW;
        int textureHeight = textureH;
        if (currentDegree == 90 || currentDegree == 270) {
            textureWidth = textureWidth + textureHeight;
            textureHeight = textureWidth - textureHeight;
            textureWidth = textureWidth - textureHeight;
        }

        float textureUS = textureWidth >= textureHeight ? 1f : (float) textureWidth / textureHeight;
        float textureVS = textureHeight > textureWidth ? 1f : (float) textureHeight / textureWidth;

        // 铺满的缩放比例
        float fullinScale = Math.max(frameSizeUS / textureUS, frameSizeVS / textureVS);
        // 不铺满的缩放比例
        float unfullInScale = Math.min(frameSizeUS / textureUS, frameSizeVS / textureVS);

        if (currentScaleType == scale_type_full_in) {
            return fullinScale;
        } else if (currentScaleType == scale_type_not_full_in) {
            return unfullInScale;
        } else if (currentScaleType == scale_type_gesture) {
            return mGestureScale;
        } else {
            return 1f;
        }
    }

    public float handleStaticTranslationX(int currentScaleType) {
        if (currentScaleType == scale_type_full_in) {
            return 0;
        } else if (currentScaleType == scale_type_not_full_in) {
            return 0;
        } else if (currentScaleType == scale_type_gesture) {
            return mTranslationX;
        } else {
            return 1f;
        }
    }

    public float handleStaticTranslationY(int currentScaleType) {
        if (currentScaleType == scale_type_full_in) {
            return 0;
        } else if (currentScaleType == scale_type_not_full_in) {
            return 0;
        } else if (currentScaleType == scale_type_gesture) {
            return mTranslationY;
        } else {
            return 1f;
        }
    }

    private float handleScaleFullInAnimation(int viewportW, int viewportH, int textureW, int textureH, int frameSizeType,
                                           int currentScaleType, int nextScaleType) {

        mFrameSizeInfo.frameSizeCalculation(viewportW, viewportH, frameSizeType);
        // 由于画幅永远都在viewport范围内，同步画幅在三维世界的坐标
        float frameSizeUS = mFrameSizeInfo.vertexUs;
        float frameSizeVS = mFrameSizeInfo.vertexVs;

        float textureUS = textureW >= textureH ? 1f : (float) textureW / textureH;
        float textureVS = textureH > textureW ? 1f : (float) textureH / textureW;

        // 铺满的缩放比例
        float fullinScale = Math.max(frameSizeUS / textureUS, frameSizeVS / textureVS);
        // 不铺满的缩放比例
        float unfullInScale = Math.min(frameSizeUS / textureUS, frameSizeVS / textureVS);

        if (currentScaleType == scale_type_full_in && nextScaleType == scale_type_not_full_in) {
            return fullinScale + (unfullInScale - fullinScale) * mAnimFactor;
        } else if (currentScaleType == scale_type_not_full_in && nextScaleType == scale_type_full_in) {
            return unfullInScale + (fullinScale - unfullInScale) * mAnimFactor;
        } else if (currentScaleType == scale_type_full_in && nextScaleType == scale_type_full_in) {
            return fullinScale;
        } else if (currentScaleType == scale_type_not_full_in && nextScaleType == scale_type_not_full_in) {
            return unfullInScale;
        } else if (currentScaleType == scale_type_gesture && nextScaleType == scale_type_full_in) {
            return mGestureScale + (fullinScale - mGestureScale) * mAnimFactor;
        } else if (currentScaleType == scale_type_gesture && nextScaleType == scale_type_not_full_in) {
            return mGestureScale + (unfullInScale - mGestureScale) * mAnimFactor;
        } else {
            return 1f;
        }
    }

    public float handleScaleFullInAnimation(int viewportW, int viewportH, int textureW, int textureH, int frameSizeType,
                                            int currentScaleType, int nextScaleType, float currentDegree, float nextDegree) {

        int tempW = textureW;
        int tempH = textureH;

        if ((currentDegree >= 90 && currentDegree < 180) || currentDegree >= 270) {
            tempW = tempW + tempH;
            tempH = tempW - tempH;
            tempW = tempW - tempH;
        }

        float currentDegreeScale = handleScaleFullInAnimation(viewportW, viewportH, tempW, tempH, frameSizeType, currentScaleType, nextScaleType);

        if (currentDegree != nextDegree) {
            tempW = textureW;
            tempH = textureH;
            if (nextDegree == 90 || nextDegree == 270) {
                tempW = tempW + tempH;
                tempH = tempW - tempH;
                tempW = tempW - tempH;
            }

            float nextDegreeScale = handleScaleFullInAnimation(viewportW, viewportH, tempW, tempH, frameSizeType, currentScaleType, nextScaleType);

            return currentDegreeScale + (nextDegreeScale - currentDegreeScale) * mAnimFactor;
        }

        return currentDegreeScale;
    }

    public void handleGesture(int viewportW, int viewportH, int textureW, int textureH, int frameSizeType,
                              int currentScaleType, float currentDegree,
                              float gestureScale, float gestureTransX, float gestureTransY) {

        mFrameSizeInfo.frameSizeCalculation(viewportW, viewportH, frameSizeType);
        // 由于画幅永远都在viewport范围内，同步画幅在三维世界的坐标
        float frameSizeUS = mFrameSizeInfo.vertexUs;
        float frameSizeVS = mFrameSizeInfo.vertexVs;

        int textureWidth = textureW;
        int textureHeight = textureH;
        if (currentDegree == 90 || currentDegree == 270) {
            textureWidth = textureWidth + textureHeight;
            textureHeight = textureWidth - textureHeight;
            textureWidth = textureWidth - textureHeight;
        }

        float textureUS = textureWidth >= textureHeight ? 1f : (float) textureWidth / textureHeight;
        float textureVS = textureHeight > textureWidth ? 1f : (float) textureHeight / textureWidth;

        // 铺满的缩放比例
        float fullinScale = Math.max(frameSizeUS / textureUS, frameSizeVS / textureVS);
        // 不铺满的缩放比例
        float unfullInScale = Math.min(frameSizeUS / textureUS, frameSizeVS / textureVS);

        float maxScale = fullinScale * mMaxScaleFactor;
        float minScale = unfullInScale;
        float scale = mGestureScale;
        if (currentScaleType == scale_type_full_in) {
            scale = fullinScale;
        } else if (currentScaleType == scale_type_not_full_in) {
            scale = unfullInScale;
        }

        if (scale * gestureScale > maxScale) {
            scale = maxScale;
        } else if (scale * gestureScale < minScale) {
            scale = minScale;
        } else {
            scale *= gestureScale;
        }

        mTempGestureScale = scale;

        float[] leftTop = new float[]{-textureUS * scale, textureVS * scale};
        float[] rightBottom = new float[]{textureUS * scale, -textureVS * scale};

        float width = rightBottom[0];
        float height = leftTop[1];

        float x = mTranslationX + gestureTransX;
        float y = mTranslationY - gestureTransY;

        mMatrix.reset();
        mMatrix.postTranslate(x, y);

        mMatrix.mapPoints(leftTop);
        mMatrix.mapPoints(rightBottom);

        float left = leftTop[0];
        float top = leftTop[1];
        float right = rightBottom[0];
        float bottom = rightBottom[1];

        // 可以左右滑
        if (width > frameSizeUS) {
            // 控制边界
            if (x > 0 && left > -frameSizeUS) {
                mTempGestureTransX = (x - (left + frameSizeUS));
            } else if (x < 0 && right < frameSizeUS) {
                mTempGestureTransX = (x - (right - frameSizeUS));
            } else {
                mTempGestureTransX = x;
            }
        } else {
            // 判断点是否在区域内, 居中显示
            float t = (left + right) / 2f;
            mTempGestureTransX = (x - t);
        }

        // 可以上下滑
        if (height > frameSizeVS) {
            // 控制边界
            if (y < 0 && top < frameSizeVS) {
                mTempGestureTransY = (y - (top - frameSizeVS));
            } else if (y > 0 && bottom > -frameSizeVS) {
                mTempGestureTransY = (y - (bottom + frameSizeVS));
            } else {
                mTempGestureTransY = y;
            }
        } else {
            // 判断点是否在区域内, 居中显示
            float t = (top + bottom) / 2f;
            mTempGestureTransY = (y - t);
        }
    }

    public float getTempGestureScale() {
        return mTempGestureScale;
    }

    public void syncGestureHandledData() {
        mGestureScale = mTempGestureScale;
        mTranslationX = mTempGestureTransX;
        mTranslationY = mTempGestureTransY;
    }

    private float mTempGestureTransX;
    private float mTempGestureTransY;

    public float getTempGestureTransX() {
        return mTempGestureTransX;
    }

    public float getTempGestureTransY() {
        return mTempGestureTransY;
    }

    private float mTempGestureScale = 1f;

    private volatile float mGestureScale = 1f;

    private float mTranslationX;
    private float mTranslationY;

    private static class FrameSizeInfo {
        int width; // 真实宽
        int height; // 真实高
        float vertexUs; // 顶点坐标x轴位置
        float vertexVs; // 顶点坐标y轴位置

        public void frameSizeCalculation(int viewportW, int viewportH, int frameSizeType) {
            float frameSizeAspectRatio = FrameSizeType.getAspectRatio(frameSizeType);
            int frameSizeW = viewportW;
            int frameSizeH = viewportH;
            // 由于画幅永远都在viewport范围内，同步画幅在三维世界的坐标
            float frameSizeUS = 1f;
            float frameSizeVS = (float) viewportH / viewportW;

            if (frameSizeAspectRatio != 0) {
                // 计算画幅宽高
                int tempH = (int) (viewportW / frameSizeAspectRatio);
                if (tempH > frameSizeH) {
                    frameSizeW = (int) (viewportH * frameSizeAspectRatio);
                } else {
                    frameSizeH = tempH;
                }

                frameSizeVS = 1f;
                if (frameSizeH > frameSizeW) {
                    frameSizeUS = (float) frameSizeW / frameSizeH;
                } else {
                    frameSizeVS = (float) frameSizeH / frameSizeW;
                }
            }

            this.width = frameSizeW;
            this.height = frameSizeH;
            this.vertexUs = frameSizeUS;
            this.vertexVs = frameSizeVS;
        }
    }
}
