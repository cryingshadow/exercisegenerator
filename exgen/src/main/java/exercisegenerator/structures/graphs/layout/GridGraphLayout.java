package exercisegenerator.structures.graphs.layout;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

public class GridGraphLayout<V extends Comparable<V>, E extends Comparable<E>> implements GraphLayout<V, E, Integer> {

    public static class GridGraphLayoutBuilder<V extends Comparable<V>, E extends Comparable<E>> {

        private boolean directed;

        private boolean drawEdgeLabels;

        private boolean drawEdges;

        private final Map<Vertex<V>, Map<Vertex<V>, EdgeStyle>> edgeStyles;

        private final Map<Vertex<V>, Coordinates2D<Integer>> nodeCoordinates;

        private final Map<Coordinates2D<Integer>, Vertex<V>> nodesAtCoordinates;

        private GridGraphLayoutBuilder() {
            this.directed = false;
            this.drawEdges = true;
            this.drawEdgeLabels = true;
            this.nodeCoordinates = new LinkedHashMap<Vertex<V>, Coordinates2D<Integer>>();
            this.nodesAtCoordinates = new LinkedHashMap<Coordinates2D<Integer>, Vertex<V>>();
            this.edgeStyles = new LinkedHashMap<Vertex<V>, Map<Vertex<V>, EdgeStyle>>();

        }

        public GridGraphLayoutBuilder<V, E> addVertex(
            final Vertex<V> vertex,
            final Coordinates2D<Integer> coordinates
        ) {
            this.nodeCoordinates.put(vertex, coordinates);
            this.nodesAtCoordinates.put(coordinates, vertex);
            return this;
        }

        public GridGraphLayout<V, E> build() {
            return new GridGraphLayout<V, E>(
                this.makeCoordinatesNonNegative(),
                Map.of(),
                this.edgeStyles,
                this.directed,
                this.drawEdges,
                this.drawEdgeLabels
            );
        }

        public Coordinates2D<Integer> getCoordinates(final Vertex<V> vertex) {
            return this.nodeCoordinates.get(vertex);
        }

        public List<Coordinates2D<Integer>> getFreePositions(final Vertex<V> vertex) {
            return GridGraphLayout.surroundingCoordinates(this.nodeCoordinates.get(vertex))
                .filter(c -> !this.nodesAtCoordinates.containsKey(c))
                .toList();
        }

        public List<Vertex<V>> getSurroundingVertices(final Coordinates2D<Integer> coordinates) {
            return GridGraphLayout.surroundingCoordinates(coordinates)
                .filter(this.nodesAtCoordinates::containsKey)
                .map(this.nodesAtCoordinates::get)
                .toList();
        }

        public GridGraphLayoutBuilder<V, E> setDirected(final boolean directed) {
            this.directed = directed;
            return this;
        }

        public GridGraphLayoutBuilder<V, E> setDrawEdgeLabels(final boolean drawEdgeLabels) {
            this.drawEdgeLabels = drawEdgeLabels;
            return this;
        }

        public GridGraphLayoutBuilder<V, E> setDrawEdges(final boolean drawEdges) {
            this.drawEdges = drawEdges;
            return this;
        }

        public void setEdgeStyle(final Vertex<V> from, final Vertex<V> to, final EdgeStyle edgeStyle) {
            if (!this.edgeStyles.containsKey(from)) {
                this.edgeStyles.put(from, new LinkedHashMap<Vertex<V>, EdgeStyle>());
            }
            this.edgeStyles.get(from).put(to, edgeStyle);
        }

        private Map<Vertex<V>, Coordinates2D<Integer>> makeCoordinatesNonNegative() {
            int minX = 0;
            int minY = 0;
            for (final Coordinates2D<Integer> coordinates : this.nodesAtCoordinates.keySet()) {
                minX = Math.min(minX, coordinates.x());
                minY = Math.min(minY, coordinates.y());
            }
            if ((minX + minY) % 2 == 1) {
                minX--;
            }
            final Map<Vertex<V>, Coordinates2D<Integer>> result =
                new LinkedHashMap<Vertex<V>, Coordinates2D<Integer>>();
            for (final Map.Entry<Vertex<V>, Coordinates2D<Integer>> entry : this.nodeCoordinates.entrySet()) {
                result.put(entry.getKey(), entry.getValue().plus(-minX, -minY));
            }
            return result;
        }

    }

    public static <V extends Comparable<V>, E extends Comparable<E>>
    GridGraphLayout.GridGraphLayoutBuilder<V, E> builder() {
        return new GridGraphLayoutBuilder<V, E>();
    }

    public static Stream<Coordinates2D<Integer>> surroundingCoordinates(final Coordinates2D<Integer> coordinates) {
        return (coordinates.x() + coordinates.y()) % 2 == 0 ?
            Stream.of(
                coordinates.plus(1, 0),
                coordinates.plus(0, 1),
                coordinates.plus(0, -1),
                coordinates.plus(-1, 0),
                coordinates.plus(-1, -1),
                coordinates.plus(-1, 1),
                coordinates.plus(1, -1),
                coordinates.plus(1, 1)
            ) :
                Stream.of(
                    coordinates.plus(1, 0),
                    coordinates.plus(0, 1),
                    coordinates.plus(0, -1),
                    coordinates.plus(-1, 0)
                );
    }

    protected final boolean directed;

    protected final boolean drawEdgeLabels;

    protected final boolean drawEdges;

    protected final Map<Vertex<V>, Map<Vertex<V>, EdgeStyle>> edgeStyles;

    protected final Map<Vertex<V>, Coordinates2D<Integer>> nodeCoordinates;

    protected final Map<Vertex<V>, List<Edge<E, V>>> toHighlight;

    protected GridGraphLayout(
        final Map<Vertex<V>, Coordinates2D<Integer>> nodeCoordinates,
        final Map<Vertex<V>, List<Edge<E, V>>> toHighlight,
        final Map<Vertex<V>, Map<Vertex<V>, EdgeStyle>> edgeStyles,
        final boolean directed,
        final boolean drawEdges,
        final boolean drawEdgeLabels
    ) {
        this.directed = directed;
        this.drawEdges = drawEdges;
        this.drawEdgeLabels = drawEdgeLabels;
        this.nodeCoordinates = Collections.unmodifiableMap(nodeCoordinates);
        this.toHighlight = Collections.unmodifiableMap(toHighlight);
        this.edgeStyles = Collections.unmodifiableMap(edgeStyles);
    }

    public <F extends Comparable<F>> GridGraphLayout<V, F> convertEdgeLabelType() {
        return new GridGraphLayout<V, F>(
            this.nodeCoordinates,
            Map.of(),
            Map.of(),
            this.directed,
            this.drawEdges,
            this.drawEdgeLabels
        );
    }

    @Override
    public Coordinates2D<Integer> getPosition(final Vertex<V> vertex) {
        return this.nodeCoordinates.get(vertex);
    }

    public GridGraphLayout<V, E> highlight(final Collection<Pair<Vertex<V>, Edge<E, V>>> edges) {
        final Map<Vertex<V>, List<Edge<E, V>>> newToHighlight =
            new LinkedHashMap<Vertex<V>, List<Edge<E, V>>>(this.toHighlight);
        for (final Pair<Vertex<V>, Edge<E, V>> entry : edges) {
            if (!newToHighlight.containsKey(entry.x)) {
                newToHighlight.put(entry.x, new LinkedList<Edge<E, V>>());
            }
            newToHighlight.get(entry.x).add(entry.y);
        }
        return new GridGraphLayout<V, E>(
            this.nodeCoordinates,
            newToHighlight,
            this.edgeStyles,
            this.directed,
            this.drawEdges,
            this.drawEdgeLabels
        );
    }

    @Override
    public void printTikZ(final Graph<V, E> graph, final BufferedWriter writer) throws IOException {
        LaTeXUtils.printTikzBeginning(this.directed ? TikZStyle.GRAPH : TikZStyle.SYM_GRAPH, writer);
        for (final Vertex<V> vertex : graph.getVertices()) {
            writer.write(this.toTikZ(vertex, graph.isStartVertex(vertex), graph.isEndVertex(vertex)));
        }
        for (final Map.Entry<Vertex<V>, Set<Edge<E, V>>> entry : graph.getEdges().entrySet()) {
            for (final Edge<E, V> edge : entry.getValue()) {
                writer.write(this.toTikZ(entry.getKey(), edge));
            }
        }
        LaTeXUtils.printTikzEnd(writer);
    }

    public GridGraphLayout<V, E> setDirected(final boolean directed) {
        return new GridGraphLayout<V, E>(
            this.nodeCoordinates,
            this.toHighlight,
            this.edgeStyles,
            directed,
            this.drawEdges,
            this.drawEdgeLabels
        );
    }

    public GridGraphLayout<V, E> setDrawEdgeLabels(final boolean drawEdgeLabels) {
        return new GridGraphLayout<V, E>(
            this.nodeCoordinates,
            this.toHighlight,
            this.edgeStyles,
            this.directed,
            this.drawEdges,
            drawEdgeLabels
        );
    }

    public GridGraphLayout<V, E> setDrawEdges(final boolean drawEdges) {
        return new GridGraphLayout<V, E>(
            this.nodeCoordinates,
            this.toHighlight,
            this.edgeStyles,
            this.directed,
            drawEdges,
            this.drawEdgeLabels
        );
    }

    public StretchedGridGraphLayout<V, E> stretch(final double factor) {
        return new StretchedGridGraphLayout<V, E>(
            this.nodeCoordinates,
            this.toHighlight,
            this.edgeStyles,
            this.directed,
            this.drawEdges,
            this.drawEdgeLabels,
            factor
        );
    }

    protected String toTikZ(final Vertex<V> vertex, final boolean startVertex, final boolean endVertex) {
        final Coordinates2D<Integer> coordinates = this.nodeCoordinates.get(vertex);
        final String id = vertex.id().toString();
        return String.format(
            "\\node[%s] (n%s) at (%d,%d) {%s};%s",
            endVertex ? "endnode" : "node",
            id,
            coordinates.x(),
            coordinates.y(),
            vertex.label().map(label -> label.toString()).orElse(""),
            startVertex ? GraphLayout.startNodeDecoration(id) : Main.lineSeparator
        );
    }

    private String defaultEdgeStyle(final Vertex<V> from, final Edge<E, V> edge) {
        return
            this.directed ?
                (
                    this.toHighlight.getOrDefault(from, List.of()).contains(edge) ?
                        TikZStyle.EDGE_HIGHLIGHT_STYLE :
                            TikZStyle.EDGE_STYLE
                ).style :
                    (
                        this.toHighlight.getOrDefault(from, List.of()).contains(edge) ?
                            TikZStyle.SYM_EDGE_HIGHLIGHT_STYLE :
                                TikZStyle.SYM_EDGE_STYLE
                    ).style;
    }

    private String toTikZ(final Vertex<V> from, final Edge<E, V> edge) {
        if (!this.drawEdges || !this.directed && edge.to().id().compareTo(from.id()) < 0) {
            return "";
        }
        if (this.edgeStyles.containsKey(from)) {
            final Map<Vertex<V>, EdgeStyle> styles = this.edgeStyles.get(from);
            if (styles.containsKey(edge.to())) {
                final EdgeStyle style = styles.get(edge.to());
                return GraphLayout.edgeFormat(
                    style.edgeStyle().isBlank() ?
                        this.defaultEdgeStyle(from, edge) :
                            String.format(
                                "[%s]",
                                this.toHighlight.getOrDefault(from, List.of()).contains(edge) ?
                                    style.edgeStyle() + ", very thick, red":
                                        style.edgeStyle()
                            ),
                    from.id().toString(),
                    this.drawEdgeLabels && edge.label().isPresent() ? List.of(edge.label().get()) : List.of(),
                    style.labelStyle(),
                    edge.to().id().toString()
                );
            }
        }
        return GraphLayout.edgeFormat(
            this.defaultEdgeStyle(from, edge),
            from.id().toString(),
            this.drawEdgeLabels && edge.label().isPresent() ? List.of(edge.label().get()) : List.of(),
            edge.to().id().toString()
        );
    }

}
