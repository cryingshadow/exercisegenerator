package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

public class PrimAlgorithm implements AlgorithmImplementation {

    public static final PrimAlgorithm INSTANCE = new PrimAlgorithm();

    /**
     * Prints exercise and solution for Prim's algorithm.
     * @param graph The graph.
     * @param start The start vertex.
     * @param comp A comparator for sorting the vertices in the table (may be null - then no sorting is applied).
     * @param exWriter The writer to send the exercise output to.
     * @param solWriter The writer to send the solution output to.
     * @throws IOException If some error occurs during output.
     */
    public static <V> void prim(
        final Graph<V, Integer> graph,
        final Vertex<V> start,
        final Comparator<Vertex<V>> comp,
        final BufferedWriter exWriter,
        final BufferedWriter solWriter
    ) throws IOException {
        final List<Vertex<V>> vertices = new ArrayList<Vertex<V>>(graph.getVertices());
        final String[][] exTable = new String[vertices.size()+1][vertices.size()+1];
        final String[][] solTable = new String[vertices.size()+1][vertices.size()+1];
        exTable[0][0] = "\\#Iteration";
        solTable[0][0] = "\\#Iteration";
        final Map<Vertex<V>, Integer> key = new LinkedHashMap<Vertex<V>, Integer>();
        final AdjacencyLists<V, Integer> parent = new AdjacencyLists<V, Integer>();
        int i = 1;
        for (final Vertex<V> vertex : vertices) {
            key.put(vertex, null);
            final String label = vertex.label.isEmpty() ? "" : vertex.label.get().toString();
            exTable[i][0] = label;
            solTable[i][0] = label;
            i++;
        }
        final List<Vertex<V>> q = new ArrayList<Vertex<V>>(graph.getVertices());
        key.put(start, 0);
        int iteration = 1;
        // actual algorithm
        while (!q.isEmpty()) {
            // extract the minimum from q
            Vertex<V> minVertex = null;
            for (final Vertex<V> vertex : q) {
                if (
                    minVertex == null
                    || key.get(minVertex) == null
                    || (key.get(vertex) != null && key.get(minVertex).intValue() > key.get(vertex).intValue())
                ) {
                    minVertex = vertex;
                }
            }
            // write solution
            exTable[0][iteration] = "" + iteration;
            solTable[0][iteration] = "" + iteration;
            i = 1;
            for (final Vertex<V> vertex : vertices) {
                if (q.contains(vertex)) {
                    if (key.get(vertex) == null) {
                        solTable[i][iteration] = "$\\infty$";
                    } else if (minVertex == vertex) {
                        solTable[i][iteration] = "\\underline{" + key.get(vertex) + "}";
                    } else {
                        solTable[i][iteration] = "" + key.get(vertex);
                    }
                } else {
                    solTable[i][iteration] = "";
                }
                i++;
            }
            // update the minimums successors remaining in q
            for (final Edge<Integer, V> edge : graph.getAdjacencyList(minVertex)) {
                if (q.contains(edge.y) && (key.get(edge.y) == null || edge.x < key.get(edge.y))) {
                    final List<Edge<Integer, V>> adList = new ArrayList<Edge<Integer, V>>();
                    adList.add(new Edge<Integer, V>(edge.x, minVertex));
                    parent.put(edge.y, adList);
                    key.put(edge.y, edge.x);
                }
            }
            q.remove(minVertex);
            iteration++;
        }
        // create output
        for (int j = 1; j < exTable.length; j++) {
            exTable[j][1] = solTable[j][1];
        }
        exWriter.write("F\\\"uhren Sie Prim's Algorithmus auf dem folgenden Graphen aus.");
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, exWriter);
        graph.printTikZ(exWriter, null, false);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, exWriter);
        if (start.label.isEmpty()) {
            throw new IllegalArgumentException("Prim cannot be applied to unlabeled graphs!");
        }
        exWriter.write("Der Startknoten hat hierbei den Schl\\\"ussel " + start.label.get().toString() + ".");
        exWriter.write(" Geben Sie dazu \\emphasize{vor} jedem Durchlauf der \\\"au{\\ss}eren Schleife an,");
        Main.newLine(exWriter);
        exWriter.write("\\begin{enumerate}[label=\\arabic*)]");
        Main.newLine(exWriter);
        exWriter.write("    \\item welche Kosten die Randknoten haben (d.\\,h.~f\\\"ur jeden Knoten \\texttt{v} in ");
        exWriter.write("\\texttt{Q} den Wert \\texttt{key[v]})");
        Main.newLine(exWriter);
        exWriter.write("    \\item und welchen Knoten \\texttt{extractMin(Q)} w\\\"ahlt, indem Sie den Kosten-Wert ");
        exWriter.write("des gew\\\"ahlten Randknoten in der Tabelle unterstreichen (wie es in der ersten Zeile ");
        exWriter.write("bereits vorgegeben ist).");
        Main.newLine(exWriter);
        exWriter.write("\\end{enumerate}");
        Main.newLine(exWriter);
        exWriter.write(" Geben Sie zudem den vom Algorithmus bestimmten minimalen Spannbaum an.");
        Main.newLine(exWriter);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, exWriter);
        LaTeXUtils.printArrayStretch(1.5, exWriter);
        LaTeXUtils.printTable(
            exTable,
            Optional.empty(),
            LaTeXUtils.defaultColumnDefinition("2.0cm"),
            false,
            10,
            exWriter
        );
        LaTeXUtils.printArrayStretch(1, exWriter);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, exWriter);
        exWriter.write("Minimaler Spannbaum:");
        Main.newLine(exWriter);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, solWriter);
        LaTeXUtils.printArrayStretch(1.5, solWriter);
        LaTeXUtils.printTable(
            solTable,
            Optional.empty(),
            LaTeXUtils.defaultColumnDefinition("2.0cm"),
            false,
            10,
            solWriter
        );
        LaTeXUtils.printArrayStretch(1, solWriter);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, solWriter);
        // print the spanning tree
        solWriter.write("Hierbei gibt eine unterstrichene Zahl an in welcher Iteration (zugeh\\\"origer Zeilenkopf)");
        solWriter.write(" welcher Knoten (zugeh\\\"origer Spaltenkopf) durch \\texttt{extractMin(Q)} gew\\\"ahlt");
        solWriter.write(" wurde. Wir erhalten den folgenden minimalen Spannbaum:");
        Main.newLine(solWriter);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, solWriter);
        graph.printTikZ(solWriter, parent, false);
        Main.newLine(solWriter);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, solWriter);
    }

    private PrimAlgorithm() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final Pair<Graph<String, Integer>, Vertex<String>> pair =
            GraphAlgorithms.parseOrGenerateGraph(input.options);
        PrimAlgorithm.prim(pair.x, pair.y, new StringVertexComparator(), input.exerciseWriter, input.solutionWriter);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
