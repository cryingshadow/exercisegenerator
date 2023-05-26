package exercisegenerator.algorithms.sorting;

import java.io.*;
import java.util.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.util.*;

public class QuickSort implements AlgorithmImplementation {

    /**
     * Mode indicating how to treat elements equal to the pivot element during partitioning.
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

    public static final QuickSort INSTANCE = new QuickSort();

    /**
     * Flag indicating whether elements equal to the pivot element should end up in the left partition.
     */
    private static final PartitionMode PARTITION_MODE = PartitionMode.EQUAL_RIGHT; // TODO parameterize

    public static List<List<ItemWithTikZInformation<Integer>>> quicksort(final int[] initialArray) {
        final List<List<ItemWithTikZInformation<Integer>>> result =
            new ArrayList<List<ItemWithTikZInformation<Integer>>>();
        result.add(Sorting.toTikZItems(initialArray));
        final int[] array = ArrayUtils.copy(initialArray);
        final boolean[] separate = new boolean[array.length - 1];
        final boolean[] mark = new boolean[array.length];
        Arrays.fill(separate, false);
        Arrays.fill(mark, false);
        QuickSort.quicksort(array, 0, array.length - 1, separate, mark, result);
        return result;
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
        switch (QuickSort.PARTITION_MODE) {
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
        final int middle = QuickSort.partition(array, start, end);
        if (middle > 0) {
            separate[middle - 1] = true;
        }
        if (middle < array.length - 1) {
            separate[middle] = true;
        }
        Arrays.fill(mark, false);
        mark[middle] = true;
        result.add(Sorting.toTikZItems(array, separate, mark));
        QuickSort.quicksort(array, start, middle - 1, separate, mark, result);
        QuickSort.quicksort(array, middle + 1, end, separate, mark, result);
    }

    private QuickSort() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        Sorting.sort(
            input,
            Algorithm.QUICKSORT.longName,
            "Partition-Operation",
            " und markieren Sie das jeweils verwendete Pivot-Element",
            QuickSort::quicksort,
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
