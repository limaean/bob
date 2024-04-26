package project20280.priorityqueue;

/*
 */

import project20280.interfaces.Entry;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;

import static java.lang.Math.max;


/**
 * An implementation of a priority queue using an array-based heap.
 */

public class HeapPriorityQueue<K, V> extends AbstractPriorityQueue<K, V> {

    protected ArrayList<Entry<K, V>> heap = new ArrayList<>();

    /**
     * Creates an empty priority queue based on the natural ordering of its keys.
     */
    public HeapPriorityQueue() {
        super();
    }

    /**
     * Creates an empty priority queue using the given comparator to order keys.
     *
     * @param comp comparator defining the order of keys in the priority queue
     */
    public HeapPriorityQueue(Comparator<K> comp) {
        super(comp);
    }

    /**
     * Creates a priority queue initialized with the respective key-value pairs. The
     * two arrays given will be paired element-by-element. They are presumed to have
     * the same length. (If not, entries will be created only up to the length of
     * the shorter of the arrays)
     *
     * @param keys   an array of the initial keys for the priority queue
     * @param values an array of the initial values for the priority queue
     */
    public HeapPriorityQueue(K[] keys, V[] values) {
        int i = 0;
        for (K key : keys) {
            Entry<K, V> kv = new PQEntry<>(key, values[i]);
            heap.add(kv);
            i++;
        }
        heapify();
    }

    // protected utilities
    protected int parent(int j) {
        return (j-1)/2;
    }

    protected int left(int j) {
        return (j * 2 + 1);
    }

    protected int right(int j) {
        return (j * 2 + 2);
    }

    protected boolean hasLeft(int j) {
        // TODO
        return false;
    }

    protected boolean hasRight(int j) {
        // TODO
        return false;
    }

    /**
     * Exchanges the entries at indices i and j of the array list.
     */
    protected void swap(int i, int j) {
        Entry<K, V> temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    /**
     * Moves the entry at index j higher, if necessary, to restore the heap
     * property.
     */
    protected void upheap(int j) {
        if (j == 0) return;
        int smallest = parent(j);

        if (compare(heap.get(j), heap.get(smallest)) < 0) {
            smallest = j;
        }

        if (smallest == j) {
            swap(j, parent(j));
            upheap(parent(j));
        }
    }

    /**
     * Moves the entry at index j lower, if necessary, to restore the heap property.
     */
    protected void downheapSwap(int j) {

    }

    protected void downheap(int j) {

    }

    /**
     * Performs a bottom-up construction of the heap in linear time.
     */

    protected int height() {
        int n = size();
        return (int) (Math.ceil(Math.log(n + 1) / Math.log(2))) - 1;
    }

    protected int layerNodeCount(int h) {
        return (int) Math.pow(2, h-1);
    }
    protected void heapifyBranch(int i) {
        int smallest = i;

        if (left(i) < heap.size() && compare(heap.get(left(i)), heap.get(smallest)) < 0) {
            smallest = left(i);
        }

        if (right(i) < heap.size() && compare(heap.get(right(i)), heap.get(smallest)) < 0) {
            smallest = right(i);
        }

        if (smallest != i) {
            swap(i, smallest);
            heapifyBranch(smallest);
        }
    }
    protected ArrayList<Entry<K, V>> heapify() {
        for (int i=heap.size() / 2 - 1; i >= 0; i--) {
            heapifyBranch(i);
        }
        return heap;
    }

    public void siftDown(int index, int size) {

    }

    // public methods

    /**
     * Returns the number of items in the priority queue.
     *
     * @return number of items
     */
    @Override
    public int size() {
        return heap.size();
    }

    /**
     * Returns (but does not remove) an entry with minimal key.
     *
     * @return entry having a minimal key (or null if empty)
     */
    @Override
    public Entry<K, V> min() {
        return heap.get(0);
    }

    /**
     * Inserts a key-value pair and return the entry created.
     *
     * @param key   the key of the new entry
     * @param value the associated value of the new entry
     * @return the entry storing the new key-value pair
     * @throws IllegalArgumentException if the key is unacceptable for this queue
     */
    @Override
    public Entry<K, V> insert(K key, V value) throws IllegalArgumentException {
        Entry<K, V> n = new PQEntry<K, V>(key, value);
        heap.add(n);
        upheap(heap.size() - 1);
        return n;
    }

    /**
     * Removes and returns an entry with minimal key.
     *
     * @return the removed entry (or null if empty)
     */
    @Override
    public Entry<K, V> removeMin() {
        Entry<K, V> min = min();
        swap(0, size() - 1);
        heap.remove(heap.size() - 1);
        heapifyBranch(0);
        return min;
    }

    public String toString() {
        return heap.toString();
    }

    /**
     * Used for debugging purposes only
     */
    private void sanityCheck() {
        for (int j = 0; j < heap.size(); j++) {
            int left = left(j);
            int right = right(j);
            //System.out.println("-> " +left + ", " + j + ", " + right);
            Entry<K, V> e_left, e_right;
            e_left = left < heap.size() ? heap.get(left) : null;
            e_right = right < heap.size() ? heap.get(right) : null;
            if (left < heap.size() && compare(heap.get(left), heap.get(j)) < 0) {
                System.out.println("Invalid left child relationship");
                System.out.println("=> " + e_left + ", " + heap.get(j) + ", " + e_right);
            }
            if (right < heap.size() && compare(heap.get(right), heap.get(j)) < 0) {
                System.out.println("Invalid right child relationship");
                System.out.println("=> " + e_left + ", " + heap.get(j) + ", " + e_right);
            }
        }
    }

    public static void main(String[] args) {
        Integer[] rands = new Integer[]{35, 26, 15, 24, 33, 4, 12, 1, 23, 21, 2, 5};
        HeapPriorityQueue<Integer, Integer> pq = new HeapPriorityQueue<>(rands, rands);

        System.out.println("elements: " + rands);
        System.out.println("after adding elements: " + pq);

        System.out.println("min element: " + pq.min());

        pq.removeMin();
        System.out.println("after removeMin: " + pq);
        // [             1,
        //        2,            4,
        //   23,     21,      5, 12,
        // 24, 26, 35, 33, 15]
    }
}
