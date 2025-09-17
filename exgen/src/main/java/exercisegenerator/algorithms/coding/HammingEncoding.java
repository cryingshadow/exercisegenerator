package exercisegenerator.algorithms.coding;

import java.io.*;
import java.math.*;
import java.util.*;

import clit.*;
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
    public String commandPrefix() {
        return "ToHamming";
    }

    @Override
    public BitString generateProblem(final Parameters<Flag> options) {
        final int length = AlgorithmImplementation.parseOrGenerateLength(4, 4, options);
        return CodingAlgorithms.generateHammingMessage(length);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[0];
        return result;
    }

    @Override
    public List<BitString> parseProblems(final BufferedReader reader, final Parameters<Flag> options) throws IOException {
        return BitString.parseBitStringProblems(reader, options);
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<BitString> problems,
        final List<BitString> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Geben Sie den jeweiligen \\emphasize{Hamming-Code} f\\\"ur die folgenden bin\\\"aren ");
        writer.write("Nachrichten an.\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeSingleProblemInstance(
        final BitString problem,
        final BitString solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Geben Sie den \\emphasize{Hamming-Code} f\\\"ur die folgende bin\\\"are Nachricht an:\\\\[2ex]");
        Main.newLine(writer);
    }

    @Override
    public void printProblemInstance(
        final BitString problem,
        final BitString solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write(LaTeXUtils.codeseq(problem.toString()));
        Main.newLine(writer);
    }

    @Override
    public void printSolutionInstance(
        final BitString problem,
        final BitString solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write(LaTeXUtils.codeseq(solution.toString()));
        Main.newLine(writer);
    }

    @Override
    public void printSolutionSpace(
        final BitString problem,
        final BitString solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

}
