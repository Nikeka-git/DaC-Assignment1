package com.nikita.algos;

public class Metrics {
    private long comparisons = 0;
    private long allocations = 0;
    private int currentDepth = 0;
    private long maxDepth = 0;

    private long startTime = 0;
    private long endTime = 0;

    public Metrics() {}

    // Getters
    public long getComparisons() { return comparisons; }
    public long getAllocations() { return allocations; }
    public long getMaxDepth() { return maxDepth; }

    // Timers
    public void startTimer() { startTime = System.nanoTime(); }
    public void endTimer() { endTime = System.nanoTime(); }

    public double getElapsedMillis() {
        return (endTime - startTime) / 1_000_000.0;
    }

    // Metrics
    public void incComparisons() { comparisons++; }
    public void incAllocations() { allocations++; }
    public void enterRecursion() {
        currentDepth++;
        if (currentDepth > maxDepth) maxDepth = currentDepth;
    }
    public void exitRecursion() { currentDepth--; }
}
