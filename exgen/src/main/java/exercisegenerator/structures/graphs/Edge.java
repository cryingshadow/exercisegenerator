package exercisegenerator.structures.graphs;

import java.util.*;

import exercisegenerator.util.*;

public record Edge<E extends Comparable<E>, V extends Comparable<V>> (
    Optional<E> label,
    Vertex<V> to
) implements Comparable<Edge<E, V>> {

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

    @Override
    public int compareTo(final Edge<E, V> o) {
        return LexicographicComparator.compare(
            this,
            o,
            (o1, o2) -> o1.to().compareTo(o2.to()),
            (o1, o2) -> OptionalComparator.compareOptional(o1.label(), o2.label())
        );
    }

}
