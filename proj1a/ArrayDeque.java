//import java.util.NoSuchElementException;

public class ArrayDeque<T> {
    private static final int DEFAULT_SIZE = 8;
    private static final int RESIZE_FACTOR = 2;
    private static final int USAGE_FACTOR = 4;

    private int size;
    private int nextFirst;
    private int nextLast;
    private T[] items;

    public ArrayDeque() {
        size = 0;
        nextFirst = 3;
        nextLast = 4;
        items = (T[]) new Object[DEFAULT_SIZE];
    }

    public void addFirst(T item) {
        ensureCapacity();
        size++;

        items[nextFirst] = item;
        nextFirst = minusOne(nextFirst);
    }

    public void addLast(T item) {
        ensureCapacity();
        size++;

        items[nextLast] = item;
        nextLast = addOne(nextLast);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        int i = addOne(nextFirst), itemsPrinted = 0;

        while (itemsPrinted++ < size) {
            System.out.print(items[i]);

            if ((i = addOne(i)) != nextLast) {
                System.out.print(" ");
            }
        }

        System.out.println();
    }

    public T removeFirst() {
        if (isEmpty()) {
            //throw new NoSuchElementException();
            return null;
        }

        ensureUsage();
        size--;

        nextFirst = addOne(nextFirst);
        T item = items[nextFirst];
        items[nextFirst] = null;

        return item;
    }

    public T removeLast() {
        if (isEmpty()) {
            //throw new NoSuchElementException();
            return null;
        }

        ensureUsage();
        size--;

        nextLast = minusOne(nextLast);
        T item = items[nextLast];
        items[nextLast] = null;

        return item;
    }

    public T get(int index) {
        if (index >= size || index < 0) {
            //throw new IndexOutOfBoundsException();
            return null;
        }

        index = addOne(nextFirst + index);
        return items[index];
    }

    private int addOne(int index) {
        return (index + 1) % items.length;
    }

    private int minusOne(int index) {
        return Math.floorMod(index - 1, items.length);
    }

    private void ensureCapacity() {
        if (size + 1 > items.length) {
            grow();
        }
    }

    private void ensureUsage() {
        if (items.length > DEFAULT_SIZE) {
            if (size - 1 < items.length / USAGE_FACTOR) {
                shrink();
            }
        }
    }

    private void grow() {
        moveItems(items.length * RESIZE_FACTOR);
    }

    private void shrink() {
        moveItems(items.length / RESIZE_FACTOR);
    }

    private void moveItems(int newSize) {
        if (newSize <= size) {
            //throw new IllegalStateException();
            return;
        }

        T newItems[] = (T[]) new Object[newSize];

        int first = addOne(nextFirst), last = minusOne(nextLast);
        int newFirst = newItems.length / 4;

        if (first < last) {
            System.arraycopy(items, first, newItems, newFirst, size);
        }
        else {
            int copyLength = size - first;
            System.arraycopy(items, first, newItems, newFirst, copyLength);
            System.arraycopy(items, 0, newItems, newFirst + copyLength, last + 1);
        }

        items = newItems;
        nextFirst = newFirst - 1;
        nextLast = newFirst + size;
    }
}
