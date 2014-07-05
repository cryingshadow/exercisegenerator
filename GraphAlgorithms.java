import java.io.*;
import java.util.*;

/**
 * Class offering methods for graph algorithms.
 * @author Thomas Stroeder
 * @version $Id$
 */
public abstract class GraphAlgorithms {

    /**
     * Flag to enable special wishes of Erika for Dijkstra's algorithm.
     */
    private static final boolean ERIKA_MODE = true;
    
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
        final String[][] exTable;
        final String[][] solTable;
        final String[][] exColor;
        final String[][] solColor;
        if (GraphAlgorithms.ERIKA_MODE) {
            exTable = new String[size][size + 1];
            solTable = new String[size][size + 1];
            exColor = new String[size][size + 1];
            solColor = new String[size][size + 1];
            solTable[0][0] = "\\texttt{v}";
            exTable[0][0] = solTable[0][0];
            Integer[] distances = new Integer[size];
            Map<Node<N>, Integer> ids = new LinkedHashMap<Node<N>, Integer>();
            int i = 1;
            for (Node<N> node : nodes) {
                if (!node.equals(start)) {
                    solTable[0][i + 1] = "\\texttt{key[}" + node.getLabel().toString() + "\\texttt{]}";
                    exTable[0][i + 1] = solTable[0][i + 1];
                    ids.put(node, i);
                    i++;
                }
            }
            solTable[0][1] = "\\texttt{key[}" + start.getLabel().toString() + "\\texttt{]}";
            exTable[0][1] = solTable[0][1];
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
                for (int j = 1; j <= size; j++) {
                    final Integer dist = distances[j - 1];
                    if (dist == null) {
                        solTable[i][j] = "$\\infty$";
                    } else {
                        if (!used.contains(j - 1) && (curMin == null || curMin > dist)) {
                            curMin = dist;
                            minIndex = j - 1;
                        }
                        solTable[i][j] = "" + dist;
                    }
                    exTable[i][j] = "";
                }
                if (minIndex < 0) {
                    // no shortening possible
                    break;
                }
                current = minIndex;
                solColor[i][current + 1] = "black!20";
            }
        } else {
            exTable = new String[size][size];
            solTable = new String[size][size];
            exColor = new String[size][size];
            solColor = new String[size][size];
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
                solColor[i][current + 1] = "black!20";
            }
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
        if (GraphAlgorithms.ERIKA_MODE) {
            exWriter.write(" aus. F\\\"ullen Sie dazu die nachfolgende Tabelle aus, indem Sie den Wert von ");
            exWriter.write("\\texttt{v} und \\texttt{key} nach jeder Iteration der \\texttt{while}-Schleife ");
            exWriter.write("eintragen:\\\\[2ex]");
        } else {
            exWriter.write(" aus. F\\\"ullen Sie dazu die nachfolgende Tabelle aus:\\\\[2ex]");
        }
        exWriter.newLine();
        TikZUtils.printBeginning(TikZUtils.CENTER, exWriter);
        exWriter.newLine();
        TikZUtils.printArrayStretch(1.5, exWriter);
        TikZUtils.printArrayStretch(1.5, solWriter);
        TikZUtils.printTable(exTable, exColor, "2cm", exWriter);
        TikZUtils.printTable(solTable, solColor, "2cm", solWriter);
        TikZUtils.printArrayStretch(1.0, exWriter);
        TikZUtils.printArrayStretch(1.0, solWriter);
        solWriter.newLine();
        solWriter.write("\\vspace*{1ex}");
        solWriter.newLine();
        solWriter.newLine();
        solWriter.write("Die grau unterlegten Zellen markieren, an welcher Stelle f\\\"ur welchen Knoten die minimale");
        solWriter.write(" Distanz sicher berechnet worden ist.");
        solWriter.newLine();
        TikZUtils.printEnd(TikZUtils.CENTER, exWriter);
    }
    
    /**
     * Prints exercise and solution for the Floyd Algorithm.
     * @param graph The graph.
     * @param warshall Flag indicating whether the Floyd-Warshall or just the Floyd algorithm should be performed.
     * @param start The start node.
     * @param comp A comparator for sorting the nodes in the table (may be null - then no sorting is applied).
     * @param exWriter The writer to send the exercise output to.
     * @param solWriter The writer to send the solution output to.
     * @throws IOException If some error occurs during output.
     */
    public static <N> void floyd(
        Graph<N, Integer> graph,
        boolean warshall,
        Comparator<Node<N>> comp,
        BufferedWriter exWriter,
        BufferedWriter solWriter
    ) throws IOException {
        final int tableCount = 2;
        final List<Node<N>> nodes = new ArrayList<Node<N>>(graph.getNodes());
        final int size = nodes.size();
        final ArrayList<String[][]> exercises = new ArrayList<String[][]>();
        final ArrayList<String[][]> solutions = new ArrayList<String[][]>();
        final ArrayList<String[][]> exColors = new ArrayList<String[][]>();
        final ArrayList<String[][]> solColors = new ArrayList<String[][]>();
        String[][] firstExercise = new String[size+1][size+1];
        String[][] otherExercise = new String[size+1][size+1];
        String[][] currentSolution = new String[size+1][size+1];
        String[][] curExColor = new String[size+1][size+1];
        String[][] curSolColor = new String[size+1][size+1];
        Integer[][] weights = new Integer[size][size];
        boolean[][] changed = new boolean[size][size];
        // initialize ids
        Map<Node<N>, Integer> ids = new LinkedHashMap<Node<N>, Integer>();
        for (int current = 0 ; current < size; ++current) {
            Node<N> currentNode = nodes.get(current);
            ids.put(currentNode, current);
        }
        firstExercise[0][0] = "";
        otherExercise[0][0] = "";
        currentSolution[0][0] = "";
        // initialize weights
        for (int current = 0 ; current < size; ++current) {
            Node<N> currentNode = nodes.get(current);
            // set labels
            firstExercise[0][current+1] = currentNode.getLabel().toString();
            firstExercise[current+1][0] = currentNode.getLabel().toString();
            otherExercise[0][current+1] = currentNode.getLabel().toString();
            otherExercise[current+1][0] = currentNode.getLabel().toString();
            currentSolution[0][current+1] = currentNode.getLabel().toString();
            currentSolution[current+1][0] = currentNode.getLabel().toString();
            // prepare weights and solution array
            for (int i = 0; i < size; ++i) {
                if (!warshall) {
                    firstExercise[current+1][i+1] = "$\\infty$";
                    currentSolution[current+1][i+1] = "$\\infty$";
                } else {
                    firstExercise[current+1][i+1] = "false";
                    currentSolution[current+1][i+1] = "false";
                }
                weights[current][i] = null;
            }
            for (Pair<Integer, Node<N>> edge : graph.getAdjacencyList(currentNode)) {
                weights[current][ids.get(edge.y)] = edge.x;
                if (!warshall) {
                    firstExercise[current+1][ids.get(edge.y)+1] = edge.x.toString();
                    currentSolution[current+1][ids.get(edge.y)+1] = edge.x.toString();
                } else {
                    firstExercise[current+1][ids.get(edge.y)+1] = "true";
                    currentSolution[current+1][ids.get(edge.y)+1] = "true";
                //System.out.println("Add: " + currentNode.getLabel() + " -" + currentSolution[current][ids.get(edge.y)] + "-> " + edge.y.getLabel());
                }
            }
            weights[current][current] = 0;
            if (!warshall) {
                firstExercise[current+1][current+1] = "0";
                currentSolution[current+1][current+1] = "0";
            } else {
                firstExercise[current+1][current+1] = "true";
                currentSolution[current+1][current+1] = "true";
            }
        }
        exercises.add(firstExercise);
        solutions.add(currentSolution);
        exColors.add(curExColor);
        solColors.add(curSolColor);
        // clear solution and reset
        currentSolution = new String[size+1][size+1];
        curSolColor = new String[size+1][size+1];
        currentSolution[0][0] = "";
        for (int current = 0 ; current < size; ++current) {
            Node<N> currentNode = nodes.get(current);
            // set labels
            currentSolution[0][current+1] = currentNode.getLabel().toString();
            currentSolution[current+1][0] = currentNode.getLabel().toString();
        }
        // actual algorithm
        for (int intermediate = 0; intermediate < size; ++intermediate) {
            for (int start = 0; start < size; ++start) {
                for (int target = 0; target < size; ++target) {
                    Integer oldValue = weights[start][target];
                    if (weights[start][target] != null) {
                        if (weights[start][intermediate] != null && weights[intermediate][target] != null) {
                            weights[start][target] =
                                Integer.compare(
                                    weights[start][target],
                                    weights[start][intermediate] + weights[intermediate][target]
                                ) < 0 ?
                                    weights[start][target] :
                                        weights[start][intermediate] + weights[intermediate][target];
                        }
                        // no else here as we can keep the old value as the path is currently infinite (null)
                    } else if (weights[start][intermediate] != null && weights[intermediate][target] != null) {
                        weights[start][target] = weights[start][intermediate] + weights[intermediate][target];
                    }
                    if(!warshall) {
                        changed[start][target] = (oldValue != weights[start][target]);
                    } else {
                        changed[start][target] = (oldValue == null && weights[start][target] != null);
                    }
                }
            }
            // write solution
            for (int i = 0; i < size; ++i) {
                for (int j = 0; j < size; ++j) {
                    if (changed[i][j]) {
                        curSolColor[i+1][j+1] = "black!20";
                    }
                    if (weights[i][j] == null) {
                        if (!warshall) {
                            currentSolution[i+1][j+1] = "$\\infty$";
                        } else {
                            currentSolution[i+1][j+1] = "false";
                        }
                    } else {
                        if (!warshall) {
                            currentSolution[i+1][j+1] = "" + weights[i][j];
                        } else {
                            currentSolution[i+1][j+1] = "true";
                        }
                    }
                }
            }
            exercises.add(otherExercise);
            solutions.add(currentSolution);
            exColors.add(curExColor);
            solColors.add(curSolColor);
            // clear solution and reset.
            currentSolution = new String[size+1][size+1];
            curSolColor = new String[size+1][size+1];
            currentSolution[0][0] = "";
            for (int current = 0 ; current < size; ++current) {
                Node<N> currentNode = nodes.get(current);
                // set labels
                currentSolution[0][current+1] = currentNode.getLabel().toString();
                currentSolution[current+1][0] = currentNode.getLabel().toString();
            }
            changed = new boolean[size][size];
        }
        // create output
        exWriter.write("Betrachten Sie den folgenden Graphen:\\\\[2ex]");
        exWriter.newLine();
        TikZUtils.printBeginning(TikZUtils.CENTER, exWriter);
        graph.printTikZ(exWriter);
        exWriter.newLine();
        TikZUtils.printEnd(TikZUtils.CENTER, exWriter);
        exWriter.newLine();
        if (!warshall) {
            exWriter.write("F\\\"uhren Sie den Algorithmus von Floyd auf diesem Graphen aus. ");
        } else {
            exWriter.write("F\\\"uhren Sie den Algorithmus von Warshall auf diesem Graphen aus. ");
        }
        exWriter.write("Geben Sie dazu nach jedem Durchlauf der \\\"au{\\ss}eren Schleife die aktuellen Entfernungen ");
        exWriter.write("in einer Tabelle an.\\\\[2ex]");
        exWriter.newLine();
        exWriter.newLine();
        TikZUtils.printArrayStretch(1.5, exWriter);
        TikZUtils.printArrayStretch(1.5, solWriter);
        int solCount = 0;
        int exCount = 0;
        for (int iteration = 0; iteration < solutions.size(); ++iteration) {
            solCount =
                GraphAlgorithms.printTables(
                    solCount,
                    tableCount,
                    iteration,
                    solutions.get(iteration),
                    solColors.get(iteration),
                    solWriter
                );
            exCount =
                GraphAlgorithms.printTables(
                    exCount,
                    tableCount,
                    iteration,
                    exercises.get(iteration),
                    exColors.get(iteration),
                    exWriter
                );
        }
        TikZUtils.printArrayStretch(1.0, exWriter);
        TikZUtils.printArrayStretch(1.0, solWriter);
    }
	
	/**
     * Prints exercise and solution for Prim's algorithm.
     * @param graph The graph.
     * @param start The start node.
     * @param comp A comparator for sorting the nodes in the table (may be null - then no sorting is applied).
     * @param exWriter The writer to send the exercise output to.
     * @param solWriter The writer to send the solution output to.
     * @throws IOException If some error occurs during output.
     */
    public static <N> void prim(
        Graph<N, Integer> graph,
        Node<N> start,
        Comparator<Node<N>> comp,
        BufferedWriter exWriter,
        BufferedWriter solWriter
    ) throws IOException {
		List<Node<N>> nodes = new ArrayList<Node<N>>(graph.getNodes());
		String[][] solutions = new String[nodes.size()+1][nodes.size()+1];
		solutions[0][0] = "\\#Iteration";
		solutions[0][1] = "0";
		Map<Node<N>, Integer> key = new LinkedHashMap<Node<N>, Integer>();
		
		Map<Node<N>, List<Pair<Integer, Node<N>>>> parent = new LinkedHashMap<Node<N>, List<Pair<Integer, Node<N>>>>();
		int i = 1;
		for (Node<N> node : nodes) {
		    key.put(node, null);
		    solutions[i][0] = node.getLabel().toString();
		    i++;
		}
		List<Node<N>> q = new ArrayList<Node<N>>(graph.getNodes());
		key.put(start, new Integer(0));
		int iteration = 1;
		
		// actual algorithm
		while (!q.isEmpty()) {
		    // extract the minimum from q
		    Node<N> minNode = null;
		    for (Node<N> node : q)
		    {
		        if (minNode == null || key.get(minNode) == null || (key.get(node) != null && key.get(minNode).intValue() > key.get(node).intValue())) {
		            minNode = node;
		        }
		    }
			
			// write solution
			solutions[0][iteration] = "" + iteration;
			i = 1;
		    for (Node<N> node : nodes) {
		        if (q.contains(node)) {
		            if (key.get(node) == null) {
		                solutions[i][iteration] = "$\\infty$";
		            } else {
		                if (minNode == node) {
		                    solutions[i][iteration] = "\\underline{" + key.get(node) + "}";
		                } else {
		                    solutions[i][iteration] = "" + key.get(node);
		                }
		            }
                } else {
                    solutions[i][iteration] = "";
                }
                i++;
		    }
		    // update the minimums successors remaining in q
			for (Pair<Integer, Node<N>> edge : graph.getAdjacencyList(minNode)) {
                if (q.contains(edge.y) && (key.get(edge.y) == null || edge.x < key.get(edge.y))) {
		            List<Pair<Integer, Node<N>>> adList = new ArrayList<Pair<Integer, Node<N>>>();
		            adList.add(new Pair<Integer, Node<N>>(edge.x, minNode));
                    parent.put(edge.y, adList);
                    key.put(edge.y, edge.x);
                }
            }
            q.remove(minNode);
		    iteration++;
		}
		
		// create output
		exWriter.write("F\\\"uhren Sie Prim's Algorithmus auf dem folgenden Graphen aus.");
		exWriter.write(" Geben Sie dazu \\underline{vor} jedem Durchlauf der \\\"au{\\ss}eren Schleife an");
        exWriter.newLine();
		exWriter.write("\\begin{enumerate}");
        exWriter.newLine();
		exWriter.write("    \\item welchen Knoten \\texttt{extractMin(Q)} w\\\"ahlt");
        exWriter.newLine();
		exWriter.write("    \\item und welche Kosten die Randknoten haben, d.h. f\\\"ur jeden Knoten \\texttt{v} in \\texttt{Q} den Wert \\texttt{key[v]}.");
        exWriter.newLine();
		exWriter.write("\\end{enumerate}");
        exWriter.newLine();
		exWriter.write(" Geben Sie zudem den vom Algorithmus bestimmten minimalen Spannbaum an.\\\\[2ex]");
        exWriter.newLine();
        TikZUtils.printBeginning(TikZUtils.CENTER, exWriter);
        graph.printTikZ(exWriter, null, false   );
        exWriter.newLine();
        TikZUtils.printEnd(TikZUtils.CENTER, exWriter);
		
		TikZUtils.printTable(solutions, 2.0, solWriter);
		solWriter.newLine();
		solWriter.newLine();
		solWriter.write("\\medskip");
		solWriter.newLine();
		// print the spanning tree
		solWriter.write("Hierbei gibt eine unterstrichene Zahl an in welcher Iteration (zugeh\\\"origer Zeilenkopf)");
		solWriter.write(" welcher Knoten (zugeh\\\"origer Spaltenkopf) durch \\texttt{extractMin(Q)} gew\\\"ahlt");
		solWriter.write(" wurde. Wir erhalten den folgenden minimalen Spannbaum:\\\\[2ex]");
        solWriter.newLine();
        TikZUtils.printBeginning(TikZUtils.CENTER, solWriter);
        graph.printTikZ(solWriter, parent, false);
        solWriter.newLine();
        TikZUtils.printEnd(TikZUtils.CENTER, solWriter);
    }

    /**
     * Prints exercise and solution for the Ford-Fulkerson method. Uses Diniz's algorithm for selecting augmenting 
     * paths.
     * @param graph The flow network.
     * @param source The source node.
     * @param sink The sink node.
     * @param exWriter The writer to send the exercise output to.
     * @param solWriter The writer to send the solution output to.
     * @throws IOException If some error occurs during output.
     */
    public static <N> void fordFulkerson(
        Graph<N, Pair<Integer, Integer>> graph,
        Node<N> source,
        Node<N> sink,
        BufferedWriter exWriter,
        BufferedWriter solWriter
    ) throws IOException {
        //TODO exercise text
        while (true) {
            List<Node<N>> path =
                GraphAlgorithms.selectAugmentingPath(
                    GraphAlgorithms.computeResidualGraph(graph, solWriter),
                    source,
                    sink
                );
            if (path == null) {
                break;
            }
            GraphAlgorithms.addFlow(graph, path, solWriter);
        }
    }

    private static <N> void addFlow(Graph<N, Pair<Integer, Integer>> graph, List<Node<N>> path, BufferedWriter writer)
    throws IOException {
        //TODO
    }

    private static <N> Graph<N, Integer> computeResidualGraph(
        Graph<N, Pair<Integer, Integer>> graph,
        BufferedWriter writer
    ) throws IOException {
        //TODO
        return null;
    }

    /**
     * Prints the specified table with the specified cell colors and possibly insert line breaks.
     * @param count The current number of tables printed in one row.
     * @param tableCount The max number of tables printed in one row.
     * @param iteration The current iteration.
     * @param table The table to print.
     * @param color The cell colors.
     * @param writer The writer to send the output to.
     * @return The next number of tables printed in the current row.
     * @throws IOException If some error occurs during output.
     */
    private static int printTables(
        int count,
        int tableCount,
        int iteration,
        String[][] table,
        String[][] color,
        BufferedWriter writer
    ) throws IOException {
        if (count < tableCount) {
            TikZUtils.printTable(table, color, "1cm", writer);
            writer.newLine();
            writer.write("\\hspace{2em}");
            writer.newLine();
            return count + 1;
        }
        writer.write("\\\\[2ex]");
        writer.newLine();
        TikZUtils.printTable(table, color, "1cm", writer);
        writer.newLine();
        writer.write("\\hspace{2em}");
        writer.newLine();
        return 1;
    }

    private static <N> List<Node<N>> selectAugmentingPath(Graph<N, Integer> graph, Node<N> source, Node<N> sink) {
        //TODO
        return null;
    }

}
