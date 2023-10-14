package exercisegenerator.algorithms.hashing;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.hashing.Hashing.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;

public class HashingMultiplicationQuadratic implements AlgorithmImplementation {

    public static final HashingMultiplicationQuadratic INSTANCE = new HashingMultiplicationQuadratic();

    private static String toAdditionalHint(
        final int capacity,
        final double multiplicationFactor,
        final int linearProbingFactor,
        final int quadraticProbingFactor
    ) {
        return Main.TEXT_VERSION == TextVersion.ABRAHAM ?
            String.format(
                Locale.GERMANY,
                " ($f(n,i) = \\left \\lfloor{%d \\cdot ( n \\cdot %.2f \\mod 1 )}\\right \\rfloor + %d \\cdot i + %d \\cdot i^2 $), wobei $x \\mod 1$ den Nachkommateil von $x$ bezeichnet",
                capacity,
                multiplicationFactor,
                linearProbingFactor,
                quadraticProbingFactor
            ):
                "";
    }

    private static String toParameterString(
        final int capacity,
        final double multiplicationFactor,
        final int linearProbingFactor,
        final int quadraticProbingFactor
    ) {
        return String.format(
            Locale.GERMANY,
            "m = %d, c = %.2f, $c_1$ = %d, $c_2$ = %d%s",
            capacity,
            multiplicationFactor,
            linearProbingFactor,
            quadraticProbingFactor,
            Hashing.PARAMETER_STRING_END
        );
    }

    private HashingMultiplicationQuadratic() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final List<Integer> values = Hashing.parseOrGenerateValues(input.options);
        final IntegerList[] initialHashTable = Hashing.parseOrGenerateInitialArray(values.size(), input.options);
        final double factor = Hashing.parseOrGenerateMultiplicationFactor(input.options);
        final HashResultWithProbingFactors resultWithProbingFactors =
            Hashing.parseOrGenerateProbingFactorsAndComputeResult(
                initialHashTable.length,
                (linearProbingFactor, quadraticProbingFactor) ->
                    Hashing.hashingWithMultiplicationMethod(
                        values,
                        initialHashTable,
                        factor,
                        Optional.of(Hashing.quadraticProbing(linearProbingFactor, quadraticProbingFactor))
                    ),
                input.options
            );
        Hashing.printHashingExerciseAndSolution(
            values,
            initialHashTable,
            resultWithProbingFactors.result,
            new PrintOptions(
                Hashing.toMultiplicationMethodExerciseText(factor)
                .concat(
                    Hashing.toQuadraticProbingText(
                        resultWithProbingFactors.linearProbingFactor,
                        resultWithProbingFactors.quadraticProbingFactor
                    )
                ).concat(Hashing.GENERAL_HASHING_EXERCISE_TEXT_END)
                .concat(
                    HashingMultiplicationQuadratic.toAdditionalHint(
                        initialHashTable.length,
                        factor,
                        resultWithProbingFactors.linearProbingFactor,
                        resultWithProbingFactors.quadraticProbingFactor
                    )
                ),
                HashingMultiplicationQuadratic.toParameterString(
                    initialHashTable.length,
                    factor,
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
