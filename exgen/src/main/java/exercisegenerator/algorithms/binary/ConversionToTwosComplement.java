package exercisegenerator.algorithms.binary;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.binary.*;

public class ConversionToTwosComplement implements BinaryNumbersAlgorithm<NumberComplementTask> {

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
        final BitString result = BinaryNumbersAlgorithm.toUnsignedBinary(number, length);
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
    public SolvedBinaryTask algorithm(final NumberComplementTask task) {
        return new SolvedBinaryTask(
            ConversionToTwosComplement.toTwosComplement(task.number(), task.bitLength()),
            "=",
            String.valueOf(task.number())
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

    @Override
    public String getExerciseText(final Parameters<Flag> options) {
        return String.format(
            ConversionToTwosComplement.EXERCISE_TEXT_PATTERN_TO_TWOS,
            BinaryNumbersAlgorithm.getBitLength(options)
        );
    }

    @Override
    public String getExerciseTextSingular(final Parameters<Flag> options) {
        return String.format(
            ConversionToTwosComplement.EXERCISE_TEXT_PATTERN_TO_TWOS_SINGULAR,
            BinaryNumbersAlgorithm.getBitLength(options)
        );
    }

    @Override
    public List<NumberComplementTask> parseOrGenerateProblem(final Parameters<Flag> options) throws IOException {
        return BinaryNumbersAlgorithm.parseOrGenerateNumberComplementTasks(options);
    }

    @Override
    public int toContentLength(final List<SolvedBinaryTask> solvedTasks) {
        return 1;
    }

    @Override
    public List<? extends ItemWithTikZInformation<?>> toSolution(final SolvedBinaryTask solvedTask) {
        return BinaryNumbersAlgorithm.toBitStringSolution(solvedTask);
    }

    @Override
    public String toTaskText(final SolvedBinaryTask solvedTask) {
        return BinaryNumbersAlgorithm.toValueTask(solvedTask);
    }
}
