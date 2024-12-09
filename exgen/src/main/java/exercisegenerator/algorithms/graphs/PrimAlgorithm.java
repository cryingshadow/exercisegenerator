package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.*;

public class PrimAlgorithm implements GraphAlgorithm<PrimResult<String>> {

    public static final PrimAlgorithm INSTANCE = new PrimAlgorithm();

    static final PrimEntry INFINITY = new PrimEntry(0, true, false);

    private static final String PRIM_PATTERN =
        "F\\\"uhren Sie den \\emphasize{Algorithmus von Prim} auf diesem Graphen mit dem \\emphasize{Startknoten %s} aus.";

    private static void printTable(
        final PrimEntry[][] table,
        final List<Vertex<String>> vertices,
        final boolean solution,
        final BufferedWriter writer
    ) throws IOException {
        final String[][] result = new String[table.length + 1][table[0].length + 1];
        result[0][0] = "\\#Iteration";
        int index = 1;
        for (final Vertex<String> vertex : vertices) {
            result[0][index++] = vertex.label.get();
        }
        for (int i = 0; i < table.length; i++) {
            result[i + 1][0] = String.valueOf(i + 1);
            for (int j = 0; j < table[i].length; j++) {
                result[i + 1][j + 1] = solution ? (table[i][j] == null ? "" : table[i][j].toString()) : "";
            }
        }
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        LaTeXUtils.printArrayStretch(1.5, writer);
        LaTeXUtils.printTable(
            result,
            Optional.empty(),
            cols -> String.format("|c|*{%d}{C{%s}|}", cols - 1, "7mm"),
            true,
            0,
            writer
        );
        LaTeXUtils.printArrayStretch(1, writer);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
    }

    private PrimAlgorithm() {}

    @Override
    public PrimResult<String> apply(final GraphProblem problem) {
        final List<Vertex<String>> vertices = new ArrayList<Vertex<String>>(problem.graph().getVertices());
        Collections.sort(vertices, problem.comparator());
        final int numberOfVertices = vertices.size();
        final Graph<String, Integer> tree = problem.graph().nodeCopy();
        int recentlyAdded = vertices.indexOf(problem.startNode());
        final int[] parents = new int[numberOfVertices];
        final PrimEntry[][] table = new PrimEntry[numberOfVertices][numberOfVertices];
        for (int i = 0; i < numberOfVertices; i++) {
            table[0][i] = i == recentlyAdded ? new PrimEntry(0, false, true) : PrimAlgorithm.INFINITY;
            parents[i] = -1;
        }
        for (int i = 1; i < numberOfVertices; i++) {
            for (final Edge<Integer, String> edge : problem.graph().getAdjacencyList(vertices.get(recentlyAdded))) {
                final int toIndex = vertices.indexOf(edge.to);
                final PrimEntry toEntry = table[i - 1][toIndex];
                if (toEntry != null && !toEntry.done() && toEntry.compareTo(edge.label.get()) > 0) {
                    table[i][toIndex] = new PrimEntry(edge.label.get());
                    parents[toIndex] = recentlyAdded;
                }
            }
            Integer min = null;
            int minIndex = -1;
            for (int vertex = 0; vertex < numberOfVertices; vertex++) {
                if (table[i][vertex] == null && table[i - 1][vertex] != null && !table[i - 1][vertex].done()) {
                    table[i][vertex] = table[i - 1][vertex];
                }
                final PrimEntry current = table[i][vertex];
                if (current != null && !current.done() && current.compareTo(min) < 0) {
                    min = current.value();
                    minIndex = vertex;
                }
            }
            table[i][minIndex] = table[i][minIndex].toDone();
            final Vertex<String> from = vertices.get(parents[minIndex]);
            final Vertex<String> to = vertices.get(minIndex);
            tree.addEdge(from, Optional.of(min), to);
            tree.addEdge(to, Optional.of(min), from);
            tree.setGrid(problem.graph().getGrid());
            recentlyAdded = minIndex;
        }
        return new PrimResult<String>(table, tree);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public void printExercise(
        final GraphProblem problem,
        final PrimResult<String> solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        final List<Vertex<String>> vertices = new ArrayList<Vertex<String>>(problem.graph().getVertices());
        Collections.sort(vertices, problem.comparator());
        GraphAlgorithm.printGraphExercise(
            problem.graph(),
            String.format(PrimAlgorithm.PRIM_PATTERN, problem.startNode().label.get()),
            GraphAlgorithm.parseDistanceFactor(options),
            GraphPrintMode.UNDIRECTED,
            writer
        );
        writer.write(
            "F\\\"ullen Sie dazu die nachfolgende Tabelle aus und geben Sie den resultierenden minimalen Spannbaum an:\\\\[2ex]"
        );
        Main.newLine(writer);
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
        PrimAlgorithm.printTable(solution.table(), vertices, false, writer);
        writer.write("Minimaler Spannbaum:");
        Main.newLine(writer);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("10ex"), options, writer);
    }

    @Override
    public void printSolution(
        final GraphProblem problem,
        final PrimResult<String> solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        final List<Vertex<String>> vertices = new ArrayList<Vertex<String>>(problem.graph().getVertices());
        Collections.sort(vertices, problem.comparator());
        PrimAlgorithm.printTable(solution.table(), vertices, true, writer);
        writer.write("Minimaler Spannbaum:");
        Main.newLine(writer);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        solution.tree().printTikZ(GraphPrintMode.UNDIRECTED, GraphAlgorithm.parseDistanceFactor(options), null, writer);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
        Main.newLine(writer);
    }

}
