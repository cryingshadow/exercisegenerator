package exercisegenerator.algorithms.binary;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.binary.BinaryNumbers.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.binary.*;

public class ConversionFromASCII implements AlgorithmImplementation {

    private static class ASCIIBitStringTask extends BinaryTask {
        private final String character;

        private ASCIIBitStringTask(final String character) {
            this.character = character;
        }
    }

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

    private static List<ASCIIBitStringTask> generateASCIIBitStringTasks(final Parameters options) {
        final int numOfTasks = BinaryNumbers.generateNumOfTasks(options);
        final List<ASCIIBitStringTask> result = new ArrayList<ASCIIBitStringTask>(numOfTasks);
        for (int i = 0; i < numOfTasks; i++) {
            result.add(new ASCIIBitStringTask(ConversionFromASCII.generateASCII()));
        }
        return result;
    }

    private static List<ASCIIBitStringTask> parseASCIIBitStringTasks(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        return Arrays.stream(reader.readLine().split(";"))
            .map(character -> new ASCIIBitStringTask(character))
            .toList();
    }

    private static List<ASCIIBitStringTask> parseOrGenerateASCIIBitStringTasks(
        final Parameters options
    ) throws IOException {
        return new ParserAndGenerator<List<ASCIIBitStringTask>>(
            ConversionFromASCII::parseASCIIBitStringTasks,
            ConversionFromASCII::generateASCIIBitStringTasks
        ).getResult(options);
    }

    private ConversionFromASCII() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        BinaryNumbers.allBinaryTasks(
            input,
            ConversionFromASCII.EXERCISE_TEXT_FROM_ASCII,
            ConversionFromASCII.EXERCISE_TEXT_FROM_ASCII_SINGULAR,
            task -> new SolvedBinaryTask(
                task.character,
                ConversionFromASCII.fromASCII(task.character.charAt(0)),
                "="
            ),
            ConversionFromASCII::parseOrGenerateASCIIBitStringTasks,
            BinaryNumbers::toValueTask,
            BinaryNumbers::toBitStringSolution,
            solvedTask -> 1
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
