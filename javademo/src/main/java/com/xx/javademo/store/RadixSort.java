package com.xx.javademo.store;

/**
 * 基数排序
 */
class RadixSort {
    static void sort(int[] data) {
        if (data == null || data.length <= 1) return;
        int len = data.length;
        int max = 0;
        // 取得数组中的最大数
        for (int datum : data) {
            max = Math.max(Math.abs(datum), max);
        }
        // 取得最大数的位数
        int power = 1;
        while (max / 10 != 0) {
            power++;
            max /= 10;
        }

        // 申请一个和原数组长度一样的桶，作为临时容器
        int[] tempArr = new int[len];
        int index, div, pow;
        for (int i = 1; i <= power; i++) {
            div = (int) Math.pow(10, i - 1);
            pow = (int) Math.pow(10, i);
            index = 0;
            for (int j = 0; j < 10; j++) {
                for (int datum : data) {
                    // 计算出每一位上的单独数字，按照（原数组的顺序 && 符合0-9的遍历顺序）放入桶
                    int value = (datum / div) % pow;
                    if (value == j) {
                        tempArr[index] = datum;
                        index++;
                    }
                }
            }
            int[] a = data;
            data = tempArr;
            tempArr = a;
        }
    }
}
