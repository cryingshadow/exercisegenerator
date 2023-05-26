package exercisegenerator.algorithms.coding;

import java.io.*;
import java.math.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.binary.*;

public class HammingEncoding implements AlgorithmImplementation {

    public static final HammingEncoding INSTANCE = new HammingEncoding();

    public static BitString encodeHamming(final BitString message) {
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

    private static int findNextPowerOfTwo(final int number) {
        int result = 4;
        while (result <= number) {
            result = result * 2;
        }
        return result;
    }

    private static BitString generateHammingMessage(final Parameters options) {
        final int length = Integer.parseInt(options.getOrDefault(Flag.LENGTH, "4"));
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

    private static void printHammingEncodingExerciseAndSolution(
        final BitString message,
        final BitString code,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        exerciseWriter.write(
            "Geben Sie den \\emphasize{Hamming-Code} f\\\"ur die folgende bin\\\"are Nachricht an:\\\\[2ex]"
        );
        Main.newLine(exerciseWriter);
        exerciseWriter.write(LaTeXUtils.codeseq(message.toString()));
        Main.newLine(exerciseWriter);
        Main.newLine(exerciseWriter);
        solutionWriter.write(LaTeXUtils.codeseq(code.toString()));
        Main.newLine(solutionWriter);
        Main.newLine(solutionWriter);
    }

    private HammingEncoding() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final BitString message =
            new ParserAndGenerator<BitString>(
                BitString::parse,
                HammingEncoding::generateHammingMessage
            ).getResult(input.options);
        final BitString code = HammingEncoding.encodeHamming(message);
        HammingEncoding.printHammingEncodingExerciseAndSolution(message, code, input.exerciseWriter, input.solutionWriter);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[0];
        return result;
    }

}
