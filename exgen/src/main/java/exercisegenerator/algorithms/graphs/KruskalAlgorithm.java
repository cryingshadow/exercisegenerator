package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.*;
import exercisegenerator.structures.graphs.layout.*;

public class KruskalAlgorithm implements GraphAlgorithm<KruskalResult<String>> {

    public static final KruskalAlgorithm INSTANCE = new KruskalAlgorithm();

    private KruskalAlgorithm() {}

    @Override
    public KruskalResult<String> apply(final GraphProblem problem) {
        final Graph<String, Integer> graph = problem.graphWithLayout().graph();
        final List<UndirectedEdge<String, Integer>> result = new ArrayList<UndirectedEdge<String, Integer>>();
        final Graph<String, Integer> tree = graph.nodeCopy();
        final LinkedList<UndirectedEdge<String, Integer>> edges =
            graph
            .getAllUndirectedEdges()
            .stream()
            .sorted(
                new Comparator<UndirectedEdge<String, Integer>>() {
                    @Override
                    public int compare(
                        final UndirectedEdge<String, Integer> edge1,
                        final UndirectedEdge<String, Integer> edge2
                    ) {
                        return edge1.label().get().compareTo(edge2.label().get());
                    }
                }
            ).collect(Collectors.toCollection(LinkedList::new));
        final UnionFind<Vertex<String>> components = new UnionFind<Vertex<String>>();
        while (!edges.isEmpty()) {
            final UndirectedEdge<String, Integer> edge = edges.removeFirst();
            if (!components.connected(edge.from(), edge.to())) {
                result.add(edge);
                tree.addEdge(edge.from(), edge.label(), edge.to());
                tree.addEdge(edge.to(), edge.label(), edge.from());
                components.union(edge.from(), edge.to());
            }
        }
        return new KruskalResult<String>(
            result,
            new GraphWithLayout<String, Integer, Integer>(tree, problem.graphWithLayout().layout())
        );
    }

    @Override
    public String commandPrefix() {
        return "Kruskal";
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
                ((GridGraphLayout<String, Integer>)problem.graphWithLayout().layout()).setDirected(false)
            ),
            GraphAlgorithm.parseDistanceFactor(options)
        );
    }

    @Override
    public void printAfterSingleProblemInstance(
        final GraphProblem problem,
        final KruskalResult<String> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("F\\\"uhren Sie den \\emphasize{Algorithmus von Kruskal} auf diesem Graphen aus.");
        Main.newLine(writer);
        writer.write("Geben Sie dazu die Kanten in der Reihenfolge an, in der sie vom Algorithmus zum minimalen ");
        writer.write("Spannbaum hinzugef\\\"ugt werden, und geben Sie den resultierenden minimalen Spannbaum ");
        writer.write("an:\\\\[2ex]");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<GraphProblem> problems,
        final List<KruskalResult<String>> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("F\\\"uhren Sie den \\emphasize{Algorithmus von Kruskal} auf den folgenden Graphen aus.");
        Main.newLine(writer);
        writer.write("Geben Sie dazu jeweils die Kanten in der Reihenfolge an, in der sie vom Algorithmus zum ");
        writer.write("minimalen Spannbaum hinzugef\\\"ugt werden, und geben Sie den jeweils resultierenden ");
        writer.write("minimalen Spannbaum an.\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printSolutionInstance(
        final GraphProblem problem,
        final KruskalResult<String> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Kantenreihenfolge:\\\\[-2ex]");
        Main.newLine(writer);
        LaTeXUtils.printBeginning(LaTeXUtils.ENUMERATE, writer);
        for (final UndirectedEdge<String, Integer> edge : solution.edges()) {
            writer.write(LaTeXUtils.ITEM);
            writer.write(" ");
            writer.write(edge.toString());
            Main.newLine(writer);
        }
        LaTeXUtils.printEnd(LaTeXUtils.ENUMERATE, writer);
        writer.write("Minimaler Spannbaum:\\\\");
        Main.newLine(writer);
        LaTeXUtils.printAdjustboxBeginning(writer);
        solution.treeWithLayout().graph().printTikZ(
            GraphAlgorithm.stretch(
                ((GridGraphLayout<String, Integer>)solution.treeWithLayout().layout()).setDirected(false),
                GraphAlgorithm.parseDistanceFactor(options)
            ),
            writer
        );
        LaTeXUtils.printAdjustboxEnd(writer);
    }

    @Override
    public void printSolutionSpace(
        final GraphProblem problem,
        final KruskalResult<String> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
        writer.write(String.format("Kantenreihenfolge:\\\\[%dex]", solution.edges().size() * 4));
        Main.newLine(writer);
        writer.write("Minimaler Spannbaum:");
        Main.newLine(writer);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("10ex"), options, writer);
    }

}
