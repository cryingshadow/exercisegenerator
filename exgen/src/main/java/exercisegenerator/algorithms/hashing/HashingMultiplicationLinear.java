package exercisegenerator.algorithms.hashing;

import java.io.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.hashing.*;

public class HashingMultiplicationLinear implements Hashing {

    public static final HashingMultiplicationLinear INSTANCE = new HashingMultiplicationLinear();

    private HashingMultiplicationLinear() {}

    @Override
    public String commandPrefix() {
        return "HashingMultiplicationLinear";
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public HashFunctionWithParameters hashFunction(
        final int capacity,
        final Parameters<Flag> options
    ) throws IOException {
        final BigFraction factor = Hashing.parseOrGenerateMultiplicationFactor(options);
        final Map<String, BigFraction> parameters = new LinkedHashMap<String, BigFraction>();
        parameters.put("m", new BigFraction(capacity));
        parameters.put("c", factor);
        return new HashFunctionWithParameters(
            new MultiplicationMethod(capacity, factor),
            Hashing.toMultiplicationMethodExerciseText(factor),
            parameters,
            text -> Main.TEXT_VERSION == TextVersion.ABRAHAM ?
                String.format(
                    " ($f(n,i) = \\left \\lfloor{%d \\cdot ( n \\cdot %s \\mod 1 )}\\right \\rfloor~ + i \\mod %d$), wobei $x \\mod 1$ den Nachkommateil von $x$ bezeichnet",
                    capacity,
                    LaTeXUtils.toCoefficient(factor),
                    capacity
                ) :
                    ""
        );
    }

    @Override
    public Optional<ProbingFunctionWithParameters> optionalProbingFunction(
        final int capacity,
        final Parameters<Flag> options
    ) throws IOException {
        return Optional.of(
            new ProbingFunctionWithParameters(
                LinearProbing.INSTANCE,
                Hashing.LINEAR_PROBING,
                Map.of(),
                ""
            )
        );
    }

}
