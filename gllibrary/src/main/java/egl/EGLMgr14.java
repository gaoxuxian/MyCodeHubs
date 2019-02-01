package egl;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.util.Log;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/30.
 */
public class EGLMgr14 extends EGLMgr<ComponentSizeChooser14>
{
    private static final String TAG = EGLMgr14.class.getName();
    private EGLDisplay mEglDisplay;
    private EGLConfig mEglConfig;
    private EGLContext mEglContext;
    private EGLSurface mEglSurface;

    @Override
    protected void start(int eglContextVersion, ComponentSizeChooser14 configChooser)
    {
        // 由于 EGL14 是谷歌封装的api, 不需要额外获取 EGL 对象, 直接静态调用
        // 与 EGL10 的区别只是: 版本提高了, GLSurfaceView 用的还是 EGL10 配置的环境, 只是为了适配低版本

        // first step: 获取 EGLDisplay 对象(屏幕的抽象)
        mEglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);

        // 检查 EGLDisplay 的可用性
        if (mEglDisplay == EGL14.EGL_NO_DISPLAY)
        {
            throw new RuntimeException("egl14 GetDisplay failed");
        }

        // 版本格式 xx.yy
        int[] version_major = new int[1]; // 代表版本格式的 xx
        int[] version_minor = new int[1]; // 代表版本格式的 yy
        // second step: 初始化EGLDisplay连接, 成功返回具体版本
        if (!EGL14.eglInitialize(mEglDisplay, version_major, 0, version_minor, 0))
        {
            throw new RuntimeException("egl14 Initialize failed");
        }

        // third step: 获取 想要的 EGL framebuffer 配置
        mEglConfig = configChooser.chooseConfig(mEglDisplay);

        int[] context_attribute_list = {EGL14.EGL_CONTEXT_CLIENT_VERSION, eglContextVersion,
                EGL14.EGL_NONE};
        // forth step: 构建 EGLContext(环境的抽象)
        mEglContext = EGL14.eglCreateContext(mEglDisplay, mEglConfig, EGL14.EGL_NO_CONTEXT, context_attribute_list, 0);

        if (mEglContext == EGL14.EGL_NO_CONTEXT)
        {
            throw new RuntimeException("egl14 create Context fail");
        }
    }

    @Override
    public boolean setUpEglBackgroundEnvironment(int eglContextVersion, ComponentSizeChooser14 configChooser, int width, int height)
    {
        start(eglContextVersion, configChooser);

        // 设置 EGLSurface 的宽高(唯一可以设置的属性)
        int[] surface_attribute_list = {EGL14.EGL_WIDTH, width,
                                        EGL14.EGL_HEIGHT, height,
                                        EGL14.EGL_NONE};
        // fifth step: 构建一个 Pbuffer 单缓冲区(无法直接显示) 的 EGLSurface(实际上 EGLSurface 是一个 FrameBuffer)
        mEglSurface = EGL14.eglCreatePbufferSurface(mEglDisplay, mEglConfig, surface_attribute_list, 0);

        // 检查 EGLSurface 可用性
        if (mEglSurface == EGL14.EGL_NO_SURFACE)
        {
            Log.e(TAG, "egl create surface fail");
            return false;
        }

        // sixth step: 将 EGLContext 与当前线程、EGLSurface 进行绑定
        if (!EGL14.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext))
        {
            Log.e(TAG, "setUpEglPbufferSurfaceEnvironment:egl Make Current fail");
            return false;
        }

        return false;
    }

    @Override
    public void destroy()
    {
        EGL14.eglMakeCurrent(mEglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);

        EGL14.eglDestroySurface(mEglDisplay, mEglSurface);

        EGL14.eglDestroyContext(mEglDisplay, mEglContext);

        EGL14.eglTerminate(mEglDisplay);

        mEglSurface = EGL14.EGL_NO_SURFACE;
        mEglDisplay = EGL14.EGL_NO_DISPLAY;
        mEglContext = EGL14.EGL_NO_CONTEXT;
    }
}
