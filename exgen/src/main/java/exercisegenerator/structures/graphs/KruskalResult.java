package exercisegenerator.structures.graphs;

import java.util.*;

public class KruskalResult<V> {

    public final List<UndirectedEdge<V, Integer>> edges;

    public final Graph<V, Integer> tree;

    public KruskalResult(final List<UndirectedEdge<V, Integer>> edges, final Graph<V, Integer> tree) {
        this.edges = edges;
        this.tree = tree;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object o) {
        if (o instanceof KruskalResult) {
            final KruskalResult<V> other = (KruskalResult<V>)o;
            return this.edges.equals(other.edges) && this.tree.logicallyEquals(other.tree);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.edges.hashCode() * this.tree.hashCode();
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", this.edges.toString(), this.tree.toString());
    }

}