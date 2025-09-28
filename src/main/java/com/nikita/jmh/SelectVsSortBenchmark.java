package com.nikita.jmh;

import com.nikita.algos.*;
import com.nikita.algos.Metrics;
import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class SelectVsSortBenchmark {

    @Param({"1000", "5000", "10000"})
    private int size;

    private int[] data;
    private Random rnd;
    private QuickSort quickSort;
    private DeterministicSelect select;

    @Setup(Level.Iteration)
    public void setup() {
        rnd = new Random(42);
        data = rnd.ints(size, 0, 1_000_000).toArray();
        quickSort = new QuickSort();
        select = new DeterministicSelect();
    }

    @Benchmark
    public int quicksortMedian() {
        int[] copy = Arrays.copyOf(data, data.length);
        Metrics m = new Metrics();
        quickSort.sort(copy, m);
        return copy[size / 2]; // медиана
    }

    @Benchmark
    public int selectMedian() {
        int[] copy = Arrays.copyOf(data, data.length);
        Metrics m = new Metrics();
        return select.select(copy, size / 2, m);
    }
}
