package exercisegenerator.algorithms.binary;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.binary.*;

public class ConversionFromASCII implements BinaryNumbersAlgorithm<String> {

    public static final ConversionFromASCII INSTANCE = new ConversionFromASCII();

    private static final String EXERCISE_TEXT_FROM_ASCII =
        "Geben Sie zu den folgenden ASCII Zeichen das jeweilige Bitmuster an";

    private static final String EXERCISE_TEXT_FROM_ASCII_SINGULAR =
        "Geben Sie zum folgenden ASCII Zeichen das entsprechende Bitmuster an";

    public static BitString fromASCII(final char character) {
        return ConversionToTwosComplement.toTwosComplement(character, 8);
    }

    private static String generateASCII() {
        return String.valueOf((char)(Main.RANDOM.nextInt(95) + 32));
    }

    private static List<String> generateASCIIBitStringTasks(final Parameters<Flag> options) {
        final int numOfTasks = BinaryNumbersAlgorithm.generateNumOfTasks(options);
        final List<String> result = new ArrayList<String>(numOfTasks);
        for (int i = 0; i < numOfTasks; i++) {
            result.add(ConversionFromASCII.generateASCII());
        }
        return result;
    }

    private static List<String> parseASCIIBitStringTasks(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        return Arrays.asList(reader.readLine().split(";"));
    }

    private ConversionFromASCII() {}

    @Override
    public SolvedBinaryTask algorithm(final String task) {
        return new SolvedBinaryTask(
            ConversionFromASCII.fromASCII(task.charAt(0)),
            "=",
            task
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
        return ConversionFromASCII.EXERCISE_TEXT_FROM_ASCII;
    }

    @Override
    public String getExerciseTextSingular(final Parameters<Flag> options) {
        return ConversionFromASCII.EXERCISE_TEXT_FROM_ASCII_SINGULAR;
    }

    @Override
    public List<String> parseOrGenerateProblem(
        final Parameters<Flag> options
    ) throws IOException {
        return new ParserAndGenerator<List<String>>(
            ConversionFromASCII::parseASCIIBitStringTasks,
            ConversionFromASCII::generateASCIIBitStringTasks
        ).getResult(options);
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
