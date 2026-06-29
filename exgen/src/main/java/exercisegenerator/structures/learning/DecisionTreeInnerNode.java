package exercisegenerator.structures.learning;

import java.util.*;

public record DecisionTreeInnerNode (Map<String, DecisionTree> children, String selector) implements DecisionTree {

    @Override
    public String classify(final Map<String, String> attributes) {
        return this.children().get(attributes.get(this.selector())).classify(attributes);
    }

}
