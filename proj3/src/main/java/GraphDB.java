import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.stream.Collectors;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */
    static class Node {
        long id;
        double lat;
        double lon;
        String name;
        private Map<Long, Edge> edges;

        Node() {
            this.id = 0;
            this.lat = 0;
            this.lon = 0;
            this.name = "";
            this.edges = new HashMap<>();
        }

        void addEdge(long v, Edge e) {
            edges.put(v, e);
        }
        Edge removeEdge(long v) {
            return edges.remove(v);
        }
        Edge getEdge(long v) {
            return edges.get(v);
        }
        boolean hasEdges() {
            return !edges.isEmpty();
        }
        Set<Long> getNeighbors() {
            return edges.keySet();
        }

        @Override
        public int hashCode() {
            return Long.hashCode(id);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Node) {
                return id == ((Node) o).id;
            }
            return false;
        }
    }

    static class Edge {
        String name;
        String maxSpeed;

        Edge() {
            this.name = "";
            this.maxSpeed = "";
        }
    }

    private final Map<Long, Node> graph = new HashMap<>();
    private final Map<String, String> fullNames = new HashMap<>();
    private final Map<String, Set<Node>> locations = new HashMap<>();
    private final Trie trie = new Trie();

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            FileInputStream inputStream = new FileInputStream(inputFile);
            // GZIPInputStream stream = new GZIPInputStream(inputStream);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputStream, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        graph.values().removeIf(node -> !node.hasEdges());
    }

    /**
     * Returns an iterable of all vertex IDs in the graph.
     * @return An iterable of id's of all vertices in the graph.
     */
    Iterable<Long> vertices() {
        return graph.keySet();
    }

    /**
     * Returns ids of all vertices adjacent to v.
     * @param v The id of the vertex we are looking adjacent to.
     * @return An iterable of the ids of the neighbors of v.
     */
    Iterable<Long> adjacent(long v) {
        Node node = graph.get(v);
        return node != null ? node.getNeighbors() : null;
    }

    /**
     * Returns the great-circle distance between vertices v and w in miles.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The great-circle distance between the two locations from the graph.
     */
    double distance(long v, long w) {
        return distance(lon(v), lat(v), lon(w), lat(w));
    }

    static double distance(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double dphi = Math.toRadians(latW - latV);
        double dlambda = Math.toRadians(lonW - lonV);

        double a = Math.sin(dphi / 2.0) * Math.sin(dphi / 2.0);
        a += Math.cos(phi1) * Math.cos(phi2) * Math.sin(dlambda / 2.0) * Math.sin(dlambda / 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 3963 * c;
    }

    /**
     * Returns the initial bearing (angle) between vertices v and w in degrees.
     * The initial bearing is the angle that, if followed in a straight line
     * along a great-circle arc from the starting point, would take you to the
     * end point.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The initial bearing between the vertices.
     */
    double bearing(long v, long w) {
        return bearing(lon(v), lat(v), lon(w), lat(w));
    }

    static double bearing(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double lambda1 = Math.toRadians(lonV);
        double lambda2 = Math.toRadians(lonW);

        double y = Math.sin(lambda2 - lambda1) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2);
        x -= Math.sin(phi1) * Math.cos(phi2) * Math.cos(lambda2 - lambda1);
        return Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    long closest(double lon, double lat) {
        long closest = 0;
        double closestDistance = Double.MAX_VALUE;

        for (Node node : graph.values()) {
            double distance = distance(lon, lat, node.lon, node.lat);
            if (distance < closestDistance) {
                closest = node.id;
                closestDistance = distance;
            }
        }

        return closest;
    }

    /**
     * Gets the longitude of a vertex.
     * @param v The id of the vertex.
     * @return The longitude of the vertex.
     */
    double lon(long v) {
        Node node = graph.get(v);
        return node != null ? node.lon : 0;
    }

    /**
     * Gets the latitude of a vertex.
     * @param v The id of the vertex.
     * @return The latitude of the vertex.
     */
    double lat(long v) {
        Node node = graph.get(v);
        return node != null ? node.lat : 0;
    }

    void addNode(Node node) {
        if (node != null) {
            graph.put(node.id, node);

            String cleanName = cleanString(node.name);
            trie.put(cleanName);
            fullNames.put(cleanName, node.name);

            locations.computeIfAbsent(node.name, k -> new HashSet<>()).add(node);
        }
    }

    void addEdge(long from, long to, Edge edge) {
        if (edge != null) {
            Node fromNode = graph.get(from);
            Node toNode = graph.get(to);

            if (fromNode != null && toNode != null) {
                fromNode.addEdge(to, edge);
                toNode.addEdge(from, edge);
            }
        }
    }

    Node removeNode(long v) {
        Node node = graph.remove(v);
        if (node == null) {
            return null;
        }

        // Remove all edges connecting to this node
        for (long w : node.getNeighbors()) {
            Node connectedNode = graph.get(w);
            if (connectedNode != null) {
                connectedNode.removeEdge(v);
            }
        }

        // Remove node from appearing in autocomplete searches
        String cleanName = cleanString(node.name);
        trie.remove(cleanName);
        fullNames.remove(cleanName);

        // Remove node from being found in location searches
        locations.computeIfPresent(node.name, (location, nodes) -> {
            if (nodes.remove(node) && nodes.isEmpty()) {
                return null;
            }
            return nodes;
        });

        return node;
    }

    Edge getEdge(long from, long to) {
        Node node = graph.get(from);
        return node != null ? node.getEdge(to) : null;
    }

    Set<Node> getLocations(String name) {
        return locations.getOrDefault(name, new HashSet<>());
    }

    List<String> getLocationsByPrefix(String prefix) {
        return trie.get(cleanString(prefix)).stream()
                .map(name -> fullNames.get(name))
                .collect(Collectors.toList());
    }
}
