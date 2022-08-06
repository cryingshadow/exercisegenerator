package exercisegenerator.algorithms;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.io.*;

/**
 * Class offering methods for dynamic programming.
 * @author Florian Corzilius, Thomas Stroeder
 * @version 1.0.1
 */
public abstract class DynamicProgramming {

    /**
     * Prints exercise and solution for solving a knapsack problem with dynamic programming.
     * @param weights The weights of the items to put into the knapsack.
     * @param values The values of the items to put into the knapsack.
     * @param capacity The capacity of the knapsack.
     * @param mode The preprint mode.
     * @param solWriter The writer to send the solution output to.
     * @param exWriter The writer to send the exercise output to.
     * @throws IOException If some error occurs during output.
     */
    public static void knapsack(
        final Integer[] weights,
        final Integer[] values,
        final Integer capacity,
        final PreprintMode mode,
        final BufferedWriter solWriter,
        final BufferedWriter exWriter
    ) throws IOException {
        // actual algorithm
        final int n = weights.length;
        final Integer[][] C = new Integer[n + 1][capacity + 1];
        final String[][] solutions = new String[n + 2][capacity + 2];
        for (int j = 0; j <= capacity; j++) {
            C[0][j] = 0;
            solutions[0][j + 1] = "" + j;
            solutions[1][j + 1] = "0";
        }
        solutions[0][0] = "";
        solutions[1][0] = "0";
        for (int i = 1; i <= n; i++) {
            solutions[i + 1][0] = "" + i;
            for (int j = 0; j <= capacity; j++) {
                if (weights[i - 1] <= j) {
                    final Integer valueA = values[i - 1] + C[i - 1][j - weights[i - 1]];
                    if (C[i - 1][j] < valueA) {
                        C[i][j] = valueA;
                    } else {
                        C[i][j] = C[i - 1][j];
                    }
                } else {
                    C[i][j] = C[i - 1][j];
                }
                solutions[i + 1][j + 1] = "" + C[i][j];
            }
        }
        // find the items to choose to get the maximum value
        int row = n;
        int column = capacity;
        final List<Integer> itemsToChoose = new ArrayList<Integer>();
        while (column > 0 && row > 0) {
            if (C[row][column] != C[row - 1][column]) {
                itemsToChoose.add(row);
                final Integer chosenWeight = weights[row - 1];
                for (int i = 0; i < chosenWeight; i++) {
                    solutions[row + 1][column + 1] = "$\\leftarrow$ " + solutions[row + 1][column + 1];
                    column--;
                }
                solutions[row + 1][column + 1] = "$\\uparrow$ " + solutions[row + 1][column + 1];
                row--;
            } else {
                solutions[row + 1][column + 1] = "$\\uparrow$ " + solutions[row + 1][column + 1];
                row--;
            }
        }
        // create output
        exWriter.write("Gegeben sei ein Rucksack mit \\emphasize{maximaler Tragkraft} " + capacity);
        exWriter.write(" sowie " + n + " \\emphasize{Gegenst\\\"ande}. Der $i$-te Gegenstand soll hierbei");
        exWriter.write(" ein Gewicht von $w_i$ und einen Wert von $c_i$ haben.");
        exWriter.write(" Bestimmen Sie mit Hilfe des in der Vorlesung vorgestellten Algorithmus zum L\\\"osen");
        exWriter.write(" des Rucksackproblems mit dynamischer Programmierung den maximalen");
        exWriter.write(" Gesamtwert der Gegenst\\\"ande, die der Rucksack tragen kann (das Gesamtgewicht der");
        exWriter.write(" mitgef\\\"uhrten Gegenst\\\"ande \\\"ubersteigt nicht die Tragkraft des Rucksacks).");
        exWriter.write(" Die \\emphasize{Gewichte} seien dabei $w_0=" + weights[0] + "$");
        for (int i = 1; i < n - 1; i++) {
            exWriter.write(", $w_{" + i + "} =" + weights[i] + "$");
        }
        exWriter.write(" und $w_{" + (n - 1) + "} =" + weights[n - 1]);
        exWriter.write("$ und die \\emphasize{Werte} $c_0=" + values[0] + "$");
        for (int i = 1; i < n - 1; i++) {
            exWriter.write(", $c_{" + i + "} =" + values[i] + "$");
        }
        exWriter.write(" und $c_{" + (n - 1) + "} =" + values[n - 1] + "$.");
        exWriter.write(" Geben Sie zudem die vom Algorithmus bestimmte Tabelle \\texttt{C} an");
        exWriter.write(" und beschreiben Sie anhand der Tabelle wie man die mitzunehmenden Gegenst\\\"ande");
        exWriter.write(" bestimmen kann, um den maximalen Wert zu erreichen.");
        exWriter.newLine();
        exWriter.newLine();
        solWriter.write("Die Tabelle \\texttt{C} wird vom Algorithmus wie folgt gef\\\"ullt:");
        solWriter.newLine();
        solWriter.newLine();
        final int tableWidth = Main.STUDENT_MODE ? 10 : 12;
//        System.out.println(capacity);
        if (capacity + 2 > tableWidth) {
            String[][] solutionsTmp = new String[n + 2][tableWidth];
            String[][] solutionsTmpEx = new String[n + 2][tableWidth];
            // copy first column (legend) for exercise
            for (int rowNr = 1; rowNr < n + 2; rowNr++) {
                solutionsTmpEx[rowNr][0] = solutions[rowNr][0];
            }
//            boolean remainderStarted = true;
            for (int columnNr = 0; columnNr < capacity + 2; columnNr++) {
                // System.out.println("columnNr = " + columnNr);
                for (int rowNr = 0; rowNr < n + 2; rowNr++) {
                    // System.out.println("add column " + (columnNr % tableWidth));
                    solutionsTmp[rowNr][columnNr % tableWidth] = solutions[rowNr][columnNr];
                }
                // copy first row (legend) for exercise
                solutionsTmpEx[0][columnNr % tableWidth] = solutions[0][columnNr];
                if (columnNr > 0 && (columnNr % tableWidth == tableWidth - 1 || columnNr == capacity + 1)) {
                    // we are at the last column of a table (table is filled completely)
                    TikZUtils.printBeginning(TikZUtils.CENTER, solWriter);
                    solWriter.write("{\\Large");
                    solWriter.newLine();
                    TikZUtils.printTable(solutionsTmp, null, "1.2cm", solWriter, true, 0);
                    solWriter.newLine();
                    solWriter.write("}");
                    solWriter.newLine();
                    TikZUtils.printEnd(TikZUtils.CENTER, solWriter);
                    solWriter.newLine();
                    switch (mode) {
                        case SOLUTION_SPACE:
                            TikZUtils.printSolutionSpaceBeginning(exWriter);
                            // fall-through
                        case ALWAYS:
                            TikZUtils.printBeginning(TikZUtils.CENTER, exWriter);
                            exWriter.write("{\\Large");
                            exWriter.newLine();
                            TikZUtils.printTable(solutionsTmpEx, null, "1.2cm", exWriter, true, 0);
                            exWriter.newLine();
                            exWriter.write("}");
                            exWriter.newLine();
                            TikZUtils.printEnd(TikZUtils.CENTER, exWriter);
                            if (mode == PreprintMode.SOLUTION_SPACE) {
                                TikZUtils.printSolutionSpaceEnd(exWriter);
                            }
                            exWriter.newLine();
                            break;
                        case NEVER:
                            // do nothing
                    }
                    // there are capacity + 2 - (columnNr + 1) columns left to go
                    final int columnNrTmp = Math.min(tableWidth, capacity + 1 - columnNr);
                    // System.out.println("columnNrTmp = " + columnNrTmp);
                    solutionsTmp = new String[n + 2][columnNrTmp];
                    solutionsTmpEx = new String[n + 2][columnNrTmp];
                }
            }
        } else {
            final String[][] solutionsTmpEx = new String[n + 2][capacity + 2];
            for (int rowNr = 0; rowNr < n + 2; rowNr++) {
                solutionsTmpEx[rowNr][0] = solutions[rowNr][0];
            }
            for (int columnNr = 1; columnNr < capacity + 2; columnNr++) {
                solutionsTmpEx[0][columnNr] = solutions[0][columnNr];
            }
            switch (mode) {
                case SOLUTION_SPACE:
                    TikZUtils.printSolutionSpaceBeginning(exWriter);
                    // fall-through
                case ALWAYS:
                    TikZUtils.printBeginning(TikZUtils.CENTER, exWriter);
                    exWriter.write("{\\Large");
                    exWriter.newLine();
                    TikZUtils.printTable(solutionsTmpEx, null, "1.2cm", exWriter, true, 0);
                    exWriter.newLine();
                    exWriter.write("}");
                    exWriter.newLine();
                    TikZUtils.printEnd(TikZUtils.CENTER, exWriter);
                    if (mode == PreprintMode.SOLUTION_SPACE) {
                        TikZUtils.printSolutionSpaceEnd(exWriter);
                    }
                    exWriter.newLine();
                    break;
                case NEVER:
                    // do nothing
            }
            TikZUtils.printBeginning(TikZUtils.CENTER, solWriter);
            solWriter.write("{\\Large");
            solWriter.newLine();
            TikZUtils.printTable(solutions, null, "1.2cm", solWriter, true, 0);
            solWriter.newLine();
            solWriter.write("}");
            solWriter.newLine();
            TikZUtils.printEnd(TikZUtils.CENTER, solWriter);
            solWriter.newLine();
        }
        solWriter.write("\\medskip");
        solWriter.newLine();
        solWriter.write("Damit ergibt sich der maximale Wert " + C[n][capacity] + " f\\\"ur den Fall, dass der");
        for (int i = itemsToChoose.size() - 1; i >= 0; i--) {
            if (i > 0 && i < itemsToChoose.size() - 1) {
                solWriter.write(",");
            } else if (i == 0) {
                solWriter.write(" und");
            }
            solWriter.write(" " + (itemsToChoose.get(i) - 1) + ".");
        }
        solWriter.write(" Gegenstand mitgenommen werden.");
        solWriter.newLine();
        solWriter.newLine();
        solWriter.write("Dies l\\\"asst sich von der Tabelle wie folgt ablesen: Wenn die $i$-te Zeile einen Pfeil ");
        solWriter.write("nach links enth\\\"alt dann wird der $(i-1)$-te Gegenstand mitgenommen. Die Pfeile zeigen ");
        solWriter.write("dabei an wie der folgende Algorithmus durch die Tabelle l\\\"auft:");
        solWriter.newLine();
        solWriter.newLine();
        solWriter.write("\\begin{verbatim}");
        solWriter.newLine();
        solWriter.write("int i = n; int j = M;");
        solWriter.newLine();
        solWriter.write("while (i > 0 && j > 0) {");
        solWriter.newLine();
        solWriter.write("    if (C[i][j] != C[i-1][j])");
        solWriter.newLine();
        solWriter.write("        for (int k = 0; k < w[i-1]; k++) j--;");
        solWriter.newLine();
        solWriter.write("    i--;");
        solWriter.newLine();
        solWriter.write("}");
        solWriter.newLine();
        solWriter.write("\\end{verbatim}");
        solWriter.newLine();
        solWriter.newLine();
    }

    /**
     * Prints exercise and solution for solving a longest common subsequence (lcs) problem with dynamic programming.
     * @param wordA The first word to find the longest subsequence in, which also is a subsequence of the second word.
     * @param wordB The second word to find the longest subsequence in, which also is a subsequence of the first word.
     * @param mode The preprint mode.
     * @param solWriter The writer to send the solution output to.
     * @param exWriter The writer to send the exercise output to.
     * @throws IOException If some error occurs during output.
     */
    public static void lcs(
        final String wordA,
        final String wordB,
        final PreprintMode mode,
        final BufferedWriter solWriter,
        final BufferedWriter exWriter
    ) throws IOException {
        // some preprocessing
        // actual algorithm
        final int n = wordA.length();
        final int m = wordB.length();
        final Integer[][] C = new Integer[n+1][m+1];
        final String[][] solutions = new String[n + 2][m + 2];
        C[0][0] = 0;
        solutions[0][0] = "";
        solutions[0][1] = "$\\emptyset$";
        solutions[1][0] = "$\\emptyset$";
        solutions[1][1] = "0";
        for (int j = 1; j <= m; j++) {
            C[0][j] = 0;
            solutions[0][j + 1] = "" + wordB.charAt(j-1);
            solutions[1][j + 1] = "0";
        }
        for (int j = 1; j <= n; j++) {
            C[j][0] = 0;
            solutions[j+1][0] = "" + wordA.charAt(j-1);
            solutions[j+1][1] = "0";
        }
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (wordA.charAt(i-1) == wordB.charAt(j-1)) {
                    C[i][j] = C[i-1][j-1] + 1;
                } else {
                    final Integer valueA = C[i][j-1];
                    if (C[i-1][j] < valueA) {
                        C[i][j] = valueA;
                    } else {
                        C[i][j] = C[i-1][j];
                    }
                }
                solutions[i + 1][j + 1] = "" + C[i][j];
            }
        }
        // find the items to choose to get the longest common subsequence
        int row = n;
        int column = m;
        final List<String> itemsToChoose = new ArrayList<String>();
        while (column > 0 && row > 0) {
            if (wordA.charAt(row-1) == wordB.charAt(column-1)) {
                itemsToChoose.add(new String("" + wordA.charAt(row-1)));
                solutions[row + 1][column + 1] = "$\\nwarrow$ " + solutions[row + 1][column + 1];
                column--;
                row--;
            } else {
                if (C[row][column-1] >= C[row-1][column]) {
                    solutions[row + 1][column + 1] = "$\\leftarrow$ " + solutions[row + 1][column + 1];
                    column--;
                } else {
                    solutions[row + 1][column + 1] = "$\\uparrow$ " + solutions[row + 1][column + 1];
                    row--;
                }
            }
        }
        // create output
        exWriter.write(" Bestimmen Sie die \\emphasize{l\\\"angste gemeinsame Teilsequenz} der Sequenzen \\texttt{" + wordA + "} und \\texttt{" + wordB + "}.");
        exWriter.write(" Benutzen Sie hierf\\\"ur den in der Vorlesung vorgestellten Algorithmus mit dynamischer Programmierung");
        exWriter.write(" und f\\\"ullen Sie die folgende Tabelle aus.");
        exWriter.write(" Beschreiben Sie wie man anhand der Tabelle die l\\\"angste gemeinsame Teilsequenz der gegebenen W\\\"orter");
        exWriter.write(" und die L\\\"ange dieser Teilsequenz bestimmen kann.");
        exWriter.newLine();
        exWriter.newLine();
        solWriter.write("Die Tabelle wird vom Algorithmus wie folgt gef\\\"ullt:");
        solWriter.newLine();
        solWriter.newLine();
        final int tableWidth = Main.STUDENT_MODE ? 10 : 12;
        if (m + 2 > tableWidth) {
            String[][] solutionsTmp = new String[n + 2][tableWidth];
            String[][] solutionsTmpEx = new String[n + 2][tableWidth];
            // copy first column (legend) for exercise
            for (int rowNr = 1; rowNr < n + 2; rowNr++) {
                solutionsTmpEx[rowNr][0] = solutions[rowNr][0];
            }
//            boolean remainderStarted = true;
            for (int columnNr = 0; columnNr < m + 2; columnNr++) {
                // System.out.println("columnNr = " + columnNr);
                for (int rowNr = 0; rowNr < n + 2; rowNr++) {
                    // System.out.println("add column " + (columnNr % tableWidth));
                    solutionsTmp[rowNr][columnNr % tableWidth] = solutions[rowNr][columnNr];
                }
                // copy first row (legend) for exercise
                solutionsTmpEx[0][columnNr % tableWidth] = solutions[0][columnNr];
                if (columnNr > 0 && (columnNr % tableWidth == tableWidth - 1 || columnNr == m + 1)) {
                    // we are at the last column of a table (table is filled completely)
                    TikZUtils.printBeginning(TikZUtils.CENTER, solWriter);
                    solWriter.write("{\\Large");
                    solWriter.newLine();
                    TikZUtils.printTable(solutionsTmp, null, "1.2cm", solWriter, true, 0);
                    solWriter.newLine();
                    solWriter.write("}");
                    solWriter.newLine();
                    TikZUtils.printEnd(TikZUtils.CENTER, solWriter);
                    solWriter.newLine();
                    switch (mode) {
                        case SOLUTION_SPACE:
                            TikZUtils.printSolutionSpaceBeginning(exWriter);
                            // fall-through
                        case ALWAYS:
                            TikZUtils.printBeginning(TikZUtils.CENTER, exWriter);
                            exWriter.write("{\\Large");
                            exWriter.newLine();
                            TikZUtils.printTable(solutionsTmpEx, null, "1.2cm", exWriter, true, 0);
                            exWriter.newLine();
                            exWriter.write("}");
                            exWriter.newLine();
                            TikZUtils.printEnd(TikZUtils.CENTER, exWriter);
                            if (mode == PreprintMode.SOLUTION_SPACE) {
                                TikZUtils.printSolutionSpaceEnd(exWriter);
                            }
                            exWriter.newLine();
                            break;
                        case NEVER:
                            // do nothing
                    }
                    // there are m + 2 - (columnNr + 1) columns left to go
                    final int columnNrTmp = Math.min(tableWidth, m + 1 - columnNr);
                    // System.out.println("columnNrTmp = " + columnNrTmp);
                    solutionsTmp = new String[n + 2][columnNrTmp];
                    solutionsTmpEx = new String[n + 2][columnNrTmp];
                }
            }
        } else {
            final String[][] solutionsTmpEx = new String[n + 2][m + 2];
            for (int rowNr = 0; rowNr < n + 2; rowNr++) {
                solutionsTmpEx[rowNr][0] = solutions[rowNr][0];
            }
            for (int columnNr = 1; columnNr < m + 2; columnNr++) {
                solutionsTmpEx[0][columnNr] = solutions[0][columnNr];
            }
            switch (mode) {
                case SOLUTION_SPACE:
                    TikZUtils.printSolutionSpaceBeginning(exWriter);
                    // fall-through
                case ALWAYS:
                    TikZUtils.printBeginning(TikZUtils.CENTER, exWriter);
                    exWriter.write("{\\Large");
                    exWriter.newLine();
                    TikZUtils.printTable(solutionsTmpEx, null, "1.2cm", exWriter, true, 0);
                    exWriter.newLine();
                    exWriter.write("}");
                    exWriter.newLine();
                    TikZUtils.printEnd(TikZUtils.CENTER, exWriter);
                    if (mode == PreprintMode.SOLUTION_SPACE) {
                        TikZUtils.printSolutionSpaceEnd(exWriter);
                    }
                    exWriter.newLine();
                    break;
                case NEVER:
                    // do nothing
            }
            TikZUtils.printBeginning(TikZUtils.CENTER, solWriter);
            solWriter.write("{\\Large");
            solWriter.newLine();
            TikZUtils.printTable(solutions, null, "1.2cm", solWriter, true, 0);
            solWriter.newLine();
            solWriter.write("}");
            solWriter.newLine();
            TikZUtils.printEnd(TikZUtils.CENTER, solWriter);
            solWriter.newLine();
        }
        solWriter.write("\\medskip");
        solWriter.newLine();
        solWriter.write("Also erhalten wir die Sequenz \\texttt{");
        for (int i = itemsToChoose.size() - 1; i >= 0; i--) {
            solWriter.write(itemsToChoose.get(i));
        }
        solWriter.write("} als l\\\"angste gemeinsame Teilsequenz der Sequenzen \\texttt{" + wordA + "} und \\texttt{" + wordB + "}.");
        solWriter.newLine();
        solWriter.newLine();
        solWriter.write("Dies l\\\"asst sich von der Tabelle wie folgt ablesen: Wenn eine Zeile einen Pfeil ");
        solWriter.write("nach links oben enth\\\"alt dann ist der Buchstabe, der den Zeilenkopf ");
        solWriter.write("bildet, teil der l\\\"angsten gemeinsamen Teilsequenz. Die Pfeile zeigen ");
        solWriter.write("dabei an wie der folgende Algorithmus f\\\"ur gegebene W\\\"orter \\texttt{wordA} und \\texttt{wordB} ");
        solWriter.write("durch die erstellte Tabelle \\texttt{C} l\\\"auft:");
        solWriter.newLine();
        solWriter.newLine();
        solWriter.write("\\begin{verbatim}");
        solWriter.newLine();
        solWriter.write("int i = wordA.length(); int j = wordB.length();");
        solWriter.newLine();
        solWriter.write("while (i > 0 && j > 0) {");
        solWriter.newLine();
        solWriter.write("    if (wordA.charAt(i-1) == wordB.charAt(j-1)) { i--; j--; }");
        solWriter.newLine();
        solWriter.write("    else if (C[i][j-1] >= C[i-1][j]) j--;");
        solWriter.newLine();
        solWriter.write("    else i--;");
        solWriter.newLine();
        solWriter.write("}");
        solWriter.newLine();
        solWriter.write("\\end{verbatim}");
        solWriter.newLine();
        solWriter.newLine();
    }

}
