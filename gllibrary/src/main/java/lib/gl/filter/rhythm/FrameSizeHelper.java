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

    private Matrix mMatrix;

    public FrameSizeHelper() {
        mMatrix = new Matrix();
    }

    private PointF mLTPoint = new PointF();
    private PointF mRTPoint = new PointF();
    private PointF mLBPoint = new PointF();
    private PointF mRBPoint = new PointF();

    public void setTextureVertex(float left, float right, float top, float bottom) {
        mLTPoint.set(left, top);
        mRTPoint.set(right, top);
        mLBPoint.set(left, bottom);
        mRBPoint.set(right, bottom);
    }

    private RectF mFrameSizeRect = new RectF();

    public void setFrameSizeVertex(float left, float right, float top, float bottom) {
        mFrameSizeRect.set(left, top, right, bottom);
    }

    private float mScale = 1f;

    public void setScale(float scale) {
        mScale = scale;
    }

    private float mTranslationX;
    private float mTranslationY;

    public void setTranslation(float x, float y) {
        mTranslationX = x;
        mTranslationY = y;
    }

    private volatile float mAnimFactor;

    public void setAnimFactor(float factor) {
        mAnimFactor = factor;
    }

    private float mScaleX = 1f;
    private float mScaleY = 1f;

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

    public float getScaleX() {
        return mScaleX;
    }

    public float getScaleY() {
        return mScaleY;
    }

    public void requestToCalculation() {
        float scale = mScale;
        mMatrix.postScale(scale, scale, 0, 0);
        float x = mTranslationX;
        float y = mTranslationY;
        mMatrix.postTranslate(x, y);
    }
}
