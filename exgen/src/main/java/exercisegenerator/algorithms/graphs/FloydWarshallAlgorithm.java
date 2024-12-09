package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.*;

interface FloydWarshallAlgorithm<T> extends GraphAlgorithm<T[][][]> {

    static class LayoutConfiguration {

        private final int columns;

        private LayoutConfiguration() {
            this(1);
        }

        private LayoutConfiguration(final int columns) {
            this.columns = columns;
        }

    }

    static final String FLOYD_WARSHALL_PATTERN =
        "F\\\"uhren Sie den \\emphasize{Algorithmus von %s} auf diesem Graphen aus. F\\\"ullen Sie dazu die "
        + "nachfolgenden Tabellen aus.\\\\[2ex]";

    private static LayoutConfiguration generateLayoutConfiguration(final Parameters options) {
        return new LayoutConfiguration();
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

    private static String toIteration(final int i) {
        return String.format("\\circled{%d}", i + 1);
    }

    private static String[] toLabels(final GraphProblem problem) {
        final List<Vertex<String>> vertices =
            GraphAlgorithm.getSortedListOfVertices(problem.graph(), problem.comparator());
        return vertices.stream().map(v -> v.label.get()).toArray(String[]::new);
    }

    @Override
    default public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    default public void printExercise(
        final GraphProblem problem,
        final T[][][] solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        final LayoutConfiguration layout = FloydWarshallAlgorithm.parseOrGenerateLayoutConfiguration(options);
        GraphAlgorithm.printGraphExercise(
            problem.graph(),
            String.format(FloydWarshallAlgorithm.FLOYD_WARSHALL_PATTERN, this.getName()),
            GraphAlgorithm.parseDistanceFactor(options),
            GraphPrintMode.ALL,
            writer
        );
        LaTeXUtils.printSolutionSpaceBeginning(Optional.empty(), options, writer);
        if (layout.columns > 1) {
            LaTeXUtils.beginMulticols(layout.columns, writer);
        }
        FloydWarshallAlgorithm.printTables(
            this.toPrintableTables(solution, false, FloydWarshallAlgorithm.toLabels(problem)),
            Optional.empty(),
            layout,
            writer
        );
        if (layout.columns > 1) {
            LaTeXUtils.endMulticols(writer);
        }
        LaTeXUtils.printSolutionSpaceEnd(Optional.empty(), options, writer);
    }

    @Override
    default public void printSolution(
        final GraphProblem problem,
        final T[][][] solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        final LayoutConfiguration layout = FloydWarshallAlgorithm.parseOrGenerateLayoutConfiguration(options);
        if (layout.columns > 1) {
            LaTeXUtils.beginMulticols(layout.columns, writer);
        }
        FloydWarshallAlgorithm.printTables(
            this.toPrintableTables(solution, true, FloydWarshallAlgorithm.toLabels(problem)),
            Optional.of(this.toColorTables(solution)),
            layout,
            writer
        );
        if (layout.columns > 1) {
            LaTeXUtils.endMulticols(writer);
        }
        Main.newLine(writer);
    }

    @Override
    default T[][][] apply(final GraphProblem problem) {
        final Graph<String, Integer> graph = problem.graph();
        final List<Vertex<String>> vertices = GraphAlgorithm.getSortedListOfVertices(graph, problem.comparator());
        final int size = vertices.size();
        final T[][][] tables = this.createTable(size + 1, size, size);
        for (int from = 0; from < size; from++) {
            for (int to = 0; to < size; to++) {
                tables[0][from][to] = this.initialValue(graph, vertices, from, to);
            }
        }
        for (int via = 0; via < size; via++) {
            for (int from = 0; from < size; from++) {
                for (int to = 0; to < size; to++) {
                    tables[via + 1][from][to] = this.update(tables, from, via, to);
                }
            }
        }
        return tables;
    }

    T[][][] createTable(int size1, int size2, int size3);

    String getName();

    T initialValue(Graph<String, Integer> graph, List<Vertex<String>> vertices, int from, int to);

    String toString(T value);

    T update(T[][][] tables, int from, int via, int to);

    private String[][][] toColorTables(final T[][][] tables) {
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

    private String[][][] toPrintableTables(
        final T[][][] tables,
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
                result[0][from + 1][to + 1] = this.toString(tables[0][from][to]);
            }
        }
        if (fillSolution) {
            for (int i = 1; i < tables.length; i++) {
                for (int from = 0; from < labels.length; from++) {
                    for (int to = 0; to < labels.length; to++) {
                        result[i][from + 1][to + 1] = this.toString(tables[i][from][to]);
                    }
                }
            }
        }
        return result;
    }

}
