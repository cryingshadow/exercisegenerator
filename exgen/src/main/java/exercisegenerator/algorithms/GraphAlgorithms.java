package exercisegenerator.algorithms;

import java.io.*;
import java.util.*;
import java.util.Map.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

/**
 * Class offering methods for graph algorithms.
 */
public abstract class GraphAlgorithms {

    /**
     * The default value being probably close to the edge values.
     */
    private static final int DEFAULT_EDGE_ROOT = 3;

    /**
     * The default value being probably close to the edge values adjacent to source/sink vertices.
     */
    private static final int DEFAULT_SOURCE_SINK_ROOT = 7;

    /**
     * The phrase "each residual graph".
     */
    private static final String EACH_RESIDUAL_GRAPH = "\\emphasize{jedes Restnetzwerk (auch das initiale)}";

    /**
     * The set of those graph algorithms needing a start vertex.
     */
    private static final Set<String> GRAPH_ALGORITHMS_WITH_START_VERTEX =
        GraphAlgorithms.initGraphAlgorithmsWithStartVertex();

    /**
     * The name of a residual graph.
     */
    private static final String RESIDUAL_GRAPH = "Restnetzwerk";

    /**
     * The set of those graph algorithms working on undirected graphs.
     */
    private static final Set<String> UNDIRECTED_GRAPH_ALGORITHMS = GraphAlgorithms.initUndirectedGraphAlgorithms();

    public static <V, E> List<V> breadthFirstSearch(
        final Graph<V, E> graph,
        final Vertex<V> start,
        final Comparator<Vertex<V>> comparator
    ) {
        final Set<Vertex<V>> used = new LinkedHashSet<Vertex<V>>();
        final Queue<Vertex<V>> queue = new LinkedList<Vertex<V>>();
        queue.offer(start);
        final List<V> result = new LinkedList<V>();
        while (!queue.isEmpty()) {
            final Vertex<V> vertex = queue.poll();
            if (used.contains(vertex)) {
                continue;
            }
            used.add(vertex);
            result.add(vertex.label.get());
            final List<Vertex<V>> nextVertices = new ArrayList<Vertex<V>>(graph.getAdjacentVertices(vertex));
            Collections.sort(nextVertices, comparator);
            queue.addAll(nextVertices);
        }
        return result;
    }

    /**
     * @param gen A random number generator.
     * @param numOfVertices The number of vertices (excluding source and sink) in the returned flow network.
     * @return A random flow network with <code>numOfVertices</code> vertices labeled with Strings (each vertex has a
     *         unique label and the source is labeled with s and the sink is labeled with t) and edges labeled with
     *         pairs of Integers (the current flow and the capacity - the current flow will be set to 0).
     */
    public static Graph<String, FlowPair> createRandomFlowNetwork(final Random gen, final int numOfVertices) {
        if (numOfVertices < 0) {
            throw new IllegalArgumentException("Number of vertices must not be negative!");
        }
        final Graph<String, FlowPair> graph = new Graph<String, FlowPair>();
        final Map<GridCoordinates, Vertex<String>> grid =
            new LinkedHashMap<GridCoordinates, Vertex<String>>();
        final Vertex<String> source = new Vertex<String>(Optional.of("s"));
        final Map<Vertex<String>, VertexGridPosition> positions =
            new LinkedHashMap<Vertex<String>, VertexGridPosition>();
        final GridCoordinates startPos = new GridCoordinates(0, 0);
        GraphAlgorithms.addVertex(
            source,
            graph,
            new Pair<GridCoordinates, Boolean>(startPos, true),
            grid,
            positions
        );
        if (numOfVertices == 0) {
            final Vertex<String> sink = new Vertex<String>(Optional.of("t"));
            GraphAlgorithms.addVertex(
                sink,
                graph,
                new Pair<GridCoordinates, Boolean>(new GridCoordinates(1, 0), false),
                grid,
                positions
            );
            final int value = GraphAlgorithms.randomEdgeValue(gen, GraphAlgorithms.DEFAULT_EDGE_ROOT);
            graph.addEdge(source, new FlowPair(0, value), sink);
            graph.setGrid(grid);
            return graph;
        }
        int xPos = 1;
        int minYPos = 0;
        int curMinYPos = 0;
        int curMaxYPos = 0;
        int prevMinYPos;
        int prevMaxYPos;
        int remainingVertices = numOfVertices;
        int letter = 0;
        // not all vertices can have diagonals: reduce only possible at every second step
        while (remainingVertices > 0) {
            prevMaxYPos = curMaxYPos;
            prevMinYPos = curMinYPos;
            final int prevXPos = xPos - 1;
            final boolean minDiagonal = (prevMinYPos + prevXPos) % 2 == 0;
            final boolean maxDiagonal = (prevMaxYPos + prevXPos) % 2 == 0;
            if (prevMaxYPos == prevMinYPos) {
                if (minDiagonal) {
                    if (remainingVertices > 2) {
                        switch (gen.nextInt(3)) {
                            case 0:
                                // expand min
                                curMinYPos--;
                                break;
                            case 1:
                                // expand max
                                curMaxYPos++;
                                break;
                            default:
                                // exand both
                                curMinYPos--;
                                curMaxYPos++;
                        }
                    } else if (remainingVertices == 2) {
                        if (gen.nextBoolean()) {
                            // expand min
                            curMinYPos--;
                        } else {
                            // expand max
                            curMaxYPos++;
                        }
                    }
                    // else keep
                }
                // else keep
            } else {
                final int reduceMin = 1;
                final int keepMin = 2;
                final int expandMin = 4;
                final int reduceMax = 8;
                final int keepMax = 16;
                final int expandMax = 32;
                final List<Integer> options = new ArrayList<Integer>();
                if (minDiagonal) {
                    if (maxDiagonal) {
                        options.add(reduceMin + reduceMax);
                        if (GraphAlgorithms.enoughVertices(remainingVertices, prevMinYPos - 1, prevMaxYPos + 1, xPos)) {
                            options.add(keepMin + reduceMax);
                            options.add(reduceMin + keepMax);
                            options.add(keepMin + keepMax);
                            options.add(keepMin + keepMax);
                            options.add(expandMin + keepMax);
                            options.add(expandMin + keepMax);
                            options.add(expandMin + keepMax);
                            options.add(keepMin + expandMax);
                            options.add(keepMin + expandMax);
                            options.add(keepMin + expandMax);
                            options.add(expandMin + expandMax);
                            options.add(expandMin + expandMax);
                            options.add(expandMin + expandMax);
                            options.add(expandMin + expandMax);
                            options.add(expandMin + expandMax);
                            options.add(expandMin + expandMax);
                        } else {
                            final boolean justExpandMax =
                                GraphAlgorithms.enoughVertices(remainingVertices, prevMinYPos, prevMaxYPos + 1, xPos);
                            final boolean justExpandMin =
                                GraphAlgorithms.enoughVertices(remainingVertices, prevMinYPos - 1, prevMaxYPos, xPos);
                            if (justExpandMax && justExpandMin) {
                                options.add(keepMin + reduceMax);
                                options.add(reduceMin + keepMax);
                                options.add(keepMin + keepMax);
                                options.add(keepMin + keepMax);
                                options.add(expandMin + keepMax);
                                options.add(expandMin + keepMax);
                                options.add(expandMin + keepMax);
                                options.add(keepMin + expandMax);
                                options.add(keepMin + expandMax);
                                options.add(keepMin + expandMax);
                            } else if (justExpandMax) {
                                options.add(keepMin + reduceMax);
                                options.add(reduceMin + keepMax);
                                options.add(keepMin + keepMax);
                                options.add(keepMin + keepMax);
                                options.add(keepMin + expandMax);
                                options.add(keepMin + expandMax);
                                options.add(keepMin + expandMax);
                            } else if (justExpandMin) {
                                options.add(keepMin + reduceMax);
                                options.add(reduceMin + keepMax);
                                options.add(keepMin + keepMax);
                                options.add(keepMin + keepMax);
                                options.add(expandMin + keepMax);
                                options.add(expandMin + keepMax);
                                options.add(expandMin + keepMax);
                            } else if (GraphAlgorithms.enoughVertices(remainingVertices, prevMinYPos, prevMaxYPos, xPos)) {
                                options.add(keepMin + reduceMax);
                                options.add(reduceMin + keepMax);
                                options.add(keepMin + keepMax);
                                options.add(keepMin + keepMax);
                            } else {
                                if (GraphAlgorithms.enoughVertices(remainingVertices, prevMinYPos + 1, prevMaxYPos, xPos)) {
                                    options.add(reduceMin + keepMax);
                                }
                                if (GraphAlgorithms.enoughVertices(remainingVertices, prevMinYPos, prevMaxYPos - 1, xPos)) {
                                    options.add(keepMin + reduceMax);
                                }
                            }
                        }
                    } else {
                        options.add(reduceMin + keepMax);
                        if (GraphAlgorithms.enoughVertices(remainingVertices, prevMinYPos - 1, prevMaxYPos, xPos)) {
                            options.add(keepMin + keepMax);
                            options.add(keepMin + keepMax);
                            options.add(expandMin + keepMax);
                            options.add(expandMin + keepMax);
                            options.add(expandMin + keepMax);
                        } else if (GraphAlgorithms.enoughVertices(remainingVertices, prevMinYPos, prevMaxYPos, xPos)) {
                            options.add(keepMin + keepMax);
                            options.add(keepMin + keepMax);
                        }
                    }
                } else if (maxDiagonal) {
                    options.add(keepMin + reduceMax);
                    if (GraphAlgorithms.enoughVertices(remainingVertices, prevMinYPos, prevMaxYPos + 1, xPos)) {
                        options.add(keepMin + keepMax);
                        options.add(keepMin + keepMax);
                        options.add(keepMin + expandMax);
                        options.add(keepMin + expandMax);
                        options.add(keepMin + expandMax);
                    } else if (GraphAlgorithms.enoughVertices(remainingVertices, prevMinYPos, prevMaxYPos, xPos)) {
                        options.add(keepMin + keepMax);
                        options.add(keepMin + keepMax);
                    }
                } else {
                    options.add(keepMin + keepMax);
                }
                switch (options.get(gen.nextInt(options.size()))) {
                    case reduceMin | reduceMax:
                        curMinYPos++;
                        curMaxYPos--;
                        break;
                    case reduceMin | keepMax:
                        curMinYPos++;
                        break;
                    case keepMin | reduceMax:
                        curMaxYPos--;
                        break;
                    case keepMin | keepMax:
                        // do nothing
                        break;
                    case keepMin | expandMax:
                        curMaxYPos++;
                        break;
                    case expandMin | keepMax:
                        curMinYPos--;
                        break;
                    case expandMin | expandMax:
                        curMinYPos--;
                        curMaxYPos++;
                        break;
                    default:
                        throw new IllegalStateException("Impossible combination of reduce/keep/expand!");
                }
            }
            final int verticesAtXPos = curMaxYPos - curMinYPos + 1;
            minYPos = Math.min(minYPos, curMinYPos);
            for (int yPos = curMinYPos; yPos <= curMaxYPos; yPos++) {
                // at least one edge from previous level
                final Vertex<String> vertex =
                    new Vertex<String>(Optional.of(GraphAlgorithms.toStringLabel(letter++)));
                final boolean hasDiagonals = (xPos + yPos) % 2 == 0;
                final GridCoordinates pos = new GridCoordinates(xPos, yPos);
                GraphAlgorithms.addVertex(
                    vertex,
                    graph,
                    new Pair<GridCoordinates, Boolean>(pos, hasDiagonals),
                    grid,
                    positions
                );
                if (hasDiagonals) {
                    final List<Vertex<String>> existing = new ArrayList<Vertex<String>>();
                    Vertex<String> previousVertex = grid.get(new GridCoordinates(prevXPos, yPos - 1));
                    if (previousVertex != null) {
                        existing.add(previousVertex);
                    }
                    previousVertex =  grid.get(new GridCoordinates(prevXPos, yPos));
                    if (previousVertex != null) {
                        existing.add(previousVertex);
                    }
                    previousVertex =  grid.get(new GridCoordinates(prevXPos, yPos + 1));
                    if (previousVertex != null) {
                        existing.add(previousVertex);
                    }
                    final int index = gen.nextInt(existing.size());
                    previousVertex = existing.remove(index);
                    graph.addEdge(
                        previousVertex,
                        new FlowPair(
                            0,
                            GraphAlgorithms.randomEdgeValue(
                                gen,
                                prevXPos == 0 ?
                                    GraphAlgorithms.DEFAULT_SOURCE_SINK_ROOT :
                                        GraphAlgorithms.DEFAULT_EDGE_ROOT
                            )
                        ),
                        vertex
                    );
                    for (final Vertex<String> otherVertex : existing) {
                        if (gen.nextBoolean()) {
                            graph.addEdge(
                                otherVertex,
                                new FlowPair(
                                    0,
                                    GraphAlgorithms.randomEdgeValue(gen, GraphAlgorithms.DEFAULT_EDGE_ROOT)
                                ),
                                vertex
                            );
                        }
                    }
                } else {
                    graph.addEdge(
                        grid.get(new GridCoordinates(prevXPos, yPos)),
                        new FlowPair(
                            0,
                            GraphAlgorithms.randomEdgeValue(
                                gen,
                                prevXPos == 0 ?
                                    GraphAlgorithms.DEFAULT_SOURCE_SINK_ROOT :
                                        GraphAlgorithms.DEFAULT_EDGE_ROOT
                            )
                        ),
                        vertex
                    );
                }
                if (yPos > curMinYPos) {
                    // north-south edges
                    final Vertex<String> north = grid.get(new GridCoordinates(xPos, yPos - 1));
                    if (gen.nextBoolean()) {
                        graph.addEdge(
                            north,
                            new FlowPair(0, GraphAlgorithms.randomEdgeValue(gen, GraphAlgorithms.DEFAULT_EDGE_ROOT)),
                            vertex
                        );
                    }
                    if (gen.nextBoolean()) {
                        graph.addEdge(
                            vertex,
                            new FlowPair(0, GraphAlgorithms.randomEdgeValue(gen, GraphAlgorithms.DEFAULT_EDGE_ROOT)),
                            north
                        );
                    }
                }
            }
            // at least one edge for each vertex on previous level to current level
            outer: for (int prevYPos = prevMinYPos; prevYPos <= prevMaxYPos; prevYPos++) {
                final Vertex<String> previousVertex = grid.get(new GridCoordinates(prevXPos, prevYPos));
                final boolean prevDiagonals = (prevXPos + prevYPos) % 2 == 0;
                if (prevDiagonals) {
                    final List<Vertex<String>> existing = new ArrayList<Vertex<String>>();
                    Vertex<String> nextVertex = grid.get(new GridCoordinates(xPos, prevYPos - 1));
                    if (nextVertex != null) {
                        existing.add(nextVertex);
                    }
                    nextVertex =  grid.get(new GridCoordinates(xPos, prevYPos));
                    if (nextVertex != null) {
                        existing.add(nextVertex);
                    }
                    nextVertex =  grid.get(new GridCoordinates(xPos, prevYPos + 1));
                    if (nextVertex != null) {
                        existing.add(nextVertex);
                    }
                    for (final Vertex<String> otherVertex : existing) {
                        if (!graph.getEdges(previousVertex, otherVertex).isEmpty()) {
                            continue outer;
                        }
                    }
                    final int index = gen.nextInt(existing.size());
                    nextVertex = existing.remove(index);
                    graph.addEdge(
                        previousVertex,
                        new FlowPair(0, GraphAlgorithms.randomEdgeValue(gen, GraphAlgorithms.DEFAULT_EDGE_ROOT)),
                        nextVertex
                    );
                } else {
                    final Vertex<String> nextVertex = grid.get(new GridCoordinates(xPos, prevYPos));
                    if (graph.getEdges(previousVertex, nextVertex).isEmpty()) {
                        graph.addEdge(
                            previousVertex,
                            new FlowPair(0, GraphAlgorithms.randomEdgeValue(gen, GraphAlgorithms.DEFAULT_EDGE_ROOT)),
                            nextVertex
                        );
                    }
                }
            }
            remainingVertices -= verticesAtXPos;
            xPos++;
        }
        final Vertex<String> sink = new Vertex<String>(Optional.of("t"));
        int yPos = curMinYPos + ((curMaxYPos - curMinYPos) / 2);
        if ((xPos + yPos) % 2 != 0) {
            yPos++;
        }
        GraphAlgorithms.addVertex(
            sink,
            graph,
            new Pair<GridCoordinates, Boolean>(new GridCoordinates(xPos, yPos), true),
            grid,
            positions
        );
        final List<Vertex<String>> existing = new ArrayList<Vertex<String>>();
        Vertex<String> previousVertex = grid.get(new GridCoordinates(xPos - 1, yPos - 1));
        if (previousVertex != null) {
            existing.add(previousVertex);
        }
        previousVertex =  grid.get(new GridCoordinates(xPos - 1, yPos));
        if (previousVertex != null) {
            existing.add(previousVertex);
        }
        previousVertex =  grid.get(new GridCoordinates(xPos - 1, yPos + 1));
        if (previousVertex != null) {
            existing.add(previousVertex);
        }
        for (final Vertex<String> otherVertex : existing) {
            graph.addEdge(
                otherVertex,
                new FlowPair(0, GraphAlgorithms.randomEdgeValue(gen, GraphAlgorithms.DEFAULT_SOURCE_SINK_ROOT)),
                sink
            );
        }
        // adjust grid (non-negative coordinates, sum of coordinates even -> has diagonals
        int xAdd = 0;
        if (minYPos % 2 != 0) {
            xAdd++;
        }
        final Map<GridCoordinates, Vertex<String>> newGrid =
            new LinkedHashMap<GridCoordinates, Vertex<String>>();
        for (final Entry<GridCoordinates, Vertex<String>> entry : grid.entrySet()) {
            final GridCoordinates key = entry.getKey();
            newGrid.put(new GridCoordinates(key.x + xAdd, key.y - minYPos), entry.getValue());
        }
        graph.setGrid(newGrid);
        return graph;
    }

    /**
     * @param gen A random number generator.
     * @param numOfVertices The number of vertices in the returned graph.
     * @param undirected Should the graph be undirected?
     * @return A random graph with <code>numOfVertices</code> vertices labeled with Strings (each vertex has a unique
     *         label and there is a vertex with label A) and edges labeled with Integers.
     */
    public static Graph<String, Integer> createRandomGraph(
        final Random gen,
        final int numOfVertices,
        final boolean undirected
    ) {
        if (numOfVertices < 0) {
            throw new IllegalArgumentException("Number of vertices must not be negative!");
        }
        final Graph<String, Integer> graph = new Graph<String, Integer>();
        final Map<GridCoordinates, Vertex<String>> grid =
            new LinkedHashMap<GridCoordinates, Vertex<String>>();
        if (numOfVertices == 0) {
            graph.setGrid(grid);
            return graph;
        }
        final Map<Vertex<String>, VertexGridPosition> positions =
            new LinkedHashMap<Vertex<String>, VertexGridPosition>();
        final Vertex<String> start = new Vertex<String>(Optional.of("A"));
        final GridCoordinates startPos = new GridCoordinates(0, 0);
        final boolean startDiagonal = gen.nextBoolean();
        GraphAlgorithms.addVertex(
            start,
            graph,
            new Pair<GridCoordinates, Boolean>(startPos, startDiagonal),
            grid,
            positions
        );
        final List<Vertex<String>> verticesWithFreeNeighbors = new ArrayList<Vertex<String>>();
        verticesWithFreeNeighbors.add(start);
        for (int letter = 1; letter < numOfVertices; letter++) {
            final Vertex<String> nextVertex =
                verticesWithFreeNeighbors.get(gen.nextInt(verticesWithFreeNeighbors.size()));
            final VertexGridPosition nextPos = positions.get(nextVertex);
            final Pair<GridCoordinates, Boolean> toAddPos = nextPos.randomFreePosition(gen);
            final Vertex<String> toAddVertex =
                new Vertex<String>(Optional.of(GraphAlgorithms.toStringLabel(letter)));
            final VertexGridPosition gridPos = GraphAlgorithms.addVertex(toAddVertex, graph, toAddPos, grid, positions);
            final int value = GraphAlgorithms.randomEdgeValue(gen, GraphAlgorithms.DEFAULT_EDGE_ROOT);
            graph.addEdge(nextVertex, value, toAddVertex);
            if (undirected) {
                graph.addEdge(toAddVertex, value, nextVertex);
            }
            final List<Pair<GridCoordinates, Boolean>> existing = gridPos.getExistingPositions();
            final List<Pair<Vertex<String>, Vertex<String>>> freeVertexPairs =
                new ArrayList<Pair<Vertex<String>, Vertex<String>>>();
            for (final Pair<GridCoordinates, Boolean> other : existing) {
                final Vertex<String> otherVertex = grid.get(other.x);
                if (otherVertex.equals(nextVertex)) {
                    if (!undirected) {
                        freeVertexPairs.add(new Pair<Vertex<String>, Vertex<String>>(toAddVertex, otherVertex));
                    }
                } else {
                    freeVertexPairs.add(new Pair<Vertex<String>, Vertex<String>>(toAddVertex, otherVertex));
                    if (!undirected) {
                        freeVertexPairs.add(new Pair<Vertex<String>, Vertex<String>>(otherVertex, toAddVertex));
                    }
                }
            }
            for (int numEdges = GraphAlgorithms.randomNumOfEdges(gen, freeVertexPairs.size()); numEdges > 0; numEdges--) {
                final int pairIndex = gen.nextInt(freeVertexPairs.size());
                final Pair<Vertex<String>, Vertex<String>> pair = freeVertexPairs.remove(pairIndex);
                final int nextValue = GraphAlgorithms.randomEdgeValue(gen, GraphAlgorithms.DEFAULT_EDGE_ROOT);
                graph.addEdge(pair.x, nextValue, pair.y);
                if (undirected) {
                    graph.addEdge(pair.y, nextValue, pair.x);
                }
            }
            for (final Pair<GridCoordinates, Boolean> neighborPos : existing) {
                final Vertex<String> neighborVertex = grid.get(neighborPos.x);
                if (!positions.get(neighborVertex).hasFreePosition()) {
                    verticesWithFreeNeighbors.remove(neighborVertex);
                }
            }
            if (gridPos.hasFreePosition()) {
                verticesWithFreeNeighbors.add(toAddVertex);
            }
        }
        // adjust grid (non-negative coordinates, sum of coordinates even -> has diagonals
        int minX = 0;
        int minY = 0;
        for (final GridCoordinates pair : grid.keySet()) {
            minX = Math.min(minX, pair.x);
            minY = Math.min(minY, pair.y);
        }
        if (
            (startDiagonal && (startPos.x - minX + startPos.y - minY) % 2 == 1)
            || (!startDiagonal && (startPos.x - minX + startPos.y - minY) % 2 == 0)
        ) {
            minX--;
        }
        final Map<GridCoordinates, Vertex<String>> newGrid =
            new LinkedHashMap<GridCoordinates, Vertex<String>>();
        for (final Entry<GridCoordinates, Vertex<String>> entry : grid.entrySet()) {
            final GridCoordinates key = entry.getKey();
            newGrid.put(new GridCoordinates(key.x - minX, key.y - minY), entry.getValue());
        }
        graph.setGrid(newGrid);
        return graph;
    }

    public static <V, E> List<V> depthFirstSearch(
        final Graph<V, E> graph,
        final Vertex<V> start,
        final Comparator<Vertex<V>> comparator
    ) {
        return GraphAlgorithms.depthFirstSearch(graph, start, comparator, new LinkedHashSet<Vertex<V>>());
    }

    public static void dijkstra(final AlgorithmInput input) throws Exception {
        final Pair<Graph<String, Integer>, Vertex<String>> pair =
            GraphAlgorithms.parseOrGenerateGraph(input.options);
        GraphAlgorithms.printDijkstra(
            pair.x,
            pair.y,
            GraphAlgorithms.dijkstra(pair.x, pair.y, new StringVertexComparator()),
            Algorithm.parsePreprintMode(input.options),
            input.options,
            input.exerciseWriter,
            input.solutionWriter
        );
    }

    public static <V> DijkstraTables dijkstra(
        final Graph<V, Integer> graph,
        final Vertex<V> start,
        final Comparator<Vertex<V>> comp
    ) {
        final List<Vertex<V>> vertices = new ArrayList<Vertex<V>>(graph.getVertices());
        if (comp != null) {
            Collections.sort(vertices, comp);
        }
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
        GraphAlgorithms.fillRowHeadingsAndIdsForDijkstra(exTable, solTable, vertices, vertexIds, start);
        distances[0] = 0;
        int currentVertexId = 0;
        for (int columnIndex = 1; columnIndex < size; columnIndex++) {
            used.add(currentVertexId);
            final Vertex<V> currentVertex = vertices.get(currentVertexId);
            GraphAlgorithms.setColumnHeadForDijkstra(exTable, solTable, columnIndex, currentVertex);
            GraphAlgorithms.improveDistancesForVertex(currentVertex, currentVertexId, graph, vertexIds, distances);
            final Optional<Integer> vertexIndexWithMinimumDistance =
                GraphAlgorithms.computeVertexIndexWithMinimumDistance(
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
        exTable[1][0] = GraphAlgorithms.toColumnHeading(start.label);
        if (Main.TEXT_VERSION == TextVersion.ABRAHAM) {
            final String[][] exTableExtended = new String[size][size + 1];
            final String[][] exColorExtended = new String[size][size + 1];
            final String[][] solTableExtended = new String[size][size + 1];
            final String[][] solColorExtended = new String[size][size + 1];
            GraphAlgorithms.copyToExtended(exTable, exTableExtended);
            GraphAlgorithms.copyToExtended(exColor, exColorExtended);
            GraphAlgorithms.copyToExtended(solTable, solTableExtended);
            GraphAlgorithms.copyToExtended(solColor, solColorExtended);
            solTableExtended[0][1] = GraphAlgorithms.toRowHeading(start.label);
            exTableExtended[0][1] = solTableExtended[0][1];
            for (int i = 1; i < size; i++) {
                solTableExtended[i][1] = String.valueOf(0);
            }
            return new DijkstraTables(exTableExtended, exColorExtended, solTableExtended, solColorExtended);
        }
        return new DijkstraTables(exTable, exColor, solTable, solColor);
    }

    public static void floyd(final AlgorithmInput input) throws Exception {
        final Pair<Graph<String, Integer>, Vertex<String>> pair =
            GraphAlgorithms.parseOrGenerateGraph(input.options);
        GraphAlgorithms.floyd(pair.x, false, new StringVertexComparator(), input.exerciseWriter, input.solutionWriter);
    }

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
    public static <V> void floyd(
        final Graph<V, Integer> graph,
        final boolean warshall,
        final Comparator<Vertex<V>> comp,
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
                weights[current][ids.get(edge.y)] = edge.x;
                if (!warshall) {
                    firstExercise[current+1][ids.get(edge.y)+1] = edge.x.toString();
                    currentSolution[current+1][ids.get(edge.y)+1] = edge.x.toString();
                } else {
                    firstExercise[current+1][ids.get(edge.y)+1] = "true";
                    currentSolution[current+1][ids.get(edge.y)+1] = "true";
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
        if (warshall) {
            graph.printTikZ(GraphPrintMode.NO_EDGE_LABELS, 1, null, exWriter);
        } else {
            graph.printTikZ(GraphPrintMode.ALL, 1, null, exWriter);
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
                GraphAlgorithms.printTables(
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
                GraphAlgorithms.printTables(
                    exCount,
                    tableCount,
                    iteration,
                    exTable,
                    exColors.get(iteration),
                    exWriter,
                    true,
                    tableMaxWidth
                );
        }
        LaTeXUtils.printArrayStretch(1.0, exWriter);
        LaTeXUtils.printArrayStretch(1.0, solWriter);
    }

    public static void fordFulkerson(final AlgorithmInput input) throws Exception {
        final FlowNetworkInput<String, FlowPair> flow = GraphAlgorithms.parseOrGenerateFlowNetwork(input.options);
        GraphAlgorithms.fordFulkerson(
            flow.graph,
            flow.source,
            flow.sink,
            flow.multiplier,
            flow.twocolumns,
            Algorithm.parsePreprintMode(input.options),
            input.exerciseWriter,
            input.solutionWriter
        );
    }

    /**
     * Prints exercise and solution for the Ford-Fulkerson method. Uses the Edmonds-Karp Algorithm for selecting
     * augmenting paths.
     * @param graph The flow network.
     * @param source The source vertex.
     * @param sink The sink vertex.
     * @param multiplier Multiplier for vertex distances.
     * @param twocolumns True if residual graphs and flow networks should be displayed in two columns.
     * @param mode Preprint mode.
     * @param exWriter The writer to send the exercise output to.
     * @param solWriter The writer to send the solution output to.
     * @throws IOException If some error occurs during output.
     */
    public static <V> void fordFulkerson(
        final Graph<V, FlowPair> graph,
        final Vertex<V> source,
        final Vertex<V> sink,
        final double multiplier,
        final boolean twocolumns,
        final PreprintMode mode,
        final BufferedWriter exWriter,
        final BufferedWriter solWriter
    ) throws IOException {
        exWriter.write("Betrachten Sie das folgende Flussnetzwerk mit Quelle ");
        exWriter.write(source.label.isEmpty() ? "" : source.label.get().toString());
        exWriter.write(" und Senke ");
        exWriter.write(sink.label.isEmpty() ? "" : sink.label.get().toString());
        exWriter.write(":\\\\");
        Main.newLine(exWriter);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, exWriter);
        graph.printTikZ(GraphPrintMode.ALL, multiplier, null, exWriter);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, exWriter);
        Main.newLine(exWriter);
        exWriter.write("Berechnen Sie den maximalen Fluss in diesem Netzwerk mithilfe der");
        exWriter.write(" \\emphasize{Ford-Fulkerson Methode}. Geben Sie dazu ");
        exWriter.write(GraphAlgorithms.EACH_RESIDUAL_GRAPH);
        exWriter.write(" sowie \\emphasize{nach jeder Augmentierung} den aktuellen Zustand des Flussnetzwerks an. ");
        exWriter.write("Geben Sie au\\ss{}erdem den \\emphasize{Wert des maximalen Flusses} an.");
        switch (mode) {
            case ALWAYS:
            case SOLUTION_SPACE:
                exWriter.write(
                    " Die vorgegebene Anzahl an L\\\"osungsschritten muss nicht mit der ben\\\"otigten Anzahl "
                );
                exWriter.write("solcher Schritte \\\"ubereinstimmen.");
                break;
            case NEVER:
                // do nothing
        }
        Main.newLine(exWriter);
        int step = 0;
        LaTeXUtils.printSamePageBeginning(
            step++,
            twocolumns ? LaTeXUtils.TWO_COL_WIDTH : LaTeXUtils.COL_WIDTH,
            solWriter
        );
        solWriter.write("Initiales Flussnetzwerk:\\\\[2ex]");
        graph.printTikZ(GraphPrintMode.ALL, multiplier, null, solWriter);
        LaTeXUtils.printSamePageEnd(solWriter);
        Main.newLine(solWriter);
        switch (mode) {
            case SOLUTION_SPACE:
                exWriter.write("\\solutionSpace{");
                Main.newLine(exWriter);
                // fall-through
            case ALWAYS:
                if (twocolumns) {
                    exWriter.write("\\begin{longtable}{cc}");
                    Main.newLine(exWriter);
                }
                break;
            case NEVER:
                // do nothing
        }
        if (twocolumns) {
            solWriter.write("\\begin{longtable}{cc}");
            Main.newLine(solWriter);
        }
        while (true) {
            final Graph<V, Integer> residualGraph = GraphAlgorithms.computeResidualGraph(graph);
            final List<Vertex<V>> path = GraphAlgorithms.selectAugmentingPath(residualGraph, source, sink);
            switch (mode) {
                case ALWAYS:
                case SOLUTION_SPACE:
                    LaTeXUtils.printSamePageBeginning(
                        step,
                        twocolumns ? LaTeXUtils.TWO_COL_WIDTH : LaTeXUtils.COL_WIDTH,
                        exWriter
                    );
                    exWriter.write(GraphAlgorithms.RESIDUAL_GRAPH);
                    exWriter.write(":\\\\[2ex]");
                    Main.newLine(exWriter);
                    break;
                case NEVER:
                    // do nothing
            }
            LaTeXUtils.printSamePageBeginning(
                step++,
                twocolumns ? LaTeXUtils.TWO_COL_WIDTH : LaTeXUtils.COL_WIDTH,
                solWriter
            );
            solWriter.write(GraphAlgorithms.RESIDUAL_GRAPH);
            solWriter.write(":\\\\[2ex]");
            Main.newLine(solWriter);
            final Set<Pair<Vertex<V>, Edge<Integer, V>>> toHighlightResidual;
            switch (Main.TEXT_VERSION) {
                case ABRAHAM:
                    toHighlightResidual = GraphAlgorithms.toEdges(residualGraph, path);
                    break;
                case GENERAL:
                    toHighlightResidual = null;
                    break;
                default:
                    throw new IllegalStateException("Unkown text version!");
            }
            switch (mode) {
                case ALWAYS:
                case SOLUTION_SPACE:
                    residualGraph.printTikZ(GraphPrintMode.NO_EDGES, multiplier, toHighlightResidual, exWriter);
                    LaTeXUtils.printSamePageEnd(exWriter);
                    if (twocolumns) {
                        exWriter.write(" & ");
                    } else {
                        Main.newLine(exWriter);
                    }
                    break;
                case NEVER:
                    // do nothing
            }
            residualGraph.printTikZ(GraphPrintMode.ALL, multiplier, toHighlightResidual, solWriter);
            LaTeXUtils.printSamePageEnd(solWriter);
            if (twocolumns) {
                solWriter.write(" & ");
            } else {
                Main.newLine(solWriter);
            }
            if (path == null) {
                break;
            }
            final Set<Pair<Vertex<V>, Edge<FlowPair, V>>> toHighlightFlow =
                GraphAlgorithms.addFlow(graph, path);
            switch (mode) {
                case ALWAYS:
                case SOLUTION_SPACE:
                    LaTeXUtils.printSamePageBeginning(
                        step,
                        twocolumns ? LaTeXUtils.TWO_COL_WIDTH : LaTeXUtils.COL_WIDTH,
                        exWriter
                    );
                    exWriter.write("N\\\"achstes Flussnetzwerk mit aktuellem Fluss:\\\\[2ex]");
                    Main.newLine(exWriter);
                    graph.printTikZ(GraphPrintMode.NO_EDGE_LABELS, multiplier, null, exWriter);
                    LaTeXUtils.printSamePageEnd(exWriter);
                    if (twocolumns) {
                        exWriter.write("\\\\");
                    }
                    Main.newLine(exWriter);
                    break;
                case NEVER:
                    // do nothing
            }
            LaTeXUtils.printSamePageBeginning(
                step++,
                twocolumns ? LaTeXUtils.TWO_COL_WIDTH : LaTeXUtils.COL_WIDTH,
                solWriter
            );
            solWriter.write("N\\\"achstes Flussnetzwerk mit aktuellem Fluss:\\\\[2ex]");
            Main.newLine(solWriter);
            graph.printTikZ(GraphPrintMode.ALL, multiplier, toHighlightFlow, solWriter);
            LaTeXUtils.printSamePageEnd(solWriter);
            if (twocolumns) {
                solWriter.write("\\\\");
            }
            Main.newLine(solWriter);
        }
        int flow = 0;
        final List<Edge<FlowPair, V>> list = graph.getAdjacencyList(source);
        if (list != null) {
            for (final Edge<FlowPair, V> edge : list) {
                flow += edge.x.x;
            }
        }
        switch (mode) {
            case ALWAYS:
            case SOLUTION_SPACE:
                if (twocolumns) {
                    exWriter.write("\\end{longtable}");
                    Main.newLine(exWriter);
                }
                Main.newLine(exWriter);
                Main.newLine(exWriter);
                exWriter.write("\\vspace*{1ex}");
                Main.newLine(exWriter);
                Main.newLine(exWriter);
                exWriter.write("Der maximale Fluss hat den Wert: ");
                Main.newLine(exWriter);
                if (mode == PreprintMode.SOLUTION_SPACE) {
                    exWriter.write("}");
                    Main.newLine(exWriter);
                }
                Main.newLine(exWriter);
                break;
            case NEVER:
                // do nothing
        }
        if (twocolumns) {
            solWriter.write("\\end{longtable}");
            Main.newLine(solWriter);
        }
        Main.newLine(solWriter);
        Main.newLine(solWriter);
        solWriter.write("\\vspace*{1ex}");
        Main.newLine(solWriter);
        Main.newLine(solWriter);
        solWriter.write("Der maximale Fluss hat den Wert: " + flow);
        Main.newLine(solWriter);
        Main.newLine(solWriter);
    }

    public static String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    public static void prim(final AlgorithmInput input) throws Exception {
        final Pair<Graph<String, Integer>, Vertex<String>> pair =
            GraphAlgorithms.parseOrGenerateGraph(input.options);
        GraphAlgorithms.prim(pair.x, pair.y, new StringVertexComparator(), input.exerciseWriter, input.solutionWriter);
    }

    /**
     * Prints exercise and solution for Prim's algorithm.
     * @param graph The graph.
     * @param start The start vertex.
     * @param comp A comparator for sorting the vertices in the table (may be null - then no sorting is applied).
     * @param exWriter The writer to send the exercise output to.
     * @param solWriter The writer to send the solution output to.
     * @throws IOException If some error occurs during output.
     */
    public static <V> void prim(
        final Graph<V, Integer> graph,
        final Vertex<V> start,
        final Comparator<Vertex<V>> comp,
        final BufferedWriter exWriter,
        final BufferedWriter solWriter
    ) throws IOException {
        final List<Vertex<V>> vertices = new ArrayList<Vertex<V>>(graph.getVertices());
        final String[][] exTable = new String[vertices.size()+1][vertices.size()+1];
        final String[][] solTable = new String[vertices.size()+1][vertices.size()+1];
        exTable[0][0] = "\\#Iteration";
        solTable[0][0] = "\\#Iteration";
        final Map<Vertex<V>, Integer> key = new LinkedHashMap<Vertex<V>, Integer>();
        final AdjacencyLists<V, Integer> parent = new AdjacencyLists<V, Integer>();
        int i = 1;
        for (final Vertex<V> vertex : vertices) {
            key.put(vertex, null);
            final String label = vertex.label.isEmpty() ? "" : vertex.label.get().toString();
            exTable[i][0] = label;
            solTable[i][0] = label;
            i++;
        }
        final List<Vertex<V>> q = new ArrayList<Vertex<V>>(graph.getVertices());
        key.put(start, 0);
        int iteration = 1;
        // actual algorithm
        while (!q.isEmpty()) {
            // extract the minimum from q
            Vertex<V> minVertex = null;
            for (final Vertex<V> vertex : q) {
                if (
                    minVertex == null
                    || key.get(minVertex) == null
                    || (key.get(vertex) != null && key.get(minVertex).intValue() > key.get(vertex).intValue())
                ) {
                    minVertex = vertex;
                }
            }
            // write solution
            exTable[0][iteration] = "" + iteration;
            solTable[0][iteration] = "" + iteration;
            i = 1;
            for (final Vertex<V> vertex : vertices) {
                if (q.contains(vertex)) {
                    if (key.get(vertex) == null) {
                        solTable[i][iteration] = "$\\infty$";
                    } else if (minVertex == vertex) {
                        solTable[i][iteration] = "\\underline{" + key.get(vertex) + "}";
                    } else {
                        solTable[i][iteration] = "" + key.get(vertex);
                    }
                } else {
                    solTable[i][iteration] = "";
                }
                i++;
            }
            // update the minimums successors remaining in q
            for (final Edge<Integer, V> edge : graph.getAdjacencyList(minVertex)) {
                if (q.contains(edge.y) && (key.get(edge.y) == null || edge.x < key.get(edge.y))) {
                    final List<Edge<Integer, V>> adList = new ArrayList<Edge<Integer, V>>();
                    adList.add(new Edge<Integer, V>(edge.x, minVertex));
                    parent.put(edge.y, adList);
                    key.put(edge.y, edge.x);
                }
            }
            q.remove(minVertex);
            iteration++;
        }
        // create output
        for (int j = 1; j < exTable.length; j++) {
            exTable[j][1] = solTable[j][1];
        }
        exWriter.write("F\\\"uhren Sie Prim's Algorithmus auf dem folgenden Graphen aus.");
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, exWriter);
        graph.printTikZ(exWriter, null, false);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, exWriter);
        if (start.label.isEmpty()) {
            throw new IllegalArgumentException("Prim cannot be applied to unlabeled graphs!");
        }
        exWriter.write("Der Startknoten hat hierbei den Schl\\\"ussel " + start.label.get().toString() + ".");
        exWriter.write(" Geben Sie dazu \\emphasize{vor} jedem Durchlauf der \\\"au{\\ss}eren Schleife an,");
        Main.newLine(exWriter);
        exWriter.write("\\begin{enumerate}[1)]");
        Main.newLine(exWriter);
        exWriter.write("    \\item welche Kosten die Randknoten haben (d.\\,h.~f\\\"ur jeden Knoten \\texttt{v} in ");
        exWriter.write("\\texttt{Q} den Wert \\texttt{key[v]})");
        Main.newLine(exWriter);
        exWriter.write("    \\item und welchen Knoten \\texttt{extractMin(Q)} w\\\"ahlt, indem Sie den Kosten-Wert ");
        exWriter.write("des gew\\\"ahlten Randknoten in der Tabelle unterstreichen (wie es in der ersten Zeile ");
        exWriter.write("bereits vorgegeben ist).");
        Main.newLine(exWriter);
        exWriter.write("\\end{enumerate}");
        Main.newLine(exWriter);
        exWriter.write(" Geben Sie zudem den vom Algorithmus bestimmten minimalen Spannbaum an.");
        Main.newLine(exWriter);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, exWriter);
        LaTeXUtils.printArrayStretch(1.5, exWriter);
        LaTeXUtils.printTable(
            exTable,
            Optional.empty(),
            LaTeXUtils.defaultColumnDefinition("2.0cm"),
            false,
            10,
            exWriter
        );
        LaTeXUtils.printArrayStretch(1, exWriter);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, exWriter);
        exWriter.write("Minimaler Spannbaum:");
        Main.newLine(exWriter);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, solWriter);
        LaTeXUtils.printArrayStretch(1.5, solWriter);
        LaTeXUtils.printTable(
            solTable,
            Optional.empty(),
            LaTeXUtils.defaultColumnDefinition("2.0cm"),
            false,
            10,
            solWriter
        );
        LaTeXUtils.printArrayStretch(1, solWriter);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, solWriter);
        // print the spanning tree
        solWriter.write("Hierbei gibt eine unterstrichene Zahl an in welcher Iteration (zugeh\\\"origer Zeilenkopf)");
        solWriter.write(" welcher Knoten (zugeh\\\"origer Spaltenkopf) durch \\texttt{extractMin(Q)} gew\\\"ahlt");
        solWriter.write(" wurde. Wir erhalten den folgenden minimalen Spannbaum:");
        Main.newLine(solWriter);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, solWriter);
        graph.printTikZ(solWriter, parent, false);
        Main.newLine(solWriter);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, solWriter);
    }

    public static void scc(final AlgorithmInput input) throws IOException {
        GraphAlgorithms.gridGraph(input, "find_sccs");
    }

    public static void sharir(final AlgorithmInput input) throws IOException {
        GraphAlgorithms.gridGraph(input, "sharir");
    }

    public static void topologicsort(final AlgorithmInput input) throws IOException {
        boolean fail;
        final GridGraph graph = new GridGraph();
        final int[][] sparseAdjacencyMatrix = GraphAlgorithms.parseOrGenerateGridGraph(input.options);
        do {
            try{
                fail = false;
                final boolean writeText = true; // TODO check whether this can be removed
                GraphAlgorithms.gridGraph(
                    graph,
                    sparseAdjacencyMatrix,
                    "topologicSort",
                    input.solutionWriter,
                    Algorithm.getOptionalSpaceWriter(input),
                    writeText
                );
            } catch (final IOException e) {
                //System.out.println("Caught cycle-exception.");
                fail = true;
                final Random gen = new Random();
                for (int i = 0; i < graph.numOfVerticesInSparseAdjacencyMatrix(); i++) {
                    for (int j = 0; j < graph.numOfNeighborsInSparseAdjacencyMatrix(); j++) {
                        if (graph.isNecessarySparseMatrixEntry(i,j) ) {
                            int entry = gen.nextInt(3);
                            entry = entry == 2 ? -1 : entry;
                            if (graph.isLegalEntryForSparseAdjacencyMatrix(entry)) {
                                sparseAdjacencyMatrix[i][j] = entry;
                            } else {
                                System.out.println("SHOULD NOT HAPPEN!");
                            }
                        } else {
                            sparseAdjacencyMatrix[i][j] = 0;
                        }
                    }
                }
            }
        } while(fail);
    }

    public static void warshall(final AlgorithmInput input) throws Exception {
        final Pair<Graph<String, Integer>, Vertex<String>> pair =
            GraphAlgorithms.parseOrGenerateGraph(input.options);
        GraphAlgorithms.floyd(pair.x, true, new StringVertexComparator(), input.exerciseWriter, input.solutionWriter);
    }

    /**
     * Adds the maximal flow along the specified path in the specified flow network and returns the set of edges used
     * to add the flow.
     * @param graph The flow network to add a flow to.
     * @param path The path along which the flow is to be be added.
     * @return The set of edges whose flow has been modified.
     * @throws IOException If some error occurs during output.
     */
    private static <V> Set<Pair<Vertex<V>, Edge<FlowPair, V>>> addFlow(
        final Graph<V, FlowPair> graph,
        final List<Vertex<V>> path
    ) throws IOException {
        final Integer min = GraphAlgorithms.computeMinEdge(graph, path);
        final Iterator<Vertex<V>> it = path.iterator();
        Vertex<V> from;
        Vertex<V> to = it.next();
        final Set<Pair<Vertex<V>, Edge<FlowPair, V>>> toHighlight =
            new LinkedHashSet<Pair<Vertex<V>, Edge<FlowPair, V>>>();
        while (it.hasNext()) {
            from = to;
            to = it.next();
            int flow = min;
            for (final Edge<FlowPair, V> edge : graph.getEdges(from, to)) {
                final int added = Math.min(flow, edge.x.y - edge.x.x);
                if (added > 0) {
                    flow -= added;
                    edge.x.x += added;
                    toHighlight.add(new Pair<Vertex<V>, Edge<FlowPair, V>>(from, edge));
                }
            }
            for (final Edge<FlowPair, V> edge : graph.getEdges(to, from)) {
                final int added = Math.min(flow, edge.x.x);
                if (added > 0) {
                    flow -= added;
                    edge.x.x -= added;
                    toHighlight.add(new Pair<Vertex<V>, Edge<FlowPair, V>>(to, edge));
                }
            }
            if (flow > 0) {
                throw new IllegalStateException("Could not add flow!");
            }
        }
        return toHighlight;
    }

    /**
     * Adds a vertex to the graph and updates the grid layout accordingly.
     * @param vertex The vertex to add.
     * @param graph The graph to add the vertex to.
     * @param pos The vertex's position in the grid layout.
     * @param grid The grid.
     * @param positions zThe grid layout positions.
     * @return The grid layout position of the added vertex.
     */
    private static VertexGridPosition addVertex(
        final Vertex<String> vertex,
        final Graph<String, ?> graph,
        final Pair<GridCoordinates, Boolean> pos,
        final Map<GridCoordinates, Vertex<String>> grid,
        final Map<Vertex<String>, VertexGridPosition> positions
    ) {
        graph.addVertex(vertex);
        grid.put(pos.x, vertex);
        final int x = pos.x.x;
        final int y = pos.x.y;
        final VertexGridPosition gridPos = new VertexGridPosition(pos.x.x, pos.x.y, pos.y);
        positions.put(vertex, gridPos);
        GridCoordinates nextPos = new GridCoordinates(x, y - 1);
        Vertex<String> nextVertex = grid.get(nextPos);
        if (nextVertex != null) {
            final VertexGridPosition nextGridPos = positions.get(nextVertex);
            gridPos.north = nextGridPos;
            nextGridPos.south = gridPos;
        }
        nextPos = new GridCoordinates(x + 1, y);
        nextVertex = grid.get(nextPos);
        if (nextVertex != null) {
            final VertexGridPosition nextGridPos = positions.get(nextVertex);
            gridPos.east = nextGridPos;
            nextGridPos.west = gridPos;
        }
        nextPos = new GridCoordinates(x, y + 1);
        nextVertex = grid.get(nextPos);
        if (nextVertex != null) {
            final VertexGridPosition nextGridPos = positions.get(nextVertex);
            gridPos.south = nextGridPos;
            nextGridPos.north = gridPos;
        }
        nextPos = new GridCoordinates(x - 1, y);
        nextVertex = grid.get(nextPos);
        if (nextVertex != null) {
            final VertexGridPosition nextGridPos = positions.get(nextVertex);
            gridPos.west = nextGridPos;
            nextGridPos.east = gridPos;
        }
        if (pos.y) {
            nextPos = new GridCoordinates(x + 1, y - 1);
            nextVertex = grid.get(nextPos);
            if (nextVertex != null) {
                final VertexGridPosition nextGridPos = positions.get(nextVertex);
                gridPos.northeast = nextGridPos;
                nextGridPos.southwest = gridPos;
            }
            nextPos = new GridCoordinates(x + 1, y + 1);
            nextVertex = grid.get(nextPos);
            if (nextVertex != null) {
                final VertexGridPosition nextGridPos = positions.get(nextVertex);
                gridPos.southeast = nextGridPos;
                nextGridPos.northwest = gridPos;
            }
            nextPos = new GridCoordinates(x - 1, y + 1);
            nextVertex = grid.get(nextPos);
            if (nextVertex != null) {
                final VertexGridPosition nextGridPos = positions.get(nextVertex);
                gridPos.southwest = nextGridPos;
                nextGridPos.northeast = gridPos;
            }
            nextPos = new GridCoordinates(x - 1, y - 1);
            nextVertex = grid.get(nextPos);
            if (nextVertex != null) {
                final VertexGridPosition nextGridPos = positions.get(nextVertex);
                gridPos.northwest = nextGridPos;
                nextGridPos.southeast = gridPos;
            }
        }
        return gridPos;
    }

    /**
     * @param graph A flow network.
     * @param path A path in the specified flow network from source to sink.
     * @return The minimal remaining edge value along the path (i.e., the maximal flow along the path).
     */
    private static <V> Integer computeMinEdge(final Graph<V, FlowPair> graph, final List<Vertex<V>> path) {
        Integer min = null;
        final Iterator<Vertex<V>> it = path.iterator();
        Vertex<V> from;
        Vertex<V> to = it.next();
        while (it.hasNext()) {
            from = to;
            to = it.next();
            int flow = 0;
            for (final Edge<FlowPair, V> edge : graph.getEdges(from, to)) {
                flow += edge.x.y - edge.x.x;
            }
            for (final Edge<FlowPair, V> edge : graph.getEdges(to, from)) {
                flow += edge.x.x;
            }
            if (min == null || min > flow) {
                min = flow;
            }
        }
        return min;
    }

    /**
     * Builds the residual graph from the specified flow network.
     * @param graph The flow network.
     * @return The residual graph built for the specified flow network.
     * @throws IOException If some error occurs during output.
     */
    private static <V> Graph<V, Integer> computeResidualGraph(final Graph<V, FlowPair> graph) throws IOException {
        final Graph<V, Integer> res = new Graph<V, Integer>();
        for (final Vertex<V> vertex : graph.getVertices()) {
            res.addVertex(vertex);
            final List<Edge<FlowPair, V>> list = graph.getAdjacencyList(vertex);
            if (list == null) {
                continue;
            }
            for (final Edge<FlowPair, V> edge : list) {
                final Vertex<V> target = edge.y;
                final Integer back = edge.x.x;
                if (back > 0) {
                    final Set<Edge<Integer, V>> backEdges = res.getEdges(target, vertex);
                    if (backEdges.isEmpty()) {
                        res.addEdge(target, back, vertex);
                    } else {
                        backEdges.iterator().next().x += back;
                    }
                }
                final Integer forth = edge.x.y - back;
                if (forth > 0) {
                    final Set<Edge<Integer, V>> forthEdges = res.getEdges(vertex, target);
                    if (forthEdges.isEmpty()) {
                        res.addEdge(vertex, forth, target);
                    } else {
                        forthEdges.iterator().next().x += forth;
                    }
                }
            }
        }
        res.setGrid(graph.getGrid());
        return res;
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

    private static <V, E> List<V> depthFirstSearch(
        final Graph<V, E> graph,
        final Vertex<V> vertex,
        final Comparator<Vertex<V>> comparator,
        final Set<Vertex<V>> used
    ) {
        if (used.contains(vertex)) {
            return Collections.emptyList();
        }
        used.add(vertex);
        final List<V> result = new LinkedList<V>();
        result.add(vertex.label.get());
        final List<Vertex<V>> nextVertices = new ArrayList<Vertex<V>>(graph.getAdjacentVertices(vertex));
        Collections.sort(nextVertices, comparator);
        for (final Vertex<V> nextVertex : nextVertices) {
            result.addAll(GraphAlgorithms.depthFirstSearch(graph, nextVertex, comparator, used));
        }
        return result;
    }

    /**
     * @param remainingVertices The number of vertices yet to be added.
     * @param minYPos The minimal y position in the current level.
     * @param maxYPos The maximal y position in the current level.
     * @param xPos The x position of the current level.
     * @return True if there are at least as many vertices to be added as minimally needed when starting the current level
     *         with the specified parameters (by reducing whenever possible). False otherwise.
     */
    private static boolean enoughVertices(
        final int remainingVertices,
        final int minYPos,
        final int maxYPos,
        final int xPos
    ) {
        int neededVertices = maxYPos - minYPos + 1;
        int itMinY = minYPos;
        int itMaxY = maxYPos;
        int itX = xPos;
        while (true) {
            if ((itMinY + itX) % 2 == 0) {
                itMinY++;
            }
            if ((itMaxY + itX) % 2 == 0) {
                itMaxY--;
            }
            if (itMinY >= itMaxY) {
                break;
            }
            itX++;
            neededVertices += itMaxY - itMinY + 1;
        }
        return neededVertices <= remainingVertices;
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
                solTable[0][rowIndex] = GraphAlgorithms.toRowHeading(vertex.label);
                exTable[0][rowIndex] = solTable[0][rowIndex];
                vertexIds.put(vertex, rowIndex);
                rowIndex++;
            }
        }
    }

    private static FlowNetworkInput<String, FlowPair> generateFlowNetwork(final Parameters options) {
        final Random gen = new Random();
        final int numOfVertices;
        if (options.containsKey(Flag.LENGTH)) {
            numOfVertices = Integer.parseInt(options.get(Flag.LENGTH));
        } else {
            numOfVertices = gen.nextInt(16) + 3;
        }
        final FlowNetworkInput<String, FlowPair> res = new FlowNetworkInput<String, FlowPair>();
        res.graph = GraphAlgorithms.createRandomFlowNetwork(gen, numOfVertices);
        res.source = res.graph.getVerticesWithLabel("s").iterator().next();
        res.sink = res.graph.getVerticesWithLabel("t").iterator().next();
        res.multiplier = 1.0;
        res.twocolumns = false;
        return res;
    }

    private static Pair<Graph<String, Integer>, Vertex<String>> generateGraph(final Parameters options) {
        final String alg = options.get(Flag.ALGORITHM);
        final Random gen = new Random();
        final int numOfVertices;
        if (options.containsKey(Flag.LENGTH)) {
            numOfVertices = Integer.parseInt(options.get(Flag.LENGTH));
        } else {
            numOfVertices = gen.nextInt(16) + 5;
        }
        final Graph<String, Integer> graph =
            GraphAlgorithms.createRandomGraph(
                gen,
                numOfVertices,
                GraphAlgorithms.UNDIRECTED_GRAPH_ALGORITHMS.contains(alg)
            );
        return new Pair<Graph<String, Integer>, Vertex<String>>(
            graph,
            GraphAlgorithms.parseOrGenerateStartVertex(graph, options)
        );
    }

    private static int[][] generateGridGraph(final Parameters options) {
        final GridGraph graph = new GridGraph();
        final int[][] sparseAdjacencyMatrix =
            new int[graph.numOfVerticesInSparseAdjacencyMatrix()][graph.numOfNeighborsInSparseAdjacencyMatrix()];
        final String errorMessage =
            new String(
                "You need to provide "
                + graph.numOfVerticesInSparseAdjacencyMatrix()
                + " lines and each line has to carry "
                + graph.numOfNeighborsInSparseAdjacencyMatrix()
                + " numbers being either 0, -1, 1 or 2, which are separated by ','!\n"
                + "Example:\n"
                + "x,0,0,x,x,x\nx,0,0,0,0,x\nx,0,0,0,0,x\nx,x,0,0,0,x\n0,2,0,1,1,0\n0,0,2,1,1,0\n0,0,0,0,0,0\n2,-1,0,x,x,x\n0,1,2,1,-1,1\n"
                + "0,0,0,0,0,0\n0,x,0,0,0,0\n1,2,0,0,0,1\n0,0,0,0,0,0\n0,0,0,0,0,0\n0,0,x,x,x,x\n0,0,x,x,0,0\n0,0,x,x,0,0\n0,x,x,x,0,0\n\n"
                + "where x can be anything and will not affect the resulting graph."
            );
        final Random gen = new Random();
        if (Algorithm.SHARIR.name.equals(options.get(Flag.ALGORITHM))) {
            final int[] numbers = new int[18];
            for (int i = 0; i < numbers.length; i++) {
                final int rndNumber = gen.nextInt(9);
                if (rndNumber < 3) {
                    numbers[i] = -1;
                } else if (rndNumber < 4) {
                    numbers[i] = 0;
                } else if (rndNumber < 7) {
                    numbers[i] = 1;
                } else {
                    numbers[i] = 2;
                }
            }
            sparseAdjacencyMatrix[4][1] = numbers[0];
            sparseAdjacencyMatrix[4][2] = numbers[1];

            sparseAdjacencyMatrix[5][2] = numbers[2];
            sparseAdjacencyMatrix[5][3] = numbers[3];
            sparseAdjacencyMatrix[5][4] = numbers[4];

            sparseAdjacencyMatrix[8][0] = numbers[5];
            sparseAdjacencyMatrix[8][1] = numbers[6];
            sparseAdjacencyMatrix[8][2] = numbers[7];
            sparseAdjacencyMatrix[8][4] = numbers[8];
            sparseAdjacencyMatrix[8][5] = numbers[9];

            sparseAdjacencyMatrix[9][0] = numbers[10];
            sparseAdjacencyMatrix[9][2] = numbers[11];
            sparseAdjacencyMatrix[9][3] = numbers[12];
            sparseAdjacencyMatrix[9][4] = numbers[13];

            sparseAdjacencyMatrix[12][0] = numbers[14];
            sparseAdjacencyMatrix[12][1] = numbers[15];
            sparseAdjacencyMatrix[12][4] = numbers[16];
            sparseAdjacencyMatrix[12][5] = numbers[17];
        } else {
            for (int i = 0; i < graph.numOfVerticesInSparseAdjacencyMatrix(); i++) {
                for (int j = 0; j < graph.numOfNeighborsInSparseAdjacencyMatrix(); j++) {
                    if (graph.isNecessarySparseMatrixEntry(i,j) ) {
                        final int rndNumber = gen.nextInt(18);
                        int entry = 0;
                        if (rndNumber >= 10 && rndNumber < 13) {
                            entry = -1;
                        } else if (rndNumber >= 13 && rndNumber < 16) {
                            entry = 1;
                        } else if (rndNumber >= 16) {
                            entry = 2;
                        }
                        if (graph.isLegalEntryForSparseAdjacencyMatrix(entry)) {
                            sparseAdjacencyMatrix[i][j] = entry;
                        } else {
                            System.out.println(errorMessage);
                            return null;
                        }
                    } else {
                        sparseAdjacencyMatrix[i][j] = 0;
                    }
                }
            }
        }
        return sparseAdjacencyMatrix;
    }

    private static void gridGraph(final AlgorithmInput input, final String name) throws IOException {
        GraphAlgorithms.gridGraph(
            new GridGraph(),
            GraphAlgorithms.parseOrGenerateGridGraph(input.options),
            name,
            input.solutionWriter,
            Algorithm.getOptionalSpaceWriter(input),
            true
        );
    }

    /**
     * Takes a sparse version of the adjacency matrix and constructs the according graph, which has not more than 35
     * vertices and each vertex has a degree not greater than 8. The tex-code to present the resulting graph is then
     * written to the file given by the fourth argument. The results from applying the given operation on this graph
     * are written to the file given by the last argument.
     *
     * @param graph An empty graph object.
     * @param sparseAdjacencyMatrix A sparse version of the adjacency matrix in order to construct the according graph
     *            (elements in a row are separated by "," and rows are separated by line breaks). Sparse means, that if
     *            we consider the vertices being ordered in a 5x7 grid, we only store every second vertex, starting
     *            from the first vertex in the first row and traversing row wise (i.e., then the 3. vertex in the first
     *            row, then the 5. vertex in the first row, then the 7. vertex in the first row, then the 2. vertex in
     *            the second row, ...). Furthermore, sparse means, that we only store adjacencies to not more than 6
     *            neighbors (according to the grid) of a vertex, more precisely, those six which are positioned north,
     *            east, south, southwest, west and northwest of the vertex. If the entry in the sparse version of the
     *            adjacency matrix is
     *                * 1, then there is an outgoing edge
     *                * -1, then there is an ingoing edge
     *                * 2, then there is an outgoing and ingoing edge
     *                * 0, no edge
     *            to the corresponding vertex.
     *
     *            Example: "x,2,2,x,x,x
     *                      x,2,2,2,2,x
     *                      x,2,2,2,2,x
     *                      x,x,2,2,2,x
     *                      2,2,2,2,2,2
     *                      2,2,2,2,2,2
     *                      2,2,2,2,2,2
     *                      2,2,2,x,x,x
     *                      2,2,2,2,2,2
     *                      2,2,2,2,2,2
     *                      2,x,2,2,2,2
     *                      2,2,2,2,2,2
     *                      2,2,2,2,2,2
     *                      2,2,2,2,2,2
     *                      2,2,x,x,x,x
     *                      2,2,x,x,2,2
     *                      2,2,x,x,2,2
     *                      2,x,x,x,2,2"
     *            This is the graph where every of the 35 vertices in the grid is connected to all it's neighbors. At
     *            the positions with "x" the values do not influence the graph as they belong to potential neighbors
     *            not being in the grid.
     * @param operation The name of the operation to apply to the graph.
     * @param writer The writer for the solution.
     * @param writerSpace The writer for the tree to start with (the one reached after the <code>construction</code>
     *                    operations). May be null if this tree should not be displayed separately.
     */
    private static void gridGraph (
        final GridGraph graph,
        final int[][] sparseAdjacencyMatrix,
        final String operation,
        final BufferedWriter writer,
        final Optional<BufferedWriter> optionalWriterSpace,
        final boolean withText
    ) throws IOException {
        graph.createGraph(sparseAdjacencyMatrix);
        switch(operation) {
        case "find_sccs":
            if (optionalWriterSpace.isPresent()) {
                final BufferedWriter writerSpace = optionalWriterSpace.get();
                writerSpace.write(
                    "Geben Sie alle \\emphasize{starken Zusammenhangskomponenten} im folgenden Graph an. F\\\"ur jede dieser "
                    + "starken Zusammenhangskomponenten reicht es die Menge der Knoten anzugeben, die darin auftreten."
                );
                Main.newLine(writerSpace);
                graph.printGraph(writerSpace, false);
            }
            graph.printSCCs(writer, false, false);
            Main.newLine(writer);
            break;
        case "sharir":
            if (optionalWriterSpace.isPresent()) {
                final BufferedWriter writerSpace = optionalWriterSpace.get();
                writerSpace.write(
                    "Wenden Sie \\emphasize{Sharir's Algorithmus} an (siehe Folien zur Vorlesung) um die starken"
                    + " Zusammenhangskomponenten des folgenden Graphen zu finden. Geben Sie das Array \\texttt{color}"
                    + " und den Stack \\texttt{S} nach jeder Schleifeniteration der ersten und zweiten Phase (also nach"
                    + " Zeile 17 und nach Zeile 22) an, falls \\texttt{DFS1} bzw. \\texttt{DFS2} ausgef\\\"uhrt wurde."
                    + " Geben Sie zudem das Array \\texttt{scc} nach jeder Schleifeniteration der zweiten Phase (also"
                    + " nach Zeile 22) an, falls \\texttt{DFS2} ausgef\\\"uhrt wurde. Nehmen Sie hierbei an, dass \\texttt{scc}"
                    + " initial mit Nullen gef\\\"ullt ist und der Knoten mit Schl\\\"ussel $i$ in der Adjazenzliste den $(i-1)$-ten Eintrag hat,"
                    + " also der Knoten mit Schl\\\"ussel $1$ vom Algorithmus als erstes ber\"ucksichtig wird usw."
                );
                Main.newLine(writerSpace);
                graph.printGraph(writerSpace, false);
            }
            graph.printSCCs(writer, false, true) ;
            Main.newLine(writer);
            break;
        case "topologicSort":
            graph.printTopologicalOrder(optionalWriterSpace, writer, false, withText);
            break;
        default:

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

    private static Set<String> initGraphAlgorithmsWithStartVertex() {
        final Set<String> res = new LinkedHashSet<String>();
        if (Algorithm.DIJKSTRA.enabled) {
            res.add(Algorithm.DIJKSTRA.name);
        }
        if (Algorithm.PRIM.enabled) {
            res.add(Algorithm.PRIM.name);
        }
        return res;
    }

    /**
     * @return The set of (in student mode only enabled) graph algorithms working on undirected graphs.
     */
    private static Set<String> initUndirectedGraphAlgorithms() {
        final Set<String> res = new LinkedHashSet<String>();
        if (Algorithm.PRIM.enabled) {
            res.add(Algorithm.PRIM.name);
        }
        return res;
    }

    private static FlowNetworkInput<String, FlowPair> parseFlowNetwork(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        final Graph<String, FlowPair> graph = Graph.create(reader, new StringLabelParser(), new FlowPairLabelParser());
        Vertex<String> source = null;
        Vertex<String> sink = null;
        double multiplier = 1.0;
        boolean twocolumns = false;
        if (options.containsKey(Flag.OPERATIONS)) {
            try (BufferedReader operationsReader = new BufferedReader(new FileReader(options.get(Flag.OPERATIONS)))) {
                Set<Vertex<String>> vertices = graph.getVerticesWithLabel(operationsReader.readLine().trim());
                if (!vertices.isEmpty()) {
                    source = vertices.iterator().next();
                }
                vertices = graph.getVerticesWithLabel(operationsReader.readLine().trim());
                if (!vertices.isEmpty()) {
                    sink = vertices.iterator().next();
                }
                final String mult = operationsReader.readLine();
                if (mult != null && !"".equals(mult.trim())) {
                    multiplier = Double.parseDouble(mult);
                    final String twocols = operationsReader.readLine();
                    if (twocols != null && !"".equals(twocols.trim())) {
                        twocolumns = Boolean.parseBoolean(twocols);
                    }
                }
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        final FlowNetworkInput<String, FlowPair> res = new FlowNetworkInput<String, FlowPair>();
        res.graph = graph;
        res.source = source;
        res.sink = sink;
        res.multiplier = multiplier;
        res.twocolumns = twocolumns;
        return res;
    }

    private static Pair<Graph<String, Integer>, Vertex<String>> parseGraph(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        final Graph<String, Integer> graph = Graph.create(reader, new StringLabelParser(), new IntLabelParser());
        return new Pair<Graph<String, Integer>, Vertex<String>>(
            graph,
            GraphAlgorithms.parseOrGenerateStartVertex(graph, options)
        );
    }

    private static int[][] parseGridGraph(final BufferedReader reader, final Parameters options) throws IOException {
        final GridGraph graph = new GridGraph();
        final int[][] sparseAdjacencyMatrix =
            new int[graph.numOfVerticesInSparseAdjacencyMatrix()][graph.numOfNeighborsInSparseAdjacencyMatrix()];
        final String errorMessage =
            new String(
                "You need to provide "
                + graph.numOfVerticesInSparseAdjacencyMatrix()
                + " lines and each line has to carry "
                + graph.numOfNeighborsInSparseAdjacencyMatrix()
                + " numbers being either 0, -1, 1 or 2, which are separated by ','!\n"
                + "Example:\n"
                + "x,0,0,x,x,x\nx,0,0,0,0,x\nx,0,0,0,0,x\nx,x,0,0,0,x\n0,2,0,1,1,0\n0,0,2,1,1,0\n0,0,0,0,0,0\n2,-1,0,x,x,x\n0,1,2,1,-1,1\n"
                + "0,0,0,0,0,0\n0,x,0,0,0,0\n1,2,0,0,0,1\n0,0,0,0,0,0\n0,0,0,0,0,0\n0,0,x,x,x,x\n0,0,x,x,0,0\n0,0,x,x,0,0\n0,x,x,x,0,0\n\n"
                + "where x can be anything and will not affect the resulting graph."
            );
        String line = null;
        int rowNum = 0;
        while ((line = reader.readLine()) != null) {
            final String[] vertices = line.split(",");
            if (vertices.length != graph.numOfNeighborsInSparseAdjacencyMatrix()) {
                System.out.println(errorMessage);
                return null;
            }
            for (int i = 0; i < graph.numOfNeighborsInSparseAdjacencyMatrix(); i++) {
                if (graph.isNecessarySparseMatrixEntry(rowNum,i) ) {
                    final int entry = Integer.parseInt(vertices[i].trim());
                    if (graph.isLegalEntryForSparseAdjacencyMatrix(entry)) {
                        sparseAdjacencyMatrix[rowNum][i] = entry;
                    } else {
                        System.out.println(errorMessage);
                        return null;
                    }
                } else {
                    sparseAdjacencyMatrix[rowNum][i] = 0;
                }
            }
            rowNum++;
        }
        return sparseAdjacencyMatrix;
    }

    private static FlowNetworkInput<String, FlowPair> parseOrGenerateFlowNetwork(final Parameters options)
    throws IOException {
        return new ParserAndGenerator<FlowNetworkInput<String, FlowPair>>(
            GraphAlgorithms::parseFlowNetwork,
            GraphAlgorithms::generateFlowNetwork
        ).getResult(options);
    }

    private static Pair<Graph<String, Integer>, Vertex<String>> parseOrGenerateGraph(final Parameters options)
    throws IOException {
        return new ParserAndGenerator<Pair<Graph<String, Integer>, Vertex<String>>>(
            GraphAlgorithms::parseGraph,
            GraphAlgorithms::generateGraph
        ).getResult(options);
    }

    private static int[][] parseOrGenerateGridGraph(final Parameters options) throws IOException {
        return new ParserAndGenerator<int[][]>(
            GraphAlgorithms::parseGridGraph,
            GraphAlgorithms::generateGridGraph
        ).getResult(options);
    }

    private static Vertex<String> parseOrGenerateStartVertex(
        final Graph<String, Integer> graph,
        final Parameters options
    ) {
        Vertex<String> vertex = null;
        if (options.containsKey(Flag.OPERATIONS)) {
            final String operations = options.get(Flag.OPERATIONS);
            try (
                BufferedReader operationsReader =
                    new BufferedReader(
                        options.containsKey(Flag.INPUT) ? new StringReader(operations) : new FileReader(operations)
                    )
            ) {
                final Set<Vertex<String>> vertices = graph.getVerticesWithLabel(operationsReader.readLine().trim());
                if (!vertices.isEmpty()) {
                    vertex = vertices.iterator().next();
                }
            } catch (final IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else if (
            !options.containsKey(Flag.SOURCE)
            && !options.containsKey(Flag.INPUT)
            && GraphAlgorithms.GRAPH_ALGORITHMS_WITH_START_VERTEX.contains(options.get(Flag.ALGORITHM))
        ) {
            final Set<Vertex<String>> vertices = graph.getVerticesWithLabel("A");
            if (!vertices.isEmpty()) {
                vertex = vertices.iterator().next();
            }
        }
        return vertex;
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
        exWriter.write("Betrachten Sie den folgenden Graphen:\\\\[2ex]");
        Main.newLine(exWriter);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, exWriter);
        graph.printTikZ(GraphPrintMode.ALL, 1, null, exWriter);
        Main.newLine(exWriter);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, exWriter);
        Main.newLine(exWriter);
        exWriter.write("F\\\"uhren Sie den \\emphasize{Dijkstra} Algorithmus auf diesem Graphen mit dem ");
        exWriter.write("\\emphasize{Startknoten ");
        exWriter.write(start.label.isEmpty() ? "" : start.label.get().toString());
        exWriter.write("} aus.");
        switch (mode) {
            case ALWAYS:
            case SOLUTION_SPACE:
                switch (Main.TEXT_VERSION) {
                    case ABRAHAM:
                        exWriter.write(" F\\\"ullen Sie dazu die nachfolgende Tabelle aus, indem Sie den Wert von ");
                        exWriter.write("\\texttt{v} und \\texttt{key} \\emphasize{nach jeder Iteration} der ");
                        exWriter.write("\\texttt{while}-Schleife eintragen:\\\\[2ex]");
                        break;
                    default:
                        exWriter.write(" F\\\"ullen Sie dazu die nachfolgende Tabelle aus:\\\\[2ex]");
                }
                break;
            case NEVER:
                // do nothing
        }
        Main.newLine(exWriter);
        switch (mode) {
            case SOLUTION_SPACE:
                LaTeXUtils.printSolutionSpaceBeginning(options, exWriter);
                // fall-through
            case ALWAYS:
                LaTeXUtils.printBeginning(LaTeXUtils.CENTER, exWriter);
                Main.newLine(exWriter);
                LaTeXUtils.printArrayStretch(1.5, exWriter);
                LaTeXUtils.printTable(
                    tables.exTable,
                    Optional.of(tables.exColor),
                    LaTeXUtils.defaultColumnDefinition("2cm"),
                    false,
                    10,
                    exWriter
                );
                LaTeXUtils.printArrayStretch(1.0, exWriter);
                LaTeXUtils.printEnd(LaTeXUtils.CENTER, exWriter);
                if (mode == PreprintMode.SOLUTION_SPACE) {
                    LaTeXUtils.printSolutionSpaceEnd(options, exWriter);
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
            LaTeXUtils.defaultColumnDefinition("2cm"),
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
            writer.write("\\hspace{2em}");
            Main.newLine(writer);
            return count + 1;
        }
        writer.write("\\\\[2ex]");
        Main.newLine(writer);
        LaTeXUtils.printTable(
            table,
            Optional.of(color),
            LaTeXUtils.defaultColumnDefinition("1cm"),
            transpose,
            breakAtColumn,
            writer
        );
        writer.write("\\hspace{2em}");
        Main.newLine(writer);
        return 1;
    }

    /**
     * @param gen Random number generator.
     * @param root A value which is probably close to the result.
     * @return A random non-negative edge value.
     */
    private static int randomEdgeValue(final Random gen, final int root) {
        int value = root;
        if (gen.nextInt(3) > 0) {
            while (gen.nextInt(3) > 0) {
                value++;
            }
        } else {
            while (value > 1 && gen.nextInt(4) == 0) {
                value--;
            }
        }
        return value;
    }

    /**
     * @param gen A random number generator.
     * @param max The maximum number of additional edges.
     * @return A random number between 0 and max, most likely to be max / 2.
     */
    private static int randomNumOfEdges(final Random gen, final int max) {
        int res = max / 2;
        if (gen.nextBoolean()) {
            while (res < max && gen.nextInt(3) == 0) {
                res++;
            }
        } else {
            while (res > 0 && gen.nextInt(3) == 0) {
                res--;
            }
        }
        return res;
    }

    /**
     * @param graph A residual graph for a flow network.
     * @param source The source of the flow network.
     * @param sink The sink of the flow network.
     * @return A path from source to sink with a remaining capacity greater than 0. If no such path exists, null is
     *         returned.
     */
    private static <V> List<Vertex<V>> selectAugmentingPath(
        final Graph<V, Integer> graph,
        final Vertex<V> source,
        final Vertex<V> sink
    ) {
        List<Vertex<V>> path = new ArrayList<Vertex<V>>();
        final Deque<List<Vertex<V>>> queue = new ArrayDeque<List<Vertex<V>>>();
        path.add(source);
        queue.add(path);
        final Set<Vertex<V>> visited = new LinkedHashSet<Vertex<V>>();
        visited.add(source);
        while (!queue.isEmpty()) {
            path = queue.poll();
            final List<Edge<Integer, V>> list = graph.getAdjacencyList(path.get(path.size() - 1));
            if (list == null) {
                continue;
            }
            for (final Edge<Integer, V> edge : list) {
                if (visited.contains(edge.y)) {
                    continue;
                }
                final List<Vertex<V>> newPath = new ArrayList<Vertex<V>>(path);
                newPath.add(edge.y);
                if (sink.equals(edge.y)) {
                    return newPath;
                }
                visited.add(edge.y);
                queue.add(newPath);
            }
        }
        return null;
    }

    private static <V> void setColumnHeadForDijkstra(
        final String[][] exTable,
        final String[][] solTable,
        final int columnIndex,
        final Vertex<V> currentVertex
    ) {
        exTable[columnIndex][0] = "";
        solTable[columnIndex][0] = GraphAlgorithms.toColumnHeading(currentVertex.label);
    }

    private static <V> String toColumnHeading(final Optional<V> label) {
        return GraphAlgorithms.toHeading(label);
    }

    /**
     * @param graph Some residual graph.
     * @param path A path through this residual graph.
     * @return The set of all edges used by the specified path in the specified graph.
     */
    private static <V> Set<Pair<Vertex<V>, Edge<Integer, V>>> toEdges(
        final Graph<V, Integer> graph,
        final List<Vertex<V>> path
    ) {
        if (path == null) {
            return null;
        }
        final Set<Pair<Vertex<V>, Edge<Integer, V>>> res =
            new LinkedHashSet<Pair<Vertex<V>, Edge<Integer, V>>>();
        final Iterator<Vertex<V>> it = path.iterator();
        Vertex<V> cur = it.next();
        while (it.hasNext()) {
            final Vertex<V> next = it.next();
            for (final Edge<Integer, V> edge : graph.getAdjacencyList(cur)) {
                if (edge.y.equals(next)) {
                    res.add(new Pair<Vertex<V>, Edge<Integer, V>>(cur, edge));
                    break;
                }
            }
            cur = next;
        }
        return res;
    }

    private static <V> String toHeading(final Optional<V> label) {
        return label.isEmpty() ? "" : String.format("\\textbf{%s}", label.get().toString());
    }

    private static <V> String toRowHeading(final Optional<V> label) {
        return Main.TEXT_VERSION == TextVersion.ABRAHAM ?
            (label.isEmpty() ? "" : String.format("\\texttt{key[}%s\\texttt{]}", label.get().toString())) :
                GraphAlgorithms.toHeading(label);
    }

    /**
     * @param num A non-negative number.
     * @return A String representation of this number. 0 is A, 1 is B, 26 is AA, 27 is AB, and so on.
     */
    private static String toStringLabel(final int num) {
        int val = num;
        int rem = val % 26;
        String res = "" + (char)(65 + rem);
        val -= rem;
        while (val > 0) {
            val = val / 26;
            rem = val % 26;
            res = ((char)(65 + rem)) + res;
            val -= rem;
        }
        return res;
    }

}
