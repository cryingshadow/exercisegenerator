package exercisegenerator.structures.hashing;

import java.util.*;

import exercisegenerator.structures.*;

public record HashProblem(
    IntegerList[] initialHashTable,
    List<Integer> values,
    HashFunctionWithParameters hashFunction,
    Optional<ProbingFunctionWithParameters> optionalProbingFunction
) {}
