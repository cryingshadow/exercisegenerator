package exercisegenerator.algorithms.binary;

import java.io.*;
import java.math.*;
import java.util.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.binary.BinaryNumbers.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.binary.*;

public class ConversionFromFloat implements AlgorithmImplementation {

    private static class BitStringFloatTask extends BinaryTask {
        private final BitString bitString;
        private final int exponentLength;
        private final int mantissaLength;

        private BitStringFloatTask(final BitString bitString, final int exponentLength, final int mantissaLength) {
            this.bitString = bitString;
            this.exponentLength = exponentLength;
            this.mantissaLength = mantissaLength;
        }
    }

    public static final ConversionFromFloat INSTANCE = new ConversionFromFloat();

    private static final String EXERCISE_TEXT_PATTERN_FROM_FLOAT =
        "Geben Sie zu den folgenden 1.%d.%d Gleitkommazahlen die jeweilige rationale Zahl an";

    private static final String EXERCISE_TEXT_PATTERN_FROM_FLOAT_SINGULAR =
        "Geben Sie zur folgenden 1.%d.%d Gleitkommazahl die entsprechende rationale Zahl an";

    public static String fromFloat(final BitString bitString, final int exponentLength, final int mantissaLength) {
        final String sign = bitString.getFirst().isZero() ? "" : "-";
        final int excess = BinaryNumbers.getExcess(exponentLength);
        final int exponent = bitString.subString(1, exponentLength + 1).toUnsignedInt() - excess;
        final BitString afterComma = bitString.subString(exponentLength + 1);
        if (exponent == excess + 1) {
            if (afterComma.isZero()) {
                return sign + "inf";
            }
            return "NaN";
        }
        final BitString beforeComma = new BitString();
        if (exponent > -excess) {
            beforeComma.add(Bit.ONE);
        }
        if (exponent < 0) {
            final boolean denormalized = -exponent == excess;
            for (int i = denormalized ? 1 : 0; i < -exponent; i++) {
                ConversionFromFloat.shiftOneBitRight(beforeComma, afterComma);
            }
        } else {
            for (int i = 0; i < exponent; i++) {
                ConversionFromFloat.shiftOneBitLeft(beforeComma, afterComma);
            }
        }
        final NumberTimesDecimalPower numberAfterComma = ConversionFromFloat.toNumberAfterComma(afterComma);
        return String.format(
            "%s%d,%s",
            sign,
            beforeComma.toUnsignedInt() + numberAfterComma.getBeforeComma().intValueExact(),
            numberAfterComma.getAfterComma()
        );
    }

    private static List<BitStringFloatTask> generateBitStringFloatTasks(final Parameters options) {
        final Random gen = new Random();
        final int numOfTasks = BinaryNumbers.generateNumOfTasks(options, gen);
        final int exponentLength = BinaryNumbers.getExponentLength(options);
        final int mantissaLength = BinaryNumbers.getMantissaLength(options);
        final List<BitStringFloatTask> result = new ArrayList<BitStringFloatTask>(numOfTasks);
        for (int i = 0; i < numOfTasks; i++) {
            result.add(
                new BitStringFloatTask(
                    BinaryNumbers.generateBitString(gen, exponentLength + mantissaLength + 1),
                    exponentLength,
                    mantissaLength
                )
            );
        }
        return result;
    }

    private static List<BitStringFloatTask> parseBitStringFloatTasks(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        final int exponentLength = BinaryNumbers.getExponentLength(options);
        final int mantissaLength = BinaryNumbers.getMantissaLength(options);
        return Arrays.stream(reader.readLine().split(";"))
            .map(bitstring -> new BitStringFloatTask(BitString.parse(bitstring), exponentLength, mantissaLength))
            .toList();
    }

    private static List<BitStringFloatTask> parseOrGenerateBitStringFloatTasks(
        final Parameters options
    ) throws IOException {
        return new ParserAndGenerator<List<BitStringFloatTask>>(
            ConversionFromFloat::parseBitStringFloatTasks,
            ConversionFromFloat::generateBitStringFloatTasks
        ).getResult(options);
    }

    private static void shiftOneBitLeft(final BitString beforeComma, final BitString afterComma) {
        final Bit bit = afterComma.pollFirst();
        if (bit == null) {
            beforeComma.add(Bit.ZERO);
        } else {
            beforeComma.add(bit);
        }
    }

    private static void shiftOneBitRight(final BitString beforeComma, final BitString afterComma) {
        final Bit bit = beforeComma.pollLast();
        if (bit == null) {
            afterComma.addFirst(Bit.ZERO);
        } else {
            afterComma.addFirst(bit);
        }
    }

    private static NumberTimesDecimalPower toNumberAfterComma(final BitString afterComma) {
        NumberTimesDecimalPower result = new NumberTimesDecimalPower(BigInteger.ZERO, 0);
        final BigInteger five = BigInteger.valueOf(5);
        BigInteger factor = five;
        int exponent = -1;
        for (final Bit bit : afterComma) {
            if (!bit.isZero()) {
                result = result.add(new NumberTimesDecimalPower(factor, exponent));
            }
            factor = factor.multiply(five);
            exponent--;
        }
        return result;
    }

    private ConversionFromFloat() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        BinaryNumbers.allBinaryTasks(
            input,
            String.format(
                ConversionFromFloat.EXERCISE_TEXT_PATTERN_FROM_FLOAT,
                BinaryNumbers.getExponentLength(input.options),
                BinaryNumbers.getMantissaLength(input.options)
            ),
            String.format(
                ConversionFromFloat.EXERCISE_TEXT_PATTERN_FROM_FLOAT_SINGULAR,
                BinaryNumbers.getExponentLength(input.options),
                BinaryNumbers.getMantissaLength(input.options)
            ),
            task -> new SolvedBinaryTask(
                ConversionFromFloat.fromFloat(task.bitString, task.exponentLength, task.mantissaLength),
                task.bitString
            ),
            ConversionFromFloat::parseOrGenerateBitStringFloatTasks,
            BinaryNumbers::toBitStringTask,
            BinaryNumbers::toValueSolution,
            BinaryNumbers::getMaximumContentLength
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
