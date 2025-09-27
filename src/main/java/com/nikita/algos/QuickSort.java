package com.nikita.algos;

import java.util.Random;

public class QuickSort implements SortAlgorithm {
    private Random rand = new Random();

    @Override
    public void sort(int[] arr, Metrics metrics) {
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
        swap(arr, pivotIndex, right, metrics);
        int pivot = arr[right];

        int i = left - 1;
        for (int j = left; j < right; j++) {
            metrics.incComparisons();
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j, metrics);
            }
        }
        swap(arr, i + 1, right, metrics);
        int pi = i + 1;

        if (pi - 1 - left < right - (pi + 1)) {
            quickSort(arr, left, pi - 1, metrics);
            quickSort(arr, pi + 1, right, metrics);
        } else {
            quickSort(arr, pi + 1, right, metrics);
            quickSort(arr, left, pi - 1, metrics);
        }

        metrics.exitRecursion();
    }

    private void swap(int[] arr, int i, int j, Metrics metrics) {
        if (i != j) {
            int tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
    }
}
