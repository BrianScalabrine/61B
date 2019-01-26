package lab11.graphs;

import edu.princeton.cs.algs4.Stack;

/**
 *  @author Josh Hug
 */
public class MazeCycles extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */

    private int s;
    public int[] parentOf;
    private boolean cycleDetected = false;

    public MazeCycles(Maze m) {
        super(m);
        s = maze.xyTo1D(1, 1);
        edgeTo[s] = s;
        parentOf = new int[maze.V()];
    }

    @Override
    public void solve() {
        dfsRecursive(s);
        // dfsIterative(s);
    }

    private void dfsRecursive(int v) {
        marked[v] = true;
        announce();

        if (cycleDetected) {
            return;
        }

        for (int w : maze.adj(v)) {
            if (marked[w]) {
                if (w != parentOf[v]) {
                    cycleDetected = true;

                    int previous = parentOf[v];
                    edgeTo[v] = previous;

                    while (previous != w) {
                        edgeTo[previous] = parentOf[previous];
                        previous = parentOf[previous];
                    }

                    edgeTo[w] = v;

                    announce();
                    return;
                }
            } else {
                parentOf[w] = v;
                dfsRecursive(w);
                if (cycleDetected) {
                    return;
                }
            }
        }
    }

    private void dfsIterative(int start) {
        Stack<Integer> fringe = new Stack<>();

        fringe.push(start);
        marked[start] = true;

        while (!fringe.isEmpty()) {
            int v = fringe.pop();
            for (int w : maze.adj(v)) {
                edgeTo[w] = v;
                announce();
                distTo[w] = distTo[v] + 1;
            }
        }
    }
}

