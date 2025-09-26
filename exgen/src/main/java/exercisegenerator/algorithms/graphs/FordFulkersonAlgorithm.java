package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;
import java.util.Map.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;
import exercisegenerator.structures.graphs.flownetwork.*;
import exercisegenerator.structures.graphs.layout.*;
import exercisegenerator.structures.graphs.layout.GridGraphLayout.*;

public class FordFulkersonAlgorithm
implements AlgorithmImplementation<FlowNetworkProblem, List<FordFulkersonDoubleStep>> {

    private static record FordFulkersonConfiguration(double multiplier, boolean twoColumns) {}

    private static record GraphWithHighlights(
        Graph<String, FlowAndCapacity> graph,
        Set<FordFulkersonPathStep<String, FlowAndCapacity>> highlights
    ) {}

    public static final FordFulkersonAlgorithm INSTANCE = new FordFulkersonAlgorithm();

    static final int DEFAULT_SOURCE_SINK_ROOT = 7;

    private static final String RESIDUAL_GRAPH_NAME = "Restnetzwerk";

    public static GraphWithLayout<String, FlowAndCapacity, Integer> createRandomFlowNetwork(
        final int numOfInnerVertices
    ) {
        if (numOfInnerVertices < 0) {
            throw new IllegalArgumentException("Number of vertices must not be negative!");
        }
        final Graph<String, FlowAndCapacity> graph = new Graph<String, FlowAndCapacity>();
//        final Grid<String> grid = new Grid<String>();
        final Map<Coordinates2D<Integer>, Vertex<String>> verticesAtPositions =
            new LinkedHashMap<Coordinates2D<Integer>, Vertex<String>>();
        final Vertex<String> source = new Vertex<String>(Optional.of("s"));
//        final Map<Vertex<String>, VertexGridPosition> positions =
//            new LinkedHashMap<Vertex<String>, VertexGridPosition>();
        graph.addVertex(source);
        final Coordinates2D<Integer> startPos = new Coordinates2D<Integer>(0, 0);
        verticesAtPositions.put(startPos, source);
        if (numOfInnerVertices == 0) {
            final Vertex<String> sink = new Vertex<String>(Optional.of("t"));
            graph.addVertex(sink);
            final GridGraphLayoutBuilder<String, FlowAndCapacity> layoutBuilder =
                GridGraphLayout.<String, FlowAndCapacity>builder().setDirected(true);
            layoutBuilder.addVertex(source, startPos);
            layoutBuilder.addVertex(sink, new Coordinates2D<Integer>(1, 0));
            final int value = GraphAlgorithm.randomEdgeValue(GraphAlgorithm.DEFAULT_EDGE_ROOT);
            graph.addEdge(source, Optional.of(new FlowAndCapacity(0, value)), sink);
            return new GraphWithLayout<>(graph, layoutBuilder.build());
        }
        int xPos = 1;
        int minYPos = 0;
        int curMinYPos = 0;
        int curMaxYPos = 0;
        int prevMinYPos;
        int prevMaxYPos;
        int remainingVertices = numOfInnerVertices;
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
                            FordFulkersonAlgorithm.enoughVerticesForLevel(
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
                                FordFulkersonAlgorithm.enoughVerticesForLevel(
                                    remainingVertices,
                                    prevMinYPos,
                                    prevMaxYPos + 1,
                                    xPos
                                );
                            final boolean justExpandMin =
                                FordFulkersonAlgorithm.enoughVerticesForLevel(
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
                                FordFulkersonAlgorithm.enoughVerticesForLevel(
                                    remainingVertices,
                                    prevMinYPos,
                                    prevMaxYPos,
                                    xPos
                                )
                            ) {
                                options.add(keepMin + reduceMax);
                                options.add(reduceMin + keepMax);
                                options.add(keepMin + keepMax);
                                options.add(keepMin + keepMax);
                            } else {
                                if (
                                    FordFulkersonAlgorithm.enoughVerticesForLevel(
                                        remainingVertices,
                                        prevMinYPos + 1,
                                        prevMaxYPos,
                                        xPos
                                    )
                                ) {
                                    options.add(reduceMin + keepMax);
                                }
                                if (
                                    FordFulkersonAlgorithm.enoughVerticesForLevel(
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
                            FordFulkersonAlgorithm.enoughVerticesForLevel(
                                remainingVertices,
                                prevMinYPos - 1,
                                prevMaxYPos,
                                xPos
                            )
                        ) {
                            options.add(keepMin + keepMax);
                            options.add(keepMin + keepMax);
                            options.add(expandMin + keepMax);
                            options.add(expandMin + keepMax);
                            options.add(expandMin + keepMax);
                        } else if (
                            FordFulkersonAlgorithm.enoughVerticesForLevel(
                                remainingVertices,
                                prevMinYPos,
                                prevMaxYPos,
                                xPos
                            )
                        ) {
                            options.add(keepMin + keepMax);
                            options.add(keepMin + keepMax);
                        }
                    }
                } else if (maxDiagonal) {
                    options.add(keepMin + reduceMax);
                    if (
                        FordFulkersonAlgorithm.enoughVerticesForLevel(
                            remainingVertices,
                            prevMinYPos,
                            prevMaxYPos + 1,
                            xPos
                        )
                    ) {
                        options.add(keepMin + keepMax);
                        options.add(keepMin + keepMax);
                        options.add(keepMin + expandMax);
                        options.add(keepMin + expandMax);
                        options.add(keepMin + expandMax);
                    } else if (
                        FordFulkersonAlgorithm.enoughVerticesForLevel(remainingVertices, prevMinYPos, prevMaxYPos, xPos)
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
                final Coordinates2D<Integer> pos = new Coordinates2D<Integer>(xPos, yPos);
                graph.addVertex(vertex);
                verticesAtPositions.put(pos, vertex);
                if (hasDiagonals) {
                    final List<Vertex<String>> existing = new ArrayList<Vertex<String>>();
                    Vertex<String> previousVertex =
                        verticesAtPositions.get(new Coordinates2D<Integer>(prevXPos, yPos - 1));
                    if (previousVertex != null) {
                        existing.add(previousVertex);
                    }
                    previousVertex =  verticesAtPositions.get(new Coordinates2D<Integer>(prevXPos, yPos));
                    if (previousVertex != null) {
                        existing.add(previousVertex);
                    }
                    previousVertex =  verticesAtPositions.get(new Coordinates2D<Integer>(prevXPos, yPos + 1));
                    if (previousVertex != null) {
                        existing.add(previousVertex);
                    }
                    final int index = Main.RANDOM.nextInt(existing.size());
                    previousVertex = existing.remove(index);
                    graph.addEdge(
                        previousVertex,
                        Optional.of(
                            new FlowAndCapacity(
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
                                    new FlowAndCapacity(
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
                        verticesAtPositions.get(new Coordinates2D<Integer>(prevXPos, yPos)),
                        Optional.of(
                            new FlowAndCapacity(
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
                    final Vertex<String> north = verticesAtPositions.get(new Coordinates2D<Integer>(xPos, yPos - 1));
                    if (Main.RANDOM.nextBoolean()) {
                        graph.addEdge(
                            north,
                            Optional.of(
                                new FlowAndCapacity(0, GraphAlgorithm.randomEdgeValue(GraphAlgorithm.DEFAULT_EDGE_ROOT))
                            ),
                            vertex
                        );
                    }
                    if (Main.RANDOM.nextBoolean()) {
                        graph.addEdge(
                            vertex,
                            Optional.of(
                                new FlowAndCapacity(0, GraphAlgorithm.randomEdgeValue(GraphAlgorithm.DEFAULT_EDGE_ROOT))
                            ),
                            north
                        );
                    }
                }
            }
            // at least one edge for each vertex on previous level to current level
            outer: for (int prevYPos = prevMinYPos; prevYPos <= prevMaxYPos; prevYPos++) {
                final Vertex<String> previousVertex =
                    verticesAtPositions.get(new Coordinates2D<Integer>(prevXPos, prevYPos));
                final boolean prevDiagonals = (prevXPos + prevYPos) % 2 == 0;
                if (prevDiagonals) {
                    final List<Vertex<String>> existing = new ArrayList<Vertex<String>>();
                    Vertex<String> nextVertex = verticesAtPositions.get(new Coordinates2D<Integer>(xPos, prevYPos - 1));
                    if (nextVertex != null) {
                        existing.add(nextVertex);
                    }
                    nextVertex =  verticesAtPositions.get(new Coordinates2D<Integer>(xPos, prevYPos));
                    if (nextVertex != null) {
                        existing.add(nextVertex);
                    }
                    nextVertex =  verticesAtPositions.get(new Coordinates2D<Integer>(xPos, prevYPos + 1));
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
                            new FlowAndCapacity(0, GraphAlgorithm.randomEdgeValue(GraphAlgorithm.DEFAULT_EDGE_ROOT))
                        ),
                        nextVertex
                    );
                } else {
                    final Vertex<String> nextVertex =
                        verticesAtPositions.get(new Coordinates2D<Integer>(xPos, prevYPos));
                    if (graph.getEdges(previousVertex, nextVertex).isEmpty()) {
                        graph.addEdge(
                            previousVertex,
                            Optional.of(
                                new FlowAndCapacity(0, GraphAlgorithm.randomEdgeValue(GraphAlgorithm.DEFAULT_EDGE_ROOT))
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
        graph.addVertex(sink);
        verticesAtPositions.put(new Coordinates2D<Integer>(xPos, yPos), sink);
        final List<Vertex<String>> existing = new ArrayList<Vertex<String>>();
        Vertex<String> previousVertex = verticesAtPositions.get(new Coordinates2D<Integer>(xPos - 1, yPos - 1));
        if (previousVertex != null) {
            existing.add(previousVertex);
        }
        previousVertex =  verticesAtPositions.get(new Coordinates2D<Integer>(xPos - 1, yPos));
        if (previousVertex != null) {
            existing.add(previousVertex);
        }
        previousVertex =  verticesAtPositions.get(new Coordinates2D<Integer>(xPos - 1, yPos + 1));
        if (previousVertex != null) {
            existing.add(previousVertex);
        }
        for (final Vertex<String> otherVertex : existing) {
            graph.addEdge(
                otherVertex,
                Optional.of(
                    new FlowAndCapacity(
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
        final GridGraphLayoutBuilder<String, FlowAndCapacity> layoutBuilder =
            GridGraphLayout.<String, FlowAndCapacity>builder().setDirected(true);
        for (final Entry<Coordinates2D<Integer>, Vertex<String>> entry : verticesAtPositions.entrySet()) {
            final Coordinates2D<Integer> key = entry.getKey();
            layoutBuilder.addVertex(entry.getValue(), new Coordinates2D<Integer>(key.x() + xAdd, key.y() - minYPos));
        }
        return new GraphWithLayout<String, FlowAndCapacity, Integer>(graph, layoutBuilder.build());
    }

    private static GraphWithHighlights computeGraphWithFlow(
        final Graph<String, FlowAndCapacity> inputGraph,
        final List<Vertex<String>> flowPath
    ) {
        final Graph<String, FlowAndCapacity> graph =
            inputGraph.copy(pair -> new FlowAndCapacity(pair.flow(), pair.capacity()));
        final Integer min = FordFulkersonAlgorithm.computeMinEdge(graph, flowPath);
        final Iterator<Vertex<String>> it = flowPath.iterator();
        Vertex<String> from;
        Vertex<String> to = it.next();
        final Set<FordFulkersonPathStep<String, FlowAndCapacity>> toHighlight =
            new LinkedHashSet<FordFulkersonPathStep<String, FlowAndCapacity>>();
        while (it.hasNext()) {
            from = to;
            to = it.next();
            int flow = min;
            for (final Edge<FlowAndCapacity, String> edge : graph.getEdges(from, to)) {
                final int added = Math.min(flow, edge.label().get().capacity() - edge.label().get().flow());
                if (added > 0) {
                    flow -= added;
                    final FlowAndCapacity currentLabel = edge.label().get();
                    graph.replaceEdgeLabel(
                        from,
                        new FlowAndCapacity(currentLabel.flow() + added, currentLabel.capacity()),
                        to
                    );
                    toHighlight.add(
                        new FordFulkersonPathStep<String, FlowAndCapacity>(
                            from,
                            graph.getEdges(from, to).iterator().next()
                        )
                    );
                }
            }
            for (final Edge<FlowAndCapacity, String> edge : graph.getEdges(to, from)) {
                final int added = Math.min(flow, edge.label().get().flow());
                if (added > 0) {
                    flow -= added;
                    final FlowAndCapacity currentLabel = edge.label().get();
                    graph.replaceEdgeLabel(
                        to,
                        new FlowAndCapacity(currentLabel.flow() - added, currentLabel.capacity()),
                        from
                    );
                    toHighlight.add(
                        new FordFulkersonPathStep<String, FlowAndCapacity>(
                            to,
                            graph.getEdges(to, from).iterator().next()
                        )
                    );
                }
            }
            if (flow > 0) {
                throw new IllegalStateException("Could not add flow!");
            }
        }
        return new GraphWithHighlights(graph, toHighlight);
    }

    private static <V extends Comparable<V>> Integer computeMinEdge(
        final Graph<V, FlowAndCapacity> graph,
        final List<Vertex<V>> path
    ) {
        Integer min = null;
        final Iterator<Vertex<V>> it = path.iterator();
        Vertex<V> from;
        Vertex<V> to = it.next();
        while (it.hasNext()) {
            from = to;
            to = it.next();
            int flow = 0;
            for (final Edge<FlowAndCapacity, V> edge : graph.getEdges(from, to)) {
                flow += edge.label().get().capacity() - edge.label().get().flow();
            }
            for (final Edge<FlowAndCapacity, V> edge : graph.getEdges(to, from)) {
                flow += edge.label().get().flow();
            }
            if (min == null || min > flow) {
                min = flow;
            }
        }
        return min;
    }

    private static Pair<Integer, Integer> computeNumberOfNewAndCopiedFlowValues(
        final List<FordFulkersonDoubleStep> solution
    ) {
        int copiedValues = 0;
        int newValues = 0;
        for (int i = 1; i < solution.size(); i++) {
            final FordFulkersonDoubleStep step = solution.get(i);
            final FordFulkersonDoubleStep previousStep = solution.get(i - 1);
            final Graph<String, FlowAndCapacity> network = step.flowNetworkWithLayout().graph();
            final Graph<String, FlowAndCapacity> previousNetwork = previousStep.flowNetworkWithLayout().graph();
            final Set<Vertex<String>> vertices = network.getVertices();
            for (final Vertex<String> from : vertices) {
                for (final Vertex<String> to : network.getAdjacentVertices(from)) {
                    final Edge<FlowAndCapacity, String> edge = network.getEdges(from, to).iterator().next();
                    final Edge<FlowAndCapacity, String> previousEdge =
                        previousNetwork.getEdges(from, to).iterator().next();
                    if (edge.logicallyEquals(previousEdge)) {
                        copiedValues++;
                    } else {
                        newValues++;
                    }
                }
            }
        }
        return new Pair<Integer, Integer>(newValues, copiedValues);
    }

    private static int computeNumberOfResidualEdges(final List<FordFulkersonDoubleStep> solution) {
        return solution.stream()
            .flatMap(step ->
                step.residualGraphWithLayout()
                .graph()
                .getEdges()
                .entrySet()
                .stream()
                .map(pair -> pair.getValue().size())
            ).reduce(0, Integer::sum);
    };

    private static <V extends Comparable<V>> Graph<V, Integer> computeResidualGraph(
        final Graph<V, FlowAndCapacity> graph
    ) {
        final Graph<V, Integer> res = new Graph<V, Integer>();
        for (final Vertex<V> vertex : graph.getVertices()) {
            res.addVertex(vertex);
            final Set<Edge<FlowAndCapacity, V>> set = graph.getAdjacencySet(vertex);
            if (set == null) {
                continue;
            }
            for (final Edge<FlowAndCapacity, V> edge : set) {
                final Vertex<V> target = edge.to();
                final Integer back = edge.label().get().flow();
                if (back > 0) {
                    final Set<Edge<Integer, V>> backEdges = res.getEdges(target, vertex);
                    if (backEdges.isEmpty()) {
                        res.addEdge(target, Optional.of(back), vertex);
                    } else {
                        final Edge<Integer, V> backEdge = backEdges.iterator().next();
                        res.replaceEdgeLabel(target, backEdge.label().get() + back, vertex);
                    }
                }
                final Integer forth = edge.label().get().capacity() - back;
                if (forth > 0) {
                    final Set<Edge<Integer, V>> forthEdges = res.getEdges(vertex, target);
                    if (forthEdges.isEmpty()) {
                        res.addEdge(vertex, Optional.of(forth), target);
                    } else {
                        final Edge<Integer, V> forthEdge = forthEdges.iterator().next();
                        res.replaceEdgeLabel(vertex, forthEdge.label().get() + forth, target);
                    }
                }
            }
        }
        return res;
    }

    private static boolean enoughVerticesForLevel(
        final int remainingVertices,
        final int minYPosOfLevel,
        final int maxYPosOfLevel,
        final int xPosOfLevel
    ) {
        int neededVertices = maxYPosOfLevel - minYPosOfLevel + 1;
        int itMinY = minYPosOfLevel;
        int itMaxY = maxYPosOfLevel;
        int itX = xPosOfLevel;
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
    }

    private static FordFulkersonConfiguration parseOrGenerateConfiguration(
        final FlowNetworkProblem problem,
        final Parameters<Flag> options
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

    private static GraphWithLayout<String, FlowAndCapacity, Integer> parsePositionedGraph(
        final BufferedReader reader
    ) throws IOException {
        final AdjacencySets<String, FlowAndCapacity> adjacencySets = new AdjacencySets<String, FlowAndCapacity>();
        final GridGraphLayoutBuilder<String, FlowAndCapacity> layoutBuilder =
            GridGraphLayout.<String, FlowAndCapacity>builder().setDirected(true);
        final Map<String, Vertex<String>> nodeMap = new LinkedHashMap<String, Vertex<String>>();
        final String[] nodes = reader.readLine().split(";");
        for (final String node : nodes) {
            final String[] nodeParts = node.split(",");
            final Vertex<String> vertex = new Vertex<String>(nodeParts[0]);
            adjacencySets.put(vertex, new LinkedHashSet<Edge<FlowAndCapacity, String>>());
            layoutBuilder.addVertex(
                vertex,
                new Coordinates2D<Integer>(Integer.parseInt(nodeParts[1]), Integer.parseInt(nodeParts[2]))
            );
            nodeMap.put(nodeParts[0], vertex);
        }
        final String[] edges = reader.readLine().split(";");
        for (final String edge : edges) {
            final String[] edgeParts = edge.split(",");
            adjacencySets.addEdge(
                nodeMap.get(edgeParts[0]),
                new FlowAndCapacity(0, Integer.parseInt(edgeParts[2])),
                nodeMap.get(edgeParts[1])
            );
        }
        return new GraphWithLayout<String, FlowAndCapacity, Integer>(
            Graph.create(adjacencySets),
            layoutBuilder.build()
        );
    }

    private static void printFordFulkersonDoubleStep(
        final int stepNumber,
        final FordFulkersonDoubleStep step,
        final boolean first,
        final boolean solution,
        final SolutionSpaceMode mode,
        final FordFulkersonConfiguration configuration,
        final BufferedWriter writer
    ) throws IOException {
        if (!solution && mode == SolutionSpaceMode.NEVER) {
            return;
        }
        if (!first) {
            LaTeXUtils.printSamePageBeginning(
                stepNumber,
                configuration.twoColumns ? LaTeXUtils.TWO_COL_WIDTH : LaTeXUtils.LINE_WIDTH,
                Optional.of("n\\\"achstes Flussnetzwerk mit aktuellem Fluss"),
                writer
            );
            GridGraphLayout<String, FlowAndCapacity> layout =
                GraphAlgorithm.stretch(
                    (GridGraphLayout<String, FlowAndCapacity>)step.flowNetworkWithLayout().layout(),
                    configuration.multiplier()
                );
            if (solution) {
                layout =
                    layout.highlight(
                        step.flowHighlights()
                        .stream()
                        .map(s -> new Pair<Vertex<String>, Edge<FlowAndCapacity, String>>(s.startNode(), s.edge()))
                        .toList()
                    );
            } else {
                layout = layout.setDrawEdgeLabels(false);
            }
            step.flowNetworkWithLayout().graph().printTikZ(layout, writer);
            LaTeXUtils.printSamePageEnd(writer);
            if (configuration.twoColumns) {
                writer.write("\\\\");
            }
            Main.newLine(writer);
        }
        LaTeXUtils.printSamePageBeginning(
            first ? stepNumber : stepNumber + 1,
            configuration.twoColumns ? LaTeXUtils.TWO_COL_WIDTH : LaTeXUtils.LINE_WIDTH,
            Optional.of(FordFulkersonAlgorithm.RESIDUAL_GRAPH_NAME),
            writer
        );
        GridGraphLayout<String, Integer> layout =
            GraphAlgorithm.stretch(
                (GridGraphLayout<String, Integer>)step.residualGraphWithLayout().layout(),
                configuration.multiplier()
            );
        if (solution) {
            layout =
                layout.highlight(
                    FordFulkersonAlgorithm.filterHighLights(step.residualHighlights())
                    .stream()
                    .map(s -> new Pair<Vertex<String>, Edge<Integer, String>>(s.startNode(), s.edge()))
                    .toList()
                );
        } else {
            layout = layout.setDrawEdges(false);
        }
        step.residualGraphWithLayout().graph().printTikZ(layout, writer);
        LaTeXUtils.printSamePageEnd(writer);
        if (configuration.twoColumns) {
            writer.write(" & ");
        } else {
            Main.newLine(writer);
        }
    }

    private static void printStatisticsAsComment(
        final List<FordFulkersonDoubleStep> solution,
        final BufferedWriter writer
    ) throws IOException {
        final Pair<Integer, Integer> numFlowValues =
            FordFulkersonAlgorithm.computeNumberOfNewAndCopiedFlowValues(solution);
        final int numResidualEdges = FordFulkersonAlgorithm.computeNumberOfResidualEdges(solution);
        LaTeXUtils.printCommentLine("Anzahl kopierter Flusswerte", String.valueOf(numFlowValues.y), writer);
        LaTeXUtils.printCommentLine("Anzahl neuer Flusswerte", String.valueOf(numFlowValues.x), writer);
        LaTeXUtils.printCommentLine("Anzahl Kanten in Restnetzwerken", String.valueOf(numResidualEdges), writer);
    }

    private static <V extends Comparable<V>> List<Vertex<V>> selectAugmentingPath(
        final Graph<V, Integer> residualGraph,
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
            final Set<Edge<Integer, V>> set = residualGraph.getAdjacencySet(path.get(path.size() - 1));
            if (set == null) {
                continue;
            }
            for (final Edge<Integer, V> edge : set) {
                if (visited.contains(edge.to())) {
                    continue;
                }
                final List<Vertex<V>> newPath = new ArrayList<Vertex<V>>(path);
                newPath.add(edge.to());
                if (sink.equals(edge.to())) {
                    return newPath;
                }
                visited.add(edge.to());
                queue.add(newPath);
            }
        }
        return null;
    }

    private static <V extends Comparable<V>> Set<FordFulkersonPathStep<V, Integer>> toEdges(
        final Graph<V, Integer> residualGraph,
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
            for (final Edge<Integer, V> edge : residualGraph.getAdjacencySet(cur)) {
                if (edge.to().equals(next)) {
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
        Graph<String, FlowAndCapacity> graph = problem.graphWithLayout().graph();
        final GridGraphLayout<String, FlowAndCapacity> layout =
            (GridGraphLayout<String, FlowAndCapacity>)problem.graphWithLayout().layout();
        final GridGraphLayout<String, Integer> residualLayout = layout.convertEdgeLabelType();
        final Vertex<String> source = problem.source();
        final Vertex<String> sink = problem.sink();
        Graph<String, Integer> residualGraph = FordFulkersonAlgorithm.computeResidualGraph(graph);
        List<Vertex<String>> path = FordFulkersonAlgorithm.selectAugmentingPath(residualGraph, source, sink);
        final List<FordFulkersonDoubleStep> result = new LinkedList<FordFulkersonDoubleStep>();
        result.add(
            new FordFulkersonDoubleStep(
                problem.graphWithLayout(),
                Collections.emptySet(),
                new GraphWithLayout<String, Integer, Integer>(residualGraph, residualLayout),
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
                    new GraphWithLayout<String, FlowAndCapacity, Integer>(graph, layout),
                    graphWithAddedFlow.highlights(),
                    new GraphWithLayout<String, Integer, Integer>(residualGraph, residualLayout),
                    FordFulkersonAlgorithm.toEdges(residualGraph, path)
                )
            );
        }
        return result;
    }

    @Override
    public String commandPrefix() {
        return "FordFulkerson";
    }

    @Override
    public FlowNetworkProblem generateProblem(final Parameters<Flag> options) {
        final int numOfInnerVertices = AlgorithmImplementation.parseOrGenerateLength(3, 18, options);
        final GraphWithLayout<String, FlowAndCapacity, Integer> graphWithLayout =
            FordFulkersonAlgorithm.createRandomFlowNetwork(numOfInnerVertices);
        return new FlowNetworkProblem(
            graphWithLayout,
            graphWithLayout.graph().getVerticesWithLabel("s").iterator().next(),
            graphWithLayout.graph().getVerticesWithLabel("t").iterator().next()
        );
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public List<FlowNetworkProblem> parseProblems(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        final GraphWithLayout<String, FlowAndCapacity, Integer> graphWithLayout =
            options.containsKey(Flag.VARIANT) && options.getAsInt(Flag.VARIANT) == 1 ?
                FordFulkersonAlgorithm.parsePositionedGraph(reader) :
                    Graph.create(reader, new StringLabelParser(), new FlowAndCapacityLabelParser());
        final Graph<String, FlowAndCapacity> graph = graphWithLayout.graph();
        Vertex<String> source = graph.getVerticesWithLabel("s").iterator().next();
        Vertex<String> sink = graph.getVerticesWithLabel("t").iterator().next();
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
        return List.of(new FlowNetworkProblem(graphWithLayout, source, sink));
    }

    @Override
    public void printAfterSingleProblemInstance(
        final FlowNetworkProblem problem,
        final List<FordFulkersonDoubleStep> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("Berechnen Sie den maximalen Fluss in diesem Netzwerk mithilfe der ");
        writer.write("\\emphasize{Ford-Fulkerson Methode}. Geben Sie dazu ");
        writer.write("\\emphasize{jedes Restnetzwerk (auch das initiale)} ");
        writer.write("sowie \\emphasize{nach jeder Augmentierung} den aktuellen Zustand des Flussnetzwerks an. ");
        writer.write("Geben Sie au\\ss{}erdem den \\emphasize{Wert des maximalen Flusses} an.");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<FlowNetworkProblem> problems,
        final List<List<FordFulkersonDoubleStep>> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Berechnen Sie den maximalen Fluss in den folgenden \\emphasize{Flussnetzwerken} mithilfe der ");
        writer.write("\\emphasize{Ford-Fulkerson Methode}. Geben Sie dazu ");
        writer.write("\\emphasize{jedes Restnetzwerk (auch das jeweils initiale)} sowie ");
        writer.write("\\emphasize{nach jeder Augmentierung} den aktuellen Zustand des jeweiligen Flussnetzwerks an. ");
        writer.write("Geben Sie au\\ss{}erdem jeweils den \\emphasize{Wert des maximalen Flusses} an.");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeSingleProblemInstance(
        final FlowNetworkProblem problem,
        final List<FordFulkersonDoubleStep> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Betrachten Sie das folgende \\emphasize{Flussnetzwerk}:\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printProblemInstance(
        final FlowNetworkProblem problem,
        final List<FordFulkersonDoubleStep> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        final FordFulkersonConfiguration configuration =
            FordFulkersonAlgorithm.parseOrGenerateConfiguration(problem, options);
        LaTeXUtils.printAdjustboxBeginning(writer);
        problem.graphWithLayout().graph().printTikZ(
            GraphAlgorithm.stretch(
                (GridGraphLayout<String, FlowAndCapacity>)problem.graphWithLayout().layout(),
                configuration.multiplier()
            ),
            writer
        );
        LaTeXUtils.printAdjustboxEnd(writer);
        Main.newLine(writer);
        writer.write("Quelle: ");
        writer.write(problem.source().label().get());
        writer.write("\\\\");
        Main.newLine(writer);
        writer.write("Senke: ");
        writer.write(problem.sink().label().get());
        writer.write("\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printSolutionInstance(
        final FlowNetworkProblem problem,
        final List<FordFulkersonDoubleStep> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        final SolutionSpaceMode mode = SolutionSpaceMode.parsePreprintMode(options);
        final FordFulkersonConfiguration configuration =
            FordFulkersonAlgorithm.parseOrGenerateConfiguration(problem, options);
        int flow = 0;
        final Graph<String, FlowAndCapacity> graph = solution.getLast().flowNetworkWithLayout().graph();
        final Set<Edge<FlowAndCapacity, String>> set = graph.getAdjacencySet(problem.source());
        if (set != null) {
            for (final Edge<FlowAndCapacity, String> edge : set) {
                flow += edge.label().get().flow();
            }
        }
        if (configuration.twoColumns) {
            writer.write("\\begin{longtable}{cc}");
            Main.newLine(writer);
        }
        int stepNumber = 1;
        boolean first = true;
        final int[] pagebreakCounters =
            LaTeXUtils.parsePagebreakCountersForSolution(options.getOrDefault(Flag.KEYVALUE, ""));
        int doubleSteps = 0;
        int counterIndex = 0;
        for (final FordFulkersonDoubleStep step : solution) {
            if (counterIndex < pagebreakCounters.length && doubleSteps >= pagebreakCounters[counterIndex]) {
                writer.write("\\newpage");
                Main.newLine(writer);
                Main.newLine(writer);
                doubleSteps = 0;
                counterIndex++;
            }
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
            doubleSteps++;
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
        FordFulkersonAlgorithm.printStatisticsAsComment(solution, writer);
    }

    @Override
    public void printSolutionSpace(
        final FlowNetworkProblem problem,
        final List<FordFulkersonDoubleStep> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        final SolutionSpaceMode mode = SolutionSpaceMode.parsePreprintMode(options);
        final FordFulkersonConfiguration configuration =
            FordFulkersonAlgorithm.parseOrGenerateConfiguration(problem, options);
        switch (mode) {
        case ALWAYS:
        case SOLUTION_SPACE:
            writer.write(
                "Die vorgegebene Anzahl an L\\\"osungsschritten muss nicht mit der ben\\\"otigten Anzahl "
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
            if (mode == SolutionSpaceMode.SOLUTION_SPACE) {
                LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, writer);
            }
            break;
        case NEVER:
            // do nothing
        }
    }

}
