package exercisegenerator;

import java.io.*;

/**
 * This abstract class offers static methods for manipulating and outputting arrays as TikZ pictures.
 * @author Thomas Stroeder
 * @version 1.0.1
 */
public abstract class ArrayUtils {

    /**
     * A number to uniquely identify nodes.
     */
    private static int number = 0;

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
     * Prints a row of nodes with the contents of the array.
     * @param array The array of values.
     * @param separate An array indicating which nodes should be separated horizontally. Must have a size exactly one
     *                 less than array or be null.
     * @param mark An array indicating which node should be marked by a grey background. Must have the same size as
     *             array or be null.
     * @param below The name of the left-most node in the row above the current row.
     * @param writer The writer to send the output to.
     * @return The name of the left-most node of the current row.
     * @throws IOException If some I/O error occurs.
     */
    public static String printArray(
        final Integer[] array,
        final boolean[] separate,
        final boolean[] mark,
        final String below,
        final BufferedWriter writer
    ) throws IOException {
        final String firstName = "n" + ArrayUtils.number++;
        if (below == null) {
            writer.write("\\node[node");
            if (mark != null && mark[0]) {
                writer.write(",fill=black!20");
            }
            writer.write("] (");
            writer.write(firstName);
            if(array[0] != null)
            {
                final int val = array[0];
                writer.write(") {" + (val < 10 ? "\\phantom{0}" : "") + val);
            }
            else
            {
                writer.write(") {\\phantom{00}");
            }
            writer.write("};");
            writer.newLine();
        } else {
            writer.write("\\node[node");
            if (mark != null && mark[0]) {
                writer.write(",fill=black!20");
            }
            writer.write("] (");
            writer.write(firstName);
            writer.write(") [below=of ");
            writer.write(below);
            if(array[0] != null)
            {
                final int val = array[0];
                writer.write("] {" + (val < 10 ? "\\phantom{0}" : "") + val);
            }
            else
            {
                writer.write("] {\\phantom{00}");
            }
            writer.write("};");
            writer.newLine();
        }
        for (int i = 1; i < array.length; i++) {
            writer.write("\\node[node");
            if (mark != null && mark[i]) {
                writer.write(",fill=black!20");
            }
            writer.write("] (");
            writer.write("n" + ArrayUtils.number++);
            writer.write(") [right=");
            if (separate != null && separate[i - 1]) {
                writer.write("0.1 ");
            }
            writer.write("of ");
            writer.write("n" + (ArrayUtils.number - 2));
            if(array[i] != null)
            {
                final int val = array[i];
                writer.write("] {" + (val < 10 ? "\\phantom{0}" : "") + val);
            }
            else
            {
                writer.write("] {\\phantom{00}");
            }
            writer.write("};");
            writer.newLine();
        }
        return firstName;
    }

    /**
     * Prints a row of empty nodes as solution space for the contents of the array.
     * @param length The length of the array.
     * @param below The name of the left-most node in the row above the current row.
     * @param writer The writer to send the output to.
     * @return The name of the left-most node of the current row.
     * @throws IOException If some I/O error occurs.
     */
    public static String printEmptyArray(
        final int length,
        final String below,
        final BufferedWriter writer
    ) throws IOException {
        final String firstName = "n" + ArrayUtils.number++;
        if (below == null) {
            writer.write("\\node[node] (");
            writer.write(firstName);
            writer.write(") {\\phantom{00}};");
            writer.newLine();
        } else {
            writer.write("\\node[node] (");
            writer.write(firstName);
            writer.write(") [below=of ");
            writer.write(below);
            writer.write("] {\\phantom{00}};");
            writer.newLine();
        }
        for (int i = 1; i < length; i++) {
            writer.write("\\node[node] (n" + ArrayUtils.number++);
            writer.write(") [right=of n" + (ArrayUtils.number - 2));
            writer.write("] {\\phantom{00}};");
            writer.newLine();
        }
        return firstName;
    }

    /**
     * Prints a row of empty nodes as solution space for the contents of the array with array indices above.
     * @param length The length of the array.
     * @param writer The writer to send the output to.
     * @throws IOException If some I/O error occurs.
     */
    public static void printEmptyArrayWithIndex(final int length, final BufferedWriter writer) throws IOException {
        int i = 0;
        writer.write("\\node[node] (");
        writer.write("n" + ArrayUtils.number++);
        writer.write(") {\\phantom{00}};");
        writer.newLine();
        int ref = ArrayUtils.number - 1;
        writer.write("\\node (");
        writer.write("l" + ref);
        writer.write(") [above=0 of n" + ref);
        writer.write("] {\\scriptsize\\texttt{a[" + i);
        writer.write("]}};");
        writer.newLine();
        while (i < length - 1) {
            i++;
            writer.write("\\node[node] (n" + ArrayUtils.number++);
            writer.write(") [right=of n" + (ArrayUtils.number - 2));
            writer.write("] {\\phantom{00}};");
            writer.newLine();
            ref = ArrayUtils.number - 1;
            writer.write("\\node (");
            writer.write("l" + ref);
            writer.write(") [above=0 of n" + ref);
            writer.write("] {\\scriptsize\\texttt{a[" + i);
            writer.write("]}};");
            writer.newLine();
        }
    }

    /**
     * Prints a colum of empty nodes as solution space for the contents of the array.
     * @param length The length of the array.
     * @param left The name of the top-most node in the colum left of the current colum.
     * @param writer The writer to send the output to.
     * @return The name of the top-most node of the current colum.
     * @throws IOException If some I/O error occurs.
     */
    public static String printEmptyVerticalArray(
        final int length,
        final String left,
        final BufferedWriter writer
        ) throws IOException {
        final String firstName = "n" + ArrayUtils.number++;
        if (left == null) {
            writer.write("\\node[node] (");
            writer.write(firstName);
            writer.write(") {\\phantom{00}};");
            writer.newLine();
        } else {
            writer.write("\\node[node] (");
            writer.write(firstName);
            writer.write(") [right=of ");
            writer.write(left);
            writer.write("] {\\phantom{00}};");
            writer.newLine();
        }
        for (int i = 1; i < length; i++) {
            writer.write("\\node[node] (n" + ArrayUtils.number++);
            writer.write(") [below=of n" + (ArrayUtils.number - 2));
            writer.write("] {\\phantom{00}};");
            writer.newLine();
        }
        return firstName;
    }

    /**
     * Prints the specified array interpreted as binary tree up to the specified index.
     * @param array The array.
     * @param to The index to which the tree should be printed.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printTree(final Integer[] array, final int to, final BufferedWriter writer) throws IOException {
        if (to < 0) {
            return;
        }
        TikZUtils.printTikzBeginning(TikZStyle.TREE, writer);
        if (to > 0) {
            writer.write("\\Tree");
            ArrayUtils.printTree(array, 0, to, writer);
        } else {
            writer.write("\\node[circle,draw=black,thick,inner sep=5pt] {" + array[0] + "};");
        }
        writer.newLine();
        TikZUtils.printTikzEnd(writer);
    }

    /**
     * Prints the specified array interpreted as binary tree from the specified start index (i.e., it prints the
     * subtree starting with the element at the specified start index) to the specified end index.
     * @param array The array.
     * @param start The start index.
     * @param end The end index.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printTree(final Integer[] array, final int start, final int end, final BufferedWriter writer) throws IOException {
        final int next = 2 * start + 1;
        if (next <= end) {
            writer.write(" [." + array[start]);
            ArrayUtils.printTree(array, next, end, writer);
            if (next + 1 <= end) {
                ArrayUtils.printTree(array, next + 1, end, writer);
            }
            writer.write(" ]");
        } else {
            writer.write(" " + array[start]);
        }
    }

    /**
     * Prints a colum of nodes with the contents of the array.
     * @param array The array of values.
     * @param separate An array indicating which nodes should be separated vertically. Must have a size exactly one
     *                 less than array or be null.
     * @param mark An array indicating which node should be marked by a grey background. Must have the same size as
     *             array or be null.
     * @param left The name of the top-most node in the colum left of the current colum.
     * @param writer The writer to send the output to.
     * @return The name of the left-most node of the current row.
     * @throws IOException If some I/O error occurs.
     */
    public static String printVerticalArray(
        final Integer[] array,
        final boolean[] separate,
        final boolean[] mark,
        final String left,
        final BufferedWriter writer
    ) throws IOException {
        final String firstName = "n" + ArrayUtils.number++;
        if( left == null )
        {
            writer.write("\\node[node");
            if (mark != null && mark[0]) {
                writer.write(",fill=black!20");
            }
            writer.write("] (");
            writer.write(firstName);
            final int val = array[0];
            writer.write(") {" + (val < 10 ? "\\phantom{0}" : "") + val);
            writer.write("};");
            writer.newLine();
        } else {
            writer.write("\\node[node");
            if (mark != null && mark[0]) {
                writer.write(",fill=black!20");
            }
            writer.write("] (");
            writer.write(firstName);
            writer.write(") [right=of ");
            writer.write(left);
            final int val = array[0];
            writer.write("] {" + (val < 10 ? "\\phantom{0}" : "") + val);
            writer.write("};");
            writer.newLine();
        }
        for (int i = 1; i < array.length; i++) {
            writer.write("\\node[node");
            if (mark != null && mark[i]) {
                writer.write(",fill=black!20");
            }
            writer.write("] (");
            writer.write("n" + ArrayUtils.number++);
            writer.write(") [below=");
            if (separate != null && separate[i - 1]) {
                writer.write("0.1 ");
            }
            writer.write("of ");
            writer.write("n" + (ArrayUtils.number - 2));
            final int val = array[i];
            writer.write("] {" + (val < 10 ? "\\phantom{0}" : "") + val);
            writer.write("};");
            writer.newLine();
        }
        return firstName;
    }

    /**
     * Prints a colum of nodes with the contents of the array.
     * @param array The array of values.
     * @param separate An array indicating which nodes should be separated vertically. Must have a size exactly one
     *                 less than array or be null.
     * @param mark An array indicating which node should be marked by a grey background. Must have the same size as
     *             array or be null.
     * @param left The name of the top-most node in the colum left of the current colum.
     * @param writer The writer to send the output to.
     * @return The name of the left-most node of the current row.
     * @throws IOException If some I/O error occurs.
     */
    public static String printVerticalStringArray(
        final String[] array,
        final boolean[] separate,
        final boolean[] mark,
        final String left,
        final BufferedWriter writer
        ) throws IOException {
        final String firstName = "n" + ArrayUtils.number++;
        if( left == null )
        {
            writer.write("\\node[node");
            if (mark != null && mark[0]) {
                writer.write(",fill=black!20");
            }
            writer.write("] (");
            writer.write(firstName);
            final String val = array[0];
            writer.write(") {" + val);
            writer.write("};");
            writer.newLine();
        } else {
            writer.write("\\node[node");
            if (mark != null && mark[0]) {
                writer.write(",fill=black!20");
            }
            writer.write("] (");
            writer.write(firstName);
            writer.write(") [right=of ");
            writer.write(left);
            final String val = array[0];
            writer.write("] {" + val);
            writer.write("};");
            writer.newLine();
        }
        for (int i = 1; i < array.length; i++) {
            writer.write("\\node[node");
            if (mark != null && mark[i]) {
                writer.write(",fill=black!20");
            }
            writer.write("] (");
            writer.write("n" + ArrayUtils.number++);
            writer.write(") [below=");
            if (separate != null && separate[i - 1]) {
                writer.write("0.1 ");
            }
            writer.write("of ");
            writer.write("n" + (ArrayUtils.number - 2));
            final String val = array[i];
            writer.write("] {" + val);
            writer.write("};");
            writer.newLine();
        }
        return firstName;
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
