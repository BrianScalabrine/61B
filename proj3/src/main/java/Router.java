import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    private static class SearchNode {
        long v;
        double priority;

        public SearchNode(long v, double priority) {
            this.v = v;
            this.priority = priority;
        }

        public double priority() {
            return priority;
        }
    }

    /**
     * Return a List of longs representing the shortest path from the node
     * closest to a start location and the node closest to the destination
     * location.
     * @param g The graph to use.
     * @param stlon The longitude of the start location.
     * @param stlat The latitude of the start location.
     * @param destlon The longitude of the destination location.
     * @param destlat The latitude of the destination location.
     * @return A list of node id's in the order visited on the shortest path.
     */
    public static List<Long> shortestPath(GraphDB g, double stlon, double stlat,
                                          double destlon, double destlat) {
        long s = g.closest(stlon, stlat);
        long t = g.closest(destlon, destlat);

        LinkedList<Long> shortestPath = new LinkedList<>();
        Set<Long> marked = new HashSet<>();
        Map<Long, SearchNode> edgeTo = new HashMap<>();
        Map<Long, Double> best = new HashMap<>();
        best.put(s, 0.0);

        Queue<SearchNode> fringe = new PriorityQueue<>(
            Comparator.comparingDouble(SearchNode::priority));
        fringe.add(new SearchNode(s, g.distance(s, t)));

        while (!fringe.isEmpty()) {
            SearchNode node = fringe.poll();

            if (marked.contains(node.v)) {
                continue;
            }

            if (node.v == t) {
                while (node != null) {
                    shortestPath.addFirst(node.v);
                    node = edgeTo.get(node.v);
                }
                break;
            }

            marked.add(node.v);

            // Best known d (distance) from s (start) to v
            Double dsv = best.get(node.v);
            if (dsv != null) {
                for (long w : g.adjacent(node.v)) {
                    // Best known distance from start to w
                    Double dsw = best.get(w);

                    // Great circle distance from v to w
                    double edvw = g.distance(node.v, w);

                    if (dsw == null || dsv + edvw < dsw) {
                        // Update best distance from start to w
                        best.put(w, dsv + edvw);

                        // Update edge to
                        edgeTo.put(w, node);

                        // Great circle distance from w to t (target); heuristic
                        double h = g.distance(w, t);

                        // Add w to the fringe with priority of best known distance + heuristic
                        fringe.add(new SearchNode(w, dsv + edvw + h));
                    }
                }
            }
        }

        return shortestPath;
    }

    /**
     * Create the list of directions corresponding to a route on the graph.
     * @param g The graph to use.
     * @param route The route to translate into directions. Each element
     *              corresponds to a node from the graph in the route.
     * @return A list of NavigatiionDirection objects corresponding to the input
     * route.
     */
    public static List<NavigationDirection> routeDirections(GraphDB g, List<Long> route) {
        if (route.size() < 2) {
            throw new InvalidParameterException("Not enough elements passed in for route");
        }

        List<NavigationDirection> routeDirections = new ArrayList<>();

        Iterator<Long> iterator = route.iterator();
        long previous = iterator.next();
        long current = iterator.next();

        NavigationDirection nd = new NavigationDirection();
        nd.direction = NavigationDirection.START;
        nd.way = getWayName(g, previous, current);
        nd.distance = g.distance(previous, current);

        while (iterator.hasNext()) {
            long next = iterator.next();

            String way = getWayName(g, current, next);
            if (!way.equals(nd.way)) {
                // Way change, add old direction to route directions
                routeDirections.add(nd);

                // Calculate relative bearing
                double heading = g.bearing(previous, current);
                double bearing = g.bearing(current, next);
                double relativeBearing = getRelativeBearing(heading, bearing);

                // Create new direction with new way
                nd = new NavigationDirection();
                nd.direction = getDirection(relativeBearing);
                nd.way = way;
                nd.distance = 0;
            }

            // Accumulate distance from previous to current
            nd.distance += g.distance(current, next);

            previous = current;
            current = next;
        }

        // Add final direction
        routeDirections.add(nd);

        return routeDirections;
    }

    private static String getWayName(GraphDB g, long from, long to) {
        GraphDB.Edge way = g.getEdge(from, to);
        if (way != null && !way.name.isEmpty()) {
            return way.name;
        }
        //return NavigationDirection.UNKNOWN_ROAD;
        return "";
    }

    private static double getRelativeBearing(double heading, double bearing) {
        // Relative + Heading (in True) = True Bearing
        // Relative = True Bearing - Heading

        double relativeBearing = (bearing - heading) % 360;
        if (relativeBearing < -180.0) {
            relativeBearing += 360.0;
        }
        if (relativeBearing >= 180.0) {
            relativeBearing -= 360.0;
        }

        return relativeBearing;
    }

    private static int getDirection(double bearing) {
        if (-15 < bearing && bearing < 15) {
            return NavigationDirection.STRAIGHT;
        } else if (-30 < bearing && bearing < 30) {
            return (bearing < 0)
                ? NavigationDirection.SLIGHT_LEFT : NavigationDirection.SLIGHT_RIGHT;
        } else if (-100 < bearing && bearing < 100) {
            return (bearing < 0)
                ? NavigationDirection.LEFT : NavigationDirection.RIGHT;
        } else {
            return (bearing < 0)
                ? NavigationDirection.SHARP_LEFT : NavigationDirection.SHARP_RIGHT;
        }
    }

    /**
     * Class to represent a navigation direction, which consists of 3 attributes:
     * a direction to go, a way, and the distance to travel for.
     */
    public static class NavigationDirection {

        /** Integer constants representing directions. */
        public static final int START = 0;
        public static final int STRAIGHT = 1;
        public static final int SLIGHT_LEFT = 2;
        public static final int SLIGHT_RIGHT = 3;
        public static final int RIGHT = 4;
        public static final int LEFT = 5;
        public static final int SHARP_LEFT = 6;
        public static final int SHARP_RIGHT = 7;

        /** Number of directions supported. */
        public static final int NUM_DIRECTIONS = 8;

        /** A mapping of integer values to directions.*/
        public static final String[] DIRECTIONS = new String[NUM_DIRECTIONS];

        /** Default name for an unknown way. */
        public static final String UNKNOWN_ROAD = "unknown road";
        
        /** Static initializer. */
        static {
            DIRECTIONS[START] = "Start";
            DIRECTIONS[STRAIGHT] = "Go straight";
            DIRECTIONS[SLIGHT_LEFT] = "Slight left";
            DIRECTIONS[SLIGHT_RIGHT] = "Slight right";
            DIRECTIONS[LEFT] = "Turn left";
            DIRECTIONS[RIGHT] = "Turn right";
            DIRECTIONS[SHARP_LEFT] = "Sharp left";
            DIRECTIONS[SHARP_RIGHT] = "Sharp right";
        }

        /** The direction a given NavigationDirection represents.*/
        int direction;
        /** The name of the way I represent. */
        String way;
        /** The distance along this way I represent. */
        double distance;

        /**
         * Create a default, anonymous NavigationDirection.
         */
        public NavigationDirection() {
            this.direction = STRAIGHT;
            this.way = UNKNOWN_ROAD;
            this.distance = 0.0;
        }

        public String toString() {
            return String.format("%s on %s and continue for %.3f miles.",
                    DIRECTIONS[direction], way, distance);
        }

        /**
         * Takes the string representation of a navigation direction and converts it into
         * a Navigation Direction object.
         * @param dirAsString The string representation of the NavigationDirection.
         * @return A NavigationDirection object representing the input string.
         */
        public static NavigationDirection fromString(String dirAsString) {
            String regex = "([a-zA-Z\\s]+) on ([\\w\\s]*) and continue for ([0-9\\.]+) miles\\.";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(dirAsString);
            NavigationDirection nd = new NavigationDirection();
            if (m.matches()) {
                String direction = m.group(1);
                if (direction.equals("Start")) {
                    nd.direction = NavigationDirection.START;
                } else if (direction.equals("Go straight")) {
                    nd.direction = NavigationDirection.STRAIGHT;
                } else if (direction.equals("Slight left")) {
                    nd.direction = NavigationDirection.SLIGHT_LEFT;
                } else if (direction.equals("Slight right")) {
                    nd.direction = NavigationDirection.SLIGHT_RIGHT;
                } else if (direction.equals("Turn right")) {
                    nd.direction = NavigationDirection.RIGHT;
                } else if (direction.equals("Turn left")) {
                    nd.direction = NavigationDirection.LEFT;
                } else if (direction.equals("Sharp left")) {
                    nd.direction = NavigationDirection.SHARP_LEFT;
                } else if (direction.equals("Sharp right")) {
                    nd.direction = NavigationDirection.SHARP_RIGHT;
                } else {
                    return null;
                }

                nd.way = m.group(2);
                try {
                    nd.distance = Double.parseDouble(m.group(3));
                } catch (NumberFormatException e) {
                    return null;
                }
                return nd;
            } else {
                // not a valid nd
                return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof NavigationDirection) {
                return direction == ((NavigationDirection) o).direction
                    && way.equals(((NavigationDirection) o).way)
                    && distance == ((NavigationDirection) o).distance;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, way, distance);
        }
    }
}
