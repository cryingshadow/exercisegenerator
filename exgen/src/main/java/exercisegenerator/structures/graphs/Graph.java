package exercisegenerator.structures.graphs;

import java.io.*;
import java.math.*;
import java.util.*;
import java.util.Map.*;
import java.util.function.*;
import java.util.stream.*;

import exercisegenerator.io.*;
import exercisegenerator.structures.*;

/**
 * Directed graph implemented by adjacency lists. It supports arbitrary edge and vertex labels and multiple edges
 * between the same vertices.
 * @param <V> The type of the vertex labels.
 * @param <E> The type of the edge labels.
 */
public class Graph<V, E> {

    public static<V, E> Graph<V, E> create(final AdjacencyLists<V, E> adjacencyLists, final Grid<V> grid) {
        final Graph<V, E> result = new Graph<V, E>();
        result.adjacencyLists = new AdjacencyLists<V, E>(adjacencyLists);
        result.setGrid(grid);
        return result;
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
    public static <V, E> Graph<V, E> create(
        final BufferedReader reader,
        final LabelParser<V> vertexParser,
        final LabelParser<E> edgeParser
    ) throws IOException {
        final Graph<V, E> result = new Graph<V, E>();
        String line = reader.readLine();
        while (line.startsWith("!")) {
            line = reader.readLine();
        }
        final Map<GridCoordinates, Vertex<V>> newGrid =
            new LinkedHashMap<GridCoordinates, Vertex<V>>();
        final Map<GridCoordinates, List<Pair<Optional<E>, GridCoordinates>>> edges =
            new LinkedHashMap<GridCoordinates, List<Pair<Optional<E>, GridCoordinates>>>();
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
                        result.addEdges(
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
                        result.addEdges(
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
                            result.addEdges(
                                edges,
                                edgeLabels,
                                edgeParser,
                                new GridCoordinates(i / 2, row + 1),
                                new GridCoordinates((i / 2) + 1, row)
                            );
                        } else {
                            // descending diagonal
                            result.addEdges(
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
        for (final List<Pair<Optional<E>, GridCoordinates>> list : edges.values()) {
            for (final Pair<Optional<E>, GridCoordinates> pair : list) {
                if (!vertices.contains(pair.y)) {
                    throw new IOException("Edge to non-existent vertex detected!");
                }
            }
        }
        // everything is alright - build the graph
        result.adjacencyLists.clear();
        // mirror the positions vertically as TikZ positions are mirrored that way compared to the input format
        final Grid<V> mirroredGrid = new Grid<V>();
        for (final GridCoordinates pos : vertices) {
            final Vertex<V> vertex = newGrid.get(pos);
            result.adjacencyLists.put(vertex, new ArrayList<Edge<E, V>>());
            mirroredGrid.put(new GridCoordinates(pos.x, maxRow - pos.y), vertex);
        }
        for (final Entry<GridCoordinates, List<Pair<Optional<E>, GridCoordinates>>> edge : edges.entrySet()) {
            final List<Edge<E, V>> list = result.adjacencyLists.get(newGrid.get(edge.getKey()));
            for (final Pair<Optional<E>, GridCoordinates> pair : edge.getValue()) {
                list.add(new Edge<E, V>(pair.x, newGrid.get(pair.y)));
            }
        }
        result.grid = Optional.of(mirroredGrid);
        return result;
    }

    public static<V, E> Graph<V, E> create(final Map<Vertex<V>, ? extends List<Edge<E, V>>> adjacencyLists) {
        final Graph<V, E> result = new Graph<V, E>();
        result.adjacencyLists = new AdjacencyLists<V, E>(adjacencyLists);
        return result;
    }

    private AdjacencyLists<V, E> adjacencyLists;

    private Optional<Grid<V>> grid;

    public Graph() {
        this.adjacencyLists = new AdjacencyLists<V, E>();
        this.grid = Optional.empty();
    }

    private Graph(final AdjacencyLists<V, E> adjacencyLists, final Optional<Grid<V>> grid) {
        this.adjacencyLists = adjacencyLists;
        this.grid = grid;
    }

    public void addEdge(final Vertex<V> from, final Optional<E> label, final Vertex<V> to) {
        if (from == null || to == null) {
            throw new NullPointerException();
        }
        this.grid = Optional.empty();
        this.adjacencyLists.addEdge(from, label, to);
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

    public Graph<V, E> copy(final Function<E, E> labelCopy) {
        final Graph<V, E> result = this.nodeCopy();
        for (final Entry<Vertex<V>, List<Edge<E, V>>> entry : this.adjacencyLists.entrySet()) {
            for (final Edge<E, V> edge : entry.getValue()) {
                result.addEdge(
                    entry.getKey(),
                    edge.label.isEmpty() ? Optional.empty() : Optional.of(labelCopy.apply(edge.label.get())),
                    edge.to
                );
            }
        }
        result.grid = this.grid;
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object o) {
        if (o instanceof Graph) {
            final Graph<V,E> other = (Graph<V,E>)o;
            return this.adjacencyLists.equals(other.adjacencyLists) && this.grid.equals(other.grid);
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
        return this.getAdjacencyList(vertex).stream().map(edge -> edge.to).toList();
    }

    public List<UndirectedEdge<V, E>> getAllUndirectedEdges() {
        final List<UndirectedEdge<V, E>> result = new ArrayList<UndirectedEdge<V, E>>();
        final List<Pair<BigInteger,BigInteger>> finishedVertexPairs =
            new ArrayList<Pair<BigInteger,BigInteger>>();
        for (final Entry<Vertex<V>, List<Edge<E, V>>> entry : this.adjacencyLists.entrySet()) {
            for (final Edge<E, V> edge : entry.getValue()) {
                final Pair<BigInteger,BigInteger> reverseVertexPair =
                    new Pair<BigInteger,BigInteger>(edge.to.id, entry.getKey().id);
                if (!finishedVertexPairs.contains(reverseVertexPair)) {
                    result.add(new UndirectedEdge<V, E>(entry.getKey(), edge.label, edge.to));
                    finishedVertexPairs.add(new Pair<BigInteger,BigInteger>(entry.getKey().id, edge.to.id));
                }
            }
        }
        return result;
    }

    public Set<Edge<E, V>> getEdges(final Vertex<V> from, final Vertex<V> to) {
        final Set<Edge<E, V>> res = new LinkedHashSet<Edge<E, V>>();
        final List<Edge<E, V>> list = this.adjacencyLists.get(from);
        if (list != null) {
            for (final Edge<E, V> edge : list) {
                if (to.equals(edge.to)) {
                    res.add(edge);
                }
            }
        }
        return res;
    }

    public Optional<Grid<V>> getGrid() {
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

    @Override
    public int hashCode() {
        return this.adjacencyLists.hashCode() * this.grid.hashCode();
    }

    public boolean logicallyEquals(final Graph<V,E> other) {
        return this.adjacencyLists.logicallyEquals(other.adjacencyLists)
            && (this.grid.isEmpty() ?
                other.grid.isEmpty() :
                    other.grid.isPresent() && this.grid.get().logicallyEquals(other.grid.get()));
    }

    public Graph<V, E> nodeCopy() {
        final AdjacencyLists<V, E> emptyLists = new AdjacencyLists<V, E>();
        for (final Vertex<V> vertex : this.adjacencyLists.keySet()) {
            emptyLists.put(vertex, new ArrayList<Edge<E,V>>());
        }
        return new Graph<V, E>(emptyLists, this.grid);
    }

    public void printTikZ(
        final GraphPrintMode printMode,
        final double multiplier,
        final Set<FordFulkersonPathStep<V, E>> toHighlight,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printTikzBeginning(
            printMode == GraphPrintMode.UNDIRECTED ? TikZStyle.SYM_GRAPH : TikZStyle.GRAPH,
            writer
        );
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
            final boolean printEdgeLabels = printMode != GraphPrintMode.NO_EDGE_LABELS;
            final boolean directed = printMode != GraphPrintMode.UNDIRECTED;
            if (directed) {
                for (final Entry<Vertex<V>, List<Edge<E, V>>> entry : this.adjacencyLists.entrySet()) {
                    final Vertex<V> fromVertex = entry.getKey();
                    final BigInteger from = fromVertex.id;
                    for (final Edge<E, V> edge : entry.getValue()) {
                        if (
                            toHighlight != null
                            && toHighlight.contains(new FordFulkersonPathStep<V, E>(fromVertex, edge))
                        ) {
                            if (printEdgeLabels) {
                                LaTeXUtils.printEdge(
                                    TikZStyle.EDGE_HIGHLIGHT_STYLE,
                                    from,
                                    edge.label,
                                    edge.to.id,
                                    writer
                                );
                            } else {
                                LaTeXUtils.printEdge(
                                    TikZStyle.EDGE_HIGHLIGHT_STYLE,
                                    from,
                                    Optional.empty(),
                                    edge.to.id,
                                    writer
                                );
                            }
                        } else {
                            if (printEdgeLabels) {
                                LaTeXUtils.printEdge(TikZStyle.EDGE_STYLE, from, edge.label, edge.to.id, writer);
                            } else {
                                LaTeXUtils.printEdge(TikZStyle.EDGE_STYLE, from, Optional.empty(), edge.to.id, writer);
                            }
                        }
                    }
                }
            } else {
                for (final UndirectedEdge<V, E> edge : this.getAllUndirectedEdges()) {
                    LaTeXUtils.printEdge(TikZStyle.SYM_EDGE_STYLE, edge.from.id, edge.label, edge.to.id, writer);
                }
            }
        }
        LaTeXUtils.printTikzEnd(writer);
    }

    public void replaceEdgeLabel(final Vertex<V> from, final E label, final Vertex<V> to) {
        this.adjacencyLists.put(
            from,
            this
            .adjacencyLists
            .get(from)
            .stream()
            .map(edge -> edge.to.equals(to) ? new Edge<E, V>(Optional.of(label), to) : edge)
            .collect(Collectors.toCollection(ArrayList::new))
        );
    }

    public void setGrid(final Grid<V> newGrid) {
        this.setGrid(Optional.of(newGrid));
    }

    public void setGrid(final Optional<Grid<V>> newGrid) {
        this.grid = newGrid;
    }

    @Override
    public String toString() {
        return this.grid.isEmpty() ?
            this.adjacencyLists.toString() :
                String.format("(%s, %s)", this.adjacencyLists.toString(), this.grid.get().toString());
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
        final Map<GridCoordinates, List<Pair<Optional<E>, GridCoordinates>>> edges,
        final String[] edgeLabels,
        final LabelParser<E> edgeParser,
        final GridCoordinates fromPos,
        final GridCoordinates toPos
    ) throws IOException {
        final String fromEdge = edgeLabels[0].trim();
        final String toEdge = edgeLabels[1].trim();
        if (!"".equals(fromEdge)) {
            if (!edges.containsKey(fromPos)) {
                edges.put(fromPos, new ArrayList<Pair<Optional<E>, GridCoordinates>>());
            }
            edges.get(
                fromPos
            ).add(new Pair<Optional<E>, GridCoordinates>(Optional.of(edgeParser.parse(fromEdge)), toPos));
        }
        if (!"".equals(toEdge)) {
            if (!edges.containsKey(toPos)) {
                edges.put(toPos, new ArrayList<Pair<Optional<E>, GridCoordinates>>());
            }
            edges.get(
                toPos
            ).add(new Pair<Optional<E>, GridCoordinates>(Optional.of(edgeParser.parse(toEdge)), fromPos));
        }
    }

}
