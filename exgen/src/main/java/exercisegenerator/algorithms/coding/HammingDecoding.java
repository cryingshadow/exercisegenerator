package exercisegenerator.algorithms.coding;

import java.io.*;
import java.math.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.binary.*;

public class HammingDecoding implements AlgorithmImplementation {

    public static final HammingDecoding INSTANCE = new HammingDecoding();

    public static BitString decodeHamming(final BitString message) {
        final int codeLength = message.size();
        if (!CodingAlgorithms.isPositiveIntegerPowerOfTwo(codeLength + 1)) {
            throw new IllegalArgumentException("Code length must be one less than a power of two!");
        }
        final int numOfParityBits = HammingDecoding.log2(codeLength + 1);
        final BitString error = new BitString();
        for (int i = 0; i < numOfParityBits; i++) {
            error.addFirst(Bit.fromBoolean(HammingDecoding.oddOnes(message, i, numOfParityBits)));
        }
        final BitString result = new BitString(message);
        if (!error.isZero()) {
            result.invertBit(error.toUnsignedInt() - 1);
        }
        for (int i = numOfParityBits - 1; i >= 0; i--) {
            result.remove(BigInteger.TWO.pow(i).intValue() - 1);
        }
        return result;
    }

    private static BitString generateHammingCode(final Parameters options) {
        final int length = Integer.parseInt(options.getOrDefault(Flag.LENGTH, "7"));
        final int messageLength = HammingDecoding.hammingCodeLengthToMessageLength(length);
        final BitString message = CodingAlgorithms.generateHammingMessage(messageLength);
        final BitString result = HammingEncoding.encodeHamming(message);
        if (Main.RANDOM.nextBoolean()) {
            result.invertBit(Main.RANDOM.nextInt(result.size()));
        }
        return result;
    }

    private static int hammingCodeLengthToMessageLength(final int codeLength) {
        final int powerOfTwo = codeLength + 1;
        if (!CodingAlgorithms.isPositiveIntegerPowerOfTwo(powerOfTwo)) {
            throw new IllegalArgumentException("Code length must be one less than a power of two!");
        }
        return codeLength - HammingDecoding.log2(powerOfTwo);
    }

    private static int log2(final int number) {
        int current = number;
        int result = 0;
        while (current > 1) {
            current = current / 2;
            result++;
        }
        return result;
    }

    private static boolean oddOnes(final BitString message, final int parityBit, final int numOfParityBits) {
        boolean result = false;
        int index = 1;
        for (final Bit bit : message) {
            if (!bit.isZero()) {
                final BitString indexBits = CodingAlgorithms.toIndexBits(index, numOfParityBits);
                if (!indexBits.get(parityBit).isZero()) {
                    result = !result;
                }
            }
            index++;
        }
        return result;
    }

    private static void printHammingDecodingExerciseAndSolution(
        final BitString code,
        final BitString message,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        exerciseWriter.write("Dekodieren Sie den folgenden \\emphasize{Hamming-Code}:\\\\[2ex]");
        Main.newLine(exerciseWriter);
        exerciseWriter.write(LaTeXUtils.codeseq(code.toString()));
        Main.newLine(exerciseWriter);
        Main.newLine(exerciseWriter);
        solutionWriter.write(LaTeXUtils.codeseq(message.toString()));
        Main.newLine(solutionWriter);
        Main.newLine(solutionWriter);
    }

    private HammingDecoding() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final BitString code =
            new ParserAndGenerator<BitString>(
                BitString::parse,
                HammingDecoding::generateHammingCode
            ).getResult(input.options);
        final BitString message = HammingDecoding.decodeHamming(code);
        HammingDecoding.printHammingDecodingExerciseAndSolution(
            code,
            message,
            input.exerciseWriter,
            input.solutionWriter
        );
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[0];
        return result;
    }

}
