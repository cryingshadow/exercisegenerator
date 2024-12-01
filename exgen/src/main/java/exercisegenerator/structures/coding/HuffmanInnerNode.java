package exercisegenerator.structures.coding;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.structures.*;

public class HuffmanInnerNode extends HuffmanNode {

    public final Map<Character, HuffmanNode> children;

    public HuffmanInnerNode(final int frequency, final Map<Character, HuffmanNode> children) {
        super(frequency);
        this.children = children;
    }

    @Override
    public int getDepth() {
        return this.children.values().stream().mapToInt(HuffmanNode::getDepth).max().getAsInt() + 1;
    }

    @Override
    public char getLeastSourceSymbol() {
        return this.children.values()
            .stream()
            .map(HuffmanNode::getLeastSourceSymbol)
            .reduce(Character.MAX_VALUE, (c1, c2) -> Character.compare(c1, c2) > 0 ? c2 : c1);
    }

    @Override
    public void toTikZ(final String prefix, final BufferedWriter writer) throws IOException {
        writer.write("[.\\phantom{0}");
        Main.newLine(writer);
        final String newPrefix = prefix + "  ";
        boolean first = true;
        for (final Map.Entry<Character, HuffmanNode> entry : this.children.entrySet()) {
            writer.write(newPrefix);
            if (first) {
                first = false;
                writer.write(String.format("\\edge node[auto=right] {%s};", entry.getKey()));
            } else {
                writer.write(String.format("\\edge node[auto=left] {%s};", entry.getKey()));
            }
            Main.newLine(writer);
            writer.write(newPrefix);
            entry.getValue().toTikZ(newPrefix, writer);
        }
        writer.write(prefix);
        writer.write("]");
        Main.newLine(writer);
    }

    @Override
    Pair<Character, List<Character>> decode(final List<Character> targetText) {
        return this.children.get(targetText.get(0)).decode(targetText.subList(1, targetText.size()));
    }

    @Override
    void fillCodeBook(final String prefix, final Map<Character, String> codeBook) {
        for (final Map.Entry<Character, HuffmanNode> child : this.children.entrySet()) {
            child.getValue().fillCodeBook(prefix + child.getKey(), codeBook);
        }
    }

}
