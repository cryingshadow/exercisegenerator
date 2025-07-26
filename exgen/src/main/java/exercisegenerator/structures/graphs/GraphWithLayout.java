package exercisegenerator.structures.graphs;

import exercisegenerator.structures.graphs.layout.*;

public record GraphWithLayout<V, E, T extends Number>(Graph<V, E> graph, GraphLayout<V, E, T> layout) {}
