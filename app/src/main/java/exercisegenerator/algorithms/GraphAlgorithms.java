package exercisegenerator.algorithms;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.util.*;

/**
 * Class offering methods for graph algorithms.
 */
public abstract class GraphAlgorithms {

    /**
     * The default value being probably close to the edge values.
     */
    private static final int DEFAULT_EDGE_ROOT = 3;

    /**
     * The default value being probably close to the edge values adjacent to source/sink nodes.
     */
    private static final int DEFAULT_SOURCE_SINK_ROOT = 7;

    /**
     * The phrase "each residual graph".
     */
    private static final String EACH_RESIDUAL_GRAPH = "\\emphasize{jedes Restnetzwerk (auch das initiale)}";

    /**
     * The set of those graph algorithms needing a start node.
     */
    private static final Set<String> GRAPH_ALGORITHMS_WITH_START_NODE =
        GraphAlgorithms.initGraphAlgorithmsWithStartNode();

    /**
     * The name of a residual graph.
     */
    private static final String RESIDUAL_GRAPH = "Restnetzwerk";

    /**
     * The set of those graph algorithms working on undirected graphs.
     */
    private static final Set<String> UNDIRECTED_GRAPH_ALGORITHMS = GraphAlgorithms.initUndirectedGraphAlgorithms();

    /**
     * @param gen A random number generator.
     * @param numOfNodes The number of nodes (excluding source and sink) in the returned flow network.
     * @return A random flow network with <code>numOfNodes</code> nodes labeled with Strings (each node has a unique
     *         label and the source is labeled with s and the sink is labeled with t) and edges labeled with pairs of
     *         Integers (the current flow and the capacity - the current flow will be set to 0).
     */
    public static Graph<String, FlowPair> createRandomFlowNetwork(final Random gen, final int numOfNodes) {
        if (numOfNodes < 0) {
            throw new IllegalArgumentException("Number of nodes must not be negative!");
        }
        final Graph<String, FlowPair> graph = new Graph<String, FlowPair>();
        final Map<Pair<Integer, Integer>, Node<String>> grid = new LinkedHashMap<Pair<Integer, Integer>, Node<String>>();
        final Node<String> source = new Node<String>(Optional.of("s"));
        final Map<Node<String>, NodeGridPosition> positions = new LinkedHashMap<Node<String>, NodeGridPosition>();
        final Pair<Integer, Integer> startPos = new Pair<Integer, Integer>(0, 0);
        GraphAlgorithms.addNode(
            source,
            graph,
            new Pair<Pair<Integer, Integer>, Boolean>(startPos, true),
            grid,
            positions
        );
        if (numOfNodes == 0) {
            final Node<String> sink = new Node<String>(Optional.of("t"));
            GraphAlgorithms.addNode(
                sink,
                graph,
                new Pair<Pair<Integer, Integer>, Boolean>(new Pair<Integer, Integer>(1, 0), false),
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
        int remainingNodes = numOfNodes;
        int letter = 0;
        // not all nodes can have diagonals: reduce only possible at every second step
        while (remainingNodes > 0) {
            prevMaxYPos = curMaxYPos;
            prevMinYPos = curMinYPos;
            final int prevXPos = xPos - 1;
            final boolean minDiagonal = (prevMinYPos + prevXPos) % 2 == 0;
            final boolean maxDiagonal = (prevMaxYPos + prevXPos) % 2 == 0;
            if (prevMaxYPos == prevMinYPos) {
                if (minDiagonal) {
                    if (remainingNodes > 2) {
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
                    } else if (remainingNodes == 2) {
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
                        if (GraphAlgorithms.enoughNodes(remainingNodes, prevMinYPos - 1, prevMaxYPos + 1, xPos)) {
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
                                GraphAlgorithms.enoughNodes(remainingNodes, prevMinYPos, prevMaxYPos + 1, xPos);
                            final boolean justExpandMin =
                                GraphAlgorithms.enoughNodes(remainingNodes, prevMinYPos - 1, prevMaxYPos, xPos);
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
                            } else if (GraphAlgorithms.enoughNodes(remainingNodes, prevMinYPos, prevMaxYPos, xPos)) {
                                options.add(keepMin + reduceMax);
                                options.add(reduceMin + keepMax);
                                options.add(keepMin + keepMax);
                                options.add(keepMin + keepMax);
                            } else {
                                if (GraphAlgorithms.enoughNodes(remainingNodes, prevMinYPos + 1, prevMaxYPos, xPos)) {
                                    options.add(reduceMin + keepMax);
                                }
                                if (GraphAlgorithms.enoughNodes(remainingNodes, prevMinYPos, prevMaxYPos - 1, xPos)) {
                                    options.add(keepMin + reduceMax);
                                }
                            }
                        }
                    } else {
                        options.add(reduceMin + keepMax);
                        if (GraphAlgorithms.enoughNodes(remainingNodes, prevMinYPos - 1, prevMaxYPos, xPos)) {
                            options.add(keepMin + keepMax);
                            options.add(keepMin + keepMax);
                            options.add(expandMin + keepMax);
                            options.add(expandMin + keepMax);
                            options.add(expandMin + keepMax);
                        } else if (GraphAlgorithms.enoughNodes(remainingNodes, prevMinYPos, prevMaxYPos, xPos)) {
                            options.add(keepMin + keepMax);
                            options.add(keepMin + keepMax);
                        }
                    }
                } else if (maxDiagonal) {
                    options.add(keepMin + reduceMax);
                    if (GraphAlgorithms.enoughNodes(remainingNodes, prevMinYPos, prevMaxYPos + 1, xPos)) {
                        options.add(keepMin + keepMax);
                        options.add(keepMin + keepMax);
                        options.add(keepMin + expandMax);
                        options.add(keepMin + expandMax);
                        options.add(keepMin + expandMax);
                    } else if (GraphAlgorithms.enoughNodes(remainingNodes, prevMinYPos, prevMaxYPos, xPos)) {
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
            final int nodesAtXPos = curMaxYPos - curMinYPos + 1;
            minYPos = Math.min(minYPos, curMinYPos);
            for (int yPos = curMinYPos; yPos <= curMaxYPos; yPos++) {
                // at least one edge from previous level
                final Node<String> node = new Node<String>(Optional.of(GraphAlgorithms.toStringLabel(letter++)));
                final boolean hasDiagonals = (xPos + yPos) % 2 == 0;
                final Pair<Integer, Integer> pos = new Pair<Integer, Integer>(xPos, yPos);
                GraphAlgorithms.addNode(
                    node,
                    graph,
                    new Pair<Pair<Integer, Integer>, Boolean>(pos, hasDiagonals),
                    grid,
                    positions
                );
                if (hasDiagonals) {
                    final List<Node<String>> existing = new ArrayList<Node<String>>();
                    Node<String> prevNode = grid.get(new Pair<Integer, Integer>(prevXPos, yPos - 1));
                    if (prevNode != null) {
                        existing.add(prevNode);
                    }
                    prevNode =  grid.get(new Pair<Integer, Integer>(prevXPos, yPos));
                    if (prevNode != null) {
                        existing.add(prevNode);
                    }
                    prevNode =  grid.get(new Pair<Integer, Integer>(prevXPos, yPos + 1));
                    if (prevNode != null) {
                        existing.add(prevNode);
                    }
                    final int index = gen.nextInt(existing.size());
                    prevNode = existing.remove(index);
                    graph.addEdge(
                        prevNode,
                        new FlowPair(
                            0,
                            GraphAlgorithms.randomEdgeValue(
                                gen,
                                prevXPos == 0 ?
                                    GraphAlgorithms.DEFAULT_SOURCE_SINK_ROOT :
                                        GraphAlgorithms.DEFAULT_EDGE_ROOT
                            )
                        ),
                        node
                    );
                    for (final Node<String> otherNode : existing) {
                        if (gen.nextBoolean()) {
                            graph.addEdge(
                                otherNode,
                                new FlowPair(
                                    0,
                                    GraphAlgorithms.randomEdgeValue(gen, GraphAlgorithms.DEFAULT_EDGE_ROOT)
                                ),
                                node
                            );
                        }
                    }
                } else {
                    graph.addEdge(
                        grid.get(new Pair<Integer, Integer>(prevXPos, yPos)),
                        new FlowPair(
                            0,
                            GraphAlgorithms.randomEdgeValue(
                                gen,
                                prevXPos == 0 ?
                                    GraphAlgorithms.DEFAULT_SOURCE_SINK_ROOT :
                                        GraphAlgorithms.DEFAULT_EDGE_ROOT
                            )
                        ),
                        node
                    );
                }
                if (yPos > curMinYPos) {
                    // north-south edges
                    final Node<String> north = grid.get(new Pair<Integer, Integer>(xPos, yPos - 1));
                    if (gen.nextBoolean()) {
                        graph.addEdge(
                            north,
                            new FlowPair(0, GraphAlgorithms.randomEdgeValue(gen, GraphAlgorithms.DEFAULT_EDGE_ROOT)),
                            node
                        );
                    }
                    if (gen.nextBoolean()) {
                        graph.addEdge(
                            node,
                            new FlowPair(0, GraphAlgorithms.randomEdgeValue(gen, GraphAlgorithms.DEFAULT_EDGE_ROOT)),
                            north
                        );
                    }
                }
            }
            // at least one edge for each node on previous level to current level
            outer: for (int prevYPos = prevMinYPos; prevYPos <= prevMaxYPos; prevYPos++) {
                final Node<String> prevNode = grid.get(new Pair<Integer, Integer>(prevXPos, prevYPos));
                final boolean prevDiagonals = (prevXPos + prevYPos) % 2 == 0;
                if (prevDiagonals) {
                    final List<Node<String>> existing = new ArrayList<Node<String>>();
                    Node<String> nextNode = grid.get(new Pair<Integer, Integer>(xPos, prevYPos - 1));
                    if (nextNode != null) {
                        existing.add(nextNode);
                    }
                    nextNode =  grid.get(new Pair<Integer, Integer>(xPos, prevYPos));
                    if (nextNode != null) {
                        existing.add(nextNode);
                    }
                    nextNode =  grid.get(new Pair<Integer, Integer>(xPos, prevYPos + 1));
                    if (nextNode != null) {
                        existing.add(nextNode);
                    }
                    for (final Node<String> otherNode : existing) {
                        if (!graph.getEdges(prevNode, otherNode).isEmpty()) {
                            continue outer;
                        }
                    }
                    final int index = gen.nextInt(existing.size());
                    nextNode = existing.remove(index);
                    graph.addEdge(
                        prevNode,
                        new FlowPair(0, GraphAlgorithms.randomEdgeValue(gen, GraphAlgorithms.DEFAULT_EDGE_ROOT)),
                        nextNode
                    );
                } else {
                    final Node<String> nextNode = grid.get(new Pair<Integer, Integer>(xPos, prevYPos));
                    if (graph.getEdges(prevNode, nextNode).isEmpty()) {
                        graph.addEdge(
                            prevNode,
                            new FlowPair(0, GraphAlgorithms.randomEdgeValue(gen, GraphAlgorithms.DEFAULT_EDGE_ROOT)),
                            nextNode
                        );
                    }
                }
            }
            remainingNodes -= nodesAtXPos;
            xPos++;
        }
        final Node<String> sink = new Node<String>(Optional.of("t"));
        int yPos = curMinYPos + ((curMaxYPos - curMinYPos) / 2);
        if ((xPos + yPos) % 2 != 0) {
            yPos++;
        }
        GraphAlgorithms.addNode(
            sink,
            graph,
            new Pair<Pair<Integer, Integer>, Boolean>(new Pair<Integer, Integer>(xPos, yPos), true),
            grid,
            positions
        );
        final List<Node<String>> existing = new ArrayList<Node<String>>();
        Node<String> prevNode = grid.get(new Pair<Integer, Integer>(xPos - 1, yPos - 1));
        if (prevNode != null) {
            existing.add(prevNode);
        }
        prevNode =  grid.get(new Pair<Integer, Integer>(xPos - 1, yPos));
        if (prevNode != null) {
            existing.add(prevNode);
        }
        prevNode =  grid.get(new Pair<Integer, Integer>(xPos - 1, yPos + 1));
        if (prevNode != null) {
            existing.add(prevNode);
        }
        for (final Node<String> otherNode : existing) {
            graph.addEdge(
                otherNode,
                new FlowPair(0, GraphAlgorithms.randomEdgeValue(gen, GraphAlgorithms.DEFAULT_SOURCE_SINK_ROOT)),
                sink
            );
        }
        // adjust grid (non-negative coordinates, sum of coordinates even -> has diagonals
        int xAdd = 0;
        if (minYPos % 2 != 0) {
            xAdd++;
        }
        final Map<Pair<Integer, Integer>, Node<String>> newGrid = new LinkedHashMap<Pair<Integer, Integer>, Node<String>>();
        for (final Entry<Pair<Integer, Integer>, Node<String>> entry : grid.entrySet()) {
            final Pair<Integer, Integer> key = entry.getKey();
            newGrid.put(new Pair<Integer, Integer>(key.x + xAdd, key.y - minYPos), entry.getValue());
        }
        graph.setGrid(newGrid);
        return graph;
    }

    /**
     * @param gen A random number generator.
     * @param numOfNodes The number of nodes in the returned graph.
     * @param undirected Should the graph be undirected?
     * @return A random graph with <code>numOfNodes</code> nodes labeled with Strings (each node has a unique label
     *         and there is a node with label A) and edges labeled with Integers.
     */
    public static Graph<String, Integer> createRandomGraph(
        final Random gen,
        final int numOfNodes,
        final boolean undirected
    ) {
        if (numOfNodes < 0) {
            throw new IllegalArgumentException("Number of nodes must not be negative!");
        }
        final Graph<String, Integer> graph = new Graph<String, Integer>();
        final Map<Pair<Integer, Integer>, Node<String>> grid =
            new LinkedHashMap<Pair<Integer, Integer>, Node<String>>();
        if (numOfNodes == 0) {
            graph.setGrid(grid);
            return graph;
        }
        final Map<Node<String>, NodeGridPosition> positions = new LinkedHashMap<Node<String>, NodeGridPosition>();
        final Node<String> start = new Node<String>(Optional.of("A"));
        final Pair<Integer, Integer> startPos = new Pair<Integer, Integer>(0, 0);
        final boolean startDiagonal = gen.nextBoolean();
        GraphAlgorithms.addNode(
            start,
            graph,
            new Pair<Pair<Integer, Integer>, Boolean>(startPos, startDiagonal),
            grid,
            positions
        );
        final List<Node<String>> nodesWithFreeNeighbors = new ArrayList<Node<String>>();
        nodesWithFreeNeighbors.add(start);
        for (int letter = 1; letter < numOfNodes; letter++) {
            final Node<String> nextNode = nodesWithFreeNeighbors.get(gen.nextInt(nodesWithFreeNeighbors.size()));
            final NodeGridPosition nextPos = positions.get(nextNode);
            final Pair<Pair<Integer, Integer>, Boolean> toAddPos = nextPos.randomFreePosition(gen);
            final Node<String> toAddNode = new Node<String>(Optional.of(GraphAlgorithms.toStringLabel(letter)));
            final NodeGridPosition gridPos = GraphAlgorithms.addNode(toAddNode, graph, toAddPos, grid, positions);
            final int value = GraphAlgorithms.randomEdgeValue(gen, GraphAlgorithms.DEFAULT_EDGE_ROOT);
            graph.addEdge(nextNode, value, toAddNode);
            if (undirected) {
                graph.addEdge(toAddNode, value, nextNode);
            }
            final List<Pair<Pair<Integer, Integer>, Boolean>> existing = gridPos.getExistingPositions();
            final List<Pair<Node<String>, Node<String>>> freeNodePairs = new ArrayList<Pair<Node<String>, Node<String>>>();
            for (final Pair<Pair<Integer, Integer>, Boolean> other : existing) {
                final Node<String> otherNode = grid.get(other.x);
                if (otherNode.equals(nextNode)) {
                    if (!undirected) {
                        freeNodePairs.add(new Pair<Node<String>, Node<String>>(toAddNode, otherNode));
                    }
                } else {
                    freeNodePairs.add(new Pair<Node<String>, Node<String>>(toAddNode, otherNode));
                    if (!undirected) {
                        freeNodePairs.add(new Pair<Node<String>, Node<String>>(otherNode, toAddNode));
                    }
                }
            }
            for (int numEdges = GraphAlgorithms.randomNumOfEdges(gen, freeNodePairs.size()); numEdges > 0; numEdges--) {
                final int pairIndex = gen.nextInt(freeNodePairs.size());
                final Pair<Node<String>, Node<String>> pair = freeNodePairs.remove(pairIndex);
                final int nextValue = GraphAlgorithms.randomEdgeValue(gen, GraphAlgorithms.DEFAULT_EDGE_ROOT);
                graph.addEdge(pair.x, nextValue, pair.y);
                if (undirected) {
                    graph.addEdge(pair.y, nextValue, pair.x);
                }
            }
            for (final Pair<Pair<Integer, Integer>, Boolean> neighborPos : existing) {
                final Node<String> neighborNode = grid.get(neighborPos.x);
                if (!positions.get(neighborNode).hasFreePosition()) {
                    nodesWithFreeNeighbors.remove(neighborNode);
                }
            }
            if (gridPos.hasFreePosition()) {
                nodesWithFreeNeighbors.add(toAddNode);
            }
        }
        // adjust grid (non-negative coordinates, sum of coordinates even -> has diagonals
        int minX = 0;
        int minY = 0;
        for (final Pair<Integer, Integer> pair : grid.keySet()) {
            minX = Math.min(minX, pair.x);
            minY = Math.min(minY, pair.y);
        }
        if (
            (startDiagonal && (startPos.x - minX + startPos.y - minY) % 2 == 1)
            || (!startDiagonal && (startPos.x - minX + startPos.y - minY) % 2 == 0)
        ) {
            minX--;
        }
        final Map<Pair<Integer, Integer>, Node<String>> newGrid = new LinkedHashMap<Pair<Integer, Integer>, Node<String>>();
        for (final Entry<Pair<Integer, Integer>, Node<String>> entry : grid.entrySet()) {
            final Pair<Integer, Integer> key = entry.getKey();
            newGrid.put(new Pair<Integer, Integer>(key.x - minX, key.y - minY), entry.getValue());
        }
        graph.setGrid(newGrid);
        return graph;
    }

    public static void dijkstra(final AlgorithmInput input) throws Exception {
        final Pair<Graph<String, Integer>, Node<String>> pair = GraphAlgorithms.parseOrGenerateGraph(input.options);
        GraphAlgorithms.printDijkstra(
            pair.x,
            pair.y,
            GraphAlgorithms.dijkstra(pair.x, pair.y, new StringNodeComparator()),
            Algorithm.parsePreprintMode(input.options),
            input.options,
            input.exerciseWriter,
            input.solutionWriter
        );
    }

    public static <N> DijkstraTables dijkstra(
        final Graph<N, Integer> graph,
        final Node<N> start,
        final Comparator<Node<N>> comp
    ) {
        final List<Node<N>> nodes = new ArrayList<Node<N>>(graph.getNodes());
        if (comp != null) {
            Collections.sort(nodes, comp);
        }
        final int size = nodes.size();
        final String[][] exTable;
        final String[][] solTable;
        final String[][] exColor;
        final String[][] solColor;
        final Integer[] distances = new Integer[size];
        final Map<Node<N>, Integer> nodeIds = new LinkedHashMap<Node<N>, Integer>();
        final Set<Integer> used = new LinkedHashSet<Integer>();
        exTable = new String[size][size];
        solTable = new String[size][size];
        exColor = new String[size][size];
        solColor = new String[size][size];
        GraphAlgorithms.fillRowHeadingsAndIdsForDijkstra(exTable, solTable, nodes, nodeIds, start);
        distances[0] = 0;
        int currentNodeId = 0;
        for (int columnIndex = 1; columnIndex < size; columnIndex++) {
            used.add(currentNodeId);
            final Node<N> currentNode = nodes.get(currentNodeId);
            GraphAlgorithms.setColumnHeadForDijkstra(exTable, solTable, columnIndex, currentNode);
            GraphAlgorithms.improveDistancesForNode(currentNode, currentNodeId, graph, nodeIds, distances);
            final Optional<Integer> nodeIndexWithMinimumDistance =
                GraphAlgorithms.computeNodeIndexWithMinimumDistance(
                    columnIndex,
                    size,
                    distances,
                    used,
                    exTable,
                    solTable
                );
            if (nodeIndexWithMinimumDistance.isEmpty()) {
                // no shortening possible
                break;
            }
            currentNodeId = nodeIndexWithMinimumDistance.get();
            solColor[columnIndex][currentNodeId] = "black!20";
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
        final Pair<Graph<String, Integer>, Node<String>> pair = GraphAlgorithms.parseOrGenerateGraph(input.options);
        GraphAlgorithms.floyd(pair.x, false, new StringNodeComparator(), input.exerciseWriter, input.solutionWriter);
    }

    /**
     * Prints exercise and solution for the Floyd Algorithm.
     * @param graph The graph.
     * @param warshall Flag indicating whether the Floyd-Warshall or just the Floyd algorithm should be performed.
     * @param start The start node.
     * @param comp A comparator for sorting the nodes in the table (may be null - then no sorting is applied).
     * @param exWriter The writer to send the exercise output to.
     * @param solWriter The writer to send the solution output to.
     * @throws IOException If some error occurs during output.
     */
    public static <N> void floyd(
        final Graph<N, Integer> graph,
        final boolean warshall,
        final Comparator<Node<N>> comp,
        final BufferedWriter exWriter,
        final BufferedWriter solWriter
    ) throws IOException {
        final int tableCount = 1; // TODO had a choice for 2 when not in student mode
        final int tableMaxWidth = 10; // TODO rename, current name does not reflect usage; had a choice for 0 when not in student mode
        final List<Node<N>> nodes = new ArrayList<Node<N>>(graph.getNodes());
        final int size = nodes.size();
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
        final Map<Node<N>, Integer> ids = new LinkedHashMap<Node<N>, Integer>();
        for (int current = 0 ; current < size; ++current) {
            final Node<N> currentNode = nodes.get(current);
            ids.put(currentNode, current);
        }
        firstExercise[0][0] = "";
        otherExercise[0][0] = "";
        currentSolution[0][0] = "";
        // initialize weights
        for (int current = 0 ; current < size; ++current) {
            final Node<N> currentNode = nodes.get(current);
            // set labels
            final String currentLabel = currentNode.label.isEmpty() ? "" : currentNode.label.get().toString();
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
            for (final Pair<Integer, Node<N>> edge : graph.getAdjacencyList(currentNode)) {
                weights[current][ids.get(edge.y)] = edge.x;
                if (!warshall) {
                    firstExercise[current+1][ids.get(edge.y)+1] = edge.x.toString();
                    currentSolution[current+1][ids.get(edge.y)+1] = edge.x.toString();
                } else {
                    firstExercise[current+1][ids.get(edge.y)+1] = "true";
                    currentSolution[current+1][ids.get(edge.y)+1] = "true";
//                    System.out.println(
//                        "Add: "
//                        + currentNode.getLabel()
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
            final Node<N> currentNode = nodes.get(current);
            // set labels
            final String currentLabel = currentNode.label.isEmpty() ? "" : currentNode.label.get().toString();
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
                    } else {
                        if (!warshall) {
                            currentSolution[i+1][j+1] = "" + weights[i][j];
                        } else {
                            currentSolution[i+1][j+1] = "true";
                        }
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
                final Node<N> currentNode = nodes.get(current);
                // set labels
                final String currentLabel = currentNode.label.isEmpty() ? "" : currentNode.label.get().toString();
                currentSolution[0][current+1] = currentLabel;
                currentSolution[current+1][0] = currentLabel;
            }
            changed = new boolean[size][size];
        }
        // create output
        exWriter.write("Betrachten Sie den folgenden Graphen:");
        Main.newLine(exWriter);
        TikZUtils.printBeginning(TikZUtils.CENTER, exWriter);
        if (warshall) {
            graph.printTikZ(GraphPrintMode.NO_EDGE_LABELS, 1, null, exWriter);
        } else {
            graph.printTikZ(GraphPrintMode.ALL, 1, null, exWriter);
        }
        Main.newLine(exWriter);
        TikZUtils.printEnd(TikZUtils.CENTER, exWriter);
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
        TikZUtils.printArrayStretch(1.5, exWriter);
        TikZUtils.printArrayStretch(1.5, solWriter);
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
        TikZUtils.printArrayStretch(1.0, exWriter);
        TikZUtils.printArrayStretch(1.0, solWriter);
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
     * @param source The source node.
     * @param sink The sink node.
     * @param multiplier Multiplier for node distances.
     * @param twocolumns True if residual graphs and flow networks should be displayed in two columns.
     * @param mode Preprint mode.
     * @param exWriter The writer to send the exercise output to.
     * @param solWriter The writer to send the solution output to.
     * @throws IOException If some error occurs during output.
     */
    public static <N> void fordFulkerson(
        final Graph<N, FlowPair> graph,
        final Node<N> source,
        final Node<N> sink,
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
        TikZUtils.printBeginning(TikZUtils.CENTER, exWriter);
        graph.printTikZ(GraphPrintMode.ALL, multiplier, null, exWriter);
        TikZUtils.printEnd(TikZUtils.CENTER, exWriter);
        Main.newLine(exWriter);
        exWriter.write("Berechnen Sie den maximalen Fluss in diesem Netzwerk mithilfe der");
        exWriter.write(" \\emphasize{Ford-Fulkerson Methode}. Geben Sie dazu ");
        exWriter.write(GraphAlgorithms.EACH_RESIDUAL_GRAPH);
        exWriter.write(" sowie \\emphasize{nach jeder Augmentierung} den aktuellen Zustand des Flussnetzwerks an. ");
        exWriter.write("Geben Sie au\\ss{}erdem den \\emphasize{Wert des maximalen Flusses} an.");
        switch (mode) {
            case ALWAYS:
            case SOLUTION_SPACE:
                exWriter.write(" Die vorgegebene Anzahl an L\\\"osungsschritten muss nicht mit der ben\\\"otigten Anzahl ");
                exWriter.write("solcher Schritte \\\"ubereinstimmen.");
                break;
            case NEVER:
                // do nothing
        }
        Main.newLine(exWriter);
        int step = 0;
        TikZUtils.printSamePageBeginning(step++, twocolumns ? TikZUtils.TWO_COL_WIDTH : TikZUtils.COL_WIDTH, solWriter);
        solWriter.write("Initiales Flussnetzwerk:\\\\[2ex]");
        graph.printTikZ(GraphPrintMode.ALL, multiplier, null, solWriter);
        TikZUtils.printSamePageEnd(solWriter);
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
            final Graph<N, Integer> residualGraph = GraphAlgorithms.computeResidualGraph(graph);
            final List<Node<N>> path = GraphAlgorithms.selectAugmentingPath(residualGraph, source, sink);
            switch (mode) {
                case ALWAYS:
                case SOLUTION_SPACE:
                    TikZUtils.printSamePageBeginning(
                        step,
                        twocolumns ? TikZUtils.TWO_COL_WIDTH : TikZUtils.COL_WIDTH,
                        exWriter
                    );
                    exWriter.write(GraphAlgorithms.RESIDUAL_GRAPH);
                    exWriter.write(":\\\\[2ex]");
                    Main.newLine(exWriter);
                    break;
                case NEVER:
                    // do nothing
            }
            TikZUtils.printSamePageBeginning(
                step++,
                twocolumns ? TikZUtils.TWO_COL_WIDTH : TikZUtils.COL_WIDTH,
                solWriter
            );
            solWriter.write(GraphAlgorithms.RESIDUAL_GRAPH);
            solWriter.write(":\\\\[2ex]");
            Main.newLine(solWriter);
            final Set<Pair<Node<N>, Pair<Integer, Node<N>>>> toHighlightResidual;
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
                    TikZUtils.printSamePageEnd(exWriter);
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
            TikZUtils.printSamePageEnd(solWriter);
            if (twocolumns) {
                solWriter.write(" & ");
            } else {
                Main.newLine(solWriter);
            }
            if (path == null) {
                break;
            }
            final Set<Pair<Node<N>, Pair<FlowPair, Node<N>>>> toHighlightFlow = GraphAlgorithms.addFlow(graph, path);
            switch (mode) {
                case ALWAYS:
                case SOLUTION_SPACE:
                    TikZUtils.printSamePageBeginning(
                        step,
                        twocolumns ? TikZUtils.TWO_COL_WIDTH : TikZUtils.COL_WIDTH,
                        exWriter
                    );
                    exWriter.write("N\\\"achstes Flussnetzwerk mit aktuellem Fluss:\\\\[2ex]");
                    Main.newLine(exWriter);
                    graph.printTikZ(GraphPrintMode.NO_EDGE_LABELS, multiplier, null, exWriter);
                    TikZUtils.printSamePageEnd(exWriter);
                    if (twocolumns) {
                        exWriter.write("\\\\");
                    }
                    Main.newLine(exWriter);
                    break;
                case NEVER:
                    // do nothing
            }
            TikZUtils.printSamePageBeginning(
                step++,
                twocolumns ? TikZUtils.TWO_COL_WIDTH : TikZUtils.COL_WIDTH,
                solWriter
            );
            solWriter.write("N\\\"achstes Flussnetzwerk mit aktuellem Fluss:\\\\[2ex]");
            Main.newLine(solWriter);
            graph.printTikZ(GraphPrintMode.ALL, multiplier, toHighlightFlow, solWriter);
            TikZUtils.printSamePageEnd(solWriter);
            if (twocolumns) {
                solWriter.write("\\\\");
            }
            Main.newLine(solWriter);
        }
        int flow = 0;
        final List<Pair<FlowPair, Node<N>>> list = graph.getAdjacencyList(source);
        if (list != null) {
            for (final Pair<FlowPair, Node<N>> edge : list) {
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

    public static void prim(final AlgorithmInput input) throws Exception {
        final Pair<Graph<String, Integer>, Node<String>> pair = GraphAlgorithms.parseOrGenerateGraph(input.options);
        GraphAlgorithms.prim(pair.x, pair.y, new StringNodeComparator(), input.exerciseWriter, input.solutionWriter);
    }

    /**
     * Prints exercise and solution for Prim's algorithm.
     * @param graph The graph.
     * @param start The start node.
     * @param comp A comparator for sorting the nodes in the table (may be null - then no sorting is applied).
     * @param exWriter The writer to send the exercise output to.
     * @param solWriter The writer to send the solution output to.
     * @throws IOException If some error occurs during output.
     */
    public static <N> void prim(
        final Graph<N, Integer> graph,
        final Node<N> start,
        final Comparator<Node<N>> comp,
        final BufferedWriter exWriter,
        final BufferedWriter solWriter
    ) throws IOException {
        final List<Node<N>> nodes = new ArrayList<Node<N>>(graph.getNodes());
        final String[][] exTable = new String[nodes.size()+1][nodes.size()+1];
        final String[][] solTable = new String[nodes.size()+1][nodes.size()+1];
        exTable[0][0] = "\\#Iteration";
        solTable[0][0] = "\\#Iteration";
        final Map<Node<N>, Integer> key = new LinkedHashMap<Node<N>, Integer>();
        final Map<Node<N>, List<Pair<Integer, Node<N>>>> parent =
            new LinkedHashMap<Node<N>, List<Pair<Integer, Node<N>>>>();
        int i = 1;
        for (final Node<N> node : nodes) {
            key.put(node, null);
            final String label = node.label.isEmpty() ? "" : node.label.get().toString();
            exTable[i][0] = label;
            solTable[i][0] = label;
            i++;
        }
        final List<Node<N>> q = new ArrayList<Node<N>>(graph.getNodes());
        key.put(start, 0);
        int iteration = 1;
        // actual algorithm
        while (!q.isEmpty()) {
            // extract the minimum from q
            Node<N> minNode = null;
            for (final Node<N> node : q) {
                if (
                    minNode == null
                    || key.get(minNode) == null
                    || (key.get(node) != null && key.get(minNode).intValue() > key.get(node).intValue())
                ) {
                    minNode = node;
                }
            }
            // write solution
            exTable[0][iteration] = "" + iteration;
            solTable[0][iteration] = "" + iteration;
            i = 1;
            for (final Node<N> node : nodes) {
                if (q.contains(node)) {
                    if (key.get(node) == null) {
                        solTable[i][iteration] = "$\\infty$";
                    } else {
                        if (minNode == node) {
                            solTable[i][iteration] = "\\underline{" + key.get(node) + "}";
                        } else {
                            solTable[i][iteration] = "" + key.get(node);
                        }
                    }
                } else {
                    solTable[i][iteration] = "";
                }
                i++;
            }
            // update the minimums successors remaining in q
            for (final Pair<Integer, Node<N>> edge : graph.getAdjacencyList(minNode)) {
                if (q.contains(edge.y) && (key.get(edge.y) == null || edge.x < key.get(edge.y))) {
                    final List<Pair<Integer, Node<N>>> adList = new ArrayList<Pair<Integer, Node<N>>>();
                    adList.add(new Pair<Integer, Node<N>>(edge.x, minNode));
                    parent.put(edge.y, adList);
                    key.put(edge.y, edge.x);
                }
            }
            q.remove(minNode);
            iteration++;
        }
        // create output
        for (int j = 1; j < exTable.length; j++) {
            exTable[j][1] = solTable[j][1];
        }
        exWriter.write("F\\\"uhren Sie Prim's Algorithmus auf dem folgenden Graphen aus.");
        TikZUtils.printBeginning(TikZUtils.CENTER, exWriter);
        graph.printTikZ(exWriter, null, false);
        TikZUtils.printEnd(TikZUtils.CENTER, exWriter);
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
        TikZUtils.printBeginning(TikZUtils.CENTER, exWriter);
        TikZUtils.printArrayStretch(1.5, exWriter);
        TikZUtils.printTable(exTable, null, "2.0cm", exWriter, false, 10);
        TikZUtils.printArrayStretch(1, exWriter);
        TikZUtils.printEnd(TikZUtils.CENTER, exWriter);
        exWriter.write("Minimaler Spannbaum:");
        Main.newLine(exWriter);
        TikZUtils.printBeginning(TikZUtils.CENTER, solWriter);
        TikZUtils.printArrayStretch(1.5, solWriter);
        TikZUtils.printTable(solTable, null, "2.0cm", solWriter, false, 10);
        TikZUtils.printArrayStretch(1, solWriter);
        TikZUtils.printEnd(TikZUtils.CENTER, solWriter);
        // print the spanning tree
        solWriter.write("Hierbei gibt eine unterstrichene Zahl an in welcher Iteration (zugeh\\\"origer Zeilenkopf)");
        solWriter.write(" welcher Knoten (zugeh\\\"origer Spaltenkopf) durch \\texttt{extractMin(Q)} gew\\\"ahlt");
        solWriter.write(" wurde. Wir erhalten den folgenden minimalen Spannbaum:");
        Main.newLine(solWriter);
        TikZUtils.printBeginning(TikZUtils.CENTER, solWriter);
        graph.printTikZ(solWriter, parent, false);
        Main.newLine(solWriter);
        TikZUtils.printEnd(TikZUtils.CENTER, solWriter);
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
                for (int i = 0; i < graph.numOfNodesInSparseAdjacencyMatrix(); i++) {
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
        final Pair<Graph<String, Integer>, Node<String>> pair = GraphAlgorithms.parseOrGenerateGraph(input.options);
        GraphAlgorithms.floyd(pair.x, true, new StringNodeComparator(), input.exerciseWriter, input.solutionWriter);
    }

    /**
     * Adds the maximal flow along the specified path in the specified flow network and returns the set of edges used
     * to add the flow.
     * @param graph The flow network to add a flow to.
     * @param path The path along which the flow is to be be added.
     * @return The set of edges whose flow has been modified.
     * @throws IOException If some error occurs during output.
     */
    private static <N> Set<Pair<Node<N>, Pair<FlowPair, Node<N>>>> addFlow(final Graph<N, FlowPair> graph, final List<Node<N>> path)
    throws IOException {
        final Integer min = GraphAlgorithms.computeMinEdge(graph, path);
        final Iterator<Node<N>> it = path.iterator();
        Node<N> from;
        Node<N> to = it.next();
        final Set<Pair<Node<N>, Pair<FlowPair, Node<N>>>> toHighlight =
            new LinkedHashSet<Pair<Node<N>, Pair<FlowPair, Node<N>>>>();
        while (it.hasNext()) {
            from = to;
            to = it.next();
            int flow = min;
            for (final Pair<FlowPair, Node<N>> edge : graph.getEdges(from, to)) {
                final int added = Math.min(flow, edge.x.y - edge.x.x);
                if (added > 0) {
                    flow -= added;
                    edge.x.x += added;
                    toHighlight.add(new Pair<Node<N>, Pair<FlowPair, Node<N>>>(from, edge));
                }
            }
            for (final Pair<FlowPair, Node<N>> edge : graph.getEdges(to, from)) {
                final int added = Math.min(flow, edge.x.x);
                if (added > 0) {
                    flow -= added;
                    edge.x.x -= added;
                    toHighlight.add(new Pair<Node<N>, Pair<FlowPair, Node<N>>>(to, edge));
                }
            }
            if (flow > 0) {
                throw new IllegalStateException("Could not add flow!");
            }
        }
        return toHighlight;
    }

    /**
     * Adds a node to the graph and updates the grid layout accordingly.
     * @param node The node to add.
     * @param graph The graph to add the node to.
     * @param pos The node's position in the grid layout.
     * @param grid The grid.
     * @param positions zThe grid layout positions.
     * @return The grid layout position of the added node.
     */
    private static NodeGridPosition addNode(
        final Node<String> node,
        final Graph<String, ?> graph,
        final Pair<Pair<Integer, Integer>, Boolean> pos,
        final Map<Pair<Integer, Integer>, Node<String>> grid,
        final Map<Node<String>, NodeGridPosition> positions
    ) {
        graph.addNode(node);
        grid.put(pos.x, node);
        final int x = pos.x.x;
        final int y = pos.x.y;
        final NodeGridPosition gridPos = new NodeGridPosition(pos.x.x, pos.x.y, pos.y);
        positions.put(node, gridPos);
        Pair<Integer, Integer> nextPos = new Pair<Integer, Integer>(x, y - 1);
        Node<String> nextNode = grid.get(nextPos);
        if (nextNode != null) {
            final NodeGridPosition nextGridPos = positions.get(nextNode);
            gridPos.north = nextGridPos;
            nextGridPos.south = gridPos;
        }
        nextPos = new Pair<Integer, Integer>(x + 1, y);
        nextNode = grid.get(nextPos);
        if (nextNode != null) {
            final NodeGridPosition nextGridPos = positions.get(nextNode);
            gridPos.east = nextGridPos;
            nextGridPos.west = gridPos;
        }
        nextPos = new Pair<Integer, Integer>(x, y + 1);
        nextNode = grid.get(nextPos);
        if (nextNode != null) {
            final NodeGridPosition nextGridPos = positions.get(nextNode);
            gridPos.south = nextGridPos;
            nextGridPos.north = gridPos;
        }
        nextPos = new Pair<Integer, Integer>(x - 1, y);
        nextNode = grid.get(nextPos);
        if (nextNode != null) {
            final NodeGridPosition nextGridPos = positions.get(nextNode);
            gridPos.west = nextGridPos;
            nextGridPos.east = gridPos;
        }
        if (pos.y) {
            nextPos = new Pair<Integer, Integer>(x + 1, y - 1);
            nextNode = grid.get(nextPos);
            if (nextNode != null) {
                final NodeGridPosition nextGridPos = positions.get(nextNode);
                gridPos.northeast = nextGridPos;
                nextGridPos.southwest = gridPos;
            }
            nextPos = new Pair<Integer, Integer>(x + 1, y + 1);
            nextNode = grid.get(nextPos);
            if (nextNode != null) {
                final NodeGridPosition nextGridPos = positions.get(nextNode);
                gridPos.southeast = nextGridPos;
                nextGridPos.northwest = gridPos;
            }
            nextPos = new Pair<Integer, Integer>(x - 1, y + 1);
            nextNode = grid.get(nextPos);
            if (nextNode != null) {
                final NodeGridPosition nextGridPos = positions.get(nextNode);
                gridPos.southwest = nextGridPos;
                nextGridPos.northeast = gridPos;
            }
            nextPos = new Pair<Integer, Integer>(x - 1, y - 1);
            nextNode = grid.get(nextPos);
            if (nextNode != null) {
                final NodeGridPosition nextGridPos = positions.get(nextNode);
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
    private static <N> Integer computeMinEdge(final Graph<N, FlowPair> graph, final List<Node<N>> path) {
        Integer min = null;
        final Iterator<Node<N>> it = path.iterator();
        Node<N> from;
        Node<N> to = it.next();
        while (it.hasNext()) {
            from = to;
            to = it.next();
            int flow = 0;
            for (final Pair<FlowPair, Node<N>> edge : graph.getEdges(from, to)) {
                flow += edge.x.y - edge.x.x;
            }
            for (final Pair<FlowPair, Node<N>> edge : graph.getEdges(to, from)) {
                flow += edge.x.x;
            }
            if (min == null || min > flow) {
                min = flow;
            }
        }
        return min;
    }

    private static Optional<Integer> computeNodeIndexWithMinimumDistance(
        final int columnIndex,
        final int size,
        final Integer[] distances,
        final Set<Integer> used,
        final String[][] exTable,
        final String[][] solTable
    ) {
        Integer currentMinimumDistance = null;
        int indexOfNodeWithMinimumDistance = -1;
        for (int toNodeIndex = 1; toNodeIndex < size; toNodeIndex++) {
            final Integer distanceToNode = distances[toNodeIndex];
            if (distanceToNode == null) {
                solTable[columnIndex][toNodeIndex] = "$\\infty$";
            } else {
                if (used.contains(toNodeIndex)) {
                    solTable[columnIndex][toNodeIndex] = "\\textbf{--}";
                } else {
                    if (currentMinimumDistance == null || currentMinimumDistance > distanceToNode) {
                        currentMinimumDistance = distanceToNode;
                        indexOfNodeWithMinimumDistance = toNodeIndex;
                    }
                    solTable[columnIndex][toNodeIndex] = String.valueOf(distanceToNode);
                }
            }
            exTable[columnIndex][toNodeIndex] = "";
        }
        if (currentMinimumDistance == null) {
            return Optional.empty();
        }
        return Optional.of(indexOfNodeWithMinimumDistance);
    }

    /**
     * Builds the residual graph from the specified flow network.
     * @param graph The flow network.
     * @return The residual graph built for the specified flow network.
     * @throws IOException If some error occurs during output.
     */
    private static <N> Graph<N, Integer> computeResidualGraph(final Graph<N, FlowPair> graph) throws IOException {
        final Graph<N, Integer> res = new Graph<N, Integer>();
        for (final Node<N> node : graph.getNodes()) {
            res.addNode(node);
            final List<Pair<FlowPair, Node<N>>> list = graph.getAdjacencyList(node);
            if (list == null) {
                continue;
            }
            for (final Pair<FlowPair, Node<N>> edge : list) {
                final Node<N> target = edge.y;
                final Integer back = edge.x.x;
                if (back > 0) {
                    final Set<Pair<Integer, Node<N>>> backEdges = res.getEdges(target, node);
                    if (backEdges.isEmpty()) {
                        res.addEdge(target, back, node);
                    } else {
                        backEdges.iterator().next().x += back;
                    }
                }
                final Integer forth = edge.x.y - back;
                if (forth > 0) {
                    final Set<Pair<Integer, Node<N>>> forthEdges = res.getEdges(node, target);
                    if (forthEdges.isEmpty()) {
                        res.addEdge(node, forth, target);
                    } else {
                        forthEdges.iterator().next().x += forth;
                    }
                }
            }
        }
        res.setGrid(graph.getGrid());
        return res;
    }

    private static void copyToExtended(final String[][] table, final String[][] tableExtended) {
        for (int i = 0; i < table.length; i++) {
            tableExtended[i][0] = table[i][0];
            System.arraycopy(table[i], 1, tableExtended[i], 2, table.length - 1);
        }
    }

    /**
     * @param remainingNodes The number of nodes yet to be added.
     * @param minYPos The minimal y position in the current level.
     * @param maxYPos The maximal y position in the current level.
     * @param xPos The x position of the current level.
     * @return True if there are at least as many nodes to be added as minimally needed when starting the current level
     *         with the specified parameters (by reducing whenever possible). False otherwise.
     */
    private static boolean enoughNodes(final int remainingNodes, final int minYPos, final int maxYPos, final int xPos) {
        int neededNodes = maxYPos - minYPos + 1;
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
            neededNodes += itMaxY - itMinY + 1;
        }
        return neededNodes <= remainingNodes;
    }

    private static <N> void fillRowHeadingsAndIdsForDijkstra(
        final String[][] exTable,
        final String[][] solTable,
        final List<Node<N>> nodes,
        final Map<Node<N>, Integer> nodeIds,
        final Node<N> start
    ) {
        int rowIndex = 1;
        solTable[0][0] = Main.TEXT_VERSION == TextVersion.ABRAHAM ? "\\texttt{v}" : "\\textbf{Knoten}";
        exTable[0][0] = solTable[0][0];
        for (final Node<N> node : nodes) {
            if (!node.equals(start)) {
                solTable[0][rowIndex] = GraphAlgorithms.toRowHeading(node.label);
                exTable[0][rowIndex] = solTable[0][rowIndex];
                nodeIds.put(node, rowIndex);
                rowIndex++;
            }
        }
    }

    private static FlowNetworkInput<String, FlowPair> generateFlowNetwork(final Parameters options) {
        final Random gen = new Random();
        final int numOfNodes;
        if (options.containsKey(Flag.LENGTH)) {
            numOfNodes = Integer.parseInt(options.get(Flag.LENGTH));
        } else {
            numOfNodes = gen.nextInt(16) + 3;
        }
        final FlowNetworkInput<String, FlowPair> res = new FlowNetworkInput<String, FlowPair>();
        res.graph = GraphAlgorithms.createRandomFlowNetwork(gen, numOfNodes);
        res.source = res.graph.getNodesWithLabel("s").iterator().next();
        res.sink = res.graph.getNodesWithLabel("t").iterator().next();
        res.multiplier = 1.0;
        res.twocolumns = false;
        return res;
    }

    private static Pair<Graph<String, Integer>, Node<String>> generateGraph(final Parameters options) {
        final String alg = options.get(Flag.ALGORITHM);
        final Random gen = new Random();
        final int numOfNodes;
        if (options.containsKey(Flag.LENGTH)) {
            numOfNodes = Integer.parseInt(options.get(Flag.LENGTH));
        } else {
            numOfNodes = gen.nextInt(16) + 5;
        }
        final Graph<String, Integer> graph =
            GraphAlgorithms.createRandomGraph(
                gen,
                numOfNodes,
                GraphAlgorithms.UNDIRECTED_GRAPH_ALGORITHMS.contains(alg)
            );
        return new Pair<Graph<String, Integer>, Node<String>>(
            graph,
            GraphAlgorithms.parseOrGenerateStartNode(graph, options)
        );
    }

    private static int[][] generateGridGraph(final Parameters options) {
        final GridGraph graph = new GridGraph();
        final int[][] sparseAdjacencyMatrix =
            new int[graph.numOfNodesInSparseAdjacencyMatrix()][graph.numOfNeighborsInSparseAdjacencyMatrix()];
        final String errorMessage =
            new String(
                "You need to provide "
                + graph.numOfNodesInSparseAdjacencyMatrix()
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
            for (int i = 0; i < graph.numOfNodesInSparseAdjacencyMatrix(); i++) {
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
     * Takes a sparse version of the adjacency matrix and constructs the according graph, which has not more than 35 nodes
     * and each node has a degree not greater than 8. The tex-code to present the resulting graph is then written to the file given
     * by the fourth argument. The results from applying the given operation on this graph are written to the file given by the last
     * argument.
     *
     * @param graph An empty graph object.
     * @param sparseAdjacencyMatrix A sparse version of the adjacency matrix in order to construct the according graph
     *                 (elements in a row are separated by "," and rows are separated by line breaks). Sparse means, that if we consider the nodes
     *                 being ordered in a 5x7 grid, we only store every second node, starting from the first node in the first row and traversing
     *                 row wise (i.e., than the 3. node in the first row, than the 5. node in the first row, than the 7. node in the first row, than
     *                 the 2. node in the second row, ..). Furthermore, sparse means, that we only store adjacencies to not more than 6 neighbors
     *                 (according to the grid) of a node, more precisely, those six which are positioned north, east, south, southwest, west and
     *                 northwest of the node. If the entry in the sparse version of the adjacency matrix is
     *                          * 1, then there is an outgoing edge
     *                          * -1, then there is an ingoing edge
     *                          * 2, then there is an outgoing and ingoing edge
     *                          * 0, no edge
     *                 to the corresponding node.
     *
     *                 Example: "x,2,2,x,x,x
     *                           x,2,2,2,2,x
     *                           x,2,2,2,2,x
     *                           x,x,2,2,2,x
     *                           2,2,2,2,2,2
     *                           2,2,2,2,2,2
     *                           2,2,2,2,2,2
     *                           2,2,2,x,x,x
     *                           2,2,2,2,2,2
     *                           2,2,2,2,2,2
     *                           2,x,2,2,2,2
     *                           2,2,2,2,2,2
     *                           2,2,2,2,2,2
     *                           2,2,2,2,2,2
     *                           2,2,x,x,x,x
     *                           2,2,x,x,2,2
     *                           2,2,x,x,2,2
     *                           2,x,x,x,2,2"
     *                  This is the graph where every of the 35 nodes in the grid is connected to all it's neighbors. At the positions with "x"
     *                  the values do not influence the graph as they belong to potential neighbors not being in the grid.
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

    private static <N> void improveDistancesForNode(
        final Node<N> currentNode,
        final int currentNodeId,
        final Graph<N, Integer> graph,
        final Map<Node<N>, Integer> nodeIds,
        final Integer[] distances
    ) {
        for (final Pair<Integer, Node<N>> edge : graph.getAdjacencyList(currentNode)) {
            final Integer toNodeId = nodeIds.get(edge.y);
            if (
                toNodeId != null
                && (distances[toNodeId] == null || distances[toNodeId] > distances[currentNodeId] + edge.x)
            ) {
                distances[toNodeId] = distances[currentNodeId] + edge.x;
            }
        }
    }

    /**
     * @return The set of (in student mode only enabled) graph algorithms needing a start node.
     */
    private static Set<String> initGraphAlgorithmsWithStartNode() {
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
        final Graph<String, FlowPair> graph = new Graph<String, FlowPair>();
        graph.setGraphFromInput(reader, new StringLabelParser(), new FlowPairLabelParser());
        Node<String> source = null;
        Node<String> sink = null;
        double multiplier = 1.0;
        boolean twocolumns = false;
        if (options.containsKey(Flag.OPERATIONS)) {
            try (BufferedReader operationsReader = new BufferedReader(new FileReader(options.get(Flag.OPERATIONS)))) {
                Set<Node<String>> nodes = graph.getNodesWithLabel(operationsReader.readLine().trim());
                if (!nodes.isEmpty()) {
                    source = nodes.iterator().next();
                }
                nodes = graph.getNodesWithLabel(operationsReader.readLine().trim());
                if (!nodes.isEmpty()) {
                    sink = nodes.iterator().next();
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

    private static Pair<Graph<String, Integer>, Node<String>> parseGraph(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        final Graph<String, Integer> graph = new Graph<String, Integer>();
        graph.setGraphFromInput(reader, new StringLabelParser(), new IntLabelParser());
        return new Pair<Graph<String, Integer>, Node<String>>(
            graph,
            GraphAlgorithms.parseOrGenerateStartNode(graph, options)
        );
    }

    private static int[][] parseGridGraph(final BufferedReader reader, final Parameters options) throws IOException {
        final GridGraph graph = new GridGraph();
        final int[][] sparseAdjacencyMatrix =
            new int[graph.numOfNodesInSparseAdjacencyMatrix()][graph.numOfNeighborsInSparseAdjacencyMatrix()];
        final String errorMessage =
            new String(
                "You need to provide "
                + graph.numOfNodesInSparseAdjacencyMatrix()
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
            final String[] nodes = line.split(",");
            if (nodes.length != graph.numOfNeighborsInSparseAdjacencyMatrix()) {
                System.out.println(errorMessage);
                return null;
            }
            for (int i = 0; i < graph.numOfNeighborsInSparseAdjacencyMatrix(); i++) {
                if (graph.isNecessarySparseMatrixEntry(rowNum,i) ) {
                    final int entry = Integer.parseInt(nodes[i].trim());
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

    private static Pair<Graph<String, Integer>, Node<String>> parseOrGenerateGraph(final Parameters options)
    throws IOException {
        return new ParserAndGenerator<Pair<Graph<String, Integer>, Node<String>>>(
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

    private static Node<String> parseOrGenerateStartNode(final Graph<String, Integer> graph, final Parameters options) {
        Node<String> node = null;
        if (options.containsKey(Flag.OPERATIONS)) {
            final String operations = options.get(Flag.OPERATIONS);
            try (
                BufferedReader operationsReader =
                    new BufferedReader(
                        options.containsKey(Flag.INPUT) ? new StringReader(operations) : new FileReader(operations)
                    )
            ) {
                final Set<Node<String>> nodes = graph.getNodesWithLabel(operationsReader.readLine().trim());
                if (!nodes.isEmpty()) {
                    node = nodes.iterator().next();
                }
            } catch (final IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else if (
            !options.containsKey(Flag.SOURCE)
            && !options.containsKey(Flag.INPUT)
            && GraphAlgorithms.GRAPH_ALGORITHMS_WITH_START_NODE.contains(options.get(Flag.ALGORITHM))
        ) {
            final Set<Node<String>> nodes = graph.getNodesWithLabel("A");
            if (!nodes.isEmpty()) {
                node = nodes.iterator().next();
            }
        }
        return node;
    }

    /**
     * Prints exercise and solution for the Dijkstra algorithm.
     * @param graph The graph.
     * @param start The start node.
     * @param comp A comparator for sorting the nodes in the table (may be null - then no sorting is applied).
     * @param mode The preprint mode.
     * @param exWriter The writer to send the exercise output to.
     * @param solWriter The writer to send the solution output to.
     * @throws IOException If some error occurs during output.
     */
    private static <N> void printDijkstra(
        final Graph<N, Integer> graph,
        final Node<N> start,
        final DijkstraTables tables,
        final PreprintMode mode,
        final Parameters options,
        final BufferedWriter exWriter,
        final BufferedWriter solWriter
    ) throws IOException {
        exWriter.write("Betrachten Sie den folgenden Graphen:\\\\[2ex]");
        Main.newLine(exWriter);
        TikZUtils.printBeginning(TikZUtils.CENTER, exWriter);
        graph.printTikZ(GraphPrintMode.ALL, 1, null, exWriter);
        Main.newLine(exWriter);
        TikZUtils.printEnd(TikZUtils.CENTER, exWriter);
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
                TikZUtils.printSolutionSpaceBeginning(options, exWriter);
                // fall-through
            case ALWAYS:
                TikZUtils.printBeginning(TikZUtils.CENTER, exWriter);
                Main.newLine(exWriter);
                TikZUtils.printArrayStretch(1.5, exWriter);
                TikZUtils.printTable(tables.exTable, tables.exColor, "2cm", exWriter, false, 10);
                TikZUtils.printArrayStretch(1.0, exWriter);
                TikZUtils.printEnd(TikZUtils.CENTER, exWriter);
                if (mode == PreprintMode.SOLUTION_SPACE) {
                    TikZUtils.printSolutionSpaceEnd(options, exWriter);
                } else {
                    Main.newLine(exWriter);
                    Main.newLine(exWriter);
                }
                break;
            case NEVER:
                Main.newLine(exWriter);
        }

        TikZUtils.printBeginning(TikZUtils.CENTER, solWriter);
        Main.newLine(solWriter);
        TikZUtils.printArrayStretch(1.5, solWriter);
        TikZUtils.printTable(tables.solTable, tables.solColor, "2cm", solWriter, false, 10);
        TikZUtils.printArrayStretch(1.0, solWriter);
        TikZUtils.printEnd(TikZUtils.CENTER, solWriter);
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
            TikZUtils.printTable(table, color, "1cm", writer, transpose, breakAtColumn);
            writer.write("\\hspace{2em}");
            Main.newLine(writer);
            return count + 1;
        }
        writer.write("\\\\[2ex]");
        Main.newLine(writer);
        TikZUtils.printTable(table, color, "1cm", writer, transpose, breakAtColumn);
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
    private static <N> List<Node<N>> selectAugmentingPath(final Graph<N, Integer> graph, final Node<N> source, final Node<N> sink) {
        List<Node<N>> path = new ArrayList<Node<N>>();
        final Deque<List<Node<N>>> queue = new ArrayDeque<List<Node<N>>>();
        path.add(source);
        queue.add(path);
        final Set<Node<N>> visited = new LinkedHashSet<Node<N>>();
        visited.add(source);
        while (!queue.isEmpty()) {
            path = queue.poll();
            final List<Pair<Integer, Node<N>>> list = graph.getAdjacencyList(path.get(path.size() - 1));
            if (list == null) {
                continue;
            }
            for (final Pair<Integer, Node<N>> edge : list) {
                if (visited.contains(edge.y)) {
                    continue;
                }
                final List<Node<N>> newPath = new ArrayList<Node<N>>(path);
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

    private static <N> void setColumnHeadForDijkstra(
        final String[][] exTable,
        final String[][] solTable,
        final int columnIndex,
        final Node<N> currentNode
    ) {
        exTable[columnIndex][0] = "";
        solTable[columnIndex][0] = GraphAlgorithms.toColumnHeading(currentNode.label);
    }

    private static <N> String toColumnHeading(final Optional<N> label) {
        return GraphAlgorithms.toHeading(label);
    }

    /**
     * @param graph Some residual graph.
     * @param path A path through this residual graph.
     * @return The set of all edges used by the specified path in the specified graph.
     */
    private static <N> Set<Pair<Node<N>, Pair<Integer, Node<N>>>> toEdges(final Graph<N, Integer> graph, final List<Node<N>> path) {
        if (path == null) {
            return null;
        }
        final Set<Pair<Node<N>, Pair<Integer, Node<N>>>> res = new LinkedHashSet<Pair<Node<N>, Pair<Integer, Node<N>>>>();
        final Iterator<Node<N>> it = path.iterator();
        Node<N> cur = it.next();
        while (it.hasNext()) {
            final Node<N> next = it.next();
            for (final Pair<Integer, Node<N>> edge : graph.getAdjacencyList(cur)) {
                if (edge.y.equals(next)) {
                    res.add(new Pair<Node<N>, Pair<Integer, Node<N>>>(cur, edge));
                    break;
                }
            }
            cur = next;
        }
        return res;
    }

    private static <N> String toHeading(final Optional<N> label) {
        return label.isEmpty() ? "" : String.format("\\textbf{%s}", label.get().toString());
    }

    private static <N> String toRowHeading(final Optional<N> label) {
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
