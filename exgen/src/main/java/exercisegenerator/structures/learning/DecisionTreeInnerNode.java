package exercisegenerator.structures.learning;

import java.util.*;
import java.util.stream.*;

import exercisegenerator.*;

public record DecisionTreeInnerNode (
    Map<String, DecisionTree> children,
    String selector,
    Map<String, Double> entropies,
    Set<String> used
) implements DecisionTree {

    @Override
    public String classify(final Map<String, String> attributes) {
        return this.children().get(attributes.get(this.selector())).classify(attributes);
    }

    @Override
    public String toString() {
        return "\\Tree" + this.toStringRecursive(1);
    }

    @Override
    public String toStringRecursive(final int level) {
        final StringBuilder result = new StringBuilder();
        result.append(Main.lineSeparator);
        result.append("  ".repeat(level));
        result.append("[.{\\begin{minipage}{4cm}");
        result.append(
            this.entropies
            .entrySet()
            .stream()
            .map(entry ->
                String.format(
                    this.selector.equals(entry.getKey()) ? "\\underline{%s}: %s" : "%s: %s",
                    entry.getKey(),
                    DecisionTreeInnerNode.formatEntropy(entry.getValue())
                )
            ).collect(Collectors.joining("\\\\"))
        );
        result.append("\\end{minipage}}");
        for (final Map.Entry<String, DecisionTree> child : this.children().entrySet()) {
            result.append(
                String.format(
                    "%s%s\\edge node[midway] {%s};%s",
                    Main.lineSeparator,
                    "  ".repeat(level + 1),
                    child.getKey(),
                    child.getValue().toStringRecursive(level + 1)
                )
            );
        }
        result.append(Main.lineSeparator);
        result.append("  ".repeat(level));
        result.append("]");
        return result.toString();
    }

    private static String formatEntropy(final double value) {
        return String.format("%.3f", value, Locale.GERMANY).replaceAll(",", "{,}");
    }

}
