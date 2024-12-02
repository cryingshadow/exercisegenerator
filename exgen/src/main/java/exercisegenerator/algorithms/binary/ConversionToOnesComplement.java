package exercisegenerator.algorithms.binary;

import java.io.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.binary.BinaryNumbers.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.binary.*;

public class ConversionToOnesComplement implements AlgorithmImplementation {

    public static final ConversionToOnesComplement INSTANCE = new ConversionToOnesComplement();

    private static final String EXERCISE_TEXT_PATTERN_TO_ONES =
        "Stellen Sie die folgenden Dezimalzahlen im %d-Bit Einerkomplement dar";

    private static final String EXERCISE_TEXT_PATTERN_TO_ONES_SINGULAR =
        "Stellen Sie die folgende Dezimalzahl im %d-Bit Einerkomplement dar";

    public static BitString toOnesComplement(final int number, final int length) {
        if (BinaryNumbers.outOfBoundsForOnesComplement(number, length)) {
            throw new IllegalArgumentException(
                String.format("Number is out of bounds for a %d-bit one's complement number!", length)
            );
        }
        final BitString result = BinaryNumbers.toUnsignedBinary(number, length);
        if (number < 0) {
            return result.invert();
        }
        return result;
    }

    private ConversionToOnesComplement() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        BinaryNumbers.allBinaryTasks(
            input,
            String.format(
                ConversionToOnesComplement.EXERCISE_TEXT_PATTERN_TO_ONES,
                BinaryNumbers.getBitLength(input.options)
            ),
            String.format(
                ConversionToOnesComplement.EXERCISE_TEXT_PATTERN_TO_ONES_SINGULAR,
                BinaryNumbers.getBitLength(input.options)
            ),
            task -> new SolvedBinaryTask(
                String.valueOf(task.number),
                ConversionToOnesComplement.toOnesComplement(task.number, task.bitLength),
                "="
            ),
            BinaryNumbers::parseOrGenerateNumberComplementTasks,
            BinaryNumbers::toValueTask,
            BinaryNumbers::toBitStringSolution,
            solvedTasks -> 1
        );
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[6];
        result[0] = "-c";
        result[1] = "4";
        result[2] = "-d";
        result[3] = "3";
        result[4] = "-l";
        result[5] = "3";
        return result; //TODO
    }

}
