package lib.gl.filter.rhythm;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * 额外做缩放、平移处理的类
 */
public class FrameSizeHelper {

    public static final int scale_type_full_in = 9999;
    public static final int scale_type_not_full_in = 8888;
    public static final int scale_type_gesture = 7777;

    private Matrix mMatrix;

    public FrameSizeHelper() {
        mMatrix = new Matrix();
    }

    private volatile float mAnimFactor;

    public void setAnimFactor(float factor) {
        mAnimFactor = factor;
    }

    public float handleStaticScale(int viewportW, int viewportH, int textureW, int textureH, int frameSizeType,
                                   int currentScaleType, float currentDegree) {

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
                frameSizeH = viewportH;
            }

            frameSizeVS = 1f;
            if (frameSizeH > frameSizeW) {
                frameSizeUS = (float) frameSizeW / frameSizeH;
            } else {
                frameSizeVS = (float) frameSizeH / frameSizeW;
            }
        }

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

    public float handleScaleFullInAnimation(int viewportW, int viewportH, int textureW, int textureH, int frameSizeType,
                                           int currentScaleType, int nextScaleType) {

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
                frameSizeH = viewportH;
            }

            frameSizeVS = 1f;
            if (frameSizeH > frameSizeW) {
                frameSizeUS = (float) frameSizeW / frameSizeH;
            } else {
                frameSizeVS = (float) frameSizeH / frameSizeW;
            }
        }

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

        int width = textureW;
        int height = textureH;

        int tempW = width;
        int tempH = height;

        if ((currentDegree >= 90 && currentDegree < 180) || currentDegree >= 270) {
            tempW = tempW + tempH;
            tempH = tempW - tempH;
            tempW = tempW - tempH;
        }

        float currentDegreeScale = handleScaleFullInAnimation(viewportW, viewportH, tempW, tempH, frameSizeType, currentScaleType, nextScaleType);

        if (currentDegree != nextDegree) {
            tempW = width;
            tempH = height;
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
                frameSizeH = viewportH;
            }

            frameSizeVS = 1f;
            if (frameSizeH > frameSizeW) {
                frameSizeUS = (float) frameSizeW / frameSizeH;
            } else {
                frameSizeVS = (float) frameSizeH / frameSizeW;
            }
        }

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

        float maxScale = fullinScale * 1.5f;
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

//        mMatrix.postScale(scale, scale, 0, 0);
    }

    public float getTempGestureScale() {
        return mTempGestureScale;
    }

    public void syncGestureHandledData() {
        mGestureScale = mTempGestureScale;
    }

    public void clearGestureData() {
        mGestureScale = 1f;
        mTempGestureScale = 1f;
    }

    private float mTempGestureScale = 1f;

    private volatile float mGestureScale = 1f;

    private float mTranslationX;
    private float mTranslationY;

    public void setTranslation(float x, float y) {
        mTranslationX = x;
        mTranslationY = y;
    }
}
