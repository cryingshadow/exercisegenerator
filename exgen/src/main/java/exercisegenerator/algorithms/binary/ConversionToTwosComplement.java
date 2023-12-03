package exercisegenerator.algorithms.binary;

import java.io.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.binary.BinaryNumbers.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.binary.*;

public class ConversionToTwosComplement implements AlgorithmImplementation {

    public static final ConversionToTwosComplement INSTANCE = new ConversionToTwosComplement();

    private static final String EXERCISE_TEXT_PATTERN_TO_TWOS =
        "Stellen Sie die folgenden Dezimalzahlen im %d-Bit Zweierkomplement dar";

    private static final String EXERCISE_TEXT_PATTERN_TO_TWOS_SINGULAR =
        "Stellen Sie die folgende Dezimalzahl im %d-Bit Zweierkomplement dar";

    public static BitString toTwosComplement(final int number, final int length) {
        if (ConversionToTwosComplement.outOfBoundsForTwosComplement(number, length)) {
            throw new IllegalArgumentException(
                String.format("Number is out of bounds for a %d-bit two's complement number!", length)
            );
        }
        final BitString result = BinaryNumbers.toUnsignedBinary(number, length);
        if (number < 0) {
            return result.invert().increment();
        }
        return result;
    }

    private static boolean outOfBoundsForTwosComplement(final int number, final int length) {
        final double power = Math.pow(2, length - 1);
        return number > power - 1 || number < -power;
    }

    private ConversionToTwosComplement() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        BinaryNumbers.allBinaryTasks(
            input,
            String.format(
                ConversionToTwosComplement.EXERCISE_TEXT_PATTERN_TO_TWOS,
                BinaryNumbers.getBitLength(input.options)
            ),
            String.format(
                ConversionToTwosComplement.EXERCISE_TEXT_PATTERN_TO_TWOS_SINGULAR,
                BinaryNumbers.getBitLength(input.options)
            ),
            task -> new SolvedBinaryTask(
                String.valueOf(task.number),
                ConversionToTwosComplement.toTwosComplement(task.number, task.bitLength)
            ),
            BinaryNumbers::parseOrGenerateNumberComplementTasks,
            BinaryNumbers::toValueTask,
            BinaryNumbers::toBitStringSolution,
            numbers -> 1
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
