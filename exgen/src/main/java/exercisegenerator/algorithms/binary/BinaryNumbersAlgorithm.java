package exercisegenerator.algorithms.binary;

import java.io.*;
import java.math.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.binary.*;

interface BinaryNumbersAlgorithm<T> extends AlgorithmImplementation<List<T>, List<SolvedBinaryTask>> {

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

    static int generateNumOfTasks(final Parameters options) {
        return AlgorithmImplementation.parseOrGenerateLength(3, 3, options);
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
        return solvedTask.stream().mapToInt(task -> task.value().length()).max().getAsInt();
    }

    static boolean outOfBoundsForOnesComplement(final int number, final int length) {
        return Math.abs(number) > Math.pow(2, length - 1) - 1;
    }

    static List<BitString> parseBitStringValueTasks(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        return Arrays.stream(reader.readLine().split(";"))
            .map(BitString::parse)
            .toList();
    }

    static List<BitString> parseOrGenerateBitStringValueTasks(
        final Parameters options
    ) throws IOException {
        return new ParserAndGenerator<List<BitString>>(
            BinaryNumbersAlgorithm::parseBitStringValueTasks,
            BinaryNumbersAlgorithm::generateBitStringValueTasks
        ).getResult(options);
    }

    static List<NumberComplementTask> parseOrGenerateNumberComplementTasks(final Parameters options)
    throws IOException {
        return new ParserAndGenerator<List<NumberComplementTask>>(
            BinaryNumbersAlgorithm::parseNumberComplementTasks,
            BinaryNumbersAlgorithm::generateNumberComplementTasks
        ).getResult(options);
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

    private static boolean algorithmUsesOnesComplement(final Parameters options) {
        switch (Algorithm.forName(options.get(Flag.ALGORITHM)).get()) {
        case TO_ONES_COMPLEMENT:
        case FROM_ONES_COMPLEMENT:
            return true;
        default:
            return false;
        }
    }

    private static List<BitString> generateBitStringValueTasks(final Parameters options) {
        final int numOfTasks = BinaryNumbersAlgorithm.generateNumOfTasks(options);
        final int bitLength = BinaryNumbersAlgorithm.getBitLength(options);
        final List<BitString> result = new ArrayList<BitString>(numOfTasks);
        for (int i = 0; i < numOfTasks; i++) {
            result.add(BinaryNumbersAlgorithm.generateBitString(bitLength));
        }
        return result;
    }

    private static List<NumberComplementTask> generateNumberComplementTasks(final Parameters options) {
        final int numOfTasks = BinaryNumbersAlgorithm.generateNumOfTasks(options);
        final int bitLength = BinaryNumbersAlgorithm.getBitLength(options);
        final List<NumberComplementTask> result = new ArrayList<NumberComplementTask>(numOfTasks);
        final boolean onesComplement = BinaryNumbersAlgorithm.algorithmUsesOnesComplement(options);
        for (int i = 0; i < numOfTasks; i++) {
            result.add(
                new NumberComplementTask(
                    BinaryNumbersAlgorithm.generateNumberWithinBitlength(bitLength, onesComplement),
                    bitLength
                )
            );
        }
        return result;
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

    private static List<NumberComplementTask> parseNumberComplementTasks(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        final int bitLength = BinaryNumbersAlgorithm.getBitLength(options);
        return Arrays.stream(reader.readLine().split(";"))
            .map(n -> new NumberComplementTask(Integer.parseInt(n), bitLength))
            .toList();
    }

    @Override
    default public List<SolvedBinaryTask> apply(final List<T> problem) {
        return problem.stream().map(this::algorithm).toList();
    }

    @Override
    default public void printExercise(
        final List<T> problem,
        final List<SolvedBinaryTask> solution,
        final Parameters options,
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
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        this.printAssignments(solution, false, writer);
        Main.newLine(writer);
    }

    SolvedBinaryTask algorithm(T task);

    String getExerciseText(Parameters options);

    String getExerciseTextSingular(Parameters options);

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
