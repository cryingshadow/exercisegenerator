package exercisegenerator.structures.hashing;

import java.util.*;

import org.apache.commons.math3.fraction.*;

public record ProbingFunctionWithParameters(
    ProbingFunction probingFunction,
    String exerciseText,
    Map<String, BigFraction> parameters,
    String additionalProbingHint
) {}
