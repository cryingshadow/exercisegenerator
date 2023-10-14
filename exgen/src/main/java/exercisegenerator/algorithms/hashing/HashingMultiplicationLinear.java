package exercisegenerator.algorithms.hashing;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.hashing.Hashing.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.util.*;

public class HashingMultiplicationLinear implements AlgorithmImplementation {

    public static final HashingMultiplicationLinear INSTANCE = new HashingMultiplicationLinear();

    private static String toAdditionalHint(final int length, final double factor, final int length2) {
        return Main.TEXT_VERSION == TextVersion.ABRAHAM ?
            String.format(
                Locale.GERMANY,
                " ($f(n,i) = \\left \\lfloor{%d \\cdot ( n \\cdot %.2f \\mod 1 )}\\right \\rfloor~ + i \\mod %d$), wobei $x \\mod 1$ den Nachkommateil von $x$ bezeichnet",
                length,
                factor,
                length2
            ) :
                "";
    }

    private HashingMultiplicationLinear() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final List<Integer> values = Hashing.parseOrGenerateValues(input.options);
        final IntegerList[] initialHashTable = Hashing.parseOrGenerateInitialArray(values.size(), input.options);
        final double factor = Hashing.parseOrGenerateMultiplicationFactor(input.options);
        try {
            final IntegerList[] result = Hashing.hashingWithMultiplicationMethod(
                values,
                initialHashTable,
                factor,
                Optional.of(Hashing.linearProbing())
            );
            Hashing.printHashingExerciseAndSolution(
                values,
                initialHashTable,
                result,
                new PrintOptions(
                    Hashing.toMultiplicationMethodExerciseText(factor)
                    .concat(Hashing.LINEAR_PROBING)
                    .concat(Hashing.GENERAL_HASHING_EXERCISE_TEXT_END)
                    .concat(HashingMultiplicationLinear.toAdditionalHint(initialHashTable.length, factor, initialHashTable.length)),
                    Hashing.toParameterString(initialHashTable.length, factor),
                    true,
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
