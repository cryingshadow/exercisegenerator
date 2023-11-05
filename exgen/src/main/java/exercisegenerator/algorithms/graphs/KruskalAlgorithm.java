package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

public class KruskalAlgorithm implements AlgorithmImplementation {

    static class KruskalResult<V> {

        final List<UndirectedEdge<V, Integer>> edges;

        final Graph<V, Integer> tree;

        KruskalResult(final List<UndirectedEdge<V, Integer>> edges, final Graph<V, Integer> tree) {
            this.edges = edges;
            this.tree = tree;
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean equals(final Object o) {
            if (o instanceof KruskalResult) {
                final KruskalResult<V> other = (KruskalResult<V>)o;
                return this.edges.equals(other.edges) && this.tree.logicallyEquals(other.tree);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.edges.hashCode() * this.tree.hashCode();
        }

        @Override
        public String toString() {
            return String.format("(%s, %s)", this.edges.toString(), this.tree.toString());
        }

    }

    public static final KruskalAlgorithm INSTANCE = new KruskalAlgorithm();

    public static <V> KruskalResult<V> kruskal(final Graph<V, Integer> graph) {
        final List<UndirectedEdge<V, Integer>> result = new ArrayList<UndirectedEdge<V, Integer>>();
        final Graph<V, Integer> tree = graph.nodeCopy();
        final LinkedList<UndirectedEdge<V, Integer>> edges =
            graph
            .getAllUndirectedEdges()
            .stream()
            .sorted(
                new Comparator<UndirectedEdge<V, Integer>>() {
                    @Override
                    public int compare(final UndirectedEdge<V, Integer> edge1, final UndirectedEdge<V, Integer> edge2) {
                        return edge1.label.get().compareTo(edge2.label.get());
                    }
                }
            ).collect(Collectors.toCollection(LinkedList::new));
        final UnionFind<Vertex<V>> components = new UnionFind<Vertex<V>>();
        while (!edges.isEmpty()) {
            final UndirectedEdge<V, Integer> edge = edges.removeFirst();
            if (!components.connected(edge.from, edge.to)) {
                result.add(edge);
                tree.addEdge(edge.from, edge.label, edge.to);
                tree.addEdge(edge.to, edge.label, edge.from);
                tree.setGrid(graph.getGrid());
                components.union(edge.from, edge.to);
            }
        }
        return new KruskalResult<V>(result, tree);
    }

    private static void printExercise(
        final Graph<String, Integer> graph,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        GraphAlgorithms.printGraphExercise(
            graph,
            "F\\\"uhren Sie den \\emphasize{Algorithmus von Kruskal} auf diesem Graphen aus.",
            GraphAlgorithms.parseDistanceFactor(options),
            GraphPrintMode.UNDIRECTED,
            writer
        );
        writer.write("Geben Sie dazu die Kanten in der Reihenfolge an, in der sie vom Algorithmus zum minimalen ");
        writer.write("Spannbaum hinzugef\\\"ugt werden, und geben Sie den resultierenden minimalen Spannbaum ");
        writer.write("an:\\\\[2ex]");
        Main.newLine(writer);
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
        writer.write("Kantenreihenfolge:\\\\[20ex]");
        Main.newLine(writer);
        writer.write("Minimaler Spannbaum:");
        Main.newLine(writer);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("10ex"), options, writer);
    }

    private static void printSolution(
        final List<UndirectedEdge<String, Integer>> edges,
        final Graph<String, Integer> tree,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Kantenreihenfolge:\\\\[-2ex]");
        Main.newLine(writer);
        LaTeXUtils.printBeginning(LaTeXUtils.ENUMERATE, writer);
        for (final UndirectedEdge<String, Integer> edge : edges) {
            writer.write(LaTeXUtils.ITEM);
            writer.write(" ");
            writer.write(edge.toString());
            Main.newLine(writer);
        }
        LaTeXUtils.printEnd(LaTeXUtils.ENUMERATE, writer);
        writer.write("Minimaler Spannbaum:");
        Main.newLine(writer);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        tree.printTikZ(GraphPrintMode.UNDIRECTED, GraphAlgorithms.parseDistanceFactor(options), null, writer);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
        Main.newLine(writer);
    }

    private KruskalAlgorithm() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final Pair<Graph<String, Integer>, Vertex<String>> pair = GraphAlgorithms.parseOrGenerateGraph(input.options);
        final Graph<String, Integer> graph = pair.x;
        final KruskalResult<String> result = KruskalAlgorithm.kruskal(graph);
        KruskalAlgorithm.printExercise(
            graph,
            input.options,
            input.exerciseWriter
        );
        KruskalAlgorithm.printSolution(result.edges, result.tree, input.options, input.solutionWriter);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
