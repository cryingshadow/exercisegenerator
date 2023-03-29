package exercisegenerator.algorithms;

import java.io.*;
import java.util.*;
import java.util.function.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.util.*;

/**
 * This abstract class provides methods for sorting arrays and producing TikZ output showing intermediate steps of the
 * sorting routines.
 */
public abstract class Sorting {

    /**
     * Mode indicating how to treat elements equal to the pivot element during partitioning.
     * @author Thomas Stroeder
     * @version 1.0
     */
    private static enum PartitionMode {

        /**
         * Elements equal to the pivot element are always swapped.
         */
        EQUAL_ALWAYS_SWAP,

        /**
         * Elements equal to the pivot element end up in the left partition.
         */
        EQUAL_LEFT,

        /**
         * Elements equal to the pivot element are never swapped.
         */
        EQUAL_NEVER_SWAP,

        /**
         * Elements equal to the pivot element end up in the right partition.
         */
        EQUAL_RIGHT

    }

    /**
     * Flag indicating whether elements equal to the pivot element should end up in the left partition.
     */
    private static final PartitionMode PARTITION_MODE = PartitionMode.EQUAL_RIGHT; // TODO parameterize

    public static void bubblesort(final AlgorithmInput input) throws IOException {
        Sorting.sort(
            input,
            Algorithm.BUBBLESORT.longName,
            "Swap-Operation",
            "",
            Sorting::bubblesort
        );
    }

    public static List<List<ItemWithTikZInformation<Integer>>> bubblesort(final int[] initialArray) {
        final List<List<ItemWithTikZInformation<Integer>>> result =
            new ArrayList<List<ItemWithTikZInformation<Integer>>>();
        result.add(Sorting.toTikZItems(initialArray));
        final int[] array = ArrayUtils.copy(initialArray);
        int unsortedLength = array.length;
        while (unsortedLength > 1) {
            int lowestIndexNotSwapped = 1;
            for (int i = 0; i < unsortedLength - 1; i++) {
                if (array[i] > array[i + 1]) {
                    ArrayUtils.swap(array, i, i + 1);
                    result.add(Sorting.toTikZItems(array));
                    lowestIndexNotSwapped = i + 1;
                }
            }
            unsortedLength = lowestIndexNotSwapped;
        }
        return result;
    }

    public static String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    public static void heapsort(final AlgorithmInput input) throws IOException {
        Sorting.sort(
            input,
            Algorithm.HEAPSORT.longName,
            "Swap-Operation",
            "",
            Sorting::heapsort
        );
    }

    public static List<List<ItemWithTikZInformation<Integer>>> heapsort(final int[] initialArray) {
        final List<List<ItemWithTikZInformation<Integer>>> result =
            new ArrayList<List<ItemWithTikZInformation<Integer>>>();
        result.add(Sorting.toTikZItems(initialArray));
        final int[] array = ArrayUtils.copy(initialArray);
        final boolean[] separate = new boolean[array.length - 1];
        Arrays.fill(separate, false);
        for (int i = array.length / 2; i > 0; i--) {
            Sorting.heapify(array, i, array.length, separate, result);
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
            Sorting.heapify(array, 1, i, separate, result);
        }
        return result;
    }

    public static void heapsortWithTrees(final AlgorithmInput input) throws IOException {
        Sorting.sort(
            input,
            Algorithm.HEAPSORT_TREE.longName,
            "Swap-Operation",
            "",
            Sorting::heapsort
        );
    }

    public static void insertionsort(final AlgorithmInput input) throws IOException {
        Sorting.sort(
            input,
            Algorithm.INSERTIONSORT.longName,
            "Iteration der \\\"au\\ss{}eren Schleife",
            "",
            Sorting::insertionsort
        );
    }

    public static List<List<ItemWithTikZInformation<Integer>>> insertionsort(final int[] initialArray) {
        final List<List<ItemWithTikZInformation<Integer>>> result =
            new ArrayList<List<ItemWithTikZInformation<Integer>>>();
        result.add(Sorting.toTikZItems(initialArray));
        final int[] array = ArrayUtils.copy(initialArray);
        for (int i = 1; i < array.length; i++) {
            final int insert = array[i];
            int j = i;
            while (j > 0 && array[j - 1] > insert) {
                array[j] = array[j - 1];
                j--;
            }
            array[j] = insert;
            result.add(Sorting.toTikZItems(array));
        }
        return result;
    }

    public static void mergesort(final AlgorithmInput input) throws IOException {
        Sorting.sort(
            input,
            Algorithm.MERGESORT.longName,
            "Merge-Operation",
            "",
            (array) -> Sorting.mergesort(array, false)
        );
    }

    public static List<List<ItemWithTikZInformation<Integer>>> mergesort(
        final int[] initialArray,
        final boolean printSplitting
    ) {
        final List<List<ItemWithTikZInformation<Integer>>> result =
            new ArrayList<List<ItemWithTikZInformation<Integer>>>();
        final int[] array = ArrayUtils.copy(initialArray);
        final boolean[] separate = new boolean[array.length - 1];
        final boolean[] mark = new boolean[array.length];
        Arrays.fill(separate, false);
        Arrays.fill(mark, false);
        result.add(Sorting.toTikZItems(initialArray, separate, mark));
        Sorting.mergesort(array, 0, array.length - 1, separate, mark, printSplitting, result);
        return result;
    }

    public static void mergesortSplit(final AlgorithmInput input) throws IOException {
        Sorting.sort(
            input,
            Algorithm.MERGESORT_SPLIT.longName,
            "Merge-Operation",
            "",
            (array) -> Sorting.mergesort(array, true)
        );
    }

    public static void quicksort(final AlgorithmInput input) throws IOException {
        Sorting.sort(
            input,
            Algorithm.QUICKSORT.longName,
            "Partition-Operation",
            " und markieren Sie das jeweils verwendete Pivot-Element",
            Sorting::quicksort
        );
    }

    public static List<List<ItemWithTikZInformation<Integer>>> quicksort(final int[] initialArray) {
        final List<List<ItemWithTikZInformation<Integer>>> result =
            new ArrayList<List<ItemWithTikZInformation<Integer>>>();
        result.add(Sorting.toTikZItems(initialArray));
        final int[] array = ArrayUtils.copy(initialArray);
        final boolean[] separate = new boolean[array.length - 1];
        final boolean[] mark = new boolean[array.length];
        Arrays.fill(separate, false);
        Arrays.fill(mark, false);
        Sorting.quicksort(array, 0, array.length - 1, separate, mark, result);
        return result;
    }

    public static void selectionsort(final AlgorithmInput input) throws IOException {
        Sorting.sort(
            input,
            Algorithm.SELECTIONSORT.longName,
            "Swap-Operation",
            "",
            Sorting::selectionsort
        );
    }

    public static List<List<ItemWithTikZInformation<Integer>>> selectionsort(final int[] initialArray) {
        final List<List<ItemWithTikZInformation<Integer>>> result =
            new ArrayList<List<ItemWithTikZInformation<Integer>>>();
        result.add(Sorting.toTikZItems(initialArray));
        final int[] array = ArrayUtils.copy(initialArray);
        for (int i = 0; i < array.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < array.length; j++) {
                if (array[j] < array[min]) {
                    min = j;
                }
            }
            if (i != min) {
                ArrayUtils.swap(array, i, min);
                result.add(Sorting.toTikZItems(array));
            }
        }
        return result;
    }

    private static int[] generateArray(final Parameters options) {
        final int length;
        final Random gen = new Random();
        if (options.containsKey(Flag.LENGTH)) {
            length = Integer.parseInt(options.get(Flag.LENGTH));
        } else {
            length = gen.nextInt(16) + 5;
        }
        final int[] array = new int[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = gen.nextInt(Main.NUMBER_LIMIT);
        }
        return array;
    }

    private static int getMaximumContentLength(final int[] array) {
        return Arrays.stream(array).map(n -> String.valueOf(n).length()).max().getAsInt();
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

    /**
     * Merges two sorted array parts (between start and middle and between middle + 1 and end) to one sorted array part
     * (from start to end).
     * @param array The array.
     * @param start The start index.
     * @param middle The middle index.
     * @param end The end index.
     */
    private static void merge(final int[] array, final int start, final int middle, final int end) {
        final int[] copy = new int[end - start + 1];
        int i = 0;
        int j = start;
        int k = middle + 1;
        while (j <= middle && k <= end) {
            if (array[j] <= array[k]) {
                copy[i++] = array[j++];
            } else {
                copy[i++] = array[k++];
            }
        }
        while (j <= middle) {
            copy[i++] = array[j++];
        }
        while (k <= end) {
            copy[i++] = array[k++];
        }
        System.arraycopy(copy, 0, array, start, copy.length);
    }

    private static void mergesort(
        final int[] array,
        final int start,
        final int end,
        final boolean[] separate,
        final boolean[] mark,
        final boolean printSplitting,
        final List<List<ItemWithTikZInformation<Integer>>> result
    ) {
        if (start >= end) {
            return;
        }
        final int middle = (start + end) / 2;
        if (printSplitting) {
            separate[middle] = true;
            result.add(Sorting.toTikZItems(array, separate));
        }
        Sorting.mergesort(array, start, middle, separate, mark, printSplitting, result);
        Sorting.mergesort(array, middle + 1, end, separate, mark, printSplitting, result);
        Sorting.merge(array, start, middle, end);
        separate[middle] = false;
        Arrays.fill(mark, false);
        Arrays.fill(mark, start, end + 1, true);
        result.add(Sorting.toTikZItems(array, separate, mark));
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

    private static int[] parseOrGenerateArray(final Parameters flags) throws IOException {
        return new ParserAndGenerator<int[]>(Sorting::parseArray, Sorting::generateArray).getResult(flags);
    }

    /**
     * Partitions the array part between <code>start</code> and <code>end</code> using the element at <code>end</code>
     * as Pivot element. It returns the resulting index of the Pivot element. So after this method call, all elements
     * from <code>start</code> to the returned index minus one are less than or equal to the element at the returned
     * index and all elements from the returned index plus one to <code>end</code> are greater than or equal to the
     * element at the returned index.
     * @param array The array.
     * @param start The start index.
     * @param end The end index.
     * @return The index of the Pivot element after partitioning.
     */
    private static int partition(final int[] array, final int start, final int end) {
        int i = start - 1;
        int j = end;
        switch (Sorting.PARTITION_MODE) {
            case EQUAL_ALWAYS_SWAP:
                while (i < j) {
                    i++;
                    while (array[i] < array[end]) {
                        i++;
                    }
                    j--;
                    while (j > start - 1 && array[j] > array[end]) {
                        j--;
                    }
                    ArrayUtils.swap(array, i, j);
                }
                break;
            case EQUAL_LEFT:
                while (i < j) {
                    i++;
                    while (array[i] <= array[end]) {
                        i++;
                    }
                    j--;
                    while (j > start - 1 && array[j] > array[end]) {
                        j--;
                    }
                    ArrayUtils.swap(array, i, j);
                }
                break;
            case EQUAL_NEVER_SWAP:
                while (i < j) {
                    i++;
                    while (array[i] <= array[end]) {
                        i++;
                    }
                    j--;
                    while (j > start - 1 && array[j] >= array[end]) {
                        j--;
                    }
                    ArrayUtils.swap(array, i, j);
                }
                break;
            case EQUAL_RIGHT:
                while (i < j) {
                    i++;
                    while (array[i] < array[end]) {
                        i++;
                    }
                    j--;
                    while (j > start - 1 && array[j] >= array[end]) {
                        j--;
                    }
                    ArrayUtils.swap(array, i, j);
                }
                break;
            default:
                throw new IllegalStateException("Unknown partition mode!");
        }
        ArrayUtils.swap(array, i, j);
        ArrayUtils.swap(array, i, end);
        return i;
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
            LaTeXUtils.printListAndReturnLeftmostNodesName(
                Sorting.toTikZItems(array),
                Optional.empty(),
                Sorting.getMaximumContentLength(array),
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

    private static void printSolution(
        final List<List<ItemWithTikZInformation<Integer>>> solution,
        final int contentLength,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        String anchor = null;
        for (final List<ItemWithTikZInformation<Integer>> list : solution) {
            anchor =
                LaTeXUtils.printListAndReturnLeftmostNodesName(
                    list,
                    Optional.ofNullable(anchor),
                    contentLength,
                    writer
                );
        }
        LaTeXUtils.printTikzEnd(writer);
        Main.newLine(writer);
    }

    private static void printSolutionWithTrees(
        final List<List<ItemWithTikZInformation<Integer>>> solution,
        final int contentLength,
        final BufferedWriter writer
    ) throws IOException {
        int step = 0;
        for (final List<ItemWithTikZInformation<Integer>> list : solution) {
            LaTeXUtils.printSamePageBeginning(step++, LaTeXUtils.COL_WIDTH, writer);
            Sorting.printTree(list, writer);
            LaTeXUtils.printProtectedNewline(writer);
            LaTeXUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
            LaTeXUtils.printListAndReturnLeftmostNodesName(list, Optional.empty(), contentLength, writer);
            LaTeXUtils.printTikzEnd(writer);
            LaTeXUtils.printSamePageEnd(writer);
            Sorting.printVerticalSpaceForStep(step, writer);
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
        final int to = Sorting.getSeparationIndex(list);
        if (to < 0) {
            return;
        }
        LaTeXUtils.printTikzBeginning(TikZStyle.TREE, writer);
        if (to > 0) {
            writer.write("\\Tree");
            Sorting.printTree(list, 0, to, writer);
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
            Sorting.printTree(list, next, end, writer);
            if (next + 1 <= end) {
                Sorting.printTree(list, next + 1, end, writer);
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

    private static void quicksort(
        final int[] array,
        final int start,
        final int end,
        final boolean[] separate,
        final boolean[] mark,
        final List<List<ItemWithTikZInformation<Integer>>> result
    ) {
        if (start >= end) {
            return;
        }
        final int middle = Sorting.partition(array, start, end);
        if (middle > 0) {
            separate[middle - 1] = true;
        }
        if (middle < array.length - 1) {
            separate[middle] = true;
        }
        Arrays.fill(mark, false);
        mark[middle] = true;
        result.add(Sorting.toTikZItems(array, separate, mark));
        Sorting.quicksort(array, start, middle - 1, separate, mark, result);
        Sorting.quicksort(array, middle + 1, end, separate, mark, result);
    }

    private static <E extends Exception> void sort(
        final AlgorithmInput input,
        final String name,
        final String operation,
        final String suffix,
        final Function<int[], List<List<ItemWithTikZInformation<Integer>>>> sort
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
        if (input.options.get(Flag.ALGORITHM).equals(Algorithm.HEAPSORT_TREE.name)) {
            Sorting.printSolutionWithTrees(solution, contentLength, input.solutionWriter);
        } else {
            Sorting.printSolution(solution, contentLength, input.solutionWriter);
        }
    }

    private static List<ItemWithTikZInformation<Integer>> toTikZItems(final int[] array) {
        return Arrays.stream(array)
            .mapToObj((final int i) -> new ItemWithTikZInformation<Integer>(Optional.of(i), false, false))
            .toList();
    }

    private static List<ItemWithTikZInformation<Integer>> toTikZItems(final int[] array, final boolean[] separate) {
        final List<ItemWithTikZInformation<Integer>> result = new ArrayList<ItemWithTikZInformation<Integer>>();
        result.add(new ItemWithTikZInformation<Integer>(Optional.of(array[0]), false));
        for (int i = 1; i < array.length; i++) {
            result.add(new ItemWithTikZInformation<Integer>(Optional.of(array[i]), separate[i - 1]));
        }
        return result;
    }

    private static List<ItemWithTikZInformation<Integer>> toTikZItems(
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

}
