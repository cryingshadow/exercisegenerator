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

public class HashingMultiplicationOpen implements AlgorithmImplementation {

    public static final HashingMultiplicationOpen INSTANCE = new HashingMultiplicationOpen();

    private static String toAdditionalHint(final int length, final BigFraction factor) {
        return Main.TEXT_VERSION == TextVersion.ABRAHAM ?
            String.format(
                Locale.GERMANY,
                " ($f(n,i) = \\left \\lfloor{%d \\cdot ( n \\cdot %s \\mod 1 )\\right \\rfloor $), wobei $x \\mod 1$ den Nachkommateil von $x$ bezeichnet",
                length,
                AlgebraAlgorithms.toCoefficient(factor)
            ) :
                "";
    }

    private HashingMultiplicationOpen() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final int numOfValues = Hashing.parseOrGenerateNumberOfValues(input.options);
        final IntegerList[] initialHashTable = Hashing.parseOrGenerateInitialArray(numOfValues, input.options);
        final int capacity = initialHashTable.length;
        final BigFraction factor = Hashing.parseOrGenerateMultiplicationFactor(input.options);
        final List<Integer> values =
            Hashing.parseOrGenerateValues(numOfValues, capacity, Optional.of(factor), Optional.empty(), input.options);
        try {
            final IntegerList[] result =
                Hashing.hashingWithMultiplicationMethod(values, initialHashTable, factor, Optional.empty());
            Hashing.printHashingExerciseAndSolution(
                values,
                initialHashTable,
                result,
                new PrintOptions(
                    Hashing.toMultiplicationMethodExerciseText(factor)
                    .concat(Hashing.NO_PROBING)
                    .concat(Hashing.GENERAL_HASHING_EXERCISE_TEXT_END)
                    .concat(HashingMultiplicationOpen.toAdditionalHint(capacity, factor)),
                    Hashing.toParameterString(capacity, factor),
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
