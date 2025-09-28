package com.nikita.cli;

import com.nikita.benchmark.BenchmarkRunner;

public class CliRunner {
    public static void main(String[] args) {
        String algo = "quicksort";
        int[] sizes = {1000};
        int runs = 1;
        String outFile = "results.csv";

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--algo" -> algo = args[++i];
                case "--size" -> sizes = new int[]{Integer.parseInt(args[++i])};
                case "--runs" -> runs = Integer.parseInt(args[++i]);
                case "--out"  -> outFile = args[++i];
            }
        }

        BenchmarkRunner runner = new BenchmarkRunner(sizes, runs, outFile);
        runner.run(algo);
    }
}
