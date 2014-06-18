import java.io.*;

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
     * The name of the enumerate environment.
     */
    public static final String ENUMERATE = "enumerate";

    /**
     * The item command.
     */
    public static final String ITEM = "\\item";

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
        writer.write("\\usepackage[a4paper,margin=2cm]{geometry}");
        writer.write("\\usepackage{tikz}");
        writer.write(
            "\\usetikzlibrary{arrows,shapes.misc,shapes.arrows,shapes.multipart,shapes.geometric,chains,"
            + "matrix,positioning,scopes,decorations.pathmorphing,decorations.pathreplacing,shadows,calc,trees}"
        );
        writer.write("\\usepackage{tikz-qtree}");
        writer.newLine();
        writer.newLine();
        writer.write("\\setlength{\\parindent}{0pt}");
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
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public static void printSamePageBeginning(int step, BufferedWriter writer) throws IOException {
        writer.write("\\begin{minipage}{\\columnwidth}");
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
