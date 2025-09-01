package exercisegenerator.structures.graphs;

import java.util.*;

public record UndirectedEdge<V extends Comparable<V>, E>(
    Vertex<V> from,
    Optional<E> label,
    Vertex<V> to
) implements Comparable<UndirectedEdge<V, E>> {

    public UndirectedEdge(final Vertex<V> from, final E label, final Vertex<V> to) {
        this(from, Optional.of(label), to);
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
        return this.from.hashCode() * 2 + this.label.hashCode() * 5 + this.to.hashCode() * 3;
    }

    @Override
    public String toString() {
        return this.label.isEmpty() ?
            String.format("(%s, %s)", this.from.label().get().toString(), this.to.label().get().toString()) :
                String.format(
                    "(%s, %s, %s)",
                    this.from.label().get().toString(),
                    this.label.get().toString(),
                    this.to.label().get().toString()
                );
    }

    @Override
    public int compareTo(final UndirectedEdge<V, E> o) {
        final int compare = this.from().compareTo(o.from());
        if (compare != 0) {
            return compare;
        }
        return this.to().compareTo(o.to());
    }

}