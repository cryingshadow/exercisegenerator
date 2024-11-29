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

public class HashingMultiplicationLinear implements AlgorithmImplementation {

    public static final HashingMultiplicationLinear INSTANCE = new HashingMultiplicationLinear();

    private static String toAdditionalHint(final int length, final BigFraction factor, final int length2) {
        return Main.TEXT_VERSION == TextVersion.ABRAHAM ?
            String.format(
                Locale.GERMANY,
                " ($f(n,i) = \\left \\lfloor{%d \\cdot ( n \\cdot %s \\mod 1 )}\\right \\rfloor~ + i \\mod %d$), wobei $x \\mod 1$ den Nachkommateil von $x$ bezeichnet",
                length,
                AlgebraAlgorithms.toCoefficient(factor),
                length2
            ) :
                "";
    }

    private HashingMultiplicationLinear() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final int numOfValues = Hashing.parseOrGenerateNumberOfValues(input.options);
        final IntegerList[] initialHashTable = Hashing.parseOrGenerateInitialArray(numOfValues, input.options);
        final int capacity = initialHashTable.length;
        final BigFraction factor = Hashing.parseOrGenerateMultiplicationFactor(input.options);
        final HashFunction hashFunction = new MultiplicationMethod(capacity, factor);
        final ProbingFunction probingFunction = LinearProbing.INSTANCE;
        final List<Integer> values =
            Hashing.parseOrGenerateValues(
                numOfValues,
                capacity,
                hashFunction,
                Optional.of(probingFunction),
                input.options
            );
        try {
            final HashResult result =
                Hashing.hashing(values, initialHashTable, hashFunction, Optional.of(probingFunction));
            Hashing.printHashingExerciseAndSolution(
                values,
                initialHashTable,
                result,
                new PrintOptions(
                    Hashing.toMultiplicationMethodExerciseText(factor)
                    .concat(Hashing.LINEAR_PROBING)
                    .concat(Hashing.GENERAL_HASHING_EXERCISE_TEXT_END)
                    .concat(HashingMultiplicationLinear.toAdditionalHint(capacity, factor, capacity)),
                    Hashing.toParameterString(capacity, factor),
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
