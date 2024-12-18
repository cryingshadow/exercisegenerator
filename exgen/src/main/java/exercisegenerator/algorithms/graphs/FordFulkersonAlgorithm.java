package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;
import java.util.Map.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

public class FordFulkersonAlgorithm implements AlgorithmImplementation<FlowNetworkProblem, List<FordFulkersonDoubleStep>> {

    private static record FordFulkersonConfiguration(double multiplier, boolean twoColumns) {}

    private static record GraphWithHighlights(
        Graph<String, FlowPair> graph,
        Set<FordFulkersonPathStep<String, FlowPair>> highlights
    ) {}

    public static final FordFulkersonAlgorithm INSTANCE = new FordFulkersonAlgorithm();

    /**
     * The default value being probably close to the edge values adjacent to source/sink vertices.
     */
    static final int DEFAULT_SOURCE_SINK_ROOT = 7;

    private static final String EACH_RESIDUAL_GRAPH = "\\emphasize{jedes Restnetzwerk (auch das initiale)}";

    private static final String RESIDUAL_GRAPH_NAME = "Restnetzwerk";

    /**
     * @param numOfVertices The number of vertices (excluding source and sink) in the returned flow network.
     * @return A random flow network with <code>numOfVertices</code> vertices labeled with Strings (each vertex has a
     *         unique label and the source is labeled with s and the sink is labeled with t) and edges labeled with
     *         pairs of Integers (the current flow and the capacity - the current flow will be set to 0).
     */
    public static Graph<String, FlowPair> createRandomFlowNetwork(final int numOfVertices) {
        if (numOfVertices < 0) {
            throw new IllegalArgumentException("Number of vertices must not be negative!");
        }
        final Graph<String, FlowPair> graph = new Graph<String, FlowPair>();
        final Grid<String> grid = new Grid<String>();
        final Vertex<String> source = new Vertex<String>(Optional.of("s"));
        final Map<Vertex<String>, VertexGridPosition> positions =
            new LinkedHashMap<Vertex<String>, VertexGridPosition>();
        final GridCoordinates startPos = new GridCoordinates(0, 0);
        GraphAlgorithm.addVertexWithGridLayout(
            source,
            graph,
            new Pair<GridCoordinates, Boolean>(startPos, true),
            grid,
            positions
        );
        if (numOfVertices == 0) {
            final Vertex<String> sink = new Vertex<String>(Optional.of("t"));
            GraphAlgorithm.addVertexWithGridLayout(
                sink,
                graph,
                new Pair<GridCoordinates, Boolean>(new GridCoordinates(1, 0), false),
                grid,
                positions
            );
            final int value = GraphAlgorithm.randomEdgeValue(GraphAlgorithm.DEFAULT_EDGE_ROOT);
            graph.addEdge(source, Optional.of(new FlowPair(0, value)), sink);
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
                        switch (Main.RANDOM.nextInt(3)) {
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
                        if (Main.RANDOM.nextBoolean()) {
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
                        if (
                            FordFulkersonAlgorithm.enoughVertices(
                                remainingVertices,
                                prevMinYPos - 1,
                                prevMaxYPos + 1,
                                xPos
                            )
                        ) {
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
                                FordFulkersonAlgorithm.enoughVertices(
                                    remainingVertices,
                                    prevMinYPos,
                                    prevMaxYPos + 1,
                                    xPos
                                );
                            final boolean justExpandMin =
                                FordFulkersonAlgorithm.enoughVertices(
                                    remainingVertices,
                                    prevMinYPos - 1,
                                    prevMaxYPos,
                                    xPos
                                );
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
                            } else if (
                                FordFulkersonAlgorithm.enoughVertices(remainingVertices, prevMinYPos, prevMaxYPos, xPos)
                            ) {
                                options.add(keepMin + reduceMax);
                                options.add(reduceMin + keepMax);
                                options.add(keepMin + keepMax);
                                options.add(keepMin + keepMax);
                            } else {
                                if (
                                    FordFulkersonAlgorithm.enoughVertices(
                                        remainingVertices,
                                        prevMinYPos + 1,
                                        prevMaxYPos,
                                        xPos
                                    )
                                ) {
                                    options.add(reduceMin + keepMax);
                                }
                                if (
                                    FordFulkersonAlgorithm.enoughVertices(
                                        remainingVertices,
                                        prevMinYPos,
                                        prevMaxYPos - 1,
                                        xPos
                                    )
                                ) {
                                    options.add(keepMin + reduceMax);
                                }
                            }
                        }
                    } else {
                        options.add(reduceMin + keepMax);
                        if (
                            FordFulkersonAlgorithm.enoughVertices(remainingVertices, prevMinYPos - 1, prevMaxYPos, xPos)
                        ) {
                            options.add(keepMin + keepMax);
                            options.add(keepMin + keepMax);
                            options.add(expandMin + keepMax);
                            options.add(expandMin + keepMax);
                            options.add(expandMin + keepMax);
                        } else if (
                            FordFulkersonAlgorithm.enoughVertices(remainingVertices, prevMinYPos, prevMaxYPos, xPos)
                        ) {
                            options.add(keepMin + keepMax);
                            options.add(keepMin + keepMax);
                        }
                    }
                } else if (maxDiagonal) {
                    options.add(keepMin + reduceMax);
                    if (FordFulkersonAlgorithm.enoughVertices(remainingVertices, prevMinYPos, prevMaxYPos + 1, xPos)) {
                        options.add(keepMin + keepMax);
                        options.add(keepMin + keepMax);
                        options.add(keepMin + expandMax);
                        options.add(keepMin + expandMax);
                        options.add(keepMin + expandMax);
                    } else if (
                        FordFulkersonAlgorithm.enoughVertices(remainingVertices, prevMinYPos, prevMaxYPos, xPos)
                    ) {
                        options.add(keepMin + keepMax);
                        options.add(keepMin + keepMax);
                    }
                } else {
                    options.add(keepMin + keepMax);
                }
                switch (options.get(Main.RANDOM.nextInt(options.size()))) {
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
                    new Vertex<String>(Optional.of(GraphAlgorithm.toLetterLabel(letter++)));
                final boolean hasDiagonals = (xPos + yPos) % 2 == 0;
                final GridCoordinates pos = new GridCoordinates(xPos, yPos);
                GraphAlgorithm.addVertexWithGridLayout(
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
                    final int index = Main.RANDOM.nextInt(existing.size());
                    previousVertex = existing.remove(index);
                    graph.addEdge(
                        previousVertex,
                        Optional.of(
                            new FlowPair(
                                0,
                                GraphAlgorithm.randomEdgeValue(
                                    prevXPos == 0 ?
                                        FordFulkersonAlgorithm.DEFAULT_SOURCE_SINK_ROOT :
                                            GraphAlgorithm.DEFAULT_EDGE_ROOT
                                )
                            )
                        ),
                        vertex
                    );
                    for (final Vertex<String> otherVertex : existing) {
                        if (Main.RANDOM.nextBoolean()) {
                            graph.addEdge(
                                otherVertex,
                                Optional.of(
                                    new FlowPair(
                                        0,
                                        GraphAlgorithm.randomEdgeValue(GraphAlgorithm.DEFAULT_EDGE_ROOT)
                                    )
                                ),
                                vertex
                            );
                        }
                    }
                } else {
                    graph.addEdge(
                        grid.get(new GridCoordinates(prevXPos, yPos)),
                        Optional.of(
                            new FlowPair(
                                0,
                                GraphAlgorithm.randomEdgeValue(
                                    prevXPos == 0 ?
                                        FordFulkersonAlgorithm.DEFAULT_SOURCE_SINK_ROOT :
                                            GraphAlgorithm.DEFAULT_EDGE_ROOT
                                )
                            )
                        ),
                        vertex
                    );
                }
                if (yPos > curMinYPos) {
                    // north-south edges
                    final Vertex<String> north = grid.get(new GridCoordinates(xPos, yPos - 1));
                    if (Main.RANDOM.nextBoolean()) {
                        graph.addEdge(
                            north,
                            Optional.of(
                                new FlowPair(0, GraphAlgorithm.randomEdgeValue(GraphAlgorithm.DEFAULT_EDGE_ROOT))
                            ),
                            vertex
                        );
                    }
                    if (Main.RANDOM.nextBoolean()) {
                        graph.addEdge(
                            vertex,
                            Optional.of(
                                new FlowPair(0, GraphAlgorithm.randomEdgeValue(GraphAlgorithm.DEFAULT_EDGE_ROOT))
                            ),
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
                    final int index = Main.RANDOM.nextInt(existing.size());
                    nextVertex = existing.remove(index);
                    graph.addEdge(
                        previousVertex,
                        Optional.of(
                            new FlowPair(0, GraphAlgorithm.randomEdgeValue(GraphAlgorithm.DEFAULT_EDGE_ROOT))
                        ),
                        nextVertex
                    );
                } else {
                    final Vertex<String> nextVertex = grid.get(new GridCoordinates(xPos, prevYPos));
                    if (graph.getEdges(previousVertex, nextVertex).isEmpty()) {
                        graph.addEdge(
                            previousVertex,
                            Optional.of(
                                new FlowPair(0, GraphAlgorithm.randomEdgeValue(GraphAlgorithm.DEFAULT_EDGE_ROOT))
                            ),
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
        GraphAlgorithm.addVertexWithGridLayout(
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
                Optional.of(
                    new FlowPair(
                        0,
                        GraphAlgorithm.randomEdgeValue(FordFulkersonAlgorithm.DEFAULT_SOURCE_SINK_ROOT)
                    )
                ),
                sink
            );
        }
        // adjust grid (non-negative coordinates, sum of coordinates even -> has diagonals
        int xAdd = 0;
        if (minYPos % 2 != 0) {
            xAdd++;
        }
        final Grid<String> newGrid = new Grid<String>();
        for (final Entry<GridCoordinates, Vertex<String>> entry : grid.entrySet()) {
            final GridCoordinates key = entry.getKey();
            newGrid.put(new GridCoordinates(key.x + xAdd, key.y - minYPos), entry.getValue());
        }
        graph.setGrid(newGrid);
        return graph;
    }

    private static GraphWithHighlights computeGraphWithFlow(
        final Graph<String, FlowPair> inputGraph,
        final List<Vertex<String>> flowPath
    ) {
        final Graph<String, FlowPair> graph = inputGraph.copy(pair -> new FlowPair(pair.x, pair.y));
        final Integer min = FordFulkersonAlgorithm.computeMinEdge(graph, flowPath);
        final Iterator<Vertex<String>> it = flowPath.iterator();
        Vertex<String> from;
        Vertex<String> to = it.next();
        final Set<FordFulkersonPathStep<String, FlowPair>> toHighlight =
            new LinkedHashSet<FordFulkersonPathStep<String, FlowPair>>();
        while (it.hasNext()) {
            from = to;
            to = it.next();
            int flow = min;
            for (final Edge<FlowPair, String> edge : graph.getEdges(from, to)) {
                final int added = Math.min(flow, edge.label.get().y - edge.label.get().x);
                if (added > 0) {
                    flow -= added;
                    edge.label.get().x += added;
                    toHighlight.add(new FordFulkersonPathStep<String, FlowPair>(from, edge));
                }
            }
            for (final Edge<FlowPair, String> edge : graph.getEdges(to, from)) {
                final int added = Math.min(flow, edge.label.get().x);
                if (added > 0) {
                    flow -= added;
                    edge.label.get().x -= added;
                    toHighlight.add(new FordFulkersonPathStep<String, FlowPair>(to, edge));
                }
            }
            if (flow > 0) {
                throw new IllegalStateException("Could not add flow!");
            }
        }
        return new GraphWithHighlights(graph, toHighlight);
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
                flow += edge.label.get().y - edge.label.get().x;
            }
            for (final Edge<FlowPair, V> edge : graph.getEdges(to, from)) {
                flow += edge.label.get().x;
            }
            if (min == null || min > flow) {
                min = flow;
            }
        }
        return min;
    }

    private static <V> Graph<V, Integer> computeResidualGraph(final Graph<V, FlowPair> graph) {
        final Graph<V, Integer> res = new Graph<V, Integer>();
        for (final Vertex<V> vertex : graph.getVertices()) {
            res.addVertex(vertex);
            final List<Edge<FlowPair, V>> list = graph.getAdjacencyList(vertex);
            if (list == null) {
                continue;
            }
            for (final Edge<FlowPair, V> edge : list) {
                final Vertex<V> target = edge.to;
                final Integer back = edge.label.get().x;
                if (back > 0) {
                    final Set<Edge<Integer, V>> backEdges = res.getEdges(target, vertex);
                    if (backEdges.isEmpty()) {
                        res.addEdge(target, Optional.of(back), vertex);
                    } else {
                        final Edge<Integer, V> backEdge = backEdges.iterator().next();
                        res.replaceEdgeLabel(target, backEdge.label.get() + back, vertex);
                    }
                }
                final Integer forth = edge.label.get().y - back;
                if (forth > 0) {
                    final Set<Edge<Integer, V>> forthEdges = res.getEdges(vertex, target);
                    if (forthEdges.isEmpty()) {
                        res.addEdge(vertex, Optional.of(forth), target);
                    } else {
                        final Edge<Integer, V> forthEdge = forthEdges.iterator().next();
                        res.replaceEdgeLabel(vertex, forthEdge.label.get() + forth, target);
                    }
                }
            }
        }
        res.setGrid(graph.getGrid());
        return res;
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

    private static Set<FordFulkersonPathStep<String, Integer>> filterHighLights(
        final Set<FordFulkersonPathStep<String, Integer>> highlights
    ) {
        switch (Main.TEXT_VERSION) {
        case ABRAHAM:
            return highlights;
        case GENERAL:
            return Collections.emptySet();
        default:
            throw new IllegalStateException("Unkown text version!");
        }
    };

    private static FlowNetworkProblem generateFlowNetworkProblem(final Parameters options) {
        final int numOfVertices = AlgorithmImplementation.parseOrGenerateLength(3, 18, options);
        final Graph<String, FlowPair> graph = FordFulkersonAlgorithm.createRandomFlowNetwork(numOfVertices);
        return new FlowNetworkProblem(
            graph,
            graph.getVerticesWithLabel("s").iterator().next(),
            graph.getVerticesWithLabel("t").iterator().next()
        );
    }

    private static FlowNetworkProblem parseFlowNetworkProblem(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        final Graph<String, FlowPair> graph = Graph.create(reader, new StringLabelParser(), new FlowPairLabelParser());
        Vertex<String> source = null;
        Vertex<String> sink = null;
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
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        return new FlowNetworkProblem(graph, source, sink);
    }

    private static FordFulkersonConfiguration parseOrGenerateConfiguration(
        final FlowNetworkProblem problem,
        final Parameters options
    ) {
        double multiplier = 1.0;
        boolean twoColumns = false;
        if (options.containsKey(Flag.OPERATIONS)) {
            try (BufferedReader operationsReader = new BufferedReader(new FileReader(options.get(Flag.OPERATIONS)))) {
                operationsReader.readLine();
                operationsReader.readLine();
                final String mult = operationsReader.readLine();
                if (mult != null && !"".equals(mult.trim())) {
                    multiplier = Double.parseDouble(mult);
                    final String twocols = operationsReader.readLine();
                    if (twocols != null && !"".equals(twocols.trim())) {
                        twoColumns = Boolean.parseBoolean(twocols);
                    }
                }
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        return new FordFulkersonConfiguration(multiplier, twoColumns);
    }

    private static void printFordFulkersonDoubleStep(
        final int stepNumber,
        final FordFulkersonDoubleStep step,
        final boolean first,
        final boolean solution,
        final PreprintMode mode,
        final FordFulkersonConfiguration configuration,
        final BufferedWriter writer
    ) throws IOException {
        if (!solution && mode == PreprintMode.NEVER) {
            return;
        }
        if (!first) {
            LaTeXUtils.printSamePageBeginning(
                stepNumber,
                configuration.twoColumns ? LaTeXUtils.TWO_COL_WIDTH : LaTeXUtils.COL_WIDTH,
                writer
            );
            writer.write("N\\\"achstes Flussnetzwerk mit aktuellem Fluss:\\\\[2ex]");
            Main.newLine(writer);
            step.flowNetwork().printTikZ(
                solution ? GraphPrintMode.ALL : GraphPrintMode.NO_EDGE_LABELS,
                configuration.multiplier(),
                solution ? step.flowHighlights() : null,
                writer
            );
            LaTeXUtils.printSamePageEnd(writer);
            if (configuration.twoColumns) {
                writer.write("\\\\");
            }
            Main.newLine(writer);
        }
        LaTeXUtils.printSamePageBeginning(
            first ? stepNumber : stepNumber + 1,
            configuration.twoColumns ? LaTeXUtils.TWO_COL_WIDTH : LaTeXUtils.COL_WIDTH,
            writer
        );
        writer.write(FordFulkersonAlgorithm.RESIDUAL_GRAPH_NAME);
        writer.write(":\\\\[2ex]");
        Main.newLine(writer);
        step.residualGraph().printTikZ(
            solution ? GraphPrintMode.ALL : GraphPrintMode.NO_EDGES,
                configuration.multiplier(),
                FordFulkersonAlgorithm.filterHighLights(step.residualHighlights()),
                writer
            );
        LaTeXUtils.printSamePageEnd(writer);
        if (configuration.twoColumns) {
            writer.write(" & ");
        } else {
            Main.newLine(writer);
        }
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
                if (visited.contains(edge.to)) {
                    continue;
                }
                final List<Vertex<V>> newPath = new ArrayList<Vertex<V>>(path);
                newPath.add(edge.to);
                if (sink.equals(edge.to)) {
                    return newPath;
                }
                visited.add(edge.to);
                queue.add(newPath);
            }
        }
        return null;
    }

    /**
     * @param graph Some residual graph.
     * @param path A path through this residual graph.
     * @return The set of all edges used by the specified path in the specified graph.
     */
    private static <V> Set<FordFulkersonPathStep<V, Integer>> toEdges(
        final Graph<V, Integer> graph,
        final List<Vertex<V>> path
    ) {
        if (path == null) {
            return Collections.emptySet();
        }
        final Set<FordFulkersonPathStep<V, Integer>> res =
            new LinkedHashSet<FordFulkersonPathStep<V, Integer>>();
        final Iterator<Vertex<V>> it = path.iterator();
        Vertex<V> cur = it.next();
        while (it.hasNext()) {
            final Vertex<V> next = it.next();
            for (final Edge<Integer, V> edge : graph.getAdjacencyList(cur)) {
                if (edge.to.equals(next)) {
                    res.add(new FordFulkersonPathStep<V, Integer>(cur, edge));
                    break;
                }
            }
            cur = next;
        }
        return res;
    }

    private FordFulkersonAlgorithm() {}

    @Override
    public List<FordFulkersonDoubleStep> apply(final FlowNetworkProblem problem) {
        Graph<String, FlowPair> graph = problem.graph();
        final Vertex<String> source = problem.source();
        final Vertex<String> sink = problem.sink();
        Graph<String, Integer> residualGraph = FordFulkersonAlgorithm.computeResidualGraph(graph);
        List<Vertex<String>> path = FordFulkersonAlgorithm.selectAugmentingPath(residualGraph, source, sink);
        final List<FordFulkersonDoubleStep> result = new LinkedList<FordFulkersonDoubleStep>();
        result.add(
            new FordFulkersonDoubleStep(
                graph,
                Collections.emptySet(),
                residualGraph,
                FordFulkersonAlgorithm.toEdges(residualGraph, path)
            )
        );
        while (path != null) {
            final GraphWithHighlights graphWithAddedFlow = FordFulkersonAlgorithm.computeGraphWithFlow(graph, path);
            graph = graphWithAddedFlow.graph();
            residualGraph = FordFulkersonAlgorithm.computeResidualGraph(graph);
            path = FordFulkersonAlgorithm.selectAugmentingPath(residualGraph, source, sink);
            result.add(
                new FordFulkersonDoubleStep(
                    graph,
                    graphWithAddedFlow.highlights(),
                    residualGraph,
                    FordFulkersonAlgorithm.toEdges(residualGraph, path)
                )
            );
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
    public FlowNetworkProblem parseOrGenerateProblem(final Parameters options)
    throws IOException {
        return new ParserAndGenerator<FlowNetworkProblem>(
            FordFulkersonAlgorithm::parseFlowNetworkProblem,
            FordFulkersonAlgorithm::generateFlowNetworkProblem
        ).getResult(options);
    }

    @Override
    public void printExercise(
        final FlowNetworkProblem problem,
        final List<FordFulkersonDoubleStep> solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        final Vertex<String> source = problem.source();
        final Vertex<String> sink = problem.sink();
        final PreprintMode mode = PreprintMode.parsePreprintMode(options);
        final FordFulkersonConfiguration configuration =
            FordFulkersonAlgorithm.parseOrGenerateConfiguration(problem, options);
        writer.write("Betrachten Sie das folgende Flussnetzwerk mit Quelle ");
        writer.write(source.label.isEmpty() ? "" : source.label.get().toString());
        writer.write(" und Senke ");
        writer.write(sink.label.isEmpty() ? "" : sink.label.get().toString());
        writer.write(":\\\\");
        Main.newLine(writer);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        problem.graph().printTikZ(GraphPrintMode.ALL, configuration.multiplier(), null, writer);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
        Main.newLine(writer);
        writer.write("Berechnen Sie den maximalen Fluss in diesem Netzwerk mithilfe der");
        writer.write(" \\emphasize{Ford-Fulkerson Methode}. Geben Sie dazu ");
        writer.write(FordFulkersonAlgorithm.EACH_RESIDUAL_GRAPH);
        writer.write(" sowie \\emphasize{nach jeder Augmentierung} den aktuellen Zustand des Flussnetzwerks an. ");
        writer.write("Geben Sie au\\ss{}erdem den \\emphasize{Wert des maximalen Flusses} an.");
        switch (mode) {
        case ALWAYS:
        case SOLUTION_SPACE:
            writer.write(
                " Die vorgegebene Anzahl an L\\\"osungsschritten muss nicht mit der ben\\\"otigten Anzahl "
                );
            writer.write("solcher Schritte \\\"ubereinstimmen.\\\\[2ex]");
            break;
        case NEVER:
            // do nothing
        }
        Main.newLine(writer);
        int stepNumber = 1;
        switch (mode) {
        case SOLUTION_SPACE:
            LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
            // fall-through
        case ALWAYS:
            if (configuration.twoColumns) {
                writer.write("\\begin{longtable}{cc}");
                Main.newLine(writer);
            }
            break;
        case NEVER:
            // do nothing
        }
        boolean first = true;
        for (final FordFulkersonDoubleStep step : solution) {
            if (first) {
                first = false;
                FordFulkersonAlgorithm.printFordFulkersonDoubleStep(
                    stepNumber,
                    step,
                    true,
                    false,
                    mode,
                    configuration,
                    writer
                );
                stepNumber += 1;
            } else {
                FordFulkersonAlgorithm.printFordFulkersonDoubleStep(
                    stepNumber,
                    step,
                    false,
                    false,
                    mode,
                    configuration,
                    writer
                );
                stepNumber += 2;
            }
        }
        switch (mode) {
        case ALWAYS:
        case SOLUTION_SPACE:
            if (configuration.twoColumns) {
                writer.write("\\end{longtable}");
                Main.newLine(writer);
            }
            Main.newLine(writer);
            Main.newLine(writer);
            writer.write("\\vspace*{1ex}");
            Main.newLine(writer);
            Main.newLine(writer);
            writer.write("Der maximale Fluss hat den Wert: ");
            Main.newLine(writer);
            if (mode == PreprintMode.SOLUTION_SPACE) {
                LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, writer);
            }
            Main.newLine(writer);
            break;
        case NEVER:
            // do nothing
        }
    }

    @Override
    public void printSolution(
        final FlowNetworkProblem problem,
        final List<FordFulkersonDoubleStep> solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        final PreprintMode mode = PreprintMode.parsePreprintMode(options);
        final FordFulkersonConfiguration configuration =
            FordFulkersonAlgorithm.parseOrGenerateConfiguration(problem, options);
        int flow = 0;
        final Graph<String, FlowPair> graph = solution.getLast().flowNetwork();
        final List<Edge<FlowPair, String>> list = graph.getAdjacencyList(problem.source());
        if (list != null) {
            for (final Edge<FlowPair, String> edge : list) {
                flow += edge.label.get().x;
            }
        }
        if (configuration.twoColumns) {
            writer.write("\\begin{longtable}{cc}");
            Main.newLine(writer);
        }
        int stepNumber = 1;
        boolean first = true;
        for (final FordFulkersonDoubleStep step : solution) {
            if (first) {
                first = false;
                FordFulkersonAlgorithm.printFordFulkersonDoubleStep(
                    stepNumber,
                    step,
                    true,
                    true,
                    mode,
                    configuration,
                    writer
                );
                stepNumber += 1;
            } else {
                FordFulkersonAlgorithm.printFordFulkersonDoubleStep(
                    stepNumber,
                    step,
                    false,
                    true,
                    mode,
                    configuration,
                    writer
                );
                stepNumber += 2;
            }
        }
        if (configuration.twoColumns) {
            writer.write("\\end{longtable}");
            Main.newLine(writer);
        }
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("\\vspace*{1ex}");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("Der maximale Fluss hat den Wert: " + flow);
        Main.newLine(writer);
        Main.newLine(writer);
    }

}
