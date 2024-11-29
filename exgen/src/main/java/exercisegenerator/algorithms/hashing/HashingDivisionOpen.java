package exercisegenerator.algorithms.hashing;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.hashing.Hashing.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.util.*;

public class HashingDivisionOpen implements AlgorithmImplementation {

    public static final HashingDivisionOpen INSTANCE = new HashingDivisionOpen();

    private static String toAdditionalHint(final int length) {
        return Main.TEXT_VERSION == TextVersion.ABRAHAM ? String.format(" ($f(n) = n \\mod %d$)", length) : "";
    }

    private HashingDivisionOpen() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final int numOfValues = Hashing.parseOrGenerateNumberOfValues(input.options);
        final IntegerList[] initialHashTable = Hashing.parseOrGenerateInitialArray(numOfValues, input.options);
        final int capacity = initialHashTable.length;
        final HashFunction hashFunction = new DivisionMethod(capacity);
        final List<Integer> values =
            Hashing.parseOrGenerateValues(numOfValues, capacity, hashFunction, Optional.empty(), input.options);
        try {
            final HashResult result = Hashing.hashing(values, initialHashTable, hashFunction, Optional.empty());
            Hashing.printHashingExerciseAndSolution(
                values,
                initialHashTable,
                result,
                new PrintOptions(
                    Hashing.DIVISION_METHOD
                    .concat(Hashing.NO_PROBING)
                    .concat(Hashing.GENERAL_HASHING_EXERCISE_TEXT_END)
                    .concat(HashingDivisionOpen.toAdditionalHint(capacity)),
                    Hashing.toParameterString(capacity),
                    false,
                    PreprintMode.parsePreprintMode(input.options)
                ),
                input.options,
                input.exerciseWriter,
                input.solutionWriter
            );
        } catch (final HashException e) {
            throw new IOException(e); //TODO is this possible at all?
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
