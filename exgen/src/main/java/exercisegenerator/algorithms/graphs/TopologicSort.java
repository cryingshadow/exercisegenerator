package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.*;

public class TopologicSort implements GraphAlgorithm<List<String>> {

    public static final TopologicSort INSTANCE = new TopologicSort();

    private TopologicSort() {}

    @Override
    public List<String> apply(final GraphProblem problem) {
        final List<String> result = new LinkedList<String>();
        final Set<Vertex<String>> visited = new LinkedHashSet<Vertex<String>>();
        final Set<Vertex<String>> finished = new LinkedHashSet<Vertex<String>>();
        final Graph<String, Integer> graph = problem.graphWithLayout().graph();
        for (final Vertex<String> vertex : graph.getVertices()) {
            if (this.visitAndCheckCycle(vertex, graph, visited, finished, result)) {
                return null;
            }
        }
        return result;
    }

    @Override
    public String commandPrefix() {
        return "TopologicSort";
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<GraphProblem> problems,
        final List<List<String>> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Geben Sie jeweils eine topologische Sortierung der folgenden Graphen an oder begr\\\"unden ");
        writer.write("Sie, warum keine topologische Sortierung f\\\"ur den jeweiligen Graphen existiert.\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeSingleProblemInstance(
        final GraphProblem problem,
        final List<String> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Geben Sie eine topologische Sortierung des folgenden Graphen an oder begr\\\"unden Sie, warum ");
        writer.write("keine topologische Sortierung f\\\"ur diesen Graphen existiert.\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printSolutionInstance(
        final GraphProblem problem,
        final List<String> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        if (solution == null) {
            writer.write("Es existiert keine topologische Sortierung für diesen Graphen, da er mindestens einen ");
            writer.write("Zyklus enthält.");
        } else {
            writer.write(solution.stream().collect(Collectors.joining(", ")));
        }
        Main.newLine(writer);
    }

    @Override
    public void printSolutionSpace(
        final GraphProblem problem,
        final List<String> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

    private boolean visitAndCheckCycle(
        final Vertex<String> vertex,
        final Graph<String, Integer> graph,
        final Set<Vertex<String>> visited,
        final Set<Vertex<String>> finished,
        final List<String> result
    ) {
        if (finished.contains(vertex)) {
            return false;
        }
        if (visited.contains(vertex)) {
            return true;
        }
        visited.add(vertex);
        for (final Vertex<String> neighbour : graph.getAdjacentVertices(vertex)) {
            if (this.visitAndCheckCycle(neighbour, graph, visited, finished, result)) {
                return true;
            }
        }
        finished.add(vertex);
        result.addFirst(vertex.label().get());
        return false;
    }

}
