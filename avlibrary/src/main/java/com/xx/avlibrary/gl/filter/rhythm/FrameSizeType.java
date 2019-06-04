package com.xx.avlibrary.gl.filter.rhythm;

/**
 * 画幅比例类型，宽高比，宽:高
 */
public class FrameSizeType {
    public static final int size_1_1 = 1; // 1:1
    public static final int size_3_4 = 2; // 3:4
    public static final int size_4_3 = 3; // 4:3
    public static final int size_235_1 = 4; // 2.35:1
    public static final int size_16_9 = 5; // 16:9
    public static final int size_9_16 = 6; // 9:16

    public static float getAspectRatio(int type) {
        switch (type) {
            case size_1_1: return 1;

            case size_3_4: return 3f/4f;

            case size_4_3: return 4f/3f;

            case size_235_1: return 2.35f;

            case size_16_9: return 16f/9f;

            case size_9_16: return 9f/16f;
        }
        return 0;
    }
}
