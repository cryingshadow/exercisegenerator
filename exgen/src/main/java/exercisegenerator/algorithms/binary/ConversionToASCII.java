package exercisegenerator.algorithms.binary;

import java.io.*;
import java.math.*;
import java.util.*;

import clit.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.binary.*;

public class ConversionToASCII implements BinaryNumbersAlgorithm<BitString> {

    public static final ConversionToASCII INSTANCE = new ConversionToASCII();

    private static final String EXERCISE_TEXT_TO_ASCII =
        "Geben Sie zu den folgenden Bitmustern das jeweilige ASCII Zeichen an";

    private static final String EXERCISE_TEXT_TO_ASCII_SINGULAR =
        "Geben Sie zum folgenden Bitmuster das entsprechende ASCII Zeichen an";

    public static char toASCII(final BitString bitString) {
        if (bitString.size() != 8) {
            throw new IllegalArgumentException("Bit string must have length 8!");
        }
        return (char)ConversionFromTwosComplement.fromTwosComplement(bitString);
    }

    private static List<BitString> generateBitStringASCIITasks(final Parameters<Flag> options) {
        final int numOfTasks = BinaryNumbersAlgorithm.generateNumOfTasks(options);
        final int bitLength = 8;
        final List<BitString> result = new ArrayList<BitString>(numOfTasks);
        for (int i = 0; i < numOfTasks; i++) {
            result.add(
                BinaryNumbersAlgorithm.generateBitString(bitLength, BigInteger.valueOf(32), BigInteger.valueOf(126))
            );
        }
        return result;
    }

    private ConversionToASCII() {}

    @Override
    public SolvedBinaryTask algorithm(final BitString task) {
        return new SolvedBinaryTask(
            task,
            "=",
            String.valueOf(ConversionToASCII.toASCII(task))
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
        return ConversionToASCII.EXERCISE_TEXT_TO_ASCII;
    }

    @Override
    public String getExerciseTextSingular(final Parameters<Flag> options) {
        return ConversionToASCII.EXERCISE_TEXT_TO_ASCII_SINGULAR;
    }

    @Override
    public List<BitString> parseOrGenerateProblem(final Parameters<Flag> options) throws IOException {
        return new ParserAndGenerator<List<BitString>>(
            BinaryNumbersAlgorithm::parseBitStringValueTasks,
            ConversionToASCII::generateBitStringASCIITasks
        ).getResult(options);
    }

    @Override
    public int toContentLength(final List<SolvedBinaryTask> solvedTasks) {
        return 1;
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
