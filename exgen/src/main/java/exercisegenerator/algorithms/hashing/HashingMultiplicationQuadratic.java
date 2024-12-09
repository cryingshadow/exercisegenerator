package exercisegenerator.algorithms.hashing;

import java.io.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.algebra.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.hashing.*;

public class HashingMultiplicationQuadratic implements Hashing {

    public static final HashingMultiplicationQuadratic INSTANCE = new HashingMultiplicationQuadratic();

    private HashingMultiplicationQuadratic() {}

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public HashFunctionWithParameters hashFunction(final int capacity, final Parameters options) throws IOException {
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
                    Locale.GERMANY,
                    " ($f(n,i) = \\left \\lfloor{%d \\cdot ( n \\cdot %s \\mod 1 )}\\right \\rfloor + %s $), wobei $x \\mod 1$ den Nachkommateil von $x$ bezeichnet",
                    capacity,
                    AlgebraAlgorithms.toCoefficient(factor),
                    text
                ) :
                    ""
        );
    }

    @Override
    public Optional<ProbingFunctionWithParameters> optionalProbingFunction(
        final int capacity,
        final Parameters options
    ) throws IOException {
        final ProbingFactors probingFactors = Hashing.parseOrGenerateProbingFactors(capacity, options);
        final Map<String, BigFraction> parameters = new LinkedHashMap<String, BigFraction>();
        parameters.put("c_1", probingFactors.linearProbingFactor);
        parameters.put("c_2", probingFactors.quadraticProbingFactor);
        return Optional.of(
            new ProbingFunctionWithParameters(
                new QuadraticProbing(probingFactors),
                Hashing.toQuadraticProbingText(
                    probingFactors.linearProbingFactor,
                    probingFactors.quadraticProbingFactor
                ),
                parameters,
                String.format(
                    "\\lfloor %s \\cdot i + %s \\cdot i^2\\rfloor \\mod %d",
                    AlgebraAlgorithms.toCoefficient(probingFactors.linearProbingFactor),
                    AlgebraAlgorithms.toCoefficient(probingFactors.quadraticProbingFactor),
                    capacity
                )
            )
        );
    }

}
