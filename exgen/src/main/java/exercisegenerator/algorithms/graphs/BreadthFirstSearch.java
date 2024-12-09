package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.*;

public class BreadthFirstSearch implements GraphAlgorithm<List<String>> {

    public static final BreadthFirstSearch INSTANCE = new BreadthFirstSearch();

    private static String breadthFirstSearchTask(final String start) {
        return GraphAlgorithm.searchTask("Breitensuche", start);
    }

    private BreadthFirstSearch() {}

    @Override
    public List<String> apply(final GraphProblem problem) {
        final Set<Vertex<String>> used = new LinkedHashSet<Vertex<String>>();
        final Queue<Vertex<String>> queue = new LinkedList<Vertex<String>>();
        queue.offer(problem.startNode());
        final List<String> result = new LinkedList<String>();
        while (!queue.isEmpty()) {
            final Vertex<String> vertex = queue.poll();
            if (used.contains(vertex)) {
                continue;
            }
            used.add(vertex);
            result.add(vertex.label.get());
            final List<Vertex<String>> nextVertices =
                new ArrayList<Vertex<String>>(problem.graph().getAdjacentVertices(vertex));
            Collections.sort(nextVertices, problem.comparator());
            queue.addAll(nextVertices);
        }
        return result;
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
            BreadthFirstSearch.breadthFirstSearchTask(problem.startNode().label.get()),
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
