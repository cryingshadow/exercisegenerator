package exercisegenerator.algorithms;

import java.io.*;
import java.math.*;
import java.util.*;
import java.util.function.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.binary.*;
import exercisegenerator.util.*;

public class BinaryNumbers {

    private static class ASCIIBitStringTask extends BinaryTask {
        private final String character;

        private ASCIIBitStringTask(final String character) {
            this.character = character;
        }
    }

    private static abstract class BinaryTask {}

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

    private static class BitStringValueTask extends BinaryTask {
        private final BitString bitString;

        private BitStringValueTask(final BitString bitString) {
            this.bitString = bitString;
        }
    }

    private static class NumberComplementTask extends BinaryTask {
        private final int bitLength;
        private final int number;

        private NumberComplementTask(final int bitLength, final int number) {
            this.number = number;
            this.bitLength = bitLength;
        }
    }

    private static class NumberFloatTask extends BinaryTask {
        private final int exponentLength;
        private final int mantissaLength;
        private final String number;

        private NumberFloatTask(final String number, final int exponentLength, final int mantissaLength) {
            this.number = number;
            this.exponentLength = exponentLength;
            this.mantissaLength = mantissaLength;
        }
    }

    private static class SolvedBinaryTask {
        private final BitString bitString;
        private final String value;

        private SolvedBinaryTask(final String value, final BitString bitString) {
            this.value = value;
            this.bitString = bitString;
        }
    }

    public static final int DEFAULT_BINARY_CONTENT_LENGTH = 1;

    public static final String EXERCISE_TEXT_PATTERN_TO_ONES =
        "Stellen Sie die folgenden Dezimalzahlen im %d-Bit Einerkomplement dar";

    private static final String EXERCISE_TEXT_FROM_ASCII =
        "Geben Sie zu den folgenden ASCII Zeichen das jeweilige Bitmuster an";

    private static final String EXERCISE_TEXT_PATTERN_FROM_FLOAT =
        "Geben Sie zu den folgenden 1.%d.%d Gleitkommazahlen die jeweilige rationale Zahl an";

    private static final String EXERCISE_TEXT_PATTERN_FROM_ONES =
        "Geben Sie den Dezimalwert der folgenden Bin\\\"arzahlen im %d-Bit Einerkomplement an";

    private static final String EXERCISE_TEXT_PATTERN_FROM_TWOS =
        "Geben Sie den Dezimalwert der folgenden Bin\\\"arzahlen im %d-Bit Zweierkomplement an";

    private static final String EXERCISE_TEXT_PATTERN_TO_FLOAT =
        "Geben Sie zu den folgenden rationalen Zahlen die jeweilige 1.%d.%d Gleitkommazahl an";

    private static final String EXERCISE_TEXT_PATTERN_TO_TWOS =
        "Stellen Sie die folgenden Dezimalzahlen im %d-Bit Zweierkomplement dar";

    private static final String EXERCISE_TEXT_TO_ASCII =
        "Geben Sie zu den folgenden Bitmustern das jeweilige ASCII Zeichen an";

    public static void fromASCII(final AlgorithmInput input) throws IOException {
        BinaryNumbers.allBinaryTasks(
            input,
            BinaryNumbers.EXERCISE_TEXT_FROM_ASCII,
            task -> new SolvedBinaryTask(
                task.character,
                BinaryNumbers.fromASCII(task.character.charAt(0))
            ),
            BinaryNumbers::parseOrGenerateASCIIBitStringTasks,
            BinaryNumbers::toValueTask,
            BinaryNumbers::toBitStringSolution,
            solvedTask -> 1
        );
    }

    public static BitString fromASCII(final char character) {
        return BinaryNumbers.toTwosComplement(character, 8);
    }

    public static void fromFloat(final AlgorithmInput input) throws IOException {
        BinaryNumbers.allBinaryTasks(
            input,
            String.format(
                BinaryNumbers.EXERCISE_TEXT_PATTERN_FROM_FLOAT,
                BinaryNumbers.getExponentLength(input.options),
                BinaryNumbers.getMantissaLength(input.options)
            ),
            task -> new SolvedBinaryTask(
                BinaryNumbers.fromFloat(task.bitString, task.exponentLength, task.mantissaLength),
                task.bitString
            ),
            BinaryNumbers::parseOrGenerateBitStringFloatTasks,
            BinaryNumbers::toBitStringTask,
            BinaryNumbers::toValueSolution,
            BinaryNumbers::getMaximumContentLength
        );
    }

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
                BinaryNumbers.shiftOneBitRight(beforeComma, afterComma);
            }
        } else {
            for (int i = 0; i < exponent; i++) {
                BinaryNumbers.shiftOneBitLeft(beforeComma, afterComma);
            }
        }
        final NumberTimesDecimalPower numberAfterComma = BinaryNumbers.toNumberAfterComma(afterComma);
        return String.format(
            "%s%d,%s",
            sign,
            beforeComma.toUnsignedInt() + numberAfterComma.getBeforeComma().intValueExact(),
            numberAfterComma.getAfterComma()
        );
    }

    public static void fromOnesComplement(final AlgorithmInput input) throws IOException {
        BinaryNumbers.allBinaryTasks(
            input,
            String.format(BinaryNumbers.EXERCISE_TEXT_PATTERN_FROM_ONES, BinaryNumbers.getBitLength(input.options)),
            task -> new SolvedBinaryTask(
                String.valueOf(BinaryNumbers.fromOnesComplement(task.bitString)),
                task.bitString
            ),
            BinaryNumbers::parseOrGenerateBitStringValueTasks,
            BinaryNumbers::toBitStringTask,
            BinaryNumbers::toValueSolution,
            BinaryNumbers::getMaximumContentLength
        );
    }

    public static int fromOnesComplement(final BitString bitString) {
        if (bitString.getFirst().isZero()) {
            return bitString.toUnsignedInt();
        }
        return -bitString.invert().toUnsignedInt();
    }

    public static void fromTwosComplement(final AlgorithmInput input) throws IOException {
        BinaryNumbers.allBinaryTasks(
            input,
            String.format(BinaryNumbers.EXERCISE_TEXT_PATTERN_FROM_TWOS, BinaryNumbers.getBitLength(input.options)),
            task -> new SolvedBinaryTask(
                String.valueOf(BinaryNumbers.fromTwosComplement(task.bitString)),
                task.bitString
            ),
            BinaryNumbers::parseOrGenerateBitStringValueTasks,
            BinaryNumbers::toBitStringTask,
            BinaryNumbers::toValueSolution,
            BinaryNumbers::getMaximumContentLength
        );
    }

    public static int fromTwosComplement(final BitString bitString) {
        if (bitString.getFirst().isZero()) {
            return bitString.toUnsignedInt();
        }
        return -bitString.invert().increment().toUnsignedInt();
    }

    public static String[] generateTestParameters() {
        final String[] result = new String[6];
        result[0] = "-c";
        result[1] = "4";
        result[2] = "-d";
        result[3] = "3";
        result[4] = "-l";
        result[5] = "3";
        return result; //TODO
    }

    public static void toASCII(final AlgorithmInput input) throws IOException {
        BinaryNumbers.allBinaryTasks(
            input,
            BinaryNumbers.EXERCISE_TEXT_TO_ASCII,
            task -> new SolvedBinaryTask(
                String.valueOf(BinaryNumbers.toASCII(task.bitString)),
                task.bitString
            ),
            BinaryNumbers::parseOrGenerateBitStringASCIITasks,
            BinaryNumbers::toBitStringTask,
            BinaryNumbers::toValueSolution,
            solvedTask -> 1
        );
    }

    public static char toASCII(final BitString bitString) {
        if (bitString.size() != 8) {
            throw new IllegalArgumentException("Bit string must have length 8!");
        }
        return (char)BinaryNumbers.fromTwosComplement(bitString);
    }

    public static void toFloat(final AlgorithmInput input) throws IOException {
        BinaryNumbers.allBinaryTasks(
            input,
            String.format(
                BinaryNumbers.EXERCISE_TEXT_PATTERN_TO_FLOAT,
                BinaryNumbers.getExponentLength(input.options),
                BinaryNumbers.getMantissaLength(input.options)
            ),
            task -> new SolvedBinaryTask(
                task.number,
                BinaryNumbers.toFloat(task.number, task.exponentLength, task.mantissaLength)
            ),
            BinaryNumbers::parseOrGenerateNumberFloatTasks,
            BinaryNumbers::toValueTask,
            BinaryNumbers::toBitStringSolution,
            solvedTasks -> 1
        );
    }

    public static BitString toFloat(final String number, final int exponentLength, final int mantissaLength) {
        final String[] parts = number.strip().split(",");
        if (parts.length > 2) {
            throw new IllegalArgumentException(
                String.format("%s is not a syntactically correct rational number!", number)
            );
        }
        final BitString result = new BitString();
        BinaryNumbers.addSign(parts[0], result);
        if (parts.length == 1 && parts[0].matches("-?inf")) {
            return BinaryNumbers.toInfitiny(exponentLength, mantissaLength, result);
        }
        final int numBeforeComma = Integer.parseInt(parts[0]);
        final int excess = BinaryNumbers.getExcess(exponentLength);
        if (Math.abs(numBeforeComma) > 0) {
            return BinaryNumbers.toFloatForNonNegativeExponent(
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
            return BinaryNumbers.fillUpWithZeros(result, exponentLength + mantissaLength);
        }
        final NumberTimesDecimalPower numAfterComma = BinaryNumbers.parseNumberTimesDecimalPower(parts[1]);
        if (numAfterComma.number.compareTo(BigInteger.ZERO) == 0) {
            // number is zero
            return BinaryNumbers.fillUpWithZeros(result, exponentLength + mantissaLength);
        }
        return BinaryNumbers.toFloatForNegativeExponent(numAfterComma, exponentLength, excess, mantissaLength, result);
    }

    public static void toOnesComplement(final AlgorithmInput input) throws IOException {
        BinaryNumbers.allBinaryTasks(
            input,
            String.format(BinaryNumbers.EXERCISE_TEXT_PATTERN_TO_ONES, BinaryNumbers.getBitLength(input.options)),
            task -> new SolvedBinaryTask(
                String.valueOf(task.number),
                BinaryNumbers.toOnesComplement(task.number, task.bitLength)
            ),
            BinaryNumbers::parseOrGenerateNumberComplementTasks,
            BinaryNumbers::toValueTask,
            BinaryNumbers::toBitStringSolution,
            solvedTasks -> 1
        );
    }

    public static BitString toOnesComplement(final int number, final int length) {
        if (BinaryNumbers.outOfBoundsForOnesComplement(number, length)) {
            throw new IllegalArgumentException(
                String.format("Number is out of bounds for a %d-bit one's complement number!", length)
            );
        }
        final BitString result = BinaryNumbers.toUnsignedBinary(number, length);
        if (number < 0) {
            return result.invert();
        }
        return result;
    }

    public static void toTwosComplement(final AlgorithmInput input) throws IOException {
        BinaryNumbers.allBinaryTasks(
            input,
            String.format(BinaryNumbers.EXERCISE_TEXT_PATTERN_TO_TWOS, BinaryNumbers.getBitLength(input.options)),
            task -> new SolvedBinaryTask(
                String.valueOf(task.number),
                BinaryNumbers.toTwosComplement(task.number, task.bitLength)
            ),
            BinaryNumbers::parseOrGenerateNumberComplementTasks,
            BinaryNumbers::toValueTask,
            BinaryNumbers::toBitStringSolution,
            numbers -> 1
        );
    }

    public static BitString toTwosComplement(final int number, final int length) {
        if (BinaryNumbers.outOfBoundsForTwosComplement(number, length)) {
            throw new IllegalArgumentException(
                String.format("Number is out of bounds for a %d-bit two's complement number!", length)
            );
        }
        final BitString result = BinaryNumbers.toUnsignedBinary(number, length);
        if (number < 0) {
            return result.invert().increment();
        }
        return result;
    }

    public static BitString toUnsignedBinary(final int number, final int length) {
        final BitString result = new BitString();
        int nextNumber = Math.abs(number);
        while (nextNumber > 0) {
            if (nextNumber % 2 == 0) {
                result.addFirst(Bit.ZERO);
            } else {
                result.addFirst(Bit.ONE);
            }
            nextNumber = nextNumber / 2;
        }
        for (int i = length - result.size(); i > 0; i--) {
            result.addFirst(Bit.ZERO);
        }
        return result;
    }

    private static void addSign(final String numBeforeComma, final BitString result) {
        final Bit sign = numBeforeComma.charAt(0) == '-' ? Bit.ONE : Bit.ZERO;
        result.add(sign);
    }

    private static boolean algorithmUsesOnesComplement(final Parameters options) {
        switch (Algorithm.forName(options.get(Flag.ALGORITHM)).get()) {
        case TO_ONES_COMPLEMENT:
        case FROM_ONES_COMPLEMENT:
            return true;
        default:
            return false;
        }
    }

    private static <T extends BinaryTask> void allBinaryTasks(
        final AlgorithmInput input,
        final String exerciseText,
        final Function<T, SolvedBinaryTask> algorithm,
        final CheckedFunction<Parameters, List<T>, IOException> parserOrGenerator,
        final Function<SolvedBinaryTask, String> toTaskText,
        final Function<SolvedBinaryTask, List<? extends ItemWithTikZInformation<?>>> toSolution,
        final Function<List<SolvedBinaryTask>, Integer> toContentLength
    ) throws IOException {
        final List<T> tasks = parserOrGenerator.apply(input.options);
        final List<SolvedBinaryTask> solvedTasks = tasks.stream().map(algorithm).toList();
        final String longestTask =
            "-" +
            solvedTasks.stream()
            .map(toTaskText)
            .sorted((s1,s2) -> Integer.compare(s2.length(), s1.length()))
            .findFirst()
            .get();
        final int contentLength = toContentLength.apply(solvedTasks);
        BinaryNumbers.binaryBeginning(exerciseText, input.options, input.exerciseWriter, input.solutionWriter);
        boolean first = true;
        for (final SolvedBinaryTask solvedTask : solvedTasks) {
            if (first) {
                first = false;
            } else {
                LaTeXUtils.printVerticalProtectedSpace(input.exerciseWriter);
                LaTeXUtils.printVerticalProtectedSpace(input.solutionWriter);
            }
            Algorithm.assignment(
                toTaskText.apply(solvedTask),
                toSolution.apply(solvedTask),
                longestTask,
                contentLength,
                input.exerciseWriter,
                input.solutionWriter
            );
        }
        BinaryNumbers.binaryEnd(input.options, input.exerciseWriter, input.solutionWriter);
    }

    private static int appendExponentAndBitsBeforeAndReturnMantissaBitsFromBefore(
        final int numBefore,
        final int exponentLength,
        final int excess,
        final BitString result
    ) {
        final BitString bitsBefore = BinaryNumbers.toUnsignedBinary(numBefore, 0);
        final int mantissaBitsFromBefore = bitsBefore.size() - 1;
        final BitString exponent = BinaryNumbers.toUnsignedBinary(mantissaBitsFromBefore + excess, exponentLength);
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
            BinaryNumbers.getNextBitAndNumberTimesDecimalPower(numAfterComma);
        for (int i = 0; i < mantissaBitsLeft; i++) {
            result.add(nextBitAndNumberWithLeadingZeros.x);
            nextBitAndNumberWithLeadingZeros =
                BinaryNumbers.getNextBitAndNumberTimesDecimalPower(nextBitAndNumberWithLeadingZeros.y);
        }
    }

    private static void binaryBeginning(
        final String exerciseText,
        final Parameters options,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        exerciseWriter.write(exerciseText);
        exerciseWriter.write(":\\\\[2ex]");
        Main.newLine(exerciseWriter);
        LaTeXUtils.printSolutionSpaceBeginning(options, exerciseWriter);
    }

    private static void binaryEnd(
        final Parameters options,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        LaTeXUtils.printSolutionSpaceEnd(options, exerciseWriter);
        Main.newLine(solutionWriter);
    }

    private static BitString fillUpWithOnes(final BitString result, final int bitsToFill) {
        for (int i = 0; i < bitsToFill; i++) {
            result.add(Bit.ONE);
        }
        return result;
    }

    private static BitString fillUpWithZeros(final BitString result, final int bitsToFill) {
        result.append(BinaryNumbers.toUnsignedBinary(0, bitsToFill));
        return result;
    }

    private static String generateASCII(final Random gen) {
        return String.valueOf((char)(gen.nextInt(95) + 32));
    }

    private static List<ASCIIBitStringTask> generateASCIIBitStringTasks(final Parameters options) {
        final Random gen = new Random();
        final int numOfTasks = BinaryNumbers.generateNumOfTasks(options, gen);
        final List<ASCIIBitStringTask> result = new ArrayList<ASCIIBitStringTask>(numOfTasks);
        for (int i = 0; i < numOfTasks; i++) {
            result.add(new ASCIIBitStringTask(BinaryNumbers.generateASCII(gen)));
        }
        return result;
    }

    private static BitString generateBitString(final Random gen, final int bitLength) {
        return BinaryNumbers.generateBitString(gen, bitLength, BigInteger.ZERO, BigInteger.TWO.pow(bitLength));
    }

    private static BitString generateBitString(
        final Random gen,
        final int bitLength,
        final BigInteger from,
        final BigInteger to
    ) {
        final BitString result = new BitString();
        for (int i = 0; i < bitLength; i++) {
            result.add(Bit.fromBoolean(gen.nextBoolean()));
        }
        final BigInteger value = result.toNonNegativeBigInteger();
        if (value.compareTo(from) < 0 || value.compareTo(to) > 0) {
            final BigInteger range = to.subtract(from).add(BigInteger.ONE);
            final BigInteger newValue = value.mod(range).add(from);
            return BitString.create(newValue, bitLength);
        }
        return result;
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

    private static List<BitStringValueTask> generateBitStringValueTasks(final Parameters options) {
        final Random gen = new Random();
        final int numOfTasks = BinaryNumbers.generateNumOfTasks(options, gen);
        final int bitLength = BinaryNumbers.getBitLength(options);
        final List<BitStringValueTask> result = new ArrayList<BitStringValueTask>(numOfTasks);
        for (int i = 0; i < numOfTasks; i++) {
            result.add(new BitStringValueTask(BinaryNumbers.generateBitString(gen, bitLength)));
        }
        return result;
    }

    private static List<NumberComplementTask> generateNumberComplementTasks(final Parameters options) {
        final Random gen = new Random();
        final int numOfTasks = BinaryNumbers.generateNumOfTasks(options, gen);
        final int bitLength = BinaryNumbers.getBitLength(options);
        final List<NumberComplementTask> result = new ArrayList<NumberComplementTask>(numOfTasks);
        final boolean onesComplement = BinaryNumbers.algorithmUsesOnesComplement(options);
        for (int i = 0; i < numOfTasks; i++) {
            result.add(
                new NumberComplementTask(
                    bitLength,
                    BinaryNumbers.generateNumberWithinBitlength(gen, bitLength, onesComplement)
                )
            );
        }
        return result;
    }

    private static List<NumberFloatTask> generateNumberFloatTasks(final Parameters options) {
        final Random gen = new Random();
        final int numOfTasks = BinaryNumbers.generateNumOfTasks(options, gen);
        final int exponentLength = BinaryNumbers.getExponentLength(options);
        final int mantissaLength = BinaryNumbers.getMantissaLength(options);
        final List<NumberFloatTask> result = new ArrayList<NumberFloatTask>(numOfTasks);
        for (int i = 0; i < numOfTasks; i++) {
            result.add(
                new NumberFloatTask(
                    BinaryNumbers.generateRationalNumberWithinRange(gen, exponentLength),
                    exponentLength,
                    mantissaLength
                )
            );
        }
        return result;
    }

    private static int generateNumberWithinBitlength(
        final Random gen,
        final int bitLength,
        final boolean onesComplement
    ) {
        int limit = (int)Math.pow(2, bitLength);
        final int toSubtract = limit / 2;
        if (onesComplement) {
            limit--;
        }
        int number = gen.nextInt(limit) - toSubtract;
        if (onesComplement) {
            number++;
        }
        return number;
    }

    private static int generateNumOfTasks(final Parameters options, final Random gen) {
        if (options.containsKey(Flag.LENGTH)) {
            return Integer.parseInt(options.get(Flag.LENGTH));
        }
        return 3;
    }

    private static String generateRationalNumberWithinRange(final Random gen, final int exponentLength) {
        final int limit = (int)Math.pow(2, exponentLength - 1);
        return String.format("%d,%d", gen.nextInt(2 * limit - 1) - limit + 1, gen.nextInt(100000));
    }

    private static int getBitLength(final Parameters options) {
        return Integer.parseInt(options.get(Flag.CAPACITY));
    }

    private static int getExcess(final int exponentLength) {
        return ((int)Math.pow(2, exponentLength - 1)) - 1;
    }

    private static int getExponentLength(final Parameters options) {
        return Integer.parseInt(options.get(Flag.DEGREE));
    }

    private static int getMantissaLength(final Parameters options) {
        return Integer.parseInt(options.get(Flag.CAPACITY));
    }

    private static int getMaximumContentLength(final List<SolvedBinaryTask> solvedTask) {
        return solvedTask.stream().mapToInt(task -> task.value.length()).max().getAsInt();
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
        return BinaryNumbers.outOfBoundsForOnesComplement(numBefore, bitLength);
    }

    private static boolean outOfBoundsForOnesComplement(final int number, final int length) {
        return Math.abs(number) > Math.pow(2, length - 1) - 1;
    }

    private static boolean outOfBoundsForTwosComplement(final int number, final int length) {
        final double power = Math.pow(2, length - 1);
        return number > power - 1 || number < -power;
    }

    private static List<ASCIIBitStringTask> parseASCIIBitStringTasks(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        return Arrays.stream(reader.readLine().split(";"))
            .map(character -> new ASCIIBitStringTask(character))
            .toList();
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

    private static List<BitStringValueTask> parseBitStringValueTasks(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        return Arrays.stream(reader.readLine().split(";"))
            .map(bitstring -> new BitStringValueTask(BitString.parse(bitstring)))
            .toList();
    }

    private static List<NumberComplementTask> parseNumberComplementTasks(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        final int bitLength = BinaryNumbers.getBitLength(options);
        return Arrays.stream(reader.readLine().split(";"))
            .map(n -> new NumberComplementTask(bitLength, Integer.parseInt(n)))
            .toList();
    }

    private static List<NumberFloatTask> parseNumberFloatTasks(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        final int exponentLength = BinaryNumbers.getExponentLength(options);
        final int mantissaLength = BinaryNumbers.getMantissaLength(options);
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

    private static List<ASCIIBitStringTask> parseOrGenerateASCIIBitStringTasks(
        final Parameters options
    ) throws IOException {
        return new ParserAndGenerator<List<ASCIIBitStringTask>>(
            BinaryNumbers::parseASCIIBitStringTasks,
            BinaryNumbers::generateASCIIBitStringTasks
        ).getResult(options);
    }

    private static List<BitStringValueTask> parseOrGenerateBitStringASCIITasks(
        final Parameters options
    ) throws IOException {
        return new ParserAndGenerator<List<BitStringValueTask>>(
            BinaryNumbers::parseBitStringValueTasks,
            BinaryNumbers::generateBitStringASCIITasks
        ).getResult(options);
    }

    private static List<BitStringFloatTask> parseOrGenerateBitStringFloatTasks(
        final Parameters options
    ) throws IOException {
        return new ParserAndGenerator<List<BitStringFloatTask>>(
            BinaryNumbers::parseBitStringFloatTasks,
            BinaryNumbers::generateBitStringFloatTasks
        ).getResult(options);
    }

    private static List<BitStringValueTask> parseOrGenerateBitStringValueTasks(
        final Parameters options
    ) throws IOException {
        return new ParserAndGenerator<List<BitStringValueTask>>(
            BinaryNumbers::parseBitStringValueTasks,
            BinaryNumbers::generateBitStringValueTasks
        ).getResult(options);
    }

    private static List<NumberComplementTask> parseOrGenerateNumberComplementTasks(final Parameters options)
    throws IOException {
        return new ParserAndGenerator<List<NumberComplementTask>>(
            BinaryNumbers::parseNumberComplementTasks,
            BinaryNumbers::generateNumberComplementTasks
        ).getResult(options);
    }

    private static List<NumberFloatTask> parseOrGenerateNumberFloatTasks(final Parameters options)
    throws IOException {
        return new ParserAndGenerator<List<NumberFloatTask>>(
            BinaryNumbers::parseNumberFloatTasks,
            BinaryNumbers::generateNumberFloatTasks
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

    private static List<ItemWithTikZInformation<Bit>> toBitStringSolution(final SolvedBinaryTask solvedTask) {
        return solvedTask.bitString.stream().map(b -> new ItemWithTikZInformation<Bit>(Optional.of(b))).toList();
    }

    private static String toBitStringTask(final SolvedBinaryTask solvedTask) {
        return LaTeXUtils.code(solvedTask.bitString.toString());
    }

    private static BitString toFloatForNegativeExponent(
        final NumberTimesDecimalPower numAfterComma,
        final int exponentLength,
        final int excess,
        final int mantissaLength,
        final BitString result
    ) {
        Pair<Bit, NumberTimesDecimalPower> nextBitAndNumberWithLeadingZeros =
            BinaryNumbers.getNextBitAndNumberTimesDecimalPower(numAfterComma);
        int exponent = excess - 1;
        while (nextBitAndNumberWithLeadingZeros.x.isZero()) {
            exponent--;
            if (exponent < -mantissaLength + 1) {
                // round to zero
                return BinaryNumbers.fillUpWithZeros(result, exponentLength + mantissaLength);
            }
            nextBitAndNumberWithLeadingZeros =
                BinaryNumbers.getNextBitAndNumberTimesDecimalPower(nextBitAndNumberWithLeadingZeros.y);
        }
        final boolean denormalized = exponent < 1;
        if (denormalized) {
            BinaryNumbers.fillUpWithZeros(result, exponentLength - exponent);
            result.add(Bit.ONE);
        } else {
            result.append(BinaryNumbers.toUnsignedBinary(exponent, exponentLength));
        }
        BinaryNumbers.appendMantissa(
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
        if (BinaryNumbers.outOfBoundsForFloat(numBefore, exponentLength)) {
            return BinaryNumbers.toInfitiny(exponentLength, mantissaLength, result);
        }
        final int mantissaBitsLeft =
            mantissaLength
            - BinaryNumbers.appendExponentAndBitsBeforeAndReturnMantissaBitsFromBefore(
                numBefore,
                exponentLength,
                excess,
                result
            );
        if (parts.length == 1) {
            return BinaryNumbers.fillUpWithZeros(result, mantissaBitsLeft);
        }
        BinaryNumbers.appendMantissa(
            BinaryNumbers.parseNumberTimesDecimalPower(parts[1]),
            mantissaBitsLeft,
            result
        );
        return result;
    }

    private static BitString toInfitiny(final int exponentLength, final int mantissaLength, final BitString result) {
        BinaryNumbers.fillUpWithOnes(result, exponentLength);
        BinaryNumbers.fillUpWithZeros(result, mantissaLength);
        return result;
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

    private static List<ItemWithTikZInformation<String>> toValueSolution(final SolvedBinaryTask solvedTask) {
        return Collections.singletonList(
            new ItemWithTikZInformation<>(Optional.of(LaTeXUtils.escapeForLaTeX(solvedTask.value)))
        );
    }

    private static String toValueTask(final SolvedBinaryTask solvedTask) {
        return LaTeXUtils.escapeForLaTeX(solvedTask.value);
    }

}
