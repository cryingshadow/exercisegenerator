package exercisegenerator.algorithms.sorting;

import java.io.*;
import java.util.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.util.*;

public class BubbleSort implements AlgorithmImplementation {

    public static final BubbleSort INSTANCE = new BubbleSort();

    public static List<List<ItemWithTikZInformation<Integer>>> bubblesort(final int[] initialArray) {
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
        return result;
    }

    private BubbleSort() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        Sorting.sort(
            input,
            Algorithm.BUBBLESORT.longName,
            "Swap-Operation",
            "",
            BubbleSort::bubblesort,
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
