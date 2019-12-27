package com.xx.javademo.store;

/**
 * 冒泡排序
 */
class BubbleSort {
    static void sort(int[] data) {
        if (data == null) return;

        /*
            1、外循环限定数组边界
            2、内循环在边界内挨个对比，找到最大值，并排到边界上
         */
        int length = data.length;
        for (int i = length - 1; i >= 0; i--) {
            for (int j = 0; j < i; j++) {
                if (data[j] > data[j + 1]) {
                    int temp = data[j];
                    data[j] = data[j + 1];
                    data[j + 1] = temp;
                }
            }
        }
    }
}
