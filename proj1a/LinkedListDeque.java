import java.util.NoSuchElementException;

public class LinkedListDeque<T> implements Dequeue<T>
{
    private int size;
    private Node<T> sentinel;

    private static class Node<T>
    {
        Node<T> prev;
        T item;
        Node<T> next;

        Node(T item) {
            prev = null;
            this.item = item;
            next = null;
        }

        Node(Node<T> prev, T item, Node<T> next) {
            this.prev = prev;
            this.item = item;
            this.next = next;
        }
    }

    public LinkedListDeque() {
        size = 0;
        sentinel = new Node<>(null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
    }

    public void addFirst(T item) {
        size++;

        Node<T> currentFirst = sentinel.next;
        Node<T> first = new Node<>(sentinel, item, currentFirst);
        currentFirst.prev = first;
        sentinel.next = first;
    }

    public void addLast(T item) {
        size++;

        Node<T> currentLast = sentinel.prev;
        Node<T> last = new Node<>(currentLast, item, sentinel);
        currentLast.next = last;
        this.sentinel.prev = last;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        Node<T> ptr = sentinel.next;

        while (ptr != sentinel) {
            System.out.print(ptr.item);
            if (ptr.next != sentinel) {
                System.out.print(" ");
            }

            ptr = ptr.next;
        }

        System.out.println();
    }

    public T removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        size--;

        T item = sentinel.next.item;
        Node<T> second = sentinel.next.next;
        second.prev = sentinel;
        sentinel.next = second;

        return item;
    }

    public T removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        size--;

        T item = sentinel.prev.item;
        Node<T> secondToLast = sentinel.prev.prev;
        secondToLast.next = sentinel;
        sentinel.prev = secondToLast;

        return item;
    }

    public T get(int index) {
        checkIndexOutOfBounds(index);

        if (index == size - 1) {
            // Get last item
            return sentinel.prev.item;
        }

        Node<T> ptr = sentinel.next;
        while (index-- > 0) {
            ptr = ptr.next;
        }

        return ptr.item;
    }

    public T getRecursive(int index) {
        checkIndexOutOfBounds(index);

        if (index == size - 1) {
            // Get last item
            return sentinel.prev.item;
        }

        return getRecursiveHelper(index, sentinel.next);
    }

    private void checkIndexOutOfBounds(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException();
        }
    }

    private T getRecursiveHelper(int index, Node<T> node) {
        if (index == 0) {
            return node.item;
        }

        return getRecursiveHelper(index - 1, node.next);
    }
}