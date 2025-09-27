package com.nikita.algos.utils;

import java.util.Random;
import com.nikita.algos.Metrics;

public class SortUtils {
    private static final Random rand = new Random();

    public static void shuffle(int[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            swap(arr, i, j);
        }
    }

    public static void swap(int[] arr, int i, int j) {
        if (i != j) {
            int tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
    }

    public static int partition(int[] arr, int left, int right, Metrics metrics) {
        int pivot = arr[right];
        int i = left - 1;
        for (int j = left; j < right; j++) {
            metrics.incComparisons();
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, right);
        return i + 1;
    }
}
