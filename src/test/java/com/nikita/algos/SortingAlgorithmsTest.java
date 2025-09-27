package com.nikita.algos;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class SortingAlgorithmsTest {

    private final List<SortAlgorithm> algorithms = List.of(
            new MergeSort()

    );

    @Test
    public void testRandomArray() {
        Random random = new Random();
        int[] base = random.ints(1000, -10000, 10000).toArray();

        for (SortAlgorithm algo : algorithms) {
            int[] arr = Arrays.copyOf(base, base.length);
            int[] expected = Arrays.copyOf(base, base.length);
            Arrays.sort(expected);

            Metrics metrics = new Metrics();
            algo.sort(arr, metrics);

            assertArrayEquals(expected, arr, algo.getClass().getSimpleName() + " failed on random array");
        }
    }

    @Test
    public void testAlreadySortedArray() {
        int[] base = {1, 2, 3, 4, 5};

        for (SortAlgorithm algo : algorithms) {
            int[] arr = Arrays.copyOf(base, base.length);
            int[] expected = Arrays.copyOf(base, base.length);

            Metrics metrics = new Metrics();
            algo.sort(arr, metrics);

            assertArrayEquals(expected, arr, algo.getClass().getSimpleName() + " failed on sorted array");
        }
    }

    @Test
    public void testReverseSortedArray() {
        int[] base = {5, 4, 3, 2, 1};
        int[] expected = {1, 2, 3, 4, 5};

        for (SortAlgorithm algo : algorithms) {
            int[] arr = Arrays.copyOf(base, base.length);

            Metrics metrics = new Metrics();
            algo.sort(arr, metrics);

            assertArrayEquals(expected, arr, algo.getClass().getSimpleName() + " failed on reverse sorted array");
        }
    }

    @Test
    public void testAllEqualArray() {
        int[] base = {7, 7, 7, 7, 7};

        for (SortAlgorithm algo : algorithms) {
            int[] arr = Arrays.copyOf(base, base.length);
            int[] expected = Arrays.copyOf(base, base.length);

            Metrics metrics = new Metrics();
            algo.sort(arr, metrics);

            assertArrayEquals(expected, arr, algo.getClass().getSimpleName() + " failed on all equal array");
        }
    }
}
