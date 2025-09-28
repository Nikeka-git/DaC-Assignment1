package com.nikita.benchmark;

import com.nikita.algos.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

public class BenchmarkRunner {
    public static void main(String[] args) {
        List<SortAlgorithm> algorithms = List.of(new QuickSort());

        List<DeterministicSelect> selectAlgorithms = List.of(
                new DeterministicSelect()
        );

        Map<String, Function<Integer, int[]>> generators = new LinkedHashMap<>();
        generators.put("random", n -> new Random().ints(n, 0, 1_000_000).toArray());
        generators.put("sorted", n -> IntStream.range(0, n).toArray());
        generators.put("reversed", n -> IntStream.iterate(n - 1, i -> i - 1).limit(n).toArray());
        generators.put("constant", n -> {
            int[] arr = new int[n];
            Arrays.fill(arr, 42);
            return arr;
        });
        generators.put("partially_sorted", n -> {
            int[] arr = IntStream.range(0, n).toArray();
            Random rnd = new Random();
            for (int i = 0; i < n / 10; i++) {
                int i1 = rnd.nextInt(n), i2 = rnd.nextInt(n);
                int tmp = arr[i1]; arr[i1] = arr[i2]; arr[i2] = tmp;
            }
            return arr;
        });

        int[] sizes = {50, 100, 1000, 2000};
        int trials = 100;

        try (CsvWriter csv = new CsvWriter("results/deterministic/deterministic.csv")) {
            for (SortAlgorithm algo : algorithms) {
                for (Map.Entry<String, Function<Integer, int[]>> entry : generators.entrySet()) {
                    String caseName = entry.getKey();
                    Function<Integer, int[]> generator = entry.getValue();

                    for (int size : sizes) {
                        double totalTime = 0.0;
                        long totalAllocations = 0;
                        long totalComparisons = 0;
                        long totalDepth = 0;

                        for (int t = 0; t < trials; t++) {
                            int[] input = generator.apply(size);
                            Metrics metrics = new Metrics();

                            long start = System.nanoTime();
                            algo.sort(input, metrics);
                            long end = System.nanoTime();

                            double elapsedMs = (end - start) / 1_000_000.0;
                            totalTime += elapsedMs;
                            totalAllocations += metrics.getAllocations();
                            totalComparisons += metrics.getComparisons();
                            totalDepth += metrics.getMaxDepth();

                        }

                        double avgTime = totalTime / trials;
                        long avgAlloc = Math.round((double) totalAllocations / trials);
                        long avgComps = Math.round((double) totalComparisons / trials);
                        long avgDepth = Math.round((double) totalDepth / trials);

                        csv.writeResult(algo.name() + "_" + caseName, size, avgTime, avgComps, avgAlloc, avgDepth);
                    }
                }
            }
            for (DeterministicSelect sel : selectAlgorithms) {
                for (var entry : generators.entrySet()) {
                    String caseName = entry.getKey();
                    Function<Integer, int[]> generator = entry.getValue();

                    for (int size : sizes) {
                        double totalTime = 0.0;
                        long totalComparisons = 0;
                        long totalDepth = 0;
                        long totalAllocations = 0;

                        for (int t = 0; t < trials; t++) {
                            int[] input = generator.apply(size);
                            int k = new Random().nextInt(size);
                            Metrics metrics = new Metrics();

                            metrics.startTimer();
                            sel.select(Arrays.copyOf(input, input.length), k, metrics);
                            metrics.endTimer();

                            totalTime += metrics.getElapsedMillis();
                            totalComparisons += metrics.getComparisons();
                            totalDepth += metrics.getMaxDepth();
                            totalAllocations += metrics.getAllocations();
                        }

                        double avgTime = totalTime / trials;
                        long avgComparisons = Math.round((double) totalComparisons / trials);
                        long avgDepth = Math.round((double) totalDepth / trials);
                        long avgAllocations = Math.round((double) totalAllocations / trials);

                        csv.writeResult(
                                "select_MoM5_" + caseName,
                                size,
                                avgTime,
                                avgComparisons,
                                avgAllocations,
                                avgDepth
                        );
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
