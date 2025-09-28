package com.nikita.algos;

import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.nikita.algos.ClosestPair;

public class ClosestPairTest {

    @Test
    public void randomSmallCompareBrute() {
        Random rnd = new Random(123);
        ClosestPair cp = new ClosestPair();

        for (int t = 0; t < 20; t++) {
            int n = 200 + rnd.nextInt(1800); // up to 2000
            ClosestPair.Point[] pts = new ClosestPair.Point[n];
            for (int i = 0; i < n; i++) pts[i] = new ClosestPair.Point(rnd.nextDouble()*1e6, rnd.nextDouble()*1e6);

            Metrics m1 = new Metrics();
            double fast = cp.findClosest(pts, m1);

            Metrics m2 = new Metrics();
            double brute = ClosestPair.bruteForce(pts, m2);

            assertEquals(brute, fast, 1e-9, "Mismatch on trial n=" + n);
        }
    }

    @Test
    public void edgeAndDuplicatePoints() {
        ClosestPair cp = new ClosestPair();
        Metrics m;

        ClosestPair.Point[] a = new ClosestPair.Point[] {
                new ClosestPair.Point(0.0, 0.0),
                new ClosestPair.Point(0.0, 0.0),
                new ClosestPair.Point(100.0, 100.0)
        };
        m = new Metrics();
        double dist = cp.findClosest(a, m);
        assertEquals(0.0, dist, 1e-12);

        ClosestPair.Point[] b = new ClosestPair.Point[] {
                new ClosestPair.Point(0,0), new ClosestPair.Point(1,1)
        };
        m = new Metrics();
        assertEquals(Math.hypot(1,1), cp.findClosest(b, m), 1e-12);
    }

    @Test
    public void compareWithBruteSmall() {
        ClosestPair cp = new ClosestPair();
        Random rnd = new Random(42);
        for (int n = 2; n <= 50; n++) {
            ClosestPair.Point[] pts = new ClosestPair.Point[n];
            for (int i = 0; i < n; i++) pts[i] = new ClosestPair.Point(rnd.nextDouble()*100, rnd.nextDouble()*100);
            Metrics m1 = new Metrics();
            Metrics m2 = new Metrics();
            double fast = cp.findClosest(pts, m1);
            double brute = ClosestPair.bruteForce(pts, m2);
            assertEquals(brute, fast, 1e-9);
        }
    }
}
