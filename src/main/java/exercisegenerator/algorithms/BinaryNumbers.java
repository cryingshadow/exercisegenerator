package exercisegenerator.algorithms;

import java.io.*;
import java.util.*;
import java.util.function.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;

public class BinaryNumbers {

    public static final int DEFAULT_BINARY_CONTENT_LENGTH = 1;

    public static final String EXERCISE_TEXT_PATTERN_TO_ONES =
        "Stellen Sie die folgenden Dezimalzahlen im %d-bit Einerkomplement dar:\\\\[2ex]";

    private static final String EXERCISE_TEXT_PATTERN_FROM_ONES =
        "Geben Sie den Dezimalwert der folgenden Binärzahlen im %d-bit Einerkomplement an:\\\\[2ex]";

    private static final String EXERCISE_TEXT_PATTERN_FROM_TWOS =
        "Geben Sie den Dezimalwert der folgenden Binärzahlen im %d-bit Zweierkomplement an:\\\\[2ex]";

    private static final String EXERCISE_TEXT_PATTERN_TO_TWOS =
        "Stellen Sie die folgenden Dezimalzahlen im %d-bit Zweierkomplement dar:\\\\[2ex]";

    public static void fromOnesComplement(final AlgorithmInput input) throws IOException {
        BinaryNumbers.complement(
            input,
            BinaryNumbers.EXERCISE_TEXT_PATTERN_FROM_ONES,
            BinaryNumbers::toOnesComplement,
            (number, bitstring) -> TikZUtils.code(bitstring.toString()),
            (number, bitstring) -> BinaryNumbers.enhanceIntegerSolution(number),
            BinaryNumbers::getMaximumContentLength
        );
    }

    public static void fromTwosComplement(final AlgorithmInput input) throws IOException {
        BinaryNumbers.complement(
            input,
            BinaryNumbers.EXERCISE_TEXT_PATTERN_FROM_TWOS,
            BinaryNumbers::toTwosComplement,
            (number, bitstring) -> TikZUtils.code(bitstring.toString()),
            (number, bitstring) -> BinaryNumbers.enhanceIntegerSolution(number),
            BinaryNumbers::getMaximumContentLength
        );
    }

    public static void toFloat(final AlgorithmInput input) throws IOException {

    }

    public static void toOnesComplement(final AlgorithmInput input) throws IOException {
        BinaryNumbers.complement(
            input,
            BinaryNumbers.EXERCISE_TEXT_PATTERN_TO_ONES,
            BinaryNumbers::toOnesComplement,
            (number, bitstring) -> String.valueOf(number),
            (number, bitstring) -> BinaryNumbers.enhanceBitStringSolution(bitstring),
            numbers -> 1
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
        BinaryNumbers.complement(
            input,
            BinaryNumbers.EXERCISE_TEXT_PATTERN_TO_TWOS,
            BinaryNumbers::toTwosComplement,
            (number, bitstring) -> String.valueOf(number),
            (number, bitstring) -> BinaryNumbers.enhanceBitStringSolution(bitstring),
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

    private static boolean algorithmUsesOnesComplement(final Map<Flag, String> options) {
        switch (Algorithm.forName(options.get(Flag.ALGORITHM)).get()) {
        case TO_ONES_COMPLEMENT:
        case FROM_ONES_COMPLEMENT:
            return true;
        default:
            return false;
        }
    }

    private static void complement(
        final AlgorithmInput input,
        final String textPattern,
        final BiFunction<Integer, Integer, BitString> algorithm,
        final BiFunction<Integer, BitString, String> toTask,
        final BiFunction<Integer, BitString, List<? extends ItemWithTikZInformation<?>>> toSolution,
        final Function<List<Integer>, Integer> toContentLength
    ) throws IOException {
        final List<Integer> numbers = BinaryNumbers.parseOrGenerateInput(input.options);
        final int bitLength = BinaryNumbers.getBitLength(input.options);
        final int contentLength = toContentLength.apply(numbers);
        BinaryNumbers.complementStart(textPattern, bitLength, input.exerciseWriter, input.solutionWriter);
        boolean first = true;
        for (final int number : numbers) {
            if (first) {
                first = false;
            } else {
                TikZUtils.printVerticalProtectedSpace(input.exerciseWriter);
                TikZUtils.printVerticalProtectedSpace(input.solutionWriter);
            }
            final BitString bitstring = algorithm.apply(number, bitLength);
            BinaryNumbers.complementTask(
                toTask.apply(number, bitstring),
                toSolution.apply(number, bitstring),
                contentLength,
                input.exerciseWriter,
                input.solutionWriter
            );
        }
        BinaryNumbers.complementEnd(input.exerciseWriter, input.solutionWriter);
    }

    private static void complementEnd(final BufferedWriter exerciseWriter, final BufferedWriter solutionWriter)
    throws IOException {
        TikZUtils.printEndIf(exerciseWriter);
        Main.newLine(exerciseWriter);
        Main.newLine(solutionWriter);
    }

    private static void complementStart(
        final String textPattern,
        final int bitLength,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        exerciseWriter.write(String.format(textPattern, bitLength));
        Main.newLine(exerciseWriter);
        TikZUtils.printToggleForSolutions(exerciseWriter);
        TikZUtils.printElse(exerciseWriter);
    }

    private static void complementTask(
        final String task,
        final List<? extends ItemWithTikZInformation<?>> solution,
        final int contentLength,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        BinaryNumbers.complementTaskExercise(task, solution.size(), contentLength, exerciseWriter);
        BinaryNumbers.complementTaskSolution(task, solution, contentLength, solutionWriter);
    }

    private static void complementTaskEnd(final BufferedWriter writer) throws IOException {
        TikZUtils.printTikzEnd(writer);
        TikZUtils.printMinipageEnd(writer);
    }

    private static void complementTaskExercise(
        final String task,
        final int solutionLength,
        final int contentLength,
        final BufferedWriter writer
    ) throws IOException {
        BinaryNumbers.complementTaskStart(task, writer);
        TikZUtils.printEmptyArrayAndReturnLeftmostNodesName(solutionLength, Optional.empty(), contentLength, writer);
        BinaryNumbers.complementTaskEnd(writer);
    }

    private static void complementTaskSolution(
        final String task,
        final List<? extends ItemWithTikZInformation<?>> solution,
        final int contentLength,
        final BufferedWriter writer
    ) throws IOException {
        BinaryNumbers.complementTaskStart(task, writer);
        TikZUtils.printListAndReturnLeftmostNodesName(solution, Optional.empty(), contentLength, writer);
        BinaryNumbers.complementTaskEnd(writer);
    }

    private static void complementTaskStart(final String task, final BufferedWriter writer) throws IOException {
        final String taskText = String.format("$%s = {}$", task);
        TikZUtils.printMinipageBeginning(TikZUtils.widthOf(taskText), writer);
        writer.write(taskText);
        Main.newLine(writer);
        TikZUtils.printMinipageEnd(writer);
        TikZUtils.printMinipageBeginning(TikZUtils.widthOfComplement(taskText), writer);
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
    }

    private static List<ItemWithTikZInformation<Bit>> enhanceBitStringSolution(final BitString solution) {
        return solution.stream().map(b -> new ItemWithTikZInformation<Bit>(Optional.of(b))).toList();
    }

    private static List<ItemWithTikZInformation<Integer>> enhanceIntegerSolution(final int number) {
        return Collections.singletonList(new ItemWithTikZInformation<>(Optional.of(number)));
    }

    private static List<Integer> generateInput(final Map<Flag, String> options) {
        final Random gen = new Random();
        final int numOfTasks = BinaryNumbers.generateNumOfTasks(options, gen);
        final int bitLength = BinaryNumbers.getBitLength(options);
        final List<Integer> result = new ArrayList<Integer>(numOfTasks);
        final boolean onesComplement = BinaryNumbers.algorithmUsesOnesComplement(options);
        for (int i = 0; i < numOfTasks; i++) {
            result.add(BinaryNumbers.generateNumberWithinBitlength(gen, bitLength, onesComplement));
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

    private static int getBitLength(final Map<Flag, String> options) {
        return Integer.parseInt(options.get(Flag.CAPACITY));
    }

    private static int getMaximumContentLength(final List<Integer> numbers) {
        return numbers.stream().mapToInt(number -> number.toString().length()).max().getAsInt();
    }

    private static boolean outOfBoundsForOnesComplement(final int number, final int length) {
        return Math.abs(number) > Math.pow(2, length - 1) - 1;
    }

    private static boolean outOfBoundsForTwosComplement(final int number, final int length) {
        final double power = Math.pow(2, length - 1);
        return number > power - 1 || number < -power;
    }

    private static List<Integer> parseInput(final BufferedReader reader, final Map<Flag, String> options)
    throws IOException {
        return Arrays.stream(reader.readLine().split(",")).map(Integer::parseInt).toList();
    }

    private static List<Integer> parseOrGenerateInput(final Map<Flag, String> options)
    throws IOException {
        return new ParserAndGenerator<List<Integer>>(
            BinaryNumbers::parseInput,
            BinaryNumbers::generateInput
        ).getResult(options);
    }

}
