package hw4.puzzle;

import edu.princeton.cs.algs4.Queue;

public class Board implements WorldState{
    private int[][] tiles;
    private static final int BLANK = 0;

    public Board(int[][] tiles) {
        int N = tiles.length;
        this.tiles = new int[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                this.tiles[i][j] = tiles[i][j];
            }
        }
    }

    public int tileAt(int i, int j) {
        if (i < 0 || i >= size()) {
            throw new java.lang.IndexOutOfBoundsException("Index out of range");
        }

        if (j < 0 || j >= size()) {
            throw new java.lang.IndexOutOfBoundsException("Index out of range");
        }

        return tiles[i][j];
    }

    public int size() {
        return tiles.length;
    }

    @Override
    public Iterable<WorldState> neighbors() {
        Queue<WorldState> neighbors = new Queue<>();
        int N = size();
        int bug = -1;
        int zug = -1;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (tileAt(i, j) == BLANK) {
                    bug = i;
                    zug = j;
                }
            }
        }

        int[][] neighborTiles = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                neighborTiles[i][j] = tileAt(i, j);
            }
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (Math.abs(-bug + i) + Math.abs(j - zug) - 1 == 0) {
                    neighborTiles[bug][zug] = neighborTiles[i][j];
                    neighborTiles[i][j] = BLANK;
                    Board neighbor = new Board(neighborTiles);
                    neighbors.enqueue(neighbor);
                    neighborTiles[i][j] = neighborTiles[bug][zug];
                    neighborTiles[bug][zug] = BLANK;
                }
            }
        }

        return neighbors;
    }

    public int hamming() {
        int distance = 0;
        int N = size();

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int tileAt = tileAt(i, j);
                if (tileAt != BLANK && tileAt != (N * i) + (j + 1)) {
                    distance++;
                }
            }
        }

        return distance;
    }

    public int manhattan() {
        int distance = 0;
        int N = size();

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int value = tileAt(i, j);
                if (value != BLANK) {
                    int x = (int) Math.ceil((double) value / N) - 1;
                    int y = (value - 1) % N;
                    distance += Math.abs(i - x) + Math.abs(j - y);
                }
            }
        }

        return distance;
    }

    @Override
    public int estimatedDistanceToGoal() {
        return manhattan();
    }

    @Override
    public boolean equals(Object y) {
        if (this == y) {
            return true;
        }

        if (y == null || getClass() != y.getClass()) {
            return false;
        }

        Board other = (Board) y;

        if (size() != other.size()) {
            return false;
        }

        int N = size();
        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < N; ++j) {
                if (tileAt(i, j) != other.tileAt(i, j)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        int N = size();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tileAt(i,j)));
            }
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }
}
