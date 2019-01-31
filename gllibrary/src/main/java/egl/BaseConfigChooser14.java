package egl;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/29.
 */
public abstract class BaseConfigChooser14
{
    private int[] mConfigSpec;

    public BaseConfigChooser14(int eglContextVersion, int[] configSpec)
    {
        mConfigSpec = filterConfigSpec(eglContextVersion, configSpec);
    }

    public EGLConfig chooseConfig(EGLDisplay display)
    {
        int[] num_config = new int[1];
        // first step: 根据外部指定的 config 获取符合条件的 EGL config list size
        if (!EGL14.eglChooseConfig(display, mConfigSpec, 0, null, 0, 0, num_config, 0))
        {
            throw new IllegalArgumentException("eglChooseConfig failed");
        }

        int numConfigs = num_config[0];

        if (numConfigs <= 0)
        {
            throw new IllegalArgumentException("No configs match configSpec");
        }

        // second step: 根据获取到的 size 获取具体 config list
        EGLConfig[] configs = new EGLConfig[numConfigs];
        if (!EGL14.eglChooseConfig(display, mConfigSpec, 0, configs, 0, numConfigs, num_config, 0))
        {
            throw new IllegalArgumentException("eglChooseConfig#2 failed");
        }

        // third step: 根据子类的需要, 筛选出最适合的 config
        EGLConfig config = chooseConfig(display, configs);
        if (config == null)
        {
            throw new IllegalArgumentException("No config chosen");
        }

        return config;
    }

    private int[] filterConfigSpec(int version, int[] configSpec)
    {
        if (version != 2 && version != 3)
        {
            return configSpec;
        }

        int len = configSpec.length;
        int[] newConfigSpec = new int[len + 2];
        System.arraycopy(configSpec, 0, newConfigSpec, 0, len - 1);
        newConfigSpec[len - 1] = EGL14.EGL_RENDERABLE_TYPE; // openGL es 渲染版本
        newConfigSpec[len] = version == 2 ? EGL14.EGL_OPENGL_ES2_BIT : EGLExt.EGL_OPENGL_ES3_BIT_KHR;
        newConfigSpec[len + 1] = EGL14.EGL_NONE;// 结尾

        return newConfigSpec;
    }

    abstract EGLConfig chooseConfig(EGLDisplay display, EGLConfig[] configs);
}
