package com.clib;

public class C_Test_lib {
    static {
        System.loadLibrary("c_test_lib");
    }

    public static native void printf();
}
