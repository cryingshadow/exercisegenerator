package exercisegenerator.algorithms.binary;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.binary.*;

public class ConversionFromOnesComplement implements BinaryNumbersAlgorithm<BitString> {

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
    public SolvedBinaryTask algorithm(final BitString task) {
        return new SolvedBinaryTask(task, "=", String.valueOf(ConversionFromOnesComplement.fromOnesComplement(task)));
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
            ConversionFromOnesComplement.EXERCISE_TEXT_PATTERN_FROM_ONES,
            BinaryNumbersAlgorithm.getBitLength(options)
        );
    }

    @Override
    public String getExerciseTextSingular(final Parameters<Flag> options) {
        return String.format(
            ConversionFromOnesComplement.EXERCISE_TEXT_PATTERN_FROM_ONES_SINGULAR,
            BinaryNumbersAlgorithm.getBitLength(options)
        );
    }

    @Override
    public List<BitString> parseOrGenerateProblem(final Parameters<Flag> options) throws IOException {
        return BinaryNumbersAlgorithm.parseOrGenerateBitStringValueTasks(options);
    }

    @Override
    public int toContentLength(final List<SolvedBinaryTask> solvedTasks) {
        return BinaryNumbersAlgorithm.getMaximumContentLength(solvedTasks);
    }

    @Override
    public List<? extends ItemWithTikZInformation<?>> toSolution(final SolvedBinaryTask solvedTask) {
        return BinaryNumbersAlgorithm.toValueSolution(solvedTask);
    }

    @Override
    public String toTaskText(final SolvedBinaryTask solvedTask) {
        return BinaryNumbersAlgorithm.toBitStringTask(solvedTask);
    }

}
