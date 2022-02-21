import java.io.File;
import java.util.*;

public class BikeLock {
    private ArrayList<Dial> dials;

    public BikeLock() {
        dials = new ArrayList<>();
    }

    public static void main(String args[]) {
        boolean isPlaying = true;
        //Game loop
        while(isPlaying) {
            System.out.println("\n~~~~Welcome to the Bike Lock Generator~~~~\n");
            boolean isBadInput = true;
            int numDials = 0;
            int dialSize = 0;
            //Retrieve number of dials from user
            while (isBadInput) {
                System.out.println("Enter the number of dials (1-26): ");
                Scanner input = new Scanner(System.in);
                String temp = input.nextLine();
                try {
                    numDials = Integer.parseInt(temp);
                    if (numDials <= 26 && numDials >= 1) {
                        isBadInput = false;
                    }
                    else {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("[Error] - Must enter a number 1-26. Try again.\n");
                }
            }
            isBadInput = true;
            while(isBadInput) {
                System.out.println("Enter the number of letters per dial (1-26): ");
                Scanner input = new Scanner(System.in);
                String temp = input.nextLine();
                try {
                    dialSize = Integer.parseInt(temp);
                    if (dialSize <= 26 && dialSize >= 1) {
                        isBadInput = false;
                    }
                    else {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("[Error] - Must enter a number 1-26. Try again.\n");
                }
            }

            Dial.size = dialSize;
            File file = new File("dictionary.txt");
            Dictionary dictionary = new Dictionary(file, numDials);

            System.out.println();
            System.out.println("Number of " + numDials + "-letter words in the dictionary: " + dictionary.getDictionary().size());

            BikeLock bikeLock = new BikeLock();
            bikeLock.dials = bikeLock.getAllDials(dictionary, numDials);
            System.out.println(bikeLock);

            System.out.println();
            isBadInput = true;
            while(isBadInput) {
                System.out.println("Would you like to view those words? (Y/N): ");
                Scanner input = new Scanner(System.in);
                String temp = input.nextLine();
                try {
                    if (temp.charAt(0) == 'Y' || temp.charAt(0) == 'y') {
                        System.out.println(bikeLock.dials.get(bikeLock.dials.size()-1).getPossibleWords());
                        isBadInput = false;
                    }
                    else if (temp.charAt(0) == 'N' || temp.charAt(0) == 'n') {
                        isBadInput = false;
                    }
                    else {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("[Error] - Must enter 'Y' or 'N'. Try again.\n");
                }
            }

            System.out.println("Enter anything to try again...: ");
            Scanner input = new Scanner(System.in);
            input.next();
        }
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
        if (usedLetters.size() < Dial.size) {
            int extraSpots = Dial.size - usedLetters.size();
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

        for (int i = 0; i < Dial.size; i++) {
            if (it.hasNext()) {
                bestLetters.add((Character) it.next());
            }
        }
        return bestLetters;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Dial d : dials) {
            str.append(d.toString());
        }
        //Append the completely filtered (last dial) list of possible words.
        str.append("Number your bike lock can make: ");
        str.append(dials.get(dials.size()-1).getPossibleWords().size());
        return str.toString();
    }
}
