package exercisegenerator.algorithms.sorting;

import java.util.*;
import java.util.Optional;
import java.util.stream.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.io.*;

public class SortingTest {

    private static List<List<ItemWithTikZInformation<Integer>>> toItemsLists(final int[][] arrays) {
        return Arrays.stream(arrays)
            .map(array ->
                Arrays.stream(array)
                    .mapToObj(i -> new ItemWithTikZInformation<Integer>(Optional.of(i)))
                    .toList()
            ).toList();
    }

    private static List<List<ItemWithTikZInformation<Integer>>> toItemsLists(final int[][] arrays, final boolean[][] markers) {
        return IntStream.range(0, arrays.length)
            .mapToObj(
                i -> IntStream.range(0, arrays[i].length)
                    .mapToObj(j -> new ItemWithTikZInformation<Integer>(Optional.of(arrays[i][j]), markers[i][j], false))
                    .toList()
            ).toList();
    }

    @Test
    public void bubblesort() {
        final int[] array = new int[] {5,7,4,8,1,3};
        final List<List<ItemWithTikZInformation<Integer>>> expected =
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
            );
        Assert.assertEquals(BubbleSort.bubblesort(array), expected);
    }

    @Test
    public void heapsort() {
        final int[] array = new int[] {5,7,4,8,1,3};
        final List<List<ItemWithTikZInformation<Integer>>> expected =
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
            );
        Assert.assertEquals(HeapSort.heapsort(array), expected);
    }

    @Test
    public void insertionsort() {
        final int[] array = new int[] {5,7,4,8,1,3};
        final List<List<ItemWithTikZInformation<Integer>>> expected =
            SortingTest.toItemsLists(
                new int[][] {
                    {5,7,4,8,1,3},
                    {5,7,4,8,1,3},
                    {4,5,7,8,1,3},
                    {4,5,7,8,1,3},
                    {1,4,5,7,8,3},
                    {1,3,4,5,7,8}
                }
            );
        Assert.assertEquals(InsertionSort.insertionsort(array), expected);
    }

    @Test
    public void mergesort() {
        final int[] array = new int[] {5,7,4,8,1,3};
        final List<List<ItemWithTikZInformation<Integer>>> expected =
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
            );
        Assert.assertEquals(MergeSort.mergesort(array, false), expected);
    }

    @Test
    public void mergesortWithSplit() {
        final int[] array = new int[] {5,7,4,8,1,3};
        final List<List<ItemWithTikZInformation<Integer>>> expected =
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
            );
        Assert.assertEquals(MergeSort.mergesort(array, true), expected);
    }

    @Test
    public void quicksort() {
        final int[] array = new int[] {5,7,4,8,1,3};
        final List<List<ItemWithTikZInformation<Integer>>> expected =
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
                    new ItemWithTikZInformation<Integer>(Optional.of(1)),
                    new ItemWithTikZInformation<Integer>(Optional.of(3), true, true),
                    new ItemWithTikZInformation<Integer>(Optional.of(4), true),
                    new ItemWithTikZInformation<Integer>(Optional.of(8)),
                    new ItemWithTikZInformation<Integer>(Optional.of(5)),
                    new ItemWithTikZInformation<Integer>(Optional.of(7))
                ),
                Arrays.asList(
                    new ItemWithTikZInformation<Integer>(Optional.of(1)),
                    new ItemWithTikZInformation<Integer>(Optional.of(3), true),
                    new ItemWithTikZInformation<Integer>(Optional.of(4), true),
                    new ItemWithTikZInformation<Integer>(Optional.of(5)),
                    new ItemWithTikZInformation<Integer>(Optional.of(7), true, true),
                    new ItemWithTikZInformation<Integer>(Optional.of(8), true)
                ),
                Arrays.asList(
                    new ItemWithTikZInformation<Integer>(Optional.of(1)),
                    new ItemWithTikZInformation<Integer>(Optional.of(3), true),
                    new ItemWithTikZInformation<Integer>(Optional.of(4), true),
                    new ItemWithTikZInformation<Integer>(Optional.of(5), true, true),
                    new ItemWithTikZInformation<Integer>(Optional.of(7), true),
                    new ItemWithTikZInformation<Integer>(Optional.of(8), true)
                )
            );
        Assert.assertEquals(QuickSort.quicksort(array), expected);
    }

    @Test
    public void selectionsort() {
        final int[] array = new int[] {5,7,4,8,1,3};
        final List<List<ItemWithTikZInformation<Integer>>> expected =
            SortingTest.toItemsLists(
                new int[][] {
                    {5,7,4,8,1,3},
                    {1,7,4,8,5,3},
                    {1,3,4,8,5,7},
                    {1,3,4,5,8,7},
                    {1,3,4,5,7,8}
                }
            );
        Assert.assertEquals(SelectionSort.selectionsort(array), expected);
    }

}
