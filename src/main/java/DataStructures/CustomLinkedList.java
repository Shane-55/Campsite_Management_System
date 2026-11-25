package DataStructures;

import java.io.Serial;
import java.io.Serializable;

public class CustomLinkedList<T> implements Iterable<T>, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private DataStructures.Node<T> head;
    private DataStructures.Node<T> tail;
    private int size;

    public void add(T item) {
        DataStructures.Node<T> node = new DataStructures.Node<>(item);
        if (head == null) head = node;
        else tail.setNext(node);
        tail = node;
        size++;
    }

    public T get(int index) {
        checkIndex(index);
        return nodeAt(index).getData();
    }

    public T remove(int index) {
        checkIndex(index);
        DataStructures.Node<T> removed;
        if (index == 0) {
            removed = head;
            head = head.getNext();
            if (head == null) tail = null;
        } else {
            DataStructures.Node<T> previous = nodeAt(index - 1);
            removed = previous.getNext();
            previous.setNext(removed.getNext());
            if (removed == tail) tail = previous;
        }
        size--;
        return removed.getData();
    }

    public boolean removeItem(T item) {
        DataStructures.Node<T> current = head;
        DataStructures.Node<T> previous = null;
        while (current != null) {
            if (current.getData().equals(item)) {
                if (previous == null) head = current.getNext();
                else previous.setNext(current.getNext());
                if (current == tail) tail = previous;
                size--;
                return true;
            }
            previous = current;
            current = current.getNext();
        }
        return false;
    }

    public boolean contains(T item) {
        for (T value : this) if (value.equals(item)) return true;
        return false;
    }

    public int size() {
        return size; }
    public boolean isEmpty() {
        return size == 0; }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    private DataStructures.Node<T> nodeAt(int index) {
        DataStructures.Node<T> current = head;
        for (int i = 0; i < index; i++) current = current.getNext();
        return current;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index " + index + " is outside the list.");
    }

    @Override
    public DataStructures.CustomIterator<T> iterator() {
        return new DataStructures.CustomIterator<>(head);
    }
}
