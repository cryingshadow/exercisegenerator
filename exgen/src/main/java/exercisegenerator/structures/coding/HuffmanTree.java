package exercisegenerator.structures.coding;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.structures.*;

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

    public HuffmanTree(final Map<Character, String> codeBook) {
        final PriorityQueue<Pair<String, HuffmanNode>> queue =
            new PriorityQueue<Pair<String, HuffmanNode>>(
                new Comparator<Pair<String, HuffmanNode>>() {

                    @Override
                    public int compare(final Pair<String, HuffmanNode> pair1, final Pair<String, HuffmanNode> pair2) {
                        final String prefix1 = pair1.x;
                        final String prefix2 = pair2.x;
                        final int lengthComparison = Integer.compare(prefix2.length(), prefix1.length());
                        if (lengthComparison != 0) {
                            return lengthComparison;
                        }
                        return prefix1.compareTo(prefix2);
                    }

                }
            );
        for (final Map.Entry<Character, String> entry : codeBook.entrySet()) {
            queue.offer(new Pair<String, HuffmanNode>(entry.getValue(), new HuffmanLeaf(0, entry.getKey())));
        }
        while (queue.size() > 1) {
            Pair<String, HuffmanNode> prefixAndNode = queue.poll();
            final int length = prefixAndNode.x.length();
            if (length == 0) {
                throw new IllegalArgumentException("The specified code book does not yield a proper Huffman tree!");
            }
            final String prefix = prefixAndNode.x.substring(0, length - 1);
            final Map<Character, HuffmanNode> children = new LinkedHashMap<Character, HuffmanNode>();
            while (prefixAndNode != null && prefixAndNode.x.length() == length && prefixAndNode.x.startsWith(prefix)) {
                children.put(prefixAndNode.x.charAt(length - 1), prefixAndNode.y);
                prefixAndNode = queue.poll();
            }
            if (prefixAndNode != null) {
                queue.offer(prefixAndNode);
            }
            queue.offer(new Pair<String, HuffmanNode>(prefix, new HuffmanInnerNode(0, children)));
        }
        final Pair<String, HuffmanNode> result = queue.poll();
        if (result == null || !result.x.isEmpty()) {
            throw new IllegalArgumentException("The specified code book does not yield a proper Huffman tree!");
        }
        this.root = Optional.of(result.y);
    }

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

    public String decode(final String targetText) {
        if (this.root.isEmpty()) {
            return targetText;
        }
        final HuffmanNode rootNode = this.root.get();
        Pair<Character, List<Character>> decoded = rootNode.decode(targetText.chars().mapToObj(c -> (char)c).toList());
        final StringBuilder result = new StringBuilder();
        result.append(decoded.x);
        while (!decoded.y.isEmpty()) {
            decoded = rootNode.decode(decoded.y);
            result.append(decoded.x);
        }
        return result.toString();
    }

    public HuffmanCodeBook toCodeBook() {
        final HuffmanCodeBook codeBook = new HuffmanCodeBook();
        if (this.root.isPresent()) {
            this.root.get().fillCodeBook("", codeBook);
        }
        return codeBook;
    }

    public HuffmanEncoder toEncoder() {
        return new HuffmanEncoder(this.toCodeBook());
    }

    public void toTikZ(final BufferedWriter writer) throws IOException {
        if (this.root.isEmpty()) {
            return;
        }
        writer.write("\\Tree ");
        final HuffmanNode node = this.root.get();
        if (node instanceof HuffmanLeaf) {
            writer.write("[.");
            node.toTikZ("", writer);
            writer.write(" ]");
        } else {
            node.toTikZ("      ", writer);
        }
        Main.newLine(writer);
    }

}
