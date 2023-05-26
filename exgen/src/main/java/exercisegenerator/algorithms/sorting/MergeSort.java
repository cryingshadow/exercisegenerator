package exercisegenerator.algorithms.sorting;

import java.io.*;
import java.util.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.util.*;

public class MergeSort implements AlgorithmImplementation {

    public static final MergeSort INSTANCE = new MergeSort();

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
        MergeSort.mergesort(array, 0, array.length - 1, separate, mark, printSplitting, result);
        return result;
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
        MergeSort.mergesort(array, start, middle, separate, mark, printSplitting, result);
        MergeSort.mergesort(array, middle + 1, end, separate, mark, printSplitting, result);
        MergeSort.merge(array, start, middle, end);
        separate[middle] = false;
        Arrays.fill(mark, false);
        Arrays.fill(mark, start, end + 1, true);
        result.add(Sorting.toTikZItems(array, separate, mark));
    }

    private MergeSort() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final boolean split = Algorithm.MERGESORT_SPLIT.name.equals(input.options.get(Flag.ALGORITHM));
        Sorting.sort(
            input,
            split ? Algorithm.MERGESORT_SPLIT.longName : Algorithm.MERGESORT.longName,
            "Merge-Operation",
            "",
            (array) -> MergeSort.mergesort(array, split),
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
