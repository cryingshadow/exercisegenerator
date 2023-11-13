package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

public class FloydWarshallAlgorithm implements AlgorithmImplementation {

    private static class LayoutConfiguration {

        private final int columns;

        private LayoutConfiguration() {
            this(1);
        }

        private LayoutConfiguration(final int columns) {
            this.columns = columns;
        }

    }

    public static final FloydWarshallAlgorithm INSTANCE = new FloydWarshallAlgorithm();

    private static final String FLOYD_WARSHALL_PATTERN =
        "F\\\"uhren Sie den \\emphasize{Algorithmus von %s} auf diesem Graphen aus. F\\\"ullen Sie dazu die "
        + "nachfolgenden Tabellen aus.\\\\[2ex]";

    public static <V> Integer[][][] floyd(
        final Graph<V, Integer> graph,
        final Optional<? extends Comparator<Vertex<V>>> optionalComparator
    ) {
        final List<Vertex<V>> vertices = FloydWarshallAlgorithm.getSortedListOfVertices(graph, optionalComparator);
        final int size = vertices.size();
        final Integer[][][] tables = new Integer[size + 1][size][size];
        for (int from = 0; from < size; from++) {
            for (int to = 0; to < size; to++) {
                if (from == to) {
                    tables[0][from][to] = 0;
                } else {
                    final Set<Edge<Integer, V>> edges = graph.getEdges(vertices.get(from), vertices.get(to));
                    if (!edges.isEmpty()) {
                        tables[0][from][to] = edges.iterator().next().label.get();
                    }
                }
            }
        }
        for (int via = 0; via < size; via++) {
            for (int from = 0; from < size; from++) {
                for (int to = 0; to < size; to++) {
                    final Integer oldValue = tables[via][from][to];
                    final Integer fromVia = tables[via][from][via];
                    final Integer viaTo = tables[via][via][to];
                    final Integer update = fromVia == null ? null : (viaTo == null ? null : fromVia + viaTo);
                    if (oldValue == null || update != null && update < oldValue) {
                        tables[via + 1][from][to] = update;
                    } else {
                        tables[via + 1][from][to] = oldValue;
                    }
                }
            }
        }
        return tables;
    }

    public static <V> boolean[][][] warshall(
        final Graph<V, Integer> graph,
        final Optional<? extends Comparator<Vertex<V>>> optionalComparator
    ) {
        final List<Vertex<V>> vertices = FloydWarshallAlgorithm.getSortedListOfVertices(graph, optionalComparator);
        final int size = vertices.size();
        final boolean[][][] tables = new boolean[size + 1][size][size];
        for (int from = 0; from < size; from++) {
            for (int to = 0; to < size; to++) {
                if (from == to) {
                    tables[0][from][to] = true;
                } else {
                    final Set<Edge<Integer, V>> edges = graph.getEdges(vertices.get(from), vertices.get(to));
                    if (!edges.isEmpty()) {
                        tables[0][from][to] = true;
                    } else {
                        tables[0][from][to] = false;
                    }
                }
            }
        }
        for (int via = 0; via < size; via++) {
            for (int from = 0; from < size; from++) {
                for (int to = 0; to < size; to++) {
                    if (tables[via][from][via] && tables[via][via][to]) {
                        tables[via + 1][from][to] = true;
                    } else {
                        tables[via + 1][from][to] = tables[via][from][to];
                    }
                }
            }
        }
        return tables;
    }

    private static LayoutConfiguration generateLayoutConfiguration(final Parameters options) {
        return new LayoutConfiguration();
    }

    private static <V> List<Vertex<V>> getSortedListOfVertices(
        final Graph<V, Integer> graph,
        final Optional<? extends Comparator<Vertex<V>>> optionalComparator
    ) {
        final List<Vertex<V>> vertices = new ArrayList<Vertex<V>>(graph.getVertices());
        if (optionalComparator.isPresent()) {
            Collections.sort(vertices, optionalComparator.get());
        }
        return vertices;
    }

    private static LayoutConfiguration parseLayoutConfiguration(final BufferedReader reader, final Parameters options)
    throws IOException {
        final String line = reader.readLine();
        if (line.startsWith("!")) {
            try {
                return new LayoutConfiguration(Integer.parseInt(line.substring(1)));
            } catch (final NumberFormatException e) {
                // fall through
            }
        }
        return FloydWarshallAlgorithm.generateLayoutConfiguration(options);
    }

    private static LayoutConfiguration parseOrGenerateLayoutConfiguration(final Parameters options) throws IOException {
        return new ParserAndGenerator<LayoutConfiguration>(
            FloydWarshallAlgorithm::parseLayoutConfiguration,
            FloydWarshallAlgorithm::generateLayoutConfiguration
        ).getResult(options);
    }

    private static void printExercise(
        final Graph<String, Integer> graph,
        final boolean warshall,
        final LayoutConfiguration layout,
        final String[][][] tables,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        GraphAlgorithms.printGraphExercise(
            graph,
            String.format(FloydWarshallAlgorithm.FLOYD_WARSHALL_PATTERN, warshall ? "Warshall" : "Floyd"),
            GraphAlgorithms.parseDistanceFactor(options),
            GraphPrintMode.ALL,
            writer
        );
        LaTeXUtils.printSolutionSpaceBeginning(Optional.empty(), options, writer);
        if (layout.columns > 1) {
            LaTeXUtils.beginMulticols(layout.columns, writer);
        }
        FloydWarshallAlgorithm.printTables(tables, Optional.empty(), layout, writer);
        if (layout.columns > 1) {
            LaTeXUtils.endMulticols(writer);
        }
        LaTeXUtils.printSolutionSpaceEnd(Optional.empty(), options, writer);
    }

    private static void printSolution(
        final LayoutConfiguration layout,
        final String[][][] tables,
        final String[][][] color,
        final BufferedWriter writer
    ) throws IOException {
        if (layout.columns > 1) {
            LaTeXUtils.beginMulticols(layout.columns, writer);
        }
        FloydWarshallAlgorithm.printTables(tables, Optional.of(color), layout, writer);
        if (layout.columns > 1) {
            LaTeXUtils.endMulticols(writer);
        }
        Main.newLine(writer);
    }

    private static void printTables(
        final String[][][] tables,
        final Optional<String[][][]> color,
        final LayoutConfiguration layout,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printArrayStretch(1.5, writer);
        for (int i = 0; i < tables.length; i++) {
            if (layout.columns > 1) {
                LaTeXUtils.resizeboxBeginning("\\columnwidth", "!", writer);
            }
            LaTeXUtils.printTable(
                tables[i],
                color.isEmpty() ? Optional.empty() : Optional.of(color.get()[i]),
                LaTeXUtils.defaultColumnDefinition("1cm"),
                true,
                0,
                writer
            );
            if (layout.columns > 1) {
                LaTeXUtils.resizeboxEnd(writer);
            }
            if (layout.columns > 1 && i == tables.length / layout.columns) {
                Main.newLine(writer);
                writer.write("\\vfill\\null");
                Main.newLine(writer);
                writer.write("\\columnbreak");
                Main.newLine(writer);
                Main.newLine(writer);
            } else {
                LaTeXUtils.printVerticalProtectedSpace(writer);
            }
        }
        LaTeXUtils.printArrayStretch(1.0, writer);
    }

    private static String[][][] toColorTables(final boolean[][][] tables) {
        final String[][][] result = new String[tables.length][tables.length][tables.length];
        for (int i = 1; i < tables.length; i++) {
            for (int from = 0; from < tables.length - 1; from++) {
                for (int to = 0; to < tables.length - 1; to++) {
                    if (tables[i][from][to] != tables[i - 1][from][to]) {
                        result[i][from + 1][to + 1] = "black!20";
                    }
                }
            }
        }
        return result;
    }

    private static String[][][] toColorTables(final Integer[][][] tables) {
        final String[][][] result = new String[tables.length][tables.length][tables.length];
        for (int i = 1; i < tables.length; i++) {
            for (int from = 0; from < tables.length - 1; from++) {
                for (int to = 0; to < tables.length - 1; to++) {
                    if (tables[i][from][to] != null && !tables[i][from][to].equals(tables[i - 1][from][to])) {
                        result[i][from + 1][to + 1] = "black!20";
                    }
                }
            }
        }
        return result;
    }

    private static String toIteration(final int i) {
        return String.format("\\circled{%d}", i + 1);
    }

    private static String[][][] toPrintableTables(
        final boolean[][][] tables,
        final boolean fillSolution,
        final String[] labels
    ) {
        final String[][][] result = new String[tables.length][tables.length][tables.length];
        for (int i = 0; i < tables.length; i++) {
            result[i][0][0] = FloydWarshallAlgorithm.toIteration(i);
            for (int j = 0; j < labels.length; j++) {
                result[i][j + 1][0] = labels[j];
                result[i][0][j + 1] = labels[j];
            }
        }
        for (int from = 0; from < labels.length; from++) {
            for (int to = 0; to < labels.length; to++) {
                result[0][from + 1][to + 1] = FloydWarshallAlgorithm.toString(tables[0][from][to]);
            }
        }
        if (fillSolution) {
            for (int i = 1; i < tables.length; i++) {
                for (int from = 0; from < labels.length; from++) {
                    for (int to = 0; to < labels.length; to++) {
                        result[i][from + 1][to + 1] = FloydWarshallAlgorithm.toString(tables[i][from][to]);
                    }
                }
            }
        }
        return result;
    }

    private static String[][][] toPrintableTables(
        final Integer[][][] tables,
        final boolean fillSolution,
        final String[] labels
    ) {
        final String[][][] result = new String[tables.length][tables.length][tables.length];
        for (int i = 0; i < tables.length; i++) {
            result[i][0][0] = FloydWarshallAlgorithm.toIteration(i);
            for (int j = 0; j < labels.length; j++) {
                result[i][j + 1][0] = labels[j];
                result[i][0][j + 1] = labels[j];
            }
        }
        for (int from = 0; from < labels.length; from++) {
            for (int to = 0; to < labels.length; to++) {
                result[0][from + 1][to + 1] = FloydWarshallAlgorithm.toString(tables[0][from][to]);
            }
        }
        if (fillSolution) {
            for (int i = 1; i < tables.length; i++) {
                for (int from = 0; from < labels.length; from++) {
                    for (int to = 0; to < labels.length; to++) {
                        result[i][from + 1][to + 1] = FloydWarshallAlgorithm.toString(tables[i][from][to]);
                    }
                }
            }
        }
        return result;
    }

    private static String toString(final boolean entry) {
        return entry ? "true" : "false";
    }

    private static String toString(final Integer entry) {
        return entry == null ? "$\\infty$" : entry.toString();
    }

    private FloydWarshallAlgorithm() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final Pair<Graph<String, Integer>, Vertex<String>> pair = GraphAlgorithms.parseOrGenerateGraph(input.options);
        final LayoutConfiguration layout = FloydWarshallAlgorithm.parseOrGenerateLayoutConfiguration(input.options);
        final Graph<String, Integer> graph = pair.x;
        final Optional<StringVertexComparator> optionalComparator = Optional.of(new StringVertexComparator());
        final List<Vertex<String>> vertices = FloydWarshallAlgorithm.getSortedListOfVertices(graph, optionalComparator);
        final String[] labels = vertices.stream().map(v -> v.label.get()).toArray(String[]::new);
        if (Algorithm.WARSHALL.name.equals(input.options.get(Flag.ALGORITHM))) {
            final boolean[][][] tables = FloydWarshallAlgorithm.warshall(graph, optionalComparator);
            FloydWarshallAlgorithm.printExercise(
                graph,
                true,
                layout,
                FloydWarshallAlgorithm.toPrintableTables(tables, false, labels),
                input.options,
                input.exerciseWriter
            );
            FloydWarshallAlgorithm.printSolution(
                layout,
                FloydWarshallAlgorithm.toPrintableTables(tables, true, labels),
                FloydWarshallAlgorithm.toColorTables(tables),
                input.solutionWriter
            );
        } else {
            final Integer[][][] tables = FloydWarshallAlgorithm.floyd(graph, optionalComparator);
            FloydWarshallAlgorithm.printExercise(
                graph,
                false,
                layout,
                FloydWarshallAlgorithm.toPrintableTables(tables, false, labels),
                input.options,
                input.exerciseWriter
            );
            FloydWarshallAlgorithm.printSolution(
                layout,
                FloydWarshallAlgorithm.toPrintableTables(tables, true, labels),
                FloydWarshallAlgorithm.toColorTables(tables),
                input.solutionWriter
            );
        }
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
