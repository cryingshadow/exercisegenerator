package exercisegenerator.structures;

import exercisegenerator.util.*;

@FunctionalInterface
public interface ProbingFunction {

    int apply(int value, int initialPosition, int numberOfCollisions, int capacity) throws HashException;

}
