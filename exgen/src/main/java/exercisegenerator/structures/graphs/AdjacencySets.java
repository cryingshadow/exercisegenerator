package exercisegenerator.structures.graphs;

import java.util.*;

public class AdjacencySets<V extends Comparable<V>, E extends Comparable<E>>
extends LinkedHashMap<Vertex<V>, Set<Edge<E, V>>> {

    private static final long serialVersionUID = 4418107509182049823L;

    public AdjacencySets() {
        super();
    }

    public AdjacencySets(final Map<Vertex<V>, ? extends Set<Edge<E, V>>> adjacencySets) {
        super(adjacencySets);
        for (final Set<Edge<E, V>> edges : this.values()) {
            for (final Edge<E, V> edge : edges) {
                if (!this.containsKey(edge.to())) {
                    this.put(edge.to(), new TreeSet<Edge<E, V>>());
                }
            }
        }
    }

    public void addEdge(final Vertex<V> from, final E edgeLabel, final Vertex<V> to) {
        this.addEdge(from, Optional.of(edgeLabel), to);
    }

    public void addEdge(final Vertex<V> from, final Optional<E> edgeLabel, final Vertex<V> to) {
        if (!this.containsKey(from)) {
            this.put(from, new TreeSet<Edge<E, V>>());
        }
        if (!this.containsKey(to)) {
            this.put(to, new TreeSet<Edge<E, V>>());
        }
        this.get(from).add(new Edge<E, V>(edgeLabel, to));
    }

    public boolean logicallyEquals(final AdjacencySets<V, E> other) {
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
        for (final Map.Entry<Vertex<V>, Set<Edge<E, V>>> entry : this.entrySet()) {
            final Vertex<V> vertex = entry.getKey();
            final Vertex<V> otherVertex = nodeMapping.get(vertex);
            final Set<Edge<E, V>> edgeSet = entry.getValue();
            final Set<Edge<E, V>> otherEdgeSet = other.get(otherVertex);
            if (edgeSet.size() != otherEdgeSet.size()) {
                return false;
            }
            for (final Edge<E, V> edge : edgeSet) {
                boolean found = false;
                for (final Edge<E, V> otherEdge : otherEdgeSet) {
                    if (found) {
                        break;
                    }
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
