import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;

public class Dictionary {

    final private HashSet<String> dictionary;
    final private int WORD_LENGTH;

    public Dictionary(File file, int wordLength) {
        this.WORD_LENGTH = wordLength;
        dictionary = new HashSet<>();
        fillDictionary(file);
    }

    private void fillDictionary(File file) {
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String word = scanner.next();
                if (word.length() == WORD_LENGTH) {
                    dictionary.add(word);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashSet<String> getDictionary() {
        return dictionary;
    }
}
