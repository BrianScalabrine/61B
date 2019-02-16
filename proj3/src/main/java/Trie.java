import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Trie {
    //private static final int R = 128;

    private class Node {
        boolean exists;
        Map<Character, Node> children;
        //Node[] children;

        Node() {
            exists = false;
            children = new HashMap<>();
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

//    private Node putRecursive(Node node, String word, int index) {
//        if (node == null) {
//            node = new Node();
//        }
//
//        if (index == word.length()) {
//            node.exists = true;
//            return node;
//        }
//
//        char c = word.charAt(index);
//        node.children.put(c, putRecursive(node.children.get(c), word, index + 1));
//
//        return node;
//    }

    public boolean remove(String s) {
        // TODO
        return false;
    }

    public List<String> get(String prefix) {
        List<String> words = new LinkedList<>();

        // Collect all words extending from this node
        Node prefixNode = getPrefixNode(prefix);
        collectWords(prefixNode, prefix, words);

        return words;
    }

    private void collectWords(Node node, String word, List<String> words) {
        if (node == null || words == null) {
            return;
        } else if (node.exists) {
            words.add(word);
        }

        node.children.forEach((c, n) -> {
            collectWords(n, word + c, words);
        });
    }

    private Node getPrefixNode(String prefix) {
        return getPrefixNode(root, prefix, 0);
    }

    private Node getPrefixNode(Node node, String prefix, int index) {
        if (node == null) {
            return null;
        }

        Node next = node.children.get(prefix.charAt(index));
        if (index == prefix.length() - 1) {
            return next;
        }

        return getPrefixNode(next, prefix, index + 1);
    }
}
