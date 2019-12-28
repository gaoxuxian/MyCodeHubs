package com.xx.javademo.store;

/**
 * 快排 -- 从小到大 -- 升序
 */
class QuickSort {
    static void sort(int[] data, int start, int end) {
        if (data == null || data.length <= 1 || start >= end) return;

        int left = start;
        int right = end;
        int temp = data[left];
        while (left < right) {
            while (left < right && data[right] >= temp) {
                right--;
            }
            // 替换不是交换
            data[left] = data[right];
            while (left < right && data[left] <= temp) {
                left++;
            }
            // 替换不是交换
            data[right] = data[left];
        }
        data[left] = temp; // 把坑填回去
        sort(data, start, left - 1);
        sort(data, right + 1, end);
    }
}
