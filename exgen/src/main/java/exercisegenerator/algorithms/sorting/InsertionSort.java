package exercisegenerator.algorithms.sorting;

import java.util.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.util.*;

public class InsertionSort implements Sorting {

    public static final InsertionSort INSTANCE = new InsertionSort();

    private InsertionSort() {}

    @Override
    public String additionalExerciseText() {
        return "";
    }

    @Override
    public String algorithmName() {
        return Algorithm.INSERTIONSORT.longName;
    }

    @Override
    public SortingSolution apply(final int[] initialArray) {
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
        return new SortingSolution(Sorting.getMaximumContentLength(initialArray), result);
    }

    @Override
    public String commandPrefix() {
        return "InsertionSort";
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
        return "Iteration der \\\"au\\ss{}eren Schleife";
    }

}
