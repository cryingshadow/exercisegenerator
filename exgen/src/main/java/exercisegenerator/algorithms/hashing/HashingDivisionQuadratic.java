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

public class HashingDivisionQuadratic implements AlgorithmImplementation {

    public static final HashingDivisionQuadratic INSTANCE = new HashingDivisionQuadratic();

    private static String toAdditionalHint(
        final int length,
        final BigFraction linearProbingFactor,
        final BigFraction quadraticProbingFactor,
        final int length2
    ) {
        return Main.TEXT_VERSION == TextVersion.ABRAHAM ?
            String.format(
                " ($f(n,i) = ((n \\mod %d) + \\lfloor %s \\cdot i + %s \\cdot i^2\\rfloor) \\mod %d$)",
                length,
                AlgebraAlgorithms.toCoefficient(linearProbingFactor),
                AlgebraAlgorithms.toCoefficient(quadraticProbingFactor),
                length2
            ) :
                "";
    }

    private static String toParameterString(
        final int capacity,
        final BigFraction linearProbingFactor,
        final BigFraction quadraticProbingFactor
    ) {
        return String.format(
            "$m = %d$, $c_1 = %s$, $c_2 = %s$%s",
            capacity,
            AlgebraAlgorithms.toCoefficient(linearProbingFactor),
            AlgebraAlgorithms.toCoefficient(quadraticProbingFactor),
            Hashing.PARAMETER_STRING_END
        );
    }

    private HashingDivisionQuadratic() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final int numOfValues = Hashing.parseOrGenerateNumberOfValues(input.options);
        final IntegerList[] initialHashTable = Hashing.parseOrGenerateInitialArray(numOfValues, input.options);
        final int capacity = initialHashTable.length;
        final ProbingFactors probingFactors = Hashing.parseOrGenerateProbingFactors(capacity, input.options);
        final List<Integer> values =
            Hashing.parseOrGenerateValues(
                numOfValues,
                capacity,
                Optional.empty(),
                Optional.of(probingFactors),
                input.options
            );
        try {
            final HashResult result = Hashing.hashingWithDivisionMethod(
                values,
                initialHashTable,
                Optional.of(
                    Hashing.quadraticProbing(probingFactors.linearProbingFactor, probingFactors.quadraticProbingFactor)
                )
            );
            Hashing.printHashingExerciseAndSolution(
                values,
                initialHashTable,
                result.result,
                new PrintOptions(
                    Hashing.DIVISION_METHOD
                    .concat(
                        Hashing.toQuadraticProbingText(
                            probingFactors.linearProbingFactor,
                            probingFactors.quadraticProbingFactor
                        )
                    ).concat(Hashing.GENERAL_HASHING_EXERCISE_TEXT_END)
                    .concat(
                        HashingDivisionQuadratic.toAdditionalHint(
                            initialHashTable.length,
                            probingFactors.linearProbingFactor,
                            probingFactors.quadraticProbingFactor,
                            initialHashTable.length
                        )
                    ),
                    HashingDivisionQuadratic.toParameterString(
                        initialHashTable.length,
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
        } catch (final HashException e) {
            throw new IOException(e);
        }
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
