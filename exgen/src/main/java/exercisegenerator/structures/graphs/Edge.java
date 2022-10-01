package exercisegenerator.structures.graphs;

import exercisegenerator.structures.*;

public class Edge<E, V> extends Pair<E, Vertex<V>> {

    private static final long serialVersionUID = -5425151288469119238L;

    public Edge(final E edgeLabel, final Vertex<V> vertex) {
        super(edgeLabel, vertex);
    }

}
