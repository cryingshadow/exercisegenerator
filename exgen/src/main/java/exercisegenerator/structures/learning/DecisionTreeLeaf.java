package exercisegenerator.structures.learning;

import java.util.*;

import exercisegenerator.*;

public record DecisionTreeLeaf(String classifier) implements DecisionTree {

    @Override
    public String classify(final Map<String, String> attributes) {
        return this.classifier();
    }

    @Override
    public String toString() {
        return String.format("\\Tree [.%s ];", this.classifier());
    }

    @Override
    public String toStringRecursive(final int level) {
        final StringBuilder result = new StringBuilder();
        result.append(Main.lineSeparator);
        result.append("  ".repeat(level));
        result.append(this.classifier());
        return result.toString();
    }

}
