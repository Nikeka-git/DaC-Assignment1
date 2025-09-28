# Assignment 1 Report

I started the project by creating a Maven project in IntelliJ IDEA, setting up JUnit 5, and verifying that builds and tests run correctly. A minimal example `App.java` and `AppTest.java` were added, and GitHub Actions were configured to automatically build and test the project.

### Architecture Notes
All algorithms track metrics using a `Metrics` class. Comparisons, allocations, and recursion depth are counted, with `enterRecursion()` and `exitRecursion()` calls ensuring safe recursion. MergeSort uses a reusable buffer and a cutoff for small arrays to reduce recursion overhead. QuickSort always recurses on the smaller partition and uses a randomized pivot to limit stack depth. Deterministic Select only recurses into the partition containing the k-th element. Closest Pair reuses arrays in recursion and limits neighbor checks during the merge step.

### MergeSort
The array is split in half, each part is sorted recursively, and then merged linearly. The recurrence T(n) = 2T(n/2) + Θ(n) gives Θ(n log n) complexity according to Master Theorem (Case 2). The cutoff for small arrays reduces recursion overhead, and the reusable buffer minimizes allocations. Benchmark results show expected n log n scaling and recursion depth around log₂ n.

### QuickSort
QuickSort uses a randomized pivot and recurses on the smaller partition first. Average-case recurrence is T(n) = T(k) + T(n-k-1) + Θ(n), giving Θ(n log n) with Akra–Bazzi intuition. Randomization prevents worst-case behavior, and smaller-first recursion keeps stack depth bounded. Benchmarks confirm typical n log n performance and depth around 2 log₂ n. Constant arrays illustrate the importance of pivot randomization.

### Deterministic Select (Median-of-Medians)
Elements are grouped by five, the median-of-medians is chosen as pivot, and recursion only occurs into the partition containing the k-th element. The recurrence T(n) = T(n/5) + T(7n/10) + Θ(n) gives linear time. Benchmarks confirm the expected O(n) performance and small recursion depth.

### Closest Pair of Points
The algorithm sorts points by x-coordinate, splits recursively, and merges with a limited neighbor scan by y-coordinate. Recurrence T(n) = 2T(n/2) + Θ(n) gives Θ(n log n). Array reuse reduces allocations. Benchmarks show n log n scaling and recursion depth around log₂ n.

### Plots and Observations
The benchmark results were used to generate time vs n and recursion depth vs n plots for all algorithms. MergeSort shows consistent n log n scaling across different input types, with slightly lower times for already sorted or constant arrays due to fewer comparisons and efficient merging. QuickSort generally scales as n log n for random, sorted, reversed, and partially sorted arrays, but exhibits extremely high time on constant arrays because all elements are equal and the pivot selection degenerates. Deterministic Select maintains linear time for most inputs, while constant arrays again highlight the cost of repeated comparisons in a degenerate partition. Closest Pair of Points scales roughly as n log n, confirming the expected behavior of divide-and-conquer in two dimensions. Recursion depth plots indicate that MergeSort and Closest Pair remain shallow (around log₂ n), QuickSort depth is slightly higher but bounded due to smaller-first recursion, and Select depth is minimal as recursion occurs only into the needed partition. Overall, the plots confirm theoretical expectations while also revealing small constant-factor effects, such as better cache utilization in MergeSort and increased comparisons in degenerate QuickSort or Select cases.

### Summary
The project demonstrates safe and efficient divide-and-conquer implementations. Metrics collection confirms bounded recursion depth and minimal allocations. Performance measurements align closely with theoretical predictions for all algorithms.

