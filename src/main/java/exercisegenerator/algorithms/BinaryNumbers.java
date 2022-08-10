package exercisegenerator.algorithms;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;

public class BinaryNumbers {

    public static final int DEFAULT_BINARY_CONTENT_LENGTH = 1;

    public static final String EXERCISE_TEXT_PATTERN_TO_ONES =
        "Die Zahl %d wird im %d-bit Einerkomplement dargestellt als:\\\\[2ex]";

    private static final String EXERCISE_TEXT_PATTERN_FROM_ONES =
        "Die Binärzahl %s im %d-bit Einerkomplement hat den Wert:\\\\[2ex]";

    private static final String EXERCISE_TEXT_PATTERN_FROM_TWOS =
        "Die Binärzahl %s im %d-bit Zweierkomplement hat den Wert:\\\\[2ex]";

    private static final String EXERCISE_TEXT_PATTERN_TO_TWOS =
        "Die Zahl %d wird im %d-bit Zweierkomplement dargestellt als:\\\\[2ex]";

    public static void fromOnesComplement(final AlgorithmInput input) throws IOException {
        final List<Pair<Integer, Integer>> tasks = BinaryNumbers.parseOrGenerateInput(input.options);
        final int contentLength = BinaryNumbers.getMaximumContentLength(tasks);
        for (final Pair<Integer, Integer> task : tasks) {
            BinaryNumbers.fromOnesComplement(task.x, task.y, contentLength, input.exerciseWriter, input.solutionWriter);
        }
    }

    public static void fromTwosComplement(final AlgorithmInput input) throws IOException {
        final List<Pair<Integer, Integer>> tasks = BinaryNumbers.parseOrGenerateInput(input.options);
        final int contentLength = BinaryNumbers.getMaximumContentLength(tasks);
        for (final Pair<Integer, Integer> task : tasks) {
            BinaryNumbers.fromTwosComplement(task.x, task.y, contentLength, input.exerciseWriter, input.solutionWriter);
        }
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
        BinaryNumbers.toOnesComplementExercise(number, length, exWriter);
        BinaryNumbers.toOnesComplementSolution(number, length, solWriter);
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
        BinaryNumbers.toTwosComplementExercise(number, length, exWriter);
        BinaryNumbers.toTwosComplementSolution(number, length, solWriter);
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

    private static void fromComplementExercise(
        final String textPattern,
        final String number,
        final int length,
        final int contentLength,
        final BufferedWriter writer
    ) throws IOException {
        writer.write(String.format(textPattern, number, length));
        Main.newLine(writer);
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        TikZUtils.printEmptyArrayAndReturnLeftmostNodesName(1, Optional.empty(), contentLength, writer);
        TikZUtils.printTikzEnd(writer);
        Main.newLine(writer);
    }

    private static void fromComplementSolution(
        final int number,
        final int length,
        final int contentLength,
        final BufferedWriter writer
    ) throws IOException {
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        TikZUtils.printListAndReturnLeftmostNodesName(
            Collections.singletonList(new ItemWithTikZInformation<>(Optional.of(number))),
            Optional.empty(),
            contentLength,
            writer
        );
        TikZUtils.printTikzEnd(writer);
        Main.newLine(writer);
    }

    private static void fromOnesComplement(
        final int number,
        final int length,
        final int contentLength,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        final BitString bitstring = BinaryNumbers.toOnesComplement(number, length);
        BinaryNumbers.fromOnesComplementExercise(bitstring.toString(), length, contentLength, exerciseWriter);
        BinaryNumbers.fromComplementSolution(number, length, contentLength, solutionWriter);
    }

    private static void fromOnesComplementExercise(
        final String number,
        final int length,
        final int contentLength,
        final BufferedWriter writer
    ) throws IOException {
        BinaryNumbers.fromComplementExercise(
            BinaryNumbers.EXERCISE_TEXT_PATTERN_FROM_ONES,
            number,
            length,
            contentLength,
            writer
        );
    }

    private static void fromTwosComplement(
        final int number,
        final int length,
        final int contentLength,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        final BitString bitstring = BinaryNumbers.toTwosComplement(number, length);
        BinaryNumbers.fromTwosComplementExercise(bitstring.toString(), length, contentLength, exerciseWriter);
        BinaryNumbers.fromComplementSolution(number, length, contentLength, solutionWriter);
    }

    private static void fromTwosComplementExercise(
        final String number,
        final int length,
        final int contentLength,
        final BufferedWriter writer
    ) throws IOException {
        BinaryNumbers.fromComplementExercise(
            BinaryNumbers.EXERCISE_TEXT_PATTERN_FROM_TWOS,
            number,
            length,
            contentLength,
            writer
        );
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

    private static List<Pair<Integer, Integer>> parseOrGenerateInput(final Map<Flag, String> options)
    throws IOException {
        return new ParserAndGenerator<List<Pair<Integer, Integer>>>(
            (final BufferedReader reader) -> {
                return Arrays.stream(reader.readLine().split(";"))
                    .map(s -> {
                        final String[] pair = s.split(",");
                        if (pair.length != 2) {
                            throw new IllegalArgumentException("Input must only contain pairs of numbers!");
                        }
                        return new Pair<Integer, Integer>(Integer.parseInt(pair[0]), Integer.parseInt(pair[1]));
                    }).toList();
            },
            () -> {
                final int length;
                final Random gen = new Random();
                if (options.containsKey(Flag.LENGTH)) {
                    length = Integer.parseInt(options.get(Flag.LENGTH));
                } else {
                    length = 3;
                }
                final int maxBitLength;
                if (options.containsKey(Flag.CAPACITY)) {
                    maxBitLength = Integer.parseInt(options.get(Flag.CAPACITY));
                } else {
                    maxBitLength = 8;
                }
                final List<Pair<Integer, Integer>> result = new ArrayList<Pair<Integer, Integer>>(length);
                final boolean onesComplement;
                switch (Algorithm.forName(options.get(Flag.ALGORITHM)).get()) {
                case TO_ONES_COMPLEMENT:
                case FROM_ONES_COMPLEMENT:
                    onesComplement = true;;
                    break;
                default:
                    onesComplement = false;
                }
                for (int i = 0; i < length; i++) {
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
                    result.add(new Pair<Integer, Integer>(number, bitLength));
                }
                return result;
            }
        ).getResult(options);
    }

    private static void toComplementExercise(
        final int number,
        final int length,
        final String textPattern,
        final BufferedWriter writer
    ) throws IOException {
        writer.write(String.format(textPattern, number, length));
        Main.newLine(writer);
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        TikZUtils.printEmptyArrayAndReturnLeftmostNodesName(
            length,
            Optional.empty(),
            BinaryNumbers.DEFAULT_BINARY_CONTENT_LENGTH,
            writer
        );
        TikZUtils.printTikzEnd(writer);
        Main.newLine(writer);
    }

    private static void toComplementSolution(final BitString solution, final BufferedWriter writer)
    throws IOException {
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        TikZUtils.printListAndReturnLeftmostNodesName(
            solution.stream().map(b -> new ItemWithTikZInformation<Bit>(Optional.of(b))).toList(),
            Optional.empty(),
            BinaryNumbers.DEFAULT_BINARY_CONTENT_LENGTH,
            writer
        );
        TikZUtils.printTikzEnd(writer);
        Main.newLine(writer);
    }

    private static void toOnesComplementExercise(final int number, final int length, final BufferedWriter writer)
    throws IOException {
        BinaryNumbers.toComplementExercise(number, length, BinaryNumbers.EXERCISE_TEXT_PATTERN_TO_ONES, writer);
    }

    private static void toOnesComplementSolution(final int number, final int length, final BufferedWriter writer)
    throws IOException {
        BinaryNumbers.toComplementSolution(BinaryNumbers.toOnesComplement(number, length), writer);
    }

    private static void toTwosComplementExercise(final int number, final int length, final BufferedWriter writer)
    throws IOException {
        BinaryNumbers.toComplementExercise(number, length, BinaryNumbers.EXERCISE_TEXT_PATTERN_TO_TWOS, writer);
    }

    private static void toTwosComplementSolution(final int number, final int length, final BufferedWriter writer)
    throws IOException {
        BinaryNumbers.toComplementSolution(BinaryNumbers.toTwosComplement(number, length), writer);
    }

}
