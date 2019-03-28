import java.awt.Color;

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private Picture picture;
    private Double[][] energyMatrix;

    public SeamCarver(Picture picture) {
        this.picture = picture;
        this.energyMatrix = new Double[picture.height()][picture.width()];
    }

    public Picture picture() {
        return picture;
    }

    public int width() {
        return picture.width();
    }

    public int height() {
        return picture.height();
    }

    public double energy(int x, int y) {
        int cols = width();
        int rows = height();

        if (x < 0 || x >= cols) {
            throw new IndexOutOfBoundsException("x is out of bounds");
        } else if (y < 0 || y >= rows) {
            throw new IndexOutOfBoundsException("y is out of bounds");
        }

        if (energyMatrix[y][x] != null) {
            return energyMatrix[y][x];
        }

        Color leftColor = picture.get((x > 0 ? x - 1 : cols - 1), y);
        Color rightColor = picture.get((x < cols - 1 ? x + 1 : 0), y);
        double gradientX = gradient(leftColor, rightColor);

        Color topColor = picture.get(x, (y > 0 ? y - 1 : rows - 1));
        Color bottomColor = picture.get(x, (y < rows - 1 ? y + 1 : 0));
        double gradientY = gradient(topColor, bottomColor);

        energyMatrix[y][x] = gradientX + gradientY;
        return energyMatrix[y][x];
    }

    private static int difference(int a, int b) {
        return a >= b ? a - b : b - a;
    }

    private static double gradient(Color c1, Color c2) {
        int r = difference(c1.getRed(), c2.getRed());
        int g = difference(c1.getGreen(), c2.getGreen());
        int b = difference(c1.getBlue(), c2.getBlue());

        return (r * r) + (g * g) + (b * b);
    }

    public int[] findHorizontalSeam() {
        transpose();

        // Get vertical seam for transposed matrix
        int[] horizontalSeam = findVerticalSeam();

        transpose();

        return horizontalSeam;
    }

    public int[] findVerticalSeam() {
        int cols = width();
        int rows = height();

        int[] verticalSeam = new int[rows];
        double minimumCost = Integer.MAX_VALUE;

        // For every column find vertical seam with least total energy
        for (int col = 0; col < cols; col++) {
            // Current vertical seam being looked at
            int[] currentVerticalSeam = new int[rows];

            // Starting pixel
            double currentCost = energy(col, 0);
            currentVerticalSeam[0] = col;

            // Travel down each row, choosing the connecting pixel with the least energy
            for (int row = 0; row < rows - 1; row++) {
                int rowBelow = row + 1;

                // Set pixel directly below current as minimum for now
                double minimumEnergy = energy(col, rowBelow);
                currentVerticalSeam[rowBelow] = col;

                if (col > 0) {
                    // Row below and column to the left, if it exists
                    int colToLeft = col - 1;
                    double energy = energy(colToLeft, rowBelow);
                    if (energy < minimumEnergy) {
                        minimumEnergy = energy;
                        currentVerticalSeam[rowBelow] = colToLeft;
                    }
                }

                if (col < cols - 1) {
                    // Row below and column to the right, if it exists
                    int colToRight = col + 1;
                    double energy = energy(colToRight, rowBelow);
                    if (energy < minimumEnergy) {
                        minimumEnergy = energy;
                        currentVerticalSeam[rowBelow] = colToRight;
                    }
                }

                currentCost += minimumEnergy;
                if (currentCost >= minimumCost) {
                    // Not the minimum cost path, break out
                    break;
                }
            }

            if (currentCost < minimumCost) {
                minimumCost = currentCost;
                verticalSeam = currentVerticalSeam;
            }
        }

        return verticalSeam;
    }

    private void transpose() {
        int cols = width();
        int rows = height();

        Picture transposePicture = new Picture(rows, cols);
        Double[][] transposeMatrix = new Double[cols][rows];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                transposePicture.set(r, c, picture.get(c, r));
                transposeMatrix[c][r] = energyMatrix[r][c];
            }
        }

        picture = transposePicture;
        energyMatrix = transposeMatrix;
    }

    public void removeHorizontalSeam(int[] seam) {
        SeamRemover.removeHorizontalSeam(picture, seam);
    }

    public void removeVerticalSeam(int[] seam) {
        SeamRemover.removeVerticalSeam(picture, seam);
    }
}