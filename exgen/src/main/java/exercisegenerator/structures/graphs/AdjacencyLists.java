package exercisegenerator.structures.graphs;

import java.util.*;

public class AdjacencyLists<V, E> extends LinkedHashMap<Vertex<V>, List<Edge<E, V>>> {

    private static final long serialVersionUID = 4418107509182049823L;

    public AdjacencyLists() {
        super();
    }

    public AdjacencyLists(final Map<Vertex<V>, ? extends List<Edge<E, V>>> adjacencyLists) {
        super(adjacencyLists);
    }

    public void addEdge(final Vertex<V> from, final E edgeLabel, final Vertex<V> to) {
        this.addEdge(from, Optional.of(edgeLabel), to);
    }

    public void addEdge(final Vertex<V> from, final Optional<E> edgeLabel, final Vertex<V> to) {
        if (!this.containsKey(from)) {
            this.put(from, new ArrayList<Edge<E, V>>());
        }
        if (!this.containsKey(to)) {
            this.put(to, new ArrayList<Edge<E, V>>());
        }
        this.get(from).add(new Edge<E, V>(edgeLabel, to));
    }

    public boolean logicallyEquals(final AdjacencyLists<V, E> other) {
        if (this.size() != other.size()) {
            return false;
        }
        final Map<Vertex<V>, Vertex<V>> nodeMapping = new LinkedHashMap<Vertex<V>, Vertex<V>>();
        for (final Vertex<V> vertex : this.keySet()) {
            for (final Vertex<V> otherVertex : other.keySet()) {
                if (vertex.logicallyEquals(otherVertex)) {
                    nodeMapping.put(vertex, otherVertex);
                }
            }
        }
        if (nodeMapping.size() != this.size()) {
            return false;
        }
        for (final Map.Entry<Vertex<V>, List<Edge<E, V>>> entry : this.entrySet()) {
            final Vertex<V> vertex = entry.getKey();
            final Vertex<V> otherVertex = nodeMapping.get(vertex);
            final List<Edge<E, V>> edgeList = entry.getValue();
            final List<Edge<E, V>> otherEdgeList = other.get(otherVertex);
            if (edgeList.size() != otherEdgeList.size()) {
                return false;
            }
            for (int i = 0; i < edgeList.size(); i++) {
                final Edge<E, V> edge = edgeList.get(i);
                boolean found = false;
                for (int j = 0; !found && j < otherEdgeList.size(); j++) {
                    final Edge<E, V> otherEdge = otherEdgeList.get(j);
                    if (edge.logicallyEquals(otherEdge)) {
                        found = true;
                    }
                }
                if (!found) {
                    return false;
                }
            }
        }
        return true;
    }

}
