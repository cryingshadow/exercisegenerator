package exercisegenerator.algorithms.binary;

import java.io.*;
import java.math.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.binary.*;

public interface BinaryNumbersAlgorithm<T> extends AlgorithmImplementation<T, SolvedBinaryTask> {

    static BitString generateBitString(final int bitLength) {
        return BinaryNumbersAlgorithm.generateBitString(bitLength, BigInteger.ZERO, BigInteger.TWO.pow(bitLength));
    }

    static BitString generateBitString(final int bitLength, final BigInteger from, final BigInteger to) {
        final BitString result = new BitString();
        for (int i = 0; i < bitLength; i++) {
            result.add(Bit.fromBoolean(Main.RANDOM.nextBoolean()));
        }
        final BigInteger value = result.toNonNegativeBigInteger();
        if (value.compareTo(from) < 0 || value.compareTo(to) > 0) {
            final BigInteger range = to.subtract(from).add(BigInteger.ONE);
            final BigInteger newValue = value.mod(range).add(from);
            return BitString.create(newValue, bitLength);
        }
        return result;
    }

    static NumberComplementTask generateNumberComplementTasks(final Parameters<Flag> options) {
        final int bitLength = BinaryNumbersAlgorithm.getBitLength(options);
        final boolean onesComplement = BinaryNumbersAlgorithm.algorithmUsesOnesComplement(options);
        return new NumberComplementTask(
            BinaryNumbersAlgorithm.generateNumberWithinBitlength(bitLength, onesComplement),
            bitLength
        );
    }

    static int generateNumOfTasks(final Parameters<Flag> options) {
        return AlgorithmImplementation.parseOrGenerateLength(3, 3, options);
    }

    static int getBitLength(final Parameters<Flag> options) {
        return Integer.parseInt(options.get(Flag.CAPACITY));
    }

    static int getExcess(final int exponentLength) {
        return ((int)Math.pow(2, exponentLength - 1)) - 1;
    }

    static int getExponentLength(final Parameters<Flag> options) {
        return Integer.parseInt(options.get(Flag.DEGREE));
    }

    static int getMantissaLength(final Parameters<Flag> options) {
        return Integer.parseInt(options.get(Flag.CAPACITY));
    }

    static int getMaximumContentLength(final List<SolvedBinaryTask> solvedTask) {
        return solvedTask.stream().mapToInt(task -> task.value().length()).max().getAsInt();
    }

    static boolean outOfBoundsForOnesComplement(final int number, final int length) {
        return Math.abs(number) > Math.pow(2, length - 1) - 1;
    }

    static List<NumberComplementTask> parseNumberComplementTasks(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        final int bitLength = BinaryNumbersAlgorithm.getBitLength(options);
        return Arrays.stream(reader.readLine().split(";"))
            .map(n -> new NumberComplementTask(Integer.parseInt(n), bitLength))
            .toList();
    }

    static List<ItemWithTikZInformation<Bit>> toBitStringSolution(final SolvedBinaryTask solvedTask) {
        return solvedTask.bitString().stream().map(b -> new ItemWithTikZInformation<Bit>(Optional.of(b))).toList();
    }

    static String toBitStringTask(final SolvedBinaryTask solvedTask) {
        return LaTeXUtils.code(solvedTask.bitString().toString());
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
                Optional.of(LaTeXUtils.escapeForLaTeX(solvedTask.value()).replaceAll(" ", "\\\\textvisiblespace{}"))
            )
        );
    }

    static String toValueTask(final SolvedBinaryTask solvedTask) {
        return LaTeXUtils.escapeForLaTeX(solvedTask.value()).replaceAll(" ", "\\\\textvisiblespace{}");
    }

    private static boolean algorithmUsesOnesComplement(final Parameters<Flag> options) {
        switch (Algorithm.forName(options.get(Flag.ALGORITHM)).get()) {
        case TO_ONES_COMPLEMENT:
        case FROM_ONES_COMPLEMENT:
            return true;
        default:
            return false;
        }
    }

    private static int generateNumberWithinBitlength(final int bitLength, final boolean onesComplement) {
        int limit = (int)Math.pow(2, bitLength);
        final int toSubtract = limit / 2;
        if (onesComplement) {
            limit--;
        }
        int number = Main.RANDOM.nextInt(limit) - toSubtract;
        if (onesComplement) {
            number++;
        }
        return number;
    }

    @Override
    default public void printExercise(
        final List<T> problem,
        final List<SolvedBinaryTask> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write(problem.size() == 1 ? this.getExerciseTextSingular(options) : this.getExerciseText(options));
        writer.write(":\\\\[2ex]");
        Main.newLine(writer);
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
        this.printAssignments(solution, true, writer);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, writer);
    }

    @Override
    default public void printSolution(
        final List<T> problem,
        final List<SolvedBinaryTask> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        this.printAssignments(solution, false, writer);
        Main.newLine(writer);
    }

    @Override
    default String commandPrefix() {
        return "Binary";
    }

    String getExerciseText(Parameters<Flag> options);

    String getExerciseTextSingular(Parameters<Flag> options);

    @Override
    default void printBeforeMultipleProblemInstances(
        final List<T> problems,
        final List<SolvedBinaryTask> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

    @Override
    default void printBeforeSingleProblemInstance(
        final T problem,
        final SolvedBinaryTask solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

    @Override
    default void printProblemInstance(
        final T problem,
        final SolvedBinaryTask solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

    @Override
    default void printSolutionInstance(
        final T problem,
        final SolvedBinaryTask solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

    @Override
    default void printSolutionSpace(
        final T problem,
        final SolvedBinaryTask solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

    int toContentLength(List<SolvedBinaryTask> solvedTasks);

    List<? extends ItemWithTikZInformation<?>> toSolution(SolvedBinaryTask solvedTask);

    String toTaskText(SolvedBinaryTask solvedTask);

    private void printAssignments(
        final List<SolvedBinaryTask> solvedTasks,
        final boolean exercise,
        final BufferedWriter writer
    ) throws IOException {
        final String longestTask =
            "-" +
            solvedTasks.stream()
            .map(this::toTaskText)
            .sorted((s1,s2) -> Integer.compare(s2.length(), s1.length()))
            .findFirst()
            .get();
        final int contentLength = this.toContentLength(solvedTasks);
        boolean first = true;
        for (final SolvedBinaryTask solvedTask : solvedTasks) {
            if (first) {
                first = false;
            } else {
                LaTeXUtils.printVerticalProtectedSpace(writer);
            }
            Algorithm.assignment(
                this.toTaskText(solvedTask),
                this.toSolution(solvedTask),
                longestTask,
                solvedTask.relation(),
                contentLength,
                exercise,
                writer
            );
        }
    }

}
