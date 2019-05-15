package lib.gl.filter.rhythm;

import java.io.Serializable;

public interface FrameBase extends Serializable {
    int scale_type_full_in = 1;
    int scale_type_not_full_in = 2;
    int scale_type_gesture = 3;

    void setDegree(float degree);

    float getDegree();

    int getScaleType();

    void setScaleType(int type);

    void setScale(float scale);

    float getScale();

    void setTranslation(float x, float y);

    float getTranslationX();

    float getTranslationY();
}
