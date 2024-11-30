package exercisegenerator.algorithms.sorting;

import java.io.*;
import java.util.*;
import java.util.function.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.util.*;

abstract class Sorting {

    static class SortingSolution {
        final int contentLength;
        final List<List<ItemWithTikZInformation<Integer>>> solution;
        final BufferedWriter writer;
        SortingSolution(
            final List<List<ItemWithTikZInformation<Integer>>> solution,
            final int contentLength,
            final BufferedWriter writer
        ) {
            this.solution = solution;
            this.contentLength = contentLength;
            this.writer = writer;

        }
    }

    static int getMaximumContentLength(final int[] array) {
        return Arrays.stream(array).map(n -> String.valueOf(n).length()).max().getAsInt();
    }

    static int[] parseOrGenerateArray(final Parameters flags) throws IOException {
        return Sorting.parseOrGenerateArray(flags, 0, Main.NUMBER_LIMIT - 1);
    }

    static int[] parseOrGenerateArray(
        final Parameters flags,
        final int lowestInt,
        final int highestInt
    ) throws IOException {
        return new ParserAndGenerator<int[]>(
            Sorting::parseArray,
            options -> Sorting.generateArray(options, lowestInt, highestInt)
        ).getResult(flags);
    }

    static void printSolution(final SortingSolution solutionData) throws IOException {
        LaTeXUtils.printTikzBeginning(TikZStyle.ARRAY, solutionData.writer);
        String anchor = null;
        for (final List<ItemWithTikZInformation<Integer>> list : solutionData.solution) {
            anchor =
                LaTeXUtils.printListAndReturnLowestLeftmostNodesName(
                    list,
                    Optional.ofNullable(anchor),
                    solutionData.contentLength,
                    solutionData.writer
                );
        }
        LaTeXUtils.printTikzEnd(solutionData.writer);
        Main.newLine(solutionData.writer);
    }

    static <E extends Exception> void sort(
        final AlgorithmInput input,
        final String name,
        final String operation,
        final String suffix,
        final Function<int[], List<List<ItemWithTikZInformation<Integer>>>> sort,
        final CheckedConsumer<SortingSolution, IOException> solutionPrinter
    ) throws IOException {
        final int[] array = Sorting.parseOrGenerateArray(input.options);
        final List<List<ItemWithTikZInformation<Integer>>> solution = sort.apply(array);
        final int contentLength = Sorting.getMaximumContentLength(array);
        if (input.options.containsKey(Flag.EXERCISE)) {
            Sorting.printExerciseText(
                name,
                operation,
                suffix,
                array,
                solution.size() - 1,
                contentLength,
                input.options,
                input.exerciseWriter
            );
        }
        solutionPrinter.accept(new SortingSolution(solution, contentLength, input.solutionWriter));
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

    private static int[] generateArray(final Parameters options, final int lowestInt, final int highestInt) {
        final int length;
        if (options.containsKey(Flag.LENGTH)) {
            length = Integer.parseInt(options.get(Flag.LENGTH));
        } else {
            length = Main.RANDOM.nextInt(16) + 5;
        }
        final int[] array = new int[length];
        final int range = highestInt - lowestInt + 1;
        for (int i = 0; i < array.length; i++) {
            array[i] = Main.RANDOM.nextInt(range) + lowestInt;
        }
        return array;
    }

    private static int[] parseArray(final BufferedReader reader, final Parameters options)
    throws IOException {
        final String[] numbers = reader.readLine().split(",");
        final int[] array = new int[numbers.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = Integer.parseInt(numbers[i].trim());
        }
        return array;
    }

    private static void printExerciseText(
        final String alg,
        final String op,
        final String additional,
        final int[] array,
        final int rows,
        final int contentLength,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Sortieren Sie das folgende Array mithilfe von ");
        writer.write(alg);
        writer.write(".");
        Main.newLine(writer);
        writer.write("Geben Sie dazu das Array nach jeder ");
        writer.write(op);
        writer.write(" an");
        writer.write(additional);
        writer.write(".\\\\[2ex]");
        Main.newLine(writer);
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
        LaTeXUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        String anchor =
            LaTeXUtils.printListAndReturnLowestLeftmostNodesName(
                Sorting.toTikZItems(array),
                Optional.empty(),
                contentLength,
                writer
            );
        for (int i = 0; i < rows; i++) {
            anchor =
                LaTeXUtils.printEmptyArrayAndReturnLeftmostNodesName(
                    array.length,
                    Optional.of(anchor),
                    contentLength,
                    writer
                );
        }
        LaTeXUtils.printTikzEnd(writer);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, writer);
    }

}
