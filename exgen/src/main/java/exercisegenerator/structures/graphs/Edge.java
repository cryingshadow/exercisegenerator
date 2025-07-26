package exercisegenerator.structures.graphs;

import java.util.*;

public record Edge<E, V> (Optional<E> label, Vertex<V> to) {

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
