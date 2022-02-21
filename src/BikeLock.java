import com.sun.source.tree.Tree;

import java.io.File;
import java.sql.Array;
import java.util.*;

public class BikeLock {
    private ArrayList<Dial> dials;

    public BikeLock() {
        dials = new ArrayList<>();
    }

    public static void main(String args[]) {
        File file = new File(args[0]);
        int numDials = Integer.parseInt(args[1]);

        Dictionary dictionary = new Dictionary(file, numDials);
        System.out.println("Total " + numDials + "-letter words: " + dictionary.getDictionary().size());
        BikeLock bikeLock = new BikeLock();
        bikeLock.dials = bikeLock.getAllDials(dictionary, numDials);
        System.out.println(bikeLock.toString());
    }

    public ArrayList<Dial> getAllDials(Dictionary dictionary, Integer numDials) {
        ArrayList<Dial> dials = new ArrayList<>();
        HashSet<String> tempWordList = new HashSet<>(dictionary.getDictionary());
        for (int i = 0; i < numDials; i++) {
            Dial dial = getBestDial(tempWordList, i);
            dials.add(dial);
            tempWordList.clear();
            tempWordList.addAll(dial.getPossibleWords());
        }

        return dials;
    }

    public Dial getBestDial(HashSet<String> possibleWords, Integer position) {
        HashMap<Character, HashSet<String>> letterWordBanks = new HashMap<>();
        //Sort possible words by letter
        for (String word : possibleWords) {
            char keyLetter = word.charAt(position);
            if (letterWordBanks.get(keyLetter) == null) {
                HashSet<String> wordBank = new HashSet<>();
                wordBank.add(word);
                letterWordBanks.put(keyLetter, wordBank);
            }
            else {
                letterWordBanks.get(keyLetter).add(word);
            }
        }

        Dial dial = new Dial(position);
        HashSet<Character> usedLetters = new HashSet<>(letterWordBanks.keySet());
        TreeSet<Character> bestLetters = new TreeSet<>();

        //Check if there are enough dial-letter candidates
        //IF NOT - fill extra spots, use as best letters
        if (usedLetters.size() < Dial.SIZE) {
            int extraSpots = Dial.SIZE - usedLetters.size();
            usedLetters.addAll(getFillerLetters(usedLetters, extraSpots));
            bestLetters.addAll(usedLetters);
        }
        //IF ENOUGH - find letters with most possible words and return them
        else {
            bestLetters = getBestLetters(letterWordBanks);
        }


        //Add letters and associated word banks to new dial
        dial.setLetters(bestLetters);
        for (Character c : bestLetters) {
            if (letterWordBanks.get(c) != null) {
                dial.getPossibleWords().addAll(letterWordBanks.get(c));
            }
        }

        return dial;
    }

    public ArrayList<Character> getFillerLetters(Collection<Character> usedLetters, int amount) {
        ArrayList<Character> alphabet = new ArrayList<>(26);
        for (int i = 0; i < 26; i++) {
            Character letter = (char)('a' + i);
            alphabet.add(letter);
        }

        alphabet.removeAll(usedLetters);

        ArrayList<Character> fillerLetters = new ArrayList<>();
        for (int i = 0; i < amount; i ++) {
            fillerLetters.add(alphabet.get(i));
        }
        return fillerLetters;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Dial d : dials) {
            str.append(d.toString());
        }
        //Append the completely filtered (last dial) list of possible words.
        str.append("Total word combos: ");
        str.append(dials.get(dials.size()-1).getPossibleWords().size());
        return str.toString();
    }

    private TreeSet<Character> getBestLetters(HashMap<Character, HashSet<String>> letterMap) {
        TreeSet<Character> bestLetters = new TreeSet<>();
        TreeMap<Integer, Character> letterBySize = new TreeMap<>();
        for (Character c : letterMap.keySet()) {
            Integer size = letterMap.get(c).size();
            letterBySize.put(size, c);
        }
        //Creates a collection of letters, with largest word bank size, at the top
        Collection extractedWordBanks = letterBySize.descendingMap().values();
        Iterator it = extractedWordBanks.iterator();

        for (int i = 0; i < Dial.SIZE; i++) {
            if (it.hasNext()) {
                bestLetters.add((Character) it.next());
            }
        }
        return bestLetters;
    }

}
