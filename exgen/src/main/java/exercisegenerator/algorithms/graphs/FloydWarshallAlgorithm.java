package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

public class FloydWarshallAlgorithm implements AlgorithmImplementation {

    public static final FloydWarshallAlgorithm INSTANCE = new FloydWarshallAlgorithm();

    /**
     * Prints exercise and solution for the Floyd Algorithm.
     * @param graph The graph.
     * @param warshall Flag indicating whether the Floyd-Warshall or just the Floyd algorithm should be performed.
     * @param start The start vertex.
     * @param comp A comparator for sorting the vertices in the table (may be null - then no sorting is applied).
     * @param exWriter The writer to send the exercise output to.
     * @param solWriter The writer to send the solution output to.
     * @throws IOException If some error occurs during output.
     */
    public static <V> void floydWarshall(
        final Graph<V, Integer> graph,
        final boolean warshall,
        final Comparator<Vertex<V>> comp,
        final Parameters options,
        final BufferedWriter exWriter,
        final BufferedWriter solWriter
    ) throws IOException {
        final int tableCount = 1; // TODO had a choice for 2 when not in student mode
        final int tableMaxWidth = 10; // TODO rename, current name does not reflect usage; had a choice for 0 when not in student mode
        final List<Vertex<V>> vertices = new ArrayList<Vertex<V>>(graph.getVertices());
        final int size = vertices.size();
        final ArrayList<String[][]> exercises = new ArrayList<String[][]>();
        final ArrayList<String[][]> solutions = new ArrayList<String[][]>();
        final ArrayList<String[][]> exColors = new ArrayList<String[][]>();
        final ArrayList<String[][]> solColors = new ArrayList<String[][]>();
        final String[][] firstExercise = new String[size+1][size+1];
        final String[][] otherExercise = new String[size+1][size+1];
        String[][] currentSolution = new String[size+1][size+1];
        final String[][] curExColor = new String[size+1][size+1];
        String[][] curSolColor = new String[size+1][size+1];
        final Integer[][] weights = new Integer[size][size];
        boolean[][] changed = new boolean[size][size];
        // initialize ids
        final Map<Vertex<V>, Integer> ids = new LinkedHashMap<Vertex<V>, Integer>();
        for (int current = 0 ; current < size; ++current) {
            final Vertex<V> currentVertex = vertices.get(current);
            ids.put(currentVertex, current);
        }
        firstExercise[0][0] = "";
        otherExercise[0][0] = "";
        currentSolution[0][0] = "";
        // initialize weights
        for (int current = 0 ; current < size; ++current) {
            final Vertex<V> currentVertex = vertices.get(current);
            // set labels
            final String currentLabel = currentVertex.label.isEmpty() ? "" : currentVertex.label.get().toString();
            firstExercise[0][current+1] = currentLabel;
            firstExercise[current+1][0] = currentLabel;
            otherExercise[0][current+1] = currentLabel;
            otherExercise[current+1][0] = currentLabel;
            currentSolution[0][current+1] = currentLabel;
            currentSolution[current+1][0] = currentLabel;
            // prepare weights and solution array
            for (int i = 0; i < size; ++i) {
                if (!warshall) {
                    firstExercise[current+1][i+1] = "$\\infty$";
                    currentSolution[current+1][i+1] = "$\\infty$";
                } else {
                    firstExercise[current+1][i+1] = "false";
                    currentSolution[current+1][i+1] = "false";
                }
                weights[current][i] = null;
            }
            for (final Edge<Integer, V> edge : graph.getAdjacencyList(currentVertex)) {
                final Integer edgeLabel = edge.label.get();
                weights[current][ids.get(edge.to)] = edgeLabel;
                if (!warshall) {
                    firstExercise[current + 1][ids.get(edge.to) + 1] = edgeLabel.toString();
                    currentSolution[current + 1][ids.get(edge.to) + 1] = edgeLabel.toString();
                } else {
                    firstExercise[current + 1][ids.get(edge.to) + 1] = "true";
                    currentSolution[current + 1][ids.get(edge.to) + 1] = "true";
//                    System.out.println(
//                        "Add: "
//                        + currentVertex.getLabel()
//                        + " -"
//                        + currentSolution[current][ids.get(edge.y)]
//                        + "-> "
//                        + edge.y.getLabel()
//                    );
                }
            }
            weights[current][current] = 0;
            if (!warshall) {
                firstExercise[current+1][current+1] = "0";
                currentSolution[current+1][current+1] = "0";
            } else {
                firstExercise[current+1][current+1] = "true";
                currentSolution[current+1][current+1] = "true";
            }
        }
        exercises.add(firstExercise);
        solutions.add(currentSolution);
        exColors.add(curExColor);
        solColors.add(curSolColor);
        // clear solution and reset
        currentSolution = new String[size+1][size+1];
        curSolColor = new String[size+1][size+1];
        currentSolution[0][0] = "";
        for (int current = 0 ; current < size; ++current) {
            final Vertex<V> currentVertex = vertices.get(current);
            // set labels
            final String currentLabel = currentVertex.label.isEmpty() ? "" : currentVertex.label.get().toString();
            currentSolution[0][current+1] = currentLabel;
            currentSolution[current+1][0] = currentLabel;
        }
        // actual algorithm
        for (int intermediate = 0; intermediate < size; ++intermediate) {
            for (int start = 0; start < size; ++start) {
                for (int target = 0; target < size; ++target) {
                    final Integer oldValue = weights[start][target];
                    if (weights[start][target] != null) {
                        if (weights[start][intermediate] != null && weights[intermediate][target] != null) {
                            weights[start][target] =
                                Integer.compare(
                                    weights[start][target],
                                    weights[start][intermediate] + weights[intermediate][target]
                                ) < 0 ?
                                    weights[start][target] :
                                        weights[start][intermediate] + weights[intermediate][target];
                        }
                        // no else here as we can keep the old value as the path is currently infinite (null)
                    } else if (weights[start][intermediate] != null && weights[intermediate][target] != null) {
                        weights[start][target] = weights[start][intermediate] + weights[intermediate][target];
                    }
                    if(!warshall) {
                        changed[start][target] = (oldValue != weights[start][target]);
                    } else {
                        changed[start][target] = (oldValue == null && weights[start][target] != null);
                    }
                }
            }
            // write solution
            for (int i = 0; i < size; ++i) {
                for (int j = 0; j < size; ++j) {
                    if (changed[i][j]) {
                        curSolColor[i+1][j+1] = "black!20";
                    }
                    if (weights[i][j] == null) {
                        if (!warshall) {
                            currentSolution[i+1][j+1] = "$\\infty$";
                        } else {
                            currentSolution[i+1][j+1] = "false";
                        }
                    } else if (!warshall) {
                        currentSolution[i+1][j+1] = "" + weights[i][j];
                    } else {
                        currentSolution[i+1][j+1] = "true";
                    }
                }
            }
            exercises.add(otherExercise);
            solutions.add(currentSolution);
            exColors.add(curExColor);
            solColors.add(curSolColor);
            // clear solution and reset.
            currentSolution = new String[size+1][size+1];
            curSolColor = new String[size+1][size+1];
            currentSolution[0][0] = "";
            for (int current = 0 ; current < size; ++current) {
                final Vertex<V> currentVertex = vertices.get(current);
                // set labels
                final String currentLabel = currentVertex.label.isEmpty() ? "" : currentVertex.label.get().toString();
                currentSolution[0][current+1] = currentLabel;
                currentSolution[current+1][0] = currentLabel;
            }
            changed = new boolean[size][size];
        }
        // create output
        exWriter.write("Betrachten Sie den folgenden Graphen:");
        Main.newLine(exWriter);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, exWriter);
        final double multiplier = FloydWarshallAlgorithm.parseMultiplier(options);
        if (warshall) {
            graph.printTikZ(GraphPrintMode.NO_EDGE_LABELS, multiplier, null, exWriter);
        } else {
            graph.printTikZ(GraphPrintMode.ALL, multiplier, null, exWriter);
        }
        Main.newLine(exWriter);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, exWriter);
        Main.newLine(exWriter);
        if (!warshall) {
            exWriter.write("F\\\"uhren Sie den \\emphasize{Algorithmus von Floyd} auf diesem Graphen aus. ");
        } else {
            exWriter.write("F\\\"uhren Sie den \\emphasize{Algorithmus von Warshall} auf diesem Graphen aus. ");
        }
        exWriter.write("Geben Sie dazu nach jedem Durchlauf der \\\"au{\\ss}eren Schleife die aktuellen Entfernungen ");
        exWriter.write("in einer Tabelle an. Die erste Tabelle enth\\\"alt bereits die Adjazenzmatrix nach Bildung ");
        exWriter.write("der reflexiven H\\\"ulle.");
        if (warshall) {
            exWriter.write(" Der Eintrag in der Zeile $i$ und Spalte $j$ gibt also an, ob es eine Kante");
            exWriter.write(" vom Knoten der Zeile $i$ zu dem Knoten der Spalte $j$ gibt.\\\\[2ex]");
        } else {
            exWriter.write(" Der Eintrag in der Zeile $i$ und Spalte $j$ ist also $\\infty$, falls es keine Kante");
            exWriter.write(" vom Knoten der Zeile $i$ zu dem Knoten der Spalte $j$ gibt, und sonst");
            exWriter.write(" das Gewicht dieser Kante. Beachten Sie, dass in der reflexiven H\\\"ulle jeder Knoten");
            exWriter.write(" eine Kante mit Gewicht $0$ zu sich selbst hat.\\\\[2ex]");
        }
        Main.newLine(exWriter);
        Main.newLine(exWriter);
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, exWriter);
        LaTeXUtils.printArrayStretch(1.5, exWriter);
        LaTeXUtils.printArrayStretch(1.5, solWriter);
        int solCount = 0;
        int exCount = 0;
        for (int iteration = 0; iteration < solutions.size(); ++iteration) {
            final String[][] exTable = exercises.get(iteration);
            final String[][] solTable = solutions.get(iteration);
            exTable[0][0] = "\\circled{" + (iteration + 1) + "}";
            solTable[0][0] = "\\circled{" + (iteration + 1) + "}";
            solCount =
                FloydWarshallAlgorithm.printTables(
                    solCount,
                    tableCount,
                    iteration,
                    solTable,
                    solColors.get(iteration),
                    solWriter,
                    true,
                    tableMaxWidth
                );
            exCount =
                FloydWarshallAlgorithm.printTables(
                    exCount,
                    tableCount,
                    iteration,
                    exTable,
                    exColors.get(iteration),
                    exWriter,
                    true,
                    tableMaxWidth
                );
            LaTeXUtils.printVerticalProtectedSpace(exWriter);
            LaTeXUtils.printVerticalProtectedSpace(solWriter);
        }
        LaTeXUtils.printArrayStretch(1.0, exWriter);
        LaTeXUtils.printArrayStretch(1.0, solWriter);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, exWriter);
    }

    private static double parseMultiplier(final Parameters options) {
        if (options.containsKey(Flag.DEGREE)) {
            return Double.parseDouble(options.get(Flag.DEGREE));
        }
        return 1.0;
    }

    /**
     * Prints the specified table with the specified cell colors and possibly insert line breaks.
     * @param count The current number of tables printed in one row.
     * @param tableCount The max number of tables printed in one row.
     * @param iteration The current iteration.
     * @param table The table to print.
     * @param color The cell colors.
     * @param writer The writer to send the output to.
     * @param transpose Transpose the tables?
     * @param breakAtColumn Insert line breaks after this number of columns. Ignored if 0.
     * @return The next number of tables printed in the current row.
     * @throws IOException If some error occurs during output.
     */
    private static int printTables(
        final int count,
        final int tableCount,
        final int iteration,
        final String[][] table,
        final String[][] color,
        final BufferedWriter writer,
        final boolean transpose,
        final int breakAtColumn
    ) throws IOException {
        if (count < tableCount) {
            LaTeXUtils.printTable(
                table,
                Optional.of(color),
                LaTeXUtils.defaultColumnDefinition("1cm"),
                transpose,
                breakAtColumn,
                writer
            );
            return count + 1;
        }
        LaTeXUtils.printTable(
            table,
            Optional.of(color),
            LaTeXUtils.defaultColumnDefinition("1cm"),
            transpose,
            breakAtColumn,
            writer
        );
        return 1;
    }

    private FloydWarshallAlgorithm() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final Pair<Graph<String, Integer>, Vertex<String>> pair = GraphAlgorithms.parseOrGenerateGraph(input.options);
        FloydWarshallAlgorithm.floydWarshall(
            pair.x,
            Algorithm.WARSHALL.name.equals(input.options.get(Flag.ALGORITHM)),
            new StringVertexComparator(),
            input.options,
            input.exerciseWriter,
            input.solutionWriter
        );
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
