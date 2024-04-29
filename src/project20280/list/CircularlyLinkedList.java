package project20280.list;

import project20280.interfaces.List;

import java.util.Iterator;

public class CircularlyLinkedList<E> implements List<E> {

    private class Node<T> {
        private final T data;
        private Node<T> next;

        public Node(T e, Node<T> n) {
            data = e;
            next = n;
        }

        public T getData() {
            return data;
        }

        public void setNext(Node<T> n) {
            next = n;
        }

        public Node<T> getNext() {
            return next;
        }
    }

    private Node<E> tail = null;
    private int size = 0;

    public CircularlyLinkedList() {

    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }
    @Override
    public E get(int i) {
        int pos = i;
        i = 0;
        Node<E>curr = tail.next;
        while (i < pos ) {
            curr = curr.getNext();
            i++;
        }
        return curr.getData();
    }

    /**
     * Inserts the given element at the specified index of the list, shifting all
     * subsequent elements in the list one position further to make room.
     *
     * @param pos the index at which the new element should be stored
     * @param e the new element to be stored
     */
    @Override
    public void add(int pos, E e) {
        if (pos == size) {
            addLast(e);
        }
        else {
            Node<E> curr = tail.next;
            int i=0;
            while (i < pos-1 ) {
                curr = curr.getNext();
                i++;
            }
            Node<E> newest = new Node<>(e, curr.getNext());
            curr.setNext(newest);
        }
        size++;
    }

    @Override
    public E remove(int i) {
        int pos = i;
        Node<E> curr = tail.next;
        i = 0;
        while (i < pos - 2) {
            curr = curr.getNext();
            i++;
        }
        Node<E> old = curr.next;
        curr.next = old.next;
        size--;
        return old.getData();
    }
    public void rotate() {
        tail = tail.next;
        Node<E> curr = tail.next;
        int i = 0;
        while (i < size - 2) {
            curr = curr.getNext();
            i++;
        }
    }

    private class CircularlyLinkedListIterator<E> implements Iterator<E> {
        Node<E> curr = (Node<E>) tail;

        @Override
        public boolean hasNext() {
            return curr != tail;
        }

        @Override
        public E next() {
            E res = curr.data;
            curr = curr.next;
            return res;
        }

    }

    @Override
    public Iterator<E> iterator() {
        return new CircularlyLinkedListIterator<E>();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public E removeFirst() {
        if (!(isEmpty())) {
            E old = tail.next.getData();
            tail.next = tail.next.next;
            size--;
            return old;
        }
        return null;
    }

    @Override
    public E removeLast() {
        E old = tail.getData();
        Node<E> curr = tail.next;
        int i = 0;
        while (i < size - 2) {
            curr = curr.getNext();
            i++;
        }
        tail = tail.next;
        curr.next = tail;
        size--;
        return old;
    }

    @Override
    public void addFirst(E e) {
        if (isEmpty()) {
            addLast(e);
        }
        else {
            tail.next = new Node<E>(e, tail.next);
            size++;
        }
    }

    @Override
    public void addLast(E e) {
        if (isEmpty()) {
            tail = new Node<>(e, null);
            tail.next = tail;
        } else {
            tail.next = new Node<>(e, tail.next);
            tail = tail.next;
        }
        size++;
    }



    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<E> curr = tail;
        do {
            curr = curr.next;
            sb.append(curr.data);
            if (curr != tail) {
                sb.append(", ");
            }
        } while (curr != tail);
        sb.append("]");
        return sb.toString();
    }


    public static void main(String[] args) {
        CircularlyLinkedList<Integer> ll = new CircularlyLinkedList<Integer>();
        for (int i = 10; i < 20; ++i) {
            ll.addLast(i);
        }

        System.out.println(ll);

        ll.removeFirst();
        System.out.println(ll);

        ll.removeLast();
        System.out.println(ll);

        ll.rotate();
        System.out.println(ll);

        ll.removeFirst();
        ll.rotate();
        System.out.println(ll);

        ll.removeLast();
        ll.rotate();
        System.out.println(ll);

        for (Integer e : ll) {
            System.out.println("value: " + e);
        }

    }
}
