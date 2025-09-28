package com.nikita.algos;

import com.nikita.algos.utils.SortUtils;

/**
 * Deterministic Select (Median of Medians, groups of 5).
 * k is 0-based (0 => smallest element).
 */
public class DeterministicSelect {

    /**
     * Public entry: selects k-th smallest element in array.
     */
    public int select(int[] a, int k, Metrics metrics) {
        if (a == null || a.length == 0) {
            throw new IllegalArgumentException("Array is empty");
        }
        if (k < 0 || k >= a.length) throw new IllegalArgumentException("k out of range");
        return selectRec(a, 0, a.length - 1, k, metrics);
    }

    /**
     * selectRec: selects k-th (0-based) in a[left..right].
     * Uses recursion into the smaller side and iteration over the larger one.
     */
    private int selectRec(int[] a, int left, int right, int k, Metrics metrics) {
        while (left <= right) {
            if (left == right) return a[left];

            metrics.enterRecursion();

            // pivot value chosen by median-of-medians
            int pivotValue = medianOfMedians(a, left, right, metrics);

            // partition around pivotValue (uses SortUtils.partition)
            int pivotIndex = partitionAroundValue(a, left, right, pivotValue, metrics);

            if (k == pivotIndex) {
                metrics.exitRecursion();
                return a[k];
            }

            int leftSize = pivotIndex - left;
            int rightSize = right - pivotIndex;

            if (k < pivotIndex) {
                // target is on left
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
                // target is on right
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

    /**
     * Partition array around pivotValue. Returns final pivot index.
     * We find an occurrence of pivotValue, swap it to the end and call SortUtils.partition.
     */
    private int partitionAroundValue(int[] a, int left, int right, int pivotValue, Metrics metrics) {
        int pivotIdx = left;
        while (pivotIdx <= right && a[pivotIdx] != pivotValue) pivotIdx++;
        if (pivotIdx > right) {
            // pivotValue not found (should be rare). Just use right as pivot index.
            pivotIdx = right;
        }
        // move pivot to end
        SortUtils.swap(a, pivotIdx, right);
        // use util partition which also counts comparisons
        int finalIdx = SortUtils.partition(a, left, right, metrics);
        return finalIdx;
    }

    /**
     * Build medians of each group of 5 and recursively select median-of-medians.
     * Returns the pivot value (not index).
     */
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

    /**
     * Simple insertion sort for small ranges. Counts comparisons via metrics.
     */
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
