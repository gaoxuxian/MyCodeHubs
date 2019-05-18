package lib.gl.filter.rhythm;


/**
 * 记录帧额外的属性
 */
public final class GLFrame implements FrameBase {

    private static final long serialVersionUID = -5761530516027594902L;
    private float mDegree = 0;
    private float mTranslationX = 0;
    private float mTranslationY = 0;
    private float mGestureScale = 1;

    private int mScaleType = scale_type_full_in;

    @Override
    public void setDegree(float degree) {
        mDegree = degree;
    }

    @Override
    public float getDegree() {
        return mDegree;
    }

    @Override
    public int getScaleType() {
        return mScaleType;
    }

    @Override
    public void setScaleType(int type) {
        mScaleType = type;
    }

    @Override
    public void setGestureScale(float scale) {
        mGestureScale = scale;
    }

    @Override
    public float getGestureScale() {
        return mGestureScale;
    }

    @Override
    public void setGestureTranslation(float x, float y) {
        mTranslationX = x;
        mTranslationY = y;
    }

    @Override
    public float getGestureTranslationX() {
        return mTranslationX;
    }

    @Override
    public float getGestureTranslationY() {
        return mTranslationY;
    }

    @Override
    public void resetGestureParams() {
        mGestureScale = 1f;
        mTranslationX = 0;
        mTranslationY = 0;
    }
}
