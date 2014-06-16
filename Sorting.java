import java.io.*;
import java.util.*;

/**
 * This abstract class provides methods for sorting arrays and producing TikZ output showing intermediate steps of the 
 * sorting routines.
 * @author cryingshadow
 * @version $Id$
 */
public abstract class Sorting {

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
    public static int partition(Integer[] array, int start, int end) {
        int i = start - 1;
        int j = end;
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
        ArrayUtils.swap(array, i, j);
        ArrayUtils.swap(array, i, end);
        return i;
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
        Integer[] array,
        int start,
        int end,
        String anchor,
        boolean[] separate,
        boolean[] mark,
        BufferedWriter writer
    ) throws IOException {
        if (start < end) {
            int middle = partition(array, start, end);
            if (middle > 0) {
                separate[middle - 1] = true;
            }
            if (middle < array.length - 1) {
                separate[middle] = true;
            }
            Arrays.fill(mark, false);
            mark[middle] = true;
            Object[] firstStep =
                Sorting.quicksort(
                    array,
                    start,
                    middle - 1,
                    ArrayUtils.printArray(array, separate, mark, anchor, writer),
                    separate,
                    mark,
                    writer
                );
            Object[] secondStep =
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

    /**
     * Sorts the specified array using quicksort and outputs the solution as a TikZ picture to the specified writer.
     * @param array The array to sort.
     * @param writer The writer for outputting the solution.
     * @return The number of rows needed for the solution (excluding the original array).
     * @throws IOException If some error occurs while outputting the solution.
     */
    public static int quicksort(Integer[] array, BufferedWriter writer) throws IOException {
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        boolean[] separate = new boolean[array.length - 1];
        boolean[] mark = new boolean[array.length];
        Arrays.fill(separate, false);
        Arrays.fill(mark, false);
        int res =
            (Integer)quicksort(
                array,
                0,
                array.length - 1,
                ArrayUtils.printArray(array, separate, mark, null, writer),
                separate,
                mark,
                writer
            )[0];
        TikZUtils.printTikzEnd(writer);
        return res;
    }

    /**
     * Sorts the specified array using selectionsort and outputs the solution as a TikZ picture to the specified writer.
     * @param array The array to sort.
     * @param writer The writer for outputting the solution.
     * @return The number of rows needed for the solution (excluding the original array).
     * @throws IOException If some error occurs while outputting the solution.
     */
    public static int selectionsort(Integer[] array, BufferedWriter writer)
    throws IOException {
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        String anchor = ArrayUtils.printArray(array, null, null, null, writer);
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
                anchor = ArrayUtils.printArray(array, null, null, anchor, writer);
                res++;
            }
        }
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
        Integer[] array,
        int from,
        int to,
        String anchor,
        boolean[] separate,
        BufferedWriter writer
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
            newAnchor = ArrayUtils.printArray(array, separate, null, newAnchor, writer);
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
        Integer[] array,
        int from,
        int to,
        boolean[] separate,
        int step,
        BufferedWriter writer
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
            TikZUtils.printSamePageBeginning(res++, writer);
            ArrayUtils.printTree(array, to - 1, writer);
            TikZUtils.printProtectedNewline(writer);
            TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
            ArrayUtils.printArray(array, separate, null, null, writer);
            TikZUtils.printTikzEnd(writer);
            TikZUtils.printSamePageEnd(writer);
            TikZUtils.printVerticalSpace(res, writer);
            i = j;
        }
        return res;
    }

    /**
     * Sorts the specified array using heapsort and outputs the solution as a TikZ picture to the specified writer.
     * @param array The array to sort.
     * @param writer The writer for outputting the solution.
     * @return The number of rows needed for the solution (excluding the original array).
     * @throws IOException If some error occurs while outputting the solution.
     */
    public static int heapsort(Integer[] array, BufferedWriter writer) throws IOException {
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        String anchor = ArrayUtils.printArray(array, null, null, null, writer);
        int res = 0;
        boolean[] separate = new boolean[array.length - 1];
        Arrays.fill(separate, false);
        for (int i = array.length / 2; i > 0; i--) {
            Object[] heapified = heapify(array, i, array.length, anchor, separate, writer);
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
            Object[] heapified =
                heapify(
                    array,
                    1,
                    i,
                    ArrayUtils.printArray(array, separate, null, anchor, writer),
                    separate,
                    writer
                );
            res += (Integer)heapified[0];
            anchor = (String)heapified[1];
        }
        TikZUtils.printTikzEnd(writer);
        return res;
    }

    /**
         * Sorts the specified array using bubblesort and outputs the solution as a TikZ picture to the specified writer.
         * @param array The array to sort.
         * @param writer The writer for outputting the solution.
         * @return The number of rows needed for the solution (excluding the original array).
         * @throws IOException If some error occurs while outputting the solution.
         */
        public static int bubblesort(Integer[] array, BufferedWriter writer) throws IOException {
            TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
            String anchor = ArrayUtils.printArray(array, null, null, null, writer);
            int res = 0;
            int length = array.length;
            while (length > 1) {
                int n = 1;
                for (int i = 0; i < length - 1; i++) {
                    if (array[i] > array[i + 1]) {
                        ArrayUtils.swap(array, i, i + 1);
                        anchor = ArrayUtils.printArray(array, null, null, anchor, writer);
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
     * Sorts the specified array using heapsort and outputs the solution as a TikZ picture to the specified writer. 
     * Here, in addition to the arrays, a tree interpretation of the arrays is output.
     * @param array The array to sort.
     * @param writer The writer for outputting the solution.
     * @return The number of rows needed for the solution (excluding the original array).
     * @throws IOException If some error occurs while outputting the solution.
     */
    public static int heapsortWithTrees(Integer[] array, BufferedWriter writer) throws IOException {
        int step = 0;
        TikZUtils.printSamePageBeginning(step++, writer);
        ArrayUtils.printTree(array, array.length - 1, writer);
        TikZUtils.printProtectedNewline(writer);
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        ArrayUtils.printArray(array, null, null, null, writer);
        TikZUtils.printTikzEnd(writer);
        TikZUtils.printSamePageEnd(writer);
        TikZUtils.printVerticalSpace(step, writer);
        boolean[] separate = new boolean[array.length - 1];
        Arrays.fill(separate, false);
        for (int i = array.length / 2; i > 0; i--) {
            step = heapifyWithTrees(array, i, array.length, separate, step, writer);
        }
        for (int i = array.length - 1; i > 0; i--) {
            ArrayUtils.swap(array, 0, i);
            if (i < separate.length) {
                separate[i] = false;
            }
            TikZUtils.printSamePageBeginning(step++, writer);
            if (i > 1) {
                separate[i - 1] = true;
                ArrayUtils.printTree(array, i - 1, writer);
                TikZUtils.printProtectedNewline(writer);
            }
            TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
            ArrayUtils.printArray(array, separate, null, null, writer);
            TikZUtils.printTikzEnd(writer);
            TikZUtils.printSamePageEnd(writer);
            TikZUtils.printVerticalSpace(step, writer);
            step = heapifyWithTrees(array, 1, i, separate, step, writer);
        }
        return step - 1;
    }

    /**
     * Sorts the specified array using insertionsort and outputs the solution as a TikZ picture to the specified writer.
     * @param array The array to sort.
     * @param writer The writer for outputting the solution.
     * @return The number of rows needed for the solution (excluding the original array).
     * @throws IOException If some error occurs while outputting the solution.
     */
    public static int insertionsort(Integer[] array, BufferedWriter writer) throws IOException {
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        String anchor = ArrayUtils.printArray(array, null, null, null, writer);
        int res = 0;
        for (int i = 1; i < array.length; i++) {
            int insert = array[i];
            int j = i;
            while (j > 0 && array[j - 1] > insert) {
                array[j] = array[j - 1];
                j--;
            }
            array[j] = insert;
            anchor = ArrayUtils.printArray(array, null, null, anchor, writer);
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
    public static void merge(Integer[] array, int start, int middle, int end) {
        Integer[] copy = new Integer[end - start + 1];
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
        Integer[] array,
        int start,
        int end,
        String anchor,
        boolean[] separate,
        boolean[] mark,
        boolean printSplitting,
        BufferedWriter writer
    ) throws IOException {
        if (start < end) {
            int middle = (start + end) / 2;
            String newAnchor = anchor;
            if (printSplitting) {
                separate[middle] = true;
                newAnchor = ArrayUtils.printArray(array, separate, mark, newAnchor, writer);
            }
            Object[] firstStep =
                Sorting.mergesort(array, start, middle, newAnchor, separate, mark, printSplitting, writer);
            Object[] secondStep =
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
            merge(array, start, middle, end);
            separate[middle] = false;
            return new Object[]{
                ((Integer)firstStep[0]) + ((Integer)secondStep[0]) + 1,
                ArrayUtils.printArray(array, separate, null, (String)secondStep[1], writer)
            };
        } else {
            return new Object[]{0, anchor};
        }
    }

    /**
     * Sorts the specified array using mergesort and outputs the solution as a TikZ picture to the specified writer.
     * @param array The array to sort.
     * @param printSplitting Flag indicating whether to print the splitting in the beginning.
     * @param writer The writer for outputting the solution.
     * @return The number of rows needed for the solution (excluding the original array).
     * @throws IOException If some error occurs while outputting the solution.
     */
    public static int mergesort(Integer[] array, boolean printSplitting, BufferedWriter writer) throws IOException {
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
        boolean[] separate = new boolean[array.length - 1];
        Arrays.fill(separate, !printSplitting);
        boolean[] mark = new boolean[array.length];
        Arrays.fill(mark, true);
        int res =
            (Integer)mergesort(
                array,
                0,
                array.length - 1,
                ArrayUtils.printArray(array, separate, null, null, writer),
                separate,
                mark,
                printSplitting,
                writer
            )[0];
        TikZUtils.printTikzEnd(writer);
        return res;
    }

}
