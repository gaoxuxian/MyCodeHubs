package com.xx.javademo.store;

/**
 * 选择排序
 */
class SelectionSort {
    static void sort(int[] data) {
        if (data == null) return;

        /*
            1、外循环遍历元素temp，内循环遍历后面的元素，找到最小值min
            2、交换 temp 与 min
         */
        int length = data.length;
        for (int i = 0; i < length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < length; j++) {
                if (data[j] < data[min]) {
                    min = j;
                }
            }
            int temp = data[i];
            data[i] = data[min];
            data[min] = temp;
        }
    }
}
