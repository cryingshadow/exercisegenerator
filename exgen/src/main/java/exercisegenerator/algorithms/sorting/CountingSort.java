package exercisegenerator.algorithms.sorting;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.sorting.*;

public class CountingSort implements AlgorithmImplementation<CountingSortProblem, CountingSortSolution> {

    public static final CountingSort INSTANCE = new CountingSort();

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

    private static int[] parseOrGenerateLimits(
        final Parameters<Flag> options
    ) throws FileNotFoundException, IOException {
        if (options.containsKey(Flag.CAPACITY)) {
            return CountingSort.parseLimits(options.get(Flag.CAPACITY));
        } else if (options.containsKey(Flag.SOURCE)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.SOURCE)))) {
                reader.readLine();
                return CountingSort.parseLimits(reader.readLine());
            }
        } else if (options.containsKey(Flag.INPUT)) {
            try (BufferedReader reader = new BufferedReader(new StringReader(options.get(Flag.INPUT)))) {
                reader.readLine();
                return CountingSort.parseLimits(reader.readLine());
            }
        }
        return CountingSort.generateLimits();
    }

    private CountingSort() {}

    @Override
    public CountingSortSolution apply(final CountingSortProblem problem) {
        final int[] initialArray = problem.initialArray();
        final int lowestValue = problem.lowestValue();
        final int highestValue = problem.highestValue();
        final int[] array = new int[initialArray.length];
        final int[] count = new int[highestValue - lowestValue + 1];
        for (int i = 0; i < initialArray.length; i++) {
            count[initialArray[i] - lowestValue]++;
        }
        final List<ItemWithTikZInformation<Integer>> countArray = Sorting.toTikZItems(count);
        int index = 0;
        for (int i = 0; i < count.length; i++) {
            while (count[i] > 0) {
                array[index] = i + lowestValue;
                count[i]--;
                index++;
            }
        }
        return new CountingSortSolution(countArray, Sorting.toTikZItems(array));
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "8";
        return result;
    }

    @Override
    public CountingSortProblem parseOrGenerateProblem(final Parameters<Flag> options) throws IOException {
        final int[] limits = CountingSort.parseOrGenerateLimits(options);
        final int lowestValue = limits[0];
        final int highestValue = limits[1];
        final int[] array = Sorting.parseOrGenerateArray(options, lowestValue, highestValue);
        return new CountingSortProblem(array, lowestValue, highestValue);
    }

    @Override
    public void printExercise(
        final CountingSortProblem problem,
        final CountingSortSolution solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        final int contentLength = Sorting.getMaximumContentLength(problem.initialArray());
        writer.write("Sortieren Sie das folgende Array mit ganzen Zahlen von ");
        writer.write(String.valueOf(problem.lowestValue()));
        writer.write(" bis ");
        writer.write(String.valueOf(problem.highestValue()));
        writer.write(" mithilfe von Countingsort.");
        Main.newLine(writer);
        writer.write("Geben Sie dazu das Z\\\"ahlarray sowie das Ergebnisarray an.\\\\[2ex]");
        Main.newLine(writer);
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
        LaTeXUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        String anchor =
            LaTeXUtils.printListAndReturnLowestLeftmostNodesName(
                Sorting.toTikZItems(problem.initialArray()),
                Optional.empty(),
                contentLength,
                writer
            );
        anchor =
            LaTeXUtils.printEmptyArrayAndReturnLeftmostNodesName(
                solution.countArray().size(),
                Optional.of(anchor),
                contentLength,
                writer
            );
        anchor =
            LaTeXUtils.printEmptyArrayAndReturnLeftmostNodesName(
                solution.solutionArray().size(),
                Optional.of(anchor),
                contentLength,
                writer
            );
        LaTeXUtils.printTikzEnd(writer);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, writer);
    }

    @Override
    public void printSolution(
        final CountingSortProblem problem,
        final CountingSortSolution solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        final int contentLength = Sorting.getMaximumContentLength(problem.initialArray());
        LaTeXUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        String anchor =
            LaTeXUtils.printListAndReturnLowestLeftmostNodesName(
                Sorting.toTikZItems(problem.initialArray()),
                Optional.empty(),
                contentLength,
                writer
            );
        anchor =
            LaTeXUtils.printListAndReturnLowestLeftmostNodesName(
                solution.countArray(),
                Optional.of(anchor),
                contentLength,
                writer
            );
        LaTeXUtils.printListAndReturnLowestLeftmostNodesName(
            solution.solutionArray(),
            Optional.of(anchor),
            contentLength,
            writer
        );
        LaTeXUtils.printTikzEnd(writer);
        Main.newLine(writer);
    }

}
