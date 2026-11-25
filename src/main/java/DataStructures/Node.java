package DataStructures;

import java.io.Serial;
import java.io.Serializable;

public class Node<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final T data;
    private Node<T> next;

    public Node(T data) {
        this.data = data;
    }

    public T getData() {
        return data; }

    public Node<T> getNext() {
        return next; }

    public void setNext(Node<T> next) {
        this.next = next; }
}
