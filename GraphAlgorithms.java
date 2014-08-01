import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * Class offering methods for graph algorithms.
 * @author Thomas Stroeder
 * @version 1.0.1
 */
public abstract class GraphAlgorithms {

    /**
     * The phrase "each residual graph".
     */
    private static final String EACH_RESIDUAL_GRAPH = "\\emphasize{jedes Restnetzwerk (auch das initiale)}";

    /**
     * The name of a residual graph.
     */
    private static final String RESIDUAL_GRAPH = "Restnetzwerk";

    /**
     * The default value being probably close to the edge values.
     */
    private static final int DEFAULT_EDGE_ROOT = 3;

    /**
     * The default value being probably close to the edge values adjacent to source/sink nodes.
     */
    private static final int DEFAULT_SOURCE_SINK_ROOT = 7;

    /**
     * @param gen A random number generator.
     * @param numOfNodes The number of nodes in the returned graph.
     * @param undirected Should the graph be undirected?
     * @return A random graph with <code>numOfNodes</code> nodes labeled with Strings (each node has a unique label 
     *         and there is a node with label A) and edges labeled with Integers.
     */
    public static Graph<String, Integer> createRandomGraph(Random gen, int numOfNodes, boolean undirected) {
        if (numOfNodes < 0) {
            throw new IllegalArgumentException("Number of nodes must not be negative!");
        }
        Graph<String, Integer> graph = new Graph<String, Integer>();
        Map<Pair<Integer, Integer>, Node<String>> grid = new LinkedHashMap<Pair<Integer, Integer>, Node<String>>();
        if (numOfNodes == 0) {
            graph.setGrid(grid);
            return graph;
        }
        Map<Node<String>, NodeGridPosition> positions = new LinkedHashMap<Node<String>, NodeGridPosition>();
        Node<String> start = new Node<String>("A");
        Pair<Integer, Integer> startPos = new Pair<Integer, Integer>(0, 0);
        boolean startDiagonal = gen.nextBoolean();
        GraphAlgorithms.addNode(
            start,
            graph,
            new Pair<Pair<Integer, Integer>, Boolean>(startPos, startDiagonal),
            grid,
            positions
        );
        List<Node<String>> nodesWithFreeNeighbors = new ArrayList<Node<String>>();
        nodesWithFreeNeighbors.add(start);
        for (int letter = 1; letter < numOfNodes; letter++) {
            Node<String> nextNode = nodesWithFreeNeighbors.get(gen.nextInt(nodesWithFreeNeighbors.size()));
            NodeGridPosition nextPos = positions.get(nextNode);
            Pair<Pair<Integer, Integer>, Boolean> toAddPos = nextPos.randomFreePosition(gen);
            Node<String> toAddNode = new Node<String>(GraphAlgorithms.toStringLabel(letter));
            NodeGridPosition gridPos = GraphAlgorithms.addNode(toAddNode, graph, toAddPos, grid, positions);
            int value = GraphAlgorithms.randomEdgeValue(gen, GraphAlgorithms.DEFAULT_EDGE_ROOT);
            graph.addEdge(nextNode, value, toAddNode);
            if (undirected) {
                graph.addEdge(toAddNode, value, nextNode);
            }
            List<Pair<Pair<Integer, Integer>, Boolean>> existing = gridPos.getExistingPositions();
            List<Pair<Node<String>, Node<String>>> freeNodePairs = new ArrayList<Pair<Node<String>, Node<String>>>();
            for (Pair<Pair<Integer, Integer>, Boolean> other : existing) {
                Node<String> otherNode = grid.get(other.x);
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
                int pairIndex = gen.nextInt(freeNodePairs.size());
                Pair<Node<String>, Node<String>> pair = freeNodePairs.remove(pairIndex);
                int nextValue = GraphAlgorithms.randomEdgeValue(gen, GraphAlgorithms.DEFAULT_EDGE_ROOT);
                graph.addEdge(pair.x, nextValue, pair.y);
                if (undirected) {
                    graph.addEdge(pair.y, nextValue, pair.x);
                }
            }
            for (Pair<Pair<Integer, Integer>, Boolean> neighborPos : existing) {
                Node<String> neighborNode = grid.get(neighborPos.x);
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
        for (Pair<Integer, Integer> pair : grid.keySet()) {
            minX = Math.min(minX, pair.x);
            minY = Math.min(minY, pair.y);
        }
        if (
            (startDiagonal && (startPos.x - minX + startPos.y - minY) % 2 == 1)
            || (!startDiagonal && (startPos.x - minX + startPos.y - minY) % 2 == 0)
        ) {
            minX--;
        }
        Map<Pair<Integer, Integer>, Node<String>> newGrid = new LinkedHashMap<Pair<Integer, Integer>, Node<String>>();
        for (Entry<Pair<Integer, Integer>, Node<String>> entry : grid.entrySet()) {
            Pair<Integer, Integer> key = entry.getKey();
            newGrid.put(new Pair<Integer, Integer>(key.x - minX, key.y - minY), entry.getValue());
        }
        graph.setGrid(newGrid);
        return graph;
    }

    /**
     * @param gen A random number generator.
     * @param numOfNodes The number of nodes (excluding source and sink) in the returned flow network.
     * @return A random flow network with <code>numOfNodes</code> nodes labeled with Strings (each node has a unique 
     *         label and the source is labeled with s and the sink is labeled with t) and edges labeled with pairs of 
     *         Integers (the current flow and the capacity - the current flow will be set to 0).
     */
    public static Graph<String, FlowPair> createRandomFlowNetwork(Random gen, int numOfNodes) {
        if (numOfNodes < 0) {
            throw new IllegalArgumentException("Number of nodes must not be negative!");
        }
        Graph<String, FlowPair> graph = new Graph<String, FlowPair>();
        Map<Pair<Integer, Integer>, Node<String>> grid = new LinkedHashMap<Pair<Integer, Integer>, Node<String>>();
        Node<String> source = new Node<String>("s");
        Map<Node<String>, NodeGridPosition> positions = new LinkedHashMap<Node<String>, NodeGridPosition>();
        Pair<Integer, Integer> startPos = new Pair<Integer, Integer>(0, 0);
        GraphAlgorithms.addNode(
            source,
            graph,
            new Pair<Pair<Integer, Integer>, Boolean>(startPos, true),
            grid,
            positions
        );
        if (numOfNodes == 0) {
            Node<String> sink = new Node<String>("t");
            GraphAlgorithms.addNode(
                sink,
                graph,
                new Pair<Pair<Integer, Integer>, Boolean>(new Pair<Integer, Integer>(1, 0), false),
                grid,
                positions
            );
            int value = GraphAlgorithms.randomEdgeValue(gen, GraphAlgorithms.DEFAULT_EDGE_ROOT);
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
                List<Integer> options = new ArrayList<Integer>();
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
                Node<String> node = new Node<String>(GraphAlgorithms.toStringLabel(letter++));
                boolean hasDiagonals = (xPos + yPos) % 2 == 0;
                Pair<Integer, Integer> pos = new Pair<Integer, Integer>(xPos, yPos);
                GraphAlgorithms.addNode(
                    node,
                    graph,
                    new Pair<Pair<Integer, Integer>, Boolean>(pos, hasDiagonals),
                    grid,
                    positions
                );
                if (hasDiagonals) {
                    List<Node<String>> existing = new ArrayList<Node<String>>();
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
                    int index = gen.nextInt(existing.size());
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
                    for (Node<String> otherNode : existing) {
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
                    Node<String> north = grid.get(new Pair<Integer, Integer>(xPos, yPos - 1));
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
                Node<String> prevNode = grid.get(new Pair<Integer, Integer>(prevXPos, prevYPos));
                boolean prevDiagonals = (prevXPos + prevYPos) % 2 == 0;
                if (prevDiagonals) {
                    List<Node<String>> existing = new ArrayList<Node<String>>();
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
                    for (Node<String> otherNode : existing) {
                        if (!graph.getEdges(prevNode, otherNode).isEmpty()) {
                            continue outer;
                        }
                    }
                    int index = gen.nextInt(existing.size());
                    nextNode = existing.remove(index);
                    graph.addEdge(
                        prevNode,
                        new FlowPair(0, GraphAlgorithms.randomEdgeValue(gen, GraphAlgorithms.DEFAULT_EDGE_ROOT)),
                        nextNode
                    );
                } else {
                    Node<String> nextNode = grid.get(new Pair<Integer, Integer>(xPos, prevYPos));
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
        Node<String> sink = new Node<String>("t");
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
        List<Node<String>> existing = new ArrayList<Node<String>>();
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
        for (Node<String> otherNode : existing) {
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
        Map<Pair<Integer, Integer>, Node<String>> newGrid = new LinkedHashMap<Pair<Integer, Integer>, Node<String>>();
        for (Entry<Pair<Integer, Integer>, Node<String>> entry : grid.entrySet()) {
            Pair<Integer, Integer> key = entry.getKey();
            newGrid.put(new Pair<Integer, Integer>(key.x + xAdd, key.y - minYPos), entry.getValue());
        }
        graph.setGrid(newGrid);
        return graph;
    }

    /**
     * @param remainingNodes The number of nodes yet to be added.
     * @param minYPos The minimal y position in the current level.
     * @param maxYPos The maximal y position in the current level.
     * @param xPos The x position of the current level.
     * @return True if there are at least as many nodes to be added as minimally needed when starting the current level 
     *         with the specified parameters (by reducing whenever possible). False otherwise. 
     */
    private static boolean enoughNodes(int remainingNodes, int minYPos, int maxYPos, int xPos) {
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

    /**
     * Prints exercise and solution for the Dijkstra Algorithm.
     * @param graph The graph.
     * @param start The start node.
     * @param comp A comparator for sorting the nodes in the table (may be null - then no sorting is applied).
     * @param mode The preprint mode.
     * @param exWriter The writer to send the exercise output to.
     * @param solWriter The writer to send the solution output to.
     * @throws IOException If some error occurs during output.
     */
    public static <N> void dijkstra(
        Graph<N, Integer> graph,
        Node<N> start,
        Comparator<Node<N>> comp,
        PreprintMode mode,
        BufferedWriter exWriter,
        BufferedWriter solWriter
    ) throws IOException {
        final List<Node<N>> nodes = new ArrayList<Node<N>>(graph.getNodes());
        if (comp != null) {
            Collections.sort(nodes, comp);
        }
        final int size = nodes.size();
        final String[][] exTable;
        final String[][] solTable;
        final String[][] exColor;
        final String[][] solColor;
        Integer[] distances = new Integer[size];
        Map<Node<N>, Integer> ids = new LinkedHashMap<Node<N>, Integer>();
        int i = 1;
        int current = 0;
        Set<Integer> used = new LinkedHashSet<Integer>();
        switch (DSALExercises.TEXT_VERSION) {
            case ABRAHAM:
                exTable = new String[size][size + 1];
                solTable = new String[size][size + 1];
                exColor = new String[size][size + 1];
                solColor = new String[size][size + 1];
                solTable[0][0] = "\\texttt{v}";
                exTable[0][0] = solTable[0][0];
                for (Node<N> node : nodes) {
                    if (!node.equals(start)) {
                        solTable[0][i + 1] = "\\texttt{key[}" + node.getLabel().toString() + "\\texttt{]}";
                        exTable[0][i + 1] = solTable[0][i + 1];
                        ids.put(node, i);
                        i++;
                    }
                }
                solTable[0][1] = "\\texttt{key[}" + start.getLabel().toString() + "\\texttt{]}";
                exTable[0][1] = solTable[0][1];
                distances[current] = 0;
                for (i = 1; i < size; i++) {
                    used.add(current);
                    Node<N> currentNode = nodes.get(current);
                    solTable[i][0] = currentNode.getLabel().toString();
                    exTable[i][0] = "";
                    for (Pair<Integer, Node<N>> edge : graph.getAdjacencyList(currentNode)) {
                        Integer to = ids.get(edge.y);
                        if (to != null && (distances[to] == null || distances[to] > distances[current] + edge.x)) {
                            distances[to] = distances[current] + edge.x;
                        }
                    }
                    Integer curMin = null;
                    int minIndex = -1;
                    for (int j = 1; j <= size; j++) {
                        final Integer dist = distances[j - 1];
                        if (dist == null) {
                            solTable[i][j] = "$\\infty$";
                        } else {
                            if (!used.contains(j - 1) && (curMin == null || curMin > dist)) {
                                curMin = dist;
                                minIndex = j - 1;
                            }
                            solTable[i][j] = "" + dist;
                        }
                        exTable[i][j] = "";
                    }
                    if (minIndex < 0) {
                        // no shortening possible
                        break;
                    }
                    current = minIndex;
                    solColor[i][current + 1] = "black!20";
                }
                break;
            case GENERAL:
                exTable = new String[size][size];
                solTable = new String[size][size];
                exColor = new String[size][size];
                solColor = new String[size][size];
                solTable[0][0] = "\\textbf{Knoten}";
                exTable[0][0] = solTable[0][0];
                for (Node<N> node : nodes) {
                    if (!node.equals(start)) {
                        solTable[0][i] = "\\textbf{" + node.getLabel().toString() + "}";
                        exTable[0][i] = solTable[0][i];
                        ids.put(node, i);
                        i++;
                    }
                }
                distances[current] = 0;
                for (i = 1; i < size; i++) {
                    used.add(current);
                    Node<N> currentNode = nodes.get(current);
                    solTable[i][0] = currentNode.getLabel().toString();
                    exTable[i][0] = "";
                    for (Pair<Integer, Node<N>> edge : graph.getAdjacencyList(currentNode)) {
                        Integer to = ids.get(edge.y);
                        if (to != null && (distances[to] == null || distances[to] > distances[current] + edge.x)) {
                            distances[to] = distances[current] + edge.x;
                        }
                    }
                    Integer curMin = null;
                    int minIndex = -1;
                    for (int j = 1; j < size; j++) {
                        final Integer dist = distances[j];
                        if (dist == null) {
                            solTable[i][j] = "$\\infty$";
                        } else {
                            if (used.contains(j)) {
                                solTable[i][j] = "\\textbf{--}";
                            } else {
                                if (curMin == null || curMin > dist) {
                                    curMin = dist;
                                    minIndex = j;
                                }
                                solTable[i][j] = "" + dist;
                            }
                        }
                        exTable[i][j] = "";
                    }
                    if (minIndex < 0) {
                        // no shortening possible
                        break;
                    }
                    current = minIndex;
                    solColor[i][current + 1] = "black!20";
                }
                break;
            default:
                throw new IllegalStateException("Unkown text version!");
        }
        exTable[1][0] = start.getLabel().toString();
        exWriter.write("Betrachten Sie den folgenden Graphen:\\\\[2ex]");
        exWriter.newLine();
        TikZUtils.printBeginning(TikZUtils.CENTER, exWriter);
        graph.printTikZ(GraphPrintMode.ALL, 1, null, exWriter);
        exWriter.newLine();
        TikZUtils.printEnd(TikZUtils.CENTER, exWriter);
        exWriter.newLine();
        exWriter.write("F\\\"uhren Sie den \\emphasize{Dijkstra} Algorithmus auf diesem Graphen mit dem ");
        exWriter.write("\\emphasize{Startknoten ");
        exWriter.write(start.getLabel().toString());
        exWriter.write("} aus.");
        switch (mode) {
            case ALWAYS:
            case SOLUTION_SPACE:
                switch (DSALExercises.TEXT_VERSION) {
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
        exWriter.newLine();
        switch (mode) {
            case SOLUTION_SPACE:
                TikZUtils.printSolutionSpaceBeginning(exWriter);
                // fall-through
            case ALWAYS:
                TikZUtils.printBeginning(TikZUtils.CENTER, exWriter);
                exWriter.newLine();
                TikZUtils.printArrayStretch(1.5, exWriter);
                TikZUtils.printTable(exTable, exColor, "2cm", exWriter, false, 10);
                TikZUtils.printArrayStretch(1.0, exWriter);
                TikZUtils.printEnd(TikZUtils.CENTER, exWriter);
                if (mode == PreprintMode.SOLUTION_SPACE) {
                    TikZUtils.printSolutionSpaceEnd(exWriter);
                }
                break;
            case NEVER:
                // do nothing
        }
        TikZUtils.printArrayStretch(1.5, solWriter);
        TikZUtils.printTable(solTable, solColor, "2cm", solWriter, false, 10);
        TikZUtils.printArrayStretch(1.0, solWriter);
        solWriter.newLine();
        solWriter.write("\\vspace*{1ex}");
        solWriter.newLine();
        solWriter.newLine();
        solWriter.write("Die grau unterlegten Zellen markieren, an welcher Stelle f\\\"ur welchen Knoten die minimale");
        solWriter.write(" Distanz sicher berechnet worden ist.");
        solWriter.newLine();
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
        Graph<N, Integer> graph,
        boolean warshall,
        Comparator<Node<N>> comp,
        BufferedWriter exWriter,
        BufferedWriter solWriter
    ) throws IOException {
        final int tableCount = DSALExercises.STUDENT_MODE ? 1 : 2;
        final int tableMaxWidth = DSALExercises.STUDENT_MODE ? 10 : 0;
        final List<Node<N>> nodes = new ArrayList<Node<N>>(graph.getNodes());
        final int size = nodes.size();
        final ArrayList<String[][]> exercises = new ArrayList<String[][]>();
        final ArrayList<String[][]> solutions = new ArrayList<String[][]>();
        final ArrayList<String[][]> exColors = new ArrayList<String[][]>();
        final ArrayList<String[][]> solColors = new ArrayList<String[][]>();
        String[][] firstExercise = new String[size+1][size+1];
        String[][] otherExercise = new String[size+1][size+1];
        String[][] currentSolution = new String[size+1][size+1];
        String[][] curExColor = new String[size+1][size+1];
        String[][] curSolColor = new String[size+1][size+1];
        Integer[][] weights = new Integer[size][size];
        boolean[][] changed = new boolean[size][size];
        // initialize ids
        Map<Node<N>, Integer> ids = new LinkedHashMap<Node<N>, Integer>();
        for (int current = 0 ; current < size; ++current) {
            Node<N> currentNode = nodes.get(current);
            ids.put(currentNode, current);
        }
        firstExercise[0][0] = "";
        otherExercise[0][0] = "";
        currentSolution[0][0] = "";
        // initialize weights
        for (int current = 0 ; current < size; ++current) {
            Node<N> currentNode = nodes.get(current);
            // set labels
            firstExercise[0][current+1] = currentNode.getLabel().toString();
            firstExercise[current+1][0] = currentNode.getLabel().toString();
            otherExercise[0][current+1] = currentNode.getLabel().toString();
            otherExercise[current+1][0] = currentNode.getLabel().toString();
            currentSolution[0][current+1] = currentNode.getLabel().toString();
            currentSolution[current+1][0] = currentNode.getLabel().toString();
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
            for (Pair<Integer, Node<N>> edge : graph.getAdjacencyList(currentNode)) {
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
            Node<N> currentNode = nodes.get(current);
            // set labels
            currentSolution[0][current+1] = currentNode.getLabel().toString();
            currentSolution[current+1][0] = currentNode.getLabel().toString();
        }
        // actual algorithm
        for (int intermediate = 0; intermediate < size; ++intermediate) {
            for (int start = 0; start < size; ++start) {
                for (int target = 0; target < size; ++target) {
                    Integer oldValue = weights[start][target];
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
                Node<N> currentNode = nodes.get(current);
                // set labels
                currentSolution[0][current+1] = currentNode.getLabel().toString();
                currentSolution[current+1][0] = currentNode.getLabel().toString();
            }
            changed = new boolean[size][size];
        }
        // create output
        exWriter.write("Betrachten Sie den folgenden Graphen:\\\\[2ex]");
        exWriter.newLine();
        TikZUtils.printBeginning(TikZUtils.CENTER, exWriter);
        if (warshall) {
            graph.printTikZ(GraphPrintMode.NO_EDGE_LABELS, 1, null, exWriter);
        } else {
            graph.printTikZ(GraphPrintMode.ALL, 1, null, exWriter);
        }
        exWriter.newLine();
        TikZUtils.printEnd(TikZUtils.CENTER, exWriter);
        exWriter.newLine();
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
        exWriter.newLine();
        exWriter.newLine();
        TikZUtils.printArrayStretch(1.5, exWriter);
        TikZUtils.printArrayStretch(1.5, solWriter);
        int solCount = 0;
        int exCount = 0;
        for (int iteration = 0; iteration < solutions.size(); ++iteration) {
            solCount =
                GraphAlgorithms.printTables(
                    solCount,
                    tableCount,
                    iteration,
                    solutions.get(iteration),
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
                    exercises.get(iteration),
                    exColors.get(iteration),
                    exWriter,
                    true,
                    tableMaxWidth
                );
        }
        TikZUtils.printArrayStretch(1.0, exWriter);
        TikZUtils.printArrayStretch(1.0, solWriter);
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
        Graph<N, FlowPair> graph,
        Node<N> source,
        Node<N> sink,
        double multiplier,
        boolean twocolumns,
        PreprintMode mode,
        BufferedWriter exWriter,
        BufferedWriter solWriter
    ) throws IOException {
        exWriter.write("Betrachten Sie das folgende Flussnetzwerk mit Quelle ");
        exWriter.write(source.getLabel().toString());
        exWriter.write(" und Senke ");
        exWriter.write(sink.getLabel().toString());
        exWriter.write(":\\\\");
        exWriter.newLine();
        TikZUtils.printBeginning(TikZUtils.CENTER, exWriter);
        graph.printTikZ(GraphPrintMode.ALL, multiplier, null, exWriter);
        TikZUtils.printEnd(TikZUtils.CENTER, exWriter);
        exWriter.newLine();
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
        exWriter.newLine();
        int step = 0;
        TikZUtils.printSamePageBeginning(step++, twocolumns ? TikZUtils.TWO_COL_WIDTH : TikZUtils.COL_WIDTH, solWriter);
        solWriter.write("Initiales Flussnetzwerk:\\\\[2ex]");
        graph.printTikZ(GraphPrintMode.ALL, multiplier, null, solWriter);
        TikZUtils.printSamePageEnd(solWriter);
        solWriter.newLine();
        switch (mode) {
            case SOLUTION_SPACE:
                exWriter.write("\\solutionSpace{");
                exWriter.newLine();
                // fall-through
            case ALWAYS:
                if (twocolumns) {
                    exWriter.write("\\begin{longtable}{cc}");
                    exWriter.newLine();
                }
                break;
            case NEVER:
                // do nothing
        }
        if (twocolumns) {
            solWriter.write("\\begin{longtable}{cc}");
            solWriter.newLine();
        }
        while (true) {
            Graph<N, Integer> residualGraph = GraphAlgorithms.computeResidualGraph(graph);
            List<Node<N>> path = GraphAlgorithms.selectAugmentingPath(residualGraph, source, sink);
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
                    exWriter.newLine();
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
            solWriter.newLine();
            final Set<Pair<Node<N>, Pair<Integer, Node<N>>>> toHighlightResidual;
            switch (DSALExercises.TEXT_VERSION) {
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
                        exWriter.newLine();
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
                solWriter.newLine();
            }
            if (path == null) {
                break;
            }
            Set<Pair<Node<N>, Pair<FlowPair, Node<N>>>> toHighlightFlow = GraphAlgorithms.addFlow(graph, path);
            switch (mode) {
                case ALWAYS:
                case SOLUTION_SPACE:
                    TikZUtils.printSamePageBeginning(
                        step,
                        twocolumns ? TikZUtils.TWO_COL_WIDTH : TikZUtils.COL_WIDTH,
                        exWriter
                    );
                    exWriter.write("N\\\"achstes Flussnetzwerk mit aktuellem Fluss:\\\\[2ex]");
                    exWriter.newLine();
                    graph.printTikZ(GraphPrintMode.NO_EDGE_LABELS, multiplier, null, exWriter);
                    TikZUtils.printSamePageEnd(exWriter);
                    if (twocolumns) {
                        exWriter.write("\\\\");
                    }
                    exWriter.newLine();
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
            solWriter.newLine();
            graph.printTikZ(GraphPrintMode.ALL, multiplier, toHighlightFlow, solWriter);
            TikZUtils.printSamePageEnd(solWriter);
            if (twocolumns) {
                solWriter.write("\\\\");
            }
            solWriter.newLine();
        }
        int flow = 0;
        List<Pair<FlowPair, Node<N>>> list = graph.getAdjacencyList(source);
        if (list != null) {
            for (Pair<FlowPair, Node<N>> edge : list) {
                flow += edge.x.x;
            }
        }
        switch (mode) {
            case ALWAYS:
            case SOLUTION_SPACE:
                if (twocolumns) {
                    exWriter.write("\\end{longtable}");
                    exWriter.newLine();
                }
                exWriter.newLine();
                exWriter.newLine();
                exWriter.write("\\vspace*{1ex}");
                exWriter.newLine();
                exWriter.newLine();
                exWriter.write("Der maximale Fluss hat den Wert: ");
                exWriter.newLine();
                if (mode == PreprintMode.SOLUTION_SPACE) {
                    exWriter.write("}");
                    exWriter.newLine();
                }
                exWriter.newLine();
                break;
            case NEVER:
                // do nothing
        }
        if (twocolumns) {
            solWriter.write("\\end{longtable}");
            solWriter.newLine();
        }
        solWriter.newLine();
        solWriter.newLine();
        solWriter.write("\\vspace*{1ex}");
        solWriter.newLine();
        solWriter.newLine();
        solWriter.write("Der maximale Fluss hat den Wert: " + flow);
        solWriter.newLine();
        solWriter.newLine();
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
        Graph<N, Integer> graph,
        Node<N> start,
        Comparator<Node<N>> comp,
        BufferedWriter exWriter,
        BufferedWriter solWriter
    ) throws IOException {
        List<Node<N>> nodes = new ArrayList<Node<N>>(graph.getNodes());
        String[][] solutions = new String[nodes.size()+1][nodes.size()+1];
        solutions[0][0] = "\\#Iteration";
        solutions[0][1] = "0";
        Map<Node<N>, Integer> key = new LinkedHashMap<Node<N>, Integer>();
        
        Map<Node<N>, List<Pair<Integer, Node<N>>>> parent = new LinkedHashMap<Node<N>, List<Pair<Integer, Node<N>>>>();
        int i = 1;
        for (Node<N> node : nodes) {
            key.put(node, null);
            solutions[i][0] = node.getLabel().toString();
            i++;
        }
        List<Node<N>> q = new ArrayList<Node<N>>(graph.getNodes());
        key.put(start, new Integer(0));
        int iteration = 1;
        
        // actual algorithm
        while (!q.isEmpty()) {
            // extract the minimum from q
            Node<N> minNode = null;
            for (Node<N> node : q) {
                if (
                    minNode == null
                    || key.get(minNode) == null
                    || (key.get(node) != null && key.get(minNode).intValue() > key.get(node).intValue())
                ) {
                    minNode = node;
                }
            }
            
            // write solution
            solutions[0][iteration] = "" + iteration;
            i = 1;
            for (Node<N> node : nodes) {
                if (q.contains(node)) {
                    if (key.get(node) == null) {
                        solutions[i][iteration] = "$\\infty$";
                    } else {
                        if (minNode == node) {
                            solutions[i][iteration] = "\\underline{" + key.get(node) + "}";
                        } else {
                            solutions[i][iteration] = "" + key.get(node);
                        }
                    }
                } else {
                    solutions[i][iteration] = "";
                }
                i++;
            }
            // update the minimums successors remaining in q
            for (Pair<Integer, Node<N>> edge : graph.getAdjacencyList(minNode)) {
                if (q.contains(edge.y) && (key.get(edge.y) == null || edge.x < key.get(edge.y))) {
                    List<Pair<Integer, Node<N>>> adList = new ArrayList<Pair<Integer, Node<N>>>();
                    adList.add(new Pair<Integer, Node<N>>(edge.x, minNode));
                    parent.put(edge.y, adList);
                    key.put(edge.y, edge.x);
                }
            }
            q.remove(minNode);
            iteration++;
        }
        
        // create output
        exWriter.write("F\\\"uhren Sie Prim's Algorithmus auf dem folgenden Graphen aus.");
        exWriter.write(" Der Startknoten hat hierbei den Schl\\\"ussel " + start.getLabel().toString() + ".");
        exWriter.write(" Geben Sie dazu \\underline{vor} jedem Durchlauf der \\\"au{\\ss}eren Schleife an");
        exWriter.newLine();
        exWriter.write("\\begin{enumerate}");
        exWriter.newLine();
        exWriter.write("    \\item welchen Knoten \\texttt{extractMin(Q)} w\\\"ahlt");
        exWriter.newLine();
        exWriter.write("    \\item und welche Kosten die Randknoten haben, d.h. f\\\"ur jeden Knoten \\texttt{v} in ");
        exWriter.write("\\texttt{Q} den Wert \\texttt{key[v]}.");
        exWriter.newLine();
        exWriter.write("\\end{enumerate}");
        exWriter.newLine();
        exWriter.write(" Geben Sie zudem den vom Algorithmus bestimmten minimalen Spannbaum an.\\\\[2ex]");
        exWriter.newLine();
        TikZUtils.printBeginning(TikZUtils.CENTER, exWriter);
        graph.printTikZ(exWriter, null, false);
        exWriter.newLine();
        TikZUtils.printEnd(TikZUtils.CENTER, exWriter);
        
        TikZUtils.printTable(solutions, null, "2.0cm", solWriter, false, 10);
        solWriter.newLine();
        solWriter.newLine();
        solWriter.write("\\medskip");
        solWriter.newLine();
        // print the spanning tree
        solWriter.write("Hierbei gibt eine unterstrichene Zahl an in welcher Iteration (zugeh\\\"origer Zeilenkopf)");
        solWriter.write(" welcher Knoten (zugeh\\\"origer Spaltenkopf) durch \\texttt{extractMin(Q)} gew\\\"ahlt");
        solWriter.write(" wurde. Wir erhalten den folgenden minimalen Spannbaum:\\\\[2ex]");
        solWriter.newLine();
        TikZUtils.printBeginning(TikZUtils.CENTER, solWriter);
        graph.printTikZ(solWriter, parent, false);
        solWriter.newLine();
        TikZUtils.printEnd(TikZUtils.CENTER, solWriter);
    }

    /**
     * Adds the maximal flow along the specified path in the specified flow network and returns the set of edges used 
     * to add the flow.
     * @param graph The flow network to add a flow to.
     * @param path The path along which the flow is to be be added.
     * @return The set of edges whose flow has been modified.
     * @throws IOException If some error occurs during output.
     */
    private static <N> Set<Pair<Node<N>, Pair<FlowPair, Node<N>>>> addFlow(Graph<N, FlowPair> graph, List<Node<N>> path)
    throws IOException {
        Integer min = GraphAlgorithms.computeMinEdge(graph, path);
        Iterator<Node<N>> it = path.iterator();
        Node<N> from;
        Node<N> to = it.next();
        Set<Pair<Node<N>, Pair<FlowPair, Node<N>>>> toHighlight =
            new LinkedHashSet<Pair<Node<N>, Pair<FlowPair, Node<N>>>>();
        while (it.hasNext()) {
            from = to;
            to = it.next();
            int flow = min;
            for (Pair<FlowPair, Node<N>> edge : graph.getEdges(from, to)) {
                int added = Math.min(flow, edge.x.y - edge.x.x);
                if (added > 0) {
                    flow -= added;
                    edge.x.x += added;
                    toHighlight.add(new Pair<Node<N>, Pair<FlowPair, Node<N>>>(from, edge));
                }
            }
            for (Pair<FlowPair, Node<N>> edge : graph.getEdges(to, from)) {
                int added = Math.min(flow, edge.x.x);
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
        Node<String> node,
        Graph<String, ?> graph,
        Pair<Pair<Integer, Integer>, Boolean> pos,
        Map<Pair<Integer, Integer>, Node<String>> grid,
        Map<Node<String>, NodeGridPosition> positions
    ) {
        graph.addNode(node);
        grid.put(pos.x, node);
        int x = pos.x.x;
        int y = pos.x.y;
        NodeGridPosition gridPos = new NodeGridPosition(pos.x.x, pos.x.y, pos.y);
        positions.put(node, gridPos);
        Pair<Integer, Integer> nextPos = new Pair<Integer, Integer>(x, y - 1);
        Node<String> nextNode = grid.get(nextPos);
        if (nextNode != null) {
            NodeGridPosition nextGridPos = positions.get(nextNode);
            gridPos.north = nextGridPos;
            nextGridPos.south = gridPos;
        }
        nextPos = new Pair<Integer, Integer>(x + 1, y);
        nextNode = grid.get(nextPos);
        if (nextNode != null) {
            NodeGridPosition nextGridPos = positions.get(nextNode);
            gridPos.east = nextGridPos;
            nextGridPos.west = gridPos;
        }
        nextPos = new Pair<Integer, Integer>(x, y + 1);
        nextNode = grid.get(nextPos);
        if (nextNode != null) {
            NodeGridPosition nextGridPos = positions.get(nextNode);
            gridPos.south = nextGridPos;
            nextGridPos.north = gridPos;
        }
        nextPos = new Pair<Integer, Integer>(x - 1, y);
        nextNode = grid.get(nextPos);
        if (nextNode != null) {
            NodeGridPosition nextGridPos = positions.get(nextNode);
            gridPos.west = nextGridPos;
            nextGridPos.east = gridPos;
        }
        if (pos.y) {
            nextPos = new Pair<Integer, Integer>(x + 1, y - 1);
            nextNode = grid.get(nextPos);
            if (nextNode != null) {
                NodeGridPosition nextGridPos = positions.get(nextNode);
                gridPos.northeast = nextGridPos;
                nextGridPos.southwest = gridPos;
            }
            nextPos = new Pair<Integer, Integer>(x + 1, y + 1);
            nextNode = grid.get(nextPos);
            if (nextNode != null) {
                NodeGridPosition nextGridPos = positions.get(nextNode);
                gridPos.southeast = nextGridPos;
                nextGridPos.northwest = gridPos;
            }
            nextPos = new Pair<Integer, Integer>(x - 1, y + 1);
            nextNode = grid.get(nextPos);
            if (nextNode != null) {
                NodeGridPosition nextGridPos = positions.get(nextNode);
                gridPos.southwest = nextGridPos;
                nextGridPos.northeast = gridPos;
            }
            nextPos = new Pair<Integer, Integer>(x - 1, y - 1);
            nextNode = grid.get(nextPos);
            if (nextNode != null) {
                NodeGridPosition nextGridPos = positions.get(nextNode);
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
    private static <N> Integer computeMinEdge(Graph<N, FlowPair> graph, List<Node<N>> path) {
        Integer min = null;
        Iterator<Node<N>> it = path.iterator();
        Node<N> from;
        Node<N> to = it.next();
        while (it.hasNext()) {
            from = to;
            to = it.next();
            int flow = 0;
            for (Pair<FlowPair, Node<N>> edge : graph.getEdges(from, to)) {
                flow += edge.x.y - edge.x.x;
            }
            for (Pair<FlowPair, Node<N>> edge : graph.getEdges(to, from)) {
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
    private static <N> Graph<N, Integer> computeResidualGraph(Graph<N, FlowPair> graph) throws IOException {
        Graph<N, Integer> res = new Graph<N, Integer>();
        for (Node<N> node : graph.getNodes()) {
            res.addNode(node);
            List<Pair<FlowPair, Node<N>>> list = graph.getAdjacencyList(node);
            if (list == null) {
                continue;
            }
            for (Pair<FlowPair, Node<N>> edge : list) {
                Node<N> target = edge.y;
                Integer back = edge.x.x;
                if (back > 0) {
                    Set<Pair<Integer, Node<N>>> backEdges = res.getEdges(target, node);
                    if (backEdges.isEmpty()) {
                        res.addEdge(target, back, node);
                    } else {
                        backEdges.iterator().next().x += back;
                    }
                }
                Integer forth = edge.x.y - back;
                if (forth > 0) {
                    Set<Pair<Integer, Node<N>>> forthEdges = res.getEdges(node, target);
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
        int count,
        int tableCount,
        int iteration,
        String[][] table,
        String[][] color,
        BufferedWriter writer,
        boolean transpose,
        int breakAtColumn
    ) throws IOException {
        if (count < tableCount) {
            TikZUtils.printTable(table, color, "1cm", writer, transpose, breakAtColumn);
            writer.newLine();
            writer.write("\\hspace{2em}");
            writer.newLine();
            return count + 1;
        }
        writer.write("\\\\[2ex]");
        writer.newLine();
        TikZUtils.printTable(table, color, "1cm", writer, transpose, breakAtColumn);
        writer.newLine();
        writer.write("\\hspace{2em}");
        writer.newLine();
        return 1;
    }

    /**
     * @param gen Random number generator.
     * @param root A value which is probably close to the result.
     * @return A random non-negative edge value.
     */
    private static int randomEdgeValue(Random gen, int root) {
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
    private static int randomNumOfEdges(Random gen, int max) {
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
    private static <N> List<Node<N>> selectAugmentingPath(Graph<N, Integer> graph, Node<N> source, Node<N> sink) {
        List<Node<N>> path = new ArrayList<Node<N>>();
        Deque<List<Node<N>>> queue = new ArrayDeque<List<Node<N>>>();
        path.add(source);
        queue.add(path);
        Set<Node<N>> visited = new LinkedHashSet<Node<N>>();
        visited.add(source);
        while (!queue.isEmpty()) {
            path = queue.poll();
            List<Pair<Integer, Node<N>>> list = graph.getAdjacencyList(path.get(path.size() - 1));
            if (list == null) {
                continue;
            }
            for (Pair<Integer, Node<N>> edge : list) {
                if (visited.contains(edge.y)) {
                    continue;
                }
                List<Node<N>> newPath = new ArrayList<Node<N>>(path);
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

    /**
     * @param graph Some residual graph.
     * @param path A path through this residual graph.
     * @return The set of all edges used by the specified path in the specified graph.
     */
    private static <N> Set<Pair<Node<N>, Pair<Integer, Node<N>>>> toEdges(Graph<N, Integer> graph, List<Node<N>> path) {
        if (path == null) {
            return null;
        }
        Set<Pair<Node<N>, Pair<Integer, Node<N>>>> res = new LinkedHashSet<Pair<Node<N>, Pair<Integer, Node<N>>>>();
        Iterator<Node<N>> it = path.iterator();
        Node<N> cur = it.next();
        while (it.hasNext()) {
            Node<N> next = it.next();
            for (Pair<Integer, Node<N>> edge : graph.getAdjacencyList(cur)) {
                if (edge.y.equals(next)) {
                    res.add(new Pair<Node<N>, Pair<Integer, Node<N>>>(cur, edge));
                    break;
                }
            }
            cur = next;
        }
        return res;
    }

    /**
     * @param num A non-negative number.
     * @return A String representation of this number. 0 is A, 1 is B, 26 is AA, 27 is AB, and so on.
     */
    private static String toStringLabel(int num) {
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
