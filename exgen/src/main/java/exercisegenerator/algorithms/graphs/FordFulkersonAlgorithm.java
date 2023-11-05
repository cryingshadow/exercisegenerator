package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;
import java.util.Map.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

public class FordFulkersonAlgorithm implements AlgorithmImplementation {

    public static final FordFulkersonAlgorithm INSTANCE = new FordFulkersonAlgorithm();

    /**
     * The default value being probably close to the edge values adjacent to source/sink vertices.
     */
    static final int DEFAULT_SOURCE_SINK_ROOT = 7;

    private static final String EACH_RESIDUAL_GRAPH = "\\emphasize{jedes Restnetzwerk (auch das initiale)}";

    private static final String RESIDUAL_GRAPH_NAME = "Restnetzwerk";

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
        final Grid<String> grid = new Grid<String>();
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
                        if (FordFulkersonAlgorithm.enoughVertices(remainingVertices, prevMinYPos - 1, prevMaxYPos + 1, xPos)) {
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
                                FordFulkersonAlgorithm.enoughVertices(remainingVertices, prevMinYPos, prevMaxYPos + 1, xPos);
                            final boolean justExpandMin =
                                FordFulkersonAlgorithm.enoughVertices(remainingVertices, prevMinYPos - 1, prevMaxYPos, xPos);
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
                            } else if (FordFulkersonAlgorithm.enoughVertices(remainingVertices, prevMinYPos, prevMaxYPos, xPos)) {
                                options.add(keepMin + reduceMax);
                                options.add(reduceMin + keepMax);
                                options.add(keepMin + keepMax);
                                options.add(keepMin + keepMax);
                            } else {
                                if (FordFulkersonAlgorithm.enoughVertices(remainingVertices, prevMinYPos + 1, prevMaxYPos, xPos)) {
                                    options.add(reduceMin + keepMax);
                                }
                                if (FordFulkersonAlgorithm.enoughVertices(remainingVertices, prevMinYPos, prevMaxYPos - 1, xPos)) {
                                    options.add(keepMin + reduceMax);
                                }
                            }
                        }
                    } else {
                        options.add(reduceMin + keepMax);
                        if (FordFulkersonAlgorithm.enoughVertices(remainingVertices, prevMinYPos - 1, prevMaxYPos, xPos)) {
                            options.add(keepMin + keepMax);
                            options.add(keepMin + keepMax);
                            options.add(expandMin + keepMax);
                            options.add(expandMin + keepMax);
                            options.add(expandMin + keepMax);
                        } else if (FordFulkersonAlgorithm.enoughVertices(remainingVertices, prevMinYPos, prevMaxYPos, xPos)) {
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
                    } else if (FordFulkersonAlgorithm.enoughVertices(remainingVertices, prevMinYPos, prevMaxYPos, xPos)) {
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
                                    FordFulkersonAlgorithm.DEFAULT_SOURCE_SINK_ROOT :
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
                                    FordFulkersonAlgorithm.DEFAULT_SOURCE_SINK_ROOT :
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
                new FlowPair(0, GraphAlgorithms.randomEdgeValue(gen, FordFulkersonAlgorithm.DEFAULT_SOURCE_SINK_ROOT)),
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
        final Parameters options,
        final BufferedWriter exWriter,
        final BufferedWriter solWriter
    ) throws IOException {
        final PreprintMode mode = PreprintMode.parsePreprintMode(options);
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
        exWriter.write(FordFulkersonAlgorithm.EACH_RESIDUAL_GRAPH);
        exWriter.write(" sowie \\emphasize{nach jeder Augmentierung} den aktuellen Zustand des Flussnetzwerks an. ");
        exWriter.write("Geben Sie au\\ss{}erdem den \\emphasize{Wert des maximalen Flusses} an.");
        switch (mode) {
            case ALWAYS:
            case SOLUTION_SPACE:
                exWriter.write(
                    " Die vorgegebene Anzahl an L\\\"osungsschritten muss nicht mit der ben\\\"otigten Anzahl "
                );
                exWriter.write("solcher Schritte \\\"ubereinstimmen.\\\\[2ex]");
                break;
            case NEVER:
                // do nothing
        }
        Main.newLine(exWriter);
        int step = 1;
        switch (mode) {
            case SOLUTION_SPACE:
                LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, exWriter);
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
            final Graph<V, Integer> residualGraph = FordFulkersonAlgorithm.computeResidualGraph(graph);
            final List<Vertex<V>> path = FordFulkersonAlgorithm.selectAugmentingPath(residualGraph, source, sink);
            switch (mode) {
                case ALWAYS:
                case SOLUTION_SPACE:
                    LaTeXUtils.printSamePageBeginning(
                        step,
                        twocolumns ? LaTeXUtils.TWO_COL_WIDTH : LaTeXUtils.COL_WIDTH,
                        exWriter
                    );
                    exWriter.write(FordFulkersonAlgorithm.RESIDUAL_GRAPH_NAME);
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
            solWriter.write(FordFulkersonAlgorithm.RESIDUAL_GRAPH_NAME);
            solWriter.write(":\\\\[2ex]");
            Main.newLine(solWriter);
            final Set<Pair<Vertex<V>, Edge<Integer, V>>> toHighlightResidual;
            switch (Main.TEXT_VERSION) {
                case ABRAHAM:
                    toHighlightResidual = FordFulkersonAlgorithm.toEdges(residualGraph, path);
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
                FordFulkersonAlgorithm.addFlow(graph, path);
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
                flow += edge.label.x;
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
                    LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, exWriter);
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
        final Integer min = FordFulkersonAlgorithm.computeMinEdge(graph, path);
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
                final int added = Math.min(flow, edge.label.y - edge.label.x);
                if (added > 0) {
                    flow -= added;
                    edge.label.x += added;
                    toHighlight.add(new Pair<Vertex<V>, Edge<FlowPair, V>>(from, edge));
                }
            }
            for (final Edge<FlowPair, V> edge : graph.getEdges(to, from)) {
                final int added = Math.min(flow, edge.label.x);
                if (added > 0) {
                    flow -= added;
                    edge.label.x -= added;
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
                flow += edge.label.y - edge.label.x;
            }
            for (final Edge<FlowPair, V> edge : graph.getEdges(to, from)) {
                flow += edge.label.x;
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
                final Vertex<V> target = edge.to;
                final Integer back = edge.label.x;
                if (back > 0) {
                    final Set<Edge<Integer, V>> backEdges = res.getEdges(target, vertex);
                    if (backEdges.isEmpty()) {
                        res.addEdge(target, back, vertex);
                    } else {
                        final Edge<Integer, V> backEdge = backEdges.iterator().next();
                        res.replaceEdgeLabel(target, backEdge.label + back, vertex);
                    }
                }
                final Integer forth = edge.label.y - back;
                if (forth > 0) {
                    final Set<Edge<Integer, V>> forthEdges = res.getEdges(vertex, target);
                    if (forthEdges.isEmpty()) {
                        res.addEdge(vertex, forth, target);
                    } else {
                        final Edge<Integer, V> forthEdge = forthEdges.iterator().next();
                        res.replaceEdgeLabel(vertex, forthEdge.label + forth, target);
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

    private static FlowNetworkInput<String, FlowPair> generateFlowNetwork(final Parameters options) {
        final Random gen = new Random();
        final int numOfVertices;
        if (options.containsKey(Flag.LENGTH)) {
            numOfVertices = Integer.parseInt(options.get(Flag.LENGTH));
        } else {
            numOfVertices = gen.nextInt(16) + 3;
        }
        final FlowNetworkInput<String, FlowPair> res = new FlowNetworkInput<String, FlowPair>();
        res.graph = FordFulkersonAlgorithm.createRandomFlowNetwork(gen, numOfVertices);
        res.source = res.graph.getVerticesWithLabel("s").iterator().next();
        res.sink = res.graph.getVerticesWithLabel("t").iterator().next();
        res.multiplier = 1.0;
        res.twocolumns = false;
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

    private static FlowNetworkInput<String, FlowPair> parseOrGenerateFlowNetwork(final Parameters options)
    throws IOException {
        return new ParserAndGenerator<FlowNetworkInput<String, FlowPair>>(
            FordFulkersonAlgorithm::parseFlowNetwork,
            FordFulkersonAlgorithm::generateFlowNetwork
        ).getResult(options);
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
                if (edge.to.equals(next)) {
                    res.add(new Pair<Vertex<V>, Edge<Integer, V>>(cur, edge));
                    break;
                }
            }
            cur = next;
        }
        return res;
    }

    private FordFulkersonAlgorithm() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final FlowNetworkInput<String, FlowPair> flow =
            FordFulkersonAlgorithm.parseOrGenerateFlowNetwork(input.options);
        FordFulkersonAlgorithm.fordFulkerson(
            flow.graph,
            flow.source,
            flow.sink,
            flow.multiplier,
            flow.twocolumns,
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
