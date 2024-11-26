package exercisegenerator.algorithms.hashing;

import java.io.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.algebra.*;
import exercisegenerator.algorithms.hashing.Hashing.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.util.*;

public class HashingMultiplicationQuadratic implements AlgorithmImplementation {

    public static final HashingMultiplicationQuadratic INSTANCE = new HashingMultiplicationQuadratic();

    private static String toAdditionalHint(
        final int capacity,
        final BigFraction multiplicationFactor,
        final BigFraction linearProbingFactor,
        final BigFraction quadraticProbingFactor
    ) {
        return Main.TEXT_VERSION == TextVersion.ABRAHAM ?
            String.format(
                Locale.GERMANY,
                " ($f(n,i) = \\left \\lfloor{%d \\cdot ( n \\cdot %s \\mod 1 )}\\right \\rfloor + \\lfloor %s \\cdot i + %s \\cdot i^2\\rfloor $), wobei $x \\mod 1$ den Nachkommateil von $x$ bezeichnet",
                capacity,
                AlgebraAlgorithms.toCoefficient(multiplicationFactor),
                AlgebraAlgorithms.toCoefficient(linearProbingFactor),
                AlgebraAlgorithms.toCoefficient(quadraticProbingFactor)
            ):
                "";
    }

    private static String toParameterString(
        final int capacity,
        final BigFraction multiplicationFactor,
        final BigFraction linearProbingFactor,
        final BigFraction quadraticProbingFactor
    ) {
        return String.format(
            Locale.GERMANY,
            "$m = %d$, $c = %s$, $c_1 = %s$, $c_2 = %s$%s",
            capacity,
            AlgebraAlgorithms.toCoefficient(multiplicationFactor),
            AlgebraAlgorithms.toCoefficient(linearProbingFactor),
            AlgebraAlgorithms.toCoefficient(quadraticProbingFactor),
            Hashing.PARAMETER_STRING_END
        );
    }

    private HashingMultiplicationQuadratic() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final int numOfValues = Hashing.parseOrGenerateNumberOfValues(input.options);
        final IntegerList[] initialHashTable = Hashing.parseOrGenerateInitialArray(numOfValues, input.options);
        final int capacity = initialHashTable.length;
        final ProbingFactors probingFactors = Hashing.parseOrGenerateProbingFactors(capacity, input.options);
        final BigFraction factor = Hashing.parseOrGenerateMultiplicationFactor(input.options);
        final List<Integer> values =
            Hashing.parseOrGenerateValues(
                numOfValues,
                capacity,
                Optional.of(factor),
                Optional.of(probingFactors),
                input.options
            );
        IntegerList[] result;
        try {
            result = Hashing.hashingWithMultiplicationMethod(
                values,
                initialHashTable,
                factor,
                Optional.of(
                    Hashing.quadraticProbing(probingFactors.linearProbingFactor, probingFactors.quadraticProbingFactor)
                )
            );
        } catch (final HashException e) {
            throw new IOException(e);
        }
        Hashing.printHashingExerciseAndSolution(
            values,
            initialHashTable,
            result,
            new PrintOptions(
                Hashing.toMultiplicationMethodExerciseText(factor)
                .concat(
                    Hashing.toQuadraticProbingText(
                        probingFactors.linearProbingFactor,
                        probingFactors.quadraticProbingFactor
                    )
                ).concat(Hashing.GENERAL_HASHING_EXERCISE_TEXT_END)
                .concat(
                    HashingMultiplicationQuadratic.toAdditionalHint(
                        initialHashTable.length,
                        factor,
                        probingFactors.linearProbingFactor,
                        probingFactors.quadraticProbingFactor
                    )
                ),
                HashingMultiplicationQuadratic.toParameterString(
                    initialHashTable.length,
                    factor,
                    probingFactors.linearProbingFactor,
                    probingFactors.quadraticProbingFactor
                ),
                true,
                PreprintMode.parsePreprintMode(input.options)
            ),
            input.options,
            input.exerciseWriter,
            input.solutionWriter
        );
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
