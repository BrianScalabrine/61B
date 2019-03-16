import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Class for doing Radix sort
 *
 * @author Akhil Batra, Alexander Hwang
 *
 */
public class RadixSort {
    private static final int R = 256;
    /**
     * Does LSD radix sort on the passed in array with the following restrictions:
     * The array can only have ASCII Strings (sequence of 1 byte characters)
     * The sorting is stable and non-destructive
     * The Strings can be variable length (all Strings are not constrained to 1 length)
     *
     * @param asciis String[] that needs to be sorted
     *
     * @return String[] the sorted array
     */
    public static String[] sort(String[] asciis) {
        int maxLength = Integer.MIN_VALUE;
        for (String s : asciis) {
            int length = s.length();
            if (length > maxLength) {
                maxLength = length;
            }
        }

        String[] sorted = asciis.clone();
        for (int i = maxLength - 1; i >= 0; i--) {
            sortHelperLSD(sorted, i);
        }

        return sorted;
    }

    /**
     * LSD helper method that performs a destructive counting sort the array of
     * Strings based off characters at a specific index.
     * @param asciis Input array of Strings
     * @param index The position to sort the Strings on.
     */
    private static void sortHelperLSD(String[] asciis, int index) {
        // Get counts for each string that has character at the current index
        int[] counts = new int[R + 1];
        for (String s : asciis) {
            int asciiIndex = getAsciiIndex(s, index);
            counts[asciiIndex]++;
        }

        // Calculate start position for each string using their counts for the character at the current index
        int pos = 0;
        int[] starts = new int[counts.length];
        for (int i = 0; i < starts.length; i++) {
            starts[i] = pos;
            pos += counts[i];
        }

        // Clone the asciis string array and overwrite it in the correct order for the current character
        for (String s : asciis.clone()) {
            int asciiIndex = getAsciiIndex(s, index);
            int place = starts[asciiIndex]++;
            asciis[place] = s;
        }
    }

    private static int getAsciiIndex(String s, int index) {
        return index >= s.length() ? 0 : (int) s.charAt(index) + 1;
    }

    /**
     * MSD radix sort helper function that recursively calls itself to achieve the sorted array.
     * Destructive method that changes the passed in array, asciis.
     *
     * @param asciis String[] to be sorted
     * @param start int for where to start sorting in this method (includes String at start)
     * @param end int for where to end sorting in this method (does not include String at end)
     * @param index the index of the character the method is currently sorting on
     *
     **/
    private static void sortHelperMSD(String[] asciis, int start, int end, int index) {
        // Optional MSD helper method for optional MSD radix sort
        return;
    }

    public static void main(String[] args) {
        String[] asciis = { "SubmissionPublisher", "Brother", "Chicken", "dude", "chicken", "Gigantic", "Gargantuan" };
        String[] sorted = sort(asciis);

        Arrays.sort(asciis);
        System.out.println(Arrays.toString(sorted));
        System.out.println(Arrays.toString(asciis));

    }
}
