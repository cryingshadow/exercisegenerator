package exercisegenerator.algorithms.sorting;

import java.util.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.util.*;

public class BubbleSort implements Sorting {

    public static final BubbleSort INSTANCE = new BubbleSort();

    private BubbleSort() {}

    @Override
    public String additionalExerciseText() {
        return "";
    }

    @Override
    public String algorithmName() {
        return Algorithm.BUBBLESORT.longName;
    }

    @Override
    public SortingSolution apply(final int[] initialArray) {
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
        return new SortingSolution(Sorting.getMaximumContentLength(initialArray), result);
    }

    @Override
    public String commandPrefix() {
        return "BubbleSort";
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

}
