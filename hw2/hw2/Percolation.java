package hw2;

//import edu.princeton.cs.algs4.QuickFindUF;
import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private boolean[][] sites;
    private WeightedQuickUnionUF disjointSet;
    //private QuickFindUF disjointSet;

    private int virtualTopSite;
    private int virtualBottomSite;
    private int numberOfOpenSites;

    public Percolation(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException("N is less than or equal to 0");
        }

        int numberOfSites = N * N;
        numberOfOpenSites = 0;

        //sites = new boolean[numberOfSites];
        sites = new boolean[N][N];

        // Virtual sites
        virtualTopSite = numberOfSites;
        virtualBottomSite = virtualTopSite + 1;

        // Add two virtual sites
        disjointSet = new WeightedQuickUnionUF(numberOfSites + 2);
        //disjointSet = new QuickFindUF(numberOfSites + 2);

        if (sites.length > 1) {
            for (int col = 0; col < sites.length; ++col) {
                disjointSet.union(col, virtualTopSite);
                int bottomCol = rowColTo1D(sites.length - 1, col);
                disjointSet.union(bottomCol, virtualBottomSite);
            }
        } else {
            disjointSet.union(0, virtualBottomSite);
        }
    }

    public void open(int row, int col) {
        if (isOpen(row, col)) {
            return;
        }

        sites[row][col] = true;
        numberOfOpenSites++;

        if (row > 0) {
            connectSites(row, col, row - 1, col);
        }

        if (row < sites.length - 1) {
            connectSites(row, col, row + 1, col);
        }

        if (col > 0) {
            connectSites(row, col, row, col - 1);
        }

        if (col < sites.length - 1) {
            connectSites(row, col, row, col + 1);
        }
    }

    public boolean isOpen(int row, int col) {
        checkIndices(row, col);
        return sites[row][col];
    }

    public boolean isFull(int row, int col) {
        if (isOpen(row, col)) {
            int p = rowColTo1D(row, col);

//            int count = disjointSet.count();
//            int findP = disjointSet.find(p);
//            int findBot = disjointSet.find(virtualBottomSite);
//            int findTop = disjointSet.find(virtualTopSite);

            return disjointSet.connected(p, virtualTopSite);
        }

        return false;
    }

    public int numberOfOpenSites() {
        return numberOfOpenSites;
    }

    public boolean percolates() {
        return disjointSet.connected(virtualTopSite, virtualBottomSite);
    }

    private void connectSites(int r1, int c1, int r2, int c2) {
        if (isOpen(r1, c1) && isOpen(r2, c2)) {
            int index1 = rowColTo1D(r1, c1);
            int index2 = rowColTo1D(r2, c2);
            disjointSet.union(index1, index2);
        }
    }

    private void checkIndices(int row, int col) {
        checkIndex(row);
        checkIndex(col);
    }

    private void checkIndex(int index) {
        if (index < 0 || index > sites.length - 1) {
            throw new IndexOutOfBoundsException(
                "Index " + index + " is out of bounds for N = " + sites.length);
        }
    }

    private int rowColTo1D(int row, int col) {
        return (sites.length * row) + col;
    }

    public static void main(String[] args) {
        PercolationFactory pf = new PercolationFactory();

        for (int N = 1; N < 10000; N *= 2) {
            Stopwatch timer = new Stopwatch();
            PercolationStats ps = new PercolationStats(N, 2, pf);

            double elapsedTime = timer.elapsedTime();
            double mean = ps.mean();
            double stddev = ps.stddev();
            double low = ps.confidenceLow();
            double high = ps.confidenceHigh();

            String formatString = "N: %d" + "\nTime: %f" + "\nMean: %f"
                + "\nStddev: %f" + "\nLow: %f" + "\nHigh: %f" + "\n";

            String printString = String.format(formatString,
                N, elapsedTime, mean, stddev, low, high);

            System.out.println(printString);
        }
    }
}
