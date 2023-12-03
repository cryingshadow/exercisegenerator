package exercisegenerator.algorithms.binary;

import java.io.*;
import java.math.*;
import java.util.*;
import java.util.function.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.binary.*;
import exercisegenerator.util.*;

abstract class BinaryNumbers {

    static abstract class BinaryTask {}

    static class BitStringValueTask extends BinaryTask {
        final BitString bitString;

        BitStringValueTask(final BitString bitString) {
            this.bitString = bitString;
        }
    }

    static class NumberComplementTask extends BinaryTask {
        final int bitLength;
        final int number;

        NumberComplementTask(final int bitLength, final int number) {
            this.number = number;
            this.bitLength = bitLength;
        }
    }

    static class SolvedBinaryTask {
        private final BitString bitString;
        private final String value;

        SolvedBinaryTask(final String value, final BitString bitString) {
            this.value = value;
            this.bitString = bitString;
        }
    }

    static <T extends BinaryTask> void allBinaryTasks(
        final AlgorithmInput input,
        final String exerciseText,
        final String exerciseTextSingular,
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
        BinaryNumbers.binaryBeginning(
            tasks.size() == 1 ? exerciseTextSingular : exerciseText,
            input.options,
            input.exerciseWriter,
            input.solutionWriter
        );
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

    static BitString generateBitString(final Random gen, final int bitLength) {
        return BinaryNumbers.generateBitString(gen, bitLength, BigInteger.ZERO, BigInteger.TWO.pow(bitLength));
    }

    static BitString generateBitString(
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

    static int generateNumOfTasks(final Parameters options, final Random gen) {
        if (options.containsKey(Flag.LENGTH)) {
            return Integer.parseInt(options.get(Flag.LENGTH));
        }
        return 3;
    }

    static int getBitLength(final Parameters options) {
        return Integer.parseInt(options.get(Flag.CAPACITY));
    }

    static int getExcess(final int exponentLength) {
        return ((int)Math.pow(2, exponentLength - 1)) - 1;
    }

    static int getExponentLength(final Parameters options) {
        return Integer.parseInt(options.get(Flag.DEGREE));
    }

    static int getMantissaLength(final Parameters options) {
        return Integer.parseInt(options.get(Flag.CAPACITY));
    }

    static int getMaximumContentLength(final List<SolvedBinaryTask> solvedTask) {
        return solvedTask.stream().mapToInt(task -> task.value.length()).max().getAsInt();
    }

    static boolean outOfBoundsForOnesComplement(final int number, final int length) {
        return Math.abs(number) > Math.pow(2, length - 1) - 1;
    }

    static List<BitStringValueTask> parseBitStringValueTasks(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        return Arrays.stream(reader.readLine().split(";"))
            .map(bitstring -> new BitStringValueTask(BitString.parse(bitstring)))
            .toList();
    }

    static List<BitStringValueTask> parseOrGenerateBitStringValueTasks(
        final Parameters options
    ) throws IOException {
        return new ParserAndGenerator<List<BitStringValueTask>>(
            BinaryNumbers::parseBitStringValueTasks,
            BinaryNumbers::generateBitStringValueTasks
        ).getResult(options);
    }

    static List<NumberComplementTask> parseOrGenerateNumberComplementTasks(final Parameters options)
    throws IOException {
        return new ParserAndGenerator<List<NumberComplementTask>>(
            BinaryNumbers::parseNumberComplementTasks,
            BinaryNumbers::generateNumberComplementTasks
        ).getResult(options);
    }

    static List<ItemWithTikZInformation<Bit>> toBitStringSolution(final SolvedBinaryTask solvedTask) {
        return solvedTask.bitString.stream().map(b -> new ItemWithTikZInformation<Bit>(Optional.of(b))).toList();
    }

    static String toBitStringTask(final SolvedBinaryTask solvedTask) {
        return LaTeXUtils.code(solvedTask.bitString.toString());
    }

    static BitString toUnsignedBinary(final int number, final int length) {
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

    static List<ItemWithTikZInformation<String>> toValueSolution(final SolvedBinaryTask solvedTask) {
        return Collections.singletonList(
            new ItemWithTikZInformation<String>(
                Optional.of(LaTeXUtils.escapeForLaTeX(solvedTask.value).replaceAll(" ", "\\\\textvisiblespace{}"))
            )
        );
    }

    static String toValueTask(final SolvedBinaryTask solvedTask) {
        return LaTeXUtils.escapeForLaTeX(solvedTask.value).replaceAll(" ", "\\\\textvisiblespace{}");
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

    private static void binaryBeginning(
        final String exerciseText,
        final Parameters options,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        exerciseWriter.write(exerciseText);
        exerciseWriter.write(":\\\\[2ex]");
        Main.newLine(exerciseWriter);
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, exerciseWriter);
    }

    private static void binaryEnd(
        final Parameters options,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, exerciseWriter);
        Main.newLine(solutionWriter);
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

    private static List<NumberComplementTask> parseNumberComplementTasks(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        final int bitLength = BinaryNumbers.getBitLength(options);
        return Arrays.stream(reader.readLine().split(";"))
            .map(n -> new NumberComplementTask(bitLength, Integer.parseInt(n)))
            .toList();
    }

}
