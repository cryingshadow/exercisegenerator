package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

public class BellmanFordAlgorithm implements AlgorithmImplementation {

    static class BellmanFordStep<V> {
        final Map<V, Integer> distances;
        final Map<V, V> predecessors;
        BellmanFordStep(final Map<V, Integer> distances, final Map<V, V> predecessors) {
            this.distances = new LinkedHashMap<V, Integer>(distances);
            this.predecessors = new LinkedHashMap<V, V>(predecessors);
        }
        @Override
        public boolean equals(final Object o) {
            if (o instanceof BellmanFordStep) {
                @SuppressWarnings("unchecked")
                final
                BellmanFordStep<V> other = (BellmanFordStep<V>)o;
                return this.distances.equals(other.distances) && this.predecessors.equals(other.predecessors);
            }
            return false;
        }
        @Override
        public int hashCode() {
            return 43 + 2 * this.distances.hashCode() + 3 * this.predecessors.hashCode();
        }
        @Override
        public String toString() {
            return this
                .distances
                .keySet()
                .stream()
                .map(key -> key + "=" + this.distances.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
        }
    }

    public static final BellmanFordAlgorithm INSTANCE = new BellmanFordAlgorithm();

    public static <V> List<BellmanFordStep<V>> bellmanFord(
        final Graph<V, Integer> graph,
        final Vertex<V> start,
        final Comparator<Vertex<V>> comparator
    ) {
        final List<Vertex<V>> vertices = new ArrayList<Vertex<V>>(graph.getVertices());
        if (comparator != null) {
            Collections.sort(vertices, comparator);
        }
        final int numberOfVertices = vertices.size();
        final List<BellmanFordStep<V>> result = new LinkedList<BellmanFordStep<V>>();
        final Map<V, Integer> distances = new LinkedHashMap<V, Integer>();
        final Map<V, V> predecessors = new LinkedHashMap<V, V>();
        distances.put(start.label.get(), 0);
        result.add(new BellmanFordStep<V>(distances, predecessors));
        for (int i = 0; i < numberOfVertices - 1; i++) {
            for (final Vertex<V> from : vertices) {
                final V fromLabel = from.label.get();
                if (!distances.containsKey(fromLabel)) {
                    continue;
                }
                for (final Edge<Integer, V> edge : graph.getAdjacencyList(from)) {
                    final int newDistance = distances.get(from.label.get()) + edge.x;
                    final V toLabel = edge.y.label.get();
                    if (!distances.containsKey(toLabel) || newDistance < distances.get(toLabel)) {
                        distances.put(toLabel, newDistance);
                        predecessors.put(toLabel, fromLabel);
                    }
                }
            }
            result.add(new BellmanFordStep<V>(distances, predecessors));
        }
        return result;
    }

    private BellmanFordAlgorithm() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final Pair<Graph<String, Integer>, Vertex<String>> pair = GraphAlgorithms.parseOrGenerateGraph(input.options);
        final List<BellmanFordStep<String>> result =
            BellmanFordAlgorithm.bellmanFord(pair.x, pair.y, new StringVertexComparator());
        this.printExercise(result, input.exerciseWriter);
        this.printSolution(result, input.solutionWriter);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    private void printExercise(final List<BellmanFordStep<String>> result, final BufferedWriter writer) {
        // TODO Auto-generated method stub

    }

    private void printSolution(final List<BellmanFordStep<String>> result, final BufferedWriter writer) {
        // TODO Auto-generated method stub

    }

}
