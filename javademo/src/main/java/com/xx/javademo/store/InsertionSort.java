package com.xx.javademo.store;

/**
 * 插入排序
 */
class InsertionSort {
    static void sort(int[] data) {
        if (data == null || data.length <= 0) return;
        int len = data.length;
        /*
            1、由于插入排序第0位前面已无其他元素可比，默认第0位已经被排序，故从1开始
            2、外循环遍历每一个元素temp，内循环根据temp值，与前面已经排序好的值做对比
            3、找到比temp小的值，并插在后面，同时大于temp往后移
         */
        for (int i = 1; i < len; i++) {
            for (int j = i; j >= 0; j--) {
                if (j > 0 && data[j - 1] > data[i]) {
                    data[j] = data[j - 1];
                } else {
                    data[j] = data[i];
                    break;
                }
            }
        }
    }
}
