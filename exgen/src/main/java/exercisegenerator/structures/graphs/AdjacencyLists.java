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

}
