package exercisegenerator.algorithms.sorting;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.sorting.*;

public class BucketSort implements AlgorithmImplementation<BucketSortProblem, BucketSortSolution> {

    private static class Counter {
        private int index = 0;
    }

    public static final BucketSort INSTANCE = new BucketSort();

    private static int[] generateLimitsAndBuckets() {
        return new int[] {0,99,10};
    }

    private static int[] parseLimitsAndBuckets(final String line) {
        final String[] split = line.split(";");
        final int[] result = new int[3];
        result[0] = Integer.parseInt(split[0]);
        result[1] = Integer.parseInt(split[1]);
        result[2] = Integer.parseInt(split[2]);
        return result;
    }

    private static int[] parseOrGenerateLimitsAndBuckets(final Parameters<Flag> options) throws IOException {
        if (options.containsKey(Flag.CAPACITY)) {
            return BucketSort.parseLimitsAndBuckets(options.get(Flag.CAPACITY));
        } else if (options.containsKey(Flag.SOURCE)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.SOURCE)))) {
                return BucketSort.parseLimitsAndBuckets(reader.readLine());
            }
        } else if (options.containsKey(Flag.INPUT)) {
            try (BufferedReader reader = new BufferedReader(new StringReader(options.get(Flag.INPUT)))) {
                return BucketSort.parseLimitsAndBuckets(reader.readLine());
            }
        }
        return BucketSort.generateLimitsAndBuckets();
    }

    private BucketSort() {}

    @Override
    public BucketSortSolution apply(final BucketSortProblem problem) {
        final int[] initialArray = problem.initialArray();
        final int numberOfBuckets = problem.numberOfBuckets();
        final int lowestValue = problem.lowestValue();
        final int highestValue = problem.highestValue();
        final int[] array = new int[initialArray.length];
        final IntegerList[] buckets = new IntegerList[numberOfBuckets];
        for (int i = 0; i < numberOfBuckets; i++) {
            buckets[i] = new IntegerList();
        }
        for (int i = 0; i < initialArray.length; i++) {
            final int normalized = initialArray[i] - lowestValue;
            final int bucket = normalized / ((highestValue - lowestValue + 1) / numberOfBuckets);
            buckets[bucket].add(initialArray[i]);
        }
        final Counter index = new Counter();
        for (int i = 0; i < numberOfBuckets; i++) {
            buckets[i].stream().sorted().forEach(number -> {
                array[index.index++] = number;
            });
        }
        return new BucketSortSolution(buckets, Sorting.toTikZItems(array));
    }

    @Override
    public String commandPrefix() {
        return "BucketSort";
    }

    @Override
    public BucketSortProblem generateProblem(final Parameters<Flag> options) {
        final int[] limitsAndBuckets = BucketSort.generateLimitsAndBuckets();
        final int lowestValue = limitsAndBuckets[0];
        final int highestValue = limitsAndBuckets[1];
        final int numberOfBuckets = limitsAndBuckets[2];
        return new BucketSortProblem(
            Sorting.generateArray(options, lowestValue, highestValue),
            lowestValue,
            highestValue,
            numberOfBuckets
        );
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "8";
        return result;
    }

    @Override
    public List<BucketSortProblem> parseProblems(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        final int[] limitsAndBuckets = BucketSort.parseOrGenerateLimitsAndBuckets(options);
        final int lowestValue = limitsAndBuckets[0];
        final int highestValue = limitsAndBuckets[1];
        final int numberOfBuckets = limitsAndBuckets[2];
        final List<BucketSortProblem> result = new ArrayList<BucketSortProblem>();
        reader.readLine();
        for (final int[] array : Sorting.parseArrays(reader, options)) {
            result.add(new BucketSortProblem(array, lowestValue, highestValue, numberOfBuckets));
        }
        return result;
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<BucketSortProblem> problems,
        final List<BucketSortSolution> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Sortieren Sie die folgenden Arrays mit ganzen Zahlen mithilfe von \\emphasize{Bucketsort}.");
        Main.newLine(writer);
        writer.write("Geben Sie dazu jeweils die Buckets unmittelbar vor deren Sortierung sowie das jeweilige ");
        writer.write("Ergebnisarray an.\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeSingleProblemInstance(
        final BucketSortProblem problem,
        final BucketSortSolution solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Sortieren Sie das folgende Array mit ganzen Zahlen von ");
        writer.write(String.valueOf(problem.lowestValue()));
        writer.write(" bis ");
        writer.write(String.valueOf(problem.highestValue()));
        writer.write(" mithilfe von \\emphasize{Bucketsort} mit ");
        writer.write(String.valueOf(problem.numberOfBuckets()));
        writer.write(" Buckets.");
        Main.newLine(writer);
        writer.write("Geben Sie dazu die Buckets unmittelbar vor deren Sortierung sowie das Ergebnisarray ");
        writer.write("an.\\\\[2ex]");
        Main.newLine(writer);
    }

    @Override
    public void printProblemInstance(
        final BucketSortProblem problem,
        final BucketSortSolution solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Kleinste m\\\"ogliche Zahl: ");
        writer.write(String.valueOf(problem.lowestValue()));
        writer.write("\\\\");
        Main.newLine(writer);
        writer.write("Gr\\\"o\\ss{}te m\\\"ogliche Zahl: ");
        writer.write(String.valueOf(problem.highestValue()));
        writer.write("\\\\");
        Main.newLine(writer);
        writer.write("Anzahl Buckets: ");
        writer.write(String.valueOf(problem.numberOfBuckets()));
        writer.write("\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printSolutionInstance(
        final BucketSortProblem problem,
        final BucketSortSolution solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        final int contentLength = Sorting.getMaximumContentLength(problem.initialArray());
        LaTeXUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        LaTeXUtils.printListAndReturnLowestLeftmostNodesName(
            Sorting.toTikZItems(problem.initialArray()),
            Optional.empty(),
            contentLength,
            writer
        );
        LaTeXUtils.printTikzEnd(writer);
        Main.newLine(writer);
        LaTeXUtils.printTikzBeginning(TikZStyle.BORDERLESS, writer);
        LaTeXUtils.printVerticalStringArray(
            IntegerList.toVerticalStringArray(solution.buckets()),
            null,
            null,
            null,
            writer
        );
        LaTeXUtils.printTikzEnd(writer);
        Main.newLine(writer);
        LaTeXUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        LaTeXUtils.printListAndReturnLowestLeftmostNodesName(
            solution.solutionArray(),
            Optional.empty(),
            contentLength,
            writer
        );
        LaTeXUtils.printTikzEnd(writer);
    }

    @Override
    public void printSolutionSpace(
        final BucketSortProblem problem,
        final BucketSortSolution solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
        LaTeXUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        LaTeXUtils.printListAndReturnLowestLeftmostNodesName(
            Sorting.toTikZItems(problem.initialArray()),
            Optional.empty(),
            Sorting.getMaximumContentLength(problem.initialArray()),
            writer
        );
        LaTeXUtils.printTikzEnd(writer);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, writer);
    }

}
