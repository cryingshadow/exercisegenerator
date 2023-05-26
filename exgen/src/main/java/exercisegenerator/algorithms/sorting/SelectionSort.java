package exercisegenerator.algorithms.sorting;

import java.io.*;
import java.util.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.util.*;

public class SelectionSort implements AlgorithmImplementation {

    public static final SelectionSort INSTANCE = new SelectionSort();

    public static List<List<ItemWithTikZInformation<Integer>>> selectionsort(final int[] initialArray) {
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
        return result;
    }

    private SelectionSort() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        Sorting.sort(
            input,
            Algorithm.SELECTIONSORT.longName,
            "Swap-Operation",
            "",
            SelectionSort::selectionsort,
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
