package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.*;
import exercisegenerator.structures.graphs.layout.*;

public class BreadthFirstSearch implements GraphAlgorithm<List<String>> {

    public static final BreadthFirstSearch INSTANCE = new BreadthFirstSearch();

    private BreadthFirstSearch() {}

    @Override
    public List<String> apply(final GraphProblem problem) {
        final Set<Vertex<String>> used = new LinkedHashSet<Vertex<String>>();
        final Queue<Vertex<String>> queue = new LinkedList<Vertex<String>>();
        queue.offer(problem.startNode().get());
        final List<String> result = new LinkedList<String>();
        while (!queue.isEmpty()) {
            final Vertex<String> vertex = queue.poll();
            if (used.contains(vertex)) {
                continue;
            }
            used.add(vertex);
            result.add(vertex.label().get());
            final List<Vertex<String>> nextVertices =
                new ArrayList<Vertex<String>>(problem.graphWithLayout().graph().getAdjacentVertices(vertex));
            Collections.sort(nextVertices, problem.comparator());
            queue.addAll(nextVertices);
        }
        return result;
    }

    @Override
    public String commandPrefix() {
        return "Bfs";
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public GraphWithLayout<String, Integer, Integer> getGraphWithLayoutForProblemInstance(
        final GraphProblem problem,
        final Parameters<Flag> options
    ) {
        return GraphAlgorithm.stretch(
            new GraphWithLayout<String, Integer, Integer>(
                problem.graphWithLayout().graph(),
                ((GridGraphLayout<String, Integer>)problem.graphWithLayout().layout()).setDrawEdgeLabels(false)
            ),
            GraphAlgorithm.parseDistanceFactor(options)
        );
    }

    @Override
    public void printAfterSingleProblemInstance(
        final GraphProblem problem,
        final List<String> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write(GraphAlgorithm.searchTask("Breitensuche", problem.startNode().get().label().get()));
        Main.newLine(writer);
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<GraphProblem> problems,
        final List<List<String>> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("F\\\"uhren Sie eine \\emphasize{Breitensuche} auf den folgenden Graphen mit ihren jeweiligen ");
        writer.write("Startknoten aus. Geben Sie dazu jeweils die Knoten in der Reihenfolge an, in der sie durch die ");
        writer.write("Breitensuche gefunden werden. Nehmen Sie an, dass der Algorithmus die Kanten in der ");
        writer.write("alphabetischen Reihenfolge ihrer Zielknoten durchl\\\"auft.");
        Main.newLine(writer);
    }

    @Override
    public void printSolutionInstance(
        final GraphProblem problem,
        final List<String> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write(solution.stream().collect(Collectors.joining(", ")));
        Main.newLine(writer);
    }

    @Override
    public void printSolutionSpace(
        final GraphProblem problem,
        final List<String> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

}
