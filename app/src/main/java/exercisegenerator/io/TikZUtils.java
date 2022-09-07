package exercisegenerator.io;

import java.io.*;
import java.math.*;
import java.util.*;
import java.util.stream.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.structures.*;

/**
 * This abstract class provides methods for creating TikZ output.
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

    public static String code(final String text) {
        return String.format("\\code{%s}", text);
    }

    /**
     * Prints a new stretch factor for the array height.
     * @param stretch The stretch factor.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printArrayStretch(final double stretch, final BufferedWriter writer) throws IOException {
        writer.write("\\renewcommand{\\arraystretch}{" + stretch + "}");
        Main.newLine(writer);
    }

    /**
     * Prints the beginning of the specified environment.
     * @param environment The environment.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printBeginning(final String environment, final BufferedWriter writer) throws IOException {
        writer.write("\\begin{" + environment + "}");
        Main.newLine(writer);
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
        Main.newLine(writer);
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
        Main.newLine(writer);
    }

    public static String printEmptyArrayAndReturnLeftmostNodesName(
        final int length,
        final Optional<String> below,
        final int contentLength,
        final BufferedWriter writer
    ) throws IOException {
        return TikZUtils.printListAndReturnLeftmostNodesName(
            Stream.generate(() -> new ItemWithTikZInformation<>()).limit(length).toList(),
            below,
            contentLength,
            writer
        );
    }

    /**
     * Prints a row of empty nodes as solution space for the contents of the array with array indices above.
     * @param length The length of the array.
     * @param writer The writer to send the output to.
     * @throws IOException If some I/O error occurs.
     */
    public static void printEmptyArrayWithIndex(final int length, final BufferedWriter writer) throws IOException {
        TikZUtils.printListAndReturnLeftmostNodesName(
            IntStream.range(0, length).mapToObj(i -> new ItemWithTikZInformation<>(i)).toList(),
            Optional.empty(),
            Algorithm.DEFAULT_CONTENT_LENGTH,
            writer
        );
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
            Main.newLine(writer);
        } else {
            writer.write("\\node[node] (");
            writer.write(firstName);
            writer.write(") [right=of ");
            writer.write(left);
            writer.write("] {\\phantom{00}};");
            Main.newLine(writer);
        }
        for (int i = 1; i < length; i++) {
            writer.write("\\node[node] (n" + TikZUtils.number++);
            writer.write(") [below=of n" + (TikZUtils.number - 2));
            writer.write("] {\\phantom{00}};");
            Main.newLine(writer);
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
        writer.write("\\end{");
        writer.write(environment);
        writer.write("}");
        Main.newLine(writer);
    }

    public static void printFlushRightBeginning(final BufferedWriter writer) throws IOException {
        writer.write("\\begin{flushright}");
        Main.newLine(writer);
    }

    public static void printFlushRightEnd(final BufferedWriter writer) throws IOException {
        writer.write("\\end{flushright}");
        Main.newLine(writer);
    }

    /**
     * Prints the header of a LaTeX file with the required packages and settings for our exercise environment.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printLaTeXBeginning(final BufferedWriter writer) throws IOException {
        writer.write("\\documentclass{article}");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("\\usepackage[table]{xcolor}");
        Main.newLine(writer);
        writer.write("\\usepackage[a4paper,margin=2cm]{geometry}");
        Main.newLine(writer);
        writer.write("\\usepackage{tikz}");
        Main.newLine(writer);
        writer.write("\\usetikzlibrary{arrows,shapes.misc,shapes.arrows,shapes.multipart,shapes.geometric,chains,");
        writer.write("matrix,positioning,scopes,decorations.pathmorphing,decorations.pathreplacing,shadows,calc,");
        writer.write("trees,backgrounds}");
        Main.newLine(writer);
        writer.write("\\usepackage{tikz-qtree}");
        Main.newLine(writer);
        writer.write("\\usepackage{array}");
        Main.newLine(writer);
        writer.write("\\usepackage{amsmath}");
        Main.newLine(writer);
        writer.write("\\usepackage{enumerate}");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("\\newcolumntype{C}[1]{>{\\centering\\let\\newline\\\\\\arraybackslash\\hspace{0pt}}m{#1}}");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("\\setlength{\\parindent}{0pt}");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("\\newcommand{\\emphasize}[1]{\\textbf{#1}}");
        Main.newLine(writer);
        writer.write("\\newcommand*\\circled[1]{\\tikz[baseline=(char.base)]{");
        Main.newLine(writer);
        writer.write("            \\node[shape=circle,draw,inner sep=2pt] (char) {#1};}}");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("\\begin{document}");
        Main.newLine(writer);
        Main.newLine(writer);
    }

    /**
     * Prints the end of a LaTeX document.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printLaTeXEnd(final BufferedWriter writer) throws IOException {
        Main.newLine(writer);
        writer.write("\\end{document}");
        Main.newLine(writer);
        Main.newLine(writer);
    }

    public static String printListAndReturnLeftmostNodesName(
        final List<? extends ItemWithTikZInformation<?>> list,
        final Optional<String> below,
        final int contentLength,
        final BufferedWriter writer
    ) throws IOException {
        final ItemWithTikZInformation<?> firstItem = list.get(0);
        final String firstName =
            TikZUtils.printListItemAndReturnNodeName(
                firstItem.optionalContent,
                below.isEmpty() ?
                    Optional.empty() :
                        Optional.of(new TikZNodeOrientation(below.get(), TikZNodeDirection.BELOW)),
                firstItem.marker,
                firstItem.separateBefore,
                Optional.empty(),
                contentLength,
                writer
            );
        String previousName = firstName;
        for (int i = 1; i < list.size(); i++) {
            final ItemWithTikZInformation<?> item = list.get(i);
            previousName =
                TikZUtils.printListItemAndReturnNodeName(
                    item.optionalContent,
                    Optional.of(new TikZNodeOrientation(previousName, TikZNodeDirection.RIGHT)),
                    item.marker,
                    item.separateBefore,
                    Optional.empty(),
                    contentLength,
                    writer
                );
        }
        return firstName;
    }

    public static void printMinipageBeginning(final String length, final BufferedWriter writer) throws IOException {
        writer.write("\\begin{minipage}{");
        writer.write(length);
        writer.write("}");
        Main.newLine(writer);
    }

    public static void printMinipageEnd(final BufferedWriter writer) throws IOException {
        TikZUtils.printEnd("minipage", writer);
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
        Main.newLine(writer);
    }

    /**
     * Prints a protected whitespace and a line terminator to the specified writer.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printProtectedNewline(final BufferedWriter writer) throws IOException {
        writer.write("~\\\\*\\vspace*{1ex}");
        Main.newLine(writer);
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
        Main.newLine(writer);
        writer.write("\\vspace*{2ex}");
        Main.newLine(writer);
        Main.newLine(writer);
        if (op.y) {
            writer.write("Schritt " + step + ": F\\\"uge " + op.x + " ein\\\\[-2ex]");
        } else {
            writer.write("Schritt " + step + ": L\\\"osche " + op.x + "\\\\[-2ex]");
        }
        Main.newLine(writer);
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
        Main.newLine(writer);
        writer.write("\\vspace*{1ex}");
        Main.newLine(writer);
        writer.write("Schritt " + step + ":\\\\[-2ex]");
        Main.newLine(writer);
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
        Main.newLine(writer);
    }

    public static void printSolutionSpaceBeginning(final Parameters options, final BufferedWriter writer)
    throws IOException {
        if (Main.standalone(options)) {
            return;
        }
        TikZUtils.printToggleForSolutions(writer);
        TikZUtils.printVerticalProtectedSpace("-3ex", writer);
        TikZUtils.printElse(writer);
    }

    public static void printSolutionSpaceEnd(final Parameters options, final BufferedWriter writer) throws IOException {
        if (Main.standalone(options)) {
            Main.newLine(writer);
            Main.newLine(writer);
            return;
        }
        TikZUtils.printVerticalProtectedSpace(writer);
        TikZUtils.printEndIf(writer);
        Main.newLine(writer);
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
            Main.newLine(writer);
            writer.write("\\hline");
            Main.newLine(writer);
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
                Main.newLine(writer);
            }
            writer.write("\\end{tabular}");
            Main.newLine(writer);
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
        Main.newLine(writer);
        writer.write(style.style);
        Main.newLine(writer);
    }

    /**
     * Prints the end of the TikZ picture environment to the specified writer.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printTikzEnd(final BufferedWriter writer) throws IOException {
        writer.write("\\end{tikzpicture}");
        Main.newLine(writer);
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
            Main.newLine(writer);
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
            Main.newLine(writer);
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
            Main.newLine(writer);
        }
        return firstName;
    }

    public static void printVerticalProtectedSpace(final BufferedWriter writer) throws IOException {
        TikZUtils.printVerticalProtectedSpace("1ex", writer);
    }

    public static void printVerticalProtectedSpace(final String space, final BufferedWriter writer) throws IOException {
        Main.newLine(writer);
        writer.write(String.format("\\vspace*{%s}", space));
        Main.newLine(writer);
        Main.newLine(writer);
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
            Main.newLine(writer);
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
            Main.newLine(writer);
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
            Main.newLine(writer);
        }
        return firstName;
    }

    public static void reset() {
        TikZUtils.number = 0;
    }

    public static String widthOf(final String text) {
        return String.format("\\widthof{%s}", text);
    }

    public static String widthOfComplement(final String text) {
        return String.format("\\textwidth-\\widthof{%s}", text);
    }

    private static void printElse(final BufferedWriter writer) throws IOException {
        writer.write("\\else");
        Main.newLine(writer);
    }

    private static void printEndIf(final BufferedWriter writer) throws IOException {
        writer.write("\\fi");
        Main.newLine(writer);
    }

    private static String printListItemAndReturnNodeName(
        final Optional<?> optionalContent,
        final Optional<TikZNodeOrientation> optionalNodeOrientation,
        final boolean marker,
        final boolean separateBefore,
        final Optional<Integer> optionalIndex,
        final int contentLength,
        final BufferedWriter writer
    ) throws IOException {
        final int currentNumber = TikZUtils.number++;
        final String name = "n" + currentNumber;
        writer.write("\\node[node");
        if (marker) {
            writer.write(",fill=black!20");
        }
        writer.write("] (");
        writer.write(name);
        writer.write(") ");
        if (optionalNodeOrientation.isPresent()) {
            final TikZNodeOrientation nodeOrientation = optionalNodeOrientation.get();
            writer.write("[");
            writer.write(nodeOrientation.direction.tikz);
            writer.write("=");
            if (separateBefore) {
                writer.write("0.1 ");
            }
            writer.write("of ");
            writer.write(nodeOrientation.name);
            writer.write("] ");
        }
        writer.write("{");
        if (optionalContent.isPresent()) {
            final String content = optionalContent.get().toString();
            final int contentLengthDiff = contentLength - content.length();
            if (contentLengthDiff > 0) {
                TikZUtils.printPhantomSpace(contentLengthDiff, writer);
            }
            writer.write(content);
        } else {
            TikZUtils.printPhantomSpace(contentLength, writer);
        }
        writer.write("};");
        Main.newLine(writer);
        if (optionalIndex.isPresent()) {
            writer.write("\\node (l");
            writer.write(String.valueOf(currentNumber));
            writer.write(") [above=0 of ");
            writer.write(name);
            writer.write("] {\\scriptsize\\texttt{a[");
            writer.write(String.valueOf(optionalIndex.get()));
            writer.write("]}};");
            Main.newLine(writer);
        }
        return name;
    }

    private static void printPhantomSpace(final int numOfZerosForSpace, final BufferedWriter writer) throws IOException {
        writer.write("\\phantom{");
        TikZUtils.printZeros(numOfZerosForSpace, writer);
        writer.write("}");
    }

    private static void printToggleForSolutions(final BufferedWriter writer) throws IOException {
        writer.write("\\ifprintanswers");
        Main.newLine(writer);
    }

    private static void printZeros(final int numOfZeros, final BufferedWriter writer) throws IOException {
        for (int i = 0; i < numOfZeros; i++) {
            writer.write("0");
        }
    }

}
