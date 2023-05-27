package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

public class BellmanFordAlgorithm implements AlgorithmImplementation {

    static class BellmanFordStep<V> {
        final Map<V, Integer> distances;
        final Map<V, V> predecessors;
        BellmanFordStep(final Map<V, Integer> distances, final Map<V, V> predecessors) {
            this.distances = new LinkedHashMap<V, Integer>(distances);
            this.predecessors = new LinkedHashMap<V, V>(predecessors);
        }
        @Override
        public boolean equals(final Object o) {
            if (o instanceof BellmanFordStep) {
                @SuppressWarnings("unchecked")
                final
                BellmanFordStep<V> other = (BellmanFordStep<V>)o;
                return this.distances.equals(other.distances) && this.predecessors.equals(other.predecessors);
            }
            return false;
        }
        @Override
        public int hashCode() {
            return 43 + 2 * this.distances.hashCode() + 3 * this.predecessors.hashCode();
        }
        @Override
        public String toString() {
            return this
                .distances
                .keySet()
                .stream()
                .map(key -> key + "=" + this.distances.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
        }
    }

    public static final BellmanFordAlgorithm INSTANCE = new BellmanFordAlgorithm();

    private static final String BELLMAN_FORD_PATTERN =
        "F\\\"uhren Sie den \\emphasize{Bellman-Ford}-Algorithmus auf diesem Graphen mit dem \\emphasize{Startknoten %s} aus.";

    public static <V> List<BellmanFordStep<V>> bellmanFord(
        final Graph<V, Integer> graph,
        final Vertex<V> start,
        final Comparator<Vertex<V>> comparator
    ) {
        final List<Vertex<V>> vertices = GraphAlgorithms.getSortedListOfVertices(graph, comparator);
        final int numberOfVertices = vertices.size();
        final List<BellmanFordStep<V>> result = new LinkedList<BellmanFordStep<V>>();
        final Map<V, Integer> distances = new LinkedHashMap<V, Integer>();
        final Map<V, V> predecessors = new LinkedHashMap<V, V>();
        distances.put(start.label.get(), 0);
        result.add(new BellmanFordStep<V>(distances, predecessors));
        boolean changed = true;
        for (int i = 0; i < numberOfVertices - 1 && changed; i++) {
            changed = false;
            for (final Vertex<V> from : vertices) {
                final V fromLabel = from.label.get();
                if (!distances.containsKey(fromLabel)) {
                    continue;
                }
                for (final Edge<Integer, V> edge : graph.getAdjacencyList(from)) {
                    final int newDistance = distances.get(from.label.get()) + edge.x;
                    final V toLabel = edge.y.label.get();
                    if (!distances.containsKey(toLabel) || newDistance < distances.get(toLabel)) {
                        distances.put(toLabel, newDistance);
                        predecessors.put(toLabel, fromLabel);
                        changed = true;
                    }
                }
            }
            result.add(new BellmanFordStep<V>(distances, predecessors));
        }
        return result;
    }

    private static void printExercise(
        final Graph<String, Integer> graph,
        final String startLabel,
        final List<Vertex<String>> vertices,
        final List<BellmanFordStep<String>> result,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        GraphAlgorithms.printGraphExercise(
            graph,
            String.format(BellmanFordAlgorithm.BELLMAN_FORD_PATTERN,  startLabel),
            GraphAlgorithms.parseDistanceFactor(options),
            GraphPrintMode.ALL,
            writer
        );
        writer.write("F\\\"ullen Sie dazu die nachfolgenden Tabellen aus:\\\\[2ex]");
        Main.newLine(writer);
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
        BellmanFordAlgorithm.printTables(vertices, result, false, writer);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, writer);
    }

    private static void printSolution(
        final List<Vertex<String>> vertices,
        final List<BellmanFordStep<String>> result,
        final BufferedWriter writer
    ) throws IOException {
        BellmanFordAlgorithm.printTables(vertices, result, true, writer);
        Main.newLine(writer);
    }

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
                cols -> String.format("|c|*{%d}{C{%s}|}", cols - 1, columnWidth),
                true,
                6,
                true,
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
            final String label = vertex.label.get();
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
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final Pair<Graph<String, Integer>, Vertex<String>> pair = GraphAlgorithms.parseOrGenerateGraph(input.options);
        final Comparator<Vertex<String>> comparator = new StringVertexComparator();
        final List<BellmanFordStep<String>> result = BellmanFordAlgorithm.bellmanFord(pair.x, pair.y, comparator);
        final List<Vertex<String>> vertices = GraphAlgorithms.getSortedListOfVertices(pair.x, comparator);
        BellmanFordAlgorithm.printExercise(pair.x, pair.y.label.get(), vertices, result, input.options, input.exerciseWriter);
        BellmanFordAlgorithm.printSolution(vertices, result, input.solutionWriter);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
