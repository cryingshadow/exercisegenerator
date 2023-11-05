package exercisegenerator.structures.graphs;

import java.util.*;

public class Edge<E, V> {

    public final Optional<E> label;

    public final Vertex<V> to;

    public Edge(final Optional<E> label, final Vertex<V> to) {
        this.label = label;
        this.to = to;
    }

    public boolean logicallyEquals(final Edge<E, V> other) {
        return this.label.equals(other.label) && this.to.logicallyEquals(other.to);
    }

}
