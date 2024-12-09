package exercisegenerator.algorithms.graphs;

import java.util.*;

import exercisegenerator.structures.graphs.*;

public class WarshallAlgorithm implements FloydWarshallAlgorithm<Boolean> {

    public static final WarshallAlgorithm INSTANCE = new WarshallAlgorithm();

    private WarshallAlgorithm() {}

    @Override
    public Boolean[][][] createTable(final int size1, final int size2, final int size3) {
        return new Boolean[size1][size2][size3];
    }

    @Override
    public String getName() {
        return "Warshall";
    }

    @Override
    public Boolean initialValue(
        final Graph<String, Integer> graph,
        final List<Vertex<String>> vertices,
        final int from,
        final int to
    ) {
        return from == to || !graph.getEdges(vertices.get(from), vertices.get(to)).isEmpty();
    }

    @Override
    public String toString(final Boolean entry) {
        return entry ? "true" : "false";
    }

    @Override
    public Boolean update(final Boolean[][][] tables, final int from, final int via, final int to) {
        if (tables[via][from][via] && tables[via][via][to]) {
            return true;
        } else {
            return tables[via][from][to];
        }
    }

}
