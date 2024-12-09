package exercisegenerator.algorithms.binary;

import java.io.*;
import java.util.*;

import exercisegenerator.io.*;
import exercisegenerator.structures.binary.*;

public class ConversionFromTwosComplement implements BinaryNumbersAlgorithm<BitString> {

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
    public SolvedBinaryTask algorithm(final BitString task) {
        return new SolvedBinaryTask(task, "=", String.valueOf(ConversionFromTwosComplement.fromTwosComplement(task)));
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
            ConversionFromTwosComplement.EXERCISE_TEXT_PATTERN_FROM_TWOS,
            BinaryNumbersAlgorithm.getBitLength(options)
        );
    }

    @Override
    public String getExerciseTextSingular(final Parameters options) {
        return String.format(
            ConversionFromTwosComplement.EXERCISE_TEXT_PATTERN_FROM_TWOS_SINGULAR,
            BinaryNumbersAlgorithm.getBitLength(options)
        );
    }

    @Override
    public List<BitString> parseOrGenerateProblem(final Parameters options) throws IOException {
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
