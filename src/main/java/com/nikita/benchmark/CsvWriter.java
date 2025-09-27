package com.nikita.benchmark;

import com.nikita.algos.Metrics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

public class CsvWriter implements AutoCloseable {
    private final PrintWriter writer;
    private boolean headerWritten = false;

    public CsvWriter(String filename) {
        try {
            File file = new File(filename);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            boolean needHeader = !file.exists() || file.length() == 0;
            this.writer = new PrintWriter(new FileWriter(file, true)); // append
            this.headerWritten = !needHeader;
            if (needHeader) {
                writer.println("algorithm,n,time_ms,comparisons,allocations,depth");
                writer.flush();
                headerWritten = true;
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot open CSV file: " + filename, e);
        }
    }


    public synchronized void writeResult(String algorithm, int n,
                                         double timeMs,
                                         long comparisons,
                                         long allocations,
                                         long depth) {
        writer.printf(Locale.US, "%s,%d,%.6f,%d,%d,%d%n",
                algorithm,
                n,
                timeMs,
                comparisons,
                allocations,
                depth);
        writer.flush();
    }

    public synchronized void writeResult(String algorithm, int n, Metrics metrics) {
        writeResult(algorithm, n,
                metrics.getElapsedMillis(),
                metrics.getComparisons(),
                metrics.getAllocations(),
                metrics.getMaxDepth());
    }

    @Override
    public void close() {
        writer.close();
    }
}
