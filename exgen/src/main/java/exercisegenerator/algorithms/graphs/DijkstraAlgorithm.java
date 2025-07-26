package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.*;

public class DijkstraAlgorithm implements GraphAlgorithm<DijkstraTables> {

    public static final DijkstraAlgorithm INSTANCE = new DijkstraAlgorithm();

    private static final String COLUMN_WIDTH = "16mm";

    private static final String DIJKSTRA_PATTERN =
        "F\\\"uhren Sie den \\emphasize{Dijkstra} Algorithmus auf diesem Graphen mit dem \\emphasize{Startknoten %s} aus.";

    private static Optional<Integer> computeVertexIndexWithMinimumDistance(
        final int columnIndex,
        final int size,
        final Integer[] distances,
        final Set<Integer> used,
        final String[][] exTable,
        final String[][] solTable
    ) {
        Integer currentMinimumDistance = null;
        int indexOfVertexWithMinimumDistance = -1;
        for (int toVertexIndex = 1; toVertexIndex < size; toVertexIndex++) {
            final Integer distanceToVertex = distances[toVertexIndex];
            if (distanceToVertex == null) {
                solTable[columnIndex][toVertexIndex] = "$\\infty$";
            } else if (used.contains(toVertexIndex)) {
                solTable[columnIndex][toVertexIndex] = "\\textbf{--}";
            } else {
                if (currentMinimumDistance == null || currentMinimumDistance > distanceToVertex) {
                    currentMinimumDistance = distanceToVertex;
                    indexOfVertexWithMinimumDistance = toVertexIndex;
                }
                solTable[columnIndex][toVertexIndex] = String.valueOf(distanceToVertex);
            }
            exTable[columnIndex][toVertexIndex] = "";
        }
        if (currentMinimumDistance == null) {
            return Optional.empty();
        }
        return Optional.of(indexOfVertexWithMinimumDistance);
    }

    private static void copyToExtended(final String[][] table, final String[][] tableExtended) {
        for (int i = 0; i < table.length; i++) {
            tableExtended[i][0] = table[i][0];
            System.arraycopy(table[i], 1, tableExtended[i], 2, table.length - 1);
        }
    }

    private static <V> void fillRowHeadingsAndIdsForDijkstra(
        final String[][] exTable,
        final String[][] solTable,
        final List<Vertex<V>> vertices,
        final Map<Vertex<V>, Integer> vertexIds,
        final Vertex<V> start
    ) {
        int rowIndex = 1;
        solTable[0][0] = Main.TEXT_VERSION == TextVersion.ABRAHAM ? "\\texttt{v}" : "\\textbf{Knoten}";
        exTable[0][0] = solTable[0][0];
        for (final Vertex<V> vertex : vertices) {
            if (!vertex.equals(start)) {
                solTable[0][rowIndex] = DijkstraAlgorithm.toRowHeading(vertex.label);
                exTable[0][rowIndex] = solTable[0][rowIndex];
                vertexIds.put(vertex, rowIndex);
                rowIndex++;
            }
        }
    }

    private static <V> void improveDistancesForVertex(
        final Vertex<V> currentVertex,
        final int currentVertexId,
        final Graph<V, Integer> graph,
        final Map<Vertex<V>, Integer> vertexIds,
        final Integer[] distances
    ) {
        for (final Edge<Integer, V> edge : graph.getAdjacencyList(currentVertex)) {
            final Integer toVertexId = vertexIds.get(edge.to());
            if (
                toVertexId != null
                && (
                    distances[toVertexId] == null
                    || distances[toVertexId] > distances[currentVertexId] + edge.label().get()
                )
            ) {
                distances[toVertexId] = distances[currentVertexId] + edge.label().get();
            }
        }
    }

    private static <V> void setColumnHeadForDijkstra(
        final String[][] exTable,
        final String[][] solTable,
        final int columnIndex,
        final Vertex<V> currentVertex
    ) {
        exTable[columnIndex][0] = "";
        solTable[columnIndex][0] = DijkstraAlgorithm.toColumnHeading(currentVertex.label);
    }

    private static <V> String toColumnHeading(final Optional<V> label) {
        return DijkstraAlgorithm.toHeading(label);
    }

    private static <V> String toHeading(final Optional<V> label) {
        return label.isEmpty() ? "" : String.format("\\textbf{%s}", label.get().toString());
    }

    private static <V> String toRowHeading(final Optional<V> label) {
        return Main.TEXT_VERSION == TextVersion.ABRAHAM ?
            (label.isEmpty() ? "" : String.format("\\texttt{key[}%s\\texttt{]}", label.get().toString())) :
                DijkstraAlgorithm.toHeading(label);
    }

    private DijkstraAlgorithm() {}

    @Override
    public DijkstraTables apply(final GraphProblem problem) {
        final Vertex<String> start = problem.startNode();
        final Graph<String, Integer> graph = problem.graphWithLayout().graph();
        final List<Vertex<String>> vertices = GraphAlgorithm.getSortedListOfVertices(graph, problem.comparator());
        vertices.remove(start);
        vertices.add(0, start);
        final int size = vertices.size();
        final String[][] exTable;
        final String[][] solTable;
        final String[][] exColor;
        final String[][] solColor;
        final Integer[] distances = new Integer[size];
        final Map<Vertex<String>, Integer> vertexIds = new LinkedHashMap<Vertex<String>, Integer>();
        final Set<Integer> used = new LinkedHashSet<Integer>();
        exTable = new String[size][size];
        solTable = new String[size][size];
        exColor = new String[size][size];
        solColor = new String[size][size];
        DijkstraAlgorithm.fillRowHeadingsAndIdsForDijkstra(exTable, solTable, vertices, vertexIds, start);
        distances[0] = 0;
        int currentVertexId = 0;
        for (int columnIndex = 1; columnIndex < size; columnIndex++) {
            used.add(currentVertexId);
            final Vertex<String> currentVertex = vertices.get(currentVertexId);
            DijkstraAlgorithm.setColumnHeadForDijkstra(exTable, solTable, columnIndex, currentVertex);
            DijkstraAlgorithm.improveDistancesForVertex(currentVertex, currentVertexId, graph, vertexIds, distances);
            final Optional<Integer> vertexIndexWithMinimumDistance =
                DijkstraAlgorithm.computeVertexIndexWithMinimumDistance(
                    columnIndex,
                    size,
                    distances,
                    used,
                    exTable,
                    solTable
                );
            if (vertexIndexWithMinimumDistance.isEmpty()) {
                // no shortening possible
                break;
            }
            currentVertexId = vertexIndexWithMinimumDistance.get();
            solColor[columnIndex][currentVertexId] = "black!20";
        }
        exTable[1][0] = DijkstraAlgorithm.toColumnHeading(start.label);
        if (Main.TEXT_VERSION == TextVersion.ABRAHAM) {
            final String[][] exTableExtended = new String[size][size + 1];
            final String[][] exColorExtended = new String[size][size + 1];
            final String[][] solTableExtended = new String[size][size + 1];
            final String[][] solColorExtended = new String[size][size + 1];
            DijkstraAlgorithm.copyToExtended(exTable, exTableExtended);
            DijkstraAlgorithm.copyToExtended(exColor, exColorExtended);
            DijkstraAlgorithm.copyToExtended(solTable, solTableExtended);
            DijkstraAlgorithm.copyToExtended(solColor, solColorExtended);
            solTableExtended[0][1] = DijkstraAlgorithm.toRowHeading(start.label);
            exTableExtended[0][1] = solTableExtended[0][1];
            for (int i = 1; i < size; i++) {
                solTableExtended[i][1] = String.valueOf(0);
            }
            return new DijkstraTables(exTableExtended, exColorExtended, solTableExtended, solColorExtended);
        }
        return new DijkstraTables(exTable, exColor, solTable, solColor);
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
        final DijkstraTables solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        final SolutionSpaceMode mode = SolutionSpaceMode.parsePreprintMode(options);
        GraphAlgorithm.printGraphExercise(
            GraphAlgorithm.stretch(problem.graphWithLayout(), GraphAlgorithm.parseDistanceFactor(options)),
            String.format(DijkstraAlgorithm.DIJKSTRA_PATTERN,  problem.startNode().label.get().toString()),
            writer
        );
        switch (mode) {
            case ALWAYS:
            case SOLUTION_SPACE:
                switch (Main.TEXT_VERSION) {
                    case ABRAHAM:
                        writer.write("F\\\"ullen Sie dazu die nachfolgende Tabelle aus, indem Sie den Wert von ");
                        writer.write("\\texttt{v} und \\texttt{key} \\emphasize{nach jeder Iteration} der ");
                        writer.write("\\texttt{while}-Schleife eintragen:\\\\[2ex]");
                        break;
                    default:
                        writer.write("F\\\"ullen Sie dazu die nachfolgende Tabelle aus:\\\\[2ex]");
                }
                break;
            case NEVER:
                // do nothing
        }
        Main.newLine(writer);
        switch (mode) {
            case SOLUTION_SPACE:
                LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
                // fall-through
            case ALWAYS:
                LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
                Main.newLine(writer);
                LaTeXUtils.printArrayStretch(1.5, writer);
                LaTeXUtils.printTable(
                    solution.exTable,
                    Optional.of(solution.exColor),
                    LaTeXUtils.defaultColumnDefinition(DijkstraAlgorithm.COLUMN_WIDTH),
                    false,
                    10,
                    writer
                );
                LaTeXUtils.printArrayStretch(1.0, writer);
                LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
                if (mode == SolutionSpaceMode.SOLUTION_SPACE) {
                    LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, writer);
                } else {
                    Main.newLine(writer);
                    Main.newLine(writer);
                }
                break;
            case NEVER:
                Main.newLine(writer);
        }
    }

    @Override
    public void printSolution(
        final GraphProblem problem,
        final DijkstraTables solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        Main.newLine(writer);
        LaTeXUtils.printArrayStretch(1.5, writer);
        LaTeXUtils.printTable(
            solution.solTable,
            Optional.of(solution.solColor),
            LaTeXUtils.defaultColumnDefinition(DijkstraAlgorithm.COLUMN_WIDTH),
            false,
            10,
            writer
        );
        LaTeXUtils.printArrayStretch(1.0, writer);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
        Main.newLine(writer);
        writer.write("\\vspace*{1ex}");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("Die grau unterlegten Zellen markieren, an welcher Stelle f\\\"ur welchen Knoten die minimale");
        writer.write(" Distanz sicher berechnet worden ist.");
        Main.newLine(writer);
        Main.newLine(writer);
    }

}
