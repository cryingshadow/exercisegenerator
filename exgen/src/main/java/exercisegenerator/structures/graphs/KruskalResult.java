package exercisegenerator.structures.graphs;

import java.util.*;

public record KruskalResult<V extends Comparable<V>>(
    List<UndirectedEdge<V, Integer>> edges,
    GraphWithLayout<V, Integer, Integer> treeWithLayout
) {

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