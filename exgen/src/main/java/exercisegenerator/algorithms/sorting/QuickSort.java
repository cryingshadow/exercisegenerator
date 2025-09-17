package exercisegenerator.algorithms.sorting;

import java.util.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.util.*;

public class QuickSort implements Sorting {

    private static enum PartitionMode {

        EQUAL_ALWAYS_SWAP,

        EQUAL_LEFT,

        EQUAL_NEVER_SWAP,

        EQUAL_RIGHT

    }

    public static final QuickSort INSTANCE = new QuickSort();

    private static final PartitionMode PARTITION_MODE = PartitionMode.EQUAL_RIGHT; // TODO parameterize

    private static boolean forwardLeft(final int left, final int pivot, final PartitionMode mode) {
        switch (mode) {
        case EQUAL_LEFT:
        case EQUAL_NEVER_SWAP:
            return left <= pivot;
        case EQUAL_ALWAYS_SWAP:
        case EQUAL_RIGHT:
            return left < pivot;
        default:
            throw new IllegalStateException("Unknown partition mode!");
        }
    }

    private static boolean forwardRight(final int right, final int pivot, final PartitionMode mode) {
        switch (mode) {
        case EQUAL_NEVER_SWAP:
        case EQUAL_RIGHT:
            return right >= pivot;
        case EQUAL_ALWAYS_SWAP:
        case EQUAL_LEFT:
            return right > pivot;
        default:
            throw new IllegalStateException("Unknown partition mode!");
        }
    }

    private static int partition(final int[] array, final int from, final int to) {
        int left = from - 1;
        int right = to;
        final int pivot = array[to];
        final PartitionMode mode = QuickSort.PARTITION_MODE;
        while (left < right) {
            left++;
            while (QuickSort.forwardLeft(array[left], pivot, mode)) {
                left++;
            }
            right--;
            while (right > from && QuickSort.forwardRight(array[right], pivot, mode)) {
                right--;
            }
            ArrayUtils.swap(array, left, right);
        }
        ArrayUtils.swap(array, left, right);
        ArrayUtils.swap(array, left, to);
        return left;
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
    public String additionalExerciseText() {
        return " und markieren Sie das jeweils verwendete Pivot-Element";
    }

    @Override
    public String algorithmName() {
        return Algorithm.QUICKSORT.longName;
    }

    @Override
    public SortingSolution apply(final int[] initialArray) {
        final List<List<ItemWithTikZInformation<Integer>>> result =
            new ArrayList<List<ItemWithTikZInformation<Integer>>>();
        result.add(Sorting.toTikZItems(initialArray));
        final int[] array = ArrayUtils.copy(initialArray);
        final boolean[] separate = new boolean[array.length - 1];
        final boolean[] mark = new boolean[array.length];
        Arrays.fill(separate, false);
        Arrays.fill(mark, false);
        QuickSort.quicksort(array, 0, array.length - 1, separate, mark, result);
        return new SortingSolution(Sorting.getMaximumContentLength(initialArray), result);
    }

    @Override
    public String commandPrefix() {
        return "QuickSort";
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
        return "Partition-Operation";
    }

}
