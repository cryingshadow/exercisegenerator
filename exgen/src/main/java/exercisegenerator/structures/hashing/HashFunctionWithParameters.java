package exercisegenerator.structures.hashing;

import java.util.*;
import java.util.function.*;

import org.apache.commons.math3.fraction.*;

public record HashFunctionWithParameters(
    HashFunction hashFunction,
    String exerciseText,
    Map<String, BigFraction> parameters,
    Function<String, String> additionalHint
) {}
