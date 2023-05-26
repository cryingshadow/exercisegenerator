package exercisegenerator.algorithms.hashing;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.hashing.Hashing.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;

public class HashingDivisionQuadratic implements AlgorithmImplementation {

    public static final HashingDivisionQuadratic INSTANCE = new HashingDivisionQuadratic();

    private static String toAdditionalHint(
        final int length,
        final int linearProbingFactor,
        final int quadraticProbingFactor,
        final int length2
    ) {
        return Main.TEXT_VERSION == TextVersion.ABRAHAM ?
            String.format(
                " ($f(n,i) = ((n \\mod %d) + %d \\cdot i + %d \\cdot i^2) \\mod %d$)",
                length,
                linearProbingFactor,
                quadraticProbingFactor,
                length2
            ) :
                "";
    }

    private static String toParameterString(
        final int capacity,
        final int linearProbingFactor,
        final int quadraticProbingFactor
    ) {
        return String.format(
            "m = %d, $c_1$ = %d, $c_2$ = %d%s",
            capacity,
            linearProbingFactor,
            quadraticProbingFactor,
            Hashing.PARAMETER_STRING_END
        );
    }

    private HashingDivisionQuadratic() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final List<Integer> values = Hashing.parseOrGenerateValues(input.options);
        final HashList[] initialHashTable = Hashing.parseOrGenerateInitialArray(values.size(), input.options);
        final HashResultWithProbingFactors resultWithProbingFactors =
            Hashing.parseOrGenerateProbingFactorsAndComputeResult(
                initialHashTable.length,
                (linearProbingFactor, quadraticProbingFactor) ->
                    Hashing.hashingWithDivisionMethod(
                        values,
                        initialHashTable,
                        Optional.of(Hashing.quadraticProbing(linearProbingFactor, quadraticProbingFactor))
                    ),
                input.options
            );
        Hashing.printHashingExerciseAndSolution(
            values,
            initialHashTable,
            resultWithProbingFactors.result,
            new PrintOptions(
                Hashing.DIVISION_METHOD
                .concat(
                    Hashing.toQuadraticProbingText(
                        resultWithProbingFactors.linearProbingFactor,
                        resultWithProbingFactors.quadraticProbingFactor
                    )
                ).concat(Hashing.GENERAL_HASHING_EXERCISE_TEXT_END)
                .concat(
                    HashingDivisionQuadratic.toAdditionalHint(
                        initialHashTable.length,
                        resultWithProbingFactors.linearProbingFactor,
                        resultWithProbingFactors.quadraticProbingFactor,
                        initialHashTable.length
                    )
                ),
                HashingDivisionQuadratic.toParameterString(
                    initialHashTable.length,
                    resultWithProbingFactors.linearProbingFactor,
                    resultWithProbingFactors.quadraticProbingFactor
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
