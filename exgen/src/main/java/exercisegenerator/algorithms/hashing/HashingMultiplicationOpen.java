package exercisegenerator.algorithms.hashing;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.hashing.Hashing.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.util.*;

public class HashingMultiplicationOpen implements AlgorithmImplementation {

    public static final HashingMultiplicationOpen INSTANCE = new HashingMultiplicationOpen();

    private static String toAdditionalHint(final int length, final double factor) {
        return Main.TEXT_VERSION == TextVersion.ABRAHAM ?
            String.format(
                Locale.GERMANY,
                " ($f(n,i) = \\left \\lfloor{%d \\cdot ( n \\cdot %.2f \\mod 1 )\\right \\rfloor $), wobei $x \\mod 1$ den Nachkommateil von $x$ bezeichnet",
                length,
                factor
            ) :
                "";
    }

    private HashingMultiplicationOpen() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final List<Integer> values = Hashing.parseOrGenerateValues(input.options);
        final HashList[] initialHashTable = Hashing.parseOrGenerateInitialArray(values.size(), input.options);
        final double factor = Hashing.parseOrGenerateMultiplicationFactor(input.options);
        try {
            final HashList[] result =
                Hashing.hashingWithMultiplicationMethod(values, initialHashTable, factor, Optional.empty());
            Hashing.printHashingExerciseAndSolution(
                values,
                initialHashTable,
                result,
                new PrintOptions(
                    Hashing.toMultiplicationMethodExerciseText(factor)
                    .concat(Hashing.NO_PROBING)
                    .concat(Hashing.GENERAL_HASHING_EXERCISE_TEXT_END)
                    .concat(HashingMultiplicationOpen.toAdditionalHint(initialHashTable.length, factor)),
                    Hashing.toParameterString(initialHashTable.length, factor),
                    false,
                    PreprintMode.parsePreprintMode(input.options)
                ),
                input.options,
                input.exerciseWriter,
                input.solutionWriter
            );
        } catch (final HashException e) {
            throw new IOException(e); //TODO
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
