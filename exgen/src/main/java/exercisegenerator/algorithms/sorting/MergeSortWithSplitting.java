package exercisegenerator.algorithms.sorting;

import exercisegenerator.algorithms.*;

public class MergeSortWithSplitting extends MergeSort {

    public static final MergeSortWithSplitting INSTANCE = new MergeSortWithSplitting();

    private MergeSortWithSplitting() {}

    @Override
    public String algorithmName() {
        return Algorithm.MERGESORT_SPLIT.longName;
    }

    @Override
    public SortingSolution apply(final int[] initialArray) {
        return MergeSort.mergesort(initialArray, true);
    }

}
