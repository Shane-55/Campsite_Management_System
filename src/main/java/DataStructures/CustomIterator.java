package DataStructures;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CustomIterator<T> implements Iterator<T> {
    private DataStructures.Node<T> current;

    public CustomIterator(DataStructures.Node<T> first) {
        current = first;
    }

    @Override
    public boolean hasNext() {
        return current != null;
    }

    @Override
    public T next() {
        if (!hasNext()) throw new NoSuchElementException();
        T value = current.getData();
        current = current.getNext();
        return value;
    }
}
