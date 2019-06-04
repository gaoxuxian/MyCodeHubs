package com.xx.avlibrary.gl.util;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/24.
 */
public class SysConfig
{
    private static boolean isDebug = false;

    public static void setDebugMode(boolean is)
    {
        isDebug = is;
    }

    public static boolean isDebugMode()
    {
        return isDebug;
    }
}
