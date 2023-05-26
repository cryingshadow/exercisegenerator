package exercisegenerator.algorithms.sorting;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.sorting.Sorting.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.util.*;

public class HeapSort implements AlgorithmImplementation {

    public static final HeapSort INSTANCE = new HeapSort();

    public static List<List<ItemWithTikZInformation<Integer>>> heapsort(final int[] initialArray) {
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
        return result;
    }

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

    /**
     * Establishes the heap property on the branch starting at index <code>from</code> when interpreting the array up
     * to index <code>to</code> as a heap by sinking the element at index <code>from</code> as far as necessary.
     * Moreover, the current state of the array is added to the solution after each swap operation.
     */
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

    private static void printSolutionWithTrees(final SortingSolution solutionData) throws IOException {
        int step = 0;
        for (final List<ItemWithTikZInformation<Integer>> list : solutionData.solution) {
            LaTeXUtils.printSamePageBeginning(step++, LaTeXUtils.COL_WIDTH, solutionData.writer);
            HeapSort.printTree(list, solutionData.writer);
            LaTeXUtils.printProtectedNewline(solutionData.writer);
            LaTeXUtils.printTikzBeginning(TikZStyle.ARRAY, solutionData.writer);
            LaTeXUtils.printListAndReturnLeftmostNodesName(
                list,
                Optional.empty(),
                solutionData.contentLength,
                solutionData.writer
            );
            LaTeXUtils.printTikzEnd(solutionData.writer);
            LaTeXUtils.printSamePageEnd(solutionData.writer);
            HeapSort.printVerticalSpaceForStep(step, solutionData.writer);
        }
    }

    /**
     * Prints the specified array interpreted as binary tree up to the specified index.
     * @param array The array.
     * @param to The index to which the tree should be printed.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
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

    /**
     * Prints the specified array interpreted as binary tree from the specified start index (i.e., it prints the
     * subtree starting with the element at the specified start index) to the specified end index.
     * @param array The array.
     * @param start The start index.
     * @param end The end index.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
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

    /**
     * Prints vertical space
     * @param step The next evaluation step.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
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
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        Sorting.sort(
            input,
            Algorithm.HEAPSORT.longName,
            "Swap-Operation",
            "",
            HeapSort::heapsort,
            Algorithm.HEAPSORT_TREE.name.equals(input.options.get(Flag.ALGORITHM)) ?
                HeapSort::printSolutionWithTrees :
                    Sorting::printSolution
        );
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
