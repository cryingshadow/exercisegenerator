package exercisegenerator.structures.graphs;

import exercisegenerator.structures.graphs.layout.*;

public record GraphWithLayout<V, E>(Graph<V, E> graph, GraphLayout<V, E> layout) {}
