package exercisegenerator.algorithms.sorting;

import java.util.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.util.*;

public class SelectionSort implements Sorting {

    public static final SelectionSort INSTANCE = new SelectionSort();

    private SelectionSort() {}

    @Override
    public String additionalExerciseText() {
        return "";
    }

    @Override
    public String algorithmName() {
        return Algorithm.SELECTIONSORT.longName;
    }

    @Override
    public SortingSolution apply(final int[] initialArray) {
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
        return new SortingSolution(Sorting.getMaximumContentLength(initialArray), result);
    }

    @Override
    public String commandPrefix() {
        return "SelectionSort";
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
