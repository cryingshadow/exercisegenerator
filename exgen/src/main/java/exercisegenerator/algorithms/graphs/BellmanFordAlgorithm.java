package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.*;

public class BellmanFordAlgorithm implements GraphAlgorithm<List<BellmanFordStep<String>>> {

    public static final BellmanFordAlgorithm INSTANCE = new BellmanFordAlgorithm();

    private static void printTables(
        final List<Vertex<String>> vertices,
        final List<BellmanFordStep<String>> result,
        final boolean fill,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        final String columnWidth = "16mm";
        LaTeXUtils.printArrayStretch(1.5, writer);
        final int[] pagebreakCounters = LaTeXUtils.parsePagebreakCounters(options);
        int counterIndex = 0;
        int tables = 0;
        boolean first = true;
        for (final BellmanFordStep<String> step : result) {
            if (first) {
                first = false;
            } else if (counterIndex < pagebreakCounters.length && tables >= pagebreakCounters[counterIndex]) {
                writer.write("\\newpage");
                Main.newLine(writer);
                Main.newLine(writer);
                tables = 0;
                counterIndex++;
            } else {
                LaTeXUtils.printVerticalProtectedSpace(writer);
            }
            LaTeXUtils.printTable(
                BellmanFordAlgorithm.toTable(step, vertices, fill),
                Optional.empty(),
                cols -> String.format("|c|*{%d}{C{%s}|}", cols, columnWidth),
                true,
                6,
                1,
                writer
            );
            tables++;
        }
        LaTeXUtils.printArrayStretch(1.0, writer);
    }

    private static String[][] toTable(
        final BellmanFordStep<String> step,
        final List<Vertex<String>> vertices,
        final boolean fill
    ) {
        final String[][] result = new String[2][vertices.size() + 1];
        result[0][0] = "\\textbf{Knoten}";
        result[1][0] = "\\textbf{Distanz/Vorg\\\"anger}";
        int i = 1;
        for (final Vertex<String> vertex : vertices) {
            final String label = vertex.label().get();
            result[0][i] = String.format("\\textbf{%s}", label);
            if (fill) {
                if (step.distances.containsKey(label)) {
                    String cellText = step.distances.get(label).toString();
                    if (step.predecessors.containsKey(label)) {
                        cellText += String.format("/%s", step.predecessors.get(label));
                    }
                    result[1][i] = LaTeXUtils.inlineMath(cellText);
                } else {
                    result[1][i] = LaTeXUtils.inlineMath("\\infty");
                }
            } else {
                result[1][i] = "";
            }
            i++;
        }
        return result;
    }

    private BellmanFordAlgorithm() {}

    @Override
    public List<BellmanFordStep<String>> apply(final GraphProblem problem) {
        final List<Vertex<String>> vertices =
            GraphAlgorithm.getSortedListOfVertices(problem.graphWithLayout().graph(), problem.comparator());
        final int numberOfVertices = vertices.size();
        final List<BellmanFordStep<String>> result = new LinkedList<BellmanFordStep<String>>();
        final Map<String, Integer> distances = new LinkedHashMap<String, Integer>();
        final Map<String, String> predecessors = new LinkedHashMap<String, String>();
        distances.put(problem.startNode().get().label().get(), 0);
        result.add(new BellmanFordStep<String>(distances, predecessors));
        boolean changed = true;
        for (int i = 0; i < numberOfVertices - 1 && changed; i++) {
            changed = false;
            for (final Vertex<String> from : vertices) {
                final String fromLabel = from.label().get();
                if (!distances.containsKey(fromLabel)) {
                    continue;
                }
                for (final Edge<Integer, String> edge : problem.graphWithLayout().graph().getAdjacencySet(from)) {
                    final int newDistance = distances.get(from.label().get()) + edge.label().get();
                    final String toLabel = edge.to().label().get();
                    if (!distances.containsKey(toLabel) || newDistance < distances.get(toLabel)) {
                        distances.put(toLabel, newDistance);
                        predecessors.put(toLabel, fromLabel);
                        changed = true;
                    }
                }
            }
            result.add(new BellmanFordStep<String>(distances, predecessors));
        }
        return result;
    }

    @Override
    public String commandPrefix() {
        return "BellmanFord";
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public void printAfterSingleProblemInstance(
        final GraphProblem problem,
        final List<BellmanFordStep<String>> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("F\\\"uhren Sie den \\emphasize{Bellman-Ford}-Algorithmus auf diesem Graphen mit dem ");
        writer.write("\\emphasize{Startknoten ");
        writer.write(problem.startNode().get().label().get());
        writer.write("} aus.");
        Main.newLine(writer);
        writer.write("Geben Sie dazu die Distanzen und Vorg\\\"anger nach jeder Iteration aller Kanten an, indem Sie ");
        writer.write("die nachfolgenden Tabellen ausf\\\"ullen");
        if (options.containsKey(Flag.VARIANT)) {
            writer.write(". Geben Sie au\\ss{}erdem den k\\\"urzesten Pfad von ");
            writer.write(problem.startNode().get().label().get());
            writer.write(" nach ");
            writer.write(options.get(Flag.VARIANT));
            writer.write(" an oder begr\\\"unden Sie, warum kein solcher Pfad existiert.");
        } else {
            writer.write(":");
        }
        writer.write("\\\\[2ex]");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<GraphProblem> problems,
        final List<List<BellmanFordStep<String>>> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("F\\\"uhren Sie den \\emphasize{Bellman-Ford}-Algorithmus auf den folgenden Graphen aus. ");
        writer.write("Geben Sie dazu jeweils die Distanzen und Vorg\\\"anger nach jeder Iteration aller Kanten an, ");
        writer.write("indem Sie die jeweiligen Tabellen ausf\\\"ullen.");
        if (options.containsKey(Flag.VARIANT)) {
            writer.write(" Geben Sie au\\ss{}erdem jeweils den k\\\"urzesten Pfad vom Startknoten nach ");
            writer.write(options.get(Flag.VARIANT));
            writer.write(" an oder begr\\\"unden Sie, warum ein solcher Pfad nicht existiert.");
        } else {

        }
        writer.write("\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printSolutionInstance(
        final GraphProblem problem,
        final List<BellmanFordStep<String>> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        final List<Vertex<String>> vertices =
            GraphAlgorithm.getSortedListOfVertices(problem.graphWithLayout().graph(), problem.comparator());
        BellmanFordAlgorithm.printTables(vertices, solution, true, options, writer);
        if (options.containsKey(Flag.VARIANT)) {
            LaTeXUtils.printVerticalProtectedSpace(writer);
            if (solution.getLast().equals(solution.get(solution.size() - 2))) {
                final BellmanFordStep<String> lastStep = solution.getLast();
                final String start = problem.startNode().get().label().get();
                String current = options.get(Flag.VARIANT);
                if (lastStep.predecessors.containsKey(current)) {
                    final List<String> path = new LinkedList<String>();
                    while (!current.equals(start)) {
                        path.add(current);
                        current = lastStep.predecessors.get(current);
                    }
                    path.add(start);
                    writer.write("Der kürzeste Pfad von ");
                    writer.write(start);
                    writer.write(" nach ");
                    writer.write(path.getFirst());
                    writer.write(" ist ");
                    writer.write(path.reversed().stream().collect(Collectors.joining(" $\\to$ ")));
                    writer.write(".");
                } else {
                    writer.write("Der gew\\\"unschte Zielknoten ist vom Startknoten aus nicht erreichbar.");
                }
            } else {
                writer.write("In diesem Graphen gibt es einen Zyklus mit negativem Gewicht, sodass die Frage nach ");
                writer.write("k\\\"urzesten Pfaden f\\\"ur diesen Graphen nicht anwendbar ist.");
            }
            Main.newLine(writer);
        }
    }

    @Override
    public void printSolutionSpace(
        final GraphProblem problem,
        final List<BellmanFordStep<String>> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        final List<Vertex<String>> vertices =
            GraphAlgorithm.getSortedListOfVertices(problem.graphWithLayout().graph(), problem.comparator());
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
        BellmanFordAlgorithm.printTables(vertices, solution, false, options, writer);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, writer);
    }

}
