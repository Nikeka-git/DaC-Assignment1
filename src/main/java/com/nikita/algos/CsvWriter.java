package com.nikita.algos;

import java.io.FileWriter;
import java.io.IOException;

public class CsvWriter {
    private final String filename;
    private boolean headerWritten = false;

    public CsvWriter(String filename) {
        this.filename = filename;
    }

    public void writeResult(String algorithm, int n, Metrics metrics) {
        try (FileWriter writer = new FileWriter(filename, true)) {
            if (!headerWritten) {
                writer.write("algorithm,n,time_ms,comparisons,allocations,depth\n");
                headerWritten = true;
            }
            writer.write(String.format("%s,%d,%.3f,%d,%d,%d\n",
                    algorithm,
                    n,
                    metrics.getElapsedMillis(),
                    metrics.getComparisons(),
                    metrics.getAllocations(),
                    metrics.getMaxDepth()
            ));
        } catch (IOException e) {
            throw new RuntimeException("Error during CSV writing", e);
        }
    }
}
