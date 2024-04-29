package wordle;

import project20280.interfaces.Entry;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WordleCalc {

    String fileName = "wordle/resources/dictionary.txt";
    //String fileName = "wordle/resources/extended-dictionary.txt";
    List<String> dictionary = null;

    int guesses = 0;
    final int num_guesses = 6;
    final long seed = 42;
    //Random rand = new Random(seed);
    Random rand = new Random();

    static final String winMessage = "CONGRATULATIONS! YOU WON! :)";
    static final String lostMessage = "YOU LOST :( THE WORD CHOSEN BY THE GAME IS: ";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_GREY_BACKGROUND = "\u001B[100m";

    WordleCalc() {

        this.dictionary = readDictionary(fileName);

        System.out.println("dict length: " + this.dictionary.size());
        System.out.println("dict: " + dictionary);

    }

    public static void main(String[] args) {

        int j = 0;
        for (int i=0; i < 100; i++) {
            WordleCalc game = new WordleCalc();
            String target = game.getRandomTargetWord();
            System.out.println(target);
            Boolean bool = game.play(target);
            if (!(bool)) {
                j++;
            }
            System.out.print("number of successes: " + j);
        }
    }

    public boolean play(String target) {
        ArrayList<String> wrongAList = new ArrayList<>();
        ArrayList<String> superWrongAList = new ArrayList<>();
        Map<String, Integer> rightLetters = new HashMap<>();
        HEntry<Map<String, Integer>, List<String>> entry = null;
        Map<String, Integer> freqMap1 = Huffman.freqCounter(readDictionary(fileName));
        String[] guessArr = new String[1];
        for(int i = 0; i < num_guesses; ++i) {
            if (i == 0) {
                entry = solveInitial(freqMap1, dictionary, guessArr);
            }
            else if (i == 1) {
                Map<String, Integer> freqMap2 = entry.getKey();
                entry = solveInitial(freqMap2, entry.getValue(), guessArr);
            }
            else {
                solverPast(wrongAList, superWrongAList, rightLetters, guessArr);
            }

            String guess = guessArr[0];
            System.out.println(guess);

            if(guess == target) { // you won!
                win(target);
                return false;
            }

            // the hint is a string where green="+", yellow="o", grey="_"
            // didn't win ;(
            String [] hint = {"_", "_", "_", "_", "_"};

            for (int k = 0; k < 5; k++) {
                if (guess.charAt(k) == target.charAt(k)) {
                    hint[k] = "+";
                    rightLetters.putIfAbsent(String.valueOf(guess.charAt(k)), k);
                }
            }

            // set the arrays for yellow (present but not in right place), grey (not present)
            // loop over each entry:
            //  if hint == "+" (green) skip it
            //  else check if the letter is present in the target word. If yes, set to "o" (yellow)

            String wrongLetters = "";
            for (int k = 0; k < 5; k++) {
                if (hint[k] != "+") {
                    wrongLetters += target.charAt(k);
                }
            }

            for (int k = 0; k < 5; k++) {
                if (hint[k] == "+") {
                    continue;
                }
                int letterIndex = wrongLetters.indexOf(guess.charAt(k));
                if (letterIndex > -1) {
                    if (!(hasDuplicateLetterPreceding(guess, k))) {
                        hint[k] = "o";
                        wrongLetters = removeCharAtIndex(wrongLetters, letterIndex);
                    }
                }
                else {
                    if (countOccurrences(guess, String.valueOf(guess.charAt(k))) == 1) {
                        superWrongAList.add(String.valueOf(guess.charAt(k)));
                    }
                    if (!(countOccurrences(guess, String.valueOf(guess.charAt(k))) == 3)) {
                        wrongAList.add(String.valueOf(guess.charAt(k)));
                    }
                }
            }

            //accruing results from hint
            // after setting the yellow and green positions, the remaining hint positions must be "not present" or "_"
            System.out.println("hint: " + Arrays.toString(hint));


            // check for a win
            int num_green = 0;
            for(int k = 0; k < 5; ++k) {
                if(hint[k] == "+") num_green += 1;
            }
            if(num_green == 5) {
                win(target);
                return false;
            }
        }
        lost(target);
        return true;
    }
    public HEntry<Map<String, Integer>, List<String>> solveInitial(Map<String, Integer> freqMap, List<String> dictionary, String[] guessArr) {
        //copy array
        dictionary = new ArrayList<>(dictionary);
        int[] topValues = new int[3];
        String[] topKeys = new String[3];

        for (Map.Entry<String, Integer> entry : freqMap.entrySet()) {
            int value = entry.getValue();
            String key = entry.getKey();

            if (value > topValues[0]) {
                topValues[2] = topValues[1];
                topKeys[2] = topKeys[1];
                topValues[1] = topValues[0];
                topKeys[1] = topKeys[0];
                topValues[0] = value;
                topKeys[0] = key;
            } else if (value > topValues[1]) {
                topValues[2] = topValues[1];
                topKeys[2] = topKeys[1];
                topValues[1] = value;
                topKeys[1] = key;
            } else if (value > topValues[2]) {
                topValues[2] = value;
                topKeys[2] = key;
            }
        }

        ArrayList<String> guessWords = new ArrayList<>();
        for (String s : dictionary) {
            int correctDegree = 0;
            for (String subS: topKeys) {
                if (s.contains(subS)) {
                    correctDegree ++;
                }
            }
            if (correctDegree == 3) {
                guessWords.add(s);
            }
        }
        //adding frequencies of other letters in word, and comparing for greatest
        String mostWord = "";
        Integer mostFreq = 0;
        for (String s : guessWords) {
            if (hasDuplicateCharacters(s)) {
                continue;
            }
            String currWord = s;
            Integer currFreq = 0;
            for (char c: s.toCharArray()) {
                String strC = String.valueOf(c);
                if (isStringInArray(topKeys, strC)) {
                    continue;
                }
                else {
                    currFreq += freqMap.get(strC);
                }
            }
            if (currFreq > mostFreq) {
                mostFreq = currFreq;
                mostWord = currWord;
            }
        }
        //remove used characters from return dictionary
        for (char c: mostWord.toCharArray()) {
            freqMap.remove(String.valueOf(c));
        }
        for (String s: readDictionary(fileName)) {
            for (char c : s.toCharArray()) {
                String strC = String.valueOf(c);
                if (freqMap.get(strC) == null) {
                    dictionary.remove(s);
                    break;
                }
            }
        }
        guessArr[0] = mostWord;
        HEntry<Map<String, Integer>, List<String>> mapNList = new HEntry<>(freqMap, dictionary);
        return mapNList;
    }
    public void solverPast(ArrayList<String> wrongList, ArrayList<String> superWrongList, Map<String, Integer> rightLetters, String[] guessArr) {
        /*find 3 most common letters */
        ArrayList<String> wordsToRemove = new ArrayList<>();

        /* find 3 most common letters */
        for (String subS : wrongList) {
            Iterator<String> iterator = dictionary.iterator();
            while (iterator.hasNext()) {
                String s = iterator.next();
                if (countOccurrences(s,subS) == 2) {
                    wordsToRemove.add(s);
                }
            }
            iterator = dictionary.iterator();
            while (iterator.hasNext()) {
                String s = iterator.next();
                for (Map.Entry<String, Integer> entry : rightLetters.entrySet()) {
                    if (!String.valueOf(s.charAt(entry.getValue())).equals(entry.getKey())) {
                        wordsToRemove.add(s);
                    }
                }
            }
        }
        for (String wrongS : superWrongList) {
            Iterator<String> iterator = dictionary.iterator();
            while (iterator.hasNext()) {
                String s = iterator.next();
                if (s.contains(wrongS)) {
                    wordsToRemove.add(s);
                }
            }
        }
        dictionary.removeAll(wordsToRemove);
        // Remove the words that need to be removed
        guessArr[0] = getRandomTargetWord();
        dictionary.remove(guessArr[0]);
    }
    //utility function
    public static boolean hasDuplicateCharacters(String word) {
        boolean[] seen = new boolean[256]; // Assuming ASCII characters
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (seen[ch]) {
                return true; // If the character is already seen, return true
            }
            seen[ch] = true; // Mark the character as seen
        }
        return false; // No duplicate characters found
    }
    //utility function
    public static boolean isStringInArray(String[] array, String target) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(target)) {
                return true;
            }
        }
        return false;
    }

    //utility function
    public static boolean hasDuplicateLetterPreceding(String str, int index) {
        char letterToCheck = str.charAt(index);

        for (int i = 0; i < index; i++) {
            if (str.charAt(i) == letterToCheck) {
                return true; // Found a duplicate letter preceding the specified index
            }
        }
        return false; // No duplicate letter preceding the specified index
    }

    //utility function
    public static String removeCharAtIndex(String str, int index) {

        // Create a new string by concatenating the substring before and after the character to be removed
        return str.substring(0, index) + str.substring(index + 1);
    }
    public void lost(String target) {

        System.out.println();
        System.out.println(lostMessage + target.toUpperCase() + ".");
        System.out.println();

    }
    public void win(String target) {
        System.out.println(ANSI_GREEN_BACKGROUND + target.toUpperCase() + ANSI_RESET);
        System.out.println();
        System.out.println(winMessage);
        System.out.println();
    }

    public String getGuess() {
        Scanner myScanner = new Scanner(System.in, StandardCharsets.UTF_8.displayName());  // Create a Scanner object
        System.out.println("Guess:");

        String userWord = myScanner.nextLine();  // Read user input
        userWord = userWord.toLowerCase(); // covert to lowercase

        // check the length of the word and if it exists
        while ((userWord.length() != 5) || !(dictionary.contains(userWord))) {
            if ((userWord.length() != 5)) {
                System.out.println("The word " + userWord + " does not have 5 letters.");
            } else {
                System.out.println("The word " + userWord + " is not in the word list.");
            }
            // Ask for a new word
            System.out.println("Please enter a new 5-letter word.");
            userWord = myScanner.nextLine();
        }
        return userWord;
    }
    //utility
    public static int countOccurrences(String str, String target) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (String.valueOf(str.charAt(i)).equals(target)) {
                count++;
            }
        }
        return count;
    }

    public String getRandomTargetWord() {
        // generate random values from 0 to dictionary size
        return dictionary.get(rand.nextInt(dictionary.size()));
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

    protected static class HEntry<K, V> implements project20280.interfaces.Entry<K, V> {
        private K k;  // key
        private V v;  // value

        public HEntry(K key, V value) {
            k = key;
            v = value;
        }

        // methods of the Entry interface
        public K getKey() {
            return k;
        }

        public V getValue() {
            return v;
        }

        // utilities not exposed as part of the Entry interface
        protected void setKey(K key) {
            k = key;
        }

        protected void setValue(V value) {
            v = value;
        }

        public String toString() {
            //return "<" + k + ", " + v + ">";
            return String.valueOf(k);
        }
    }
}
