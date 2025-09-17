package exercisegenerator.algorithms.sorting;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;

interface Sorting extends AlgorithmImplementation<int[], SortingSolution> {

    static int[] generateArray(final Parameters<Flag> options, final int lowestInt, final int highestInt) {
        final int length = AlgorithmImplementation.parseOrGenerateLength(5, 20, options);
        final int[] array = new int[length];
        final int range = highestInt - lowestInt + 1;
        for (int i = 0; i < array.length; i++) {
            array[i] = Main.RANDOM.nextInt(range) + lowestInt;
        }
        return array;
    }

    static int getMaximumContentLength(final int[] array) {
        return Arrays.stream(array).map(n -> String.valueOf(n).length()).max().getAsInt();
    }

    static List<int[]> parseArrays(final BufferedReader reader, final Parameters<Flag> options) throws IOException {
        final List<int[]> result = new ArrayList<int[]>();
        String line = reader.readLine();
        while (line != null) {
            if (!line.isBlank()) {
                final String[] numbers = line.split(",");
                final int[] array = new int[numbers.length];
                for (int i = 0; i < array.length; i++) {
                    array[i] = Integer.parseInt(numbers[i].trim());
                }
                result.add(array);
            }
            line = reader.readLine();
        }
        return result;
    }

    static List<ItemWithTikZInformation<Integer>> toTikZItems(final int[] array) {
        return Arrays.stream(array)
            .mapToObj((final int i) -> new ItemWithTikZInformation<Integer>(Optional.of(i), false, false))
            .toList();
    }

    static List<ItemWithTikZInformation<Integer>> toTikZItems(final int[] array, final boolean[] separate) {
        final List<ItemWithTikZInformation<Integer>> result = new ArrayList<ItemWithTikZInformation<Integer>>();
        result.add(new ItemWithTikZInformation<Integer>(Optional.of(array[0]), false));
        for (int i = 1; i < array.length; i++) {
            result.add(new ItemWithTikZInformation<Integer>(Optional.of(array[i]), separate[i - 1]));
        }
        return result;
    }

    static List<ItemWithTikZInformation<Integer>> toTikZItems(
        final int[] array,
        final boolean[] separate,
        final boolean[] markers
    ) {
        final List<ItemWithTikZInformation<Integer>> result = new ArrayList<ItemWithTikZInformation<Integer>>();
        result.add(new ItemWithTikZInformation<Integer>(Optional.ofNullable(array[0]), markers[0], false));
        for (int i = 1; i < array.length; i++) {
            result.add(
                new ItemWithTikZInformation<Integer>(Optional.ofNullable(array[i]), markers[i], separate[i - 1])
            );
        }
        return result;
    }

    String additionalExerciseText();

    String algorithmName();

    @Override
    default int[] generateProblem(final Parameters<Flag> options) {
        return Sorting.generateArray(options, 0, Main.NUMBER_LIMIT - 1);
    }

    String operation();

    @Override
    default List<int[]> parseProblems(final BufferedReader reader, final Parameters<Flag> options) throws IOException {
        return Sorting.parseArrays(reader, options);
    }

    @Override
    default void printBeforeMultipleProblemInstances(
        final List<int[]> problems,
        final List<SortingSolution> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Sortieren Sie die folgenden Arrays mithilfe von ");
        writer.write(this.algorithmName());
        writer.write(".");
        Main.newLine(writer);
        writer.write("Geben Sie dazu das jeweilige Array nach jeder ");
        writer.write(this.operation());
        writer.write(" an");
        writer.write(this.additionalExerciseText());
        writer.write(".\\\\");
        Main.newLine(writer);
    }

    @Override
    default void printBeforeSingleProblemInstance(
        final int[] problem,
        final SortingSolution solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Sortieren Sie das folgende Array mithilfe von ");
        writer.write(this.algorithmName());
        writer.write(".");
        Main.newLine(writer);
        writer.write("Geben Sie dazu das Array nach jeder ");
        writer.write(this.operation());
        writer.write(" an");
        writer.write(this.additionalExerciseText());
        writer.write(".\\\\[2ex]");
        Main.newLine(writer);
    }

    @Override
    default void printProblemInstance(
        final int[] problem,
        final SortingSolution solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

    @Override
    default void printSolutionInstance(
        final int[] problem,
        final SortingSolution solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        String anchor = null;
        for (final List<ItemWithTikZInformation<Integer>> list : solution.solution()) {
            anchor =
                LaTeXUtils.printListAndReturnLowestLeftmostNodesName(
                    list,
                    Optional.ofNullable(anchor),
                    solution.contentLength(),
                    writer
                );
        }
        LaTeXUtils.printTikzEnd(writer);
    }

    @Override
    default void printSolutionSpace(
        final int[] problem,
        final SortingSolution solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
        LaTeXUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        String anchor =
            LaTeXUtils.printListAndReturnLowestLeftmostNodesName(
                Sorting.toTikZItems(problem),
                Optional.empty(),
                solution.contentLength(),
                writer
            );
        final int rows = solution.solution().size();
        for (int i = 1; i < rows; i++) {
            anchor =
                LaTeXUtils.printEmptyArrayAndReturnLeftmostNodesName(
                    problem.length,
                    Optional.of(anchor),
                    solution.contentLength(),
                    writer
                );
        }
        LaTeXUtils.printTikzEnd(writer);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, writer);
    }

}
