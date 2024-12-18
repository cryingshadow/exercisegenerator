package exercisegenerator.algorithms.coding;

import java.io.*;
import java.math.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.binary.*;

public class HammingDecoding implements AlgorithmImplementation<BitString, BitString> {

    public static final HammingDecoding INSTANCE = new HammingDecoding();

    private static BitString generateHammingCode(final Parameters options) {
        final int length = AlgorithmImplementation.parseOrGenerateLength(7, 7, options);
        final int messageLength = HammingDecoding.hammingCodeLengthToMessageLength(length);
        final BitString message = CodingAlgorithms.generateHammingMessage(messageLength);
        final BitString result = HammingEncoding.INSTANCE.apply(message);
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

    private HammingDecoding() {}

    @Override
    public BitString apply(final BitString message) {
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

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[0];
        return result;
    }

    @Override
    public BitString parseOrGenerateProblem(final Parameters options) throws IOException {
        return new ParserAndGenerator<BitString>(
            BitString::parse,
            HammingDecoding::generateHammingCode
        ).getResult(options);
    }

    @Override
    public void printExercise(
        final BitString problem,
        final BitString solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Dekodieren Sie den folgenden \\emphasize{Hamming-Code}:\\\\[2ex]");
        Main.newLine(writer);
        writer.write(LaTeXUtils.codeseq(problem.toString()));
        Main.newLine(writer);
        Main.newLine(writer);
    }

    @Override
    public void printSolution(
        final BitString problem,
        final BitString solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write(LaTeXUtils.codeseq(solution.toString()));
        Main.newLine(writer);
        Main.newLine(writer);
    }

}
