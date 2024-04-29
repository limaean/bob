package project20280.hashtable;

import com.sun.net.httpserver.Filter;
import project20280.interfaces.Entry;
import project20280.interfaces.Map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/*
 * Map implementation using hash table with separate chaining.
 */

public class ChainHashMap<K, V> extends AbstractHashMap<K, V> {
    // a fixed capacity array of UnsortedTableMap that serve as buckets
    private UnsortedTableMap<K, V>[] table; // initialized within createTable

    /**
     * Creates a hash table with capacity 11 and prime factor 109345121.
     */
    public ChainHashMap() {
        super();
    }

    /**
     * Creates a hash table with given capacity and prime factor 109345121.
     */
    public ChainHashMap(int cap) {
        super(cap);
    }

    /**
     * Creates a hash table with the given capacity and prime factor.
     */
    public ChainHashMap(int cap, int p) {
        super(cap, p);
    }

    /**
     * Creates an empty table having length equal to current capacity.
     */
    @Override
    protected void createTable() {
        table = new UnsortedTableMap[capacity];
    }

    /**
     * Returns value associated with key k in bucket with hash value h. If no such
     * entry exists, returns null.
     *
     * @param h the hash value of the relevant bucket
     * @param k the key of interest
     * @return associate value (or null, if no such entry)
     */
    @Override
    protected V bucketGet(int h, K k) {
        return (table[h].get(k));
    }

    /**
     * Associates key k with value v in bucket with hash value h, returning the
     * previously associated value, if any.
     *
     * @param h the hash value of the relevant bucket
     * @param k the key of interest
     * @param v the value to be associated
     * @return previous value associated with k (or null, if no such entry)
     */
    @Override
    protected V bucketPut(int h, K k, V v) {
        if(table[h]==null){
            table[h]= new UnsortedTableMap<>();
        }
        return table[h].put(k,v);
    }


    /**
     * Removes entry having key k from bucket with hash value h, returning the
     * previously associated value, if found.
     *
     * @param h the hash value of the relevant bucket
     * @param k the key of interest
     * @return previous value associated with k (or null, if no such entry)
     */
    @Override
    protected V bucketRemove(int h, K k) {
        return table[h].remove(k);
    }

    /**
     * Returns an iterable collection of all key-value entries of the map.
     *
     * @return iterable collection of the map's entries
     */
    @Override
    public Iterable<Entry<K, V>> entrySet() {

        ArrayList<Entry<K, V>> entries = new ArrayList<>();
        for (UnsortedTableMap<K, V> tm : table) {
            if (tm != null) {
                for (Entry<K, V> e : tm.entrySet()) {
                    entries.add(e);
                }
            }
        }
        return entries;
    }
    public Boolean contains(K k) {
        for (K key : this.keySet()) {
            if (key.equals(k)) {
                return true;
            }
        }
        return false;
    }
    public String toString() {
        return entrySet().toString();
    }

    public static double testSpeed() {
        ChainHashMap<Integer, Integer> chm = new ChainHashMap<>();
        HashMap<Integer, Integer> hm = new HashMap<>();

        HashMap<Integer, Integer> dataset = new HashMap<>();
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            dataset.put(i, i);
        }

        long start = System.nanoTime();
        for (java.util.Map.Entry<Integer, Integer> entry : dataset.entrySet()) {
            chm.put(entry.getKey(), entry.getValue());
        }
        long end = System.nanoTime();
        long chainHashMapTime = end - start;

        long start2 = System.nanoTime();
        for (java.util.Map.Entry<Integer, Integer> entry : dataset.entrySet()) {
            hm.put(entry.getKey(), entry.getValue());
        }
        long end2 = System.nanoTime();
        long hashMapTime = end2 - start2;
        double ratio = (double) hashMapTime / chainHashMapTime;
        return ratio;
    }

    public static void main(String[] args) {
        ChainHashMap<Integer, String> m = new ChainHashMap<Integer, String>();
        m.put(1, "One");
        m.put(10, "Ten");
        m.put(11, "Eleven");
        m.put(20, "Twenty");

        System.out.println("m: " + m);

        m.remove(11);
        System.out.println("m: " + m);

        double[] timeArray = new double[100];
        for (int i=0; i < 100; i++) {
            timeArray[i] = testSpeed();
        }
        double sum = 0;
        for (double num : timeArray) {
            sum += num;
        }
        System.out.println(sum / timeArray.length);
    }
}