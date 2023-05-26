package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

public class DepthFirstSearch implements AlgorithmImplementation {

    public static final DepthFirstSearch INSTANCE = new DepthFirstSearch();

    public static <V, E> List<V> depthFirstSearch(
        final Graph<V, E> graph,
        final Vertex<V> start,
        final Comparator<Vertex<V>> comparator
    ) {
        return DepthFirstSearch.depthFirstSearch(graph, start, comparator, new LinkedHashSet<Vertex<V>>());
    }

    private static <V, E> List<V> depthFirstSearch(
        final Graph<V, E> graph,
        final Vertex<V> vertex,
        final Comparator<Vertex<V>> comparator,
        final Set<Vertex<V>> used
    ) {
        if (used.contains(vertex)) {
            return Collections.emptyList();
        }
        used.add(vertex);
        final List<V> result = new LinkedList<V>();
        result.add(vertex.label.get());
        final List<Vertex<V>> nextVertices = new ArrayList<Vertex<V>>(graph.getAdjacentVertices(vertex));
        Collections.sort(nextVertices, comparator);
        for (final Vertex<V> nextVertex : nextVertices) {
            result.addAll(DepthFirstSearch.depthFirstSearch(graph, nextVertex, comparator, used));
        }
        return result;
    }

    private static String depthFirstSearchTask(final String start) {
        return GraphAlgorithms.searchTask("Tiefensuche", start);
    }

    private DepthFirstSearch() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final Pair<Graph<String, Integer>, Vertex<String>> pair = GraphAlgorithms.parseOrGenerateGraph(input.options);
        final List<String> result = DepthFirstSearch.depthFirstSearch(pair.x, pair.y, new StringVertexComparator());
        GraphAlgorithms.printGraphExercise(
            pair.x,
            DepthFirstSearch.depthFirstSearchTask(pair.y.label.get()),
            GraphAlgorithms.parseDistanceFactor(input.options),
            GraphPrintMode.NO_EDGE_LABELS,
            input.exerciseWriter
        );
        Main.newLine(input.exerciseWriter);
        input.solutionWriter.write(result.stream().collect(Collectors.joining(", ")));
        Main.newLine(input.solutionWriter);
        Main.newLine(input.solutionWriter);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
