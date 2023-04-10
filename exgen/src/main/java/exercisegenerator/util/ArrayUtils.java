package exercisegenerator.util;

import java.lang.reflect.*;
import java.util.*;

/**
 * This abstract class offers static methods for manipulating arrays.
 */
public abstract class ArrayUtils {

    /**
     * @param array An int array containing the keys in ascending order.
     * @param key The key to search.
     * @param from The start index to search from (inclusive).
     * @param to The end index to search to (inclusive).
     * @return If the array contains the key to search for within the interval specified by the start and end index, an
     *         index within that interval where the array contains this key is returned. Otherwise the index of the
     *         first element within the interval greater than the key to search for is returned. If no such element
     *         exists, the returned index is one bigger than the end index.
     */
    public static Integer binarySearch(final Integer[] array, final Integer key, final Integer from, final Integer to) {
        if (to - from <= 0) {
            if (to - from == 0 && array[from] < key) {
                return from + 1;
            }
            return from;
        }
        final Integer index = from + ((to - from) / 2);
        final Integer there = array[index];
        if (there == key) {
            return index;
        } else if (there < key) {
            return ArrayUtils.binarySearch(array, key, index + 1, to);
        } else {
            return ArrayUtils.binarySearch(array, key, from, index);
        }
    }

    public static double[] copy(final double[] array) {
        final int length = array.length;
        final double[] result = new double[length];
        System.arraycopy(array, 0, result, 0, length);
        return result;
    }

    public static double[][] copy(final double[][] array) {
        return Arrays.stream(array).map(double[]::clone).toArray(double[][]::new);
    }

    public static int[] copy(final int[] array) {
        final int length = array.length;
        final int[] result = new int[length];
        System.arraycopy(array, 0, result, 0, length);
        return result;
    }

    public static <T> T[] copy(final T[] array) {
        final int length = array.length;
        @SuppressWarnings("unchecked")
        final T[] result = (T[])Array.newInstance(array.getClass().getComponentType(), length);
        System.arraycopy(array, 0, result, 0, length);
        return result;
    }

    public static <T> T[][] copy(final T[][] array) {
        final int length = array.length;
        @SuppressWarnings("unchecked")
        final T[][] result = (T[][])Array.newInstance(array.getClass().getComponentType(), length);
        for (int index = 0; index < length; index++) {
            result[index] = ArrayUtils.copy(array[index]);
        }
        return result;
    }

    public static void swap(final int[] array, final int a, final int b) {
        if (a >= 0 && b >= 0) {
            final int store = array[a];
            array[a] = array[b];
            array[b] = store;
        }
    }

    public static <T> void swap(final T[] array, final int a, final int b) {
        if (a >= 0 && b >= 0) {
            final T store = array[a];
            array[a] = array[b];
            array[b] = store;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T[][] transpose(final T[][] table, final boolean transpose) {
        if (table == null) {
            return null;
        }
        if (table.length == 0) {
            return ArrayUtils.copy(table);
        }
        if (transpose) {
            final int size = table[0].length;
            for (int i = 0; i < table.length; i++) {
                if (table[i].length != size) {
                    throw new IllegalArgumentException("Cannot transpose a table with differing lengths!");
                }
            }
            final T[][] result = (T[][])Array.newInstance(table.getClass().getComponentType(), size);
            for (int i = 0; i < size; i++) {
                result[i] = (T[])Array.newInstance(table[0].getClass().getComponentType(), table.length);
                for (int j = 0; j < table.length; j++) {
                    result[i][j] = table[j][i];
                }
            }
            return result;
        }
        final T[][] result = (T[][])Array.newInstance(table.getClass().getComponentType(), table.length);
        for (int i = 0; i < table.length; i++) {
            result[i] = ArrayUtils.copy(table[i]);
        }
        return result;
    }

}
