package com.xx.javademo.store;

public class SortUtil {
    public static void QuitSort(int[] data) {
        QuickSort.sort(data, 0, data.length - 1);
    }

    public static void InsertSort(int[] data) {
        InsertionSort.sort(data);
    }

    public static void SelectionSort(int[] data) {
        SelectionSort.sort(data);
    }

    public static void BubbleSort(int[] data) {
        BubbleSort.sort(data);
    }

    public static void MergeSort(int[] data) {
        MergingSort.sort(data);
    }

    public static void RadixSort(int[] data) {
        RadixSort.sort(data);
    }
}
