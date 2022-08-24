package exercisegenerator.structures;

import java.util.*;

public class HuffmanTree {

    private static Optional<HuffmanNode> buildHuffmanTree(
        final List<Character> targetAlphabet,
        final List<HuffmanNode> roots
    ) {
        if (roots.isEmpty()) {
            return Optional.empty();
        }
        final PriorityQueue<HuffmanNode> queue = new PriorityQueue<HuffmanNode>(roots);
        while (queue.size() > 1) {
            final Map<Character, HuffmanNode> children = new LinkedHashMap<Character, HuffmanNode>();
            int frequency = 0;
            for (final Character symbol : targetAlphabet) {
                if (queue.isEmpty()) {
                    break;
                }
                final HuffmanNode node  = queue.poll();
                frequency += node.frequency;
                children.put(symbol, node);
            }
            queue.offer(new HuffmanInnerNode(frequency, children));
        }
        return Optional.of(queue.poll());
    }

    private static Map<Character, Integer> countFrequencies(final String sourceText) {
        final Map<Character, Integer> frequencies = new LinkedHashMap<Character, Integer>();
        for (final char c : sourceText.toCharArray()) {
            frequencies.put(c, frequencies.getOrDefault(c, 0) + 1);
        }
        return frequencies;
    }

    private static List<HuffmanNode> toRootNodes(final Map<Character, Integer> frequencies) {
        final List<HuffmanNode> result = new ArrayList<HuffmanNode>();
        for (final Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
            result.add(new HuffmanLeaf(entry.getValue(), entry.getKey()));
        }
        return result;
    }

    private final Optional<HuffmanNode> root;

    public HuffmanTree(final String sourceText, final List<Character> targetAlphabet) {
        this(
            HuffmanTree.buildHuffmanTree(
                targetAlphabet,
                HuffmanTree.toRootNodes(HuffmanTree.countFrequencies(sourceText))
            )
        );
    }

    private HuffmanTree(final Optional<HuffmanNode> root) {
        this.root = root;
    }

    public Map<Character, String> toCodeBook() {
        final Map<Character, String> codeBook = new LinkedHashMap<Character, String>();
        if (this.root.isPresent()) {
            this.root.get().fillCodeBook("", codeBook);
        }
        return codeBook;
    }

    public HuffmanEncoder toEncoder() {
        return new HuffmanEncoder(this.toCodeBook());
    }

}
