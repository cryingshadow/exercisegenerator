package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.*;

public class BellmanFordAlgorithm implements GraphAlgorithm<List<BellmanFordStep<String>>> {

    public static final BellmanFordAlgorithm INSTANCE = new BellmanFordAlgorithm();

    private static final String BELLMAN_FORD_PATTERN =
        "F\\\"uhren Sie den \\emphasize{Bellman-Ford}-Algorithmus auf diesem Graphen mit dem \\emphasize{Startknoten %s} aus.";

    private static void printTables(
        final List<Vertex<String>> vertices,
        final List<BellmanFordStep<String>> result,
        final boolean fill,
        final BufferedWriter writer
        ) throws IOException {
    final String columnWidth = "16mm";
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        Main.newLine(writer);
        LaTeXUtils.printArrayStretch(1.5, writer);
        boolean first = true;
        for (final BellmanFordStep<String> step : result) {
            if (first) {
                first = false;
            } else {
                LaTeXUtils.printVerticalProtectedSpace(writer);
            }
            LaTeXUtils.printTable(
                BellmanFordAlgorithm.toTable(step, vertices, fill),
                Optional.empty(),
                cols -> String.format("|c|*{%d}{C{%s}|}", cols, columnWidth),
                true,
                6,
                1,
                writer
            );
        }
        LaTeXUtils.printArrayStretch(1.0, writer);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
    }

    private static String[][] toTable(
        final BellmanFordStep<String> step,
        final List<Vertex<String>> vertices,
        final boolean fill
    ) {
        final String[][] result = new String[2][vertices.size() + 1];
        result[0][0] = "\\textbf{Knoten}";
        result[1][0] = "\\textbf{Distanz/Vorg\\\"anger}";
        int i = 1;
        for (final Vertex<String> vertex : vertices) {
            final String label = vertex.label().get();
            result[0][i] = String.format("\\textbf{%s}", label);
            if (fill) {
                if (step.distances.containsKey(label)) {
                    String cellText = step.distances.get(label).toString();
                    if (step.predecessors.containsKey(label)) {
                        cellText += String.format("/%s", step.predecessors.get(label));
                    }
                    result[1][i] = LaTeXUtils.inlineMath(cellText);
                } else {
                    result[1][i] = LaTeXUtils.inlineMath("\\infty");
                }
            } else {
                result[1][i] = "";
            }
            i++;
        }
        return result;
    }

    private BellmanFordAlgorithm() {}

    @Override
    public List<BellmanFordStep<String>> apply(final GraphProblem problem) {
        final List<Vertex<String>> vertices =
            GraphAlgorithm.getSortedListOfVertices(problem.graphWithLayout().graph(), problem.comparator());
        final int numberOfVertices = vertices.size();
        final List<BellmanFordStep<String>> result = new LinkedList<BellmanFordStep<String>>();
        final Map<String, Integer> distances = new LinkedHashMap<String, Integer>();
        final Map<String, String> predecessors = new LinkedHashMap<String, String>();
        distances.put(problem.startNode().get().label().get(), 0);
        result.add(new BellmanFordStep<String>(distances, predecessors));
        boolean changed = true;
        for (int i = 0; i < numberOfVertices - 1 && changed; i++) {
            changed = false;
            for (final Vertex<String> from : vertices) {
                final String fromLabel = from.label().get();
                if (!distances.containsKey(fromLabel)) {
                    continue;
                }
                for (final Edge<Integer, String> edge : problem.graphWithLayout().graph().getAdjacencySet(from)) {
                    final int newDistance = distances.get(from.label().get()) + edge.label().get();
                    final String toLabel = edge.to().label().get();
                    if (!distances.containsKey(toLabel) || newDistance < distances.get(toLabel)) {
                        distances.put(toLabel, newDistance);
                        predecessors.put(toLabel, fromLabel);
                        changed = true;
                    }
                }
            }
            result.add(new BellmanFordStep<String>(distances, predecessors));
        }
        return result;
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
        final List<BellmanFordStep<String>> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        final List<Vertex<String>> vertices =
            GraphAlgorithm.getSortedListOfVertices(problem.graphWithLayout().graph(), problem.comparator());
        GraphAlgorithm.printGraphExercise(
            GraphAlgorithm.stretch(problem.graphWithLayout(), GraphAlgorithm.parseDistanceFactor(options)),
            String.format(BellmanFordAlgorithm.BELLMAN_FORD_PATTERN, problem.startNode().get().label().get()),
            writer
        );
        writer.write("F\\\"ullen Sie dazu die nachfolgenden Tabellen aus:\\\\[2ex]");
        Main.newLine(writer);
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
        BellmanFordAlgorithm.printTables(vertices, solution, false, writer);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, writer);
    }

    @Override
    public void printSolution(
        final GraphProblem problem,
        final List<BellmanFordStep<String>> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        final List<Vertex<String>> vertices =
            GraphAlgorithm.getSortedListOfVertices(problem.graphWithLayout().graph(), problem.comparator());
        BellmanFordAlgorithm.printTables(vertices, solution, true, writer);
        Main.newLine(writer);
    }

}
