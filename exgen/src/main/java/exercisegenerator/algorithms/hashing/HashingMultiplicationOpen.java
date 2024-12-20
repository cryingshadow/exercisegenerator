package exercisegenerator.algorithms.hashing;

import java.io.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.hashing.*;

public class HashingMultiplicationOpen implements Hashing {

    public static final HashingMultiplicationOpen INSTANCE = new HashingMultiplicationOpen();

    private HashingMultiplicationOpen() {}

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
                    " ($f(n,i) = \\left \\lfloor{%d \\cdot ( n \\cdot %s \\mod 1 )\\right \\rfloor $), wobei $x \\mod 1$ den Nachkommateil von $x$ bezeichnet",
                    capacity,
                    LaTeXUtils.toCoefficient(factor)
                ) :
                    ""
        );
    }

    @Override
    public Optional<ProbingFunctionWithParameters> optionalProbingFunction(
        final int capacity,
        final Parameters<Flag> options
    ) throws IOException {
        return Optional.empty();
    }

}
