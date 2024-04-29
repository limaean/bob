package project20280.stacksqueues;

import project20280.interfaces.Queue;

public class ArrayQueue<E> implements Queue<E> {

    private static final int CAPACITY = 1000;
    private E[] data;
    private int front = 0;
    private int rear = 0;
    private int size = 0;

    public ArrayQueue(int capacity) {
        data = (E[]) new Object[capacity];
    }

    public ArrayQueue() {
        this(CAPACITY);
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() { return size == 0; }

    @Override
    public void enqueue(E e) {
        data[rear] = e;
        if (rear == data.length-1) {
            rear = -1;
        }
        rear++;
        size++;
    }

    @Override
    public E first() {
        return isEmpty() ? null : data[front];
    }

    @Override
    public E dequeue() {
        if (isEmpty()) return null;
        E deq = data[front];
        data[front] = null;
        if (front == data.length-1) {
            front = -1;
        }
        front++;
        size--;
        return deq;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; ++i) {
            System.out.println(i + front);
            E res = data[(front + i) % CAPACITY];
            sb.append(res);
            if (i != size - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    public static void main(String[] args) {
        Queue<Integer> qq = new ArrayQueue<>();
        System.out.println(qq);

        int N = 10;
        for (int i = 0; i < N; ++i) {
            qq.enqueue(i);
        }
        System.out.println(qq);

        for (int i = 0; i < N / 2; ++i) qq.dequeue();
        System.out.println(qq);

    }
}
