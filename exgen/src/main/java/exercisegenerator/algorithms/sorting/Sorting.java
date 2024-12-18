package exercisegenerator.algorithms.sorting;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;

interface Sorting extends AlgorithmImplementation<int[], SortingSolution> {

    static int getMaximumContentLength(final int[] array) {
        return Arrays.stream(array).map(n -> String.valueOf(n).length()).max().getAsInt();
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
        final int length = AlgorithmImplementation.parseOrGenerateLength(5, 20, options);
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

    @Override
    default public int[] parseOrGenerateProblem(final Parameters options) throws IOException {
        return Sorting.parseOrGenerateArray(options, 0, Main.NUMBER_LIMIT - 1);
    }

//    static <E extends Exception> void sort(
//        final AlgorithmInput input,
//        final String name,
//        final String operation,
//        final String suffix,
//        final Function<int[], List<List<ItemWithTikZInformation<Integer>>>> sort,
//        final CheckedConsumer<SortingSolution, IOException> solutionPrinter
//    ) throws IOException {
//        final int[] array = Sorting.parseOrGenerateArray(input.options);
//        final List<List<ItemWithTikZInformation<Integer>>> solution = sort.apply(array);
//        final int contentLength = Sorting.getMaximumContentLength(array);
//        if (input.options.containsKey(Flag.EXERCISE)) {
//            Sorting.printExerciseText(
//                name,
//                operation,
//                suffix,
//                array,
//                solution.size() - 1,
//                contentLength,
//                input.options,
//                input.exerciseWriter
//            );
//        }
//        solutionPrinter.accept(new SortingSolution(solution, contentLength, input.solutionWriter));
//    }

    @Override
    default public void printExercise(
        final int[] problem,
        final SortingSolution solution,
        final Parameters options,
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

    @Override
    default public void printSolution(
        final int[] problem,
        final SortingSolution solution,
        final Parameters options,
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
        Main.newLine(writer);
    }

    String additionalExerciseText();

    String algorithmName();

    String operation();

}
