package exercisegenerator.algorithms.sorting;

import java.util.*;
import java.util.Optional;
import java.util.stream.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.sorting.*;

public class SortingTest {

    private static List<ItemWithTikZInformation<Integer>> toItemsList(final int[] array) {
        return Arrays.stream(array)
            .mapToObj(i -> new ItemWithTikZInformation<Integer>(Optional.of(i)))
            .toList();
    }

    private static List<List<ItemWithTikZInformation<Integer>>> toItemsLists(final int[][] arrays) {
        return Arrays.stream(arrays).map(SortingTest::toItemsList).toList();
    }

    private static List<List<ItemWithTikZInformation<Integer>>> toItemsLists(
        final int[][] arrays,
        final boolean[][] markers
    ) {
        return IntStream.range(0, arrays.length)
            .mapToObj(
                i -> IntStream.range(0, arrays[i].length)
                    .mapToObj(
                        j -> new ItemWithTikZInformation<Integer>(Optional.of(arrays[i][j]), markers[i][j], false)
                    )
                    .toList()
            ).toList();
    }

    @Test
    public void bubblesort() {
        final int[] array = new int[] {5,7,4,8,1,3};
        final SortingSolution expected =
            new SortingSolution(
                1,
                SortingTest.toItemsLists(
                    new int[][] {
                        {5,7,4,8,1,3},
                        {5,4,7,8,1,3},
                        {5,4,7,1,8,3},
                        {5,4,7,1,3,8},
                        {4,5,7,1,3,8},
                        {4,5,1,7,3,8},
                        {4,5,1,3,7,8},
                        {4,1,5,3,7,8},
                        {4,1,3,5,7,8},
                        {1,4,3,5,7,8},
                        {1,3,4,5,7,8}
                    }
                )
            );
        Assert.assertEquals(BubbleSort.INSTANCE.apply(array), expected);
    }

    @Test
    public void bucketsort() {
        final int[] array = new int[] {27,25,44,18,31,73};
        final BucketSortSolution expected =
            new BucketSortSolution(
                new IntegerList[] {
                    new IntegerList(),
                    new IntegerList(18),
                    new IntegerList(27,25),
                    new IntegerList(31),
                    new IntegerList(44),
                    new IntegerList(),
                    new IntegerList(),
                    new IntegerList(73),
                    new IntegerList(),
                    new IntegerList()
                },
                SortingTest.toItemsList(new int[] {18,25,27,31,44,73})
            );
        final BucketSortSolution result = BucketSort.INSTANCE.apply(new BucketSortProblem(array, 0, 99, 10));
        Assert.assertEquals(result.solutionArray(), expected.solutionArray());
        Assert.assertTrue(Arrays.deepEquals(result.buckets(), expected.buckets()));
    }

    @Test
    public void countingsort() {
        final int[] array = new int[] {5,7,4,8,1,3};
        final CountingSortSolution expected =
            new CountingSortSolution(
                SortingTest.toItemsList(new int[] {0,1,0,1,1,1,0,1,1,0}),
                SortingTest.toItemsList(new int[] {1,3,4,5,7,8})
            );
        Assert.assertEquals(CountingSort.INSTANCE.apply(new CountingSortProblem(array, 0, 9)), expected);
    }

    @Test
    public void heapsort() {
        final int[] array = new int[] {5,7,4,8,1,3};
        final SortingSolution expected =
            new SortingSolution(
                1,
                Arrays.asList(
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(5)),
                        new ItemWithTikZInformation<Integer>(Optional.of(7)),
                        new ItemWithTikZInformation<Integer>(Optional.of(4)),
                        new ItemWithTikZInformation<Integer>(Optional.of(8)),
                        new ItemWithTikZInformation<Integer>(Optional.of(1)),
                        new ItemWithTikZInformation<Integer>(Optional.of(3))
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(5)),
                        new ItemWithTikZInformation<Integer>(Optional.of(8)),
                        new ItemWithTikZInformation<Integer>(Optional.of(4)),
                        new ItemWithTikZInformation<Integer>(Optional.of(7)),
                        new ItemWithTikZInformation<Integer>(Optional.of(1)),
                        new ItemWithTikZInformation<Integer>(Optional.of(3))
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(8)),
                        new ItemWithTikZInformation<Integer>(Optional.of(5)),
                        new ItemWithTikZInformation<Integer>(Optional.of(4)),
                        new ItemWithTikZInformation<Integer>(Optional.of(7)),
                        new ItemWithTikZInformation<Integer>(Optional.of(1)),
                        new ItemWithTikZInformation<Integer>(Optional.of(3))
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(8)),
                        new ItemWithTikZInformation<Integer>(Optional.of(7)),
                        new ItemWithTikZInformation<Integer>(Optional.of(4)),
                        new ItemWithTikZInformation<Integer>(Optional.of(5)),
                        new ItemWithTikZInformation<Integer>(Optional.of(1)),
                        new ItemWithTikZInformation<Integer>(Optional.of(3))
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(3)),
                        new ItemWithTikZInformation<Integer>(Optional.of(7)),
                        new ItemWithTikZInformation<Integer>(Optional.of(4)),
                        new ItemWithTikZInformation<Integer>(Optional.of(5)),
                        new ItemWithTikZInformation<Integer>(Optional.of(1)),
                        new ItemWithTikZInformation<Integer>(Optional.of(8), true)
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(7)),
                        new ItemWithTikZInformation<Integer>(Optional.of(3)),
                        new ItemWithTikZInformation<Integer>(Optional.of(4)),
                        new ItemWithTikZInformation<Integer>(Optional.of(5)),
                        new ItemWithTikZInformation<Integer>(Optional.of(1)),
                        new ItemWithTikZInformation<Integer>(Optional.of(8), true)
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(7)),
                        new ItemWithTikZInformation<Integer>(Optional.of(5)),
                        new ItemWithTikZInformation<Integer>(Optional.of(4)),
                        new ItemWithTikZInformation<Integer>(Optional.of(3)),
                        new ItemWithTikZInformation<Integer>(Optional.of(1)),
                        new ItemWithTikZInformation<Integer>(Optional.of(8), true)
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(1)),
                        new ItemWithTikZInformation<Integer>(Optional.of(5)),
                        new ItemWithTikZInformation<Integer>(Optional.of(4)),
                        new ItemWithTikZInformation<Integer>(Optional.of(3)),
                        new ItemWithTikZInformation<Integer>(Optional.of(7), true),
                        new ItemWithTikZInformation<Integer>(Optional.of(8))
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(5)),
                        new ItemWithTikZInformation<Integer>(Optional.of(1)),
                        new ItemWithTikZInformation<Integer>(Optional.of(4)),
                        new ItemWithTikZInformation<Integer>(Optional.of(3)),
                        new ItemWithTikZInformation<Integer>(Optional.of(7), true),
                        new ItemWithTikZInformation<Integer>(Optional.of(8))
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(5)),
                        new ItemWithTikZInformation<Integer>(Optional.of(3)),
                        new ItemWithTikZInformation<Integer>(Optional.of(4)),
                        new ItemWithTikZInformation<Integer>(Optional.of(1)),
                        new ItemWithTikZInformation<Integer>(Optional.of(7), true),
                        new ItemWithTikZInformation<Integer>(Optional.of(8))
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(1)),
                        new ItemWithTikZInformation<Integer>(Optional.of(3)),
                        new ItemWithTikZInformation<Integer>(Optional.of(4)),
                        new ItemWithTikZInformation<Integer>(Optional.of(5), true),
                        new ItemWithTikZInformation<Integer>(Optional.of(7)),
                        new ItemWithTikZInformation<Integer>(Optional.of(8))
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(4)),
                        new ItemWithTikZInformation<Integer>(Optional.of(3)),
                        new ItemWithTikZInformation<Integer>(Optional.of(1)),
                        new ItemWithTikZInformation<Integer>(Optional.of(5), true),
                        new ItemWithTikZInformation<Integer>(Optional.of(7)),
                        new ItemWithTikZInformation<Integer>(Optional.of(8))
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(1)),
                        new ItemWithTikZInformation<Integer>(Optional.of(3)),
                        new ItemWithTikZInformation<Integer>(Optional.of(4), true),
                        new ItemWithTikZInformation<Integer>(Optional.of(5)),
                        new ItemWithTikZInformation<Integer>(Optional.of(7)),
                        new ItemWithTikZInformation<Integer>(Optional.of(8))
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(3)),
                        new ItemWithTikZInformation<Integer>(Optional.of(1)),
                        new ItemWithTikZInformation<Integer>(Optional.of(4), true),
                        new ItemWithTikZInformation<Integer>(Optional.of(5)),
                        new ItemWithTikZInformation<Integer>(Optional.of(7)),
                        new ItemWithTikZInformation<Integer>(Optional.of(8))
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(1)),
                        new ItemWithTikZInformation<Integer>(Optional.of(3), true),
                        new ItemWithTikZInformation<Integer>(Optional.of(4)),
                        new ItemWithTikZInformation<Integer>(Optional.of(5)),
                        new ItemWithTikZInformation<Integer>(Optional.of(7)),
                        new ItemWithTikZInformation<Integer>(Optional.of(8))
                    )
                )
            );
        Assert.assertEquals(HeapSort.INSTANCE.apply(array), expected);
    }

    @Test
    public void insertionsort() {
        final int[] array = new int[] {5,7,4,8,1,3};
        final SortingSolution expected =
            new SortingSolution(
                1,
                SortingTest.toItemsLists(
                    new int[][] {
                        {5,7,4,8,1,3},
                        {5,7,4,8,1,3},
                        {4,5,7,8,1,3},
                        {4,5,7,8,1,3},
                        {1,4,5,7,8,3},
                        {1,3,4,5,7,8}
                    }
                )
            );
        Assert.assertEquals(InsertionSort.INSTANCE.apply(array), expected);
    }

    @Test
    public void mergesort() {
        final int[] array = new int[] {5,7,4,8,1,3};
        final SortingSolution expected =
            new SortingSolution(
                1,
                SortingTest.toItemsLists(
                    new int[][] {
                        {5,7,4,8,1,3},
                        {5,7,4,8,1,3},
                        {4,5,7,8,1,3},
                        {4,5,7,1,8,3},
                        {4,5,7,1,3,8},
                        {1,3,4,5,7,8}
                    },
                    new boolean[][] {
                        {false, false, false, false, false, false},
                        {true, true, false, false, false, false},
                        {true, true, true, false, false, false},
                        {false, false, false, true, true, false},
                        {false, false, false, true, true, true},
                        {true, true, true, true, true, true}
                    }
                )
            );
        Assert.assertEquals(MergeSort.INSTANCE.apply(array), expected);
    }

    @Test
    public void mergesortWithSplit() {
        final int[] array = new int[] {5,7,4,8,1,3};
        final SortingSolution expected =
            new SortingSolution(
                1,
                Arrays.asList(
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(5)),
                        new ItemWithTikZInformation<Integer>(Optional.of(7)),
                        new ItemWithTikZInformation<Integer>(Optional.of(4)),
                        new ItemWithTikZInformation<Integer>(Optional.of(8)),
                        new ItemWithTikZInformation<Integer>(Optional.of(1)),
                        new ItemWithTikZInformation<Integer>(Optional.of(3))
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(5)),
                        new ItemWithTikZInformation<Integer>(Optional.of(7)),
                        new ItemWithTikZInformation<Integer>(Optional.of(4)),
                        new ItemWithTikZInformation<Integer>(Optional.of(8), true),
                        new ItemWithTikZInformation<Integer>(Optional.of(1)),
                        new ItemWithTikZInformation<Integer>(Optional.of(3))
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(5)),
                        new ItemWithTikZInformation<Integer>(Optional.of(7)),
                        new ItemWithTikZInformation<Integer>(Optional.of(4), true),
                        new ItemWithTikZInformation<Integer>(Optional.of(8), true),
                        new ItemWithTikZInformation<Integer>(Optional.of(1)),
                        new ItemWithTikZInformation<Integer>(Optional.of(3))
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(5)),
                        new ItemWithTikZInformation<Integer>(Optional.of(7), true),
                        new ItemWithTikZInformation<Integer>(Optional.of(4), true),
                        new ItemWithTikZInformation<Integer>(Optional.of(8), true),
                        new ItemWithTikZInformation<Integer>(Optional.of(1)),
                        new ItemWithTikZInformation<Integer>(Optional.of(3))
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(5), true, false),
                        new ItemWithTikZInformation<Integer>(Optional.of(7), true, false),
                        new ItemWithTikZInformation<Integer>(Optional.of(4), true),
                        new ItemWithTikZInformation<Integer>(Optional.of(8), true),
                        new ItemWithTikZInformation<Integer>(Optional.of(1)),
                        new ItemWithTikZInformation<Integer>(Optional.of(3))
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(4), true, false),
                        new ItemWithTikZInformation<Integer>(Optional.of(5), true, false),
                        new ItemWithTikZInformation<Integer>(Optional.of(7), true, false),
                        new ItemWithTikZInformation<Integer>(Optional.of(8), true),
                        new ItemWithTikZInformation<Integer>(Optional.of(1)),
                        new ItemWithTikZInformation<Integer>(Optional.of(3))
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(4)),
                        new ItemWithTikZInformation<Integer>(Optional.of(5)),
                        new ItemWithTikZInformation<Integer>(Optional.of(7)),
                        new ItemWithTikZInformation<Integer>(Optional.of(8), true),
                        new ItemWithTikZInformation<Integer>(Optional.of(1)),
                        new ItemWithTikZInformation<Integer>(Optional.of(3), true)
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(4)),
                        new ItemWithTikZInformation<Integer>(Optional.of(5)),
                        new ItemWithTikZInformation<Integer>(Optional.of(7)),
                        new ItemWithTikZInformation<Integer>(Optional.of(8), true),
                        new ItemWithTikZInformation<Integer>(Optional.of(1), true),
                        new ItemWithTikZInformation<Integer>(Optional.of(3), true)
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(4)),
                        new ItemWithTikZInformation<Integer>(Optional.of(5)),
                        new ItemWithTikZInformation<Integer>(Optional.of(7)),
                        new ItemWithTikZInformation<Integer>(Optional.of(1), true, true),
                        new ItemWithTikZInformation<Integer>(Optional.of(8), true, false),
                        new ItemWithTikZInformation<Integer>(Optional.of(3), true)
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(4)),
                        new ItemWithTikZInformation<Integer>(Optional.of(5)),
                        new ItemWithTikZInformation<Integer>(Optional.of(7)),
                        new ItemWithTikZInformation<Integer>(Optional.of(1), true, true),
                        new ItemWithTikZInformation<Integer>(Optional.of(3), true, false),
                        new ItemWithTikZInformation<Integer>(Optional.of(8), true, false)
                    ),
                    Arrays.asList(
                        new ItemWithTikZInformation<Integer>(Optional.of(1), true, false),
                        new ItemWithTikZInformation<Integer>(Optional.of(3), true, false),
                        new ItemWithTikZInformation<Integer>(Optional.of(4), true, false),
                        new ItemWithTikZInformation<Integer>(Optional.of(5), true, false),
                        new ItemWithTikZInformation<Integer>(Optional.of(7), true, false),
                        new ItemWithTikZInformation<Integer>(Optional.of(8), true, false)
                    )
                )
            );
        Assert.assertEquals(MergeSortWithSplitting.INSTANCE.apply(array), expected);
    }

    @Test(dataProvider="quicksortData")
    public void quicksort(final int[] array, final SortingSolution expected) {
        Assert.assertEquals(QuickSort.INSTANCE.apply(array), expected);
    }

    @DataProvider
    public Object[][] quicksortData() {
        return new Object[][] {
            {
                new int[] {5,7,4,8,1,3},
                new SortingSolution(
                    1,
                    List.of(
                        List.of(
                            new ItemWithTikZInformation<Integer>(Optional.of(5)),
                            new ItemWithTikZInformation<Integer>(Optional.of(7)),
                            new ItemWithTikZInformation<Integer>(Optional.of(4)),
                            new ItemWithTikZInformation<Integer>(Optional.of(8)),
                            new ItemWithTikZInformation<Integer>(Optional.of(1)),
                            new ItemWithTikZInformation<Integer>(Optional.of(3))
                        ),
                        List.of(
                            new ItemWithTikZInformation<Integer>(Optional.of(1)),
                            new ItemWithTikZInformation<Integer>(Optional.of(3), true, true),
                            new ItemWithTikZInformation<Integer>(Optional.of(4), true),
                            new ItemWithTikZInformation<Integer>(Optional.of(8)),
                            new ItemWithTikZInformation<Integer>(Optional.of(5)),
                            new ItemWithTikZInformation<Integer>(Optional.of(7))
                        ),
                        List.of(
                            new ItemWithTikZInformation<Integer>(Optional.of(1)),
                            new ItemWithTikZInformation<Integer>(Optional.of(3), true),
                            new ItemWithTikZInformation<Integer>(Optional.of(4), true),
                            new ItemWithTikZInformation<Integer>(Optional.of(5)),
                            new ItemWithTikZInformation<Integer>(Optional.of(7), true, true),
                            new ItemWithTikZInformation<Integer>(Optional.of(8), true)
                        ),
                        List.of(
                            new ItemWithTikZInformation<Integer>(Optional.of(1)),
                            new ItemWithTikZInformation<Integer>(Optional.of(3), true),
                            new ItemWithTikZInformation<Integer>(Optional.of(4), true),
                            new ItemWithTikZInformation<Integer>(Optional.of(5), true, true),
                            new ItemWithTikZInformation<Integer>(Optional.of(7), true),
                            new ItemWithTikZInformation<Integer>(Optional.of(8), true)
                        )
                    )
                )
            },
            {
                new int[] {1,2,3,4},
                new SortingSolution(
                    1,
                    List.of(
                        List.of(
                            new ItemWithTikZInformation<Integer>(Optional.of(1)),
                            new ItemWithTikZInformation<Integer>(Optional.of(2)),
                            new ItemWithTikZInformation<Integer>(Optional.of(3)),
                            new ItemWithTikZInformation<Integer>(Optional.of(4))
                        ),
                        List.of(
                            new ItemWithTikZInformation<Integer>(Optional.of(1)),
                            new ItemWithTikZInformation<Integer>(Optional.of(2)),
                            new ItemWithTikZInformation<Integer>(Optional.of(3)),
                            new ItemWithTikZInformation<Integer>(Optional.of(4), true, true)
                        ),
                        List.of(
                            new ItemWithTikZInformation<Integer>(Optional.of(1)),
                            new ItemWithTikZInformation<Integer>(Optional.of(2)),
                            new ItemWithTikZInformation<Integer>(Optional.of(3), true, true),
                            new ItemWithTikZInformation<Integer>(Optional.of(4), true)
                        ),
                        List.of(
                            new ItemWithTikZInformation<Integer>(Optional.of(1)),
                            new ItemWithTikZInformation<Integer>(Optional.of(2), true, true),
                            new ItemWithTikZInformation<Integer>(Optional.of(3), true),
                            new ItemWithTikZInformation<Integer>(Optional.of(4), true)
                        )
                    )
                )
            },
            {
                new int[] {4,3,2,1},
                new SortingSolution(
                    1,
                    List.of(
                        List.of(
                            new ItemWithTikZInformation<Integer>(Optional.of(4)),
                            new ItemWithTikZInformation<Integer>(Optional.of(3)),
                            new ItemWithTikZInformation<Integer>(Optional.of(2)),
                            new ItemWithTikZInformation<Integer>(Optional.of(1))
                        ),
                        List.of(
                            new ItemWithTikZInformation<Integer>(Optional.of(1), true, false),
                            new ItemWithTikZInformation<Integer>(Optional.of(3), true),
                            new ItemWithTikZInformation<Integer>(Optional.of(2)),
                            new ItemWithTikZInformation<Integer>(Optional.of(4))
                        ),
                        List.of(
                            new ItemWithTikZInformation<Integer>(Optional.of(1)),
                            new ItemWithTikZInformation<Integer>(Optional.of(3), true),
                            new ItemWithTikZInformation<Integer>(Optional.of(2)),
                            new ItemWithTikZInformation<Integer>(Optional.of(4), true, true)
                        ),
                        List.of(
                            new ItemWithTikZInformation<Integer>(Optional.of(1)),
                            new ItemWithTikZInformation<Integer>(Optional.of(2), true, true),
                            new ItemWithTikZInformation<Integer>(Optional.of(3), true),
                            new ItemWithTikZInformation<Integer>(Optional.of(4), true)
                        )
                    )
                )
            }
        };
    }

    @Test
    public void selectionsort() {
        final int[] array = new int[] {5,7,4,8,1,3};
        final SortingSolution expected =
            new SortingSolution(
                1,
                SortingTest.toItemsLists(
                    new int[][] {
                        {5,7,4,8,1,3},
                        {1,7,4,8,5,3},
                        {1,3,4,8,5,7},
                        {1,3,4,5,8,7},
                        {1,3,4,5,7,8}
                    }
                )
            );
        Assert.assertEquals(SelectionSort.INSTANCE.apply(array), expected);
    }

}
