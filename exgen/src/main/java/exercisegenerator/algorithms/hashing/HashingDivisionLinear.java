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
        final List<Integer> values = Hashing.parseOrGenerateValues(input.options);
        final HashList[] initialHashTable = Hashing.parseOrGenerateInitialArray(values.size(), input.options);
        try {
            final HashList[] result =
                Hashing.hashingWithDivisionMethod(values, initialHashTable, Optional.of(Hashing.linearProbing()));
            Hashing.printHashingExerciseAndSolution(
                values,
                initialHashTable,
                result,
                new PrintOptions(
                    Hashing.DIVISION_METHOD
                    .concat(Hashing.LINEAR_PROBING)
                    .concat(Hashing.GENERAL_HASHING_EXERCISE_TEXT_END)
                    .concat(HashingDivisionLinear.toAdditionalHint(initialHashTable.length, initialHashTable.length)),
                    Hashing.toParameterString(initialHashTable.length),
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
