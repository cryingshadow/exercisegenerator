package exercisegenerator.structures.coding;

import java.io.*;
import java.util.*;

import exercisegenerator.structures.*;

public abstract class HuffmanNode implements Comparable<HuffmanNode> {

    public final int frequency;

    public HuffmanNode(final int frequency) {
        this.frequency = frequency;
    }

    @Override
    public int compareTo(final HuffmanNode otherNode) {
        final int frequencyComparison = Integer.compare(this.frequency, otherNode.frequency);
        if (frequencyComparison != 0) {
            return frequencyComparison;
        }
        final int depthComparison = Integer.compare(this.getDepth(), otherNode.getDepth());
        if (depthComparison != 0) {
            return depthComparison;
        }
        return Character.compare(this.getLeastSourceSymbol(), otherNode.getLeastSourceSymbol());
    }

    public abstract int getDepth();

    public abstract char getLeastSourceSymbol();

    public abstract void toTikZ(String prefix, final BufferedWriter writer) throws IOException;

    abstract Pair<Character, List<Character>> decode(List<Character> targetText);

    abstract void fillCodeBook(String prefix, Map<Character, String> codeBook);

}
