package exercisegenerator.structures.graphs;

import java.util.*;

public record PrimResult<V extends Comparable<V>>(
    PrimEntry[][] table,
    GraphWithLayout<V, Integer, Integer> treeWithLayout
) {

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object o) {
        if (o instanceof PrimResult) {
            final PrimResult<V> other = (PrimResult<V>)o;
            return Arrays.deepEquals(this.table, other.table)
                && this.treeWithLayout().graph().logicallyEquals(other.treeWithLayout().graph());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.table.hashCode() * 2 + this.treeWithLayout().graph().hashCode() * 3;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", Arrays.deepToString(this.table), this.treeWithLayout().graph().toString());
    }

}