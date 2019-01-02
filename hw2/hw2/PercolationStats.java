package hw2;

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.introcs.StdStats;

public class PercolationStats {
    private double mean;
    private double stddev;
    private double confidenceLow;
    private double confidenceHigh;
    private final double Z_VALUE = 1.96;

    public PercolationStats(int N, int T, PercolationFactory pf) {
        if (N <= 0 || T <= 0) {
            throw new IllegalArgumentException("N and T cannot be less than or equal to 0");
        }

        double numberOfSites = N * N;
        double[] thresholdEstimates = new double[T];

        for (int t = 0; t < T; ++t) {
            Percolation p = pf.make(N);

            while (!p.percolates()) {
                int row = StdRandom.uniform(N);
                int col = StdRandom.uniform(N);

                while (p.isOpen(row, col)) {
                    row = StdRandom.uniform(N);
                    col = StdRandom.uniform(N);
                }

                p.open(row, col);
            }

            thresholdEstimates[t] = p.numberOfOpenSites() / numberOfSites;
        }

        mean = StdStats.mean(thresholdEstimates);
        stddev = StdStats.stddev(thresholdEstimates);

        double marginOfError  = (Z_VALUE * stddev) / Math.sqrt(T);
        confidenceLow = mean - marginOfError;
        confidenceHigh = mean + marginOfError;
    }

    public double mean() {
        return mean;
    }

    public double stddev() {
        return stddev;
    }

    public double confidenceLow() {
        return confidenceLow;
    }

    public double confidenceHigh() {
        return confidenceHigh;
    }
}
