package exercisegenerator.structures.learning;

import java.util.*;

public record DecisionTreeLeaf(String classifier) implements DecisionTree {

    @Override
    public String classify(final Map<String, String> attributes) {
        return this.classifier();
    }


}
