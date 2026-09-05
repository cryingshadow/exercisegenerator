package exercisegenerator.structures.learning;

import java.util.*;

public interface DecisionTree {

    String classify(Map<String, String> attributes);

    List<String> getCalculations();

    String toStringRecursive(int level);

}
