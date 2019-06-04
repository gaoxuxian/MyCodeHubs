package com.xx.avlibrary.gl.egl;

import android.content.Context;
import android.opengl.*;
import android.view.Surface;
import com.xx.avlibrary.gl.util.GLUtil;

/**
 * EGL 环境搭建
 * @author Gxx
 */
public class EglCore extends Object {
    private static final int EGL_RECORDABLE_ANDROID = 0x3142;

    private int[] mAttributeValue;
    private int mEglContextVersion;

    private EGLDisplay mEglDisplay;
    private EGLConfig mEglConfig;
    private EGLContext mEglContext;

    public void setConfig(Context context, int redSize, int greenSize, int blueSize, int alphaSize, int depthSize, int stencilSize, boolean recordable) {
        mEglContextVersion = 2;
        int render_type = EGL14.EGL_OPENGL_ES2_BIT;

        if (GLUtil.getGlSupportVersionInt(context) >= 3) {
            mEglContextVersion = 3;
            render_type = EGLExt.EGL_OPENGL_ES3_BIT_KHR;
        }

        mAttributeValue = new int[]{
                EGL14.EGL_RED_SIZE, redSize, // r 通道大小
                EGL14.EGL_GREEN_SIZE, greenSize, // g 通道大小
                EGL14.EGL_BLUE_SIZE, blueSize, // b 通道大小
                EGL14.EGL_ALPHA_SIZE, alphaSize, // a 通道大小
                EGL14.EGL_DEPTH_SIZE, depthSize, // 深度信息
                EGL14.EGL_STENCIL_SIZE, stencilSize, // 模板测试信息
                EGL14.EGL_RENDERABLE_TYPE, render_type, // 渲染api类型
                EGL14.EGL_NONE, 0, // placeholder for recordable
                EGL14.EGL_NONE // 官网 eglChooseConfig() 指出,最后需要 EGL10.EGL_NONE 结尾
        };

        if (recordable) {
            mAttributeValue[mAttributeValue.length - 3] = EGL_RECORDABLE_ANDROID; // android_recording
            mAttributeValue[mAttributeValue.length - 2] = 1;
        }
    }

    public void initEglContext(EGLContext share_context) {

        if (mAttributeValue == null) {
            throw new RuntimeException("initEglContext() config do not set up yet");
        }

        // 由于 EGL14 是谷歌封装的api, 不需要额外获取 EGL 对象, 直接静态调用
        // 与 EGL10 的区别只是: 版本提高了, GLSurfaceView 用的还是 EGL10 配置的环境, 只是为了适配低版本

        // 1: 获取 EGLDisplay 对象(屏幕的抽象)
        mEglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);

        // 检查 EGLDisplay 的可用性
        if (mEglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("initEglContext() get display failed");
        }

        // 版本格式 xx.yy
        int[] version_major = new int[1];
        int[] version_minor = new int[1];
        // 2: 初始化EGLDisplay连接, 成功返回具体版本
        if (!EGL14.eglInitialize(mEglDisplay, version_major, 0, version_minor, 0)) {
            mEglDisplay = EGL14.EGL_NO_DISPLAY;
            throw new RuntimeException("initEglContext() initialize failed");
        }

        // 3: 获取 想要的 EGL framebuffer 配置
        int[] num_config = new int[1];
        EGLConfig[] configs = new EGLConfig[1];
        if (!EGL14.eglChooseConfig(mEglDisplay, mAttributeValue, 0, configs, 0, num_config.length, num_config, 0)) {
            mEglDisplay = EGL14.EGL_NO_DISPLAY;
            throw new RuntimeException("initEglContext() chooseConfig failed #2");
        }
        mEglConfig = configs[0];

        if (mEglConfig == null) {
            mEglDisplay = EGL14.EGL_NO_DISPLAY;
            throw new RuntimeException("initEglContext() get no fit config yet");
        }

        int[] context_attribute_list = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, mEglContextVersion,
                EGL14.EGL_NONE};

        // 4: 构建 EGLContext(环境的抽象)
        mEglContext = EGL14.eglCreateContext(mEglDisplay, mEglConfig, share_context == null ? EGL14.EGL_NO_CONTEXT : share_context, context_attribute_list, 0);

        if (mEglContext == EGL14.EGL_NO_CONTEXT) {
            mEglDisplay = EGL14.EGL_NO_DISPLAY;
            mEglConfig = null;
            throw new RuntimeException("initEglContext() create egl context failure");
        }
    }

    /**
     * 创建双缓冲表面
     * @param native_window 本地surface
     */
    public EGLSurface createWindowSurface(Surface native_window) {
        if (mEglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("create window surface failure : mEglDisplay == EGL14.EGL_NO_DISPLAY");
        }

        if (native_window == null) {
            throw new RuntimeException("create window surface failure : native_window == null");
        }

        int[] attrib_list = new int[]{
                EGL14.EGL_NONE
        };

        EGLSurface eglSurface = EGL14.eglCreateWindowSurface(mEglDisplay, mEglConfig, native_window, attrib_list, 0);

        if (eglSurface == EGL14.EGL_NO_SURFACE) {
            throw new RuntimeException("create window surface failure : created surface is null");
        }

        return eglSurface;
    }

    /**
     * 创建单缓冲表面
     */
    public EGLSurface createPbufferSurface(int width, int height){
        if (mEglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("create Pbuffer surface failure : mEglDisplay == EGL14.EGL_NO_DISPLAY");
        }

        int[] attrib_list = new int[]{
                EGL14.EGL_WIDTH, width,
                EGL14.EGL_HEIGHT, height,
                EGL14.EGL_NONE
        };

        EGLSurface eglSurface = EGL14.eglCreatePbufferSurface(mEglDisplay, mEglConfig, attrib_list, 0);

        if (eglSurface == EGL14.EGL_NO_SURFACE) {
            throw new RuntimeException("create Pbuffer surface failure : created surface is null");
        }

        return eglSurface;
    }

    public void makeCurrent(EGLSurface eglSurface) {
        makeCurrent(eglSurface, eglSurface);
    }

    public void makeCurrent(EGLSurface drawSurface, EGLSurface readSurface) {
        if (mEglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("makeCurrent after init EglContext");
        }
        if (!EGL14.eglMakeCurrent(mEglDisplay, drawSurface, readSurface, mEglContext)) {
            throw new RuntimeException("makeCurrent(draw,read) failed");
        }
    }

    public void makeDefaultCurrent() {
        if (mEglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("makeDefaultCurrent after init EglContext");
        }
        if (!EGL14.eglMakeCurrent(mEglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)) {
            throw new RuntimeException("makeDefaultCurrent failed");
        }
    }

    /**
     * Calls eglSwapBuffers.  Use this to "publish" the current frame.
     *
     * @return false on failure
     */
    public boolean swapBuffers(EGLSurface eglSurface) {
        return EGL14.eglSwapBuffers(mEglDisplay, eglSurface);
    }

    /**
     * Sends the presentation time stamp to EGL.  Time is expressed in nanoseconds.
     */
    public void setPresentationTime(EGLSurface eglSurface, long nsecs) {
        EGLExt.eglPresentationTimeANDROID(mEglDisplay, eglSurface, nsecs);
    }

    /**
     * Returns true if our context and the specified surface are current.
     */
    public boolean isCurrent(EGLSurface eglSurface) {
        return mEglContext.equals(EGL14.eglGetCurrentContext()) &&
                eglSurface.equals(EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW));
    }

    /**
     * Performs a simple surface query.
     */
    public int querySurface(EGLSurface eglSurface, int what) {
        int[] value = new int[1];
        EGL14.eglQuerySurface(mEglDisplay, eglSurface, what, value, 0);
        return value[0];
    }

    public void releaseSurface(EGLSurface eglSurface) {
        if (mEglDisplay != EGL14.EGL_NO_DISPLAY) {
            EGL14.eglDestroySurface(mEglDisplay, eglSurface);
        }
    }

    public void release() {
        if (mEglDisplay != EGL14.EGL_NO_DISPLAY) {
            EGL14.eglMakeCurrent(mEglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                    EGL14.EGL_NO_CONTEXT);
            EGL14.eglDestroyContext(mEglDisplay, mEglContext);
            EGL14.eglReleaseThread();
            EGL14.eglTerminate(mEglDisplay);
        }

        mEglDisplay = EGL14.EGL_NO_DISPLAY;
        mEglContext = EGL14.EGL_NO_CONTEXT;
        mEglConfig = null;
    }

    public EGLConfig getEglConfig() {
        return mEglConfig;
    }

    public int getError() {
        return EGL14.eglGetError();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (mEglDisplay != EGL14.EGL_NO_DISPLAY) {
                release();
            }
        } finally {
            super.finalize();
        }
    }
}
