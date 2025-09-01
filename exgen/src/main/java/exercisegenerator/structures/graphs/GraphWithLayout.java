package exercisegenerator.structures.graphs;

import exercisegenerator.structures.graphs.layout.*;

public record GraphWithLayout<V extends Comparable<V>, E extends Comparable<E>, T extends Number>(
    Graph<V, E> graph,
    GraphLayout<V, E, T> layout
) {}
