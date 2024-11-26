package exercisegenerator.algorithms.hashing;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.hashing.Hashing.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.util.*;

public class HashingDivisionLinear implements AlgorithmImplementation {

    public static final HashingDivisionLinear INSTANCE = new HashingDivisionLinear();

    private static String toAdditionalHint(final int length, final int length2) {
        return Main.TEXT_VERSION == TextVersion.ABRAHAM ?
            String.format(" ($f(n,i) = ((n \\mod %d) + i) \\mod %d$)", length, length2) :
                "";
    }

    private HashingDivisionLinear() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final int numOfValues = Hashing.parseOrGenerateNumberOfValues(input.options);
        final IntegerList[] initialHashTable = Hashing.parseOrGenerateInitialArray(numOfValues, input.options);
        final int capacity = initialHashTable.length;
        final List<Integer> values =
            Hashing.parseOrGenerateValues(numOfValues, capacity, Optional.empty(), Optional.empty(), input.options);
        try {
            final IntegerList[] result =
                Hashing.hashingWithDivisionMethod(values, initialHashTable, Optional.of(Hashing.linearProbing()));
            Hashing.printHashingExerciseAndSolution(
                values,
                initialHashTable,
                result,
                new PrintOptions(
                    Hashing.DIVISION_METHOD
                    .concat(Hashing.LINEAR_PROBING)
                    .concat(Hashing.GENERAL_HASHING_EXERCISE_TEXT_END)
                    .concat(HashingDivisionLinear.toAdditionalHint(capacity, capacity)),
                    Hashing.toParameterString(capacity),
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
