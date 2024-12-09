package exercisegenerator.algorithms.binary;

import java.io.*;
import java.util.*;

import exercisegenerator.io.*;
import exercisegenerator.structures.binary.*;

public class ConversionToOnesComplement implements BinaryNumbersAlgorithm<NumberComplementTask> {

    public static final ConversionToOnesComplement INSTANCE = new ConversionToOnesComplement();

    private static final String EXERCISE_TEXT_PATTERN_TO_ONES =
        "Stellen Sie die folgenden Dezimalzahlen im %d-Bit Einerkomplement dar";

    private static final String EXERCISE_TEXT_PATTERN_TO_ONES_SINGULAR =
        "Stellen Sie die folgende Dezimalzahl im %d-Bit Einerkomplement dar";

    public static BitString toOnesComplement(final int number, final int length) {
        if (BinaryNumbersAlgorithm.outOfBoundsForOnesComplement(number, length)) {
            throw new IllegalArgumentException(
                String.format("Number is out of bounds for a %d-bit one's complement number!", length)
            );
        }
        final BitString result = BinaryNumbersAlgorithm.toUnsignedBinary(number, length);
        if (number < 0) {
            return result.invert();
        }
        return result;
    }

    private ConversionToOnesComplement() {}

    @Override
    public SolvedBinaryTask algorithm(final NumberComplementTask task) {
        return new SolvedBinaryTask(
            ConversionToOnesComplement.toOnesComplement(task.number(), task.bitLength()),
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
    public String getExerciseText(final Parameters options) {
        return String.format(
            ConversionToOnesComplement.EXERCISE_TEXT_PATTERN_TO_ONES,
            BinaryNumbersAlgorithm.getBitLength(options)
        );
    }

    @Override
    public String getExerciseTextSingular(final Parameters options) {
        return String.format(
            ConversionToOnesComplement.EXERCISE_TEXT_PATTERN_TO_ONES_SINGULAR,
            BinaryNumbersAlgorithm.getBitLength(options)
        );
    }

    @Override
    public List<NumberComplementTask> parseOrGenerateProblem(final Parameters options) throws IOException {
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
