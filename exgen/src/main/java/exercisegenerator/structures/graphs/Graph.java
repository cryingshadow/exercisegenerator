package exercisegenerator.structures.graphs;

import java.io.*;
import java.math.*;
import java.util.*;
import java.util.Map.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;

/**
 * Directed graph implemented by adjacency lists. It supports arbitrary edge and node labels and multiple edges between
 * the same nodes.
 * @param <N> The type of the node labels.
 * @param <E> The type of the edge labels.
 */
public class Graph<N, E> {

    /**
     * The adjacency lists of the nodes in the graph.
     */
    private final Map<LabeledNode<N>, List<Pair<E, LabeledNode<N>>>> adjacencyLists;

    /**
     * If this graph is in grid form, this map stores the grid position of each node (column, row).
     * Otherwise, it is null.
     */
    private Map<Pair<Integer, Integer>, LabeledNode<N>> grid;

    /**
     * Creates an empty graph.
     */
    public Graph() {
        this.adjacencyLists = new LinkedHashMap<LabeledNode<N>, List<Pair<E, LabeledNode<N>>>>();
        this.grid = null;
    }

    /**
     * Adds an edge from the specified from node to the specified to node with the specified label. If some of the
     * argument nodes does not yet exist in the graph, it is added. Moreover, the grid structure is removed as we
     * cannot guarantee anymore that the graph complies to the grid format.
     * @param from The from node.
     * @param label The edge label.
     * @param to The to node.
     */
    public void addEdge(final LabeledNode<N> from, final E label, final LabeledNode<N> to) {
        if (from == null || to == null) {
            throw new NullPointerException();
        }
        this.grid = null;
        this.addNode(from);
        this.addNode(to);
        this.adjacencyLists.get(from).add(new Pair<E, LabeledNode<N>>(label, to));
    }

    /**
     * Adds the specified node to this graph.
     * @param node The node to add.
     * @return True if the node has been added, false if it was already contained in this graph.
     */
    public boolean addNode(final LabeledNode<N> node) {
        if (!this.adjacencyLists.containsKey(node)) {
            this.adjacencyLists.put(node, new ArrayList<Pair<E, LabeledNode<N>>>());
            return true;
        }
        return false;
    }

    /**
     * @param node Some node.
     * @return The adjacency list of the specified node or null if the node does not exist in the graph. Changes to the
     *         list are not reflected in the graph, but changes to the edges (and nodes) are.
     */
    public List<Pair<E, LabeledNode<N>>> getAdjacencyList(final LabeledNode<N> node) {
        final List<Pair<E, LabeledNode<N>>> list = this.adjacencyLists.get(node);
        if (list == null) {
            return null;
        }
        return new ArrayList<Pair<E, LabeledNode<N>>>(list);
    }

    /**
     * @param from Some node.
     * @param to Some other node.
     * @return A set of all edges from the first to the second node in this graph.
     */
    public Set<Pair<E, LabeledNode<N>>> getEdges(final LabeledNode<N> from, final LabeledNode<N> to) {
        final Set<Pair<E, LabeledNode<N>>> res = new LinkedHashSet<Pair<E, LabeledNode<N>>>();
        final List<Pair<E, LabeledNode<N>>> list = this.adjacencyLists.get(from);
        if (list != null) {
            for (final Pair<E, LabeledNode<N>> edge : list) {
                if (to.equals(edge.y)) {
                    res.add(edge);
                }
            }
        }
        return res;
    }

    /**
     * @return The grid structure for this graph (null if this graph has no grid layout).
     */
    public Map<Pair<Integer, Integer>, LabeledNode<N>> getGrid() {
        return this.grid;
    }

    /**
     * @return The set of nodes contained in this graph. Changes to the set are not reflected within the graph, but
     *         changes to the nodes are.
     */
    public Set<LabeledNode<N>> getNodes() {
        return new LinkedHashSet<LabeledNode<N>>(this.adjacencyLists.keySet());
    }

    /**
     * @param label Some label.
     * @return The set of nodes contained in this graph having the specified label.
     */
    public Set<LabeledNode<N>> getNodesWithLabel(final N label) {
        final Set<LabeledNode<N>> res = new LinkedHashSet<LabeledNode<N>>();
        for (final LabeledNode<N> node : this.adjacencyLists.keySet()) {
            if (node.label.isPresent() && label.equals(node.label.get())) {
                res.add(node);
            }
        }
        return res;
    }

    /**
     * Prints this graph in TikZ format to the specified writer. If this graph complies to the grid format, this layout
     * is used for the output. Otherwise, there is no guarantee for the layout.
     * @param writer The writer to send the output to.
     * @param adListsParam TODO
     * @param directed TODO
     * @throws IOException If some error occurs during output.
     */
    public void printTikZ(
        final BufferedWriter writer,
        final Map<LabeledNode<N>,List<Pair<E,LabeledNode<N>>>> adListsParam,
        final boolean directed
    ) throws IOException {
        final Map<LabeledNode<N>,List<Pair<E,LabeledNode<N>>>> adLists = adListsParam == null ? this.adjacencyLists : adListsParam;
        if (directed) {
            LaTeXUtils.printTikzBeginning(TikZStyle.GRAPH, writer);
        } else {
            LaTeXUtils.printTikzBeginning(TikZStyle.SYM_GRAPH, writer);
        }
        if (this.grid == null) {
            final int limit = (int)Math.sqrt(this.adjacencyLists.size());
            final Iterator<LabeledNode<N>> it = this.adjacencyLists.keySet().iterator();
            for (int row = 0; it.hasNext(); row++) {
                for (int col = 0; col < limit && it.hasNext(); col++) {
                    LaTeXUtils.printNode(it.next(), "[node]", "at (" + col + "," + row + ") ", writer);
                }
            }
        } else {
            for (final Entry<Pair<Integer, Integer>, LabeledNode<N>> entry : this.grid.entrySet()) {
                final Pair<Integer, Integer> pos = entry.getKey();
                LaTeXUtils.printNode(entry.getValue(), "[node]", "at (" + pos.x + "," + pos.y + ") ", writer);
            }
        }
        if (directed) {
            for (final Entry<LabeledNode<N>, List<Pair<E, LabeledNode<N>>>> entry : adLists.entrySet()) {
                final BigInteger from = entry.getKey().id;
                for (final Pair<E, LabeledNode<N>> edge : entry.getValue()) {
                    LaTeXUtils.printEdge(LaTeXUtils.EDGE_STYLE, from, edge.x, edge.y.id, writer);
                }
            }
        } else {
            final List<Pair<BigInteger,BigInteger>> finishedNodePairs = new ArrayList<Pair<BigInteger,BigInteger>>();
            for (final Entry<LabeledNode<N>, List<Pair<E, LabeledNode<N>>>> entry : adLists.entrySet()) {
                final BigInteger from = entry.getKey().id;
                for (final Pair<E, LabeledNode<N>> edge : entry.getValue()) {
                    final Pair<BigInteger,BigInteger> reverseNodePair =
                        new Pair<BigInteger,BigInteger>(edge.y.id, entry.getKey().id);
                    if (!finishedNodePairs.contains(reverseNodePair)) {
                        LaTeXUtils.printEdge(LaTeXUtils.SYM_EDGE_STYLE, from, edge.x, edge.y.id, writer);
                        finishedNodePairs.add(new Pair<BigInteger,BigInteger>(entry.getKey().id, edge.y.id));
                    }
                }
            }
        }
        LaTeXUtils.printTikzEnd(writer);
    }

    /**
     * Prints this graph in TikZ format to the specified writer. If this graph complies to the grid format, this layout
     * is used for the output. Otherwise, there is no guarantee for the layout.
     * @param printMode Print the edges or edge labels?
     * @param multiplier Multiplier for node distances.
     * @param toHighlight A set of edges to highlight in the graph.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public void printTikZ(
        final GraphPrintMode printMode,
        final double multiplier,
        final Set<Pair<LabeledNode<N>, Pair<E, LabeledNode<N>>>> toHighlight,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printTikzBeginning(TikZStyle.GRAPH, writer);
        if (this.grid == null) {
            final int limit = (int)Math.sqrt(this.adjacencyLists.size());
            final Iterator<LabeledNode<N>> it = this.adjacencyLists.keySet().iterator();
            for (int row = 0; it.hasNext(); row++) {
                final double multipliedRow = Math.round(multiplier * row * 10.0) / 10.0;
                for (int col = 0; col < limit && it.hasNext(); col++) {
                    final double multipliedCol = Math.round(multiplier * col * 10.0) / 10.0;
                    LaTeXUtils.printNode(
                        it.next(),
                        "[node]",
                        "at (" + multipliedCol + "," + multipliedRow + ") ",
                        writer
                    );
                }
            }
        } else {
            for (final Entry<Pair<Integer, Integer>, LabeledNode<N>> entry : this.grid.entrySet()) {
                final Pair<Integer, Integer> pos = entry.getKey();
                final double multipliedCol = Math.round(multiplier * pos.x * 10.0) / 10.0;
                final double multipliedRow = Math.round(multiplier * pos.y * 10.0) / 10.0;
                LaTeXUtils.printNode(
                    entry.getValue(),
                    "[node]",
                    "at (" + multipliedCol + "," + multipliedRow + ") ",
                    writer
                );
            }
        }
        if (printMode != GraphPrintMode.NO_EDGES) {
            final boolean printEdgeLabels = printMode == GraphPrintMode.ALL;
            for (final Entry<LabeledNode<N>, List<Pair<E, LabeledNode<N>>>> entry : this.adjacencyLists.entrySet()) {
                final LabeledNode<N> fromNode = entry.getKey();
                final BigInteger from = fromNode.id;
                for (final Pair<E, LabeledNode<N>> edge : entry.getValue()) {
                    if (
                        toHighlight != null
                        && toHighlight.contains(new Pair<LabeledNode<N>, Pair<E, LabeledNode<N>>>(fromNode, edge))
                    ) {
                        if (printEdgeLabels) {
                            LaTeXUtils.printEdge(LaTeXUtils.EDGE_HIGHLIGHT_STYLE, from, edge.x, edge.y.id, writer);
                        } else {
                            LaTeXUtils.printEdge(LaTeXUtils.EDGE_HIGHLIGHT_STYLE, from, null, edge.y.id, writer);
                        }
                    } else {
                        if (printEdgeLabels) {
                            LaTeXUtils.printEdge(LaTeXUtils.EDGE_STYLE, from, edge.x, edge.y.id, writer);
                        } else {
                            LaTeXUtils.printEdge(LaTeXUtils.EDGE_STYLE, from, null, edge.y.id, writer);
                        }
                    }
                }
            }
        }
        LaTeXUtils.printTikzEnd(writer);
        Main.newLine(writer);
    }

    /**
     * Creates a graph where the nodes are positioned according to a grid. The graph is generated from the specified
     * reader according to the following format:
     * Each even line (counting starts at 0) is of the form:
     * <node label>,<from edge label>|<to edge label>,<node label>,<from edge label>|<to edge label>,...,<node label>
     * Each odd line is of the form:
     * <vertical from edge label>|<vertical to edge label>,<diagonal from edge label>|<diagonal to edge label>,...
     * The diagonal edges follow the pattern:
     * \/\/...
     * /\/\...
     * ...
     * So the first (upper left) diagonal edge is descending.
     * An empty label text corresponds to a non-existing node or edge. If there is an edge where at least one of its
     * adjacent nodes does not exist, an IOException is thrown.
     * @param reader The reader from which to read the input.
     * @param nodeParser Parser for node labels.
     * @param edgeParser Parser for edge labels.
     * @throws IOException If some error occurs during input, the input does not comply to the expected format, or if
     *                     we have an edge adjacent to a non-existing node.
     */
    public void setGraphFromInput(
        final BufferedReader reader,
        final LabelParser<N> nodeParser,
        final LabelParser<E> edgeParser
    ) throws IOException {
        String line = reader.readLine();
        final Map<Pair<Integer, Integer>, LabeledNode<N>> newGrid = new LinkedHashMap<Pair<Integer, Integer>, LabeledNode<N>>();
        final Map<Pair<Integer, Integer>, List<Pair<E, Pair<Integer, Integer>>>> edges =
            new LinkedHashMap<Pair<Integer, Integer>, List<Pair<E, Pair<Integer, Integer>>>>();
        int maxRow = 0;
        for (int num = 0; line != null; num++) {
            line.trim();
            if ("".equals(line)) {
                break;
            }
            final String[] commaSeparated = line.split(",");
            final int row = num / 2;
            maxRow = Math.max(maxRow, row);
            if (num % 2 == 0) {
                // even line
                for (int i = 0; i < commaSeparated.length; i++) {
                    if (i % 2 == 0) {
                        // node label
                        final String label = commaSeparated[i].trim();
                        if (!"".equals(label)) {
                            newGrid.put(
                                new Pair<Integer, Integer>(i / 2, row),
                                new LabeledNode<N>(Optional.of(nodeParser.parse(label)))
                            );
                        }
                    } else {
                        // edge labels
                        final String[] edgeLabels = commaSeparated[i].split("\\|");
                        if (edgeLabels.length != 2) {
                            throw new IOException("Edge labels do not match expected format (" + i + "," + num + ")!");
                        }
                        this.addEdges(
                            edges,
                            edgeLabels,
                            edgeParser,
                            new Pair<Integer, Integer>(i / 2, row),
                            new Pair<Integer, Integer>((i / 2) + 1, row)
                        );
                    }
                }
            } else {
                // odd line
                for (int i = 0; i < commaSeparated.length; i++) {
                    final String[] edgeLabels = commaSeparated[i].split("\\|");
                    if (edgeLabels.length != 2) {
                        throw new IOException("Edge labels do not match expected format (" + i + "," + num + ")!");
                    }
                    if (i % 2 == 0) {
                        // vertical edges
                        this.addEdges(
                            edges,
                            edgeLabels,
                            edgeParser,
                            new Pair<Integer, Integer>(i / 2, row),
                            new Pair<Integer, Integer>(i / 2, row + 1)
                        );
                    } else {
                        // diagonal edges
                        if ((num % 4) + (i % 4) == 4) {
                            // ascending diagonal
                            this.addEdges(
                                edges,
                                edgeLabels,
                                edgeParser,
                                new Pair<Integer, Integer>(i / 2, row + 1),
                                new Pair<Integer, Integer>((i / 2) + 1, row)
                            );
                        } else {
                            // descending diagonal
                            this.addEdges(
                                edges,
                                edgeLabels,
                                edgeParser,
                                new Pair<Integer, Integer>(i / 2, row),
                                new Pair<Integer, Integer>((i / 2) + 1, row + 1)
                            );
                        }
                    }
                }
            }
            line = reader.readLine();
        }
        // now check whether we have edges to non-existing nodes
        final Set<Pair<Integer, Integer>> nodes = newGrid.keySet();
        if (!nodes.containsAll(edges.keySet())) {
            throw new IOException("Edge from non-existent node detected!");
        }
        for (final List<Pair<E, Pair<Integer, Integer>>> list : edges.values()) {
            for (final Pair<E, Pair<Integer, Integer>> pair : list) {
                if (!nodes.contains(pair.y)) {
                    throw new IOException("Edge to non-existent node detected!");
                }
            }
        }
        // everything is alright - build the graph
        this.adjacencyLists.clear();
        // mirror the positions vertically as TikZ positions are mirrored that way compared to the input format
        final Map<Pair<Integer, Integer>, LabeledNode<N>> mirroredGrid = new LinkedHashMap<Pair<Integer, Integer>, LabeledNode<N>>();
        for (final Pair<Integer, Integer> pos : nodes) {
            final LabeledNode<N> node = newGrid.get(pos);
            this.adjacencyLists.put(node, new ArrayList<Pair<E, LabeledNode<N>>>());
            mirroredGrid.put(new Pair<Integer, Integer>(pos.x, maxRow - pos.y), node);
        }
        for (final Entry<Pair<Integer, Integer>, List<Pair<E, Pair<Integer, Integer>>>> edge : edges.entrySet()) {
            final List<Pair<E, LabeledNode<N>>> list = this.adjacencyLists.get(newGrid.get(edge.getKey()));
            for (final Pair<E, Pair<Integer, Integer>> pair : edge.getValue()) {
                list.add(new Pair<E, LabeledNode<N>>(pair.x, newGrid.get(pair.y)));
            }
        }
        this.grid = mirroredGrid;
    }

    /**
     * @param newGrid The grid layout to set.
     */
    public void setGrid(final Map<Pair<Integer, Integer>, LabeledNode<N>> newGrid) {
        this.grid = newGrid;
    }

    /**
     * @return This graph in adjacency matrix representation. This is given by three arrays, the first being the
     *         adjacency matrix, the second being the map from node indices in the matrix to the general IDs of the
     *         corresponding nodes, and the third being such a map for the node labels. If this graph contains multiple
     *         edges between the same nodes in the same direction, an IllegalStateException is thrown.
     */
    public Pair<Object[][], Pair<BigInteger[], Object[]>> toAdjacencyMatrix() {
        final int size = this.adjacencyLists.size();
        final Object[][] matrix = new Object[size][size];
        final BigInteger[] ids = new BigInteger[size];
        final Object[] labels = new Object[size];
        final Map<BigInteger, Integer> toArray = new LinkedHashMap<BigInteger, Integer>();
        int id = 0;
        for (final LabeledNode<N> node : this.adjacencyLists.keySet()) {
            final BigInteger nodeID = node.id;
            toArray.put(nodeID, id);
            ids[id] = nodeID;
            labels[id++] = node.label.orElse(null);
        }
        for (final Entry<LabeledNode<N>, List<Pair<E, LabeledNode<N>>>> entry : this.adjacencyLists.entrySet()) {
            final int from = toArray.get(entry.getKey()); //FIXME probably wrong type
            final Set<Integer> tos = new LinkedHashSet<Integer>();
            for (final Pair<E, LabeledNode<N>> edge : entry.getValue()) {
                final int to = toArray.get(edge.y); //FIXME probably wrong type
                if (tos.contains(to)) {
                    throw new IllegalStateException(
                        "Graph with multiple edges between the same nodes in the same direction cannot be converted "
                        + "to matrix representation."
                    );
                }
                tos.add(to);
                matrix[from][to] = edge.x;
            }
        }
        return
            new Pair<Object[][], Pair<BigInteger[], Object[]>>(matrix, new Pair<BigInteger[], Object[]>(ids, labels));
    }

    /**
     * Help method to add edges to the internal grid representation during parsing of an input reader.
     * @param edges The edges.
     * @param edgeLabels An array of length 2 containing the label text to be parsed to edge labels.
     * @param edgeParser The parser for the edge labels.
     * @param fromPos The position of the from node in the grid.
     * @param toPos The position of the to node in the grid.
     * @throws IOException If an edge label cannot be parsed.
     */
    private void addEdges(
        final Map<Pair<Integer, Integer>, List<Pair<E, Pair<Integer, Integer>>>> edges,
        final String[] edgeLabels,
        final LabelParser<E> edgeParser,
        final Pair<Integer, Integer> fromPos,
        final Pair<Integer, Integer> toPos
    ) throws IOException {
        final String fromEdge = edgeLabels[0].trim();
        final String toEdge = edgeLabels[1].trim();
        if (!"".equals(fromEdge)) {
            if (!edges.containsKey(fromPos)) {
                edges.put(fromPos, new ArrayList<Pair<E, Pair<Integer, Integer>>>());
            }
            edges.get(
                fromPos
            ).add(new Pair<E, Pair<Integer, Integer>>(edgeParser.parse(fromEdge), toPos));
        }
        if (!"".equals(toEdge)) {
            if (!edges.containsKey(toPos)) {
                edges.put(toPos, new ArrayList<Pair<E, Pair<Integer, Integer>>>());
            }
            edges.get(
                toPos
            ).add(new Pair<E, Pair<Integer, Integer>>(edgeParser.parse(toEdge), fromPos));
        }
    }

}