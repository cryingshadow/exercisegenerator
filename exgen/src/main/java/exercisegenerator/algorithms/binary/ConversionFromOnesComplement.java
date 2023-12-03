package exercisegenerator.algorithms.binary;

import java.io.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.binary.BinaryNumbers.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.binary.*;

public class ConversionFromOnesComplement implements AlgorithmImplementation {

    public static final ConversionFromOnesComplement INSTANCE = new ConversionFromOnesComplement();

    private static final String EXERCISE_TEXT_PATTERN_FROM_ONES =
        "Geben Sie den Dezimalwert der folgenden Bin\\\"arzahlen im %d-Bit Einerkomplement an";

    private static final String EXERCISE_TEXT_PATTERN_FROM_ONES_SINGULAR =
        "Geben Sie den Dezimalwert der folgenden Bin\\\"arzahl im %d-Bit Einerkomplement an";

    public static int fromOnesComplement(final BitString bitString) {
        if (bitString.getFirst().isZero()) {
            return bitString.toUnsignedInt();
        }
        return -bitString.invert().toUnsignedInt();
    }

    private ConversionFromOnesComplement() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        BinaryNumbers.allBinaryTasks(
            input,
            String.format(
                ConversionFromOnesComplement.EXERCISE_TEXT_PATTERN_FROM_ONES,
                BinaryNumbers.getBitLength(input.options)
            ),
            String.format(
                ConversionFromOnesComplement.EXERCISE_TEXT_PATTERN_FROM_ONES_SINGULAR,
                BinaryNumbers.getBitLength(input.options)
            ),
            task -> new SolvedBinaryTask(
                String.valueOf(ConversionFromOnesComplement.fromOnesComplement(task.bitString)),
                task.bitString
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
