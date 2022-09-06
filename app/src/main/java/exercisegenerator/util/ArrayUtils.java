package exercisegenerator.util;

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

    /**
     * Swaps the array elements at indices a and b if both indices are greater than or equal to 0. If one of the
     * indices is negative, this method does nothing. If otherwise one of the indices is bigger than the array length
     * minus one, an ArrayOutOfBoundsException is thrown.
     * @param array The array.
     * @param a The first index.
     * @param b The second index.
     */
    public static void swap(final Integer[] array, final int a, final int b) {
        if (a >= 0 && b >= 0) {
            final int store = array[a];
            array[a] = array[b];
            array[b] = store;
        }
    }

}
