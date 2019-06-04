package com.xx.avlibrary.gl.egl;

import android.opengl.EGL14;
import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/29.
 */
public class EGLMgr10 extends EGLMgr<ComponentSizeChooser>
{
    private static final String TAG = EGLMgr10.class.getName();

    private EGL10 mEgl;
    private EGLDisplay mEglDisplay;
    private EGLConfig mEglConfig;
    private EGLContext mEglContext;
    private EGLSurface mEglSurface;

    @Override
    protected void start(int eglContextVersion, ComponentSizeChooser configChooser)
    {
        // first step: 获取 EGL 对象
        mEgl = (EGL10) EGLContext.getEGL();

        // second step: 获取 EGLDisplay 对象(屏幕的抽象)
        mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

        // 检查 EGLDisplay 的可用性
        if (mEglDisplay == EGL10.EGL_NO_DISPLAY)
        {
            throw new RuntimeException("egl GetDisplay failed");
        }

        // 版本格式 xx.yy
        int[] version = new int[2];
        // third step: 初始化EGLDisplay连接, 成功返回具体版本
        if (!mEgl.eglInitialize(mEglDisplay, version))
        {
            throw new RuntimeException("egl Initialize failed");
        }

        // forth step: 获取 想要的 EGL framebuffer 配置
        mEglConfig = configChooser.chooseConfig(mEgl, mEglDisplay);

        int[] context_attribute_list = {EGL14.EGL_CONTEXT_CLIENT_VERSION, eglContextVersion,
                EGL10.EGL_NONE};
        // fifth step: 构建 EGLContext(环境的抽象)
        mEglContext = mEgl.eglCreateContext(mEglDisplay, mEglConfig, EGL10.EGL_NO_CONTEXT, context_attribute_list);

        // 检查 EGLContext 可用性
        if (mEglContext == null || mEglContext == EGL10.EGL_NO_CONTEXT)
        {
            mEglContext = null;
            throw new RuntimeException("egl create Context fail");
        }
    }

    @Override
    public boolean setUpEglBackgroundEnvironment(int eglContextVersion, ComponentSizeChooser configChooser, int width, int height)
    {
        start(eglContextVersion, configChooser);

        // 设置 EGLSurface 的宽高(唯一可以设置的属性)
        int[] surface_attribute_list = {EGL10.EGL_WIDTH, width,
                EGL10.EGL_HEIGHT, height,
                EGL10.EGL_NONE};

        // sixth step: 构建一个 Pbuffer 单缓冲区(无法直接显示) 的 EGLSurface(实际上 EGLSurface 是一个 FrameBuffer)
        mEglSurface = mEgl.eglCreatePbufferSurface(mEglDisplay, mEglConfig, surface_attribute_list);

        // 检查 EGLSurface 可用性
        if (mEglSurface == null || mEglSurface == EGL10.EGL_NO_SURFACE)
        {
            mEglSurface = null;
            Log.e(TAG, "egl create surface fail");
            return false;
        }

        // seventh step: 将 EGLContext 与当前线程、EGLSurface 进行绑定
        if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext))
        {
            Log.e(TAG, "setUpEglPbufferSurfaceEnvironment:egl Make Current fail");
            return false;
        }

        return true;
    }

    @Override
    public void destroy()
    {
        mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);

        mEgl.eglDestroySurface(mEglDisplay, mEglSurface);

        mEgl.eglDestroyContext(mEglDisplay, mEglContext);

        mEgl.eglTerminate(mEglDisplay);

        mEglSurface = EGL10.EGL_NO_SURFACE;
        mEglDisplay = EGL10.EGL_NO_DISPLAY;
        mEglContext = EGL10.EGL_NO_CONTEXT;
        mEgl = null;
    }
}
