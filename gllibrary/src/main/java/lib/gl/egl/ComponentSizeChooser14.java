package lib.gl.egl;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLDisplay;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/29.
 */
public class ComponentSizeChooser14 extends BaseConfigChooser14
{
    private final int mRedSize;
    private final int mGreenSize;
    private final int mBlueSize;
    private final int mAlphaSize;
    private final int mDepthSize;
    private final int mStencilSize;
    private int[] mAttributeValue;

    public ComponentSizeChooser14(int eglContextVersion, int redSize, int greenSize, int blueSize,
                                  int alphaSize, int depthSize, int stencilSize)
    {
        super(eglContextVersion, new int[]{
                EGL14.EGL_RED_SIZE, redSize, // r 通道大小
                EGL14.EGL_GREEN_SIZE, greenSize, // g 通道大小
                EGL14.EGL_BLUE_SIZE, blueSize, // b 通道大小
                EGL14.EGL_ALPHA_SIZE, alphaSize, // a 通道大小
                EGL14.EGL_DEPTH_SIZE, depthSize, // 深度信息
                EGL14.EGL_STENCIL_SIZE, stencilSize, // 模板测试信息
                EGL14.EGL_NONE});// 官网 eglChooseConfig() 指出,最后需要 EGL10.EGL_NONE 结尾

        mAttributeValue = new int[1];
        mRedSize = redSize;
        mGreenSize = greenSize;
        mBlueSize = blueSize;
        mAlphaSize = alphaSize;
        mDepthSize = depthSize;
        mStencilSize = stencilSize;
    }

    @Override
    EGLConfig chooseConfig(android.opengl.EGLDisplay display, android.opengl.EGLConfig[] configs)
    {
        for (EGLConfig config : configs)
        {
            int d = findConfigAttribute(display, config, EGL14.EGL_DEPTH_SIZE, 0);
            int s = findConfigAttribute(display, config, EGL14.EGL_STENCIL_SIZE, 0);

            if ((d >= mDepthSize) && (s >= mStencilSize))
            {
                int r = findConfigAttribute(display, config, EGL14.EGL_RED_SIZE, 0);
                int g = findConfigAttribute(display, config, EGL14.EGL_GREEN_SIZE, 0);
                int b = findConfigAttribute(display, config, EGL14.EGL_BLUE_SIZE, 0);
                int a = findConfigAttribute(display, config, EGL14.EGL_ALPHA_SIZE, 0);
                if ((r == mRedSize) && (g == mGreenSize) && (b == mBlueSize) && (a == mAlphaSize)) {
                    return config;
                }
            }
        }
        return null;
    }

    private int findConfigAttribute(EGLDisplay display, EGLConfig config, int attribute, int defaultValue)
    {
        if (EGL14.eglGetConfigAttrib(display, config, attribute, mAttributeValue, 0))
        {
            return mAttributeValue[0];
        }
        return defaultValue;
    }
}
