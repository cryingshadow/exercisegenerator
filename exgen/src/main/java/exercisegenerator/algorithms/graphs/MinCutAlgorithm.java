package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.*;
import exercisegenerator.structures.graphs.flownetwork.*;

public class MinCutAlgorithm implements FlowNetworkAlgorithm {

    public static final MinCutAlgorithm INSTANCE = new MinCutAlgorithm();

    private MinCutAlgorithm() {}

    @Override
    public String commandPrefix() {
        return "MinCut";
    }

    @Override
    public void printAfterSingleProblemInstance(
        final FlowNetworkProblem problem,
        final FlowNetworkResult solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("Berechnen Sie den maximalen Fluss und einen minimalen Schnitt in diesem Netzwerk mithilfe der ");
        writer.write("\\emphasize{Ford-Fulkerson Methode}. Geben Sie dazu ");
        writer.write("\\emphasize{jedes Restnetzwerk (auch das initiale)} ");
        writer.write("sowie \\emphasize{nach jeder Augmentierung} den aktuellen Zustand des Flussnetzwerks an. ");
        writer.write("Geben Sie au\\ss{}erdem den \\emphasize{Wert des maximalen Flusses} sowie einen ");
        writer.write("\\emphasize{minimalen Schnitt} an.");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<FlowNetworkProblem> problems,
        final List<FlowNetworkResult> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Berechnen Sie den maximalen Fluss und einen minimalen Schnitt in den folgenden ");
        writer.write("\\emphasize{Flussnetzwerken} mithilfe der \\emphasize{Ford-Fulkerson Methode}. Geben Sie dazu ");
        writer.write("\\emphasize{jedes Restnetzwerk (auch das jeweils initiale)} sowie ");
        writer.write("\\emphasize{nach jeder Augmentierung} den aktuellen Zustand des jeweiligen Flussnetzwerks an. ");
        writer.write("Geben Sie au\\ss{}erdem jeweils den \\emphasize{Wert des maximalen Flusses} sowie einen ");
        writer.write("\\emphasize{minimalen Schnitt} an.");
        Main.newLine(writer);
    }

    @Override
    public void printFinalAnswer(
        final FlowNetworkProblem problem,
        final FlowNetworkResult solution,
        final BufferedWriter writer
    ) throws IOException {
        int flow = 0;
        final Graph<String, FlowAndCapacity> graph = solution.steps().getLast().flowNetworkWithLayout().graph();
        final Set<Edge<FlowAndCapacity, String>> set = graph.getAdjacencySet(problem.source());
        if (set != null) {
            for (final Edge<FlowAndCapacity, String> edge : set) {
                flow += edge.label().get().flow();
            }
        }
        writer.write("Der maximale Fluss hat den Wert: " + flow);
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("Der minimale Schnitt ist: $\\{");
        writer.write(solution.sourcePartition().stream().map(v -> v.label().get()).collect(Collectors.joining(", ")));
        writer.write("\\}, \\{");
        writer.write(
            graph
            .getVertices()
            .stream()
            .filter(v -> !solution.sourcePartition().contains(v))
            .map(v -> v.label().get())
            .sorted()
            .collect(Collectors.joining(", "))
        );
        writer.write("\\}$");
        Main.newLine(writer);
        Main.newLine(writer);
    }

    @Override
    public void printSolutionSpaceForFinalAnswer(
        final FlowNetworkResult solution,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Der maximale Fluss hat den Wert: ");
        Main.newLine(writer);
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("Der minimale Schnitt ist: ");
        Main.newLine(writer);
    }

}
