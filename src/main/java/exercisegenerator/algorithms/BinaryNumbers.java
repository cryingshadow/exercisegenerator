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

    public static void fromOnesComplement(final AlgorithmInput input) {

    }

    public static void fromTwosComplement(final AlgorithmInput input) {

    }

    public static void toOnesComplement(final AlgorithmInput input) {

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

    public static void toTwosComplement(final AlgorithmInput input) {

    }

    private static boolean outOfBoundsForOnesComplement(final int number, final int length) {
        return Math.abs(number) > Math.pow(2, length - 1) - 1;
    }

    private static BitString toOnesComplement(final int number, final int length) {
        if (BinaryNumbers.outOfBoundsForOnesComplement(number, length)) {
            throw new IllegalArgumentException(
                String.format("Number is out of bounds for a %d-bit one's complement number!", length)
            );
        }
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
        if (number < 0) {
            return result.invert();
        }
        return result;
    }

    private static void toOnesComplementExercise(final int number, final int length, final BufferedWriter writer)
    throws IOException {
        writer.write(String.format(BinaryNumbers.EXERCISE_TEXT_PATTERN_TO_ONES, number, length));
        Main.newLine(writer);
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        TikZUtils.printEmptyArrayAndReturnLeftmostNodesName(
            length,
            Optional.empty(),
            BinaryNumbers.DEFAULT_BINARY_CONTENT_LENGTH,
            writer
        );
        TikZUtils.printTikzEnd(writer);
    }

    private static void toOnesComplementSolution(final int number, final int length, final BufferedWriter writer)
    throws IOException {
        final List<Bit> binary = BinaryNumbers.toOnesComplement(number, length);
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        TikZUtils.printListAndReturnLeftmostNodesName(
            binary.stream().map(b -> new ItemWithTikZInformation<Bit>(Optional.of(b))).toList(),
            Optional.empty(),
            BinaryNumbers.DEFAULT_BINARY_CONTENT_LENGTH,
            writer
        );
        TikZUtils.printTikzEnd(writer);
    }

}
