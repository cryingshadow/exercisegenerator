package exercisegenerator.algorithms.sorting;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;

public class BucketSort implements AlgorithmImplementation {

    private static class Counter {
        private int index = 0;
    }

    public static final BucketSort INSTANCE = new BucketSort();

    public static Pair<IntegerList[], List<ItemWithTikZInformation<Integer>>> bucketsort(
        final int[] initialArray,
        final int lowestInt,
        final int highestInt,
        final int numberOfBuckets
    ) {
        final int[] array = new int[initialArray.length];
        final IntegerList[] buckets = new IntegerList[numberOfBuckets];
        for (int i = 0; i < numberOfBuckets; i++) {
            buckets[i] = new IntegerList();
        }
        for (int i = 0; i < initialArray.length; i++) {
            final int normalized = initialArray[i] - lowestInt;
            final int bucket = normalized / ((highestInt - lowestInt + 1) / numberOfBuckets);
            buckets[bucket].add(initialArray[i]);
        }
        final Counter index = new Counter();
        for (int i = 0; i < numberOfBuckets; i++) {
            buckets[i].stream().sorted().forEach(number -> {
                array[index.index++] = number;
            });
        }
        return new Pair<IntegerList[], List<ItemWithTikZInformation<Integer>>>(buckets, Sorting.toTikZItems(array));
    }

    private static int[] generateLimits() {
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

    private static int[] parseOrGenerateLimitsAndBuckets(final AlgorithmInput input) throws IOException {
        if (input.options.containsKey(Flag.CAPACITY)) {
            return BucketSort.parseLimitsAndBuckets(input.options.get(Flag.CAPACITY));
        } else if (input.options.containsKey(Flag.SOURCE)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(input.options.get(Flag.SOURCE)))) {
                reader.readLine();
                return BucketSort.parseLimitsAndBuckets(reader.readLine());
            }
        } else if (input.options.containsKey(Flag.INPUT)) {
            try (BufferedReader reader = new BufferedReader(new StringReader(input.options.get(Flag.INPUT)))) {
                reader.readLine();
                return BucketSort.parseLimitsAndBuckets(reader.readLine());
            }
        }
        return BucketSort.generateLimits();
    }

    private static void printExerciseText(
        final int[] array,
        final String lowestInt,
        final String highestInt,
        final String numberOfBuckets,
        final int contentLength,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Sortieren Sie das folgende Array mit ganzen Zahlen von ");
        writer.write(lowestInt);
        writer.write(" bis ");
        writer.write(highestInt);
        writer.write(" mithilfe von Bucketsort mit ");
        writer.write(numberOfBuckets);
        writer.write(" Buckets.");
        Main.newLine(writer);
        writer.write("Geben Sie dazu die Buckets vor deren Sortierung sowie das Ergebnisarray an.\\\\[2ex]");
        Main.newLine(writer);
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
        LaTeXUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        LaTeXUtils.printListAndReturnLeftmostNodesName(
            Sorting.toTikZItems(array),
            Optional.empty(),
            contentLength,
            writer
        );
        LaTeXUtils.printTikzEnd(writer);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, writer);
    }

    private static void printSolutionText(
        final int[] array,
        final IntegerList[] buckets,
        final List<ItemWithTikZInformation<Integer>> solution,
        final int contentLength,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        LaTeXUtils.printListAndReturnLeftmostNodesName(
            Sorting.toTikZItems(array),
            Optional.empty(),
            contentLength,
            writer
        );
        LaTeXUtils.printTikzEnd(writer);
        Main.newLine(writer);
        LaTeXUtils.printTikzBeginning(TikZStyle.BORDERLESS, writer);
        LaTeXUtils.printVerticalStringArray(
            IntegerList.toVerticalStringArray(buckets),
            null,
            null,
            null,
            writer
        );
        LaTeXUtils.printTikzEnd(writer);
        Main.newLine(writer);
        LaTeXUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        LaTeXUtils.printListAndReturnLeftmostNodesName(
            solution,
            Optional.empty(),
            contentLength,
            writer
        );
        LaTeXUtils.printTikzEnd(writer);
        Main.newLine(writer);
    }

    private BucketSort() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final int[] limitsAndBuckets = BucketSort.parseOrGenerateLimitsAndBuckets(input);
        final int lowestInt = limitsAndBuckets[0];
        final int highestInt = limitsAndBuckets[1];
        final int buckets = limitsAndBuckets[2];
        final int[] array = Sorting.parseOrGenerateArray(input.options, lowestInt, highestInt);
        final Pair<IntegerList[], List<ItemWithTikZInformation<Integer>>> solution =
            BucketSort.bucketsort(array, lowestInt, highestInt, buckets);
        final int contentLength =
            Math.max(Sorting.getMaximumContentLength(array), Sorting.getMaximumContentLength(limitsAndBuckets));
        if (input.options.containsKey(Flag.EXERCISE)) {
            BucketSort.printExerciseText(
                array,
                String.valueOf(lowestInt),
                String.valueOf(highestInt),
                String.valueOf(buckets),
                contentLength,
                input.options,
                input.exerciseWriter
            );
        }
        BucketSort.printSolutionText(array, solution.x, solution.y, contentLength, input.solutionWriter);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "8";
        return result;
    }

}
