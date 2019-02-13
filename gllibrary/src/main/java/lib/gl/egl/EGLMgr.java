package lib.gl.egl;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/30.
 */
public abstract class EGLMgr<T>
{
    /**
     * 内部调用, create EGLDisplay、EGLContext 对象
     * @param eglContextVersion 默认是 openGL es 的版本, 跟 EGL or EGL14 的 eglBindApi() 有关
     * @param configChooser egl config 构造类
     */
    protected abstract void start(int eglContextVersion, T configChooser);

    /**
     * 搭建 egl 后台环境, 外部调用
     * @param eglContextVersion 默认是 openGL es 的版本, 跟 EGL or EGL14 的 eglBindApi() 有关
     * @param configChooser egl config 构造类
     * @param width Pbuffer surface 的宽
     * @param height Pbuffer surface 的高
     * @return 环境搭建是否成功
     */
    public abstract boolean setUpEglBackgroundEnvironment(int eglContextVersion, T configChooser, int width, int height);

    public abstract void destroy();
}
