package exercisegenerator.algorithms;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.util.*;

/**
 * This abstract class provides methods for sorting arrays and producing TikZ output showing intermediate steps of the
 * sorting routines.
 * @author Thomas Stroeder
 * @version 1.1.0
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
    private static final PartitionMode PARTITION_MODE = PartitionMode.EQUAL_RIGHT;

    public static void bubblesort(final AlgorithmInput input) throws IOException {
        Sorting.sort(
            input,
            Algorithm.BUBBLESORT.longName,
            "Swap-Operation",
            "",
            Sorting::bubblesort
        );
    }

    /**
     * Sorts the specified array using bubblesort and outputs the solution as a TikZ picture to the specified writer.
     * @param array The array to sort.
     * @param writer The writer for outputting the solution.
     * @return The number of rows needed for the solution (excluding the original array).
     * @throws IOException If some error occurs while outputting the solution.
     */
    public static int bubblesort(final Integer[] array, final BufferedWriter writer) throws IOException {
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        String anchor =
            TikZUtils.printIntegerArrayAndReturnLeftMostNodesName(
                array,
                Optional.empty(),
                Algorithm.DEFAULT_CONTENT_LENGTH,
                writer
            );
        int res = 0;
        int length = array.length;
        while (length > 1) {
            int n = 1;
            for (int i = 0; i < length - 1; i++) {
                if (array[i] > array[i + 1]) {
                    ArrayUtils.swap(array, i, i + 1);
                    anchor =
                        TikZUtils.printIntegerArrayAndReturnLeftMostNodesName(
                            array,
                            Optional.of(anchor),
                            Algorithm.DEFAULT_CONTENT_LENGTH,
                            writer
                        );
                    res++;
                    n = i + 1;
                }
            }
            length = n;
        }
        //        for (int i = 0; i < array.length - 1; i++) {
        //            for (int j = array.length - 1; j > i; j--) {
        //                if (array[j] < array[j - 1]) {
        //                    SortingExercise.swap(array, j, j - 1);
        //                    anchor = SortingExercise.printArray(array, null, null, anchor, writer);
        //                    res++;
        //                }
        //            }
        //        }
        TikZUtils.printTikzEnd(writer);
        return res;
    }

    /**
     * Establishes the heap property on the branch starting at index <code>from</code> when interpreting the array up
     * to index <code>to</code> as a heap by sinking the element at index <code>from</code> as far as necessary.
     * Moreover, the current state of the array is output to writer after each swap operation.
     * @param array The array.
     * @param from The index of the element to sink.
     * @param to The length of the array part to be interpreted as a heap.
     * @param anchor The name of the left-most node of the recent row in the TikZ array output.
     * @param separate Indicates which nodes in the array output should be separated horizontally.
     * @param writer The writer to send the output to.
     * @return The number of rows needed for the solution and the name of the left-most node of the most recent row in
     *         the TikZ array output.
     * @throws IOException If some error occurs during solution output.
     */
    public static Object[] heapify(
        final Integer[] array,
        final int from,
        final int to,
        final String anchor,
        final boolean[] separate,
        final BufferedWriter writer
    ) throws IOException {
        int i = from;
        String newAnchor = anchor;
        int res = 0;
        while (i <= to / 2) {
            int j = 2 * i;
            if (j < to && array[j] > array[j - 1]) {
                j++;
            }
            if (array[j - 1] <= array[i - 1]) {
                break;
            }
            ArrayUtils.swap(array, j - 1, i - 1);
            newAnchor =
                TikZUtils.printIntegerArrayAndReturnLeftMostNodesName(
                    array,
                    separate,
                    Optional.ofNullable(newAnchor),
                    Algorithm.DEFAULT_CONTENT_LENGTH,
                    writer
                );
            res++;
            i = j;
        }
        return new Object[]{res, newAnchor};
    }

    /**
     * Establishes the heap property on the branch starting at index <code>from</code> when interpreting the array up
     * to index <code>to</code> as a heap by sinking the element at index <code>from</code> as far as necessary.
     * Moreover, the current state of the array is output both as tree and array to writer after each swap operation.
     * @param array The array.
     * @param from The index of the element to sink.
     * @param to The length of the array part to be interpreted as a heap.
     * @param separate Indicates which nodes in the array output should be separated horizontally.
     * @param step The current evaluation step.
     * @param writer The writer to send the output to.
     * @return The next evaluation step after this heapification has been performed.
     * @throws IOException If some error occurs during solution output.
     */
    public static int heapifyWithTrees(
        final Integer[] array,
        final int from,
        final int to,
        final boolean[] separate,
        final int step,
        final BufferedWriter writer
    ) throws IOException {
        int res = step;
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
            TikZUtils.printSamePageBeginning(res++, TikZUtils.COL_WIDTH, writer);
            TikZUtils.printTree(array, to - 1, writer);
            TikZUtils.printProtectedNewline(writer);
            TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
            TikZUtils.printIntegerArrayAndReturnLeftMostNodesName(
                array,
                separate,
                Optional.empty(),
                Algorithm.DEFAULT_CONTENT_LENGTH,
                writer
            );
            TikZUtils.printTikzEnd(writer);
            TikZUtils.printSamePageEnd(writer);
            TikZUtils.printVerticalSpaceForStep(res, writer);
            i = j;
        }
        return res;
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

    /**
     * Sorts the specified array using heapsort and outputs the solution as a TikZ picture to the specified writer.
     * @param array The array to sort.
     * @param writer The writer for outputting the solution.
     * @return The number of rows needed for the solution (excluding the original array).
     * @throws IOException If some error occurs while outputting the solution.
     */
    public static int heapsort(final Integer[] array, final BufferedWriter writer) throws IOException {
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        String anchor =
            TikZUtils.printIntegerArrayAndReturnLeftMostNodesName(
                array,
                Optional.empty(),
                Algorithm.DEFAULT_CONTENT_LENGTH,
                writer
            );
        int res = 0;
        final boolean[] separate = new boolean[array.length - 1];
        Arrays.fill(separate, false);
        for (int i = array.length / 2; i > 0; i--) {
            final Object[] heapified = Sorting.heapify(array, i, array.length, anchor, separate, writer);
            res += (Integer)heapified[0];
            anchor = (String)heapified[1];
        }
        for (int i = array.length - 1; i > 0; i--) {
            ArrayUtils.swap(array, 0, i);
            res++;
            if (i < separate.length) {
                separate[i] = false;
            }
            if (i > 0) {
                separate[i - 1] = true;
            }
            final Object[] heapified =
                Sorting.heapify(
                    array,
                    1,
                    i,
                    TikZUtils.printIntegerArrayAndReturnLeftMostNodesName(
                        array,
                        separate,
                        Optional.ofNullable(anchor),
                        Algorithm.DEFAULT_CONTENT_LENGTH,
                        writer
                    ),
                    separate,
                    writer
                );
            res += (Integer)heapified[0];
            anchor = (String)heapified[1];
        }
        TikZUtils.printTikzEnd(writer);
        return res;
    }

    public static void heapsortWithTrees(final AlgorithmInput input) throws IOException {
        Sorting.sort(
            input,
            Algorithm.HEAPSORT_TREE.longName,
            "Swap-Operation",
            "",
            Sorting::heapsortWithTrees
        );
    }

    /**
     * Sorts the specified array using heapsort and outputs the solution as a TikZ picture to the specified writer.
     * Here, in addition to the arrays, a tree interpretation of the arrays is output.
     * @param array The array to sort.
     * @param writer The writer for outputting the solution.
     * @return The number of rows needed for the solution (excluding the original array).
     * @throws IOException If some error occurs while outputting the solution.
     */
    public static int heapsortWithTrees(final Integer[] array, final BufferedWriter writer) throws IOException {
        int step = 0;
        TikZUtils.printSamePageBeginning(step++, TikZUtils.COL_WIDTH, writer);
        TikZUtils.printTree(array, array.length - 1, writer);
        TikZUtils.printProtectedNewline(writer);
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        TikZUtils.printIntegerArrayAndReturnLeftMostNodesName(
            array,
            Optional.empty(),
            Algorithm.DEFAULT_CONTENT_LENGTH,
            writer
        );
        TikZUtils.printTikzEnd(writer);
        TikZUtils.printSamePageEnd(writer);
        TikZUtils.printVerticalSpaceForStep(step, writer);
        final boolean[] separate = new boolean[array.length - 1];
        Arrays.fill(separate, false);
        for (int i = array.length / 2; i > 0; i--) {
            step = Sorting.heapifyWithTrees(array, i, array.length, separate, step, writer);
        }
        for (int i = array.length - 1; i > 0; i--) {
            ArrayUtils.swap(array, 0, i);
            if (i < separate.length) {
                separate[i] = false;
            }
            TikZUtils.printSamePageBeginning(step++, TikZUtils.COL_WIDTH, writer);
            if (i > 1) {
                separate[i - 1] = true;
                TikZUtils.printTree(array, i - 1, writer);
                TikZUtils.printProtectedNewline(writer);
            }
            TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
            TikZUtils.printIntegerArrayAndReturnLeftMostNodesName(
                array,
                separate,
                Optional.empty(),
                Algorithm.DEFAULT_CONTENT_LENGTH,
                writer
            );
            TikZUtils.printTikzEnd(writer);
            TikZUtils.printSamePageEnd(writer);
            TikZUtils.printVerticalSpaceForStep(step, writer);
            step = Sorting.heapifyWithTrees(array, 1, i, separate, step, writer);
        }
        return step - 1;
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

    /**
     * Sorts the specified array using insertionsort and outputs the solution as a TikZ picture to the specified writer.
     * @param array The array to sort.
     * @param writer The writer for outputting the solution.
     * @return The number of rows needed for the solution (excluding the original array).
     * @throws IOException If some error occurs while outputting the solution.
     */
    public static int insertionsort(final Integer[] array, final BufferedWriter writer) throws IOException {
        final int contentLength = Sorting.getMaximumContentLength(array);
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        String anchor =
            TikZUtils.printIntegerArrayAndReturnLeftMostNodesName(
                array,
                Optional.empty(),
                contentLength,
                writer
            );
        int res = 0;
        for (int i = 1; i < array.length; i++) {
            final int insert = array[i];
            int j = i;
            while (j > 0 && array[j - 1] > insert) {
                array[j] = array[j - 1];
                j--;
            }
            array[j] = insert;
            anchor =
                TikZUtils.printIntegerArrayAndReturnLeftMostNodesName(
                    array,
                    Optional.of(anchor),
                    contentLength,
                    writer
                );
            res++;
        }
        TikZUtils.printTikzEnd(writer);
        return res;
    }

    /**
     * Merges two sorted array parts (between start and middle and between middle + 1 and end) to one sorted array part
     * (from start to end).
     * @param array The array.
     * @param start The start index.
     * @param middle The middle index.
     * @param end The end index.
     */
    public static void merge(final Integer[] array, final int start, final int middle, final int end) {
        final Integer[] copy = new Integer[end - start + 1];
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

    public static void mergesort(final AlgorithmInput input) throws IOException {
        Sorting.sort(
            input,
            Algorithm.MERGESORT.longName,
            "Merge-Operation",
            "",
            (array, writer) -> Sorting.mergesort(array, false, writer)
        );
    }

    /**
     * Sorts the specified array using mergesort and outputs the solution as a TikZ picture to the specified writer.
     * @param array The array to sort.
     * @param printSplitting Flag indicating whether to print the splitting in the beginning.
     * @param writer The writer for outputting the solution.
     * @return The number of rows needed for the solution (excluding the original array).
     * @throws IOException If some error occurs while outputting the solution.
     */
    public static int mergesort(final Integer[] array, final boolean printSplitting, final BufferedWriter writer) throws IOException {
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        final boolean[] separate = new boolean[array.length - 1];
        Arrays.fill(separate, !printSplitting);
        final boolean[] mark = new boolean[array.length];
        Arrays.fill(mark, true);
        final int res =
            (Integer)Sorting.mergesort(
                array,
                0,
                array.length - 1,
                TikZUtils.printIntegerArrayAndReturnLeftMostNodesName(
                    array,
                    separate,
                    Optional.empty(),
                    Algorithm.DEFAULT_CONTENT_LENGTH,
                    writer
                ),
                separate,
                mark,
                printSplitting,
                writer
            )[0];
        TikZUtils.printTikzEnd(writer);
        return res;
    }

    /**
     * The actual recursive mergesort algorithm. It sorts the array part from start to end using mergesort while
     * outputting the solution as a TikZ picture to the specified writer.
     * @param array The array.
     * @param start The start index.
     * @param end The end index.
     * @param anchor The name of the left-most node of the recent row in the TikZ array output.
     * @param separate Indicates which nodes should be separated horizontally in the array output.
     * @param mark An array of equal length to <code>array</code> and all entries set to true. Just passed to avoid
     *             re-allocation of the same array over and over again.
     * @param printSplitting Flag indicating whether to print the splitting in the beginning.
     * @param writer The writer to send the output to.
     * @return The number of rows needed for the solution and the name of the left-most node of the most recent row in
     *         the TikZ array output.
     * @throws IOException If some error occurs during solution output.
     */
    public static Object[] mergesort(
        final Integer[] array,
        final int start,
        final int end,
        final String anchor,
        final boolean[] separate,
        final boolean[] mark,
        final boolean printSplitting,
        final BufferedWriter writer
    ) throws IOException {
        if (start < end) {
            final int middle = (start + end) / 2;
            String newAnchor = anchor;
            if (printSplitting) {
                separate[middle] = true;
                newAnchor =
                    TikZUtils.printIntegerArrayAndReturnLeftMostNodesName(
                        array,
                        separate,
                        mark,
                        Optional.ofNullable(newAnchor),
                        Algorithm.DEFAULT_CONTENT_LENGTH,
                        writer
                    );
            }
            final Object[] firstStep =
                Sorting.mergesort(array, start, middle, newAnchor, separate, mark, printSplitting, writer);
            final Object[] secondStep =
                Sorting.mergesort(
                    array,
                    middle + 1,
                    end,
                    (String)firstStep[1],
                    separate,
                    mark,
                    printSplitting,
                    writer
                );
            Sorting.merge(array, start, middle, end);
            separate[middle] = false;
            return new Object[]{
                ((Integer)firstStep[0]) + ((Integer)secondStep[0]) + 1,
                TikZUtils.printIntegerArrayAndReturnLeftMostNodesName(
                    array,
                    separate,
                    Optional.ofNullable((String)secondStep[1]),
                    Algorithm.DEFAULT_CONTENT_LENGTH,
                    writer
                )
            };
        } else {
            return new Object[]{0, anchor};
        }
    }

    public static void mergesortSplit(final AlgorithmInput input) throws IOException {
        Sorting.sort(
            input,
            Algorithm.MERGESORT_SPLIT.longName,
            "Merge-Operation",
            "",
            (array, writer) -> Sorting.mergesort(array, true, writer)
        );
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
    public static int partition(final Integer[] array, final int start, final int end) {
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

    public static void quicksort(final AlgorithmInput input) throws IOException {
        Sorting.sort(
            input,
            Algorithm.QUICKSORT.longName,
            "Partition-Operation",
            " und markieren Sie das jeweils verwendete Pivot-Element",
            Sorting::quicksort
        );
    }

    /**
     * Sorts the specified array using quicksort and outputs the solution as a TikZ picture to the specified writer.
     * @param array The array to sort.
     * @param writer The writer for outputting the solution.
     * @return The number of rows needed for the solution (excluding the original array).
     * @throws IOException If some error occurs while outputting the solution.
     */
    public static int quicksort(final Integer[] array, final BufferedWriter writer) throws IOException {
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        final boolean[] separate = new boolean[array.length - 1];
        final boolean[] mark = new boolean[array.length];
        Arrays.fill(separate, false);
        Arrays.fill(mark, false);
        final int res =
            (Integer)Sorting.quicksort(
                array,
                0,
                array.length - 1,
                TikZUtils.printIntegerArrayAndReturnLeftMostNodesName(
                    array,
                    separate,
                    mark,
                    Optional.empty(),
                    Algorithm.DEFAULT_CONTENT_LENGTH,
                    writer
                ),
                separate,
                mark,
                writer
            )[0];
        TikZUtils.printTikzEnd(writer);
        return res;
    }

    /**
     * The actual quicksort algorithm. It sorts the array part from start to end using quicksort while outputting the
     * solution as a TikZ picture to the specified writer.
     * @param array The array.
     * @param start The start index.
     * @param end The end index.
     * @param anchor The name of the left-most node of the recent row in the TikZ array output.
     * @param separate Indicates which nodes should be separated horizontally in the array output.
     * @param mark Indicates which nodes should be marked by a grey background in the TikZ array output (these are the
     *             Pivot elements used).
     * @param writer The writer to send the output to.
     * @return The number of rows needed for the solution and the name of the left-most node of the most recent row in
     *         the TikZ array output.
     * @throws IOException If some error occurs during solution output.
     */
    public static Object[] quicksort(
        final Integer[] array,
        final int start,
        final int end,
        final String anchor,
        final boolean[] separate,
        final boolean[] mark,
        final BufferedWriter writer
    ) throws IOException {
        if (start < end) {
            final int middle = Sorting.partition(array, start, end);
            if (middle > 0) {
                separate[middle - 1] = true;
            }
            if (middle < array.length - 1) {
                separate[middle] = true;
            }
            Arrays.fill(mark, false);
            mark[middle] = true;
            final Object[] firstStep =
                Sorting.quicksort(
                    array,
                    start,
                    middle - 1,
                    TikZUtils.printIntegerArrayAndReturnLeftMostNodesName(
                        array,
                        separate,
                        mark,
                        Optional.ofNullable(anchor),
                        Algorithm.DEFAULT_CONTENT_LENGTH,
                        writer
                    ),
                    separate,
                    mark,
                    writer
                );
            final Object[] secondStep =
                Sorting.quicksort(
                    array,
                    middle + 1,
                    end,
                    (String)firstStep[1],
                    separate,
                    mark,
                    writer
                );
            return new Object[]{((Integer)secondStep[0]) + ((Integer)firstStep[0]) + 1, secondStep[1]};
        } else {
            return new Object[]{0, anchor};
        }
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

    /**
     * Sorts the specified array using selectionsort and outputs the solution as a TikZ picture to the specified writer.
     * @param array The array to sort.
     * @param writer The writer for outputting the solution.
     * @return The number of rows needed for the solution (excluding the original array).
     * @throws IOException If some error occurs while outputting the solution.
     */
    public static int selectionsort(final Integer[] array, final BufferedWriter writer)
    throws IOException {
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        String anchor =
            TikZUtils.printIntegerArrayAndReturnLeftMostNodesName(
                array,
                Optional.empty(),
                Algorithm.DEFAULT_CONTENT_LENGTH,
                writer
            );
        int res = 0;
        for (int i = 0; i < array.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < array.length; j++) {
                if (array[j] < array[min]) {
                    min = j;
                }
            }
            if (i != min) {
                ArrayUtils.swap(array, i, min);
                anchor =
                    TikZUtils.printIntegerArrayAndReturnLeftMostNodesName(
                        array,
                        Optional.of(anchor),
                        Algorithm.DEFAULT_CONTENT_LENGTH,
                        writer
                    );
                res++;
            }
        }
        TikZUtils.printTikzEnd(writer);
        return res;
    }

    private static int getMaximumContentLength(final Integer[] array) {
        return Arrays.stream(array).mapToInt(n -> String.valueOf(n).length()).max().getAsInt();
    }

    private static int getRows(final Map<Flag, String> options) {
        return !Main.STUDENT_MODE && options.containsKey(Flag.LENGTH) ?
            Integer.parseInt(options.get(Flag.LENGTH)) :
                0;
    }

    private static Integer[] parseOrGenerateArray(final Map<Flag, String> flags) throws IOException {
        return new ParserAndGenerator<Integer[]>(
            (final BufferedReader reader, final Map<Flag, String> options) -> {
                final String[] numbers = reader.readLine().split(",");
                final Integer[] array = new Integer[numbers.length];
                for (int i = 0; i < array.length; i++) {
                    array[i] = Integer.parseInt(numbers[i].trim());
                }
                return array;
            },
            (final Map<Flag, String> options) -> {
                final int length;
                final Random gen = new Random();
                if (options.containsKey(Flag.LENGTH)) {
                    length = Integer.parseInt(options.get(Flag.LENGTH));
                } else {
                    length = gen.nextInt(16) + 5;
                }
                final Integer[] array = new Integer[length];
                for (int i = 0; i < array.length; i++) {
                    array[i] = gen.nextInt(Main.NUMBER_LIMIT);
                }
                return array;
            }
        ).getResult(flags);
    }

    private static <E extends Exception> void sort(
        final AlgorithmInput input,
        final String name,
        final String operation,
        final String suffix,
        final CheckedBiFunction<Integer[], BufferedWriter, Integer, E> sort
    ) throws E {
        try {
            final Integer[] array = Sorting.parseOrGenerateArray(input.options);
            final Optional<String> anchor =
                Sorting.sortPreProcessing(array, name, operation, suffix, input.options, input.exerciseWriter);
            final int rows = Sorting.getRows(input.options) + sort.apply(array, input.solutionWriter);
            Sorting.sortPostProcessing(array, rows, anchor, input.options, input.exerciseWriter);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Prints empty rows for solving the exercise on the sheet directly.
     * @param array The sorted array.
     * @param rows The number of empty rows to be printed in the exercise.
     * @param anchorParam The name of the node used to orient the empty rows.
     * @param options The parsed flags.
     * @param exerciseWriter The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private static void sortPostProcessing(
        final Integer[] array,
        final int rows,
        final Optional<String> anchorParam,
        final Map<Flag, String> options,
        final BufferedWriter exerciseWriter
    ) throws IOException {
        final int contentLength = Sorting.getMaximumContentLength(array);
        if (options.containsKey(Flag.EXERCISE)) {
            String anchor =
                TikZUtils.printEmptyArrayAndReturnLeftmostNodesName(
                    array.length,
                    anchorParam,
                    contentLength,
                    exerciseWriter
                );
            for (int i = 1; i < rows; i++) {
                anchor =
                    TikZUtils.printEmptyArrayAndReturnLeftmostNodesName(
                        array.length,
                        Optional.of(anchor),
                        contentLength,
                        exerciseWriter
                    );
            }
            TikZUtils.printTikzEnd(exerciseWriter);
        }
        TikZUtils.printEndIf(exerciseWriter);
    }

    /**
     * Prints the exercise text to the specified writer.
     * @param array The array to sort.
     * @param alg The name of the sorting algorithm.
     * @param op The operation after which the state of the array is to be given as intermediate result.
     * @param additional Additional instruction on how to write up the solution.
     * @param options The parsed flags.
     * @param exerciseWriter The writer to send the output to.
     * @return The name of the node used to orient empty rows in the exercise text.
     * @throws IOException If some error occurs during output.
     */
    private static Optional<String> sortPreProcessing(
        final Integer[] array,
        final String alg,
        final String op,
        final String additional,
        final Map<Flag, String> options,
        final BufferedWriter exerciseWriter
    ) throws IOException {
        if (!options.containsKey(Flag.EXERCISE)) {
            return Optional.empty();
        }
        exerciseWriter.write("Sortieren Sie das folgende Array mithilfe von ");
        exerciseWriter.write(alg);
        exerciseWriter.write(".");
        Main.newLine(exerciseWriter);
        exerciseWriter.write("Geben Sie dazu das Array nach jeder ");
        exerciseWriter.write(op);
        exerciseWriter.write(" an");
        exerciseWriter.write(additional);
        exerciseWriter.write(".\\\\[2ex]");
        Main.newLine(exerciseWriter);
        TikZUtils.printToggleForSolutions(exerciseWriter);
        TikZUtils.printElse(exerciseWriter);
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, exerciseWriter);
        return Optional.of(
            TikZUtils.printIntegerArrayAndReturnLeftMostNodesName(
                array,
                Optional.empty(),
                Sorting.getMaximumContentLength(array),
                exerciseWriter
            )
        );
    }

}
