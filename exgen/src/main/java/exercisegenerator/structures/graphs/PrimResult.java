package exercisegenerator.structures.graphs;

import java.util.*;

public record PrimResult<V>(PrimEntry[][] table, Graph<V, Integer> tree) {

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object o) {
        if (o instanceof PrimResult) {
            final PrimResult<V> other = (PrimResult<V>)o;
            return Arrays.deepEquals(this.table, other.table) && this.tree.logicallyEquals(other.tree);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.table.hashCode() * this.tree.hashCode();
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", Arrays.deepToString(this.table), this.tree.toString());
    }

}