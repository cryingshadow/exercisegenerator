package exercisegenerator.algorithms.graphs;

import java.util.*;

import exercisegenerator.structures.graphs.*;

public class FloydAlgorithm implements FloydWarshallAlgorithm<Integer> {

    public static final FloydAlgorithm INSTANCE = new FloydAlgorithm();

    private FloydAlgorithm() {}

    @Override
    public Integer[][][] createTable(final int size1, final int size2, final int size3) {
        return new Integer[size1][size2][size3];
    }

    @Override
    public String getName() {
        return "Floyd";
    }

    @Override
    public Integer initialValue(
        final Graph<String, Integer> graph,
        final List<Vertex<String>> vertices,
        final int from,
        final int to
    ) {
        if (from == to) {
            return 0;
        }
        final Set<Edge<Integer, String>> edges = graph.getEdges(vertices.get(from), vertices.get(to));
        if (!edges.isEmpty()) {
            return edges.iterator().next().label.get();
        }
        return null;
    }

    @Override
    public String toString(final Integer entry) {
        return entry == null ? "$\\infty$" : entry.toString();
    }

    @Override
    public Integer update(
        final Integer[][][] tables,
        final int from,
        final int via,
        final int to
    ) {
        final Integer oldValue = tables[via][from][to];
        final Integer fromVia = tables[via][from][via];
        final Integer viaTo = tables[via][via][to];
        final Integer update = fromVia == null ? null : (viaTo == null ? null : fromVia + viaTo);
        if (oldValue == null || update != null && update < oldValue) {
            return update;
        } else {
            return oldValue;
        }
    }

}
