package exercisegenerator.algorithms;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.optimization.*;
import exercisegenerator.util.*;

/**
 * Class offering methods for dynamic programming.
 */
public abstract class OptimizationAlgorithms {

    private static final Random RANDOM = new Random();

    public static String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    public static void knapsack(final AlgorithmInput input) throws Exception {
        final KnapsackProblem problem = OptimizationAlgorithms.parseOrGenerateKnapsackProblem(input.options);
        final int[][] table = OptimizationAlgorithms.knapsack(problem);
        OptimizationAlgorithms.printKnapsackExercise(problem, table, input.options, input.exerciseWriter);
        OptimizationAlgorithms.printKnapsackSolution(problem, table, input.options, input.solutionWriter);
    }

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
        final Parameters options,
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
        Main.newLine(exWriter);
        Main.newLine(exWriter);
        solWriter.write("Die Tabelle \\texttt{C} wird vom Algorithmus wie folgt gef\\\"ullt:");
        Main.newLine(solWriter);
        Main.newLine(solWriter);
        final int tableWidth = 10;
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
                    LaTeXUtils.printBeginning(LaTeXUtils.CENTER, solWriter);
                    solWriter.write("{\\Large");
                    Main.newLine(solWriter);
                    LaTeXUtils.printTable(
                        solutionsTmp,
                        Optional.empty(),
                        LaTeXUtils.defaultColumnDefinition("1.2cm"),
                        true,
                        0,
                        solWriter
                    );
                    Main.newLine(solWriter);
                    solWriter.write("}");
                    Main.newLine(solWriter);
                    LaTeXUtils.printEnd(LaTeXUtils.CENTER, solWriter);
                    Main.newLine(solWriter);
                    switch (mode) {
                        case SOLUTION_SPACE:
                            LaTeXUtils.printSolutionSpaceBeginning(options, exWriter);
                            // fall-through
                        case ALWAYS:
                            LaTeXUtils.printBeginning(LaTeXUtils.CENTER, exWriter);
                            exWriter.write("{\\Large");
                            Main.newLine(exWriter);
                            LaTeXUtils.printTable(
                                solutionsTmpEx,
                                Optional.empty(),
                                LaTeXUtils.defaultColumnDefinition("1.2cm"),
                                true,
                                0,
                                exWriter
                            );
                            Main.newLine(exWriter);
                            exWriter.write("}");
                            Main.newLine(exWriter);
                            LaTeXUtils.printEnd(LaTeXUtils.CENTER, exWriter);
                            if (mode == PreprintMode.SOLUTION_SPACE) {
                                LaTeXUtils.printSolutionSpaceEnd(options, exWriter);
                            }
                            Main.newLine(exWriter);
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
                    LaTeXUtils.printSolutionSpaceBeginning(options, exWriter);
                    // fall-through
                case ALWAYS:
                    LaTeXUtils.printBeginning(LaTeXUtils.CENTER, exWriter);
                    exWriter.write("{\\Large");
                    Main.newLine(exWriter);
                    LaTeXUtils.printTable(
                        solutionsTmpEx,
                        Optional.empty(),
                        LaTeXUtils.defaultColumnDefinition("1.2cm"),
                        true,
                        0,
                        exWriter
                    );
                    Main.newLine(exWriter);
                    exWriter.write("}");
                    Main.newLine(exWriter);
                    LaTeXUtils.printEnd(LaTeXUtils.CENTER, exWriter);
                    if (mode == PreprintMode.SOLUTION_SPACE) {
                        LaTeXUtils.printSolutionSpaceEnd(options, exWriter);
                    }
                    Main.newLine(exWriter);
                    break;
                case NEVER:
                    // do nothing
            }
            LaTeXUtils.printBeginning(LaTeXUtils.CENTER, solWriter);
            solWriter.write("{\\Large");
            Main.newLine(solWriter);
            LaTeXUtils.printTable(
                solutions,
                Optional.empty(),
                LaTeXUtils.defaultColumnDefinition("1.2cm"),
                true,
                0,
                solWriter
            );
            Main.newLine(solWriter);
            solWriter.write("}");
            Main.newLine(solWriter);
            LaTeXUtils.printEnd(LaTeXUtils.CENTER, solWriter);
            Main.newLine(solWriter);
        }
        solWriter.write("\\medskip");
        Main.newLine(solWriter);
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
        Main.newLine(solWriter);
        Main.newLine(solWriter);
        solWriter.write("Dies l\\\"asst sich von der Tabelle wie folgt ablesen: Wenn die $i$-te Zeile einen Pfeil ");
        solWriter.write("nach links enth\\\"alt dann wird der $(i-1)$-te Gegenstand mitgenommen. Die Pfeile zeigen ");
        solWriter.write("dabei an wie der folgende Algorithmus durch die Tabelle l\\\"auft:");
        Main.newLine(solWriter);
        Main.newLine(solWriter);
        solWriter.write("\\begin{verbatim}");
        Main.newLine(solWriter);
        solWriter.write("int i = n; int j = M;");
        Main.newLine(solWriter);
        solWriter.write("while (i > 0 && j > 0) {");
        Main.newLine(solWriter);
        solWriter.write("    if (C[i][j] != C[i-1][j])");
        Main.newLine(solWriter);
        solWriter.write("        for (int k = 0; k < w[i-1]; k++) j--;");
        Main.newLine(solWriter);
        solWriter.write("    i--;");
        Main.newLine(solWriter);
        solWriter.write("}");
        Main.newLine(solWriter);
        solWriter.write("\\end{verbatim}");
        Main.newLine(solWriter);
        Main.newLine(solWriter);
    }

    public static int[][] knapsack(final KnapsackProblem problem) {
        final int[][] result = new int[problem.weights.length + 1][problem.capacity + 1];
        for (int item = 0; item < problem.weights.length; item++) {
            for (int remainingCapacity = 1; remainingCapacity <= problem.capacity; remainingCapacity++) {
                final int weight = problem.weights[item];
                final int valueOut = result[item][remainingCapacity];
                if (weight > remainingCapacity) {
                    result[item + 1][remainingCapacity] = valueOut;
                } else {
                    final int valueIn = result[item][remainingCapacity - weight] + problem.values[item];
                    result[item + 1][remainingCapacity] = Math.max(valueOut, valueIn);
                }
            }
        }
        return result;
    }

    public static void lcs(final AlgorithmInput input) throws IOException {
        final Pair<String,String> tmpInput = OptimizationAlgorithms.parseOrGenerateLCSProblem(input.options);
        OptimizationAlgorithms.lcs(
            tmpInput.x,
            tmpInput.y,
            PreprintMode.parsePreprintMode(input.options),
            input.options,
            input.solutionWriter,
            input.exerciseWriter
        );
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
        final Parameters options,
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
            } else if (C[row][column-1] >= C[row-1][column]) {
                solutions[row + 1][column + 1] = "$\\leftarrow$ " + solutions[row + 1][column + 1];
                column--;
            } else {
                solutions[row + 1][column + 1] = "$\\uparrow$ " + solutions[row + 1][column + 1];
                row--;
            }
        }
        // create output
        exWriter.write(" Bestimmen Sie die \\emphasize{l\\\"angste gemeinsame Teilsequenz} der Sequenzen \\texttt{" + wordA + "} und \\texttt{" + wordB + "}.");
        exWriter.write(" Benutzen Sie hierf\\\"ur den in der Vorlesung vorgestellten Algorithmus mit dynamischer Programmierung");
        exWriter.write(" und f\\\"ullen Sie die folgende Tabelle aus.");
        exWriter.write(" Beschreiben Sie wie man anhand der Tabelle die l\\\"angste gemeinsame Teilsequenz der gegebenen W\\\"orter");
        exWriter.write(" und die L\\\"ange dieser Teilsequenz bestimmen kann.");
        Main.newLine(exWriter);
        Main.newLine(exWriter);
        solWriter.write("Die Tabelle wird vom Algorithmus wie folgt gef\\\"ullt:");
        Main.newLine(solWriter);
        Main.newLine(solWriter);
        final int tableWidth = 10;
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
                    LaTeXUtils.printBeginning(LaTeXUtils.CENTER, solWriter);
                    solWriter.write("{\\Large");
                    Main.newLine(solWriter);
                    LaTeXUtils.printTable(
                        solutionsTmp,
                        Optional.empty(),
                        LaTeXUtils.defaultColumnDefinition("1.2cm"),
                        true,
                        0,
                        solWriter
                    );
                    Main.newLine(solWriter);
                    solWriter.write("}");
                    Main.newLine(solWriter);
                    LaTeXUtils.printEnd(LaTeXUtils.CENTER, solWriter);
                    Main.newLine(solWriter);
                    switch (mode) {
                        case SOLUTION_SPACE:
                            LaTeXUtils.printSolutionSpaceBeginning(options, exWriter);
                            // fall-through
                        case ALWAYS:
                            LaTeXUtils.printBeginning(LaTeXUtils.CENTER, exWriter);
                            exWriter.write("{\\Large");
                            Main.newLine(exWriter);
                            LaTeXUtils.printTable(
                                solutionsTmpEx,
                                Optional.empty(),
                                LaTeXUtils.defaultColumnDefinition("1.2cm"),
                                true,
                                0,
                                exWriter
                            );
                            Main.newLine(exWriter);
                            exWriter.write("}");
                            Main.newLine(exWriter);
                            LaTeXUtils.printEnd(LaTeXUtils.CENTER, exWriter);
                            if (mode == PreprintMode.SOLUTION_SPACE) {
                                LaTeXUtils.printSolutionSpaceEnd(options, exWriter);
                            }
                            Main.newLine(exWriter);
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
                    LaTeXUtils.printSolutionSpaceBeginning(options, exWriter);
                    // fall-through
                case ALWAYS:
                    LaTeXUtils.printBeginning(LaTeXUtils.CENTER, exWriter);
                    exWriter.write("{\\Large");
                    Main.newLine(exWriter);
                    LaTeXUtils.printTable(
                        solutionsTmpEx,
                        Optional.empty(),
                        LaTeXUtils.defaultColumnDefinition("1.2cm"),
                        true,
                        0,
                        exWriter
                    );
                    Main.newLine(exWriter);
                    exWriter.write("}");
                    Main.newLine(exWriter);
                    LaTeXUtils.printEnd(LaTeXUtils.CENTER, exWriter);
                    if (mode == PreprintMode.SOLUTION_SPACE) {
                        LaTeXUtils.printSolutionSpaceEnd(options, exWriter);
                    }
                    Main.newLine(exWriter);
                    break;
                case NEVER:
                    // do nothing
            }
            LaTeXUtils.printBeginning(LaTeXUtils.CENTER, solWriter);
            solWriter.write("{\\Large");
            Main.newLine(solWriter);
            LaTeXUtils.printTable(
                solutions,
                Optional.empty(),
                LaTeXUtils.defaultColumnDefinition("1.2cm"),
                true,
                0,
                solWriter
            );
            Main.newLine(solWriter);
            solWriter.write("}");
            Main.newLine(solWriter);
            LaTeXUtils.printEnd(LaTeXUtils.CENTER, solWriter);
            Main.newLine(solWriter);
        }
        solWriter.write("\\medskip");
        Main.newLine(solWriter);
        solWriter.write("Also erhalten wir die Sequenz \\texttt{");
        for (int i = itemsToChoose.size() - 1; i >= 0; i--) {
            solWriter.write(itemsToChoose.get(i));
        }
        solWriter.write("} als l\\\"angste gemeinsame Teilsequenz der Sequenzen \\texttt{" + wordA + "} und \\texttt{" + wordB + "}.");
        Main.newLine(solWriter);
        Main.newLine(solWriter);
        solWriter.write("Dies l\\\"asst sich von der Tabelle wie folgt ablesen: Wenn eine Zeile einen Pfeil ");
        solWriter.write("nach links oben enth\\\"alt dann ist der Buchstabe, der den Zeilenkopf ");
        solWriter.write("bildet, teil der l\\\"angsten gemeinsamen Teilsequenz. Die Pfeile zeigen ");
        solWriter.write("dabei an wie der folgende Algorithmus f\\\"ur gegebene W\\\"orter \\texttt{wordA} und \\texttt{wordB} ");
        solWriter.write("durch die erstellte Tabelle \\texttt{C} l\\\"auft:");
        Main.newLine(solWriter);
        Main.newLine(solWriter);
        solWriter.write("\\begin{verbatim}");
        Main.newLine(solWriter);
        solWriter.write("int i = wordA.length(); int j = wordB.length();");
        Main.newLine(solWriter);
        solWriter.write("while (i > 0 && j > 0) {");
        Main.newLine(solWriter);
        solWriter.write("    if (wordA.charAt(i-1) == wordB.charAt(j-1)) { i--; j--; }");
        Main.newLine(solWriter);
        solWriter.write("    else if (C[i][j-1] >= C[i-1][j]) j--;");
        Main.newLine(solWriter);
        solWriter.write("    else i--;");
        Main.newLine(solWriter);
        solWriter.write("}");
        Main.newLine(solWriter);
        solWriter.write("\\end{verbatim}");
        Main.newLine(solWriter);
        Main.newLine(solWriter);
    }

    public static SimplexSolution simplex(final SimplexProblem problem) {
        final List<SimplexTableau> tableaus = new LinkedList<SimplexTableau>();
        SimplexTableau tableau = OptimizationAlgorithms.simplexInitializeTableau(problem);
        tableaus.add(tableau);
        SimplexAnswer answer = OptimizationAlgorithms.simplexComputeAnswer(tableau);
        while (answer == SimplexAnswer.INCOMPLETE) {
            tableau = OptimizationAlgorithms.simplexStep(tableau);
            tableaus.add(tableau);
            answer = OptimizationAlgorithms.simplexComputeAnswer(tableau);
        }
        return new SimplexSolution(tableaus, answer);
    }

    private static void fillKnapsackSolutionTable(
        final String[][] tableWithArrows,
        final int[][] solution,
        final int[] weights
    ) {
        for (int i = 0; i < solution.length; i++) {
            for (int j = 0; j < solution[i].length; j++) {
                tableWithArrows[i + 1][2 * j + 1] = String.valueOf(solution[i][j]);
            }
        }
        int i = solution.length - 1;
        int j = solution[0].length - 1;
        while (i > 0) {
            final int valueAbove = i == 0 ? 0 : solution[i - 1][j];
            if (solution[i][j] > valueAbove) {
                for (int k = 0; k < weights[i - 1]; k++) {
                    tableWithArrows[i + 1][2 * j + 2] = "$\\leftarrow$";
                    j--;
                }
            }
            tableWithArrows[i + 1][2 * j + 2] = "$\\uparrow$";
            i--;
        }
        while (j > 0) {
            tableWithArrows[0][2 * j + 2] = "$\\leftarrow$";
            j--;
        }
    }

    private static KnapsackProblem generateKnapsackProblem(final Parameters options) {
        final int numberOfItems = OptimizationAlgorithms.parseOrGenerateNumberOfItems(options);
        int sumOfWeights = 0;
        final int[] weights = new int[numberOfItems];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = 1 + OptimizationAlgorithms.RANDOM.nextInt(11);
            sumOfWeights += weights[i];
        }
        final int[] values = new int[numberOfItems];
        for (int i = 0; i < values.length; i++) {
            values[i] = 1 + OptimizationAlgorithms.RANDOM.nextInt(11);
        }
        final int p = 35 + OptimizationAlgorithms.RANDOM.nextInt(30);
        final int capacity = (sumOfWeights * p) / 100;
        return new KnapsackProblem(weights, values, capacity);
    }

    private static Pair<String, String> generateLCSProblem(final Parameters options) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    private static List<Integer> knapsackItems(final KnapsackProblem problem, final int[][] solution) {
        final List<Integer> result = new LinkedList<Integer>();
        int i = solution.length - 1;
        int j = solution[0].length - 1;
        while (i > 0) {
            final int valueAbove = i == 0 ? 0 : solution[i - 1][j];
            if (solution[i][j] > valueAbove) {
                result.add(i);
                j -= problem.weights[i - 1];
            }
            i--;
        }
        Collections.sort(result);
        return result;
    }

    private static String knapsackTableColumnDefinition(final int cols) {
        return String.format("|C{1.2cm}|*{%d}{C{1.2cm}C{5mm}|}", cols / 2);
    }

    private static KnapsackProblem parseKnapsackProblem(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        final String errorMessage =
            "You need to provide the same number of weights and values together with a capacity. The three parts need "
            + " to be separated by ';' while the numbers within the parts are separated by ','!";
        final String line = reader.readLine();
        final String[] parts = line.strip().split(";");
        if (parts.length != 3) {
            throw new IOException(errorMessage);
        }
        return new KnapsackProblem(
            OptimizationAlgorithms.toIntArray(parts[0]),
            OptimizationAlgorithms.toIntArray(parts[1]),
            Integer.parseInt(parts[2])
        );
    }

    private static Pair<String, String> parseLCSProblem(final BufferedReader reader, final Parameters options) throws IOException {
        String wordA = null;
        String wordB = null;
        final String errorMessage = "You need to provide two lines each carrying exactly one non-empty word.";
        String line = null;
        int rowNum = 0;
        while ((line = reader.readLine()) != null) {
            if (rowNum == 0) {
                wordA = new String(line);
                if (wordA.length() == 0) {
                    System.out.println(errorMessage);
                    return null;
                }
            } else if (rowNum == 1) {
                wordB = new String(line);
                if (wordB.length() == 0) {
                    System.out.println(errorMessage);
                    return null;
                }
            } else {
                System.out.println(errorMessage);
                return null;
            }
            rowNum++;
        }
        return new Pair<String,String>(wordA, wordB);
    }

    private static KnapsackProblem parseOrGenerateKnapsackProblem(final Parameters options)
    throws IOException {
        return new ParserAndGenerator<KnapsackProblem>(
            OptimizationAlgorithms::parseKnapsackProblem,
            OptimizationAlgorithms::generateKnapsackProblem
        ).getResult(options);
    }

    private static Pair<String, String> parseOrGenerateLCSProblem(final Parameters options) throws IOException {
        return new ParserAndGenerator<Pair<String, String>>(
            OptimizationAlgorithms::parseLCSProblem,
            OptimizationAlgorithms::generateLCSProblem
        ).getResult(options);
    }

    private static int parseOrGenerateNumberOfItems(final Parameters options) {
        if (options.containsKey(Flag.LENGTH)) {
            final int result = Integer.parseInt(options.get(Flag.LENGTH));
            if (result > 0) {
                return result;
            }
        }
        return OptimizationAlgorithms.RANDOM.nextInt(4) + 3;
    }

    private static void printKnapsackExercise(
        final KnapsackProblem problem,
        final int[][] solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws Exception {
        final int numberOfItems = problem.weights.length;
        writer.write(
            String.format(
                "Gegeben sei ein Rucksack mit \\emphasize{maximaler Tragkraft} %d sowie %d \\emphasize{Gegenst\\\"ande}. ",
                problem.capacity,
                numberOfItems
            )
        );
        Main.newLine(writer);
        writer.write("Der $i$-te Gegenstand soll hierbei ein Gewicht von $w_i$ und einen Wert von $c_i$ haben. ");
        Main.newLine(writer);
        writer.write("Bestimmen Sie mit Hilfe des Algorithmus zum L\\\"osen ");
        writer.write("des Rucksackproblems mittels dynamischer Programmierung den maximalen ");
        writer.write("Gesamtwert der Gegenst\\\"ande, die der Rucksack tragen kann (das Gesamtgewicht der ");
        writer.write("mitgef\\\"uhrten Gegenst\\\"ande \\\"ubersteigt nicht die Tragkraft des Rucksacks). ");
        Main.newLine(writer);
        writer.write("Die \\emphasize{Gewichte} seien dabei $w_{1} = " + problem.weights[0] + "$");
        for (int i = 1; i < numberOfItems - 1; i++) {
            writer.write(", $w_{" + (i + 1) + "} = " + problem.weights[i] + "$");
        }
        writer.write(" und $w_{" + numberOfItems + "} = " + problem.weights[numberOfItems - 1]);
        writer.write("$. ");
        Main.newLine(writer);
        writer.write("Die \\emphasize{Werte} seien $c_{1} = " + problem.values[0] + "$");
        for (int i = 1; i < numberOfItems - 1; i++) {
            writer.write(", $c_{" + (i + 1) + "} = " + problem.values[i] + "$");
        }
        writer.write(" und $c_{" + numberOfItems + "} = " + problem.values[numberOfItems - 1] + "$. ");
        Main.newLine(writer);
        writer.write("Geben Sie zudem die vom Algorithmus bestimmte Tabelle \\texttt{C} an ");
        writer.write("und beschreiben Sie anhand der Tabelle, wie man die mitzunehmenden Gegenst\\\"ande ");
        writer.write("bestimmen kann, um den maximalen Wert zu erreichen.");
        Main.newLine(writer);
        Main.newLine(writer);
        final PreprintMode mode = PreprintMode.parsePreprintMode(options);
        switch (mode) {
        case SOLUTION_SPACE:
            LaTeXUtils.printSolutionSpaceBeginning(options, writer);
            // fall-through
        case ALWAYS:
            LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
            writer.write("{\\Large");
            Main.newLine(writer);
            LaTeXUtils.printTable(
                OptimizationAlgorithms.toKnapsackSolutionTable(solution, Optional.empty()),
                Optional.empty(),
                OptimizationAlgorithms::knapsackTableColumnDefinition,
                true,
                0,
                writer
            );
            Main.newLine(writer);
            writer.write("}");
            Main.newLine(writer);
            LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
            if (mode == PreprintMode.SOLUTION_SPACE) {
                LaTeXUtils.printSolutionSpaceEnd(options, writer);
            }
            Main.newLine(writer);
            break;
        default:
            //do nothing
        }
    }

    private static void printKnapsackSolution(
        final KnapsackProblem problem,
        final int[][] solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Die Tabelle \\texttt{C} wird vom Algorithmus wie folgt gef\\\"ullt:");
        Main.newLine(writer);
        Main.newLine(writer);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        writer.write("{\\Large");
        Main.newLine(writer);
        LaTeXUtils.printTable(
            OptimizationAlgorithms.toKnapsackSolutionTable(solution, Optional.of(problem.weights)),
            Optional.empty(),
            OptimizationAlgorithms::knapsackTableColumnDefinition,
            true,
            0,
            writer
        );
        Main.newLine(writer);
        writer.write("}");
        Main.newLine(writer);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
        Main.newLine(writer);
        writer.write("Damit ergibt sich der maximale Wert ");
        writer.write(String.valueOf(solution[solution.length - 1][solution[0].length - 1]));
        writer.write(" f\\\"ur den Fall, dass die folgenden Gegenst\\\"ande mitgenommen werden:");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write(
            LaTeXUtils.displayMath(LaTeXUtils.mathematicalSet(OptimizationAlgorithms.knapsackItems(problem, solution)))
        );
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("Dies l\\\"asst sich von der Tabelle wie folgt ablesen: Wenn die Zeile f\\\"ur den $i$-ten ");
        writer.write("Gegenstand einen Pfeil nach links enth\\\"alt, dann wird der $i$-te Gegenstand mitgenommen ");
        writer.write("(ein Pfeil nach links in der Zeile f\\\"ur den 0-ten Gegenstand hat keine Bedeutung).");
        Main.newLine(writer);
        Main.newLine(writer);
    }

    private static void simplexBaseSwap(
        final int pivotRow,
        final int pivotColumn,
        final double[][] matrix,
        final int[] basicVariables,
        final double[] target
    ) {
        final double pivotElement = matrix[pivotRow][pivotColumn];
        if (pivotElement != 1.0) {
            for (int col = 0; col < matrix[pivotRow].length; col++) {
                matrix[pivotRow][col] /= pivotElement;
            }
        }
        for (int row = 0; row < matrix.length - 2; row++) {
            if (row != pivotRow && Double.compare(matrix[row][pivotColumn], 0.0) != 0) {
                final double factor = matrix[row][pivotColumn];
                for (int col = 0; col < matrix[row].length; col++) {
                    matrix[row][col] -= factor * matrix[pivotRow][col];
                }
            }
        }
        basicVariables[pivotRow] = pivotColumn;
        for (int col = 0; col < matrix[basicVariables.length].length; col++) {
            double sum = 0;
            for (int row = 0; row < basicVariables.length; row++) {
                sum += matrix[row][col] * OptimizationAlgorithms.simplexTargetValue(target, basicVariables[row]);
            }
            matrix[basicVariables.length][col] = sum;
            if (col < target.length) {
                matrix[basicVariables.length + 1][col] = target[col] - sum;
            } else if (col < matrix[basicVariables.length].length - 1) {
                matrix[basicVariables.length + 1][col] = -sum;
            } else {
                matrix[basicVariables.length + 1][col] = 0;
            }
        }
    }

    private static SimplexAnswer simplexComputeAnswer(final SimplexTableau tableau) {
        final double[][] matrix = tableau.problem.matrix;
        if (OptimizationAlgorithms.simplexHasNegativeLimit(matrix)) {
            if (OptimizationAlgorithms.simplexHasNegativeLimitWithNonNegativeRow(matrix)) {
                return SimplexAnswer.UNSOLVABLE;
            } else {
                return SimplexAnswer.INCOMPLETE;
            }
        }
        if (OptimizationAlgorithms.simplexPivotColumnExists(matrix)) {
            if (OptimizationAlgorithms.simplexPivotRowExists(tableau.quotients)) {
                return SimplexAnswer.INCOMPLETE;
            } else {
                return SimplexAnswer.UNBOUNDED;
            }
        }
        return SimplexAnswer.SOLVED;
    }

    private static Double[] simplexComputeQuotients(final double[][] matrix, final int pivotColumn) {
        if (pivotColumn < 0) {
            return null;
        }
        final Double[] quotients = new Double[matrix.length - 2];
        for (int row = 0; row < quotients.length; row++) {
            if (matrix[row][pivotColumn] > 0 && Double.compare(matrix[row][matrix[row].length - 1], 0) >= 0) {
                quotients[row] = matrix[row][matrix[row].length - 1] / matrix[row][pivotColumn];
            } else {
                quotients[row] = null;
            }
        }
        return quotients;
    }

    private static int simplexFirstRowWithNegativeLimit(final double[][] matrix) {
        for (int row = 0; row < matrix.length - 2; row++) {
            if (matrix[row][matrix[row].length - 1] < 0) {
                return row;
            }
        }
        return -1;
    }

    private static boolean simplexHasNegativeLimit(final double[][] matrix) {
        for (int i = 0; i < matrix.length - 2; i++) {
            if (matrix[i][matrix[i].length - 1] < 0) {
                return true;
            }
        }
        return false;
    }

    private static boolean simplexHasNegativeLimitWithNonNegativeRow(final double[][] matrix) {
        for (int i = 0; i < matrix.length - 2; i++) {
            if (matrix[i][matrix[i].length - 1] < 0) {
                boolean notFound = true;
                for (int j = 0; j < matrix.length - 1; j++) {
                    if (matrix[i][j] < 0) {
                        notFound = false;
                        break;
                    }
                }
                if (notFound) {
                    return true;
                }
            }
        }
        return false;
    }

    private static double[][] simplexInitializeMatrix(final double[][] initialMatrix, final double[] target) {
        final int initialColumnLength = initialMatrix.length;
        final double[][] newMatrix = new double[initialColumnLength + 2][initialMatrix[0].length + initialColumnLength];
        for (int row = 0; row < initialColumnLength; row++) {
            final int initialRowLength = initialMatrix[row].length - 1;
            for (int col = 0; col < initialRowLength; col++) {
                newMatrix[row][col] = initialMatrix[row][col];
            }
            final int newRowLength = newMatrix[row].length - 1;
            for (int col = initialRowLength; col < newRowLength; col++) {
                if (col - initialRowLength == row) {
                    newMatrix[row][col] = 1;
                } else {
                    newMatrix[row][col] = 0;
                }
            }
            newMatrix[row][newRowLength] = initialMatrix[row][initialRowLength];
        }
        for (int col = 0; col < newMatrix[initialColumnLength].length; col++) {
            newMatrix[initialColumnLength][col] = 0;
        }
        final int targetLength = target.length;
        for (int col = 0; col < targetLength; col++) {
            newMatrix[initialColumnLength + 1][col] = target[col];
        }
        for (int col = targetLength; col < newMatrix[initialColumnLength + 1].length; col++) {
            newMatrix[initialColumnLength + 1][col] = 0;
        }
        return newMatrix;
    }

    private static SimplexTableau simplexInitializeTableau(final SimplexProblem problem) {
        final double[][] initialMatrix = problem.matrix;
        final int initialColumnLength = initialMatrix.length;
        final double[][] newMatrix = OptimizationAlgorithms.simplexInitializeMatrix(initialMatrix, problem.target);
        final int[] basicVariables = new int[initialColumnLength];
        for (int i = 0; i < initialColumnLength; i++) {
            basicVariables[i] = i + problem.target.length;
        }
        final int pivotColumn = OptimizationAlgorithms.simplexSelectPivotColumn(newMatrix);
        final Double[] quotients = OptimizationAlgorithms.simplexComputeQuotients(newMatrix, pivotColumn);
        return new SimplexTableau(
            new SimplexProblem(problem.target, newMatrix),
            basicVariables,
            quotients,
            OptimizationAlgorithms.simplexSelectPivotRow(quotients),
            pivotColumn
        );
    }

    private static boolean simplexPivotColumnExists(final double[][] matrix) {
        for (int col = 0; col < matrix[matrix.length - 1].length - 1; col++) {
            if (matrix[matrix.length - 1][col] > 0) {
                return true;
            }
        }
        return false;
    }

    private static boolean simplexPivotRowExists(final Double[] quotients) {
        if (quotients == null) {
            return false;
        }
        for (final Double quotient : quotients) {
            if (quotient != null) {
                return true;
            }
        }
        return false;
    }

    private static int simplexSelectPivotColumn(final double[][] matrix) {
        return OptimizationAlgorithms.simplexHasNegativeLimit(matrix) ?
            OptimizationAlgorithms.simplexSelectPivotColumnPhaseOne(matrix) :
                OptimizationAlgorithms.simplexSelectPivotColumnPhaseTwo(matrix);
    }

    private static int simplexSelectPivotColumnPhaseOne(final double[][] matrix) {
        for (int row = 0; row < matrix.length - 2; row++) {
            if (matrix[row][matrix[row].length - 1] < 0) {
                double min = 0;
                int pivotColumn = -1;
                for (int col = 0; col < matrix[row].length - 1; col++) {
                    if (matrix[row][col] < min) {
                        min = matrix[row][col];
                        pivotColumn = col;
                    }
                }
                return pivotColumn;
            }
        }
        return -1;
    }

    private static int simplexSelectPivotColumnPhaseTwo(final double[][] matrix) {
        double max = 0;
        int pivotColumn = -1;
        for (int col = 0; col < matrix[matrix.length - 1].length - 1; col++) {
            if (matrix[matrix.length - 1][col] > max) {
                max = matrix[matrix.length - 1][col];
                pivotColumn = col;
            }
        }
        return pivotColumn;
    }

    private static int simplexSelectPivotRow(final Double[] quotients) {
        if (quotients == null) {
            return -1;
        }
        Double min = null;
        int pivotRow = -1;
        for (int row = 0; row < quotients.length; row++) {
            if (quotients[row] != null && (min == null || quotients[row] < min)) {
                min = quotients[row];
                pivotRow = row;
            }
        }
        return pivotRow;
    }

    private static SimplexTableau simplexStep(final SimplexTableau tableau) {
        final double[] target = tableau.problem.target;
        final double[][] matrix = ArrayUtils.copy(tableau.problem.matrix);
        final int[] basicVariables = ArrayUtils.copy(tableau.basicVariables);
        final int pivotRow =
            tableau.pivotRow < 0 ? OptimizationAlgorithms.simplexFirstRowWithNegativeLimit(matrix) : tableau.pivotRow;
        OptimizationAlgorithms.simplexBaseSwap(pivotRow, tableau.pivotColumn, matrix, basicVariables, target);
        final int pivotColumn = OptimizationAlgorithms.simplexSelectPivotColumn(matrix);
        final Double[] quotients = OptimizationAlgorithms.simplexComputeQuotients(matrix, pivotColumn);
        return new SimplexTableau(
            new SimplexProblem(target, matrix),
            basicVariables,
            quotients,
            OptimizationAlgorithms.simplexSelectPivotRow(quotients),
            pivotColumn
        );
    }

    private static double simplexTargetValue(final double[] target, final int index) {
        return index < target.length ? target[index] : 0;
    }

    private static int[] toIntArray(final String line) {
        return Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).toArray();
    }

    private static String[][] toKnapsackSolutionTable(
        final int[][] solution,
        final Optional<int[]> optionalWeightsForFilling
    ) {
        final String[][] tableWithArrows = new String[solution.length + 1][2 * solution[0].length + 1];
        tableWithArrows[0][0] = LaTeXUtils.bold("Gegenstand/Kapazit\\\"at");
        for (int i = 0; i < solution.length; i++) {
            tableWithArrows[i + 1][0] = LaTeXUtils.bold(String.valueOf(i));
        }
        for (int i = 0; i < solution[0].length; i++) {
            tableWithArrows[0][2 * i + 1] = LaTeXUtils.bold(String.valueOf(i));
        }
        if (optionalWeightsForFilling.isPresent()) {
            OptimizationAlgorithms.fillKnapsackSolutionTable(tableWithArrows, solution, optionalWeightsForFilling.get());
        }
        return tableWithArrows;
    }

}
