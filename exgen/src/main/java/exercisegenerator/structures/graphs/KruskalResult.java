package exercisegenerator.structures.graphs;

import java.util.*;

public class KruskalResult<V> {

    public final List<UndirectedEdge<V, Integer>> edges;

    public final GraphWithLayout<V, Integer, Integer> treeWithLayout;

    public KruskalResult(
        final List<UndirectedEdge<V, Integer>> edges,
        final GraphWithLayout<V, Integer, Integer> treeWithLayout
    ) {
        this.edges = edges;
        this.treeWithLayout = treeWithLayout;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object o) {
        if (o instanceof KruskalResult) {
            final KruskalResult<V> other = (KruskalResult<V>)o;
            return this.edges.equals(other.edges)
                && this.treeWithLayout.graph().logicallyEquals(other.treeWithLayout.graph());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.edges.hashCode() * 2 + this.treeWithLayout.graph().hashCode() * 3;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", this.edges.toString(), this.treeWithLayout.graph().toString());
    }

}