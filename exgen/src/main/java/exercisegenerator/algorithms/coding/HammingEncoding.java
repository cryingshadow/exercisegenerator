package exercisegenerator.algorithms.coding;

import java.io.*;
import java.math.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.binary.*;

public class HammingEncoding implements AlgorithmImplementation<BitString, BitString> {

    public static final HammingEncoding INSTANCE = new HammingEncoding();

    private static int findNextPowerOfTwo(final int number) {
        int result = 4;
        while (result <= number) {
            result = result * 2;
        }
        return result;
    }

    private static BitString generateHammingMessage(final Parameters options) {
        final int length = AlgorithmImplementation.parseOrGenerateLength(4, 4, options);
        return CodingAlgorithms.generateHammingMessage(length);
    }

    private static void invertParityBits(final int index, final BitString code, final int numOfParityBits) {
        final BitString indexBits = CodingAlgorithms.toIndexBits(index, numOfParityBits);
        int bitIndex = 0;
        for (final Bit bit : indexBits) {
            if (!bit.isZero()) {
                code.invertBit(BigInteger.TWO.pow(bitIndex).intValue() - 1);
            }
            bitIndex++;
        }
    }

    private HammingEncoding() {}

    @Override
    public BitString apply(final BitString message) {
        final int messageLength = message.size();
        final int codeLength = HammingEncoding.findNextPowerOfTwo(messageLength) - 1;
        final int numOfParityBits = codeLength - messageLength;
        if (BigInteger.TWO.pow(numOfParityBits).intValue() - 1 != codeLength) {
            throw new IllegalArgumentException("Message length does not match code length!");
        }
        final BitString result = new BitString();
        int index = 1;
        final Iterator<Bit> data = message.iterator();
        while (data.hasNext()) {
            if (CodingAlgorithms.isPositiveIntegerPowerOfTwo(index)) {
                result.add(Bit.ZERO);
            } else {
                result.add(data.next());
            }
            index++;
        }
        for (int i = 3; i <= codeLength; i++) {
            if (!result.get(i - 1).isZero()) {
                HammingEncoding.invertParityBits(i, result, numOfParityBits);
            }
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
            HammingEncoding::generateHammingMessage
        ).getResult(options);
    }

    @Override
    public void printExercise(final BitString problem, final BitString solution, final Parameters options, final BufferedWriter writer)
        throws IOException {
        writer.write(
            "Geben Sie den \\emphasize{Hamming-Code} f\\\"ur die folgende bin\\\"are Nachricht an:\\\\[2ex]"
        );
        Main.newLine(writer);
        writer.write(LaTeXUtils.codeseq(problem.toString()));
        Main.newLine(writer);
        Main.newLine(writer);
    }

    @Override
    public void printSolution(final BitString problem, final BitString solution, final Parameters options, final BufferedWriter writer)
        throws IOException {
        writer.write(LaTeXUtils.codeseq(solution.toString()));
        Main.newLine(writer);
        Main.newLine(writer);
    }

}
