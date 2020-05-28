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
            /*
            step-1
             */
            while (left < right && data[right] >= temp) {
                right--;
            }
            // 替换不是交换
            data[left] = data[right];
            /*
            step-2
             */
            while (left < right && data[left] <= temp) {
                left++;
            }
            // 替换不是交换
            data[right] = data[left];

            /*
            假设单次执行中，step-1 执行了 x 次，step-2 执行了 y 次
            那么根据条件 x + y = n，n 是数组长度

            然后每次执行完，就要将1个数组分成2个，那最终需要分 log n 次

            所以一般的时间复杂度是 n * log n
             */
        }
        data[left] = temp; // 把坑填回去
        sort(data, start, left - 1);
        sort(data, right + 1, end);
    }
}
