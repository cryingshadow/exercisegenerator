package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

public class BreadthFirstSearch implements AlgorithmImplementation {

    public static final BreadthFirstSearch INSTANCE = new BreadthFirstSearch();

    private static String breadthFirstSearchTask(final String start) {
        return GraphAlgorithms.searchTask("Breitensuche", start);
    }

    private BreadthFirstSearch() {

    }

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final Pair<Graph<String, Integer>, Vertex<String>> pair = GraphAlgorithms.parseOrGenerateGraph(input.options);
        final List<String> result = BreadthFirstSearch.breadthFirstSearch(pair.x, pair.y, new StringVertexComparator());
        GraphAlgorithms.printGraphExercise(
            pair.x,
            BreadthFirstSearch.breadthFirstSearchTask(pair.y.label.get()),
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

    public static <V, E> List<V> breadthFirstSearch(
        final Graph<V, E> graph,
        final Vertex<V> start,
        final Comparator<Vertex<V>> comparator
    ) {
        final Set<Vertex<V>> used = new LinkedHashSet<Vertex<V>>();
        final Queue<Vertex<V>> queue = new LinkedList<Vertex<V>>();
        queue.offer(start);
        final List<V> result = new LinkedList<V>();
        while (!queue.isEmpty()) {
            final Vertex<V> vertex = queue.poll();
            if (used.contains(vertex)) {
                continue;
            }
            used.add(vertex);
            result.add(vertex.label.get());
            final List<Vertex<V>> nextVertices = new ArrayList<Vertex<V>>(graph.getAdjacentVertices(vertex));
            Collections.sort(nextVertices, comparator);
            queue.addAll(nextVertices);
        }
        return result;
    }

}
