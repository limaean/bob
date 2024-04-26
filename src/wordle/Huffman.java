package wordle;

import project20280.interfaces.Entry;
import project20280.interfaces.Position;
import project20280.stacksqueues.LinkedDeque;
import project20280.tree.LinkedBinaryTree;
import project20280.tree.LinkedBinaryTree.Node;
import project20280.priorityqueue.HeapPriorityQueue;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Huffman {

    String filename;
    List<String> dict;

    public Huffman(String filename) {

        this.filename = filename;
        this.dict = readDictionary(filename);
    }
    public static Map<String, Integer> freqCounter(List<String> dict) {
        HashMap<String, Integer> freqMap = new HashMap<>();
        for (String word: dict) {
            for (char c: word.toCharArray()) {
                String ch = String.valueOf(c);
                if (!(freqMap.containsKey(ch))) {
                    freqMap.put(ch, 0);
                }
                freqMap.put(ch, freqMap.get(ch) + 1);
            }
        }
        return freqMap;
    }

    public LinkedBinaryTree<String> huffTreeGen() {
        HeapPriorityQueue<Integer, LinkedBinaryTree<String>> pq = new HeapPriorityQueue<>();
        Map<String, Integer> freqMap = freqCounter(readDictionary(filename));
        for (String c: freqMap.keySet()) {
            LinkedBinaryTree<String> t = new LinkedBinaryTree<>();
            t.insert(c);
            pq.insert(freqMap.get(c), t);
        }
        while (pq.size() > 1) {
            Entry<Integer, LinkedBinaryTree<String>> left = pq.removeMin();
            Entry<Integer, LinkedBinaryTree<String>> right = pq.removeMin();
            LinkedBinaryTree<String> t = new LinkedBinaryTree<>();

            String lString = left.getValue().root().getElement();
            String rString = right.getValue().root().getElement();
            t.insert(lString + rString);

            t.attach(t.root(), left.getValue(), right.getValue());
            pq.insert(left.getKey() + right.getKey(), t);
        }
        Entry<Integer, LinkedBinaryTree<String>> e = pq.removeMin();
        return e.getValue();
    }
    public static void main(String[] args) {
        Huffman huff = new Huffman("wordle/resources/dictionary.txt");
        System.out.print(huff.huffTreeGen().toBinaryTreeString());
        System.out.print(freqCounter(huff.dict));
    }

    public double huffDictLengthComp() {
        LinkedBinaryTree<String> huffTree = huffTreeGen();

        String bitString = "";
        for (String s: dict) {
            for (char c: s.toCharArray()) {
                String strC = String.valueOf(c);
                Node<String> curr = (Node<String>) huffTree.root();
                while (true) {
                    if (curr.getLeft() == null && curr.getRight() == null) {
                        break;
                    }

                    String left = curr.getLeft().getElement();
                    String right = curr.getRight().getElement();

                    if (left.contains(strC)) {
                        bitString += "0";
                        curr = curr.getLeft();
                    }
                    else if (right.contains(strC)) {
                        bitString += "1";
                        curr = curr.getRight();
                    }
                }
            }
        }

        //testing length of original dict
        int oDictLength = dict.size() * 5 * 8;
        double bitStringLength = (double) bitString.length();
        return bitStringLength / oDictLength;
    }
    public static void dictPrint(HashMap<String, Integer> dict) {
        boolean first = true;
        for (Map.Entry<String, Integer> entry : dict.entrySet()) {
            if (!first) {
                System.out.print(", ");
            }
            System.out.print(entry.getKey() + ": " + entry.getValue());
            first = false;
        }
    }
    public List<String> readDictionary(String fileName) {
        List<String> wordList = new ArrayList<>();

        try {
            // Open and read the dictionary file
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);
            assert in != null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String strLine;

            //Read file line By line
            while ((strLine = reader.readLine()) != null) {
                wordList.add(strLine.toLowerCase());
            }
            //Close the input stream
            in.close();

        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        return wordList;
    }
}
