package filter;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/24.
 */
public interface FilterIF<T extends FilterType>
{
    T getFilterType();

    void onSurfaceCreated(EGLConfig config);

    void onSurfaceChanged(int width, int height);

    void initFrameBuffer(int width, int height);
}
