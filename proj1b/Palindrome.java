public class Palindrome {
    public Deque<Character> wordToDeque(String word) {
        Deque<Character> deque = new LinkedListDeque<>();

        for (char c : word.toCharArray()) {
            deque.addLast(c);
        }

        return deque;
    }

    public boolean isPalindrome(String word) {
        return isPalindromeRecursive(wordToDeque(word));
    }

    public boolean isPalindrome(String word, CharacterComparator cc) {
        return isPalindromeRecursive(wordToDeque(word), cc);

    }

    private boolean isPalindromeRecursive(Deque<Character> wordDeque) {
        if (wordDeque.isEmpty() || wordDeque.size() == 1) {
            return true;
        } else if (wordDeque.removeFirst() != wordDeque.removeLast()) {
            return false;
        }

        return isPalindromeRecursive(wordDeque);
    }

    private boolean isPalindromeRecursive(Deque<Character> wordDeque, CharacterComparator cc) {
        if (wordDeque.isEmpty() || wordDeque.size() == 1) {
            return true;
        } else if (!cc.equalChars(wordDeque.removeFirst(), wordDeque.removeLast())) {
            return false;
        }

        return isPalindromeRecursive(wordDeque, cc);
    }
}
