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
        if (GraphAlgorithms.ERIKA_MODE) {
            exTable = new String[size][size + 1];
            solTable = new String[size][size + 1];
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
            }
        } else {
            exTable = new String[size][size];
            solTable = new String[size][size];
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
        exWriter.write("\\renewcommand{\\arraystretch}{1.5}");
        exWriter.newLine();
        solWriter.write("\\renewcommand{\\arraystretch}{1.5}");
        solWriter.newLine();
        TikZUtils.printTable(exTable, 2.0, exWriter);
        TikZUtils.printTable(solTable, 2.0, solWriter);
        exWriter.write("\\renewcommand{\\arraystretch}{1}");
        exWriter.newLine();
        solWriter.write("\\renewcommand{\\arraystretch}{1}");
        solWriter.newLine();
        TikZUtils.printEnd(TikZUtils.CENTER, exWriter);
    }
	
	/**
     * Prints exercise and solution for the Floyd Algorithm.
     * @param graph The graph.
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
		final ArrayList<String[][]> solutions = new ArrayList<String[][]>();
		String[][] currentSolution = new String[size+1][size+1];
		Integer[][] weights = new Integer[size][size];
		boolean[][] changed = new boolean[size][size];
		// initialize ids
		Map<Node<N>, Integer> ids = new LinkedHashMap<Node<N>, Integer>();
		for (int current = 0 ; current < size; ++current) {
			Node<N> currentNode = nodes.get(current);
			ids.put(currentNode, current);
		}
		currentSolution[0][0] = "";
		// initialize weights
		for (int current = 0 ; current < size; ++current) {
			Node<N> currentNode = nodes.get(current);
			// set labels
			currentSolution[0][current+1] = currentNode.getLabel().toString();
			currentSolution[current+1][0] = currentNode.getLabel().toString();
			
			// prepare weights and solution array
			for (int i = 0; i < size; ++i) {
				if(!warshall)
					currentSolution[current+1][i+1] = "$\\infty$";
				else
					currentSolution[current+1][i+1] = "false";
				weights[current][i] = null;
			}
			for (Pair<Integer, Node<N>> edge : graph.getAdjacencyList(currentNode)) {
				weights[current][ids.get(edge.y)] = edge.x;
				if(!warshall)
					currentSolution[current+1][ids.get(edge.y)+1] = edge.x.toString();
				else
					currentSolution[current+1][ids.get(edge.y)+1] = "true";
				//System.out.println("Add: " + currentNode.getLabel() + " -" + currentSolution[current][ids.get(edge.y)] + "-> " + edge.y.getLabel());
			}
			weights[current][current] = 0;
			if(!warshall)
				currentSolution[current+1][current+1] = "0";
			else
				currentSolution[current+1][current+1] = "true";
		}
		
		solutions.add(currentSolution);
		// clear solution and reset
		currentSolution = new String[size+1][size+1];
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
					if(weights[start][target] != null) {
						if(weights[start][intermediate] != null && weights[intermediate][target] != null) {
							weights[start][target] = Integer.compare(weights[start][target], weights[start][intermediate] + weights[intermediate][target]) < 0 ?
							weights[start][target] :
							weights[start][intermediate] + weights[intermediate][target];
						}
						// no else here as we can keep the old value as the path is currently infinite (null)
					}
					else if (weights[start][intermediate] != null && weights[intermediate][target] != null) {
						weights[start][target] = weights[start][intermediate] + weights[intermediate][target];
					}
					changed[start][target] = (oldValue != weights[start][target]);
				}
			}
			
			// write solution
			for (int i = 0; i < size; ++i) {
				for (int j = 0; j < size; ++j)
				{
					if(changed[i][j])
						currentSolution[i+1][j+1] = "\\textbf{";
					else
						currentSolution[i+1][j+1] = "";
					if(weights[i][j] == null) {
						if(!warshall)
							currentSolution[i+1][j+1] += "$\\infty$";
						else
							currentSolution[i+1][j+1] += "false";
					}
					else {
						if(!warshall)
							currentSolution[i+1][j+1] += "" + weights[i][j];
						else
							currentSolution[i+1][j+1] += "true";
					}
					if(changed[i][j])
						currentSolution[i+1][j+1] += "}";
				}
			}
			solutions.add(currentSolution);
			// clear solution and reset.
			currentSolution = new String[size+1][size+1];
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
        exWriter.write("F\\\"uhren Sie den Algorithmus von Ford auf diesem Graphen aus. ");
		exWriter.write("Geben Sie dazu nach jedem Durchlauf der \\\"au{\\ss}eren Schleife die aktuellen Entfernungen in einer Tabelle an.");
		int count = 0;
		for (int iteration = 0; iteration < solutions.size(); ++iteration) {
			if(count < tableCount) {
				TikZUtils.printTable(solutions.get(iteration), 0.8, solWriter);
				solWriter.newLine();
				solWriter.write("\\hspace{2em}");
				solWriter.newLine();
				++count;
			}
			else
			{
				solWriter.write("\\\\[2ex]");
				solWriter.newLine();
				TikZUtils.printTable(solutions.get(iteration), 0.8, solWriter);
				solWriter.newLine();
				solWriter.write("\\hspace{2em}");
				solWriter.newLine();
				count = 1;
			}
		}
	}

}
