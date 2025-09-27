package com.nikita.algos;

public class MergeSort implements SortAlgorithm {
    private static final int CUTOFF = 15;

    @Override
    public void sort(int[] arr, Metrics metrics) {
        if (arr.length <= 1) return;
        int[] buffer = new int[arr.length]; // reuse buffer
        metrics.incAllocations();
        mergeSort(arr, 0, arr.length - 1, buffer, metrics);
    }

    @Override
    public String name() {
        return "MergeSort";
    }

    private void mergeSort(int[] arr, int left, int right, int[] buffer, Metrics metrics) {
        if (right - left + 1 <= CUTOFF) {
            insertionSort(arr, left, right, metrics);
            return;
        }
        metrics.enterRecursion();
        int mid = (left + right) / 2;
        mergeSort(arr, left, mid, buffer, metrics);
        mergeSort(arr, mid + 1, right, buffer, metrics);
        merge(arr, left, mid, right, buffer, metrics);
        metrics.exitRecursion();
    }

    private void merge(int[] arr, int left, int mid, int right, int[] buffer, Metrics metrics) {
        System.arraycopy(arr, left, buffer, left, right - left + 1);

        int i = left, j = mid + 1, k = left;
        while (i <= mid && j <= right) {
            metrics.incComparisons();
            if (buffer[i] <= buffer[j]) arr[k++] = buffer[i++];
            else arr[k++] = buffer[j++];
        }
        while (i <= mid) arr[k++] = buffer[i++];
        while (j <= right) arr[k++] = buffer[j++];
    }

    private void insertionSort(int[] arr, int left, int right, Metrics metrics) {
        for (int i = left + 1; i <= right; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= left) {
                metrics.incComparisons();
                if (arr[j] > key) arr[j + 1] = arr[j--];
                else break;
            }
            arr[j + 1] = key;
        }
    }
}
