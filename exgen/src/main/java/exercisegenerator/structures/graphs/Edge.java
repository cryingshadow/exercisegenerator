package exercisegenerator.structures.graphs;

public class Edge<E, V> {

    public final E label;

    public final Vertex<V> to;

    public Edge(final E label, final Vertex<V> to) {
        this.label = label;
        this.to = to;
    }

    public boolean logicallyEquals(final Edge<E, V> other) {
        return this.label.equals(other.label) && this.to.logicallyEquals(other.to);
    }

}
