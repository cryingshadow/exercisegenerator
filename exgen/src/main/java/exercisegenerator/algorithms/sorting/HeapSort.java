package exercisegenerator.algorithms.sorting;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.util.*;

public class HeapSort implements Sorting {

    public static final HeapSort INSTANCE = new HeapSort();

    private static int getSeparationIndex(final List<ItemWithTikZInformation<Integer>> list) {
        int result = -1;
        for (final ItemWithTikZInformation<Integer> item : list) {
            if (item.separateBefore) {
                return result;
            }
            result++;
        }
        return result;
    }

    private static void heapify(
        final int[] array,
        final int from,
        final int to,
        final boolean[] separate,
        final List<List<ItemWithTikZInformation<Integer>>> result
    ) {
        int i = from;
        while (i <= to / 2) {
            int j = 2 * i;
            if (j < to && array[j] > array[j - 1]) {
                j++;
            }
            if (array[j - 1] <= array[i - 1]) {
                break;
            }
            ArrayUtils.swap(array, j - 1, i - 1);
            result.add(Sorting.toTikZItems(array, separate));
            i = j;
        }
    }

    private static void printSolutionWithTrees(
        final SortingSolution solutionData,
        final BufferedWriter writer
    ) throws IOException {
        int step = 0;
        for (final List<ItemWithTikZInformation<Integer>> list : solutionData.solution()) {
            LaTeXUtils.printSamePageBeginning(step++, LaTeXUtils.COL_WIDTH, writer);
            HeapSort.printTree(list, writer);
            LaTeXUtils.printProtectedNewline(writer);
            LaTeXUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
            LaTeXUtils.printListAndReturnLowestLeftmostNodesName(
                list,
                Optional.empty(),
                solutionData.contentLength(),
                writer
            );
            LaTeXUtils.printTikzEnd(writer);
            LaTeXUtils.printSamePageEnd(writer);
            HeapSort.printVerticalSpaceForStep(step, writer);
        }
    }

    private static void printTree(
        final List<ItemWithTikZInformation<Integer>> list,
        final BufferedWriter writer
    ) throws IOException {
        final int to = HeapSort.getSeparationIndex(list);
        if (to < 0) {
            return;
        }
        LaTeXUtils.printTikzBeginning(TikZStyle.TREE, writer);
        if (to > 0) {
            writer.write("\\Tree");
            HeapSort.printTree(list, 0, to, writer);
        } else {
            writer.write(
                String.format("\\node[circle,draw=black,thick,inner sep=5pt] {%d};", list.get(0).optionalContent.get())
            );
        }
        Main.newLine(writer);
        LaTeXUtils.printTikzEnd(writer);
    }

    private static void printTree(
        final List<ItemWithTikZInformation<Integer>> list,
        final int start,
        final int end,
        final BufferedWriter writer
    ) throws IOException {
        final int next = 2 * start + 1;
        final int valueAtStart = list.get(start).optionalContent.get();
        if (next <= end) {
            writer.write(" [." + valueAtStart);
            HeapSort.printTree(list, next, end, writer);
            if (next + 1 <= end) {
                HeapSort.printTree(list, next + 1, end, writer);
            }
            writer.write(" ]");
        } else {
            writer.write(" " + valueAtStart);
        }
    }

    private static void printVerticalSpaceForStep(final int step, final BufferedWriter writer) throws IOException {
        if (step % 3 == 0) {
            Main.newLine(writer);
            writer.write("~\\\\");
            Main.newLine(writer);
            Main.newLine(writer);
        }
    }

    private HeapSort() {}

    @Override
    public String additionalExerciseText() {
        return "";
    }

    @Override
    public String algorithmName() {
        return Algorithm.HEAPSORT.longName;
    }

    @Override
    public SortingSolution apply(final int[] initialArray) {
        final List<List<ItemWithTikZInformation<Integer>>> result =
            new ArrayList<List<ItemWithTikZInformation<Integer>>>();
        result.add(Sorting.toTikZItems(initialArray));
        final int[] array = ArrayUtils.copy(initialArray);
        final boolean[] separate = new boolean[array.length - 1];
        Arrays.fill(separate, false);
        for (int i = array.length / 2; i > 0; i--) {
            HeapSort.heapify(array, i, array.length, separate, result);
        }
        for (int i = array.length - 1; i > 0; i--) {
            ArrayUtils.swap(array, 0, i);
            if (i < separate.length) {
                separate[i] = false;
            }
            if (i > 0) { // TODO check whether 1 is better here
                separate[i - 1] = true;
            }
            result.add(Sorting.toTikZItems(array, separate));
            HeapSort.heapify(array, 1, i, separate, result);
        }
        return new SortingSolution(Sorting.getMaximumContentLength(initialArray), result);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public String operation() {
        return "Swap-Operation";
    }

    @Override
    public void printSolution(
        final int[] problem,
        final SortingSolution solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        if (Algorithm.HEAPSORT_TREE.name.equals(options.get(Flag.ALGORITHM))) {
            HeapSort.printSolutionWithTrees(solution, writer);
        } else {
            Sorting.super.printSolution(problem, solution, options, writer);
        }
    }

}
