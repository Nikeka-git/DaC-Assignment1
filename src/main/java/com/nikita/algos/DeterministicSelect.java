package com.nikita.algos;

import com.nikita.algos.utils.SortUtils;

public class DeterministicSelect {
    public DeterministicSelect() {}

    public int select(int[] a, int k, Metrics metrics) {
        if (a == null || a.length == 0) {
            throw new IllegalArgumentException("Array is empty");
        }
        if (k < 0 || k >= a.length) throw new IllegalArgumentException("k out of range");
        return selectRec(a, 0, a.length - 1, k, metrics);
    }

    private int selectRec(int[] a, int left, int right, int k, Metrics metrics) {
        while (left <= right) {
            if (left == right) return a[left];

            metrics.enterRecursion();

            // pivot value chosen by median-of-medians
            int pivotValue = medianOfMedians(a, left, right, metrics);

            int pivotIndex = partitionAroundValue(a, left, right, pivotValue, metrics);

            if (k == pivotIndex) {
                metrics.exitRecursion();
                return a[k];
            }

            int leftSize = pivotIndex - left;
            int rightSize = right - pivotIndex;

            if (k < pivotIndex) {
                if (leftSize <= rightSize) {
                    int result = selectRec(a, left, pivotIndex - 1, k, metrics);
                    metrics.exitRecursion();
                    return result;
                } else {
                    right = pivotIndex - 1;
                    metrics.exitRecursion();
                    continue;
                }
            } else {
                if (rightSize <= leftSize) {
                    int result = selectRec(a, pivotIndex + 1, right, k, metrics);
                    metrics.exitRecursion();
                    return result;
                } else {
                    left = pivotIndex + 1;
                    metrics.exitRecursion();
                    continue;
                }
            }
        }
        throw new IllegalStateException("Unreachable");
    }

    private int partitionAroundValue(int[] a, int left, int right, int pivotValue, Metrics metrics) {
        int pivotIdx = left;
        while (pivotIdx <= right && a[pivotIdx] != pivotValue) pivotIdx++;
        if (pivotIdx > right) {
            pivotIdx = right;
        }
        SortUtils.swap(a, pivotIdx, right);
        int finalIdx = SortUtils.partition(a, left, right, metrics);
        return finalIdx;
    }

    private int medianOfMedians(int[] a, int left, int right, Metrics metrics) {
        int n = right - left + 1;
        if (n <= 5) {
            insertionSort(a, left, right, metrics);
            return a[left + n / 2];
        }

        int dst = left;
        for (int i = left; i <= right; i += 5) {
            int subRight = Math.min(i + 4, right);
            insertionSort(a, i, subRight, metrics);
            int medianIdx = i + (subRight - i) / 2;
            SortUtils.swap(a, medianIdx, dst);
            dst++;
        }
        int numMedians = dst - left;
        int mid = left + (numMedians - 1) / 2;
        return selectRec(a, left, left + numMedians - 1, mid, metrics);
    }

    private void insertionSort(int[] a, int left, int right, Metrics metrics) {
        for (int i = left + 1; i <= right; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= left) {
                metrics.incComparisons();
                if (a[j] > key) {
                    a[j + 1] = a[j];
                    j--;
                } else {
                    break;
                }
            }
            a[j + 1] = key;
        }
    }
}
