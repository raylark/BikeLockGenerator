import java.util.HashSet;
import java.util.TreeSet;

public class Dial {
    final static int SIZE = 10;
    private int position;
    private TreeSet<Character> letters;
    private HashSet<String> possibleWords;

    public Dial(int position) {
        this.position = position;
        letters = new TreeSet<>();
        possibleWords = new HashSet<>();
    }

    public int getSize() {
        return SIZE;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public TreeSet<Character> getLetters() {
        return letters;
    }

    public void setLetters(TreeSet<Character> letters) {
        this.letters = letters;
    }

    public HashSet<String> getPossibleWords() {
        return possibleWords;
    }

    public void setPossibleWords(HashSet<String> possibleWords) {
        this.possibleWords = possibleWords;
    }

    @Override
    public String toString() {
        return "Dial " + (position + 1) + " " + letters + "\n";
    }
}
