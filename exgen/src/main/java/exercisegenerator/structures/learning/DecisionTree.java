package exercisegenerator.structures.learning;

import java.math.*;
import java.util.*;

public interface DecisionTree {

    String classify(Map<String, String> attributes);

    List<String> getCalculations(String prefix);

    Set<BigInteger> getDenominators();

    String toStringRecursive(int level);

}
