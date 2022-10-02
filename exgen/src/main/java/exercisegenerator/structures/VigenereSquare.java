package exercisegenerator.structures;

import java.io.*;
import java.util.*;

import exercisegenerator.io.*;

public class VigenereSquare {

    private final List<Character> alphabet;

    private final Map<Character, Integer> charMap;

    private final int size;

    public VigenereSquare(final List<Character> alphabet) {
        this.alphabet = alphabet;
        this.size = alphabet.size();
        this.charMap = new LinkedHashMap<Character, Integer>();
        for (int i = 0; i < this.size; i++) {
            final char c = alphabet.get(i);
            this.charMap.put(c, i);
        }
    }

    public char decode(final char cipher, final char key) {
        return this.alphabet.get((this.charMap.get(cipher) - this.charMap.get(key) + this.size) % this.size);
    }

    public char encode(final char source, final char key) {
        return this.alphabet.get((this.charMap.get(source) + this.charMap.get(key)) % this.size);
    }

    public void toLaTeX(final BufferedWriter writer) throws IOException {
        final String[][] table = new String[this.size][2];
        for (int i = 0; i < this.size; i++) {
            table[i][0] = String.valueOf(i);
            table[i][1] = String.valueOf(this.alphabet.get(i));
        }
        LaTeXUtils.printTable(table, Optional.empty(), LaTeXUtils.defaultColumnDefinition("1.5em"), false, 13, writer);
    }

}
