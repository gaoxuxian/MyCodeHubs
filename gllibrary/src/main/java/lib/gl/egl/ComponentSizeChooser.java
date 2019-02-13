package lib.gl.egl;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/29.
 */
public class ComponentSizeChooser extends BaseConfigChooser
{
    private final int mRedSize;
    private final int mGreenSize;
    private final int mBlueSize;
    private final int mAlphaSize;
    private final int mDepthSize;
    private final int mStencilSize;
    private int[] mAttributeValue;

    public ComponentSizeChooser(int eglContextVersion, int redSize, int greenSize, int blueSize,
                                int alphaSize, int depthSize, int stencilSize)
    {
        super(eglContextVersion, new int[]{
                EGL10.EGL_RED_SIZE, redSize, // r 通道大小
                EGL10.EGL_GREEN_SIZE, greenSize, // g 通道大小
                EGL10.EGL_BLUE_SIZE, blueSize, // b 通道大小
                EGL10.EGL_ALPHA_SIZE, alphaSize, // a 通道大小
                EGL10.EGL_DEPTH_SIZE, depthSize, // 深度信息
                EGL10.EGL_STENCIL_SIZE, stencilSize, // 模板测试信息
                EGL10.EGL_NONE});// 官网 eglChooseConfig() 指出,最后需要 EGL10.EGL_NONE 结尾

        mAttributeValue = new int[1];
        mRedSize = redSize;
        mGreenSize = greenSize;
        mBlueSize = blueSize;
        mAlphaSize = alphaSize;
        mDepthSize = depthSize;
        mStencilSize = stencilSize;
    }

    @Override
    EGLConfig chooseConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs)
    {
        for (EGLConfig config : configs)
        {
            int d = findConfigAttribute(egl, display, config, EGL10.EGL_DEPTH_SIZE, 0);
            int s = findConfigAttribute(egl, display, config, EGL10.EGL_STENCIL_SIZE, 0);

            if ((d >= mDepthSize) && (s >= mStencilSize))
            {
                int r = findConfigAttribute(egl, display, config, EGL10.EGL_RED_SIZE, 0);
                int g = findConfigAttribute(egl, display, config, EGL10.EGL_GREEN_SIZE, 0);
                int b = findConfigAttribute(egl, display, config, EGL10.EGL_BLUE_SIZE, 0);
                int a = findConfigAttribute(egl, display, config, EGL10.EGL_ALPHA_SIZE, 0);
                if ((r == mRedSize) && (g == mGreenSize) && (b == mBlueSize) && (a == mAlphaSize)) {
                    return config;
                }
            }
        }
        return null;
    }

    private int findConfigAttribute(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute, int defaultValue)
    {
        if (egl.eglGetConfigAttrib(display, config, attribute, mAttributeValue))
        {
            return mAttributeValue[0];
        }
        return defaultValue;
    }
}
