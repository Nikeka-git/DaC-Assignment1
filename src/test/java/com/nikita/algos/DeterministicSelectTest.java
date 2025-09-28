package com.nikita.algos;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeterministicSelectTest {

    @Test
    public void testRandomTrials() {
        Random rnd = new Random(123);
        DeterministicSelect ds = new DeterministicSelect();

        int trials = 100;
        for (int t = 0; t < trials; t++) {
            int n = 200 + rnd.nextInt(800); // sizes between 200 and 999
            int[] arr = rnd.ints(n, -10000, 10000).toArray();
            int[] sorted = Arrays.copyOf(arr, arr.length);
            Arrays.sort(sorted);

            int k = rnd.nextInt(n);

            Metrics metrics = new Metrics();
            int result = ds.select(Arrays.copyOf(arr, arr.length), k, metrics);

            int expected = sorted[k];
            assertEquals(expected, result, "Select returned wrong value on trial " + t + ", n=" + n + ", k=" + k);
        }
    }

    @Test
    public void testSmallAndEdgeCases() {
        DeterministicSelect ds = new DeterministicSelect();

        Metrics m1 = new Metrics();
        assertEquals(5, ds.select(new int[]{5}, 0, m1));

        int[] arr = {2, 1};
        Metrics m2 = new Metrics();
        assertEquals(2, ds.select(arr, 1, m2)); // largest

        int[] dup = {7,7,7,7,7,7};
        Metrics m3 = new Metrics();
        assertEquals(7, ds.select(dup, 3, m3));
    }
}
