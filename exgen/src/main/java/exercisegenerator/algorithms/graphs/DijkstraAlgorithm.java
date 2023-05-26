package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

public class DijkstraAlgorithm implements AlgorithmImplementation {

    public static final DijkstraAlgorithm INSTANCE = new DijkstraAlgorithm();

    private static final String DIJKSTRA_PATTERN =
        "F\\\"uhren Sie den \\emphasize{Dijkstra} Algorithmus auf diesem Graphen mit dem \\emphasize{Startknoten %s} aus.";

    public static <V> DijkstraTables dijkstra(
        final Graph<V, Integer> graph,
        final Vertex<V> start,
        final Comparator<Vertex<V>> comp
    ) {
        final List<Vertex<V>> vertices = new ArrayList<Vertex<V>>(graph.getVertices());
        if (comp != null) {
            Collections.sort(vertices, comp);
        }
        vertices.remove(start);
        vertices.add(0, start);
        final int size = vertices.size();
        final String[][] exTable;
        final String[][] solTable;
        final String[][] exColor;
        final String[][] solColor;
        final Integer[] distances = new Integer[size];
        final Map<Vertex<V>, Integer> vertexIds = new LinkedHashMap<Vertex<V>, Integer>();
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
            final Vertex<V> currentVertex = vertices.get(currentVertexId);
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
            final Integer toVertexId = vertexIds.get(edge.y);
            if (
                toVertexId != null
                && (distances[toVertexId] == null || distances[toVertexId] > distances[currentVertexId] + edge.x)
            ) {
                distances[toVertexId] = distances[currentVertexId] + edge.x;
            }
        }
    }

    /**
     * Prints exercise and solution for the Dijkstra algorithm.
     * @param graph The graph.
     * @param start The start vertex.
     * @param comp A comparator for sorting the vertices in the table (may be null - then no sorting is applied).
     * @param mode The preprint mode.
     * @param exWriter The writer to send the exercise output to.
     * @param solWriter The writer to send the solution output to.
     * @throws IOException If some error occurs during output.
     */
    private static <V> void printDijkstra(
        final Graph<V, Integer> graph,
        final Vertex<V> start,
        final DijkstraTables tables,
        final PreprintMode mode,
        final Parameters options,
        final BufferedWriter exWriter,
        final BufferedWriter solWriter
    ) throws IOException {
        GraphAlgorithms.printGraphExercise(
            graph,
            String.format(DijkstraAlgorithm.DIJKSTRA_PATTERN,  start.label.get().toString()),
            GraphAlgorithms.parseDistanceFactor(options),
            GraphPrintMode.ALL,
            exWriter
        );
        switch (mode) {
            case ALWAYS:
            case SOLUTION_SPACE:
                switch (Main.TEXT_VERSION) {
                    case ABRAHAM:
                        exWriter.write("F\\\"ullen Sie dazu die nachfolgende Tabelle aus, indem Sie den Wert von ");
                        exWriter.write("\\texttt{v} und \\texttt{key} \\emphasize{nach jeder Iteration} der ");
                        exWriter.write("\\texttt{while}-Schleife eintragen:\\\\[2ex]");
                        break;
                    default:
                        exWriter.write("F\\\"ullen Sie dazu die nachfolgende Tabelle aus:\\\\[2ex]");
                }
                break;
            case NEVER:
                // do nothing
        }
        Main.newLine(exWriter);
        final String columnWidth = "16mm";
        switch (mode) {
            case SOLUTION_SPACE:
                LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, exWriter);
                // fall-through
            case ALWAYS:
                LaTeXUtils.printBeginning(LaTeXUtils.CENTER, exWriter);
                Main.newLine(exWriter);
                LaTeXUtils.printArrayStretch(1.5, exWriter);
                LaTeXUtils.printTable(
                    tables.exTable,
                    Optional.of(tables.exColor),
                    LaTeXUtils.defaultColumnDefinition(columnWidth),
                    false,
                    10,
                    exWriter
                );
                LaTeXUtils.printArrayStretch(1.0, exWriter);
                LaTeXUtils.printEnd(LaTeXUtils.CENTER, exWriter);
                if (mode == PreprintMode.SOLUTION_SPACE) {
                    LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, exWriter);
                } else {
                    Main.newLine(exWriter);
                    Main.newLine(exWriter);
                }
                break;
            case NEVER:
                Main.newLine(exWriter);
        }

        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, solWriter);
        Main.newLine(solWriter);
        LaTeXUtils.printArrayStretch(1.5, solWriter);
        LaTeXUtils.printTable(
            tables.solTable,
            Optional.of(tables.solColor),
            LaTeXUtils.defaultColumnDefinition(columnWidth),
            false,
            10,
            solWriter
        );
        LaTeXUtils.printArrayStretch(1.0, solWriter);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, solWriter);
        Main.newLine(solWriter);
        solWriter.write("\\vspace*{1ex}");
        Main.newLine(solWriter);
        Main.newLine(solWriter);
        solWriter.write("Die grau unterlegten Zellen markieren, an welcher Stelle f\\\"ur welchen Knoten die minimale");
        solWriter.write(" Distanz sicher berechnet worden ist.");
        Main.newLine(solWriter);
        Main.newLine(solWriter);
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
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final Pair<Graph<String, Integer>, Vertex<String>> pair = GraphAlgorithms.parseOrGenerateGraph(input.options);
        DijkstraAlgorithm.printDijkstra(
            pair.x,
            pair.y,
            DijkstraAlgorithm.dijkstra(pair.x, pair.y, new StringVertexComparator()),
            PreprintMode.parsePreprintMode(input.options),
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
