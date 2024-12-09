package exercisegenerator.structures.graphs;

public record FordFulkersonPathStep<V, E>(Vertex<V> startNode, Edge<E, V> edge) {

    @Override
    public boolean equals(final Object o) {
        if (o instanceof FordFulkersonPathStep) {
            @SuppressWarnings("unchecked")
            final FordFulkersonPathStep<V, E> other = (FordFulkersonPathStep<V, E>)o;
            final boolean result =
                this.startNode().equals(other.startNode()) && this.edge().logicallyEquals(other.edge());
            return result;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.startNode().hashCode() * 3 + this.edge().hashCode() * 7;
    }

}
