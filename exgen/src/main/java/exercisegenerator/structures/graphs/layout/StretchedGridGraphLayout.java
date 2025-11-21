package exercisegenerator.structures.graphs.layout;

import java.util.*;

import exercisegenerator.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

public class StretchedGridGraphLayout<V extends Comparable<V>, E extends Comparable<E>> extends GridGraphLayout<V, E> {

    private final double factor;

    public StretchedGridGraphLayout(
        final GridGraphLayout<V, E> baseLayout,
        final double factor
    ) {
        this(
            baseLayout.nodeCoordinates,
            baseLayout.toHighlight,
            baseLayout.edgeStyles,
            baseLayout.directed,
            baseLayout.drawEdges,
            baseLayout.drawEdgeLabels,
            factor
        );
    }

    public StretchedGridGraphLayout(
        final Map<Vertex<V>, Coordinates2D<Integer>> nodeCoordinates,
        final Map<Vertex<V>, List<Edge<E, V>>> toHighlight,
        final Map<Vertex<V>, Map<Vertex<V>, EdgeStyle>> edgeStyles,
        final boolean directed,
        final boolean drawEdges,
        final boolean drawEdgeLabels,
        final double factor
    ) {
        super(nodeCoordinates, toHighlight, edgeStyles, directed, drawEdges, drawEdgeLabels);
        this.factor = factor;
    }

    @Override
    public <F extends Comparable<F>> StretchedGridGraphLayout<V, F> convertEdgeLabelType() {
        return new StretchedGridGraphLayout<V, F>(
            super.convertEdgeLabelType(),
            this.factor
        );
    }

    @Override
    public StretchedGridGraphLayout<V, E> highlight(final Collection<Pair<Vertex<V>, Edge<E, V>>> edges) {
        return new StretchedGridGraphLayout<V, E>(
            super.highlight(edges),
            this.factor
        );
    }

    @Override
    public GridGraphLayout<V, E> setDirected(final boolean directed) {
        return new StretchedGridGraphLayout<V, E>(
            super.setDirected(directed),
            this.factor
        );
    }

    @Override
    public GridGraphLayout<V, E> setDrawEdgeLabels(final boolean drawEdgeLabels) {
        return new StretchedGridGraphLayout<V, E>(
            super.setDrawEdgeLabels(drawEdgeLabels),
            this.factor
        );
    }

    @Override
    public GridGraphLayout<V, E> setDrawEdges(final boolean drawEdges) {
        return new StretchedGridGraphLayout<V, E>(
            super.setDrawEdges(drawEdges),
            this.factor
        );
    }

    @Override
    public StretchedGridGraphLayout<V, E> stretch(final double factor) {
        return new StretchedGridGraphLayout<V, E>(
            this.nodeCoordinates,
            this.toHighlight,
            this.edgeStyles,
            this.directed,
            this.drawEdges,
            this.drawEdgeLabels,
            factor * this.factor
        );
    }

    @Override
    public String toTikZ(final Vertex<V> vertex, final boolean startVertex, final boolean endVertex) {
        final Coordinates2D<Integer> coordinates = this.nodeCoordinates.get(vertex);
        final String id = vertex.id().toString();
        return String.format(
            Locale.US,
            "\\node[%s] (n%s) at (%.2f,%.2f) {%s};%s",
            endVertex ? "endnode" : "node",
            id,
            coordinates.x() * this.factor,
            coordinates.y() * this.factor,
            vertex.label().map(label -> label.toString()).orElse(""),
            startVertex ? GraphLayout.startNodeDecoration(id) : Main.lineSeparator
        );
    }

}
