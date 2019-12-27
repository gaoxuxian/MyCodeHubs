package com.xx.javademo.store;

import java.util.Arrays;

/**
 * 归并排序
 */
class MergingSort {
    static void sort(int[] data) {
        System.arraycopy(split(data), 0, data, 0, data.length);
    }

    private static int[] split(int[] data) {
        if (data == null || data.length <= 1) return data;

        int num = data.length >> 1;
        int[] left = Arrays.copyOfRange(data, 0, num);
        int[] right = Arrays.copyOfRange(data, num, data.length);
        return merge(split(left), split(right));
    }

    private static int[] merge(int[] data1, int[] data2) {
        int[] out = new int[data1.length + data2.length];
        int i = 0;
        int j = 0;
        int k = 0;
        while (i < data1.length && j < data2.length) {
            if (data1[i] < data2[j]) {
                out[k] = data1[i];
                i++;
            } else {
                out[k] = data2[j];
                j++;
            }
            k++;
        }
        while (i < data1.length) {
            out[k] = data1[i];
            i++;
            k++;
        }
        while (j < data2.length) {
            out[k] = data2[j];
            j++;
            k++;
        }
        return out;
    }
}
