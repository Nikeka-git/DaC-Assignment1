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
        CsvWriter csv = null;
        try {
            csv = new CsvWriter(outFile);

            switch (algo.toLowerCase()) {
                case "quicksort" -> runSort(csv, new QuickSort());
                case "mergesort" -> runSort(csv, new MergeSort());
                case "select"    -> runSelect(csv, new DeterministicSelect());
                case "closest"   -> runClosest(csv, new ClosestPair());
                default -> throw new IllegalArgumentException("Unknown algo: " + algo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (csv != null) {
                try {
                    csv.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void runSort(CsvWriter csv, SortAlgorithm algo) throws Exception {
        Map<String, Function<Integer, int[]>> generators = makeGenerators();
        Random rnd = new Random(12345);

        /*System.out.println("RUN SORT: algo=" + algo.name());
        System.out.println("sizes = " + Arrays.toString(sizes));
        System.out.println("runs = " + runs);
        System.out.println("generators = " + generators.keySet());*/

        for (var entry : generators.entrySet()) {
            String caseName = entry.getKey();
            Function<Integer, int[]> generator = entry.getValue();

            for (int n : sizes) {
                double totalTime = 0.0;
                long totalComparisons = 0;
                long totalAllocations = 0;
                long totalDepth = 0;

                for (int t = 0; t < runs; t++) {
                    int[] input = generator.apply(n);
                    Metrics m = new Metrics();
                    long start = System.nanoTime();
                    algo.sort(input, m);
                    long end = System.nanoTime();

                    totalTime += (end - start) / 1_000_000.0;
                    totalComparisons += m.getComparisons();
                    totalAllocations += m.getAllocations();
                    totalDepth += m.getMaxDepth();
                }

                double avgTime = totalTime / runs;
                long avgComparisons = Math.round((double) totalComparisons / runs);
                long avgAlloc = Math.round((double) totalAllocations / runs);
                long avgDepth = Math.round((double) totalDepth / runs);

                csv.writeResult(algo.name() + "_" + caseName, n, avgTime, avgComparisons, avgAlloc, avgDepth);
                System.out.printf("  %s n=%d avg=%.6fms comps=%d alloc=%d depth=%d%n",
                        algo.name() + "_" + caseName, n, avgTime, avgComparisons, avgAlloc, avgDepth);

            }
        }
    }


    private void runSelect(CsvWriter csv, DeterministicSelect sel) throws Exception {
        Map<String, Function<Integer, int[]>> generators = makeGenerators();
        Random rnd = new Random(12345);

        System.out.println("-> Running select: MoM5");
        for (var entry : generators.entrySet()) {
            String caseName = entry.getKey();
            Function<Integer, int[]> generator = entry.getValue();

            for (int n : sizes) {
                double totalTime = 0.0;
                long totalComparisons = 0;
                long totalAllocations = 0;
                long totalDepth = 0;

                for (int t = 0; t < runs; t++) {
                    int[] input = generator.apply(n);
                    int k = rnd.nextInt(Math.max(1, n));
                    Metrics m = new Metrics();

                    long start = System.nanoTime();
                    sel.select(Arrays.copyOf(input, input.length), k, m);
                    long end = System.nanoTime();

                    totalTime += (end - start) / 1_000_000.0;
                    totalComparisons += m.getComparisons();
                    totalAllocations += m.getAllocations();
                    totalDepth += m.getMaxDepth();
                }

                double avgTime = totalTime / runs;
                long avgComparisons = Math.round((double) totalComparisons / runs);
                long avgAlloc = Math.round((double) totalAllocations / runs);
                long avgDepth = Math.round((double) totalDepth / runs);

                csv.writeResult("select_MoM5_" + caseName, n, avgTime, avgComparisons, avgAlloc, avgDepth);
                System.out.printf("  select MoM5 %s n=%d avg=%.6fms comps=%d alloc=%d depth=%d%n",
                        caseName, n, avgTime, avgComparisons, avgAlloc, avgDepth);
            }
        }
    }


    private void runClosest(CsvWriter csv, ClosestPair cp) throws Exception {
        Random rnd = new Random(12345);
        Map<String, Function<Integer, ClosestPair.Point[]>> pointGenerators = new LinkedHashMap<>();
        pointGenerators.put("random", size -> {
            ClosestPair.Point[] pts = new ClosestPair.Point[size];
            Random r = new Random();
            for (int i = 0; i < size; i++)
                pts[i] = new ClosestPair.Point(r.nextDouble() * 1e6, r.nextDouble() * 1e6);
            return pts;
        });

        System.out.println("-> Running closest pair (divide & conquer)");
        for (var entry : pointGenerators.entrySet()) {
            String caseName = entry.getKey();
            Function<Integer, ClosestPair.Point[]> generator = entry.getValue();

            for (int n : sizes) {
                double totalTime = 0.0;
                long totalComparisons = 0;
                long totalDepth = 0;

                for (int t = 0; t < runs; t++) {
                    ClosestPair.Point[] pts = generator.apply(n);
                    Metrics m = new Metrics();

                    long start = System.nanoTime();
                    cp.findClosest(Arrays.copyOf(pts, pts.length), m);
                    long end = System.nanoTime();

                    totalTime += (end - start) / 1_000_000.0;
                    totalComparisons += m.getComparisons();
                    totalDepth += m.getMaxDepth();
                }

                double avgTime = totalTime / runs;
                long avgComparisons = Math.round((double) totalComparisons / runs);
                long avgDepth = Math.round((double) totalDepth / runs);

                csv.writeResult("closest_divide_" + caseName, n, avgTime, avgComparisons, 0L, avgDepth);
                System.out.printf("  closest %s n=%d avg=%.6fms comps=%d depth=%d%n",
                        caseName, n, avgTime, avgComparisons, avgDepth);
            }
        }
    }


    private Map<String, Function<Integer, int[]>> makeGenerators() {
        Map<String, Function<Integer, int[]>> g = new LinkedHashMap<>();
        g.put("random", n -> new Random().ints(n, 0, 1_000_000).toArray());
        g.put("sorted", n -> IntStream.range(0, n).toArray());
        g.put("reversed", n -> {
            int[] a = IntStream.range(0, n).toArray();
            for (int i = 0, j = n - 1; i < j; i++, j--) {
                int tmp = a[i];
                a[i] = a[j];
                a[j] = tmp;
            }
            return a;
        });
        g.put("constant", n -> {
            int[] arr = new int[n];
            Arrays.fill(arr, 42);
            return arr;
        });
        g.put("partially_sorted", n -> {
            int[] arr = IntStream.range(0, n).toArray();
            Random rnd = new Random();
            for (int i = 0; i < n / 10; i++) {
                int i1 = rnd.nextInt(n), i2 = rnd.nextInt(n);
                int tmp = arr[i1];
                arr[i1] = arr[i2];
                arr[i2] = tmp;
            }
            return arr;
        });
        return g;
    }
}
