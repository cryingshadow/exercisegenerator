package exercisegenerator.structures;

import java.util.*;
import java.util.stream.*;

public class HuffmanEncoder {

    private final Map<Character, String> codeBook;

    public HuffmanEncoder(final Map<Character, String> codeBook) {
        this.codeBook = codeBook;
    }

    public String encode(final String sourceText) {
        return sourceText.chars()
            .mapToObj(c -> this.codeBook.get(Character.valueOf((char)c)))
            .collect(Collectors.joining());
    }

}
