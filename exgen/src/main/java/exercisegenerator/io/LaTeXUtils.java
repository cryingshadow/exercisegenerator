package exercisegenerator.io;

import java.io.*;
import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.apache.commons.math3.fraction.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;
import exercisegenerator.structures.trees.*;

public abstract class LaTeXUtils {

    public static final String CENTER = "center";

    public static final String COL_WIDTH = "\\columnwidth";

    public static final String ENUMERATE = "enumerate";

    public static final String ITEM = "\\item";

    public static final String MATH_VARIABLE_NAME = "x";

    public static final String TWO_COL_WIDTH = "8cm";

    private static final int MAX_NUMBER_OF_ARRAY_CELLS_IN_A_ROW = 17;

    private static int number = 0;

    public static void beginMulticols(final int cols, final BufferedWriter writer) throws IOException {
        writer.write(String.format("\\begin{multicols}{%d}", cols));
        Main.newLine(writer);
    }

    public static String bold(final String text) {
        return String.format("\\textbf{%s}", text);
    }

    public static String code(final String text) {
        return String.format("\\code{%s}", text);
    }

    public static String codeseq(final String text) {
        return String.format("\\codeseq{%s}", text);
    }

    public static Function<Integer, String> defaultColumnDefinition(final String width) {
        return cols -> String.format("|*{%d}{C{%s}|}", cols, width);
    }

    public static String displayMath(final String content) {
        return String.format("\\[%s\\]", content);
    }

    public static void endMulticols(final BufferedWriter writer) throws IOException {
        writer.write("\\end{multicols}");
        Main.newLine(writer);
    }

    public static String escapeForLaTeX(final char c) {
        return LaTeXUtils.escapeForLaTeX(String.valueOf(c));
    }

    public static String escapeForLaTeX(final String text) {
        return text.replaceAll("\\\\", "\\\\textbackslash")
            .replaceAll("([&\\$%\\{\\}_#])", "\\\\$1")
            .replaceAll("~", "\\\\textasciitilde{}")
            .replaceAll("\\^", "\\\\textasciicircum{}")
            .replaceAll("\\\\textbackslash", "\\\\textbackslash{}")
            .replaceAll("([^\\\\])\"", "$1''")
            .replaceAll("^\"", "''");
    }

    public static String inlineMath(final String content) {
        return String.format("$%s$", content);
    }

    public static String mathematicalSet(final Collection<?> elements) {
        return LaTeXUtils.mathematicalSet(elements.stream());
    }

    public static String mathematicalSet(final Stream<?> elements) {
        return String.format("\\{%s\\}", elements.map(x -> x.toString()).collect(Collectors.joining(",")));
    }

    public static void printAdjustboxBeginning(final BufferedWriter writer, final String... parameters) throws IOException {
        writer.write("\\begin{adjustbox}{");
        writer.write(Arrays.stream(parameters).collect(Collectors.joining(",")));
        writer.write("}");
        Main.newLine(writer);
    }

    public static void printAdjustboxEnd(final BufferedWriter writer) throws IOException {
        writer.write("\\end{adjustbox}");
        Main.newLine(writer);
    }

    public static void printArrayStretch(final double stretch, final BufferedWriter writer) throws IOException {
        writer.write("\\renewcommand{\\arraystretch}{" + stretch + "}");
        Main.newLine(writer);
    }

    public static void printBeginning(final String environment, final BufferedWriter writer) throws IOException {
        writer.write("\\begin{" + environment + "}");
        Main.newLine(writer);
    }

    public static void printBTree(final BTree<Integer> tree, final BufferedWriter writer) throws IOException {
        if (tree.hasJustRoot()) {
            writer.write("\\node[draw=black,rounded corners,thick,inner sep=5pt] " + tree.toString() + ";");
        } else if (tree.isEmpty()) {
            writer.write("\\node {" + tree.toString() + "};");
        } else {
            writer.write("\\Tree " + tree.toString() + ";");
        }
        Main.newLine(writer);
    }

    public static <E> void printEdge(
        final TikZStyle style,
        final BigInteger from,
        final Optional<E> label,
        final BigInteger to,
        final BufferedWriter writer
    ) throws IOException {
        final StringBuilder res = new StringBuilder();
        res.append("\\draw");
        res.append(style.style);
        res.append(" (n");
        res.append(from.toString());
        res.append(") to ");
        if (label.isPresent()) {
            res.append("node[auto, swap] {");
            res.append(label.get().toString());
            res.append("} ");
        }
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
        return LaTeXUtils.printListAndReturnLowestLeftmostNodesName(
            Stream.generate(() -> new ItemWithTikZInformation<>()).limit(length).toList(),
            below,
            contentLength,
            writer
        );
    }

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

    public static void printLaTeXBeginning(final BufferedWriter writer) throws IOException {
        writer.write("\\documentclass{article}");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("\\usepackage[ngerman]{babel}");
        Main.newLine(writer);
        writer.write("\\usepackage[T1]{fontenc}");
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
        writer.write("\\usepackage{calc}");
        Main.newLine(writer);
        writer.write("\\usepackage{array}");
        Main.newLine(writer);
        writer.write("\\usepackage{amsmath}");
        Main.newLine(writer);
        writer.write("\\usepackage{enumitem}");
        Main.newLine(writer);
        writer.write("\\usepackage{seqsplit}");
        Main.newLine(writer);
        writer.write("\\usepackage{multicol}");
        Main.newLine(writer);
        writer.write("\\usepackage{adjustbox}");
        Main.newLine(writer);
        writer.write("\\usepackage[output-decimal-marker={,}]{siunitx}");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("\\newcolumntype{C}[1]{>{\\centering\\let\\newline\\\\\\arraybackslash\\hspace{0pt}}m{#1}}");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("\\setlength{\\parindent}{0pt}");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("\\newcommand{\\code}[1]{\\textnormal{\\texttt{#1}}}");
        Main.newLine(writer);
        writer.write("\\newcommand{\\codeseq}[1]{{\\ttfamily\\seqsplit{#1}}}");
        Main.newLine(writer);
        writer.write("\\newcommand{\\emphasize}[1]{\\textbf{#1}}");
        Main.newLine(writer);
        writer.write("\\newcommand*{\\circled}[1]{\\tikz[baseline=(char.base)]{");
        Main.newLine(writer);
        writer.write("            \\node[shape=circle,draw,inner sep=2pt] (char) {#1};}}");
        Main.newLine(writer);
        writer.write("\\newcommand{\\var}[1]{\\textit{#1}}");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("\\begin{document}");
        Main.newLine(writer);
        Main.newLine(writer);
    }

    public static void printLaTeXEnd(final BufferedWriter writer) throws IOException {
        Main.newLine(writer);
        writer.write("\\end{document}");
        Main.newLine(writer);
        Main.newLine(writer);
    }

    public static String printListAndReturnLowestLeftmostNodesName(
        final List<? extends ItemWithTikZInformation<?>> list,
        final Optional<String> below,
        final int contentLength,
        final BufferedWriter writer
    ) throws IOException {
        final ItemWithTikZInformation<?> firstItem = list.get(0);
        final String firstName =
            LaTeXUtils.printListItemAndReturnNodeName(
                firstItem.optionalContent,
                below.isEmpty() ?
                    Optional.empty() :
                        Optional.of(new TikZNodeOrientation(below.get(), TikZNodeDirection.BELOW)),
                firstItem.marker,
                firstItem.separateBefore,
                firstItem.optionalIndex,
                contentLength,
                writer
            );
        String previousName = firstName;
        String currentLeftMostName = firstName;
        for (int i = 1; i < list.size(); i++) {
            final ItemWithTikZInformation<?> item = list.get(i);
            if (i % LaTeXUtils.MAX_NUMBER_OF_ARRAY_CELLS_IN_A_ROW == 0) {
                previousName =
                    LaTeXUtils.printListItemAndReturnNodeName(
                        item.optionalContent,
                        Optional.of(new TikZNodeOrientation(currentLeftMostName, TikZNodeDirection.BELOW)),
                        item.marker,
                        item.separateBefore,
                        item.optionalIndex,
                        contentLength,
                        writer
                    );
                currentLeftMostName = previousName;
            } else {
                previousName =
                    LaTeXUtils.printListItemAndReturnNodeName(
                        item.optionalContent,
                        Optional.of(new TikZNodeOrientation(previousName, TikZNodeDirection.RIGHT)),
                        item.marker,
                        item.separateBefore,
                        item.optionalIndex,
                        contentLength,
                        writer
                    );
            }
        }
        return currentLeftMostName;
    }

    public static void printMinipageBeginning(final String length, final BufferedWriter writer) throws IOException {
        writer.write("\\begin{minipage}{");
        writer.write(length);
        writer.write("}");
        Main.newLine(writer);
    }

    public static void printMinipageEnd(final BufferedWriter writer) throws IOException {
        LaTeXUtils.printEnd("minipage", writer);
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
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
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
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
    }

    /**
     * Prints the end of a samepage environment.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printSamePageEnd(final BufferedWriter writer) throws IOException {
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
        writer.write("\\end{minipage}");
        Main.newLine(writer);
    }

    public static void printSolutionSpaceBeginning(
        final Optional<String> protectedSpace,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        if (Main.standalone(options)) {
            return;
        }
        if (Main.embeddedExam(options)) {
            LaTeXUtils.printToggleForSolutions(writer);
            if (protectedSpace.isPresent()) {
                LaTeXUtils.printVerticalProtectedSpace(protectedSpace.get(), writer);
            }
            LaTeXUtils.printElse(writer);
        } else {
            writer.write("\\solutionSpace{");
            Main.newLine(writer);
        }
    }

    public static void printSolutionSpaceEnd(
        final Optional<String> protectedSpace,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        if (Main.standalone(options)) {
            Main.newLine(writer);
            Main.newLine(writer);
        } else  if (Main.embeddedExam(options)) {
            if (protectedSpace.isPresent()) {
                LaTeXUtils.printVerticalProtectedSpace(protectedSpace.get(), writer);
            }
            LaTeXUtils.printEndIf(writer);
            Main.newLine(writer);
        } else {
            writer.write("}");
            Main.newLine(writer);
        }
    }

    public static void printTable(
        final String[][] table,
        final Optional<String[][]> optionalColor,
        final Function<Integer, String> columnDefinition,
        final boolean transpose,
        final int breakAtColumn,
        final boolean titleColumn,
        final BufferedWriter writer
    ) throws IOException {
        final int allCols = (transpose ? table[0].length : table.length);
        int remainingCols = allCols;
        boolean firstColumnBlock = true;
        do {
            final int cols =
                LaTeXUtils.computeNumberOfColumns(remainingCols, breakAtColumn, titleColumn, firstColumnBlock);
//                remainingCols > breakAtColumn && breakAtColumn > 0 ? breakAtColumn : remainingCols;
//                LaTeXUtils.computeNumberOfColumns(remainingCols, breakAtColumn, titleColumn, firstRow);
            final int rows = (transpose ? table.length : table[0].length);
            writer.write("\\begin{tabular}{");
            writer.write(columnDefinition.apply(titleColumn && !firstColumnBlock ? cols + 1 : cols));
            writer.write("}");
            Main.newLine(writer);
            writer.write("\\hline");
            Main.newLine(writer);
            for (int row = 0; row < rows; row++) {
                boolean first = true;
                final int startCol = allCols - remainingCols;
                for (int col = startCol; col < startCol + cols; col++) {
                    if (first) {
                        first = false;
                        if (titleColumn && !firstColumnBlock) {
                            LaTeXUtils.writeTableCell(0, row, transpose, optionalColor, table, writer);
                            writer.write(" & ");
                        }
                    } else {
                        writer.write(" & ");
                    }
                    LaTeXUtils.writeTableCell(col, row, transpose, optionalColor, table, writer);
                }
                writer.write("\\\\\\hline");
                Main.newLine(writer);
            }
            writer.write("\\end{tabular}");
            Main.newLine(writer);
            remainingCols -= cols;
            firstColumnBlock = false;
        } while (remainingCols > 0);
    }

    public static void printTable(
        final String[][] table,
        final Optional<String[][]> optionalColor,
        final Function<Integer, String> columnDefinition,
        final boolean transpose,
        final int breakAtColumn,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printTable(table, optionalColor, columnDefinition, transpose, breakAtColumn, false, writer);
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
     * Prints a String representation of the specified node suitable for TikZ output to the specified writer.
     * @param node The node to print.
     * @param style The node style (may be empty, but not null).
     * @param position The position of the node (may be empty, but not null).
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static <N> void printVertex (
        final Vertex<N> node,
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
        final String firstName = "n" + LaTeXUtils.number++;
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
            writer.write("n" + LaTeXUtils.number++);
            writer.write(") [below=");
            if (separate != null && separate[i - 1]) {
                writer.write("0.1 ");
            }
            writer.write("of ");
            writer.write("n" + (LaTeXUtils.number - 2));
            final int val = array[i];
            writer.write("] {" + (val < 10 ? "\\phantom{0}" : "") + val);
            writer.write("};");
            Main.newLine(writer);
        }
        return firstName;
    }

    public static void printVerticalProtectedSpace(final BufferedWriter writer) throws IOException {
        LaTeXUtils.printVerticalProtectedSpace("1ex", writer);
    }

    public static void printVerticalProtectedSpace(final String space, final BufferedWriter writer) throws IOException {
        Main.newLine(writer);
        writer.write(String.format("\\vspace*{%s}", space));
        Main.newLine(writer);
        Main.newLine(writer);
    }

    public static void printVerticalSpace(final String space, final BufferedWriter writer) throws IOException {
        Main.newLine(writer);
        writer.write(String.format("\\vspace{%s}", space));
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
        final String firstName = "n" + LaTeXUtils.number++;
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
            writer.write("n" + LaTeXUtils.number++);
            writer.write(") [below=");
            if (separate != null && separate[i - 1]) {
                writer.write("0.1 ");
            }
            writer.write("of ");
            writer.write("n" + (LaTeXUtils.number - 2));
            final String val = array[i];
            writer.write("] {" + val);
            writer.write("};");
            Main.newLine(writer);
        }
        return firstName;
    }

    public static void reset() {
        LaTeXUtils.number = 0;
    }

    public static void resizeboxBeginning(
        final String horizontal,
        final String vertical,
        final BufferedWriter writer
    ) throws IOException {
        writer.write(String.format("\\resizebox{%s}{%s}{%%", horizontal, vertical));
        Main.newLine(writer);
    }

    public static void resizeboxEnd(final BufferedWriter writer) throws IOException {
        writer.write("}");
        Main.newLine(writer);
    }

    public static String toCoefficient(final BigFraction coefficient) {
        if (coefficient.getDenominator().compareTo(BigInteger.ONE) == 0) {
            return String.valueOf(coefficient.intValue());
        }
        return LaTeXUtils.toFractionString(coefficient);
    }

    public static String toCoefficientWithVariable(
        final boolean first,
        final boolean matrix,
        final boolean firstNonZero,
        final int variableIndex,
        final BigFraction coefficient
    ) {
        if (coefficient.getDenominator().compareTo(BigInteger.ONE) == 0) {
            final int value = coefficient.intValue();
            if (value == 0) {
                if (matrix && !first) {
                    return " &  & ";
                }
                return "";
            }
            if (value == 1) {
                if (first) {
                    return String.format("%s_{%d}", LaTeXUtils.MATH_VARIABLE_NAME, variableIndex);
                }
                if (matrix) {
                    if (firstNonZero) {
                        return String.format(" &  & %s_{%d}", LaTeXUtils.MATH_VARIABLE_NAME, variableIndex);
                    }
                    return String.format(" & + & %s_{%d}", LaTeXUtils.MATH_VARIABLE_NAME, variableIndex);
                }
                return String.format(" + %s_{%d}", LaTeXUtils.MATH_VARIABLE_NAME, variableIndex);
            }
            if (value == -1) {
                if (first) {
                    return String.format("-%s_{%d}", LaTeXUtils.MATH_VARIABLE_NAME, variableIndex);
                }
                if (matrix) {
                    if (firstNonZero) {
                        return String.format(" &  & -%s_{%d}", LaTeXUtils.MATH_VARIABLE_NAME, variableIndex);
                    }
                    return String.format(" & - & %s_{%d}", LaTeXUtils.MATH_VARIABLE_NAME, variableIndex);
                }
                return String.format(" - %s_{%d}", LaTeXUtils.MATH_VARIABLE_NAME, variableIndex);
            }
            if (first) {
                return String.format("%d%s_{%d}", value, LaTeXUtils.MATH_VARIABLE_NAME, variableIndex);
            }
            if (value < 0) {
                if (matrix) {
                    if (firstNonZero) {
                        return String.format(
                            " &  & %d%s_{%d}",
                            value,
                            LaTeXUtils.MATH_VARIABLE_NAME,
                            variableIndex
                        );
                    }
                    return String.format(
                        " & - & %d%s_{%d}",
                        -value,
                        LaTeXUtils.MATH_VARIABLE_NAME,
                        variableIndex
                    );
                }
                return String.format(" - %d%s_{%d}", -value, LaTeXUtils.MATH_VARIABLE_NAME, variableIndex);
            }
            if (matrix) {
                if (firstNonZero) {
                    return String.format(" &  & %d%s_{%d}", value, LaTeXUtils.MATH_VARIABLE_NAME, variableIndex);
                }
                return String.format(" & + & %d%s_{%d}", value, LaTeXUtils.MATH_VARIABLE_NAME, variableIndex);
            }
            return String.format(" + %d%s_{%d}", value, LaTeXUtils.MATH_VARIABLE_NAME, variableIndex);
        }
        if (first) {
            return String.format(
                "%s%s_{%d}",
                LaTeXUtils.toFractionString(coefficient),
                LaTeXUtils.MATH_VARIABLE_NAME,
                variableIndex
            );
        }
        if (coefficient.compareTo(BigFraction.ZERO) < 0) {
            if (matrix) {
                if (firstNonZero) {
                    return String.format(
                        " &  & %s%s_{%d}",
                        LaTeXUtils.toFractionString(coefficient),
                        LaTeXUtils.MATH_VARIABLE_NAME,
                        variableIndex
                    );
                }
                return String.format(
                    " & - & %s%s_{%d}",
                    LaTeXUtils.toFractionString(coefficient.negate()),
                    LaTeXUtils.MATH_VARIABLE_NAME,
                    variableIndex
                );
            }
            return String.format(
                " - %s%s_{%d}",
                LaTeXUtils.toFractionString(coefficient.negate()),
                LaTeXUtils.MATH_VARIABLE_NAME,
                variableIndex
            );
        }
        if (matrix) {
            if (firstNonZero) {
                return String.format(
                    " &  & %s%s_{%d}",
                    LaTeXUtils.toFractionString(coefficient),
                    LaTeXUtils.MATH_VARIABLE_NAME,
                    variableIndex
                );
            }
            return String.format(
                " & + & %s%s_{%d}",
                LaTeXUtils.toFractionString(coefficient),
                LaTeXUtils.MATH_VARIABLE_NAME,
                variableIndex
            );
        }
        return String.format(
            " + %s%s_{%d}",
            LaTeXUtils.toFractionString(coefficient),
            LaTeXUtils.MATH_VARIABLE_NAME,
            variableIndex
        );
    }

    public static String toFractionString(final BigFraction coefficient) {
        final BigInteger numerator = coefficient.getNumerator();
        return String.format(
            "%s\\frac{%s}{%s}",
            numerator.compareTo(BigInteger.ZERO) < 0 ? "-" : "",
            numerator.abs().toString(),
            coefficient.getDenominator().toString()
        );
    }

    public static String widthOf(final String text) {
        return String.format("\\widthof{%s}", text);
    }

    public static String widthOfComplement(final String text) {
        return String.format("\\textwidth-\\widthof{%s}", text);
    }

    private static int computeNumberOfColumns(
        final int remainingCols,
        final int breakAtColumn,
        final boolean titleColumn,
        final boolean firstColumnBlock
    ) {
        if (remainingCols > breakAtColumn && breakAtColumn > 0) {
            if (titleColumn && !firstColumnBlock) {
                return breakAtColumn - 1;
            } else {
                return breakAtColumn;
            }
        }
        return remainingCols;
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
        final int currentNumber = LaTeXUtils.number++;
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
                LaTeXUtils.printPhantomSpace(contentLengthDiff, writer);
            }
            writer.write(content);
        } else {
            LaTeXUtils.printPhantomSpace(contentLength, writer);
        }
        writer.write("};");
        Main.newLine(writer);
        if (optionalIndex.isPresent()) {
            writer.write("\\node (l");
            writer.write(String.valueOf(currentNumber));
            writer.write(") [above=0 of ");
            writer.write(name);
            writer.write("] {\\scriptsize\\texttt{");
            writer.write(String.valueOf(optionalIndex.get()));
            writer.write("}};");
            Main.newLine(writer);
        }
        return name;
    }

    private static void printPhantomSpace(final int numOfZerosForSpace, final BufferedWriter writer) throws IOException {
        writer.write("\\phantom{");
        LaTeXUtils.printZeros(numOfZerosForSpace, writer);
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

    private static void writeTableCell(
        final int col,
        final int row,
        final boolean transpose,
        final Optional<String[][]> optionalColor,
        final String[][] table,
        final BufferedWriter writer
    ) throws IOException {
        final int transCol = transpose ? row : col;
        final int transRow = transpose ? col : row;
        if (optionalColor.isPresent()) {
            final String color = optionalColor.get()[transCol][transRow];
            if (color != null) {
                writer.write(String.format("\\cellcolor{%s}", color));
            }
        }
        final String content = table[transCol][transRow];
        if (content != null) {
            writer.write(content);
        }
    }

}
