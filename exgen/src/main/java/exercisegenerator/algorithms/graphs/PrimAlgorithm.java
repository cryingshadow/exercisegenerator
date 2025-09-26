package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.*;
import exercisegenerator.structures.graphs.layout.*;

public class PrimAlgorithm implements GraphAlgorithm<PrimResult<String>> {

    public static final PrimAlgorithm INSTANCE = new PrimAlgorithm();

    static final PrimEntry INFINITY = new PrimEntry(0, true, false);

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
            result[0][index++] = vertex.label().get();
        }
        for (int i = 0; i < table.length; i++) {
            result[i + 1][0] = String.valueOf(i + 1);
            for (int j = 0; j < table[i].length; j++) {
                result[i + 1][j + 1] = solution ? (table[i][j] == null ? "" : table[i][j].toString()) : "";
            }
        }
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
    }

    private PrimAlgorithm() {}

    @Override
    public PrimResult<String> apply(final GraphProblem problem) {
        final Graph<String, Integer> originalGraph = problem.graphWithLayout().graph();
        final List<Vertex<String>> vertices =
            new ArrayList<Vertex<String>>(originalGraph.getVertices());
        Collections.sort(vertices, problem.comparator());
        final int numberOfVertices = vertices.size();
        final Graph<String, Integer> tree = originalGraph.nodeCopy();
        int recentlyAdded = vertices.indexOf(problem.startNode().get());
        final int[] parents = new int[numberOfVertices];
        final PrimEntry[][] table = new PrimEntry[numberOfVertices][numberOfVertices];
        for (int i = 0; i < numberOfVertices; i++) {
            table[0][i] = i == recentlyAdded ? new PrimEntry(0, false, true) : PrimAlgorithm.INFINITY;
            parents[i] = -1;
        }
        for (int i = 1; i < numberOfVertices; i++) {
            for (final Edge<Integer, String> edge : originalGraph.getAdjacencySet(vertices.get(recentlyAdded))) {
                final int toIndex = vertices.indexOf(edge.to());
                final PrimEntry toEntry = table[i - 1][toIndex];
                if (toEntry != null && !toEntry.done() && toEntry.compareTo(edge.label().get()) > 0) {
                    table[i][toIndex] = new PrimEntry(edge.label().get());
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
            recentlyAdded = minIndex;
        }
        return new PrimResult<String>(
            table,
            new GraphWithLayout<>(tree, problem.graphWithLayout().layout())
        );
    }

    @Override
    public String commandPrefix() {
        return "Prim";
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public GraphWithLayout<String, Integer, Integer> getGraphWithLayoutForProblemInstance(
        final GraphProblem problem,
        final Parameters<Flag> options
    ) {
        return GraphAlgorithm.stretch(
            new GraphWithLayout<String, Integer, Integer>(
                problem.graphWithLayout().graph(),
                ((GridGraphLayout<String, Integer>)problem.graphWithLayout().layout()).setDirected(false)
            ),
            GraphAlgorithm.parseDistanceFactor(options)
        );
    }

    @Override
    public void printAfterSingleProblemInstance(
        final GraphProblem problem,
        final PrimResult<String> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("F\\\"uhren Sie den \\emphasize{Algorithmus von Prim} auf diesem Graphen mit dem ");
        writer.write("\\emphasize{Startknoten ");
        writer.write(problem.startNode().get().label().get());
        writer.write("} aus.");
        Main.newLine(writer);
        writer.write("F\\\"ullen Sie dazu die nachfolgende Tabelle aus und geben Sie den resultierenden minimalen ");
        writer.write("Spannbaum an:\\\\[2ex]");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<GraphProblem> problems,
        final List<PrimResult<String>> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("F\\\"uhren Sie den \\emphasize{Algorithmus von Prim} auf den folgenden Graphen aus.");
        Main.newLine(writer);
        writer.write("F\\\"ullen Sie dazu die jeweiligen Tabelle aus und geben Sie den jeweils resultierenden ");
        writer.write("minimalen Spannbaum an.\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printSolutionInstance(
        final GraphProblem problem,
        final PrimResult<String> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        final List<Vertex<String>> vertices =
            new ArrayList<Vertex<String>>(problem.graphWithLayout().graph().getVertices());
        Collections.sort(vertices, problem.comparator());
        PrimAlgorithm.printTable(solution.table(), vertices, true, writer);
        writer.write("Minimaler Spannbaum:\\\\");
        Main.newLine(writer);
        LaTeXUtils.printAdjustboxBeginning(writer);
        solution.treeWithLayout().graph().printTikZ(
            GraphAlgorithm.stretch(
                ((GridGraphLayout<String, Integer>)solution.treeWithLayout().layout()).setDirected(false),
                GraphAlgorithm.parseDistanceFactor(options)
            ),
            writer
        );
        LaTeXUtils.printAdjustboxEnd(writer);
    }

    @Override
    public void printSolutionSpace(
        final GraphProblem problem,
        final PrimResult<String> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        final List<Vertex<String>> vertices =
            new ArrayList<Vertex<String>>(problem.graphWithLayout().graph().getVertices());
        Collections.sort(vertices, problem.comparator());
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
        PrimAlgorithm.printTable(solution.table(), vertices, false, writer);
        writer.write("Minimaler Spannbaum:");
        Main.newLine(writer);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("10ex"), options, writer);
    }

}
