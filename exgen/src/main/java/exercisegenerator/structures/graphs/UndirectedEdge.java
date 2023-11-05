package exercisegenerator.structures.graphs;

import java.util.*;

public class UndirectedEdge<V, E> {

    public final Vertex<V> from;

    public final Optional<E> label;

    public final Vertex<V> to;

    public UndirectedEdge(final Vertex<V> from, final E label, final Vertex<V> to) {
        this(from, Optional.of(label), to);
    }

    public UndirectedEdge(final Vertex<V> from, final Optional<E> label, final Vertex<V> to) {
        this.from = from;
        this.label = label;
        this.to = to;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object o) {
        if (o instanceof UndirectedEdge) {
            final UndirectedEdge<V, E> other = (UndirectedEdge<V, E>)o;
            return
                this.label.equals(other.label)
                && (
                    (this.from.logicallyEquals(other.from) && this.to.logicallyEquals(other.to))
                    || (this.from.logicallyEquals(other.to) && this.to.logicallyEquals(other.from))
                );
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.from.hashCode() * this.label.hashCode() * this.to.hashCode();
    }

    @Override
    public String toString() {
        return this.label.isEmpty() ?
            String.format("(%s, %s)", this.from.label.get().toString(), this.to.label.get().toString()) :
                String.format(
                    "(%s, %s, %s)",
                    this.from.label.get().toString(),
                    this.label.get().toString(),
                    this.to.label.get().toString()
                );
    }

}