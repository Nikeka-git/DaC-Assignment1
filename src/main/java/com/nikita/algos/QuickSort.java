package com.nikita.algos;

import com.nikita.algos.utils.SortUtils;
import java.util.Random;

public class QuickSort implements SortAlgorithm {
    private final Random rand = new Random();

    @Override
    public void sort(int[] arr, Metrics metrics) {
        if (arr == null || arr.length <= 1) return;
        SortUtils.shuffle(arr);           // рандомизация перед сортировкой
        quickSort(arr, 0, arr.length - 1, metrics);
    }

    @Override
    public String name() {
        return "QuickSort";
    }

    /**
     * Итеративно-рекурсивный QuickSort: рекурсируем только в меньшую часть,
     * большую обрабатываем циклом (tail recursion elimination).
     */
    private void quickSort(int[] arr, int left, int right, Metrics metrics) {
        while (left < right) {
            // случайный pivot, переместим его в правый конец
            int pivotIndex = left + rand.nextInt(right - left + 1);
            SortUtils.swap(arr, pivotIndex, right);

            int pi = SortUtils.partition(arr, left, right, metrics);
            // теперь pivot окончательно стоит на позиции pi

            int leftSize = pi - left;   // количество элементов в левой части
            int rightSize = right - pi; // количество в правой части

            // Рекурсируем в меньшую часть, а в большую — продолжаем цикл (меняем left/right).
            if (leftSize < rightSize) {
                // рекурсивно обрабатываем левую (меньшую) часть
                if (left < pi - 1) {
                    metrics.enterRecursion();
                    quickSort(arr, left, pi - 1, metrics);
                    metrics.exitRecursion();
                }
                // затем продолжаем обработку правой части итеративно
                left = pi + 1;
            } else {
                // рекурсивно обрабатываем правую (меньшую или равную) часть
                if (pi + 1 < right) {
                    metrics.enterRecursion();
                    quickSort(arr, pi + 1, right, metrics);
                    metrics.exitRecursion();
                }
                // затем продолжаем обработку левой части итеративно
                right = pi - 1;
            }
        }
    }
}
