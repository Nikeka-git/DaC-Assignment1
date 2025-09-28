package com.nikita.benchmark;

import com.nikita.algos.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

public class BenchmarkRunner {
    private final int[] sizes;
    private final int runs;
    private final String outFile;

    public BenchmarkRunner(int[] sizes, int runs, String outFile) {
        this.sizes = sizes;
        this.runs = runs;
        this.outFile = outFile;
    }

    public void run(String algo) {
        try (CsvWriter csv = new CsvWriter(outFile)) {
            switch (algo.toLowerCase()) {
                case "quicksort" -> runSort(csv, new QuickSort());
                case "select"    -> runSelect(csv, new DeterministicSelect());
                case "closest"   -> runClosest(csv, new ClosestPair());
                default -> throw new IllegalArgumentException("Unknown algo: " + algo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runSort(CsvWriter csv, SortAlgorithm algo) throws Exception {
        Map<String, Function<Integer, int[]>> generators = makeGenerators();
        for (Map.Entry<String, Function<Integer, int[]>> entry : generators.entrySet()) {
            String caseName = entry.getKey();
            Function<Integer, int[]> generator = entry.getValue();
            for (int size : sizes) {
                double totalTime = 0;
                long totalComparisons = 0, totalAllocations = 0, totalDepth = 0;
                for (int t = 0; t < runs; t++) {
                    int[] input = generator.apply(size);
                    Metrics m = new Metrics();
                    long start = System.nanoTime();
                    algo.sort(input, m);
                    long end = System.nanoTime();
                    totalTime += (end - start) / 1_000_000.0;
                    totalComparisons += m.getComparisons();
                    totalAllocations += m.getAllocations();
                    totalDepth += m.getMaxDepth();
                }
                csv.writeResult(
                        algo.name() + "_" + caseName,
                        size,
                        totalTime / runs,
                        totalComparisons / runs,
                        totalAllocations / runs,
                        totalDepth / runs
                );
            }
        }
    }

    private void runSelect(CsvWriter csv, DeterministicSelect sel) throws Exception {
        Map<String, Function<Integer, int[]>> generators = makeGenerators();
        Random rnd = new Random();
        for (Map.Entry<String, Function<Integer, int[]>> entry : generators.entrySet()) {
            String caseName = entry.getKey();
            Function<Integer, int[]> generator = entry.getValue();
            for (int size : sizes) {
                double totalTime = 0;
                long totalComparisons = 0, totalAllocations = 0, totalDepth = 0;
                for (int t = 0; t < runs; t++) {
                    int[] input = generator.apply(size);
                    int k = rnd.nextInt(size);
                    Metrics m = new Metrics();
                    long start = System.nanoTime();
                    sel.select(Arrays.copyOf(input, input.length), k, m);
                    long end = System.nanoTime();
                    totalTime += (end - start) / 1_000_000.0;
                    totalComparisons += m.getComparisons();
                    totalAllocations += m.getAllocations();
                    totalDepth += m.getMaxDepth();
                }
                csv.writeResult(
                        "select_MoM5_" + caseName,
                        size,
                        totalTime / runs,
                        totalComparisons / runs,
                        totalAllocations / runs,
                        totalDepth / runs
                );
            }
        }
    }

    private void runClosest(CsvWriter csv, ClosestPair cp) throws Exception {
        Random rnd = new Random();
        for (int size : sizes) {
            double totalTime = 0;
            long totalComparisons = 0, totalDepth = 0;
            for (int t = 0; t < runs; t++) {
                ClosestPair.Point[] pts = new ClosestPair.Point[size];
                for (int i = 0; i < size; i++) {
                    pts[i] = new ClosestPair.Point(rnd.nextDouble() * 1e6, rnd.nextDouble() * 1e6);
                }
                Metrics m = new Metrics();
                long start = System.nanoTime();
                cp.findClosest(Arrays.copyOf(pts, pts.length), m);
                long end = System.nanoTime();
                totalTime += (end - start) / 1_000_000.0;
                totalComparisons += m.getComparisons();
                totalDepth += m.getMaxDepth();
            }
            csv.writeResult(
                    "closest_divide_random",
                    size,
                    totalTime / runs,
                    totalComparisons / runs,
                    0,
                    totalDepth / runs
            );
        }
    }

    private Map<String, Function<Integer, int[]>> makeGenerators() {
        Map<String, Function<Integer, int[]>> g = new LinkedHashMap<>();
        g.put("random", n -> new Random().ints(n, 0, 1_000_000).toArray());
        g.put("sorted", n -> IntStream.range(0, n).toArray());
        g.put("reversed", n -> IntStream.iterate(n - 1, i -> i - 1).limit(n).toArray());
        g.put("constant", n -> {
            int[] arr = new int[n]; Arrays.fill(arr, 42); return arr;
        });
        return g;
    }
}
