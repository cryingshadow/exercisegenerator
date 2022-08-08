package exercisegenerator.io;

import java.io.*;
import java.math.*;

import exercisegenerator.structures.*;

/**
 * This abstract class provides methods for creating TikZ output.
 * @author Thomas Stroeder
 * @version 1.1.0
 */
public abstract class TikZUtils {

    /**
     * The name of the center environment.
     */
    public static final String CENTER = "center";

    /**
     * The column width.
     */
    public static final String COL_WIDTH = "\\columnwidth";

    /**
     * Style for drawing highlighted edges.
     */
    public static final String EDGE_HIGHLIGHT_STYLE = "[p, bend right = 10, very thick, red]";

    /**
     * Style for drawing edges.
     */
    public static final String EDGE_STYLE = "[p, bend right = 10]";

    /**
     * The name of the enumerate environment.
     */
    public static final String ENUMERATE = "enumerate";

    /**
     * The item command.
     */
    public static final String ITEM = "\\item";

    /**
     * Style for drawing symmetric edges.
     */
    public static final String SYM_EDGE_STYLE = "[p]";

    /**
     * A bit less than half the column width.
     */
    public static final String TWO_COL_WIDTH = "8cm";

    /**
     * A number to uniquely identify nodes.
     */
    private static int number = 0;

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
        final String firstName = "n" + TikZUtils.number++;
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
            writer.write("n" + TikZUtils.number++);
            writer.write(") [right=");
            if (separate != null && separate[i - 1]) {
                writer.write("0.1 ");
            }
            writer.write("of ");
            writer.write("n" + (TikZUtils.number - 2));
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
     * Prints a new stretch factor for the array height.
     * @param stretch The stretch factor.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printArrayStretch(final double stretch, final BufferedWriter writer) throws IOException {
        writer.write("\\renewcommand{\\arraystretch}{" + stretch + "}");
        writer.newLine();
    }

    /**
     * Prints the beginning of the specified environment.
     * @param environment The environment.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printBeginning(final String environment, final BufferedWriter writer) throws IOException {
        writer.write("\\begin{" + environment + "}");
        writer.newLine();
    }

    /**
     * Prints a B-tree to the specified writer.
     * @param tree The B-tree.
     * @param writer The writer.
     * @throws IOException If some error occurs during output.
     */
    public static void printBTree(final IntBTree tree, final BufferedWriter writer) throws IOException {
        if (tree.hasJustRoot()) {
            writer.write("\\node[draw=black,rounded corners,thick,inner sep=5pt] " + tree.toString() + ";");
        } else if (tree.isEmpty()) {
            writer.write("\\node {" + tree.toString() + "};");
        } else {
            writer.write("\\Tree " + tree.toString() + ";");
        }
        writer.newLine();
    }

    /**
     * Prints a String representation of the edge from the specified from node to the specified to node with the
     * specified label suitable for TikZ output to the specified writer.
     * @param from The ID of the from node.
     * @param style The edge style (may be empty, but not null).
     * @param label The edge label (may be null).
     * @param to The ID of the to node.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static <E> void printEdge(
        final String style,
        final BigInteger from,
        final E label,
        final BigInteger to,
        final BufferedWriter writer
    ) throws IOException {
        final StringBuilder res = new StringBuilder();
        res.append("\\draw");
        res.append(style);
        res.append(" (n");
        res.append(from.toString());
        res.append(") to ");
        res.append("node[auto, swap] {");
        if (label != null) {
            res.append(label.toString());
        }
        res.append("} ");
        res.append("(n");
        res.append(to.toString());
        res.append(")");
        res.append(";");
        writer.write(res.toString());
        writer.newLine();
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
        final String firstName = "n" + TikZUtils.number++;
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
            writer.write("\\node[node] (n" + TikZUtils.number++);
            writer.write(") [right=of n" + (TikZUtils.number - 2));
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
        writer.write("n" + TikZUtils.number++);
        writer.write(") {\\phantom{00}};");
        writer.newLine();
        int ref = TikZUtils.number - 1;
        writer.write("\\node (");
        writer.write("l" + ref);
        writer.write(") [above=0 of n" + ref);
        writer.write("] {\\scriptsize\\texttt{a[" + i);
        writer.write("]}};");
        writer.newLine();
        while (i < length - 1) {
            i++;
            writer.write("\\node[node] (n" + TikZUtils.number++);
            writer.write(") [right=of n" + (TikZUtils.number - 2));
            writer.write("] {\\phantom{00}};");
            writer.newLine();
            ref = TikZUtils.number - 1;
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
        final String firstName = "n" + TikZUtils.number++;
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
            writer.write("\\node[node] (n" + TikZUtils.number++);
            writer.write(") [below=of n" + (TikZUtils.number - 2));
            writer.write("] {\\phantom{00}};");
            writer.newLine();
        }
        return firstName;
    }

    /**
     * Prints the end of the specified environment.
     * @param environment The environment.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printEnd(final String environment, final BufferedWriter writer) throws IOException {
        writer.write("\\end{" + environment + "}");
        writer.newLine();
    }

    /**
     * Prints the header of a LaTeX file with the required packages and settings for our exercise environment.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printLaTeXBeginning(final BufferedWriter writer) throws IOException {
        writer.write("\\documentclass{article}");
        writer.newLine();
        writer.newLine();
        writer.write("\\usepackage[table]{xcolor}");
        writer.newLine();
        writer.write("\\usepackage[a4paper,margin=2cm]{geometry}");
        writer.newLine();
        writer.write("\\usepackage{tikz}");
        writer.newLine();
        writer.write("\\usetikzlibrary{arrows,shapes.misc,shapes.arrows,shapes.multipart,shapes.geometric,chains,");
        writer.write("matrix,positioning,scopes,decorations.pathmorphing,decorations.pathreplacing,shadows,calc,");
        writer.write("trees,backgrounds}");
        writer.newLine();
        writer.write("\\usepackage{tikz-qtree}");
        writer.newLine();
        writer.write("\\usepackage{array}");
        writer.newLine();
        writer.write("\\usepackage{amsmath}");
        writer.newLine();
        writer.write("\\usepackage{enumerate}");
        writer.newLine();
        writer.newLine();
        writer.write("\\newcolumntype{C}[1]{>{\\centering\\let\\newline\\\\\\arraybackslash\\hspace{0pt}}m{#1}}");
        writer.newLine();
        writer.newLine();
        writer.write("\\setlength{\\parindent}{0pt}");
        writer.newLine();
        writer.newLine();
        writer.write("\\newcommand{\\emphasize}[1]{\\textbf{#1}}");
        writer.newLine();
        writer.write("\\newcommand*\\circled[1]{\\tikz[baseline=(char.base)]{");
        writer.newLine();
        writer.write("            \\node[shape=circle,draw,inner sep=2pt] (char) {#1};}}");
        writer.newLine();
        writer.newLine();
        writer.write("\\begin{document}");
        writer.newLine();
        writer.newLine();
    }

    /**
     * Prints the end of a LaTeX document.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printLaTeXEnd(final BufferedWriter writer) throws IOException {
        writer.newLine();
        writer.write("\\end{document}");
        writer.newLine();
    }

    /**
     * Prints a String representation of the specified node suitable for TikZ output to the specified writer.
     * @param node The node to print.
     * @param style The node style (may be empty, but not null).
     * @param position The position of the node (may be empty, but not null).
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static <N> void printNode (
        final Node<N> node,
        final String style,
        final String position,
        final BufferedWriter writer
    ) throws IOException {
        writer.write(
            String.format(
                "\\node%s (n%s) %s{%s};",
                style,
                node.id.toString(),
                position,
                node.label.isEmpty() ? "" : node.label.get().toString()
            )
        );
        writer.newLine();
    }

    /**
     * Prints a protected whitespace and a line terminator to the specified writer.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printProtectedNewline(final BufferedWriter writer) throws IOException {
        writer.write("~\\\\*\\vspace*{1ex}");
        writer.newLine();
    }

    /**
     * Prints the beginning of a samepage environment.
     * @param step The current evaluation step.
     * @param op The current operation.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printSamePageBeginning(
        final int step,
        final Pair<Integer, Boolean> op,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("\\begin{minipage}{\\columnwidth}");
        writer.newLine();
        writer.write("\\vspace*{2ex}");
        writer.newLine();
        writer.newLine();
        if (op.y) {
            writer.write("Schritt " + step + ": F\\\"uge " + op.x + " ein\\\\[-2ex]");
        } else {
            writer.write("Schritt " + step + ": L\\\"osche " + op.x + "\\\\[-2ex]");
        }
        writer.newLine();
        TikZUtils.printBeginning(TikZUtils.CENTER, writer);
    }

    /**
     * Prints the beginning of a samepage environment.
     * @param step The current evaluation step.
     * @param width A LaTeX String indicating the width of the minipage.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printSamePageBeginning(
        final int step,
        final String width,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("\\begin{minipage}{" + width + "}");
        writer.newLine();
        writer.write("\\vspace*{1ex}");
        writer.newLine();
        writer.write("Schritt " + step + ":\\\\[-2ex]");
        writer.newLine();
        TikZUtils.printBeginning(TikZUtils.CENTER, writer);
    }

    /**
     * Prints the end of a samepage environment.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printSamePageEnd(final BufferedWriter writer) throws IOException {
        TikZUtils.printEnd(TikZUtils.CENTER, writer);
        writer.write("\\end{minipage}");
        writer.newLine();
    }

    /**
     * Prints the beginning of solution space to the specified writer.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printSolutionSpaceBeginning(final BufferedWriter writer) throws IOException {
        writer.write("\\solutionSpace{");
        writer.newLine();
    }

    /**
     * Prints the end of solution space to the specified writer.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printSolutionSpaceEnd(final BufferedWriter writer) throws IOException {
        writer.write("}");
        writer.newLine();
    }

    /**
     * Prints a table by centering each column.
     * @param table A two-dimensional table of Strings.
     * @param color Color settings for each cell. Null means no color. The array must not be null.
     * @param width The column width.
     * @param writer The writer to send the output to.
     * @param transpose Transpose the table?
     * @param breakAtColumn Insert line breaks after this number of columns. Ignored if 0.
     * @throws IOException If some error occurs during output.
     */
    public static void printTable(
        final String[][] table,
        final String[][] color,
        final String width,
        final BufferedWriter writer,
        final boolean transpose,
        final int breakAtColumn
    ) throws IOException {
        final int allCols = (transpose ? table[0].length : table.length);
        int remainingCols = allCols;
        do {
            final int cols = remainingCols > breakAtColumn && breakAtColumn > 0 ? breakAtColumn : remainingCols;
            final int rows = (transpose ? table.length : table[0].length);
            writer.write("\\begin{tabular}{|*{" + cols + "}{C{" + width + "}|}}");
            writer.newLine();
            writer.write("\\hline");
            writer.newLine();
            for (int row = 0; row < rows; row++) {
                boolean first = true;
                final int startCol = allCols - remainingCols;
                for (int col = startCol; col < startCol + cols; col++) {
                    if (first) {
                        first = false;
                    } else {
                        writer.write(" & ");
                    }
                    if (transpose) {
                        if (color != null && color[row][col] != null) {
                            writer.write("\\cellcolor{" + color[row][col] + "}");
                        }
                        writer.write(table[row][col] == null ? "" : table[row][col]);
                    } else {
                        if (color != null && color[col][row] != null) {
                            writer.write("\\cellcolor{" + color[col][row] + "}");
                        }
                        writer.write(table[col][row] == null ? "" : table[col][row]);
                    }
                }
                writer.write("\\\\\\hline");
                writer.newLine();
            }
            writer.write("\\end{tabular}");
            writer.newLine();
            remainingCols -= cols;
        } while (remainingCols > 0);
    }

    /**
     * Prints the beginning of the TikZ picture environment to the specified writer, including style settings for
     * arrays or trees.
     * @param style The style to use.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printTikzBeginning(final TikZStyle style, final BufferedWriter writer) throws IOException {
        writer.write("\\begin{tikzpicture}");
        writer.newLine();
        writer.write(style.style);
        writer.newLine();
    }

    /**
     * Prints the end of the TikZ picture environment to the specified writer.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printTikzEnd(final BufferedWriter writer) throws IOException {
        writer.write("\\end{tikzpicture}");
        writer.newLine();
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
            TikZUtils.printTree(array, 0, to, writer);
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
            TikZUtils.printTree(array, next, end, writer);
            if (next + 1 <= end) {
                TikZUtils.printTree(array, next + 1, end, writer);
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
        final String firstName = "n" + TikZUtils.number++;
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
            writer.write("n" + TikZUtils.number++);
            writer.write(") [below=");
            if (separate != null && separate[i - 1]) {
                writer.write("0.1 ");
            }
            writer.write("of ");
            writer.write("n" + (TikZUtils.number - 2));
            final int val = array[i];
            writer.write("] {" + (val < 10 ? "\\phantom{0}" : "") + val);
            writer.write("};");
            writer.newLine();
        }
        return firstName;
    }

    /**
     * Prints vertical space
     * @param step The next evaluation step.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printVerticalSpace(final int step, final BufferedWriter writer) throws IOException {
        if (step % 3 == 0) {
            writer.newLine();
            writer.write("~\\\\");
            writer.newLine();
            writer.newLine();
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
    public static String printVerticalStringArray(
        final String[] array,
        final boolean[] separate,
        final boolean[] mark,
        final String left,
        final BufferedWriter writer
        ) throws IOException {
        final String firstName = "n" + TikZUtils.number++;
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
            writer.write("n" + TikZUtils.number++);
            writer.write(") [below=");
            if (separate != null && separate[i - 1]) {
                writer.write("0.1 ");
            }
            writer.write("of ");
            writer.write("n" + (TikZUtils.number - 2));
            final String val = array[i];
            writer.write("] {" + val);
            writer.write("};");
            writer.newLine();
        }
        return firstName;
    }

}
