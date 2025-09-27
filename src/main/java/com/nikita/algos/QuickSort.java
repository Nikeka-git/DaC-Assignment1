package com.nikita.algos;

import com.nikita.algos.utils.SortUtils;
import java.util.Random;

public class QuickSort implements SortAlgorithm {
    private Random rand = new Random();

    @Override
    public void sort(int[] arr, Metrics metrics) {
        if (arr == null || arr.length <= 1) return;

        SortUtils.shuffle(arr);

        quickSort(arr, 0, arr.length - 1, metrics);
    }

    @Override
    public String name() {
        return "QuickSort";
    }

    private void quickSort(int[] arr, int left, int right, Metrics metrics) {
        if (left >= right) return;

        metrics.enterRecursion();

        int pivotIndex = left + rand.nextInt(right - left + 1);
        SortUtils.swap(arr, pivotIndex, right);

        int pi = SortUtils.partition(arr, left, right, metrics);

        if (pi - 1 - left < right - (pi + 1)) {
            quickSort(arr, left, pi - 1, metrics);
            quickSort(arr, pi + 1, right, metrics);
        } else {
            quickSort(arr, pi + 1, right, metrics);
            quickSort(arr, left, pi - 1, metrics);
        }

        metrics.exitRecursion();
    }
}
