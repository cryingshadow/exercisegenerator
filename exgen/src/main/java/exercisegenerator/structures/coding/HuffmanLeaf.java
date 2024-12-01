package exercisegenerator.structures.coding;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;

public class HuffmanLeaf extends HuffmanNode {

    public final char sourceSymbol;

    public HuffmanLeaf(final int frequency, final char sourceSymbol) {
        super(frequency);
        this.sourceSymbol = sourceSymbol;
    }

    @Override
    public int getDepth() {
        return 0;
    }

    @Override
    public char getLeastSourceSymbol() {
        return this.sourceSymbol;
    }

    @Override
    public void toTikZ(final String prefix, final BufferedWriter writer) throws IOException {
        writer.write(String.format("%d/%s", this.frequency, LaTeXUtils.escapeForLaTeX(this.sourceSymbol)));
        Main.newLine(writer);
    }

    @Override
    Pair<Character, List<Character>> decode(final List<Character> targetText) {
        return new Pair<Character, List<Character>>(this.sourceSymbol, targetText);
    }

    @Override
    void fillCodeBook(final String prefix, final Map<Character, String> codeBook) {
        codeBook.put(this.sourceSymbol, prefix);
    }

}
