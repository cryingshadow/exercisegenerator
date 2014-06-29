import java.io.*;
import java.util.*;

/**
 * Class offering methods for graph algorithms.
 * @author Thomas Stroeder
 * @version $Id$
 */
public abstract class GraphAlgorithms {

    /**
     * Prints exercise and solution for the Dijkstra Algorithm.
     * @param graph The graph.
     * @param start The start node.
     * @param comp A comparator for sorting the nodes in the table (may be null - then no sorting is applied).
     * @param exWriter The writer to send the exercise output to.
     * @param solWriter The writer to send the solution output to.
     * @throws IOException If some error occurs during output.
     */
    public static <N> void dijkstra(
        Graph<N, Integer> graph,
        Node<N> start,
        Comparator<Node<N>> comp,
        BufferedWriter exWriter,
        BufferedWriter solWriter
    ) throws IOException {
        final List<Node<N>> nodes = new ArrayList<Node<N>>(graph.getNodes());
        if (comp != null) {
            Collections.sort(nodes, comp);
        }
        final int size = nodes.size();
        String[][] exTable = new String[size][size];
        String[][] solTable = new String[size][size];
        solTable[0][0] = "\\textbf{Knoten}";
        exTable[0][0] = solTable[0][0];
        Integer[] distances = new Integer[size];
        Map<Node<N>, Integer> ids = new LinkedHashMap<Node<N>, Integer>();
        int i = 1;
        for (Node<N> node : nodes) {
            if (!node.equals(start)) {
                solTable[0][i] = "\\textbf{" + node.getLabel().toString() + "}";
                exTable[0][i] = solTable[0][i];
                ids.put(node, i);
                i++;
            }
        }
        int current = 0;
        distances[current] = 0;
        Set<Integer> used = new LinkedHashSet<Integer>();
        for (i = 1; i < size; i++) {
            used.add(current);
            Node<N> currentNode = nodes.get(current);
            solTable[i][0] = currentNode.getLabel().toString();
            exTable[i][0] = "";
            for (Pair<Integer, Node<N>> edge : graph.getAdjacencyList(currentNode)) {
                Integer to = ids.get(edge.y);
                if (to != null && (distances[to] == null || distances[to] > distances[current] + edge.x)) {
                    distances[to] = distances[current] + edge.x;
                }
            }
            Integer curMin = null;
            int minIndex = -1;
            for (int j = 1; j < size; j++) {
                final Integer dist = distances[j];
                if (dist == null) {
                    solTable[i][j] = "$\\infty$";
                } else {
                    if (used.contains(j)) {
                        solTable[i][j] = "\\textbf{--}";
                    } else {
                        if (curMin == null || curMin > dist) {
                            curMin = dist;
                            minIndex = j;
                        }
                        solTable[i][j] = "" + dist;
                    }
                }
                exTable[i][j] = "";
            }
            if (minIndex < 0) {
                // no shortening possible
                break;
            }
            current = minIndex;
        }
        exTable[1][0] = start.getLabel().toString();
        exWriter.write("Betrachten Sie den folgenden Graphen:\\\\[2ex]");
        exWriter.newLine();
        TikZUtils.printBeginning(TikZUtils.CENTER, exWriter);
        graph.printTikZ(exWriter);
        exWriter.newLine();
        TikZUtils.printEnd(TikZUtils.CENTER, exWriter);
        exWriter.newLine();
        exWriter.write("F\\\"uhren Sie den Dijkstra Algorithmus auf diesem Graphen mit dem Startknoten ");
        exWriter.write(start.getLabel().toString());
        exWriter.write(" aus. F\\\"ullen Sie dazu die nachfolgende Tabelle aus:\\\\[2ex]");
        exWriter.newLine();
        TikZUtils.printBeginning(TikZUtils.CENTER, exWriter);
        exWriter.newLine();
        exWriter.write("\\renewcommand{\\arraystretch}{1.5}");
        exWriter.newLine();
        solWriter.write("\\renewcommand{\\arraystretch}{1.5}");
        solWriter.newLine();
        TikZUtils.printTable(exTable, exWriter);
        TikZUtils.printTable(solTable, solWriter);
        exWriter.write("\\renewcommand{\\arraystretch}{1}");
        exWriter.newLine();
        solWriter.write("\\renewcommand{\\arraystretch}{1}");
        solWriter.newLine();
        TikZUtils.printEnd(TikZUtils.CENTER, exWriter);
    }

}
