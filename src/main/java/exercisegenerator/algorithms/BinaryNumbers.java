package exercisegenerator.algorithms;

import java.io.*;
import java.util.*;
import java.util.function.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.util.*;

public class BinaryNumbers {

    private static abstract class BinaryTask {}

    private static class BitStringComplementTask extends BinaryTask {
        private final BitString bitString;

        private BitStringComplementTask(final BitString bitString) {
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
        private final int mantisseLength;
        private final String number;

        private NumberFloatTask(final String number, final int exponentLength, final int mantisseLength) {
            this.number = number;
            this.exponentLength = exponentLength;
            this.mantisseLength = mantisseLength;
        }
    }

    private static class NumberWithLeadingZeros {
        private final int leadingZeros;
        private final int number;

        private NumberWithLeadingZeros(final int number, final int leadingZeros) {
            this.number = number;
            this.leadingZeros = leadingZeros;
        }
    }

    private static class SolvedBinaryTask {
        private final BitString bitString;
        private final String number;

        private SolvedBinaryTask(final String number, final BitString bitString) {
            this.number = number;
            this.bitString = bitString;
        }
    }

    public static final int DEFAULT_BINARY_CONTENT_LENGTH = 1;

    public static final String EXERCISE_TEXT_PATTERN_TO_ONES =
        "Stellen Sie die folgenden Dezimalzahlen im %d-bit Einerkomplement dar:\\\\[2ex]";

    private static final String EXERCISE_TEXT_PATTERN_FROM_ONES =
        "Geben Sie den Dezimalwert der folgenden Binärzahlen im %d-bit Einerkomplement an:\\\\[2ex]";

    private static final String EXERCISE_TEXT_PATTERN_FROM_TWOS =
        "Geben Sie den Dezimalwert der folgenden Binärzahlen im %d-bit Zweierkomplement an:\\\\[2ex]";

    private static final String EXERCISE_TEXT_PATTERN_TO_FLOAT =
        "Geben Sie zu den folgenden rationalen Zahlen die jeweilige 1.%d.%d Gleitkommazahl nach IEEE 754 an:\\\\[2ex]";

    private static final String EXERCISE_TEXT_PATTERN_TO_TWOS =
        "Stellen Sie die folgenden Dezimalzahlen im %d-bit Zweierkomplement dar:\\\\[2ex]";

    public static void fromOnesComplement(final AlgorithmInput input) throws IOException {
        BinaryNumbers.allBinaryTasks(
            input,
            String.format(BinaryNumbers.EXERCISE_TEXT_PATTERN_FROM_ONES, BinaryNumbers.getBitLength(input.options)),
            task -> new SolvedBinaryTask(
                String.valueOf(BinaryNumbers.fromOnesComplement(task.bitString)),
                task.bitString
            ),
            BinaryNumbers::parseOrGenerateBitStringComplementTasks,
            BinaryNumbers::toBitStringTask,
            BinaryNumbers::toNumberSolution,
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
            BinaryNumbers::parseOrGenerateBitStringComplementTasks,
            BinaryNumbers::toBitStringTask,
            BinaryNumbers::toNumberSolution,
            BinaryNumbers::getMaximumContentLength
        );
    }

    public static int fromTwosComplement(final BitString bitString) {
        if (bitString.getFirst().isZero()) {
            return bitString.toUnsignedInt();
        }
        return -bitString.invert().increment().toUnsignedInt();
    }

    public static void toFloat(final AlgorithmInput input) throws IOException {
        BinaryNumbers.allBinaryTasks(
            input,
            String.format(
                BinaryNumbers.EXERCISE_TEXT_PATTERN_TO_FLOAT,
                BinaryNumbers.getExponentLength(input.options),
                BinaryNumbers.getMantisseLength(input.options)
            ),
            task -> new SolvedBinaryTask(
                task.number,
                BinaryNumbers.toFloat(task.number, task.exponentLength, task.mantisseLength)
            ),
            BinaryNumbers::parseOrGenerateNumberFloatTasks,
            BinaryNumbers::toNumberTask,
            BinaryNumbers::toBitStringSolution,
            solvedTasks -> 1
        );
    }

    public static BitString toFloat(final String number, final int exponentLength, final int mantisseLength) {
        final String[] parts = number.split(",");
        final int numBeforeComma = Integer.parseInt(parts[0]);
        if (BinaryNumbers.outOfBoundsForFloat(numBeforeComma, exponentLength)) {
            throw new IllegalArgumentException(
                String.format("Number is out of bounds for a %d-bit exponent!", exponentLength)
            );
        }
        final BitString result = new BitString();
        BinaryNumbers.addSign(parts[0], result);
        final int excess = ((int)Math.pow(2, exponentLength - 1)) - 1;
        if (Math.abs(numBeforeComma) > 0) {
            return BinaryNumbers.toFloatForNonNegativeExponent(
                parts,
                numBeforeComma,
                exponentLength,
                excess,
                mantisseLength,
                result
            );
        }
        if (parts.length == 1) {
            return BinaryNumbers.fillUpWithZeros(result, exponentLength + mantisseLength);
        }
        final NumberWithLeadingZeros numAfterComma = BinaryNumbers.parseNumberWithLeadingZeros(parts[1]);
        if (numAfterComma.number == 0) {
            return BinaryNumbers.fillUpWithZeros(result, exponentLength + mantisseLength);
        }
        return BinaryNumbers.toFloatForNegativeExponent(numAfterComma, exponentLength, excess, mantisseLength, result);
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
            BinaryNumbers::toNumberTask,
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
            BinaryNumbers::toNumberTask,
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

    private static boolean algorithmUsesOnesComplement(final Map<Flag, String> options) {
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
        final CheckedFunction<Map<Flag, String>, List<T>, IOException> parserOrGenerator,
        final Function<SolvedBinaryTask, String> toTaskText,
        final Function<SolvedBinaryTask, List<? extends ItemWithTikZInformation<?>>> toSolution,
        final Function<List<SolvedBinaryTask>, Integer> toContentLength
    ) throws IOException {
        final List<T> tasks = parserOrGenerator.apply(input.options);
        final List<SolvedBinaryTask> solvedTasks = tasks.stream().map(algorithm).toList();
        final int contentLength = toContentLength.apply(solvedTasks);
        BinaryNumbers.binaryBeginning(exerciseText, input.exerciseWriter, input.solutionWriter);
        boolean first = true;
        for (final SolvedBinaryTask solvedTask : solvedTasks) {
            if (first) {
                first = false;
            } else {
                TikZUtils.printVerticalProtectedSpace(input.exerciseWriter);
                TikZUtils.printVerticalProtectedSpace(input.solutionWriter);
            }
            BinaryNumbers.binaryTask(
                toTaskText.apply(solvedTask),
                toSolution.apply(solvedTask),
                contentLength,
                input.exerciseWriter,
                input.solutionWriter
            );
        }
        BinaryNumbers.binaryEnd(input.exerciseWriter, input.solutionWriter);
    }

    private static int appendExponentAndBitsBeforeAndReturnMantisseBitsFromBefore(
        final int numBefore,
        final int exponentLength,
        final int excess,
        final BitString result
    ) {
        final BitString bitsBefore = BinaryNumbers.toUnsignedBinary(numBefore, 0);
        final int mantisseBitsFromBefore = bitsBefore.size() - 1;
        final BitString exponent = BinaryNumbers.toUnsignedBinary(mantisseBitsFromBefore + excess, exponentLength);
        result.append(exponent);
        final Iterator<Bit> iterator = bitsBefore.iterator();
        iterator.next();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return mantisseBitsFromBefore;
    }

    private static void appendMantisse(
        final NumberWithLeadingZeros numAfterComma,
        final int mantisseBitsLeft,
        final BitString result
    ) {
        Pair<Bit, NumberWithLeadingZeros> nextBitAndNumberWithLeadingZeros =
            BinaryNumbers.getNextBitAndNumberWithLeadingZeros(numAfterComma);
        for (int i = 0; i < mantisseBitsLeft; i++) {
            result.add(nextBitAndNumberWithLeadingZeros.x);
            nextBitAndNumberWithLeadingZeros =
                BinaryNumbers.getNextBitAndNumberWithLeadingZeros(nextBitAndNumberWithLeadingZeros.y);
        }
    }

    private static void binaryBeginning(
        final String exerciseText,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        exerciseWriter.write(exerciseText);
        Main.newLine(exerciseWriter);
        TikZUtils.printToggleForSolutions(exerciseWriter);
        TikZUtils.printElse(exerciseWriter);
    }

    private static void binaryEnd(final BufferedWriter exerciseWriter, final BufferedWriter solutionWriter)
    throws IOException {
        TikZUtils.printEndIf(exerciseWriter);
        Main.newLine(exerciseWriter);
        Main.newLine(solutionWriter);
    }

    private static void binaryTask(
        final String task,
        final List<? extends ItemWithTikZInformation<?>> solution,
        final int contentLength,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        BinaryNumbers.binaryTaskExercise(task, solution.size(), contentLength, exerciseWriter);
        BinaryNumbers.binaryTaskSolution(task, solution, contentLength, solutionWriter);
    }

    private static void binaryTaskBeginning(final String task, final BufferedWriter writer) throws IOException {
        final String taskText = String.format("$%s = {}$", task);
        TikZUtils.printMinipageBeginning(TikZUtils.widthOf(taskText), writer);
        writer.write(taskText);
        Main.newLine(writer);
        TikZUtils.printMinipageEnd(writer);
        TikZUtils.printMinipageBeginning(TikZUtils.widthOfComplement(taskText), writer);
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
    }

    private static void binaryTaskEnd(final BufferedWriter writer) throws IOException {
        TikZUtils.printTikzEnd(writer);
        TikZUtils.printMinipageEnd(writer);
    }

    private static void binaryTaskExercise(
        final String task,
        final int solutionLength,
        final int contentLength,
        final BufferedWriter writer
    ) throws IOException {
        BinaryNumbers.binaryTaskBeginning(task, writer);
        TikZUtils.printEmptyArrayAndReturnLeftmostNodesName(solutionLength, Optional.empty(), contentLength, writer);
        BinaryNumbers.binaryTaskEnd(writer);
    }

    private static void binaryTaskSolution(
        final String task,
        final List<? extends ItemWithTikZInformation<?>> solution,
        final int contentLength,
        final BufferedWriter writer
    ) throws IOException {
        BinaryNumbers.binaryTaskBeginning(task, writer);
        TikZUtils.printListAndReturnLeftmostNodesName(solution, Optional.empty(), contentLength, writer);
        BinaryNumbers.binaryTaskEnd(writer);
    }

    private static int decimalLength(final int positiveNumber) {
        return String.valueOf(positiveNumber).length();
    }

    private static BitString fillUpWithZeros(final BitString result, final int bitsLeft) {
        result.append(BinaryNumbers.toUnsignedBinary(0, bitsLeft));
        return result;
    }

    private static BitString generateBitString(final Random gen, final int bitLength) {
        final BitString result = new BitString();
        for (int i = 0; i < bitLength; i++) {
            result.add(Bit.fromBoolean(gen.nextBoolean()));
        }
        return result;
    }

    private static List<BitStringComplementTask> generateBitStringComplementTasks(final Map<Flag, String> options) {
        final Random gen = new Random();
        final int numOfTasks = BinaryNumbers.generateNumOfTasks(options, gen);
        final int bitLength = BinaryNumbers.getBitLength(options);
        final List<BitStringComplementTask> result = new ArrayList<BitStringComplementTask>(numOfTasks);
        for (int i = 0; i < numOfTasks; i++) {
            result.add(new BitStringComplementTask(BinaryNumbers.generateBitString(gen, bitLength)));
        }
        return result;
    }

    private static List<NumberComplementTask> generateNumberComplementTasks(final Map<Flag, String> options) {
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

    private static List<NumberFloatTask> generateNumberFloatTasks(final Map<Flag, String> options) {
        final Random gen = new Random();
        final int numOfTasks = BinaryNumbers.generateNumOfTasks(options, gen);
        final int exponentLength = BinaryNumbers.getExponentLength(options);
        final int mantisseLength = BinaryNumbers.getMantisseLength(options);
        final List<NumberFloatTask> result = new ArrayList<NumberFloatTask>(numOfTasks);
        for (int i = 0; i < numOfTasks; i++) {
            result.add(
                new NumberFloatTask(
                    BinaryNumbers.generateRationalNumberWithinRange(gen, exponentLength),
                    exponentLength,
                    mantisseLength
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

    private static int generateNumOfTasks(final Map<Flag, String> options, final Random gen) {
        if (options.containsKey(Flag.LENGTH)) {
            return Integer.parseInt(options.get(Flag.LENGTH));
        }
        return 3;
    }

    private static String generateRationalNumberWithinRange(final Random gen, final int exponentLength) {
        final int limit = (int)Math.pow(2, exponentLength - 1);
        return String.format("%d,%d", gen.nextInt(2 * limit - 1) - limit + 1, gen.nextInt(100000));
    }

    private static int getBitLength(final Map<Flag, String> options) {
        return Integer.parseInt(options.get(Flag.CAPACITY));
    }

    private static int getExponentLength(final Map<Flag, String> options) {
        return Integer.parseInt(options.get(Flag.DEGREE));
    }

    private static int getIndexOfFirstNonZeroDigit(final String numberAfterComma) {
        final char[] charArray = numberAfterComma.toCharArray();
        int result = 0;
        while (result < charArray.length && charArray[result] == '0') {
            result++;
        }
        return result;
    }

    private static int getMantisseLength(final Map<Flag, String> options) {
        return Integer.parseInt(options.get(Flag.CAPACITY));
    }

    private static int getMaximumContentLength(final List<SolvedBinaryTask> solvedTask) {
        return solvedTask.stream().mapToInt(task -> task.number.length()).max().getAsInt();
    }

    private static Pair<Bit, NumberWithLeadingZeros> getNextBitAndNumberWithLeadingZeros(
        final NumberWithLeadingZeros numberWithLeadingZeros
    ) {
        final int doubledNumber = numberWithLeadingZeros.number * 2;
        final boolean overflow =
            BinaryNumbers.decimalLength(doubledNumber) > BinaryNumbers.decimalLength(numberWithLeadingZeros.number);
        if (numberWithLeadingZeros.leadingZeros > 0) {
            final int nextLeadingZeros =
                overflow ? numberWithLeadingZeros.leadingZeros - 1 : numberWithLeadingZeros.leadingZeros;
            return new Pair<Bit, NumberWithLeadingZeros>(
                Bit.ZERO,
                new NumberWithLeadingZeros(doubledNumber, nextLeadingZeros)
            );
        }
        if (!overflow) {
            return new Pair<Bit, NumberWithLeadingZeros>(Bit.ZERO, new NumberWithLeadingZeros(doubledNumber, 0));
        }
        final int decimalLengthOfNumber = BinaryNumbers.decimalLength(numberWithLeadingZeros.number);
        final int nextNumber = doubledNumber - (int)Math.pow(10, decimalLengthOfNumber);
        return new Pair<Bit, NumberWithLeadingZeros>(
            Bit.ONE,
            new NumberWithLeadingZeros(nextNumber, decimalLengthOfNumber - BinaryNumbers.decimalLength(nextNumber))
        );
    }

    private static boolean outOfBoundsForFloat(final int numBefore, final int exponentLength) {
        final int bitLength = ((int)Math.pow(2, exponentLength - 1)) + 2;
        return BinaryNumbers.outOfBoundsForOnesComplement(numBefore, bitLength);
    }

    private static boolean outOfBoundsForOnesComplement(final int number, final int length) {
        return Math.abs(number) > Math.pow(2, length - 1) - 1;
    }

    private static boolean outOfBoundsForTwosComplement(final int number, final int length) {
        final double power = Math.pow(2, length - 1);
        return number > power - 1 || number < -power;
    }

    private static List<BitStringComplementTask> parseBitStringComplementTasks(
        final BufferedReader reader,
        final Map<Flag, String> options
    ) throws IOException {
        return Arrays.stream(reader.readLine().split(";"))
            .map(bitstring -> new BitStringComplementTask(BitString.parse(bitstring)))
            .toList();
    }

    private static List<NumberComplementTask> parseNumberComplementTasks(
        final BufferedReader reader,
        final Map<Flag, String> options
    ) throws IOException {
        final int bitLength = BinaryNumbers.getBitLength(options);
        return Arrays.stream(reader.readLine().split(";"))
            .map(n -> new NumberComplementTask(bitLength, Integer.parseInt(n)))
            .toList();
    }

    private static List<NumberFloatTask> parseNumberFloatTasks(
        final BufferedReader reader,
        final Map<Flag, String> options
    ) throws IOException {
        final int exponentLength = BinaryNumbers.getExponentLength(options);
        final int mantisseLength = BinaryNumbers.getMantisseLength(options);
        return Arrays.stream(reader.readLine().split(";"))
            .map(n -> new NumberFloatTask(n, exponentLength, mantisseLength))
            .toList();
    }

    private static NumberWithLeadingZeros parseNumberWithLeadingZeros(final String numberAfterComma) {
        return new NumberWithLeadingZeros(
            Integer.parseInt(numberAfterComma),
            BinaryNumbers.getIndexOfFirstNonZeroDigit(numberAfterComma)
        );
    }

    private static List<BitStringComplementTask> parseOrGenerateBitStringComplementTasks(
        final Map<Flag, String> options
    ) throws IOException {
        return new ParserAndGenerator<List<BitStringComplementTask>>(
            BinaryNumbers::parseBitStringComplementTasks,
            BinaryNumbers::generateBitStringComplementTasks
        ).getResult(options);
    }

    private static List<NumberComplementTask> parseOrGenerateNumberComplementTasks(final Map<Flag, String> options)
    throws IOException {
        return new ParserAndGenerator<List<NumberComplementTask>>(
            BinaryNumbers::parseNumberComplementTasks,
            BinaryNumbers::generateNumberComplementTasks
        ).getResult(options);
    }

    private static List<NumberFloatTask> parseOrGenerateNumberFloatTasks(final Map<Flag, String> options)
    throws IOException {
        return new ParserAndGenerator<List<NumberFloatTask>>(
            BinaryNumbers::parseNumberFloatTasks,
            BinaryNumbers::generateNumberFloatTasks
        ).getResult(options);
    }

    private static List<ItemWithTikZInformation<Bit>> toBitStringSolution(final SolvedBinaryTask solvedTask) {
        return solvedTask.bitString.stream().map(b -> new ItemWithTikZInformation<Bit>(Optional.of(b))).toList();
    }

    private static String toBitStringTask(final SolvedBinaryTask solvedTask) {
        return TikZUtils.code(solvedTask.bitString.toString());
    }

    private static BitString toFloatForNegativeExponent(
        final NumberWithLeadingZeros numAfterComma,
        final int exponentLength,
        final int excess,
        final int mantisseLength,
        final BitString result
    ) {
        Pair<Bit, NumberWithLeadingZeros> nextBitAndNumberWithLeadingZeros =
            BinaryNumbers.getNextBitAndNumberWithLeadingZeros(numAfterComma);
        int exponent = excess - 1;
        while (nextBitAndNumberWithLeadingZeros.x.isZero()) {
            exponent--;
            if (exponent < 0) {
                return BinaryNumbers.fillUpWithZeros(result, exponentLength + mantisseLength);
            }
            nextBitAndNumberWithLeadingZeros =
                BinaryNumbers.getNextBitAndNumberWithLeadingZeros(nextBitAndNumberWithLeadingZeros.y);
        }
        result.append(BinaryNumbers.toUnsignedBinary(exponent, exponentLength));
        BinaryNumbers.appendMantisse(
            nextBitAndNumberWithLeadingZeros.y,
            mantisseLength,
            result
        );
        return result;
    }

    private static BitString toFloatForNonNegativeExponent(
        final String[] parts,
        final int numBefore,
        final int exponentLength,
        final int excess,
        final int mantisseLength,
        final BitString result
    ) {
        final int mantisseBitsLeft =
            mantisseLength
            - BinaryNumbers.appendExponentAndBitsBeforeAndReturnMantisseBitsFromBefore(
                numBefore,
                exponentLength,
                excess,
                result
            );
        if (parts.length == 1) {
            return BinaryNumbers.fillUpWithZeros(result, mantisseBitsLeft);
        }
        BinaryNumbers.appendMantisse(
            BinaryNumbers.parseNumberWithLeadingZeros(parts[1]),
            mantisseBitsLeft,
            result
        );
        return result;
    }

    private static List<ItemWithTikZInformation<String>> toNumberSolution(final SolvedBinaryTask solvedTask) {
        return Collections.singletonList(new ItemWithTikZInformation<>(Optional.of(solvedTask.number)));
    }

    private static String toNumberTask(final SolvedBinaryTask solvedTask) {
        return solvedTask.number;
    }

}
