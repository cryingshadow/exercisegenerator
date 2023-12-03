package exercisegenerator.algorithms.binary;

import java.io.*;
import java.math.*;
import java.util.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.binary.BinaryNumbers.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.binary.*;

public class ConversionToASCII implements AlgorithmImplementation {

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

    private static List<BitStringValueTask> generateBitStringASCIITasks(final Parameters options) {
        final Random gen = new Random();
        final int numOfTasks = BinaryNumbers.generateNumOfTasks(options, gen);
        final int bitLength = 8;
        final List<BitStringValueTask> result = new ArrayList<BitStringValueTask>(numOfTasks);
        for (int i = 0; i < numOfTasks; i++) {
            result.add(
                new BitStringValueTask(
                    BinaryNumbers.generateBitString(gen, bitLength, BigInteger.valueOf(32), BigInteger.valueOf(126))
                )
            );
        }
        return result;
    }

    private static List<BitStringValueTask> parseOrGenerateBitStringASCIITasks(
        final Parameters options
    ) throws IOException {
        return new ParserAndGenerator<List<BitStringValueTask>>(
            BinaryNumbers::parseBitStringValueTasks,
            ConversionToASCII::generateBitStringASCIITasks
        ).getResult(options);
    }

    private ConversionToASCII() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        BinaryNumbers.allBinaryTasks(
            input,
            ConversionToASCII.EXERCISE_TEXT_TO_ASCII,
            ConversionToASCII.EXERCISE_TEXT_TO_ASCII_SINGULAR,
            task -> new SolvedBinaryTask(
                String.valueOf(ConversionToASCII.toASCII(task.bitString)),
                task.bitString
            ),
            ConversionToASCII::parseOrGenerateBitStringASCIITasks,
            BinaryNumbers::toBitStringTask,
            BinaryNumbers::toValueSolution,
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
