package exercisegenerator.algorithms.sorting;

import java.util.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.util.*;

public class MergeSort implements Sorting {

    public static final MergeSort INSTANCE = new MergeSort();

    static SortingSolution mergesort(
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
        MergeSort.mergesort(array, 0, array.length - 1, separate, mark, printSplitting, result);
        return new SortingSolution(Sorting.getMaximumContentLength(initialArray), result);
    }

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
        MergeSort.mergesort(array, start, middle, separate, mark, printSplitting, result);
        MergeSort.mergesort(array, middle + 1, end, separate, mark, printSplitting, result);
        MergeSort.merge(array, start, middle, end);
        separate[middle] = false;
        Arrays.fill(mark, false);
        Arrays.fill(mark, start, end + 1, true);
        result.add(Sorting.toTikZItems(array, separate, mark));
    }

    protected MergeSort() {}

    @Override
    public String additionalExerciseText() {
        return "";
    }

    @Override
    public String algorithmName() {
        return Algorithm.MERGESORT.longName;
    }

    @Override
    public SortingSolution apply(final int[] initialArray) {
        return MergeSort.mergesort(initialArray, false);
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
        return "Merge-Operation";
    }

}
