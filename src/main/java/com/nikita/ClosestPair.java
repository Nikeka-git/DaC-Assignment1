package com.nikita.algos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class ClosestPair {

    public static class Point {
        public final double x, y;
        public Point(double x, double y) { this.x = x; this.y = y; }
    }

    public double findClosest(Point[] pts, Metrics metrics) {
        if (pts == null || pts.length < 2) return Double.POSITIVE_INFINITY;
        // copy arrays
        Point[] px = Arrays.copyOf(pts, pts.length);
        Point[] py = Arrays.copyOf(pts, pts.length);
        Arrays.sort(px, Comparator.comparingDouble(p -> p.x));
        Arrays.sort(py, Comparator.comparingDouble(p -> p.y));
        return closestRec(px, py, metrics);
    }

    public static double bruteForce(Point[] pts, Metrics metrics) {
        double best = Double.POSITIVE_INFINITY;
        int n = pts.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                metrics.incComparisons();
                double d = distance(pts[i], pts[j]);
                if (d < best) best = d;
            }
        }
        return best;
    }

    private double closestRec(Point[] px, Point[] py, Metrics metrics) {
        return rec(px, py, metrics);
    }

    private double rec(Point[] px, Point[] py, Metrics metrics) {
        int n = px.length;
        if (n <= 3) {
            return bruteForce(px, metrics);
        }

        metrics.enterRecursion();

        int mid = n / 2;
        Point midPoint = px[mid];

        Point[] pxLeft = Arrays.copyOfRange(px, 0, mid);
        Point[] pxRight = Arrays.copyOfRange(px, mid, n);

        List<Point> leftList = new ArrayList<>();
        List<Point> rightList = new ArrayList<>();
        for (Point p : py) {
            if (p.x < midPoint.x || (p.x == midPoint.x && indexOf(pxLeft, p) >= 0)) {
                leftList.add(p);
            } else {
                rightList.add(p);
            }
        }
        Point[] pyLeft = leftList.toArray(new Point[0]);
        Point[] pyRight = rightList.toArray(new Point[0]);

        double dl = rec(pxLeft, pyLeft, metrics);
        double dr = rec(pxRight, pyRight, metrics);
        double d = Math.min(dl, dr);

        List<Point> strip = new ArrayList<>();
        for (Point p : py) {
            if (Math.abs(p.x - midPoint.x) < d) strip.add(p);
        }

        double best = d;
        for (int i = 0; i < strip.size(); i++) {
            Point pi = strip.get(i);
            for (int j = i + 1; j < strip.size() && (strip.get(j).y - pi.y) < best; j++) {
                metrics.incComparisons();
                double dist = distance(pi, strip.get(j));
                if (dist < best) best = dist;
            }
        }

        metrics.exitRecursion();
        return best;
    }

    private int indexOf(Point[] arr, Point p) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == p) return i;
            if (Double.compare(arr[i].x, p.x) == 0 && Double.compare(arr[i].y, p.y) == 0) return i;
        }
        return -1;
    }

    private static double distance(Point a, Point b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        return Math.hypot(dx, dy);
    }
}
