package exercisegenerator.algorithms.sorting;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.sorting.Sorting.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;

public class CountingSort implements AlgorithmImplementation {

    public static final CountingSort INSTANCE = new CountingSort();

    public static List<List<ItemWithTikZInformation<Integer>>> countingsort(
        final int[] initialArray,
        final int lowestInt,
        final int highestInt
    ) {
        final List<List<ItemWithTikZInformation<Integer>>> result =
            new ArrayList<List<ItemWithTikZInformation<Integer>>>();
        result.add(Sorting.toTikZItems(initialArray));
        final int[] array = new int[initialArray.length];
        final int[] count = new int[highestInt - lowestInt + 1];
        for (int i = 0; i < initialArray.length; i++) {
            count[initialArray[i] - lowestInt]++;
        }
        result.add(Sorting.toTikZItems(count));
        int index = 0;
        for (int i = 0; i < count.length; i++) {
            while (count[i] > 0) {
                array[index] = i + lowestInt;
                count[i]--;
                index++;
            }
        }
        result.add(Sorting.toTikZItems(array));
        return result;
    }

    private static int[] generateLimits() {
        return new int[] {0,9};
    }

    private static int[] parseLimits(final String line) {
        final String[] split = line.split(";");
        final int[] result = new int[2];
        result[0] = Integer.parseInt(split[0]);
        result[1] = Integer.parseInt(split[1]);
        return result;
    }

    private static int[] parseOrGenerateLimits(final AlgorithmInput input) throws FileNotFoundException, IOException {
        if (input.options.containsKey(Flag.CAPACITY)) {
            return CountingSort.parseLimits(input.options.get(Flag.CAPACITY));
        } else if (input.options.containsKey(Flag.SOURCE)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(input.options.get(Flag.SOURCE)))) {
                reader.readLine();
                return CountingSort.parseLimits(reader.readLine());
            }
        } else if (input.options.containsKey(Flag.INPUT)) {
            try (BufferedReader reader = new BufferedReader(new StringReader(input.options.get(Flag.INPUT)))) {
                reader.readLine();
                return CountingSort.parseLimits(reader.readLine());
            }
        }
        return CountingSort.generateLimits();
    }

    private static void printExerciseText(
        final List<List<ItemWithTikZInformation<Integer>>> result,
        final String lowestInt,
        final String highestInt,
        final int contentLength,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Sortieren Sie das folgende Array mit ganzen Zahlen von ");
        writer.write(lowestInt);
        writer.write(" bis ");
        writer.write(highestInt);
        writer.write(" mithilfe von Countingsort.");
        Main.newLine(writer);
        writer.write("Geben Sie dazu das Z\\\"ahlarray sowie das Ergebnisarray an.\\\\[2ex]");
        Main.newLine(writer);
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
        LaTeXUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        String anchor =
            LaTeXUtils.printListAndReturnLowestLeftmostNodesName(
                result.get(0),
                Optional.empty(),
                contentLength,
                writer
            );
        anchor =
            LaTeXUtils.printEmptyArrayAndReturnLeftmostNodesName(
                result.get(1).size(),
                Optional.of(anchor),
                contentLength,
                writer
            );
        anchor =
            LaTeXUtils.printEmptyArrayAndReturnLeftmostNodesName(
                result.get(2).size(),
                Optional.of(anchor),
                contentLength,
                writer
            );
        LaTeXUtils.printTikzEnd(writer);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, writer);
    }

    private CountingSort() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final int[] limits = CountingSort.parseOrGenerateLimits(input);
        final int lowestInt = limits[0];
        final int highestInt = limits[1];
        final int[] array = Sorting.parseOrGenerateArray(input.options, lowestInt, highestInt);
        final List<List<ItemWithTikZInformation<Integer>>> solution =
            CountingSort.countingsort(array, lowestInt, highestInt);
        final int contentLength =
            Math.max(Sorting.getMaximumContentLength(array), Sorting.getMaximumContentLength(limits));
        if (input.options.containsKey(Flag.EXERCISE)) {
            CountingSort.printExerciseText(
                solution,
                String.valueOf(lowestInt),
                String.valueOf(highestInt),
                contentLength,
                input.options,
                input.exerciseWriter
            );
        }
        Sorting.printSolution(new SortingSolution(solution, contentLength, input.solutionWriter));
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "8";
        return result;
    }

}
