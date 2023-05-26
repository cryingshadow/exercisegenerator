package exercisegenerator.algorithms.sorting;

import java.io.*;
import java.util.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.util.*;

public class InsertionSort implements AlgorithmImplementation {

    public static final InsertionSort INSTANCE = new InsertionSort();

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

    private InsertionSort() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        Sorting.sort(
            input,
            Algorithm.INSERTIONSORT.longName,
            "Iteration der \\\"au\\ss{}eren Schleife",
            "",
            InsertionSort::insertionsort,
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
