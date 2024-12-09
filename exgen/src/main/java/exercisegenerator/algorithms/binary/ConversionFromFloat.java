package exercisegenerator.algorithms.binary;

import java.io.*;
import java.math.*;
import java.util.*;

import exercisegenerator.io.*;
import exercisegenerator.structures.binary.*;

public class ConversionFromFloat implements BinaryNumbersAlgorithm<BitStringFloatTask> {

    public static final ConversionFromFloat INSTANCE = new ConversionFromFloat();

    private static final String EXERCISE_TEXT_PATTERN_FROM_FLOAT =
        "Geben Sie zu den folgenden 1.%d.%d Gleitkommazahlen die jeweilige rationale Zahl an";

    private static final String EXERCISE_TEXT_PATTERN_FROM_FLOAT_SINGULAR =
        "Geben Sie zur folgenden 1.%d.%d Gleitkommazahl die entsprechende rationale Zahl an";

    public static String fromFloat(final BitString bitString, final int exponentLength, final int mantissaLength) {
        final String sign = bitString.getFirst().isZero() ? "" : "-";
        final int excess = BinaryNumbersAlgorithm.getExcess(exponentLength);
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
        final int numOfTasks = BinaryNumbersAlgorithm.generateNumOfTasks(options);
        final int exponentLength = BinaryNumbersAlgorithm.getExponentLength(options);
        final int mantissaLength = BinaryNumbersAlgorithm.getMantissaLength(options);
        final List<BitStringFloatTask> result = new ArrayList<BitStringFloatTask>(numOfTasks);
        for (int i = 0; i < numOfTasks; i++) {
            result.add(
                new BitStringFloatTask(
                    BinaryNumbersAlgorithm.generateBitString(exponentLength + mantissaLength + 1),
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
        final int exponentLength = BinaryNumbersAlgorithm.getExponentLength(options);
        final int mantissaLength = BinaryNumbersAlgorithm.getMantissaLength(options);
        return Arrays.stream(reader.readLine().split(";"))
            .map(bitstring -> new BitStringFloatTask(BitString.parse(bitstring), exponentLength, mantissaLength))
            .toList();
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
    public SolvedBinaryTask algorithm(final BitStringFloatTask task) {
        return new SolvedBinaryTask(
            task.bitString(),
            "=",
            ConversionFromFloat.fromFloat(task.bitString(), task.exponentLength(), task.mantissaLength())
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
    public String getExerciseText(final Parameters options) {
        return String.format(
            ConversionFromFloat.EXERCISE_TEXT_PATTERN_FROM_FLOAT,
            BinaryNumbersAlgorithm.getExponentLength(options),
            BinaryNumbersAlgorithm.getMantissaLength(options)
        );
    }

    @Override
    public String getExerciseTextSingular(final Parameters options) {
        return String.format(
            ConversionFromFloat.EXERCISE_TEXT_PATTERN_FROM_FLOAT_SINGULAR,
            BinaryNumbersAlgorithm.getExponentLength(options),
            BinaryNumbersAlgorithm.getMantissaLength(options)
        );
    }

    @Override
    public List<BitStringFloatTask> parseOrGenerateProblem(
        final Parameters options
    ) throws IOException {
        return new ParserAndGenerator<List<BitStringFloatTask>>(
            ConversionFromFloat::parseBitStringFloatTasks,
            ConversionFromFloat::generateBitStringFloatTasks
        ).getResult(options);
    }

    @Override
    public int toContentLength(final List<SolvedBinaryTask> solvedTasks) {
        return BinaryNumbersAlgorithm.getMaximumContentLength(solvedTasks);
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
