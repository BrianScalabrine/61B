import org.junit.Test;
import static org.junit.Assert.*;

public class TestPalindrome {
    // You must use this palindrome, and not instantiate
    // new Palindromes, or the autograder might be upset.
    static Palindrome palindrome = new Palindrome();

    @Test
    public void testWordToDeque() {
        Deque d = palindrome.wordToDeque("persiflage");
        String actual = "";
        for (int i = 0; i < "persiflage".length(); i++) {
            actual += d.removeFirst();
        }
        assertEquals("persiflage", actual);
    }

    @Test
    public void testIsPalindrome() {
        assertTrue(palindrome.isPalindrome("racecar"));
        assertTrue(palindrome.isPalindrome("a"));
        assertTrue(palindrome.isPalindrome("A"));

        assertFalse(palindrome.isPalindrome("aA"));
        assertFalse(palindrome.isPalindrome("Aa"));
        assertFalse(palindrome.isPalindrome("doge"));
    }

    @Test
    public void testIsOffByOnePalindrome() {
        CharacterComparator ccOne = new OffByOne();

        assertTrue(palindrome.isPalindrome("flake", ccOne));
        assertTrue(palindrome.isPalindrome("a", ccOne));
        assertTrue(palindrome.isPalindrome("ab", ccOne));

        assertFalse(palindrome.isPalindrome("racecar", ccOne));
        assertFalse(palindrome.isPalindrome("aa", ccOne));
    }

    @Test
    public void testIsOffByNPalindrome() {
        CharacterComparator ccN = new OffByN(5);

        assertTrue(palindrome.isPalindrome("af", ccN));
        assertTrue(palindrome.isPalindrome("fa", ccN));

        assertFalse(palindrome.isPalindrome("fh", ccN));
        assertFalse(palindrome.isPalindrome("racecar", ccN));
        assertFalse(palindrome.isPalindrome("aa", ccN));
        assertFalse(palindrome.isPalindrome("ab", ccN));
    }
}
