import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
//import java.util.Stack;

public class Trie {
    //private static final int R = 128;

    private class Node {
        boolean exists;
        Map<Character, Node> children;
        //Node[] children;

        Node() {
            exists = false;
            children = new TreeMap<>();
            //children = new Node[R];
        }
    }

    private final Node root;

    public Trie() {
        root = new Node();
    }

    public void put(String word) {
        //putRecursive(root, word, 0);
        putIterative(word);
    }

    private Node putIterative(String word) {
        if (word.isEmpty()) {
            return null;
        }

        Node node = root;
        for (char c : word.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new Node());
        }

        node.exists = true;

        return node;
    }

    private Node putRecursive(Node node, String word, int index) {
        if (node == null) {
            node = new Node();
        }

        if (index == word.length()) {
            node.exists = true;
            return node;
        }

        char c = word.charAt(index);
        node.children.put(c, putRecursive(node.children.get(c), word, index + 1));

        return node;
    }

    public boolean remove(String word) {
        // TODO
        return false;
    }

    public List<String> get(String prefix) {
        List<String> words = new ArrayList<>();

        Node prefixNode = getNode(prefix);
        if (prefixNode != null) {
            collectWordsRecursive(prefixNode, new StringBuilder(prefix), words);
        }
        return words;
    }

    private void collectWordsRecursive(Node node, StringBuilder word, List<String> words) {
        if (node == null || words == null || word.length() == 0) {
            return;
        }

        if (node.exists) {
            words.add(word.toString());
        }

        node.children.forEach((c, child) -> {
            collectWordsRecursive(child, new StringBuilder(word).append(c), words);
        });
    }

//    private List<String> collectWordsIterative(String prefix) {
//        List<String> words = new ArrayList<>();
//
//        if (prefix.isEmpty()) {
//            return words;
//        }
//
//        Node node = getNode(prefix);
//        if (node == null) {
//            return words;
//        }
//
//        // Depth first traversal to collect words extending prefix
//        Stack<Node> stack = new Stack<>();
//        stack.push(node);
//
//        while (!stack.empty()) {
//            node = stack.pop();
//            if (node != null && node.exists) {
//
//            }
//        }
//
//        return words;
//    }

    private Node getNode(String prefix) {
        //return getNode(root, prefix, 0);
        return getNodeIterative(prefix);
    }

    private Node getNodeIterative(String prefix) {
        if (prefix.isEmpty()) {
            return null;
        }

        Node node = root;
        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) {
                break;
            }
        }
        return node;
    }

    private Node getNodeRecursive(Node node, String prefix, int index) {
        if (node == null) {
            return null;
        }

        Node next = node.children.get(prefix.charAt(index));
        if (index == prefix.length() - 1) {
            return next;
        }

        return getNodeRecursive(next, prefix, index + 1);
    }
}
