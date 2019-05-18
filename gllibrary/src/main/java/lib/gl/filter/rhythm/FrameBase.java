package lib.gl.filter.rhythm;

import java.io.Serializable;

public interface FrameBase extends Serializable {
    int scale_type_full_in = 1;
    int scale_type_not_full_in = 2;

    void setDegree(float degree);

    float getDegree();

    int getScaleType();

    void setScaleType(int type);

    void setGestureScale(float scale);

    float getGestureScale();

    void setGestureTranslation(float x, float y);

    float getGestureTranslationX();

    float getGestureTranslationY();

    void resetGestureParams();
}
