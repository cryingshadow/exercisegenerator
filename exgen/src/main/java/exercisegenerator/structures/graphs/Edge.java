package exercisegenerator.structures.graphs;

import java.util.*;

public class Edge<E, V> {

    public final Optional<E> label;

    public final Vertex<V> to;

    public Edge(final Optional<E> label, final Vertex<V> to) {
        this.label = label;
        this.to = to;
    }

    @Override
    public int hashCode() {
        return this.to.hashCode() * 3 + this.label.hashCode() * 7;
    }

    public boolean logicallyEquals(final Edge<E, V> other) {
        return this.label.equals(other.label) && this.to.logicallyEquals(other.to);
    }

    @Override
    public String toString() {
        return String.format(
            "-%s-> %s",
            this.label.isEmpty() ? "" : String.format("(%s)", this.label.get().toString()),
            this.to.toString()
        );
    }

}
