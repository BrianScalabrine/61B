package lab11.graphs;

import edu.princeton.cs.algs4.MinPQ;

import java.util.Comparator;

/**
 *  @author Josh Hug
 */
public class MazeAStarPath extends MazeExplorer {
    private int s;
    private int t;
    private boolean targetFound = false;
    private Maze maze;

    public MazeAStarPath(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        maze = m;
        s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
        distTo[s] = 0;
        edgeTo[s] = s;
    }

    /** Estimate of the distance from v to the target. */
    private int h(int v) {
        return Math.abs(maze.toX(v) - maze.toX(t)) + Math.abs(maze.toY(v) - maze.toY(t));
    }

    /** Finds vertex estimated to be closest to target. */
    private int findMinimumUnmarked() {
        return -1;
        /* You do not have to use this method. */
    }

    /** Performs an A star search from vertex s. */
    private void astar(int s) {
        MinPQ<Integer> fringe = new MinPQ<>(Comparator.comparingInt(this::h));
        fringe.insert(s);

        while (!fringe.isEmpty()) {
            int v = fringe.delMin();
            marked[v] = true;
            announce();

            if (v == t) {
                return;
            }

            for (int w : maze.adj(v)) {
                if (!marked[w] && w != edgeTo[v]) {
                    edgeTo[w] = v;
                    distTo[w] = distTo[v] + 1;
                    fringe.insert(w);
                }
            }
        }
    }

    @Override
    public void solve() {
        astar(s);
    }

}

