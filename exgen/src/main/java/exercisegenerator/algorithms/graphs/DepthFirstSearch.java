package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.*;

public class DepthFirstSearch implements GraphAlgorithm<List<String>> {

    public static final DepthFirstSearch INSTANCE = new DepthFirstSearch();

    private static List<String> depthFirstSearch(
        final Graph<String, Integer> graph,
        final Vertex<String> vertex,
        final Comparator<Vertex<String>> comparator,
        final Set<Vertex<String>> used
    ) {
        if (used.contains(vertex)) {
            return Collections.emptyList();
        }
        used.add(vertex);
        final List<String> result = new LinkedList<String>();
        result.add(vertex.label.get());
        final List<Vertex<String>> nextVertices = new ArrayList<Vertex<String>>(graph.getAdjacentVertices(vertex));
        Collections.sort(nextVertices, comparator);
        for (final Vertex<String> nextVertex : nextVertices) {
            result.addAll(DepthFirstSearch.depthFirstSearch(graph, nextVertex, comparator, used));
        }
        return result;
    }

    private static String depthFirstSearchTask(final String start) {
        return GraphAlgorithm.searchTask("Tiefensuche", start);
    }

    private DepthFirstSearch() {}

    @Override
    public List<String> apply(final GraphProblem problem) {
        return DepthFirstSearch.depthFirstSearch(
            problem.graph(),
            problem.startNode(),
            problem.comparator(),
            new LinkedHashSet<Vertex<String>>()
        );
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public void printExercise(
        final GraphProblem problem,
        final List<String> solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        GraphAlgorithm.printGraphExercise(
            problem.graph(),
            DepthFirstSearch.depthFirstSearchTask(problem.startNode().label.get()),
            GraphAlgorithm.parseDistanceFactor(options),
            GraphPrintMode.NO_EDGE_LABELS,
            writer
        );
        Main.newLine(writer);
    }

    @Override
    public void printSolution(
        final GraphProblem problem,
        final List<String> solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write(solution.stream().collect(Collectors.joining(", ")));
        Main.newLine(writer);
        Main.newLine(writer);
    }

}
