package exercisegenerator.algorithms.binary;

import java.io.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.binary.BinaryNumbers.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.binary.*;

public class ConversionFromTwosComplement implements AlgorithmImplementation {

    public static final ConversionFromTwosComplement INSTANCE = new ConversionFromTwosComplement();

    private static final String EXERCISE_TEXT_PATTERN_FROM_TWOS =
        "Geben Sie den Dezimalwert der folgenden Bin\\\"arzahlen im %d-Bit Zweierkomplement an";

    private static final String EXERCISE_TEXT_PATTERN_FROM_TWOS_SINGULAR =
        "Geben Sie den Dezimalwert der folgenden Bin\\\"arzahl im %d-Bit Zweierkomplement an";

    public static int fromTwosComplement(final BitString bitString) {
        if (bitString.getFirst().isZero()) {
            return bitString.toUnsignedInt();
        }
        return -bitString.invert().increment().toUnsignedInt();
    }

    private ConversionFromTwosComplement() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        BinaryNumbers.allBinaryTasks(
            input,
            String.format(
                ConversionFromTwosComplement.EXERCISE_TEXT_PATTERN_FROM_TWOS,
                BinaryNumbers.getBitLength(input.options)
            ),
            String.format(
                ConversionFromTwosComplement.EXERCISE_TEXT_PATTERN_FROM_TWOS_SINGULAR,
                BinaryNumbers.getBitLength(input.options)
            ),
            task -> new SolvedBinaryTask(
                String.valueOf(ConversionFromTwosComplement.fromTwosComplement(task.bitString)),
                task.bitString,
                "="
            ),
            BinaryNumbers::parseOrGenerateBitStringValueTasks,
            BinaryNumbers::toBitStringTask,
            BinaryNumbers::toValueSolution,
            BinaryNumbers::getMaximumContentLength
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
