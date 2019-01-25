package hw4.puzzle;

import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import java.util.Comparator;

public class Solver {
    private class SearchNode {
        private WorldState state;
        private SearchNode previous;
        private int movesMade;
        private int heuristic;

        public SearchNode(WorldState state, SearchNode previous, int movesMade) {
            this.state = state;
            this.previous = previous;
            this.movesMade = movesMade;
            this.heuristic = state.estimatedDistanceToGoal();
        }

        public int priority() {
            return movesMade + heuristic;
        }
    }

    private int moves;
    private Stack<WorldState> solution;

    public Solver(WorldState initial) {
        solution = new Stack<>();

        MinPQ<SearchNode> fringe =
            new MinPQ<>(Comparator.comparingInt(SearchNode::priority));

        // Start node
        fringe.insert(new SearchNode(initial, null, 0));

        while (!fringe.isEmpty()) {
            SearchNode node = fringe.delMin();
            if (node.state.isGoal()) {
                // Done, set up solution
                moves = node.movesMade;

                while (node != null) {
                    solution.push(node.state);
                    node = node.previous;
                }

                break;
            }

            for (WorldState neighbor : node.state.neighbors()) {
                // Add neighbors to fringe, after checking not equal to grandparent
                if (node.previous == null || !neighbor.equals(node.previous.state)) {
                    fringe.insert(new SearchNode(neighbor, node, node.movesMade + 1));
                }
            }
        }
    }

    public int moves() {
        return moves;
    }

    public Iterable<WorldState> solution() {
        return solution;
    }
}
