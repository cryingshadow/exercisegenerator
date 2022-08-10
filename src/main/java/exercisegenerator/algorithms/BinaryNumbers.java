package exercisegenerator.algorithms;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;

public class BinaryNumbers {

    public static final int DEFAULT_BINARY_CONTENT_LENGTH = 1;

    public static final String EXERCISE_TEXT_PATTERN_TO_ONES =
        "Die Zahl %s wird im %d-bit Einerkomplement dargestellt als:\\\\[2ex]";

    private static final String EXERCISE_TEXT_PATTERN_FROM_ONES =
        "Die Binärzahl %s im %d-bit Einerkomplement hat den Wert:\\\\[2ex]";

    private static final String EXERCISE_TEXT_PATTERN_FROM_TWOS =
        "Die Binärzahl %s im %d-bit Zweierkomplement hat den Wert:\\\\[2ex]";

    private static final String EXERCISE_TEXT_PATTERN_TO_TWOS =
        "Die Zahl %s wird im %d-bit Zweierkomplement dargestellt als:\\\\[2ex]";

    public static void fromOnesComplement(final AlgorithmInput input) throws IOException {
        final List<Pair<Integer, Integer>> tasks = BinaryNumbers.parseOrGenerateInput(input.options);
        final int contentLength = BinaryNumbers.getMaximumContentLength(tasks);
        for (final Pair<Integer, Integer> task : tasks) {
            BinaryNumbers.fromOnesComplement(task.x, task.y, contentLength, input.exerciseWriter, input.solutionWriter);
        }
    }

    public static void fromOnesComplement(
        final int number,
        final int length,
        final int contentLength,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        final BitString bitstring = BinaryNumbers.toOnesComplement(number, length);
        BinaryNumbers.complementExercise(
            bitstring.toString(),
            length,
            BinaryNumbers.EXERCISE_TEXT_PATTERN_FROM_ONES,
            1,
            contentLength,
            exerciseWriter
        );
        BinaryNumbers.complementSolution(BinaryNumbers.enhanceIntegerSolution(number), contentLength, solutionWriter);
    }

    public static void fromTwosComplement(final AlgorithmInput input) throws IOException {
        final List<Pair<Integer, Integer>> tasks = BinaryNumbers.parseOrGenerateInput(input.options);
        final int contentLength = BinaryNumbers.getMaximumContentLength(tasks);
        for (final Pair<Integer, Integer> task : tasks) {
            BinaryNumbers.fromTwosComplement(task.x, task.y, contentLength, input.exerciseWriter, input.solutionWriter);
        }
    }

    public static void fromTwosComplement(
        final int number,
        final int length,
        final int contentLength,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        final BitString bitstring = BinaryNumbers.toTwosComplement(number, length);
        BinaryNumbers.complementExercise(
            bitstring.toString(),
            length,
            BinaryNumbers.EXERCISE_TEXT_PATTERN_FROM_TWOS,
            1,
            contentLength,
            exerciseWriter
        );
        BinaryNumbers.complementSolution(
            BinaryNumbers.enhanceIntegerSolution(number),
            contentLength,
            solutionWriter
        );
    }

    public static void toOnesComplement(final AlgorithmInput input) throws IOException {
        final List<Pair<Integer, Integer>> tasks = BinaryNumbers.parseOrGenerateInput(input.options);
        for (final Pair<Integer, Integer> task : tasks) {
            BinaryNumbers.toOnesComplement(task.x, task.y, input.exerciseWriter, input.solutionWriter);
        }
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

    public static void toOnesComplement(
        final int number,
        final int length,
        final BufferedWriter exWriter,
        final BufferedWriter solWriter
    ) throws IOException {
        BinaryNumbers.complementExercise(
            String.valueOf(number),
            length,
            BinaryNumbers.EXERCISE_TEXT_PATTERN_TO_ONES,
            length,
            BinaryNumbers.DEFAULT_BINARY_CONTENT_LENGTH,
            exWriter
        );
        BinaryNumbers.complementSolution(
            BinaryNumbers.enhanceBitStringSolution(BinaryNumbers.toOnesComplement(number, length)),
            BinaryNumbers.DEFAULT_BINARY_CONTENT_LENGTH,
            solWriter
        );
    }

    public static void toTwosComplement(final AlgorithmInput input) throws IOException {
        final List<Pair<Integer, Integer>> tasks = BinaryNumbers.parseOrGenerateInput(input.options);
        for (final Pair<Integer, Integer> task : tasks) {
            BinaryNumbers.toTwosComplement(task.x, task.y, input.exerciseWriter, input.solutionWriter);
        }
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

    public static void toTwosComplement(
        final int number,
        final int length,
        final BufferedWriter exWriter,
        final BufferedWriter solWriter
    ) throws IOException {
        BinaryNumbers.complementExercise(
            String.valueOf(number),
            length,
            BinaryNumbers.EXERCISE_TEXT_PATTERN_TO_TWOS,
            length,
            BinaryNumbers.DEFAULT_BINARY_CONTENT_LENGTH,
            exWriter
        );
        BinaryNumbers.complementSolution(
            BinaryNumbers.enhanceBitStringSolution(BinaryNumbers.toTwosComplement(number, length)),
            BinaryNumbers.DEFAULT_BINARY_CONTENT_LENGTH,
            solWriter
        );
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

    private static void complementExercise(
        final String number,
        final int bitLength,
        final String textPattern,
        final int solutionLength,
        final int contentLength,
        final BufferedWriter writer
    ) throws IOException {
        writer.write(String.format(textPattern, number, bitLength));
        Main.newLine(writer);
        TikZUtils.printToggleForSolutions(writer);
        TikZUtils.printElse(writer);
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        TikZUtils.printEmptyArrayAndReturnLeftmostNodesName(solutionLength, Optional.empty(), contentLength, writer);
        TikZUtils.printTikzEnd(writer);
        TikZUtils.printEndIf(writer);
        Main.newLine(writer);
    }

    private static void complementSolution(
        final List<? extends ItemWithTikZInformation<?>> solution,
        final int contentLength,
        final BufferedWriter writer
    ) throws IOException {
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        TikZUtils.printListAndReturnLeftmostNodesName(solution, Optional.empty(), contentLength, writer);
        TikZUtils.printTikzEnd(writer);
        Main.newLine(writer);
    }

    private static List<ItemWithTikZInformation<Bit>> enhanceBitStringSolution(final BitString solution) {
        return solution.stream().map(b -> new ItemWithTikZInformation<Bit>(Optional.of(b))).toList();
    }

    private static List<ItemWithTikZInformation<Integer>> enhanceIntegerSolution(final int number) {
        return Collections.singletonList(new ItemWithTikZInformation<>(Optional.of(number)));
    }

    private static List<Pair<Integer, Integer>> generateInput(final Map<Flag, String> options) {
        final Random gen = new Random();
        final int numOfTasks = BinaryNumbers.generateNumOfTasks(options, gen);
        final int maxBitLength = BinaryNumbers.generateMaxBitLength(options, gen);
        final List<Pair<Integer, Integer>> result = new ArrayList<Pair<Integer, Integer>>(numOfTasks);
        final boolean onesComplement = BinaryNumbers.algorithmUsesOnesComplement(options);
        for (int i = 0; i < numOfTasks; i++) {
            result.add(BinaryNumbers.generateTask(gen, maxBitLength, onesComplement));
        }
        return result;
    }

    private static int generateMaxBitLength(final Map<Flag, String> options, final Random gen) {
        if (options.containsKey(Flag.CAPACITY)) {
            return Integer.parseInt(options.get(Flag.CAPACITY));
        }
        return 8;
    }

    private static int generateNumOfTasks(final Map<Flag, String> options, final Random gen) {
        if (options.containsKey(Flag.LENGTH)) {
            return Integer.parseInt(options.get(Flag.LENGTH));
        }
        return 3;
    }

    private static Pair<Integer, Integer> generateTask(
        final Random gen,
        final int maxBitLength,
        final boolean onesComplement
    ) {
        final int bitLength = gen.nextInt(maxBitLength - 1) + 2;
        int limit = (int)Math.pow(2, bitLength);
        final int toSubtract = limit / 2;
        if (onesComplement) {
            limit--;
        }
        int number = gen.nextInt(limit) - toSubtract;
        if (onesComplement) {
            number++;
        }
        return new Pair<Integer, Integer>(number, bitLength);
    }

    private static int getMaximumContentLength(final List<Pair<Integer, Integer>> tasks) {
        return tasks.stream().mapToInt(pair -> pair.x.toString().length()).max().getAsInt();
    }

    private static boolean outOfBoundsForOnesComplement(final int number, final int length) {
        return Math.abs(number) > Math.pow(2, length - 1) - 1;
    }

    private static boolean outOfBoundsForTwosComplement(final int number, final int length) {
        final double power = Math.pow(2, length - 1);
        return number > power - 1 || number < -power;
    }

    private static List<Pair<Integer, Integer>> parseInput(
        final BufferedReader reader,
        final Map<Flag, String> options
    ) throws IOException {
        return Arrays.stream(reader.readLine().split(";"))
            .map(s -> {
                final String[] pair = s.split(",");
                if (pair.length != 2) {
                    throw new IllegalArgumentException("Input must only contain pairs of numbers!");
                }
                return new Pair<Integer, Integer>(Integer.parseInt(pair[0]), Integer.parseInt(pair[1]));
            }).toList();
    }

    private static List<Pair<Integer, Integer>> parseOrGenerateInput(final Map<Flag, String> options)
    throws IOException {
        return new ParserAndGenerator<List<Pair<Integer, Integer>>>(
            BinaryNumbers::parseInput,
            BinaryNumbers::generateInput
        ).getResult(options);
    }

}
