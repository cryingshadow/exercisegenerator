package exercisegenerator.structures.graphs;

import java.util.*;

public record GraphProblem(
    GraphWithLayout<String, Integer, Integer> graphWithLayout,
    Optional<Vertex<String>> startNode,
    Comparator<Vertex<String>> comparator
) {

    public GraphProblem(
        final GraphWithLayout<String, Integer, Integer> graphWithLayout,
        final Optional<Vertex<String>> startNode,
        final Comparator<Vertex<String>> comparator
    ) {
        this.graphWithLayout = graphWithLayout;
        this.startNode = startNode;
        this.comparator = comparator;
    }

    public GraphProblem(final GraphWithLayout<String, Integer, Integer> graphWithLayout) {
        this(graphWithLayout, Optional.empty(), StringVertexComparator.INSTANCE);
    }

    public GraphProblem(
        final GraphWithLayout<String, Integer, Integer> graphWithLayout,
        final Optional<Vertex<String>> startNode
    ) {
        this(graphWithLayout, startNode, StringVertexComparator.INSTANCE);
    }

    public GraphProblem(
        final GraphWithLayout<String, Integer, Integer> graphWithLayout,
        final Vertex<String> startNode
    ) {
        this(graphWithLayout, Optional.ofNullable(startNode), StringVertexComparator.INSTANCE);
    }

}
