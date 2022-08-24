package exercisegenerator.structures;

import java.util.*;

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
    void fillCodeBook(final String prefix, final Map<Character, String> codeBook) {
        for (final Map.Entry<Character, HuffmanNode> child : this.children.entrySet()) {
            child.getValue().fillCodeBook(prefix + child.getKey(), codeBook);
        }
    }

}
