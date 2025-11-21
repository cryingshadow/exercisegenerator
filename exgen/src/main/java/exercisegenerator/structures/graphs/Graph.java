package exercisegenerator.structures.graphs;

import java.io.*;
import java.math.*;
import java.util.*;
import java.util.Map.*;
import java.util.function.*;
import java.util.stream.*;

import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.layout.*;
import exercisegenerator.structures.graphs.layout.GridGraphLayout.*;

public class Graph<V extends Comparable<V>, E extends Comparable<E>> {

    public static<V extends Comparable<V>, E extends Comparable<E>> Graph<V, E> create(
        final AdjacencySets<V, E> adjacencySets
    ) {
        final Graph<V, E> result = new Graph<V, E>();
        result.adjacencySets = new AdjacencySets<V, E>(adjacencySets);
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
    public static <V extends Comparable<V>, E extends Comparable<E>> GraphWithLayout<V, E, Integer> create(
        final BufferedReader reader,
        final LabelParser<V> vertexParser,
        final LabelParser<E> edgeParser
    ) throws IOException {
        String line = reader.readLine();
        while (line.startsWith("!")) {
            line = reader.readLine();
        }
        final Map<Coordinates2D<Integer>, Vertex<V>> verticesAtPositions =
            new LinkedHashMap<Coordinates2D<Integer>, Vertex<V>>();
        final Map<Coordinates2D<Integer>, List<Pair<Optional<E>, Coordinates2D<Integer>>>> edges =
            new LinkedHashMap<Coordinates2D<Integer>, List<Pair<Optional<E>, Coordinates2D<Integer>>>>();
        int maxRow = 0;
        for (int lineNumber = 0; line != null; lineNumber++) {
            if (line.isBlank()) {
                break;
            }
            final String[] commaSeparated = line.split(",");
            final int row = lineNumber / 2;
            maxRow = Math.max(maxRow, row);
            if (lineNumber % 2 == 0) {
                for (int i = 0; i < commaSeparated.length; i++) {
                    if (i % 2 == 0) {
                        final String vertexLabel = commaSeparated[i].trim();
                        if (!"".equals(vertexLabel)) {
                            verticesAtPositions.put(
                                new Coordinates2D<Integer>(i / 2, row),
                                new Vertex<V>(vertexParser.parse(vertexLabel))
                            );
                        }
                    } else {
                        final String[] edgeLabels = commaSeparated[i].split("\\|");
                        if (edgeLabels.length != 2) {
                            throw new IOException(
                                String.format("Edge labels do not match expected format (%d,%d)!", i, lineNumber)
                            );
                        }
                        Graph.addEdges(
                            edges,
                            edgeLabels,
                            edgeParser,
                            new Coordinates2D<Integer>(i / 2, row),
                            new Coordinates2D<Integer>((i / 2) + 1, row)
                        );
                    }
                }
            } else {
                for (int i = 0; i < commaSeparated.length; i++) {
                    final String[] edgeLabels = commaSeparated[i].split("\\|");
                    if (edgeLabels.length != 2) {
                        throw new IOException("Edge labels do not match expected format (" + i + "," + lineNumber + ")!");
                    }
                    if (i % 2 == 0) {
                        // no diagonal edges
                        Graph.addEdges(
                            edges,
                            edgeLabels,
                            edgeParser,
                            new Coordinates2D<Integer>(i / 2, row),
                            new Coordinates2D<Integer>(i / 2, row + 1)
                        );
                    } else {
                        // diagonal edges
                        if ((lineNumber % 4) + (i % 4) == 4) {
                            // ascending diagonal
                            Graph.addEdges(
                                edges,
                                edgeLabels,
                                edgeParser,
                                new Coordinates2D<Integer>(i / 2, row + 1),
                                new Coordinates2D<Integer>((i / 2) + 1, row)
                            );
                        } else {
                            // descending diagonal
                            Graph.addEdges(
                                edges,
                                edgeLabels,
                                edgeParser,
                                new Coordinates2D<Integer>(i / 2, row),
                                new Coordinates2D<Integer>((i / 2) + 1, row + 1)
                            );
                        }
                    }
                }
            }
            line = reader.readLine();
        }
        final Set<Coordinates2D<Integer>> vertexPositions = verticesAtPositions.keySet();
        if (Graph.someVertexIsMissingForAnEdge(vertexPositions, edges)) {
            throw new IOException("Edge from/to non-existent vertex detected!");
        }
        final Graph<V, E> graph = new Graph<V, E>();
        // mirror the positions vertically as TikZ positions are mirrored that way compared to the input format
        final GridGraphLayoutBuilder<V, E> layoutBuilder = GridGraphLayout.<V, E>builder().setDirected(true);
        for (final Coordinates2D<Integer> pos : vertexPositions) {
            final Vertex<V> vertex = verticesAtPositions.get(pos);
            graph.adjacencySets.put(vertex, new TreeSet<Edge<E, V>>());
            layoutBuilder.addVertex(vertex, new Coordinates2D<Integer>(pos.x(), maxRow - pos.y()));
        }
        for (
            final Entry<Coordinates2D<Integer>, List<Pair<Optional<E>, Coordinates2D<Integer>>>> edge : edges.entrySet()
        ) {
            final Set<Edge<E, V>> set = graph.adjacencySets.get(verticesAtPositions.get(edge.getKey()));
            for (final Pair<Optional<E>, Coordinates2D<Integer>> pair : edge.getValue()) {
                set.add(new Edge<E, V>(pair.x, verticesAtPositions.get(pair.y)));
            }
        }
        return new GraphWithLayout<V, E, Integer>(graph, layoutBuilder.build());
    }

    public static<V extends Comparable<V>, E extends Comparable<E>> Graph<V, E> create(
        final Map<Vertex<V>, ? extends Set<Edge<E, V>>> adjacencySets
    ) {
        final Graph<V, E> result = new Graph<V, E>();
        result.adjacencySets = new AdjacencySets<V, E>(adjacencySets);
        return result;
    }

    private static <E extends Comparable<E>> void addEdges(
        final Map<Coordinates2D<Integer>, List<Pair<Optional<E>, Coordinates2D<Integer>>>> edges,
        final String[] edgeLabels,
        final LabelParser<E> edgeParser,
        final Coordinates2D<Integer> fromPos,
        final Coordinates2D<Integer> toPos
    ) throws IOException {
        final String fromEdge = edgeLabels[0].trim();
        final String toEdge = edgeLabels[1].trim();
        if (!"".equals(fromEdge)) {
            if (!edges.containsKey(fromPos)) {
                edges.put(fromPos, new ArrayList<Pair<Optional<E>, Coordinates2D<Integer>>>());
            }
            edges.get(
                fromPos
            ).add(new Pair<Optional<E>, Coordinates2D<Integer>>(Optional.of(edgeParser.parse(fromEdge)), toPos));
        }
        if (!"".equals(toEdge)) {
            if (!edges.containsKey(toPos)) {
                edges.put(toPos, new ArrayList<Pair<Optional<E>, Coordinates2D<Integer>>>());
            }
            edges.get(
                toPos
            ).add(new Pair<Optional<E>, Coordinates2D<Integer>>(Optional.of(edgeParser.parse(toEdge)), fromPos));
        }
    }

    private static <E extends Comparable<E>> boolean someVertexIsMissingForAnEdge(
        final Set<Coordinates2D<Integer>> vertexPositions,
        final Map<Coordinates2D<Integer>, List<Pair<Optional<E>, Coordinates2D<Integer>>>> edges
    ) {
        if (!vertexPositions.containsAll(edges.keySet())) {
            return true;
        }
        for (final List<Pair<Optional<E>, Coordinates2D<Integer>>> list : edges.values()) {
            for (final Pair<Optional<E>, Coordinates2D<Integer>> pair : list) {
                if (!vertexPositions.contains(pair.y)) {
                    return true;
                }
            }
        }
        return false;
    }

    private AdjacencySets<V, E> adjacencySets;

    private final Set<Vertex<V>> endVertices;

    private final Set<Vertex<V>> startVertices;

    public Graph() {
        this.adjacencySets = new AdjacencySets<V, E>();
        this.startVertices = new TreeSet<Vertex<V>>();
        this.endVertices = new TreeSet<Vertex<V>>();
    }

    private Graph(
        final AdjacencySets<V, E> adjacencySets,
        final Set<Vertex<V>> startVertices,
        final Set<Vertex<V>> endVertices
    ) {
        this.adjacencySets = adjacencySets;
        this.startVertices = startVertices;
        this.endVertices = endVertices;
    }

    public void addEdge(final Vertex<V> from, final Optional<E> label, final Vertex<V> to) {
        if (from == null || to == null) {
            throw new NullPointerException();
        }
        this.adjacencySets.addEdge(from, label, to);
    }

    public void addVertex(final Vertex<V> vertex) {
        if (!this.adjacencySets.containsKey(vertex)) {
            this.adjacencySets.put(vertex, new TreeSet<Edge<E, V>>());
        }
    }

    public Graph<V, E> copy(final Function<E, E> labelCopy) {
        final Graph<V, E> result = this.nodeCopy();
        for (final Entry<Vertex<V>, Set<Edge<E, V>>> entry : this.adjacencySets.entrySet()) {
            for (final Edge<E, V> edge : entry.getValue()) {
                result.addEdge(
                    entry.getKey(),
                    edge.label().isEmpty() ? Optional.empty() : Optional.of(labelCopy.apply(edge.label().get())),
                    edge.to()
                );
            }
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object o) {
        if (o instanceof Graph) {
            final Graph<V,E> other = (Graph<V,E>)o;
            return this.adjacencySets.equals(other.adjacencySets);
        }
        return false;
    }

    public Set<Edge<E, V>> getAdjacencySet(final Vertex<V> vertex) {
        final Set<Edge<E, V>> set = this.adjacencySets.get(vertex);
        if (set == null) {
            return null;
        }
        return new TreeSet<Edge<E, V>>(set);
    }

    public Set<Vertex<V>> getAdjacentVertices(final Vertex<V> vertex) {
        return this.getAdjacencySet(vertex).stream().map(edge -> edge.to()).collect(Collectors.toSet());
    }

    public Set<UndirectedEdge<V, E>> getAllUndirectedEdges() {
        final Set<UndirectedEdge<V, E>> result = new TreeSet<UndirectedEdge<V, E>>();
        final List<Pair<BigInteger,BigInteger>> finishedVertexPairs =
            new ArrayList<Pair<BigInteger,BigInteger>>();
        for (final Entry<Vertex<V>, Set<Edge<E, V>>> entry : this.adjacencySets.entrySet()) {
            for (final Edge<E, V> edge : entry.getValue()) {
                final Pair<BigInteger,BigInteger> reverseVertexPair =
                    new Pair<BigInteger,BigInteger>(edge.to().id(), entry.getKey().id());
                if (!finishedVertexPairs.contains(reverseVertexPair)) {
                    result.add(new UndirectedEdge<V, E>(entry.getKey(), edge.label(), edge.to()));
                    finishedVertexPairs.add(new Pair<BigInteger,BigInteger>(entry.getKey().id(), edge.to().id()));
                }
            }
        }
        return result;
    }

    public Map<Vertex<V>, Set<Edge<E, V>>> getEdges() {
        final Map<Vertex<V>, Set<Edge<E, V>>> result = new TreeMap<Vertex<V>, Set<Edge<E, V>>>();
        for (final Map.Entry<Vertex<V>, Set<Edge<E, V>>> entry : this.adjacencySets.entrySet()) {
            result.put(entry.getKey(), new TreeSet<Edge<E, V>>(entry.getValue()));
        }
        return result;
    }

    public Set<Edge<E, V>> getEdges(final Vertex<V> from, final Vertex<V> to) {
        final Set<Edge<E, V>> res = new TreeSet<Edge<E, V>>();
        final Set<Edge<E, V>> set = this.adjacencySets.get(from);
        if (set != null) {
            for (final Edge<E, V> edge : set) {
                if (to.equals(edge.to())) {
                    res.add(edge);
                }
            }
        }
        return res;
    }

    public Vertex<V> getVertexWithUniqueLabel(final V label) {
        return this.adjacencySets
            .keySet()
            .stream()
            .filter(vertex -> vertex.label().isPresent() && vertex.label().get().equals(label))
            .findAny()
            .get();
    }

    public Set<Vertex<V>> getVertices() {
        return new TreeSet<Vertex<V>>(this.adjacencySets.keySet());
    }

    public Set<Vertex<V>> getVerticesWithLabel(final V label) {
        final Set<Vertex<V>> res = new TreeSet<Vertex<V>>();
        for (final Vertex<V> vertex : this.adjacencySets.keySet()) {
            if (vertex.label().isPresent() && label.equals(vertex.label().get())) {
                res.add(vertex);
            }
        }
        return res;
    }

    @Override
    public int hashCode() {
        return this.adjacencySets.hashCode() * 3;
    }

    public boolean isEndVertex(final Vertex<V> vertex) {
        return this.endVertices.contains(vertex);
    }

    public boolean isStartVertex(final Vertex<V> vertex) {
        return this.startVertices.contains(vertex);
    }

    public boolean logicallyEquals(final Graph<V,E> other) {
        return this.adjacencySets.logicallyEquals(other.adjacencySets);
    }

    public Graph<V, E> nodeCopy() {
        final AdjacencySets<V, E> emptySets = new AdjacencySets<V, E>();
        for (final Vertex<V> vertex : this.adjacencySets.keySet()) {
            emptySets.put(vertex, new TreeSet<Edge<E,V>>());
        }
        return new Graph<V, E>(
            emptySets,
            new TreeSet<Vertex<V>>(this.startVertices),
            new TreeSet<Vertex<V>>(this.endVertices)
        );
    }

    public <T extends Number> void printTikZ(
        final GraphLayout<V, E, T> layout,
        final BufferedWriter writer
    ) throws IOException {
        layout.printTikZ(this, writer);
    }

    public void replaceEdgeLabel(final Vertex<V> from, final E label, final Vertex<V> to) {
        this.adjacencySets.put(
            from,
            this
            .adjacencySets
            .get(from)
            .stream()
            .map(edge -> edge.to().equals(to) ? new Edge<E, V>(Optional.of(label), to) : edge)
            .collect(Collectors.toCollection(TreeSet::new))
        );
    }

    public void setStartVertex(final Vertex<V> vertex) {
        this.startVertices.add(vertex);
    }

    @Override
    public String toString() {
        return this.adjacencySets.toString();
    }

    public Graph<V, E> transpose() {
        final AdjacencySets<V, E> transposed = new AdjacencySets<V, E>();
        for (final Map.Entry<Vertex<V>, Set<Edge<E, V>>> entry : this.adjacencySets.entrySet()) {
            for (final Edge<E, V> edge : entry.getValue()) {
                transposed.addEdge(edge.to(), edge.label(), entry.getKey());
            }
            if (entry.getValue().isEmpty() && !transposed.containsKey(entry.getKey())) {
                transposed.put(entry.getKey(), new TreeSet<Edge<E, V>>());
            }
        }
        return new Graph<V, E>(transposed, this.startVertices, this.endVertices);
    }

}
