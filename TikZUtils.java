import java.io.*;
import java.math.*;

/**
 * This abstract class provides methods for creating TikZ output.
 * @author cryingshadow
 * @version $Id$
 */
public abstract class TikZUtils {

    /**
     * The name of the center environment.
     */
    public static final String CENTER = "center";

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
     * The column width.
     */
    public static final String COL_WIDTH = "\\columnwidth";

    /**
     * A bit less than half the column width.
     */
    public static final String TWO_COL_WIDTH = "8cm";

    /**
     * Prints a new stretch factor for the array height.
     * @param stretch The stretch factor.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printArrayStretch(double stretch, BufferedWriter writer) throws IOException {
        writer.write("\\renewcommand{\\arraystretch}{" + stretch + "}");
        writer.newLine();
    }

    /**
     * Prints the beginning of the specified environment.
     * @param environment The environment.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printBeginning(String environment, BufferedWriter writer) throws IOException {
        writer.write("\\begin{" + environment + "}");
        writer.newLine();
    }

    /**
     * Prints a B-tree to the specified writer.
     * @param tree The B-tree.
     * @param writer The writer.
     * @throws IOException If some error occurs during output.
     */
    public static void printBTree(IntBTree tree, BufferedWriter writer) throws IOException {
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
    public static <E> void printEdge(String style, BigInteger from, E label, BigInteger to, BufferedWriter writer)
    throws IOException {
        StringBuilder res = new StringBuilder();
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
     * Prints the end of the specified environment.
     * @param environment The environment.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printEnd(String environment, BufferedWriter writer) throws IOException {
        writer.write("\\end{" + environment + "}");
        writer.newLine();
    }

    /**
     * Prints the header of a LaTeX file with the required packages and settings for our exercise environment.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printLaTeXBeginning(BufferedWriter writer) throws IOException {
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
        writer.newLine();
        writer.write("\\newcolumntype{C}[1]{>{\\centering\\let\\newline\\\\\\arraybackslash\\hspace{0pt}}m{#1}}");
        writer.newLine();
        writer.newLine();
        writer.write("\\setlength{\\parindent}{0pt}");
        writer.newLine();
        writer.newLine();
        writer.write("\\newcommand{\\emphasize}[1]{\\textbf{#1}}");
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
    public static void printLaTeXEnd(BufferedWriter writer) throws IOException {
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
    public static <N> void printNode (Node<N> node, String style, String position, BufferedWriter writer)
    throws IOException {
        StringBuilder res = new StringBuilder();
        res.append("\\node");
        res.append(style);
        res.append(" (n");
        res.append(node.getID().toString());
        res.append(") ");
        res.append(position);
        res.append("{");
        N label = node.getLabel();
        if (label != null) {
            res.append(label.toString());
        }
        res.append("};");
        writer.write(res.toString());
        writer.newLine();
    }

    /**
     * Prints a protected whitespace and a line terminator to the specified writer.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printProtectedNewline(BufferedWriter writer) throws IOException {
        writer.write("~\\\\*\\vspace*{1ex}");
        writer.newLine();
    }

    /**
     * Prints the beginning of a samepage environment.
     * @param step The current evaluation step.
     * @param width A LaTeX String indicating the width of the minipage.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printSamePageBeginning(int step, String width, BufferedWriter writer) throws IOException {
        writer.write("\\begin{minipage}{" + width + "}");
        writer.newLine();
        writer.write("\\vspace*{1ex}");
        writer.newLine();
        writer.write("Schritt " + step + ":\\\\[-2ex]");
        writer.newLine();
        TikZUtils.printBeginning(TikZUtils.CENTER, writer);
    }

    /**
     * Prints the beginning of a samepage environment.
     * @param step The current evaluation step.
     * @param op The current operation.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printSamePageBeginning(int step, Pair<Integer, Boolean> op, BufferedWriter writer)
    throws IOException {
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
     * Prints the end of a samepage environment.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printSamePageEnd(BufferedWriter writer) throws IOException {
        TikZUtils.printEnd(TikZUtils.CENTER, writer);
        writer.write("\\end{minipage}");
        writer.newLine();
    }

    /**
     * Prints a table by centering each column.
     * @param table A two-dimensional table of Strings.
     * @param color Color settings for each cell. Null means no color. The array must not be null.
     * @param width The column width.
     * @param writer The writer to send the output to.
     * @param transpose Transpose the table?
     * @throws IOException If some error occurs during output.
     */
    public static void printTable(
        String[][] table,
        String[][] color,
        String width,
        BufferedWriter writer,
        boolean transpose,
        int breakAtColumn
    ) throws IOException {
        int allCols = (transpose ? table[0].length : table.length);
        int remainingCols = allCols;
        do {
            int cols = remainingCols > breakAtColumn && breakAtColumn > 0 ? breakAtColumn : remainingCols;
            int rows = (transpose ? table.length : table[0].length);
            writer.write("\\begin{tabular}{|*{" + cols + "}{C{" + width + "}|}}");
            writer.newLine();
            writer.write("\\hline");
            writer.newLine();
            for (int row = 0; row < rows; row++) {
                boolean first = true;
                int startCol = allCols - remainingCols;
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
    public static void printTikzBeginning(TikZStyle style, BufferedWriter writer) throws IOException {
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
    public static void printTikzEnd(BufferedWriter writer) throws IOException {
        writer.write("\\end{tikzpicture}");
        writer.newLine();
    }

    /**
     * Prints vertical space
     * @param step The next evaluation step.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printVerticalSpace(int step, BufferedWriter writer) throws IOException {
        if (step % 3 == 0) {
            writer.newLine();
            writer.write("~\\\\");
            writer.newLine();
            writer.newLine();
        }
    }

}
