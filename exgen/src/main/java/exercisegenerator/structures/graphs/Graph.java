package exercisegenerator.structures.graphs;

import java.io.*;
import java.math.*;
import java.util.*;
import java.util.Map.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;

/**
 * Directed graph implemented by adjacency lists. It supports arbitrary edge and vertex labels and multiple edges
 * between the same vertices.
 * @param <V> The type of the vertex labels.
 * @param <E> The type of the edge labels.
 */
public class Graph<V, E> {

    private final Map<Vertex<V>, List<Edge<E, V>>> adjacencyLists;

    private Optional<Map<GridCoordinates, Vertex<V>>> grid;

    public Graph() {
        this.adjacencyLists = new LinkedHashMap<Vertex<V>, List<Edge<E, V>>>();
        this.grid = Optional.empty();
    }

    /**
     * Adds an edge from the specified from vertex to the specified to vertex with the specified label. If some of the
     * argument vertices do not yet exist in the graph, they are added. Moreover, the grid structure is removed as we
     * cannot guarantee anymore that the graph complies to the grid format.
     * @param from The from vertex.
     * @param label The edge label.
     * @param to The to vertex.
     */
    public void addEdge(final Vertex<V> from, final E label, final Vertex<V> to) {
        if (from == null || to == null) {
            throw new NullPointerException();
        }
        this.grid = Optional.empty();
        this.addVertex(from);
        this.addVertex(to);
        this.adjacencyLists.get(from).add(new Edge<E, V>(label, to));
    }

    /**
     * Adds the specified vertex to this graph.
     * @param vertex The vertex to add.
     * @return True if the vertex has been added, false if it was already contained in this graph.
     */
    public boolean addVertex(final Vertex<V> vertex) {
        if (!this.adjacencyLists.containsKey(vertex)) {
            this.adjacencyLists.put(vertex, new ArrayList<Edge<E, V>>());
            return true;
        }
        return false;
    }

    public List<Edge<E, V>> getAdjacencyList(final Vertex<V> vertex) {
        final List<Edge<E, V>> list = this.adjacencyLists.get(vertex);
        if (list == null) {
            return null;
        }
        return new ArrayList<Edge<E, V>>(list);
    }

    public List<Vertex<V>> getAdjacentVertices(final Vertex<V> vertex) {
        return this.getAdjacencyList(vertex).stream().map(edge -> edge.y).toList();
    }

    public Set<Edge<E, V>> getEdges(final Vertex<V> from, final Vertex<V> to) {
        final Set<Edge<E, V>> res = new LinkedHashSet<Edge<E, V>>();
        final List<Edge<E, V>> list = this.adjacencyLists.get(from);
        if (list != null) {
            for (final Edge<E, V> edge : list) {
                if (to.equals(edge.y)) {
                    res.add(edge);
                }
            }
        }
        return res;
    }

    public Optional<Map<GridCoordinates, Vertex<V>>> getGrid() {
        return this.grid;
    }

    public Set<Vertex<V>> getVertices() {
        return new LinkedHashSet<Vertex<V>>(this.adjacencyLists.keySet());
    }

    public Set<Vertex<V>> getVerticesWithLabel(final V label) {
        final Set<Vertex<V>> res = new LinkedHashSet<Vertex<V>>();
        for (final Vertex<V> vertex : this.adjacencyLists.keySet()) {
            if (vertex.label.isPresent() && label.equals(vertex.label.get())) {
                res.add(vertex);
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
        final Map<Vertex<V>, List<Edge<E, V>>> adListsParam,
        final boolean directed
    ) throws IOException {
        final Map<Vertex<V>, List<Edge<E, V>>> adLists = adListsParam == null ? this.adjacencyLists : adListsParam;
        if (directed) {
            LaTeXUtils.printTikzBeginning(TikZStyle.GRAPH, writer);
        } else {
            LaTeXUtils.printTikzBeginning(TikZStyle.SYM_GRAPH, writer);
        }
        if (this.grid.isEmpty()) {
            final int limit = (int)Math.sqrt(this.adjacencyLists.size());
            final Iterator<Vertex<V>> it = this.adjacencyLists.keySet().iterator();
            for (int row = 0; it.hasNext(); row++) {
                for (int col = 0; col < limit && it.hasNext(); col++) {
                    LaTeXUtils.printVertex(it.next(), "[node]", "at (" + col + "," + row + ") ", writer);
                }
            }
        } else {
            for (final Entry<GridCoordinates, Vertex<V>> entry : this.grid.get().entrySet()) {
                final GridCoordinates pos = entry.getKey();
                LaTeXUtils.printVertex(entry.getValue(), "[node]", "at (" + pos.x + "," + pos.y + ") ", writer);
            }
        }
        if (directed) {
            for (final Entry<Vertex<V>, List<Edge<E, V>>> entry : adLists.entrySet()) {
                final BigInteger from = entry.getKey().id;
                for (final Edge<E, V> edge : entry.getValue()) {
                    LaTeXUtils.printEdge(LaTeXUtils.EDGE_STYLE, from, edge.x, edge.y.id, writer);
                }
            }
        } else {
            final List<Pair<BigInteger,BigInteger>> finishedVertexPairs = new ArrayList<Pair<BigInteger,BigInteger>>();
            for (final Entry<Vertex<V>, List<Edge<E, V>>> entry : adLists.entrySet()) {
                final BigInteger from = entry.getKey().id;
                for (final Edge<E, V> edge : entry.getValue()) {
                    final Pair<BigInteger,BigInteger> reverseVertexPair =
                        new Pair<BigInteger,BigInteger>(edge.y.id, entry.getKey().id);
                    if (!finishedVertexPairs.contains(reverseVertexPair)) {
                        LaTeXUtils.printEdge(LaTeXUtils.SYM_EDGE_STYLE, from, edge.x, edge.y.id, writer);
                        finishedVertexPairs.add(new Pair<BigInteger,BigInteger>(entry.getKey().id, edge.y.id));
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
     * @param multiplier Multiplier for vertex distances.
     * @param toHighlight A set of edges to highlight in the graph.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public void printTikZ(
        final GraphPrintMode printMode,
        final double multiplier,
        final Set<Pair<Vertex<V>, Edge<E, V>>> toHighlight,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printTikzBeginning(TikZStyle.GRAPH, writer);
        if (this.grid.isEmpty()) {
            final int limit = (int)Math.sqrt(this.adjacencyLists.size());
            final Iterator<Vertex<V>> it = this.adjacencyLists.keySet().iterator();
            for (int row = 0; it.hasNext(); row++) {
                final double multipliedRow = Math.round(multiplier * row * 10.0) / 10.0;
                for (int col = 0; col < limit && it.hasNext(); col++) {
                    final double multipliedCol = Math.round(multiplier * col * 10.0) / 10.0;
                    LaTeXUtils.printVertex(
                        it.next(),
                        "[node]",
                        "at (" + multipliedCol + "," + multipliedRow + ") ",
                        writer
                    );
                }
            }
        } else {
            for (final Entry<GridCoordinates, Vertex<V>> entry : this.grid.get().entrySet()) {
                final GridCoordinates pos = entry.getKey();
                final double multipliedCol = Math.round(multiplier * pos.x * 10.0) / 10.0;
                final double multipliedRow = Math.round(multiplier * pos.y * 10.0) / 10.0;
                LaTeXUtils.printVertex(
                    entry.getValue(),
                    "[node]",
                    "at (" + multipliedCol + "," + multipliedRow + ") ",
                    writer
                );
            }
        }
        if (printMode != GraphPrintMode.NO_EDGES) {
            final boolean printEdgeLabels = printMode == GraphPrintMode.ALL;
            for (final Entry<Vertex<V>, List<Edge<E, V>>> entry : this.adjacencyLists.entrySet()) {
                final Vertex<V> fromVertex = entry.getKey();
                final BigInteger from = fromVertex.id;
                for (final Edge<E, V> edge : entry.getValue()) {
                    if (
                        toHighlight != null
                        && toHighlight.contains(new Pair<Vertex<V>, Edge<E, V>>(fromVertex, edge))
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
     * Creates a graph where the vertices are positioned according to a grid. The graph is generated from the specified
     * reader according to the following format:
     * Each even line (counting starts at 0) is of the form:
     * <vertex label>,<from edge label>|<to edge label>,<vertex label>,<from edge label>|<to edge label>,...,<vertex label>
     * Each odd line is of the form:
     * <vertical from edge label>|<vertical to edge label>,<diagonal from edge label>|<diagonal to edge label>,...
     * The diagonal edges follow the pattern:
     * \/\/...
     * /\/\...
     * ...
     * So the first (upper left) diagonal edge is descending.
     * An empty label text corresponds to a non-existing vertex or edge. If there is an edge where at least one of its
     * adjacent vertices does not exist, an IOException is thrown.
     * @param reader The reader from which to read the input.
     * @param vertexParser Parser for vertex labels.
     * @param edgeParser Parser for edge labels.
     * @throws IOException If some error occurs during input, the input does not comply to the expected format, or if
     *                     we have an edge adjacent to a non-existing vertex.
     */
    public void setGraphFromInput(
        final BufferedReader reader,
        final LabelParser<V> vertexParser,
        final LabelParser<E> edgeParser
    ) throws IOException {
        String line = reader.readLine();
        final Map<GridCoordinates, Vertex<V>> newGrid =
            new LinkedHashMap<GridCoordinates, Vertex<V>>();
        final Map<GridCoordinates, List<Pair<E, GridCoordinates>>> edges =
            new LinkedHashMap<GridCoordinates, List<Pair<E, GridCoordinates>>>();
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
                        // vertex label
                        final String label = commaSeparated[i].trim();
                        if (!"".equals(label)) {
                            newGrid.put(
                                new GridCoordinates(i / 2, row),
                                new Vertex<V>(Optional.of(vertexParser.parse(label)))
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
                            new GridCoordinates(i / 2, row),
                            new GridCoordinates((i / 2) + 1, row)
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
                            new GridCoordinates(i / 2, row),
                            new GridCoordinates(i / 2, row + 1)
                        );
                    } else {
                        // diagonal edges
                        if ((num % 4) + (i % 4) == 4) {
                            // ascending diagonal
                            this.addEdges(
                                edges,
                                edgeLabels,
                                edgeParser,
                                new GridCoordinates(i / 2, row + 1),
                                new GridCoordinates((i / 2) + 1, row)
                            );
                        } else {
                            // descending diagonal
                            this.addEdges(
                                edges,
                                edgeLabels,
                                edgeParser,
                                new GridCoordinates(i / 2, row),
                                new GridCoordinates((i / 2) + 1, row + 1)
                            );
                        }
                    }
                }
            }
            line = reader.readLine();
        }
        // now check whether we have edges to non-existing vertices
        final Set<GridCoordinates> vertices = newGrid.keySet();
        if (!vertices.containsAll(edges.keySet())) {
            throw new IOException("Edge from non-existent vertex detected!");
        }
        for (final List<Pair<E, GridCoordinates>> list : edges.values()) {
            for (final Pair<E, GridCoordinates> pair : list) {
                if (!vertices.contains(pair.y)) {
                    throw new IOException("Edge to non-existent vertex detected!");
                }
            }
        }
        // everything is alright - build the graph
        this.adjacencyLists.clear();
        // mirror the positions vertically as TikZ positions are mirrored that way compared to the input format
        final Map<GridCoordinates, Vertex<V>> mirroredGrid =
            new LinkedHashMap<GridCoordinates, Vertex<V>>();
        for (final GridCoordinates pos : vertices) {
            final Vertex<V> vertex = newGrid.get(pos);
            this.adjacencyLists.put(vertex, new ArrayList<Edge<E, V>>());
            mirroredGrid.put(new GridCoordinates(pos.x, maxRow - pos.y), vertex);
        }
        for (final Entry<GridCoordinates, List<Pair<E, GridCoordinates>>> edge : edges.entrySet()) {
            final List<Edge<E, V>> list = this.adjacencyLists.get(newGrid.get(edge.getKey()));
            for (final Pair<E, GridCoordinates> pair : edge.getValue()) {
                list.add(new Edge<E, V>(pair.x, newGrid.get(pair.y)));
            }
        }
        this.grid = Optional.of(mirroredGrid);
    }

    public void setGrid(final Map<GridCoordinates, Vertex<V>> newGrid) {
        this.setGrid(Optional.of(newGrid));
    }

    public void setGrid(final Optional<Map<GridCoordinates, Vertex<V>>> newGrid) {
        this.grid = newGrid;
    }

    /**
     * Help method to add edges to the internal grid representation during parsing of an input reader.
     * @param edges The edges.
     * @param edgeLabels An array of length 2 containing the label text to be parsed to edge labels.
     * @param edgeParser The parser for the edge labels.
     * @param fromPos The position of the from vertex in the grid.
     * @param toPos The position of the to vertex in the grid.
     * @throws IOException If an edge label cannot be parsed.
     */
    private void addEdges(
        final Map<GridCoordinates, List<Pair<E, GridCoordinates>>> edges,
        final String[] edgeLabels,
        final LabelParser<E> edgeParser,
        final GridCoordinates fromPos,
        final GridCoordinates toPos
    ) throws IOException {
        final String fromEdge = edgeLabels[0].trim();
        final String toEdge = edgeLabels[1].trim();
        if (!"".equals(fromEdge)) {
            if (!edges.containsKey(fromPos)) {
                edges.put(fromPos, new ArrayList<Pair<E, GridCoordinates>>());
            }
            edges.get(
                fromPos
            ).add(new Pair<E, GridCoordinates>(edgeParser.parse(fromEdge), toPos));
        }
        if (!"".equals(toEdge)) {
            if (!edges.containsKey(toPos)) {
                edges.put(toPos, new ArrayList<Pair<E, GridCoordinates>>());
            }
            edges.get(
                toPos
            ).add(new Pair<E, GridCoordinates>(edgeParser.parse(toEdge), fromPos));
        }
    }

}
