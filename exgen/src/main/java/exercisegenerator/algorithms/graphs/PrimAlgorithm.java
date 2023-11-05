package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

public class PrimAlgorithm implements AlgorithmImplementation {

    static class PrimEntry implements Comparable<Integer> {

        final boolean done;

        final boolean infinity;

        final int value;

        PrimEntry(final int value) {
            this(value, false, false);
        }

        PrimEntry(final int value, final boolean infinity, final boolean done) {
            this.done = done;
            this.value = value;
            this.infinity = infinity;
        }

        @Override
        public int compareTo(final Integer value) {
            if (this.infinity) {
                return value == null ? 0 : 1;
            }
            if (value == null) {
                return -1;
            }
            return Integer.compare(this.value, value);
        }

        @Override
        public boolean equals(final Object o) {
            if (o instanceof PrimEntry) {
                final PrimEntry other = (PrimEntry)o;
                if (this.infinity) {
                    return other.infinity;
                }
                return this.value == other.value && this.done == other.done;
            }
            return false;
        }

        @Override
        public int hashCode() {
            if (this.infinity) {
                return 42;
            }
            return this.value + (this.done ? 13 : 0);
        }

        @Override
        public String toString() {
            return this.infinity ?
                "$\\infty$" :
                    (this.done ? String.format("\\underline{%d}", this.value) : String.valueOf(this.value));
        }

        PrimEntry done() {
            return new PrimEntry(this.value, this.infinity, true);
        }

    }

    static class PrimResult<V> {

        final PrimEntry[][] table;

        final Graph<V, Integer> tree;

        PrimResult(final PrimEntry[][] table, final Graph<V, Integer> tree) {
            this.table = table;
            this.tree = tree;
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean equals(final Object o) {
            if (o instanceof PrimResult) {
                final PrimResult<V> other = (PrimResult<V>)o;
                return Arrays.deepEquals(this.table, other.table) && this.tree.logicallyEquals(other.tree);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.table.hashCode() * this.tree.hashCode();
        }

        @Override
        public String toString() {
            return String.format("(%s, %s)", Arrays.deepToString(this.table), this.tree.toString());
        }

    }

    public static final PrimAlgorithm INSTANCE = new PrimAlgorithm();

    static final PrimEntry INFINITY = new PrimEntry(0, true, false);

    private static final String PRIM_PATTERN =
        "F\\\"uhren Sie den \\emphasize{Algorithmus von Prim} auf diesem Graphen mit dem \\emphasize{Startknoten %s} aus.";

    public static <V> PrimResult<V> prim(
        final Graph<V, Integer> graph,
        final Vertex<V> start,
        final Optional<Comparator<Vertex<V>>> comparator
    ) {
        final List<Vertex<V>> vertices = new ArrayList<Vertex<V>>(graph.getVertices());
        if (comparator.isPresent()) {
            Collections.sort(vertices, comparator.get());
        }
        final int numberOfVertices = vertices.size();
        final Graph<V, Integer> tree = graph.nodeCopy();
        int recentlyAdded = vertices.indexOf(start);
        final int[] parents = new int[numberOfVertices];
        final PrimEntry[][] table = new PrimEntry[numberOfVertices][numberOfVertices];
        for (int i = 0; i < numberOfVertices; i++) {
            table[0][i] = i == recentlyAdded ? new PrimEntry(0, false, true) : PrimAlgorithm.INFINITY;
            parents[i] = -1;
        }
        for (int i = 1; i < numberOfVertices; i++) {
            for (final Edge<Integer,V> edge : graph.getAdjacencyList(vertices.get(recentlyAdded))) {
                final int toIndex = vertices.indexOf(edge.to);
                final PrimEntry toEntry = table[i - 1][toIndex];
                if (toEntry != null && !toEntry.done && toEntry.compareTo(edge.label) > 0) {
                    table[i][toIndex] = new PrimEntry(edge.label);
                    parents[toIndex] = recentlyAdded;
                }
            }
            Integer min = null;
            int minIndex = -1;
            for (int vertex = 0; vertex < numberOfVertices; vertex++) {
                if (table[i][vertex] == null && table[i - 1][vertex] != null && !table[i - 1][vertex].done) {
                    table[i][vertex] = table[i - 1][vertex];
                }
                final PrimEntry current = table[i][vertex];
                if (current != null && !current.done && current.compareTo(min) < 0) {
                    min = current.value;
                    minIndex = vertex;
                }
            }
            table[i][minIndex] = table[i][minIndex].done();
            final Vertex<V> from = vertices.get(parents[minIndex]);
            final Vertex<V> to = vertices.get(minIndex);
            tree.addEdge(from, min, to);
            tree.addEdge(to, min, from);
            tree.setGrid(graph.getGrid());
            recentlyAdded = minIndex;
        }
        return new PrimResult<V>(table, tree);
    }

    private static void printExercise(
        final Graph<String, Integer> graph,
        final String startLabel,
        final PrimEntry[][] table,
        final List<Vertex<String>> vertices,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        GraphAlgorithms.printGraphExercise(
            graph,
            String.format(PrimAlgorithm.PRIM_PATTERN, startLabel),
            GraphAlgorithms.parseDistanceFactor(options),
            GraphPrintMode.UNDIRECTED,
            writer
        );
        writer.write(
            "F\\\"ullen Sie dazu die nachfolgende Tabelle aus und geben Sie den resultierenden minimalen Spannbaum an:\\\\[2ex]"
        );
        Main.newLine(writer);
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
        PrimAlgorithm.printTable(table, vertices, false, writer);
        writer.write("Minimaler Spannbaum:");
        Main.newLine(writer);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("10ex"), options, writer);
    }

    private static void printSolution(
        final PrimEntry[][] table,
        final List<Vertex<String>> vertices,
        final Graph<String, Integer> tree,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        PrimAlgorithm.printTable(table, vertices, true, writer);
        writer.write("Minimaler Spannbaum:");
        Main.newLine(writer);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        tree.printTikZ(GraphPrintMode.UNDIRECTED, GraphAlgorithms.parseDistanceFactor(options), null, writer);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
        Main.newLine(writer);
    }

    private static void printTable(
        final PrimEntry[][] table,
        final List<Vertex<String>> vertices,
        final boolean solution,
        final BufferedWriter writer
    ) throws IOException {
        final String[][] result = new String[table.length + 1][table[0].length + 1];
        result[0][0] = "\\#Iteration";
        int index = 1;
        for (final Vertex<String> vertex : vertices) {
            result[0][index++] = vertex.label.get();
        }
        for (int i = 0; i < table.length; i++) {
            result[i + 1][0] = String.valueOf(i + 1);
            for (int j = 0; j < table[i].length; j++) {
                result[i + 1][j + 1] = solution ? (table[i][j] == null ? "" : table[i][j].toString()) : "";
            }
        }
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        LaTeXUtils.printArrayStretch(1.5, writer);
        LaTeXUtils.printTable(
            result,
            Optional.empty(),
            cols -> String.format("|c|*{%d}{C{%s}|}", cols - 1, "7mm"),
            true,
            0,
            writer
        );
        LaTeXUtils.printArrayStretch(1, writer);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
    }

    private PrimAlgorithm() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final Pair<Graph<String, Integer>, Vertex<String>> pair = GraphAlgorithms.parseOrGenerateGraph(input.options);
        final StringVertexComparator comparator = new StringVertexComparator();
        final Graph<String, Integer> graph = pair.x;
        final Vertex<String> start = pair.y;
        final PrimResult<String> result = PrimAlgorithm.prim(graph, start, Optional.of(comparator));
        final List<Vertex<String>> vertices = new ArrayList<Vertex<String>>(graph.getVertices());
        Collections.sort(vertices, comparator);
        PrimAlgorithm.printExercise(
            graph,
            start.label.get(),
            result.table,
            vertices,
            input.options,
            input.exerciseWriter
        );
        PrimAlgorithm.printSolution(result.table, vertices, result.tree, input.options, input.solutionWriter);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
