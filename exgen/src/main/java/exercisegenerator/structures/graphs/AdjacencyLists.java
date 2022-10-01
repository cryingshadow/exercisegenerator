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
        if (!this.containsKey(from)) {
            this.put(from, new ArrayList<Edge<E, V>>());
        }
        if (!this.containsKey(to)) {
            this.put(to, new ArrayList<Edge<E, V>>());
        }
        this.get(from).add(new Edge<E, V>(edgeLabel, to));
    }

}
