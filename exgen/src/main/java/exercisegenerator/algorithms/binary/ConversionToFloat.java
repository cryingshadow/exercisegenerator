package exercisegenerator.algorithms.binary;

import java.io.*;
import java.math.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.binary.*;

public class ConversionToFloat implements BinaryNumbersAlgorithm<NumberFloatTask> {

    public static final ConversionToFloat INSTANCE = new ConversionToFloat();

    private static final String EXERCISE_TEXT_PATTERN_TO_FLOAT =
        "Geben Sie zu den folgenden rationalen Zahlen die jeweilige 1.%d.%d Gleitkommazahl an";

    private static final String EXERCISE_TEXT_PATTERN_TO_FLOAT_SINGULAR =
        "Geben Sie zu der folgenden rationalen Zahl die entsprechende 1.%d.%d Gleitkommazahl an";

    public static BitString toFloat(final String number, final int exponentLength, final int mantissaLength) {
        final String[] parts = number.strip().split(",");
        if (parts.length > 2) {
            throw new IllegalArgumentException(
                String.format("%s is not a syntactically correct rational number!", number)
            );
        }
        final BitString result = new BitString();
        ConversionToFloat.addSign(parts[0], result);
        if (parts.length == 1 && parts[0].matches("-?inf")) {
            return ConversionToFloat.toInfitiny(exponentLength, mantissaLength, result);
        }
        final int numBeforeComma = Integer.parseInt(parts[0]);
        final int excess = BinaryNumbersAlgorithm.getExcess(exponentLength);
        if (Math.abs(numBeforeComma) > 0) {
            return ConversionToFloat.toFloatForNonNegativeExponent(
                parts,
                numBeforeComma,
                exponentLength,
                excess,
                mantissaLength,
                result
            );
        }
        if (parts.length == 1) {
            // number is zero
            return ConversionToFloat.fillUpWithZeros(result, exponentLength + mantissaLength);
        }
        final NumberTimesDecimalPower numAfterComma = ConversionToFloat.parseNumberTimesDecimalPower(parts[1]);
        if (numAfterComma.number.compareTo(BigInteger.ZERO) == 0) {
            // number is zero
            return ConversionToFloat.fillUpWithZeros(result, exponentLength + mantissaLength);
        }
        return ConversionToFloat.toFloatForNegativeExponent(
            numAfterComma,
            exponentLength,
            excess,
            mantissaLength,
            result
        );
    }

    private static void addSign(final String numBeforeComma, final BitString result) {
        final Bit sign = numBeforeComma.charAt(0) == '-' ? Bit.ONE : Bit.ZERO;
        result.add(sign);
    }

    private static int appendExponentAndBitsBeforeAndReturnMantissaBitsFromBefore(
        final int numBefore,
        final int exponentLength,
        final int excess,
        final BitString result
    ) {
        final BitString bitsBefore = BinaryNumbersAlgorithm.toUnsignedBinary(numBefore, 0);
        final int mantissaBitsFromBefore = bitsBefore.size() - 1;
        final BitString exponent = BinaryNumbersAlgorithm.toUnsignedBinary(mantissaBitsFromBefore + excess, exponentLength);
        result.append(exponent);
        final Iterator<Bit> iterator = bitsBefore.iterator();
        iterator.next();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return mantissaBitsFromBefore;
    }

    private static void appendMantissa(
        final NumberTimesDecimalPower numAfterComma,
        final int mantissaBitsLeft,
        final BitString result
    ) {
        Pair<Bit, NumberTimesDecimalPower> nextBitAndNumberWithLeadingZeros =
            ConversionToFloat.getNextBitAndNumberTimesDecimalPower(numAfterComma);
        for (int i = 0; i < mantissaBitsLeft; i++) {
            result.add(nextBitAndNumberWithLeadingZeros.x);
            nextBitAndNumberWithLeadingZeros =
                ConversionToFloat.getNextBitAndNumberTimesDecimalPower(nextBitAndNumberWithLeadingZeros.y);
        }
    }

    private static BitString fillUpWithOnes(final BitString result, final int bitsToFill) {
        for (int i = 0; i < bitsToFill; i++) {
            result.add(Bit.ONE);
        }
        return result;
    }

    private static BitString fillUpWithZeros(final BitString result, final int bitsToFill) {
        result.append(BinaryNumbersAlgorithm.toUnsignedBinary(0, bitsToFill));
        return result;
    }

    private static List<NumberFloatTask> generateNumberFloatTasks(final Parameters<Flag> options) {
        final int numOfTasks = BinaryNumbersAlgorithm.generateNumOfTasks(options);
        final int exponentLength = BinaryNumbersAlgorithm.getExponentLength(options);
        final int mantissaLength = BinaryNumbersAlgorithm.getMantissaLength(options);
        final List<NumberFloatTask> result = new ArrayList<NumberFloatTask>(numOfTasks);
        for (int i = 0; i < numOfTasks; i++) {
            result.add(
                new NumberFloatTask(
                    ConversionToFloat.generateRationalNumberWithinRange(exponentLength),
                    exponentLength,
                    mantissaLength
                )
            );
        }
        return result;
    }

    private static String generateRationalNumberWithinRange(final int exponentLength) {
        final int limit = (int)Math.pow(2, exponentLength - 1);
        return String.format("%d,%d", Main.RANDOM.nextInt(2 * limit - 1) - limit + 1, Main.RANDOM.nextInt(100000));
    }

    private static Pair<Bit, NumberTimesDecimalPower> getNextBitAndNumberTimesDecimalPower(
        final NumberTimesDecimalPower numberTimesDecimalPower
    ) {
        final NumberTimesDecimalPower doubled = numberTimesDecimalPower.times2();
        if (doubled.lessThanOne()) {
            return new Pair<Bit, NumberTimesDecimalPower>(Bit.ZERO, doubled);
        }
        return new Pair<Bit, NumberTimesDecimalPower>(Bit.ONE, doubled.subtractOne());
    }

    private static boolean outOfBoundsForFloat(final int numBefore, final int exponentLength) {
        final int bitLength = ((int)Math.pow(2, exponentLength - 1)) + 1;
        return BinaryNumbersAlgorithm.outOfBoundsForOnesComplement(numBefore, bitLength);
    }

    private static List<NumberFloatTask> parseNumberFloatTasks(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        final int exponentLength = BinaryNumbersAlgorithm.getExponentLength(options);
        final int mantissaLength = BinaryNumbersAlgorithm.getMantissaLength(options);
        return Arrays.stream(reader.readLine().split(";"))
            .map(n -> new NumberFloatTask(n, exponentLength, mantissaLength))
            .toList();
    }

    private static NumberTimesDecimalPower parseNumberTimesDecimalPower(final String numberAfterComma) {
        return new NumberTimesDecimalPower(
            new BigInteger(numberAfterComma),
            -numberAfterComma.length()
        );
    }

    private static BitString toFloatForNegativeExponent(
        final NumberTimesDecimalPower numAfterComma,
        final int exponentLength,
        final int excess,
        final int mantissaLength,
        final BitString result
    ) {
        Pair<Bit, NumberTimesDecimalPower> nextBitAndNumberWithLeadingZeros =
            ConversionToFloat.getNextBitAndNumberTimesDecimalPower(numAfterComma);
        int exponent = excess - 1;
        while (nextBitAndNumberWithLeadingZeros.x.isZero()) {
            exponent--;
            if (exponent < -mantissaLength + 1) {
                // round to zero
                return ConversionToFloat.fillUpWithZeros(result, exponentLength + mantissaLength);
            }
            nextBitAndNumberWithLeadingZeros =
                ConversionToFloat.getNextBitAndNumberTimesDecimalPower(nextBitAndNumberWithLeadingZeros.y);
        }
        final boolean denormalized = exponent < 1;
        if (denormalized) {
            ConversionToFloat.fillUpWithZeros(result, exponentLength - exponent);
            result.add(Bit.ONE);
        } else {
            result.append(BinaryNumbersAlgorithm.toUnsignedBinary(exponent, exponentLength));
        }
        ConversionToFloat.appendMantissa(
            nextBitAndNumberWithLeadingZeros.y,
            denormalized ? mantissaLength + exponent - 1 : mantissaLength,
            result
        );
        return result;
    }

    private static BitString toFloatForNonNegativeExponent(
        final String[] parts,
        final int numBefore,
        final int exponentLength,
        final int excess,
        final int mantissaLength,
        final BitString result
    ) {
        if (ConversionToFloat.outOfBoundsForFloat(numBefore, exponentLength)) {
            return ConversionToFloat.toInfitiny(exponentLength, mantissaLength, result);
        }
        final int mantissaBitsLeft =
            mantissaLength
            - ConversionToFloat.appendExponentAndBitsBeforeAndReturnMantissaBitsFromBefore(
                numBefore,
                exponentLength,
                excess,
                result
            );
        if (parts.length == 1) {
            return ConversionToFloat.fillUpWithZeros(result, mantissaBitsLeft);
        }
        ConversionToFloat.appendMantissa(
            ConversionToFloat.parseNumberTimesDecimalPower(parts[1]),
            mantissaBitsLeft,
            result
        );
        return result;
    }

    private static BitString toInfitiny(final int exponentLength, final int mantissaLength, final BitString result) {
        ConversionToFloat.fillUpWithOnes(result, exponentLength);
        ConversionToFloat.fillUpWithZeros(result, mantissaLength);
        return result;
    }

    private ConversionToFloat() {}

    @Override
    public SolvedBinaryTask algorithm(final NumberFloatTask task) {
        return new SolvedBinaryTask(
            ConversionToFloat.toFloat(task.number(), task.exponentLength(), task.mantissaLength()),
            "\\to",
            task.number()
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
        return String.format(
            ConversionToFloat.EXERCISE_TEXT_PATTERN_TO_FLOAT,
            BinaryNumbersAlgorithm.getExponentLength(options),
            BinaryNumbersAlgorithm.getMantissaLength(options)
        );
    }

    @Override
    public String getExerciseTextSingular(final Parameters<Flag> options) {
        return String.format(
            ConversionToFloat.EXERCISE_TEXT_PATTERN_TO_FLOAT_SINGULAR,
            BinaryNumbersAlgorithm.getExponentLength(options),
            BinaryNumbersAlgorithm.getMantissaLength(options)
        );
    }

    @Override
    public List<NumberFloatTask> parseOrGenerateProblem(final Parameters<Flag> options) throws IOException {
        return new ParserAndGenerator<List<NumberFloatTask>>(
            ConversionToFloat::parseNumberFloatTasks,
            ConversionToFloat::generateNumberFloatTasks
        ).getResult(options);
    }

    @Override
    public int toContentLength(final List<SolvedBinaryTask> solvedTasks) {
        return 1;
    }

    @Override
    public List<? extends ItemWithTikZInformation<?>> toSolution(final SolvedBinaryTask solvedTask) {
        return BinaryNumbersAlgorithm.toBitStringSolution(solvedTask);
    }

    @Override
    public String toTaskText(final SolvedBinaryTask solvedTask) {
        return BinaryNumbersAlgorithm.toValueTask(solvedTask);
    }

}
