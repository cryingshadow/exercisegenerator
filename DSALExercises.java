import java.io.*;
import java.util.*;

/**
 * Programm for creating solutions for DSAL exercises.
 * @author cryingshadow
 */
public class DSALExercises {

    /**
     * Limit for random numbers in student mode.
     */
    public static final int NUMBER_LIMIT;

    /**
     * Flag used to turn off some options for the student version.
     */
    public static final boolean STUDENT_MODE;
    
    /**
     * The set of (in student mode only enabled) dynamic programming algorithms.
     */
    private static final Set<String> DYNAMIC_PROGRAMMING_ALGORITHMS;

    /**
     * The set of (in student mode only enabled) graph algorithms.
     */
    private static final Set<String> GEOMETRIC_ALGORITHMS;

    /**
     * The set of (in student mode only enabled) graph algorithms.
     */
    private static final Set<String> GRAPH_ALGORITHMS;

    /**
     * The set of those graph algorithms needing a start node.
     */
    private static final Set<String> GRAPH_ALGORITHMS_WITH_START_NODE;

    /**
     * The set of (in student mode only enabled) grid graph algorithms.
     */
    private static final Set<String> GRID_GRAPH_ALGORITHMS;

    /**
     * The set of (in student mode only enabled) hashing algorithms.
     */
    private static final Set<String> HASHING_ALGORITHMS;
    
    /**
     * The help text displayed when just called with -h. Each entry is separated by a newline.
     */
    private static final String[] HELP;
    
    /**
     * The set of (in student mode only enabled) sorting algorithms.
     */
    private static final Set<String> SORTING_ALGORITHMS;

    /**
     * The set of (in student mode only enabled) tree algorithms.
     */
    private static final Set<String> TREE_ALGORITHMS;

    /**
     * The set of those graph algorithms working on undirected graphs.
     */
    private static final Set<String> UNDIRECTED_GRAPH_ALGORITHMS;

    /**
     * The version of this program.
     */
    private static final String VERSION;
    
    static {
        VERSION = "1.0.1";
        NUMBER_LIMIT = 100;
        STUDENT_MODE = false;
        HASHING_ALGORITHMS = DSALExercises.initHashingAlgorithms();
        SORTING_ALGORITHMS = DSALExercises.initSortingAlgorithms();
        DYNAMIC_PROGRAMMING_ALGORITHMS = DSALExercises.initDynamicProgrammingAlgorithms();
        TREE_ALGORITHMS = DSALExercises.initTreeAlgorithms();
        GRID_GRAPH_ALGORITHMS = DSALExercises.initGridGraphAlgorithms();
        GRAPH_ALGORITHMS = DSALExercises.initGraphAlgorithms();
        UNDIRECTED_GRAPH_ALGORITHMS = DSALExercises.initUndirectedGraphAlgorithms();
        GRAPH_ALGORITHMS_WITH_START_NODE = DSALExercises.initGraphAlgorithmsWithStartNode();
        GEOMETRIC_ALGORITHMS = DSALExercises.initGeometricAlgorithms();
        HELP = DSALExercises.initHelpText();
    }

    /**
     * Reads an input from a source file, executes the specified algorithm on this input, and outputs the solution in 
     * LaTeX code to the specified target file(s).
     * @param args The program arguments as flag/value pairs. The following flags are currently implemented:<br>
     *             -s : Source file containing the input. Must not be specified together with -i, but one of them must 
     *                  be specified.<br>
     *             -i : Input directly specified as a String. Must not be specified together with -s, but one of them 
     *                  must be specified.<br>
     *             -t : Target file to store the LaTeX code in. If not specified, the solution is sent to the standard 
     *                  output.<br>
     *             -p : File to store LaTeX code for a pre-print where to solve an exercise. E.g., for sorting this 
     *                  might be the input array followed by a number of empty arrays. If not set, no pre-print will be 
     *                  generated.<br>
     *             -a : The algorithm to apply to the input. Must be specified.<br>
     *             -l : Additional lines for pre-prints. Defaults to 0 if not set. Ignored if -p is not set.<br>
     *             -d : Degree (e.g., of a B-tree).<br>
     *             -o : File containing operations used to construct a start structure.
     */
    @SuppressWarnings({"resource", "unchecked"})
    public static void main(String[] args) {
        if (args == null || args.length < 1) {
            System.out.println("You need to provide arguments! Type -h for help.");
            return;
        }
        if ("-h".equals(args[0])) {
            if (args.length == 1) {
                for (String text : DSALExercises.HELP) {
                    System.out.println(text);
                }
            } else if (args.length > 2) {
                System.out.println("You can only ask for help on one algorithm at a time!");
            } else {
                String input = args[1];
                for (Algorithm alg : Algorithm.values()) {
                    if (DSALExercises.STUDENT_MODE && !alg.enabled) {
                        continue;
                    }
                    if (alg.name.equals(input)) {
                        for (String text : alg.docu) {
                            System.out.println(text);
                        }
                        break;
                    }
                }
            }
            return;
        }
        if (args.length % 2 != 0) {
            System.out.println("The number of arguments must be even (flag/value pairs)!");
            return;
        }
//        for (int rep = 0; rep < 10000; rep++) {
        Map<Flag, String> options;
        try {
            options = DSALExercises.parseFlags(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
        int rows =
            !DSALExercises.STUDENT_MODE && options.containsKey(Flag.LENGTH) ?
                Integer.parseInt(options.get(Flag.LENGTH)) :
                    0;
        try (
            BufferedWriter solutionWriter =
                new BufferedWriter(
                    options.containsKey(Flag.TARGET) ?
                        new FileWriter(options.get(Flag.TARGET)) :
                            new OutputStreamWriter(System.out)
                );
            BufferedWriter exerciseWriter =
                new BufferedWriter(
                    options.containsKey(Flag.EXERCISE) ?
                        new FileWriter(options.get(Flag.EXERCISE)) :
                            new OutputStreamWriter(System.out)
                );    
        ) {
            if (DSALExercises.STUDENT_MODE) {
                TikZUtils.printLaTeXBeginning(exerciseWriter);
                exerciseWriter.write("{\\large Aufgabe}\\\\[3ex]");
                exerciseWriter.newLine();
                exerciseWriter.newLine();
                TikZUtils.printLaTeXBeginning(solutionWriter);
                solutionWriter.write("{\\large L\\\"osung}\\\\[3ex]");
                solutionWriter.newLine();
                solutionWriter.newLine();
            }
            final Object input = DSALExercises.parseInput(options);
            Integer[] array = null;
            Integer m = 0;
            double[] params = null;
            Pair<double[], Integer[]> in = new Pair<double[], Integer[]>(null, null);
            String anchor = null;
            final String alg = options.get(Flag.ALGORITHM);
            final String hash1 = "F\\\"ugen Sie die folgenden Werte in das unten stehende Array der L\\\"ange ";
            final String hash2 = " unter Verwendung der ";
            final String hash3 = " ein:\\\\[2ex]";
            final String linProb = " mit linearer Sondierung";
            final String quadProb1 = " mit quadratischer Sondierung ($c_1 = ";
            final String quadProb2 = "$, $c_2 = ";
            final String quadProb3 = "$)";
            final String mult1 = "Multiplikationsmethode ($c = ";
            final String mult2 = "$)";
            final String div = "Divisionsmethode";
            final String noProb = " ohne Sondierung (also durch Verkettung)";
            if (Algorithm.SELECTIONSORT.name.equals(alg)) {
                array = (Integer[])input;
                anchor =
                    DSALExercises.sortPreProcessing(
                        array,
                        Algorithm.SELECTIONSORT.longname,
                        "Swap-Operation",
                        "",
                        options,
                        exerciseWriter
                    );
                rows += Sorting.selectionsort(array, solutionWriter);
                DSALExercises.sortPostProcessing(array, rows, anchor, options, exerciseWriter);
            } else if (Algorithm.BUBBLESORT.name.equals(alg)) {
                array = (Integer[])input;
                anchor =
                    DSALExercises.sortPreProcessing(
                        array,
                        Algorithm.BUBBLESORT.longname,
                        "Swap-Operation",
                        "",
                        options,
                        exerciseWriter
                    );
                rows += Sorting.bubblesort(array, solutionWriter);
                DSALExercises.sortPostProcessing(array, rows, anchor, options, exerciseWriter);
            } else if (Algorithm.INSERTIONSORT.name.equals(alg)) {
                array = (Integer[])input;
                anchor =
                    DSALExercises.sortPreProcessing(
                        array,
                        Algorithm.INSERTIONSORT.longname,
                        "Iteration der \\\"au\\ss{}eren Schleife",
                        "",
                        options,
                        exerciseWriter
                    );
                rows += Sorting.insertionsort(array, solutionWriter);
                DSALExercises.sortPostProcessing(array, rows, anchor, options, exerciseWriter);
            } else if (Algorithm.QUICKSORT.name.equals(alg)) {
                array = (Integer[])input;
                anchor =
                    DSALExercises.sortPreProcessing(
                        array,
                        Algorithm.QUICKSORT.longname,
                        "Partition-Operation",
                        " und markieren Sie das jeweils verwendete Pivot-Element",
                        options,
                        exerciseWriter
                    );
                rows += Sorting.quicksort(array, solutionWriter);
                DSALExercises.sortPostProcessing(array, rows, anchor, options, exerciseWriter);
            } else if (Algorithm.MERGESORT.name.equals(alg)) {
                array = (Integer[])input;
                anchor =
                    DSALExercises.sortPreProcessing(
                        array,
                        Algorithm.MERGESORT.longname,
                        "Merge-Operation",
                        "",
                        options,
                        exerciseWriter
                    );
                rows += Sorting.mergesort(array, false, solutionWriter);
                DSALExercises.sortPostProcessing(array, rows, anchor, options, exerciseWriter);
            } else if (Algorithm.MERGESORT_SPLIT.name.equals(alg)) {
                array = (Integer[])input;
                anchor =
                    DSALExercises.sortPreProcessing(
                        array,
                        Algorithm.MERGESORT_SPLIT.longname,
                        "Merge-Operation",
                        "",
                        options,
                        exerciseWriter
                    );
                rows += Sorting.mergesort(array, true, solutionWriter);
                DSALExercises.sortPostProcessing(array, rows, anchor, options, exerciseWriter);
            } else if (Algorithm.HEAPSORT.name.equals(alg)) {
                array = (Integer[])input;
                anchor =
                    DSALExercises.sortPreProcessing(
                        array,
                        Algorithm.HEAPSORT.longname,
                        "Swap-Operation",
                        "",
                        options,
                        exerciseWriter
                    );
                rows += Sorting.heapsort(array, solutionWriter);
                DSALExercises.sortPostProcessing(array, rows, anchor, options, exerciseWriter);
            } else if (Algorithm.HEAPSORT_TREE.name.equals(alg)) {
                array = (Integer[])input;
                anchor =
                    DSALExercises.sortPreProcessing(
                        array,
                        Algorithm.HEAPSORT_TREE.longname,
                        "Swap-Operation",
                        "",
                        options,
                        exerciseWriter
                    );
                rows += Sorting.heapsortWithTrees(array, solutionWriter);
                DSALExercises.sortPostProcessing(array, rows, anchor, options, exerciseWriter);
            } else if (Algorithm.BTREE.name.equals(alg)) {
                IntBTree.btree(
                    new IntBTree(
                        options.containsKey(Flag.DEGREE) ? Integer.parseInt(options.get(Flag.DEGREE)) : 2
                    ),
                    (Deque<Pair<Integer,Boolean>>)input,
                    DSALExercises.parseOperations(options),
                    solutionWriter,
                    options.containsKey(Flag.EXERCISE) ? exerciseWriter : null
                );
            } else if (Algorithm.RBTREE.name.equals(alg)) {
                IntRBTree.rbtree(
                    new IntRBTree(),
                    (Deque<Pair<Integer,Boolean>>)input,
                    DSALExercises.parseOperations(options),
                    solutionWriter,
                    options.containsKey(Flag.EXERCISE) ? exerciseWriter : null
                );
            } else if (Algorithm.AVLTREE.name.equals(alg)) {
                IntAVLTree.avltree(
                    new IntAVLTree(),
                    (Deque<Pair<Integer,Boolean>>)input,
                    DSALExercises.parseOperations(options),
                    solutionWriter,
                    options.containsKey(Flag.EXERCISE) ? exerciseWriter : null
                );
            } else if (Algorithm.SCC.name.equals(alg)) {
                GridGraph.gridGraph(
                    new GridGraph(),
                    (int[][])input,
                    "find_sccs",
                    solutionWriter,
                    options.containsKey(Flag.EXERCISE) ? exerciseWriter : null,
                    true
                );
			} else if (Algorithm.SHARIR.name.equals(alg)) {
                GridGraph.gridGraph(
                    new GridGraph(),
                    (int[][])input,
                    "sharir",
                    solutionWriter,
                    options.containsKey(Flag.EXERCISE) ? exerciseWriter : null,
                    true
                );
			} else if (Algorithm.TOPOLOGICSORT.name.equals(alg)) {
				boolean fail;
				GridGraph graph = new GridGraph();
				int[][] sparseAdjacencyMatrix = new int[graph.numOfNodesInSparseAdjacencyMatrix()][graph.numOfNeighborsInSparseAdjacencyMatrix()];
				sparseAdjacencyMatrix = (int[][])input;
                do {
					try{
						fail = false;
						boolean writeText = true;
						if (options.containsKey(Flag.SOURCE) || !DSALExercises.STUDENT_MODE) {
						    writeText = false;
						}
						GridGraph.gridGraph(
							graph,
							sparseAdjacencyMatrix,
							"topologicSort",
							solutionWriter,
							options.containsKey(Flag.EXERCISE) ? exerciseWriter : null,
							writeText
						);
					} catch (IOException e) {
						System.out.println("Caught cycle-exception.");
						fail = true;
						Random gen = new Random();
						for (int i = 0; i < graph.numOfNodesInSparseAdjacencyMatrix(); i++) {
							for (int j = 0; j < graph.numOfNeighborsInSparseAdjacencyMatrix(); j++) {
								if (graph.isNecessarySparseMatrixEntry(i,j) ) {
									int entry = gen.nextInt(3);
									entry = entry == 2 ? -1 : entry;
									if (graph.isLegalEntryForSparseAdjacencyMatrix(entry)) {
										sparseAdjacencyMatrix[i][j] = entry;
									} else {
										System.out.println("SHOULD NOT HAPPEN!");
									}
								} else {
									sparseAdjacencyMatrix[i][j] = 0;
								}
							}
						}
					}
				} while(fail);
            } else if (Algorithm.HASH_DIV.name.equals(alg)) {
                in = (Pair<double[], Integer[]>)input;
                m = (int)in.x[0];
                params = new double[5];
                params[0] = 1;
                params[1] = 0;
                params[2] = 0;
                params[3] = 0;
                params[4] = 0;
                try {
                    Hashing.hashing(in.y, m, params, !DSALExercises.STUDENT_MODE, solutionWriter);
                } catch (HashException e) {
                    throw new IllegalStateException("Could not hash without probing - this should be impossible...");
                }
                exerciseWriter.write(hash1 + m + hash2 + div + noProb + hash3);
                Hashing.printExercise(in.y, m, false, exerciseWriter);
            } else if (Algorithm.HASH_DIV_LIN.name.equals(alg)) {
                in = (Pair<double[], Integer[]>)input;
                m = (int)in.x[0];
                params = new double[5];
                params[0] = 1;
                params[1] = 1;
                params[2] = 0;
                params[3] = 0;
                params[4] = 0;
                try {
                    Hashing.hashing(in.y, m, params, !DSALExercises.STUDENT_MODE, solutionWriter);
                } catch (HashException e) {
                    throw new IllegalStateException("Could not hash with linear probing - this should not happen...");
                }
                exerciseWriter.write(hash1 + m + hash2 + div + linProb + hash3);
                Hashing.printExercise(in.y, m, true, exerciseWriter);
            } else if (Algorithm.HASH_DIV_QUAD.name.equals(alg)) {
                in = (Pair<double[], Integer[]>)input;
                m = (int)in.x[0];
                double c1 = in.x[2];
                double c2 = in.x[3];
                params = new double[5];
                params[0] = 1;
                params[1] = 2;
                params[2] = 0;
                params[3] = c1;
                params[4] = c2;
                boolean fail;
                do {
                    try {
                        fail = false;
                        Hashing.hashing(in.y, m, params, !DSALExercises.STUDENT_MODE, solutionWriter);
                    } catch (HashException e) {
                        Random gen = new Random();
                        Pair<Double, Double> cs = Hashing.newDivQuadInstance(gen, m, params);
                        c1 = cs.x;
                        c2 = cs.y;
                        fail = true;
                    }
                } while (fail);
                exerciseWriter.write(hash1 + m + hash2 + div + quadProb1 + c1 + quadProb2 + c2 + quadProb3 + hash3);
                Hashing.printExercise(in.y, m, true, exerciseWriter);
            } else if (Algorithm.HASH_MULT.name.equals(alg)) {
                in = (Pair<double[], Integer[]>)input;
                m = (int)in.x[0];
                double c = in.x[1];
                params = new double[5];
                params[0] = 2;
                params[1] = 0;
                params[2] = c;
                params[3] = 0;
                params[4] = 0;
                try {
                    Hashing.hashing(in.y, m, params, !DSALExercises.STUDENT_MODE, solutionWriter);
                } catch (HashException e) {
                    throw new IllegalStateException("Could not hash without probing - this should be impossible...");
                }
                exerciseWriter.write(hash1 + m + hash2 + mult1 + c + mult2 + noProb + hash3);
                Hashing.printExercise(in.y, m, false, exerciseWriter);
            } else if (Algorithm.HASH_MULT_LIN.name.equals(alg)) {
                in = (Pair<double[], Integer[]>)input;
                m = (int)in.x[0];
                double c = in.x[1];
                params = new double[5];
                params[0] = 2;
                params[1] = 1;
                params[2] = c;
                params[3] = 0;
                params[4] = 0;
                try {
                    Hashing.hashing(in.y, m, params, !DSALExercises.STUDENT_MODE, solutionWriter);
                } catch (HashException e) {
                    throw new IllegalStateException("Could not hash with linear probing - this should not happen...");
                }
                exerciseWriter.write(hash1 + m + hash2 + mult1 + c + mult2 + linProb + hash3);
                Hashing.printExercise(in.y, m, true, exerciseWriter);
            } else if (Algorithm.HASH_MULT_QUAD.name.equals(alg)) {
                in = (Pair<double[], Integer[]>)input;
                m = (int)in.x[0];
                double c = in.x[1];
                double c1 = in.x[2];
                double c2 = in.x[3];
                params = new double[5];
                params[0] = 2;
                params[1] = 2;
                params[2] = c;
                params[3] = c1;
                params[4] = c2;
                boolean fail;
                do {
                    try {
                        fail = false;
                        Hashing.hashing(in.y, m, params, !DSALExercises.STUDENT_MODE, solutionWriter);
                    } catch (HashException e) {
                        Random gen = new Random();
                        Pair<Double, Pair<Double, Double>> cs = Hashing.newMultQuadInstance(gen, m, params);
                        c = cs.x;
                        c1 = cs.y.x;
                        c2 = cs.y.y;
                        fail = true;
                    }
                } while (fail);
                exerciseWriter.write(
                    hash1
                    + m
                    + hash2
                    + mult1
                    + c
                    + mult2
                    + quadProb1
                    + c1
                    + quadProb2
                    + c2
                    + quadProb3
                    + hash3
                );
                Hashing.printExercise(in.y, m, true, exerciseWriter);
            } else if (Algorithm.DIJKSTRA.name.equals(alg)) {
                Pair<Graph<String, Integer>, Node<String>> pair = (Pair<Graph<String, Integer>, Node<String>>)input;
                GraphAlgorithms.dijkstra(pair.x, pair.y, new StringNodeComparator(), exerciseWriter, solutionWriter);
            } else if (Algorithm.FORD_FULKERSON.name.equals(alg)) {
                FlowNetworkInput<String, FlowPair> flow = (FlowNetworkInput<String, FlowPair>)input;
                GraphAlgorithms.fordFulkerson(
                    flow.graph,
                    flow.source,
                    flow.sink,
                    flow.multiplier,
                    exerciseWriter,
                    solutionWriter
                );
			} else if (Algorithm.FLOYD.name.equals(alg)) {
                Pair<Graph<String, Integer>, Node<String>> pair = (Pair<Graph<String, Integer>, Node<String>>)input;
                GraphAlgorithms.floyd(pair.x, false, new StringNodeComparator(), exerciseWriter, solutionWriter);
			} else if (Algorithm.WARSHALL.name.equals(alg)) {
                Pair<Graph<String, Integer>, Node<String>> pair = (Pair<Graph<String, Integer>, Node<String>>)input;
                GraphAlgorithms.floyd(pair.x, true, new StringNodeComparator(), exerciseWriter, solutionWriter);
            } else if (Algorithm.PRIM.name.equals(alg)) {
                Pair<Graph<String, Integer>, Node<String>> pair = (Pair<Graph<String, Integer>, Node<String>>)input;
                GraphAlgorithms.prim(pair.x, pair.y, new StringNodeComparator(), exerciseWriter, solutionWriter);
            } else if (Algorithm.HULL.name.equals(alg)) {
                ArrayList<Pair<Double,Double>> pointSet = (ArrayList<Pair<Double,Double>>)input;
                GeometricAlgorithms.printConvexHull(pointSet, exerciseWriter, solutionWriter);
            } else if (Algorithm.LCS.name.equals(alg)) {
                Pair<String,String> tmpInput = (Pair<String,String>) input;
                DynamicProgramming.lcs(
                    tmpInput.x,
                    tmpInput.y,
                    solutionWriter,
                    options.containsKey(Flag.EXERCISE) ? exerciseWriter : null
                );
			} else if (Algorithm.KNAPSACK.name.equals(alg)) {
			    Pair<Pair<Integer[],Integer[]>,Integer> tmpInput = (Pair<Pair<Integer[],Integer[]>,Integer>) input;
			    DynamicProgramming.knapsack(
                    tmpInput.x.x,
                    tmpInput.x.y,
                    tmpInput.y,
                    solutionWriter,
                    options.containsKey(Flag.EXERCISE) ? exerciseWriter : null
                );
            } else {
                System.out.println("Unknown algorithm!");
                return;
            }
            if (DSALExercises.STUDENT_MODE) {
                TikZUtils.printLaTeXEnd(exerciseWriter);
                TikZUtils.printLaTeXEnd(solutionWriter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        }
    }

    /**
     * @return The names of the supported algorithms, separated by commas.
     */
    private static String algorithmNames() {
        StringBuilder res = new StringBuilder();
        boolean first = true;
        for (Algorithm alg : Algorithm.values()) {
            if (DSALExercises.STUDENT_MODE && !alg.enabled) {
                continue;
            }
            if (first) {
                first = false;
            } else {
                res.append(", ");
            }
            res.append(alg.name);
        }
        return res.toString();
    }

    /**
     * @return The set of (in student mode only enabled) dynamic programming algorithms.
     */
    private static Set<String> initDynamicProgrammingAlgorithms() {
        Set<String> res = new LinkedHashSet<String>();
        if (!DSALExercises.STUDENT_MODE || Algorithm.KNAPSACK.enabled) {
            res.add(Algorithm.KNAPSACK.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.LCS.enabled) {
            res.add(Algorithm.LCS.name);
        }
        return res;
    }
    
    /**
     * @return The set of (in student mode only enabled) geometric algorithms.
     */
    private static Set<String> initGeometricAlgorithms() {
        Set<String> res = new LinkedHashSet<String>();
        if (!DSALExercises.STUDENT_MODE || Algorithm.DIJKSTRA.enabled) {
            res.add(Algorithm.HULL.name);
        }
        return res;
    }

    /**
     * @return The set of (in student mode only enabled) graph algorithms.
     */
    private static Set<String> initGraphAlgorithms() {
        Set<String> res = new LinkedHashSet<String>();
        if (!DSALExercises.STUDENT_MODE || Algorithm.DIJKSTRA.enabled) {
            res.add(Algorithm.DIJKSTRA.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.FLOYD.enabled) {
            res.add(Algorithm.FLOYD.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.WARSHALL.enabled) {
            res.add(Algorithm.WARSHALL.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.PRIM.enabled) {
            res.add(Algorithm.PRIM.name);
        }
        return res;
    }

    /**
     * @return The set of (in student mode only enabled) graph algorithms needing a start node.
     */
    private static Set<String> initGraphAlgorithmsWithStartNode() {
        Set<String> res = new LinkedHashSet<String>();
        if (!DSALExercises.STUDENT_MODE || Algorithm.DIJKSTRA.enabled) {
            res.add(Algorithm.DIJKSTRA.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.PRIM.enabled) {
            res.add(Algorithm.PRIM.name);
        }
        return res;
    }

    /**
     * @return The set of (in student mode only enabled) grid graph algorithms.
     */
    private static Set<String> initGridGraphAlgorithms() {
        Set<String> res = new LinkedHashSet<String>();
        if (!DSALExercises.STUDENT_MODE || Algorithm.SCC.enabled) {
            res.add(Algorithm.SCC.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.SHARIR.enabled) {
            res.add(Algorithm.SHARIR.name);
        }
		if (!DSALExercises.STUDENT_MODE || Algorithm.TOPOLOGICSORT.enabled) {
            res.add(Algorithm.TOPOLOGICSORT.name);
        }
        return res;
    }

    /**
     * @return The set of (in student mode only enabled) hashing algorithms.
     */
    private static Set<String> initHashingAlgorithms() {
        Set<String> res = new LinkedHashSet<String>();
        if (!DSALExercises.STUDENT_MODE || Algorithm.HASH_DIV.enabled) {
            res.add(Algorithm.HASH_DIV.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.HASH_DIV_LIN.enabled) {
            res.add(Algorithm.HASH_DIV_LIN.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.HASH_DIV_QUAD.enabled) {
            res.add(Algorithm.HASH_DIV_QUAD.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.HASH_MULT.enabled) {
            res.add(Algorithm.HASH_MULT.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.HASH_MULT_LIN.enabled) {
            res.add(Algorithm.HASH_MULT_LIN.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.HASH_MULT_QUAD.enabled) {
            res.add(Algorithm.HASH_MULT_QUAD.name);
        }
        return res;
    }
    
    /**
     * @return The general help text as String array.
     */
    private static String[] initHelpText() {
        List<String> text = new ArrayList<String>();
        text.add(
            "This is ExerciseCreator version "
            + DSALExercises.VERSION
            + (DSALExercises.STUDENT_MODE ? " (student)." : ".")
        );
        text.add(
            "You can create exercises and solutions automatically using the following flags, where each flag needs to "
            + "be followed by exactly one argument:"
        );
        for (Flag flag : Flag.values()) {
            if (!DSALExercises.STUDENT_MODE || flag.forStudents) {
                text.add("");
                text.add(flag.shortName);
                text.add(flag.docu);
            }
        }
        String[] res = new String[text.size()];
        res = text.toArray(res);
        return res;
    }

    /**
     * @return The set of (in student mode only enabled) sorting algorithms.
     */
    private static Set<String> initSortingAlgorithms() {
        Set<String> res = new LinkedHashSet<String>();
        if (!DSALExercises.STUDENT_MODE || Algorithm.BUBBLESORT.enabled) {
            res.add(Algorithm.BUBBLESORT.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.HEAPSORT.enabled) {
            res.add(Algorithm.HEAPSORT.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.HEAPSORT_TREE.enabled) {
            res.add(Algorithm.HEAPSORT_TREE.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.INSERTIONSORT.enabled) {
            res.add(Algorithm.INSERTIONSORT.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.MERGESORT.enabled) {
            res.add(Algorithm.MERGESORT.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.MERGESORT_SPLIT.enabled) {
            res.add(Algorithm.MERGESORT_SPLIT.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.QUICKSORT.enabled) {
            res.add(Algorithm.QUICKSORT.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.SELECTIONSORT.enabled) {
            res.add(Algorithm.SELECTIONSORT.name);
        }
        return res;
    }

    /**
     * @return The set of (in student mode only enabled) tree algorithms.
     */
    private static Set<String> initTreeAlgorithms() {
        Set<String> res = new LinkedHashSet<String>();
        if (!DSALExercises.STUDENT_MODE || Algorithm.BTREE.enabled) {
            res.add(Algorithm.BTREE.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.RBTREE.enabled) {
            res.add(Algorithm.RBTREE.name);
        }
        if (!DSALExercises.STUDENT_MODE || Algorithm.AVLTREE.enabled) {
            res.add(Algorithm.AVLTREE.name);
        }
        return res;
    }

    /**
     * @return The set of (in student mode only enabled) graph algorithms working on undirected graphs.
     */
    private static Set<String> initUndirectedGraphAlgorithms() {
        Set<String> res = new LinkedHashSet<String>();
        if (!DSALExercises.STUDENT_MODE || Algorithm.PRIM.enabled) {
            res.add(Algorithm.PRIM.name);
        }
        return res;
    }

    /**
     * @param args The program arguments.
     * @return A map from Flags to their values parsed from the program arguments.
     * @throws Exception If the program arguments are not of the desired form.
     */
    private static Map<Flag, String> parseFlags(String[] args) throws Exception {
        Map<Flag, String> res = new LinkedHashMap<Flag, String>();
        outer: for (int i = 0; i < args.length - 1; i += 2) {
            String option = args[i];
            for (Flag flag : Flag.values()) {
                if (DSALExercises.STUDENT_MODE) {
                    if (!flag.forStudents) {
                        continue;
                    }
                }
                if (!flag.shortName.equals(option)) {
                    continue;
                }
                if (res.containsKey(flag)) {
                    throw new Exception(flag.longName + " flag must not be specified more than once!");
                }
                switch (flag) {
                    case SOURCE:
                        if (res.containsKey(Flag.INPUT)) {
                            throw new Exception("Input must not be specified by a file and a string together!");
                        }
                        break;
                    case INPUT:
                        if (res.containsKey(Flag.SOURCE)) {
                            throw new Exception("Input must not be specified by a file and a string together!");
                        }
                        break;
                    default:
                        // do nothing
                }
                res.put(flag, args[i + 1]);
                continue outer;
            }
            throw new Exception("Unknown option specified (" + option + ")!");
        }
        if (!res.containsKey(Flag.ALGORITHM)) {
            throw new Exception("No algorithm specified!");
        }
        if (!DSALExercises.STUDENT_MODE && !res.containsKey(Flag.SOURCE) && !res.containsKey(Flag.INPUT)) {
            throw new Exception("No input specified!");
        }
        if (!res.containsKey(Flag.TARGET) && !res.containsKey(Flag.EXERCISE)) {
            throw new Exception(
                "Cannot output both exercise and solution on stdout! Please specify a file for at least one of them."
            );
        }
        return res;
    }

    /**
     * @param options The option flags.
     * @return The input specified by the options.
     */
    private static Object parseInput(Map<Flag, String> options) {
        String[] nums = null;
        String alg = options.get(Flag.ALGORITHM);
        if (DSALExercises.SORTING_ALGORITHMS.contains(alg)) {
            if (options.containsKey(Flag.SOURCE)) {
                try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.SOURCE)))) {
                    nums = reader.readLine().split(",");
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else if (DSALExercises.STUDENT_MODE) {
                final int length;
                Random gen = new Random();
                if (options.containsKey(Flag.LENGTH)) {
                    length = Integer.parseInt(options.get(Flag.LENGTH));
                } else {
                    length = gen.nextInt(16) + 5;
                }
                Integer[] array = new Integer[length];
                for (int i = 0; i < array.length; i++) {
                    array[i] = gen.nextInt(DSALExercises.NUMBER_LIMIT);
                }
                return array;
            } else {
                nums = options.get(Flag.INPUT).split(",");
            }
            Integer[] array = new Integer[nums.length];
            for (int i = 0; i < array.length; i++) {
                array[i] = Integer.parseInt(nums[i].trim());
            }
            return array;
        } else if (DSALExercises.HASHING_ALGORITHMS.contains(alg)) {
            Pair<double[], Integer[]> input = new Pair<double[], Integer[]>(null,null);
            String[] paramString = null;
            Integer[] array;
            if (options.containsKey(Flag.SOURCE)) {
                try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.SOURCE)))) {
                    paramString = reader.readLine().split(",");
                    nums = reader.readLine().split(",");
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else if (DSALExercises.STUDENT_MODE) {
                final int length;
                Random gen = new Random();
                if (options.containsKey(Flag.LENGTH)) {
                    length = Integer.parseInt(options.get(Flag.LENGTH));
                } else {
                    length = gen.nextInt(16) + 5;
                }
                return Hashing.createRandomInput(gen, length, alg);
            } else {
                nums = options.get(Flag.INPUT).split(",");
                paramString = options.get(Flag.DEGREE).split(",");
            }
            array = new Integer[nums.length];
            for (int i = 0; i < array.length; i++) {
                array[i] = Integer.parseInt(nums[i].trim());
            }
            double[] params = new double[4];
            for (int i = 0; i < paramString.length; ++i) {
                params[i] = Double.parseDouble(paramString[i].trim());
            }
            switch(alg)
            {
                case "hashDivision":
                case "hashDivisionLinear":
                    params[1] = 0;
                    params[2] = 0;
                    params[3] = 0;
                    break;
                case "hashMultiplication":
                case "hashMultiplicationLinear":
                    params[2] = 0;
                    params[3] = 0;
                    break;
                case "hashDivisionQuadratic":
                    params[3] = params[2];
                    params[2] = params[1];
                    params[1] = 0;
                    break;
                default:
            }
            input = new Pair<double[], Integer[]>(params, array);
            return input;
        } else if (DSALExercises.TREE_ALGORITHMS.contains(alg)) {
            if (options.containsKey(Flag.SOURCE)) {
                try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.SOURCE)))) {
                    nums = reader.readLine().split(",");
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else if (DSALExercises.STUDENT_MODE) {
                final int length;
                Random gen = new Random();
                if (options.containsKey(Flag.LENGTH)) {
                    length = Integer.parseInt(options.get(Flag.LENGTH));
                } else {
                    length = gen.nextInt(16) + 5;
                }
                Deque<Pair<Integer, Boolean>> deque = new ArrayDeque<Pair<Integer, Boolean>>();
                List<Integer> in = new ArrayList<Integer>();
                for (int i = 0; i < length; i++) {
                    if (in.isEmpty() || gen.nextInt(3) > 0) {
                        int next = gen.nextInt(DSALExercises.NUMBER_LIMIT);
                        deque.offer(new Pair<Integer, Boolean>(next, true));
                        in.add(next);
                    } else {
                        deque.offer(new Pair<Integer, Boolean>(in.remove(gen.nextInt(in.size())), false));
                    }
                }
                return deque;
            } else {
                nums = options.get(Flag.INPUT).split(",");
            }
            Deque<Pair<Integer, Boolean>> deque = new ArrayDeque<Pair<Integer, Boolean>>();
            for (String num : nums) {
                String trimmed = num.trim();
                if (trimmed.startsWith("~")) {
                    deque.offer(new Pair<Integer, Boolean>(Integer.parseInt(trimmed.substring(1)), false));
                } else {
                    deque.offer(new Pair<Integer, Boolean>(Integer.parseInt(trimmed), true));
                }
            }
            return deque;
        } else if (DSALExercises.GRID_GRAPH_ALGORITHMS.contains(alg)) {
            GridGraph graph = new GridGraph();
            int[][] sparseAdjacencyMatrix =
                new int[graph.numOfNodesInSparseAdjacencyMatrix()][graph.numOfNeighborsInSparseAdjacencyMatrix()];
            String errorMessage =
                new String(
                    "You need to provide "
                    + graph.numOfNodesInSparseAdjacencyMatrix()
                    + " lines and each line has to carry "
                    + graph.numOfNeighborsInSparseAdjacencyMatrix()
                    + " numbers being either 0, -1, 1 or 2, which are separated by ','!\n"
                    + "Example:\n"
                    + "x,0,0,x,x,x\nx,0,0,0,0,x\nx,0,0,0,0,x\nx,x,0,0,0,x\n0,2,0,1,1,0\n0,0,2,1,1,0\n0,0,0,0,0,0\n2,-1,0,x,x,x\n0,1,2,1,-1,1\n"
                    + "0,0,0,0,0,0\n0,x,0,0,0,0\n1,2,0,0,0,1\n0,0,0,0,0,0\n0,0,0,0,0,0\n0,0,x,x,x,x\n0,0,x,x,0,0\n0,0,x,x,0,0\n0,x,x,x,0,0\n\n"
                    + "where x can be anything and will not affect the resulting graph."
                );
            if (options.containsKey(Flag.SOURCE)) {
                try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.SOURCE)))) {
                    String line = null;
                    int rowNum = 0;
                    while ((line = reader.readLine()) != null) {
                        String[] nodes = line.split(",");
                        if (nodes.length != graph.numOfNeighborsInSparseAdjacencyMatrix()) {
                            System.out.println(errorMessage);
                            return null;
                        }
                        for (int i = 0; i < graph.numOfNeighborsInSparseAdjacencyMatrix(); i++) {
                            if (graph.isNecessarySparseMatrixEntry(rowNum,i) ) {
                                int entry = Integer.parseInt(nodes[i].trim());
                                if (graph.isLegalEntryForSparseAdjacencyMatrix(entry)) {
                                    sparseAdjacencyMatrix[rowNum][i] = entry;
                                } else {
                                    System.out.println(errorMessage);
                                    return null;
                                }
                            } else {
                                sparseAdjacencyMatrix[rowNum][i] = 0;
                            }
                        }
                        rowNum++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else if (DSALExercises.STUDENT_MODE) {
                Random gen = new Random();
                for (int i = 0; i < graph.numOfNodesInSparseAdjacencyMatrix(); i++) {
                    for (int j = 0; j < graph.numOfNeighborsInSparseAdjacencyMatrix(); j++) {
                        if (graph.isNecessarySparseMatrixEntry(i,j) ) {
                            int rndNumber = gen.nextInt(18); 
                            int entry = 0;
                            if (rndNumber >= 10 && rndNumber < 13) {
                                entry = -1;
                            } else if (rndNumber >= 13 && rndNumber < 16) {
                                entry = 1;
                            } else if (rndNumber >= 16) {
                                entry = 2;
                            } 
                            if (graph.isLegalEntryForSparseAdjacencyMatrix(entry)) {
                                sparseAdjacencyMatrix[i][j] = entry;
                            } else {
                                System.out.println(errorMessage);
                                return null;
                            }
                        } else {
                            sparseAdjacencyMatrix[i][j] = 0;
                        }
                    }
                }
            } else {
                errorMessage =
                    new String(
                        "You need to provide "
                        + graph.numOfNodesInSparseAdjacencyMatrix()
                        + " sections, which are separated by '|' and each section has to carry "
                        + graph.numOfNeighborsInSparseAdjacencyMatrix()
                        + " numbers being either 0, -1, 1 or 2, which are separated by ','!\n"
                        + "Example:\n"
                        + "x,0,0,x,x,x|x,0,0,0,0,x|x,0,0,0,0,x|x,x,0,0,0,x|0,2,0,1,1,0|0,0,2,1,1,0|0,0,0,0,0,0|2,-1,0,x,x,x|0,1,2,1,-1,1|"
                        + "0,0,0,0,0,0|0,x,0,0,0,0|1,2,0,0,0,1|0,0,0,0,0,0|0,0,0,0,0,0|0,0,x,x,x,x|0,0,x,x,0,0|0,0,x,x,0,0|0,x,x,x,0,0\n\n"
                        + "where x can be anything and will not affect the resulting graph."
                    );
                String[] nodes = options.get(Flag.INPUT).split("|");
                if (nodes.length != graph.numOfNodesInSparseAdjacencyMatrix()) {
                    System.out.println(errorMessage);
                    return null;
                }
                for (int i = 0; i < nodes.length; i++) {
                    String[] neighbors = nodes[i].split(",");
                    if (neighbors.length != graph.numOfNeighborsInSparseAdjacencyMatrix()) {
                        System.out.println(errorMessage);
                        return null;
                    }
                    for (int j = 0; j < neighbors.length; j++) {
                        if (graph.isNecessarySparseMatrixEntry(i,j) ) {
                            int entry = Integer.parseInt(neighbors[j].trim());
                            if (graph.isLegalEntryForSparseAdjacencyMatrix(entry)) {
                                sparseAdjacencyMatrix[i][j] = entry;
                            } else {
                                System.out.println(errorMessage);
                                return null;
                            }
                        } else {
                            sparseAdjacencyMatrix[i][j] = 0;
                        }
                    }
                }
            }
            return sparseAdjacencyMatrix;
        } else if (DSALExercises.GRAPH_ALGORITHMS.contains(alg)) {
            final Graph<String, Integer> graph;
            if (options.containsKey(Flag.SOURCE)) {
                graph = new Graph<String, Integer>();
                try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.SOURCE)))) {
                    graph.setGraphFromInput(reader, new StringLabelParser(), new IntLabelParser());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            } else if (DSALExercises.STUDENT_MODE) {
                Random gen = new Random();
                final int numOfNodes;
                if (options.containsKey(Flag.LENGTH)) {
                    numOfNodes = Integer.parseInt(options.get(Flag.LENGTH));
                } else {
                    numOfNodes = gen.nextInt(16) + 5;
                }
                graph =
                    GraphAlgorithms.createRandomGraph(
                        gen,
                        numOfNodes,
                        DSALExercises.UNDIRECTED_GRAPH_ALGORITHMS.contains(alg)
                    );
            } else {
                graph = new Graph<String, Integer>();
                try (BufferedReader reader = new BufferedReader(new StringReader(options.get(Flag.INPUT)))) {
                    graph.setGraphFromInput(reader, new StringLabelParser(), new IntLabelParser());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            Node<String> node = null;
            if (options.containsKey(Flag.OPERATIONS)) {
                try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.OPERATIONS)))) {
                    Set<Node<String>> nodes = graph.getNodesWithLabel(reader.readLine().trim());
                    if (!nodes.isEmpty()) {
                        node = nodes.iterator().next();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            } else if (DSALExercises.STUDENT_MODE && DSALExercises.GRAPH_ALGORITHMS_WITH_START_NODE.contains(alg)) {
                Set<Node<String>> nodes = graph.getNodesWithLabel("A");
                if (!nodes.isEmpty()) {
                    node = nodes.iterator().next();
                }
            }
            return new Pair<Graph<String, Integer>, Node<String>>(graph, node);
        } else if (DSALExercises.GEOMETRIC_ALGORITHMS.contains(alg)) {
            String line = null;
            ArrayList<Pair<Double,Double>> input = new ArrayList<Pair<Double,Double>>();
            Double[] coordinates = new Double[2];
            if (options.containsKey(Flag.SOURCE)) {
                try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.SOURCE)))) {
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        coordinates[0] = Double.parseDouble(line.split(",")[0].trim());
                        coordinates[1] = Double.parseDouble(line.split(",")[1].trim());
                        Pair<Double,Double> point = new Pair<Double,Double>(coordinates[0], coordinates[1]);
                        input.add(point);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            } else if (DSALExercises.STUDENT_MODE) {
                Random gen = new Random();
                Double rangeMin = 0.0;
                Double rangeMax = 10.0;
                final int numOfPoints;
                if (options.containsKey(Flag.LENGTH)) {
                    numOfPoints = Integer.parseInt(options.get(Flag.LENGTH));
                } else {
                    numOfPoints = gen.nextInt(16) + 5;
                }
                for(int i = 0; i < numOfPoints; ++i) {
                    coordinates[0] = rangeMin + (rangeMax - rangeMin) * gen.nextDouble();
                    coordinates[1] = rangeMin + (rangeMax - rangeMin) * gen.nextDouble();
                    Pair<Double,Double> point = new Pair<Double,Double>(coordinates[0], coordinates[1]);
                    input.add(point);
                }
                
            } else {
                nums = options.get(Flag.INPUT).split(";");
                for(int i = 0; i < nums.length; ++i) {
                    coordinates[0] = Double.parseDouble(nums[i].split(",")[0].trim());
                    coordinates[1] = Double.parseDouble(nums[i].split(",")[1].trim());
                    Pair<Double,Double> point = new Pair<Double,Double>(coordinates[0], coordinates[1]);
                    input.add(point);
                }
            }
            
            return input;
        } else if (Algorithm.FORD_FULKERSON.name.equals(alg)) {
            Graph<String, FlowPair> graph = new Graph<String, FlowPair>();
            if (options.containsKey(Flag.SOURCE)) {
                try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.SOURCE)))) {
                    graph.setGraphFromInput(reader, new StringLabelParser(), new FlowPairLabelParser());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            } else if (DSALExercises.STUDENT_MODE) {
                Random gen = new Random();
                final int numOfNodes;
                if (options.containsKey(Flag.LENGTH)) {
                    numOfNodes = Integer.parseInt(options.get(Flag.LENGTH));
                } else {
                    numOfNodes = gen.nextInt(16) + 3;
                }
                FlowNetworkInput<String, FlowPair> res = new FlowNetworkInput<String, FlowPair>();
                res.graph = GraphAlgorithms.createRandomFlowNetwork(gen, numOfNodes);
                res.source = res.graph.getNodesWithLabel("s").iterator().next();
                res.sink = res.graph.getNodesWithLabel("t").iterator().next();
                res.multiplier = 1.0;
                return res;
            } else {
                try (BufferedReader reader = new BufferedReader(new StringReader(options.get(Flag.INPUT)))) {
                    graph.setGraphFromInput(reader, new StringLabelParser(), new FlowPairLabelParser());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            Node<String> source = null;
            Node<String> sink = null;
            double multiplier = 1.0;
            if (options.containsKey(Flag.OPERATIONS)) {
                try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.OPERATIONS)))) {
                    Set<Node<String>> nodes = graph.getNodesWithLabel(reader.readLine().trim());
                    if (!nodes.isEmpty()) {
                        source = nodes.iterator().next();
                    }
                    nodes = graph.getNodesWithLabel(reader.readLine().trim());
                    if (!nodes.isEmpty()) {
                        sink = nodes.iterator().next();
                    }
                    String mult = reader.readLine();
                    if (mult != null && !"".equals(mult.trim())) {
                        multiplier = Double.parseDouble(mult);
                    }
                } catch (IOException | NumberFormatException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            FlowNetworkInput<String, FlowPair> res = new FlowNetworkInput<String, FlowPair>();
            res.graph = graph;
            res.source = source;
            res.sink = sink;
            res.multiplier = multiplier;
            return res;
        } else if (Algorithm.KNAPSACK.name.equals(alg)) {
            Integer[] weights = null;
            Integer[] values = null;
            Integer capacity = 0;
            if (options.containsKey(Flag.SOURCE)) {
                String errorMessage =
                    "You need to provide two lines of numbers, each number separated only by a ','!"
                    + " The first lines represents the weights of the items and the second line represents"
                    + " the values of the items. Note, that there must be supplied the same number of"
                    + " weights and values and at least one.";
                try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.SOURCE)))) {
                    String line = null;
                    int rowNum = 0;
                    int numOfElements = -1;
                    Integer sumOfWeights = new Integer(0);
                    while ((line = reader.readLine()) != null) {
                        String[] numbers = line.split(",");
                        if (numOfElements == 0 || (numOfElements > 0 && numOfElements != numbers.length)) {
                            System.out.println(errorMessage);
                            return null;
                        }
                        if (rowNum == 0) {
                            weights = new Integer[numbers.length];
                        } else if (rowNum == 1) {
                            values = new Integer[numbers.length];
                        } else {
                            System.out.println(errorMessage);
                            return null;
                        }
                        for (int i = 0; i < numbers.length; i++) {
                            int number = Integer.parseInt(numbers[i].trim());
                            if (rowNum == 0) {
                                weights[i] = number;
                                sumOfWeights += number;
                            } else {
                                values[i] = number;
                            }
                        }
                        rowNum++;
                    }
                    if (options.containsKey(Flag.CAPACITY)) {
                        capacity = Integer.parseInt(options.get(Flag.CAPACITY));
                        if (capacity <= 0) {
                            capacity = sumOfWeights/2;
                        }
                    } else {
                        capacity = sumOfWeights/2;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else if (DSALExercises.STUDENT_MODE) {
                int length;
                Random gen = new Random();
                if (options.containsKey(Flag.LENGTH)) {
                    length = Integer.parseInt(options.get(Flag.LENGTH));
                    if (length <= 0) {
                        length = gen.nextInt(4) + 3;
                    }
                } else {
                    length = gen.nextInt(4) + 3;
                }
                Integer sumOfWeights = new Integer(0);
                weights = new Integer[length];
                for (int i = 0; i < weights.length; i++) {
                    weights[i] = 1 + gen.nextInt(11);
                    sumOfWeights += weights[i];
                }
                values = new Integer[length];
                for (int i = 0; i < values.length; i++) {
                    values[i] = 1 + gen.nextInt(11);
                }
                Integer p = 35 + gen.nextInt(30);
                capacity = (sumOfWeights*p)/100;
            } else {
                String errorMessage =
                    "You need to provide two set of numbers, each number separated only by a ',' and each set divided "
                    + "by a '|'. The first set represents the weights of the items and the second set represents the "
                    + "values of the items. Note, that there must be supplied the same number of weights and values "
                    + "and at least one.";
                String[] sets = options.get(Flag.INPUT).split("|");
                Integer sumOfWeights = new Integer(0);
                if (
                    sets.length != 2
                    || sets[0].length() == 0
                    || sets[1].length() == 0
                    || sets[0].length() != sets[1].length()
                ) {
                    System.out.println(errorMessage);
                    return null;
                }
                String[] numbersA = sets[0].split(",");
                for (int i = 0; i < numbersA.length; i++) {
                    int number = Integer.parseInt(numbersA[i].trim());
                    //FIXME weights might be null here
                    weights[i] = number;
                    sumOfWeights += number;
                }
                String[] numbersB = sets[0].split(",");
                for (int i = 0; i < numbersB.length; i++) {
                    int number = Integer.parseInt(numbersB[i].trim());
                    //FIXME values might be null here
                    values[i] = number;
                }
                if (options.containsKey(Flag.CAPACITY)) {
                    capacity = Integer.parseInt(options.get(Flag.CAPACITY));
                    if (capacity <= 0) {
                        capacity = sumOfWeights/2;
                    }
                } else {
                    capacity = sumOfWeights/2;
                }
            }
            return
                new Pair<Pair<Integer[],Integer[]>,Integer>(
                    new Pair<Integer[],Integer[]>(weights, values),
                    capacity
                );
        }  else if (Algorithm.LCS.name.equals(alg)) {
            String wordA = null;
            String wordB = null;
            if (options.containsKey(Flag.SOURCE)) {
                String errorMessage = "You need to provide two lines each carrying exactly one non-empty word.";
                try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.SOURCE)))) {
                    String line = null;
                    int rowNum = 0;
                    while ((line = reader.readLine()) != null) {
                        if (rowNum == 0) {
                            wordA = new String(line);
                            if (wordA.length() == 0) {
                                System.out.println(errorMessage);
                                return null;
                            }
                        } else if (rowNum == 1) {
                            wordB = new String(line);
                            if (wordB.length() == 0) {
                                System.out.println(errorMessage);
                                return null;
                            }
                        } else {
                            System.out.println(errorMessage);
                            return null;
                        }
                        rowNum++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else if (DSALExercises.STUDENT_MODE) {
                throw new UnsupportedOperationException("Not yet implemented!");
            } else {
                String errorMessage = "You need to provide two non-empty words separated by a '|'.";
                String[] words = options.get(Flag.INPUT).split("|");
                if (words.length != 2 || words[0].length() == 0 || words[1].length() == 0) {
                    System.out.println(errorMessage);
                    return null;
                }
                wordA = new String(words[0]);
                wordB = new String(words[1]);
            }
            return new Pair<String,String>(wordA, wordB);
        } else {
            return null;
        }
    }

    /**
     * @param options The program arguments.
     * @return The operations specified in the operation file or null if no such file is specified in the program 
     *         arguments.
     */
    private static Deque<Pair<Integer, Boolean>> parseOperations(Map<Flag, String> options) {
        if (!options.containsKey(Flag.OPERATIONS)) {
            if (DSALExercises.STUDENT_MODE) {
                Random gen = new Random();
                final int length = gen.nextInt(20);
                Deque<Pair<Integer, Boolean>> deque = new ArrayDeque<Pair<Integer, Boolean>>();
                List<Integer> in = new ArrayList<Integer>();
                for (int i = 0; i < length; i++) {
                    if (in.isEmpty() || gen.nextInt(3) > 0) {
                        int next = gen.nextInt(DSALExercises.NUMBER_LIMIT);
                        deque.offer(new Pair<Integer, Boolean>(next, true));
                        in.add(next);
                    } else {
                        deque.offer(new Pair<Integer, Boolean>(in.remove(gen.nextInt(in.size())), false));
                    }
                }
                return deque;
            }
            return new ArrayDeque<Pair<Integer, Boolean>>();
        }
        String[] nums = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.OPERATIONS)))) {
            nums = reader.readLine().split(",");
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayDeque<Pair<Integer, Boolean>>();
        }
        Deque<Pair<Integer, Boolean>> deque = new ArrayDeque<Pair<Integer, Boolean>>();
        for (String num : nums) {
            String trimmed = num.trim();
            if (trimmed.startsWith("~")) {
                deque.offer(new Pair<Integer, Boolean>(Integer.parseInt(trimmed.substring(1)), false));
            } else {
                deque.offer(new Pair<Integer, Boolean>(Integer.parseInt(trimmed), true));
            }
        }
        return deque;
    }

    /**
     * Prints empty rows for solving the exercise on the sheet directly.
     * @param array The sorted array.
     * @param rows The number of empty rows to be printed in the exercise.
     * @param anchorParam The name of the node used to orient the empty rows.
     * @param options The parsed flags.
     * @param exerciseWriter The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private static void sortPostProcessing(
        Integer[] array,
        int rows,
        String anchorParam,
        Map<Flag, String> options,
        BufferedWriter exerciseWriter
    ) throws IOException {
        String anchor = anchorParam;
        if (options.containsKey(Flag.EXERCISE)) {
            for (int i = 0; i < rows; i++) {
                anchor = ArrayUtils.printEmptyArray(array.length, anchor, exerciseWriter);
            }
            TikZUtils.printTikzEnd(exerciseWriter);
        }
    }

    /**
     * Prints the exercise text to the specified writer.
     * @param array The array to sort.
     * @param alg The name of the sorting algorithm.
     * @param op The operation after which the state of the array is to be given as intermediate result.
     * @param additional Additional instruction on how to write up the solution.
     * @param options The parsed flags.
     * @param exerciseWriter The writer to send the output to.
     * @return The name of the node used to orient empty rows in the exercise text.
     * @throws IOException If some error occurs during output.
     */
    private static String sortPreProcessing(
        Integer[] array,
        String alg,
        String op,
        String additional,
        Map<Flag, String> options,
        BufferedWriter exerciseWriter
    ) throws IOException {
        String anchor = null;
        if (options.containsKey(Flag.EXERCISE)) {
            if (DSALExercises.STUDENT_MODE) {
                exerciseWriter.write("Sortieren Sie das folgende Array mithilfe von " + alg + ".");
                exerciseWriter.newLine();
                exerciseWriter.write("Geben Sie dazu das Array nach jeder " + op + " an" + additional + ".\\\\[2ex]");
                exerciseWriter.newLine();
            }
            TikZUtils.printTikzBeginning(TikZStyle.ARRAY, exerciseWriter);
            anchor = ArrayUtils.printArray(array, null, null, null, exerciseWriter);
        }
        return anchor;
    }

    /**
     * Algorithms supported by the current version. Can be used to switch on/off certain algorithms.
     * @author cryingshadow
     * @version $Id$
     */
    private static enum Algorithm {

        /**
         * Insertion and deletion in AVL-trees with int values.
         */
        AVLTREE(
            "avltree",
            "AVL-Baum",
            new String[]{
                "Insertion and deletion of keys in an AVL-Tree.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies how many operations should be performed on the AVL-Tree." :
                            "TODO"
                )
            },
            true
        ),

        /**
         * Insertion and deletion in B-trees with int values.
         */
        BTREE(
            "btree",
            IntBTree.NAME_OF_BTREE_WITH_DEGREE_2,
            new String[]{
                "Insertion and deletion of keys in a B-tree. The flag -d can be used to set the degree of the B-Tree "
                + "(an integer greater than 1, if not specified, the degree defaults to 2).",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies how many operations should be performed on the B-Tree." :
                            "TODO"
                )
            },
            true
        ),

        /**
         * Bubblesort on Integer arrays.
         */
        BUBBLESORT(
            "bubblesort",
            "Bubblesort",
            new String[]{
                "Perform Bubblesort on an array of integers.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies the length of the array to sort." :
                            "TODO"
                )
            },
            true
        ),

        /**
         * Dijkstra's algorithm to find shortest paths from a single source.
         */
        DIJKSTRA(
            "dijkstra",
            "Dijkstra Algorithmus",
            new String[]{
                "Dijkstra's algorithm to find the shortest paths from a single source to all other nodes.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies how many elements will be added to the hash table." :
                            "Parameters are: m (size of the hashmap)"
                )
            },
            true
        ),
		
		/**
         * Floyd's algorithm to find shortest paths from a single source.
         */
        FLOYD(
			"floyd",
			"Floyd Algorithmus",
			new String[]{
				"Floyd's algorithm to find all shortest paths to all other nodes.",
				(
					DSALExercises.STUDENT_MODE ?
					    "The flag -l specifies how many nodes will be added to the graph." :
							"TODO"
				)
			},
			true
		),
		
		/**
         * Ford-Fulkerson for flow networks.
         */
        FORD_FULKERSON(
            "fordfulkerson",
            "Ford-Fulkerson",
            new String[]{
                "Perform Ford-Fulkerson (Edmonds-Karp) on a flow network.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies how many nodes will be added to the flow network in addition to source "
                        + "and sink. Thus, the number of nodes in the network is l + 2." :
                            "TODO"
                )
            },
            true
        ),
		
		/**
         * Linked hashing on Integer arrays with the division method.
         */
        HASH_DIV(
            "hashDivision",
            "Hashing",
            new String[]{
                "Use the division method in combination with linking for hashing into integer arrays.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies how many elements will be added to the hash table." :
                            "Parameters are: m (size of the hashmap)"
                )
            },
            true
        ),

        /**
         * Hashing on Integer arrays with the division method and linear probing.
         */
        HASH_DIV_LIN(
            "hashDivisionLinear",
            "Hashing",
            new String[]{
                "Use the division method in combination with linear probing for hashing into integer arrays.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies how many elements will be added to the hash table." :
                            "Parameters are: m (size of the hashmap)"
                )
            },
            true
        ),

        /**
         * Hashing on Integer arrays with the division method and quadratic probing.
         */
        HASH_DIV_QUAD(
            "hashDivisionQuadratic",
            "Hashing",
            new String[]{
                "Use the division method in combination with quadratic probing for hashing into integer arrays.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies how many elements will be added to the hash table." :
                            "Parameters are: m (size of the hashmap), c1 and c2 (constants for quadratic probing)"
                )
            },
            true
        ),

        /**
         * Linked hashing on Integer arrays with the multiplication method.
         */
        HASH_MULT(
            "hashMultiplication",
            "Hashing",
            new String[]{
                "Use the multiplication method in combination with linking for hashing into integer arrays.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies how many elements will be added to the hash table." :
                            "Parameters are: m (size of the hashmap), c (constant between 0 and 1 for the "
                            + "multiplication method)"
                )
            },
            true
        ),

        /**
         * Hashing on Integer arrays with the multiplication method and linear probing.
         */
        HASH_MULT_LIN(
            "hashMultiplicationLinear",
            "Hashing",
            new String[]{
                "Use the multiplication method in combination with linear probing for hashing into integer arrays.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies how many elements will be added to the hash table." :
                            "Parameters are: m (size of the hashmap), c (constant between 0 and 1 for the "
                            + "multiplication method)"
                )
            },
            true
        ),

        /**
         * Hashing on Integer arrays with the multiplication method and quadratic probing.
         */
        HASH_MULT_QUAD(
            "hashMultiplicationQuadratic",
            "Hashing",
            new String[]{
                "Use the multiplication method in combination with quadratic probing for hashing into integer arrays.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies how many elements will be added to the hash table." :
                            "Parameters are: m (size of the hashmap), c (constant between 0 and 1 for the "
                            + "multiplication method), c1 and c2 (constants for quadratic probing)"
                )
            },
            true
        ),

        /**
         * Heapsort on Integer arrays.
         */
        HEAPSORT(
            "heapsort",
            "Heapsort",
            new String[]{
                "Perform Heapsort on an array of integers.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies the length of the array to sort." :
                            "TODO"
                )
            },
            true
        ),

        /**
         * Heapsort on Integer arrays where the tree interpretation of the current array is explicitly displayed.
         */
        HEAPSORT_TREE(
            "heapsortWithTrees",
            "Heapsort",
            new String[]{
                "Perform Heapsort on an array of integers. Additionally output the heap interpretation of each array "
                + "in the solution as trees.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies the length of the array to sort." :
                            "TODO"
                )
            },
            true
        ),
        
        /**
         * Convex hull according to Grahams' Scan.
         */
        HULL(
              "hull",
              "Convex Hull",
              new String[]{
                  "Calculate the convex hull of a given pointset according to Grahams' Scan.",
                  (
                        DSALExercises.STUDENT_MODE ?
                   "The flag -l specifies the number of points in the pointset." :
                   "TODO"
                   )
              },
              true
        ),

        /**
         * Insertionsort on Integer arrays.
         */
        INSERTIONSORT(
            "insertionsort",
            "Insertionsort",
            new String[]{
                "Perform Insertionsort on an array of integers.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies the length of the array to sort." :
                            "TODO"
                )
            },
            true
        ),

        /**
         * Dynamic programming based algorithm to find the maximum value of the item fitting into a knapsack with certain capacity.
         */
        KNAPSACK(
			"knapsack",
			"Knapsack Problem Solved With Dynamic programming",
			new String[]{
				"Knapsack problem solved with dynamic programming.",
				(
				DSALExercises.STUDENT_MODE ?
				   "The flag -l specifies how many items can be chosen to put into the bag." :
						"TODO"
				)
			},
			false
		),

        /**
         * Dynamic programming based algorithm to find the longest common subsequence of two strings.
         */
        LCS(
			"lcs",
			"LCS Problem Solved With Dynamic programming",
			new String[]{
				"LCS problem solved with dynamic programming.",
				(
				DSALExercises.STUDENT_MODE ?
				   "TODO" :
						"TODO"
				)
			},
			false
		),

        /**
         * Mergesort on Integer arrays.
         */
        MERGESORT(
            "mergesort",
            "Mergesort",
            new String[]{
                "Perform Mergesort on an array of integers.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies the length of the array to sort." :
                            "TODO"
                )
            },
            true
        ),

        /**
         * Mergesort on Integer arrays where splitting is explicitly displayed.
         */
        MERGESORT_SPLIT(
            "mergesortWithSplitting",
            "Mergesort",
            new String[]{
                "Perform Mergesort on an array of integers. Additionally output the split operations in the solution, "
                + "although they do not change the array content.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies the length of the array to sort." :
                            "TODO"
                )
            },
            true
        ),

        /**
         * Prim's algorithm to find minimum spanning trees from a single source.
         */
        PRIM(
			"prim",
			"Prim Algorithmus",
			new String[]{
				"Prim's algorithm to find the minimum spanning tree.",
				(
					DSALExercises.STUDENT_MODE ?
					    "The flag -l specifies how many nodes will be added to the graph." :
							"TODO"
				)
			},
			true
		),

        /**
         * Quicksort on Integer arrays.
         */
        QUICKSORT(
            "quicksort",
            "Quicksort",
            new String[]{
                "Perform Quicksort on an array of integers.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies the length of the array to sort." :
                            "TODO"
                )
            },
            true
        ),
        
        /**
         * Insertion and deletion in Red-Black-trees with int values.
         */
        RBTREE(
            "rbtree",
            "Rot-Schwarz-Baum",
            new String[]{
                "Insertion and deletion of keys in a Red-Black-Tree.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies how many operations should be performed on the Red-Black-Tree." :
                            "TODO"
                )
            },
            true
        ),
		
		/**
         * Detection of strongly connected components.
         */
        SCC(
            "scc",
            "Starke Zusammenhangskomponenten",
            new String[]{
                "Detection of strongly connected components.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "" :
                            "TODO"
                )
            },
            true
        ),

        /**
         * Selectionsort on Integer arrays.
         */
        SELECTIONSORT(
            "selectionsort",
            "Selectionsort",
            new String[]{
                "Perform Selectionsort on an array of integers.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "The flag -l specifies the length of the array to sort." :
                            "TODO"
                )
            },
            true
        ),

        /**
         * Detection of strongly connected components using Sharir's algorithm.
         */
        SHARIR(
            "sharir",
            "Starke Zusammenhangskomponenten finden mit Sharir's Algorithmus",
            new String[]{
                "Detection of strongly connected components using Sharir's algorithm.",
                (
                    DSALExercises.STUDENT_MODE ?
                        "" :
                            "TODO"
                )
            },
            true
        ),

        /**
		 * Topological sorting of a graph.
		 */
		TOPOLOGICSORT(
			"topologicSort",
			"Topologisches Sortieren",
			new String[]{
				"Perform topological sort.",
				(
					DSALExercises.STUDENT_MODE ?
						"TODO" :
						    "TODO"
				)
			},
			true
		),

        /**
         * Warshall's algorithm to find shortest paths from a single source.
         */
        WARSHALL(
			"warshall",
			"Warshall Algorithmus",
			new String[]{
				"Warshall's algorithm to find the transitive hull.",
				(
				DSALExercises.STUDENT_MODE ?
				    "The flag -l specifies how many nodes will be added to the graph." :
						"TODO"
				)
			},
			true
		);

        /**
         * The documentation for this algorithm.
         */
        private final String[] docu;

        /**
         * Flag indicating whether the algorithm is enabled in student mode.
         */
        private final boolean enabled;

        /**
         * The name of the algorithm for text processing.
         */
        private final String longname;

        /**
         * The name of the algorithm.
         */
        private final String name;

        /**
         * @param n The name of the algorithm.
         * @param l The name of the algorithm for text processing.
         * @param d The documentation for this algorithm.
         * @param e Flag indicating whether the algorithm is enabled in student mode.
         */
        private Algorithm(String n, String l, String[] d, boolean e) {
            this.name = n;
            this.longname = l;
            this.docu = d;
            this.enabled = e;
        }

    }

    /**
     * Option flags for the program arguments.
     * @author cryingshadow
     * @version $Id$
     */
    private static enum Flag {

        /**
         * The algorithm to apply to the input. Must be specified.
         */
        ALGORITHM(
            "-a",
            "Algorithm",
            "The algorithm (must be specified). Currently, the supported algorithms are: "
            + DSALExercises.algorithmNames()
            + ". To see detailed help for one of the supported algorithms, call this program with \"-h <alg>\" where "
            + "<alg> is the name of the algorithm.",
            true
        ),

        /**
         * The capacity used for several purposes. Its use depends on the algorithm.
         */
        CAPACITY(
            "-c",
            "Capacity",
            "Used to specify the capacity (a certain limit which should not be exceeded), e.g., the capacity of a knapsack. Its use depends on the chosen algorithm.",
            true
        ),

        /**
         * Degree (e.g., of a B-tree).
         */
        DEGREE("-d", "Degree", "Used to specify the degree, e.g., of a B-tree. Not relevant for all algorithms.", true),

        /**
         * File to store LaTeX code for the exercise. E.g., for sorting this might be the 
         * input array followed by a number of empty arrays. If not set, no exercise will be generated.
         */
        EXERCISE("-e", "Exercise file", "Path to the file where to store the exercise text in LaTeX code.", true),

        /**
         * Input directly specified as a String. Must not be specified together with -s, but one of them must be 
         * specified.
         */
        INPUT("-i", "Input", "TODO", false),

        /**
         * Length used for several purposes. Its use depends on the algorithm.
         */
        LENGTH(
            "-l",
            "Length",
            "Used to specify a length, e.g., of an array. Its use depends on the chosen algorithm.",
            true
        ),

        /**
         * File containing operations used to construct a start structure.
         */
        OPERATIONS("-o", "Operations for start structure", "TODO", false),

        /**
         * Source file containing the input. Must not be specified together with -i, but one of them must be specified.
         */
        SOURCE("-s", "Source file", "TODO", false),

        /**
         * Target file to store the LaTeX code in. If not specified, the solution is sent to the standard output.
         */
        TARGET("-t", "Target file", "Path to the file where to store the solution text in LaTeX code.", true);

        /**
         * The docu for this flag.
         */
        private final String docu;

        /**
         * Flag indicating whether this option is available for students.
         */
        private final boolean forStudents;

        /**
         * The name of the option flag in readable form.
         */
        private final String longName;

        /**
         * The name of the option flag as to be given by the user.
         */
        private final String shortName;

        /**
         * Creates an option flag with the specified short and long names.
         * @param s The short name.
         * @param l The long name.
         * @param d The documentation.
         * @param a Flag indicating whether this option is available for students.
         */
        private Flag(String s, String l, String d, boolean a) {
            this.shortName = s;
            this.longName = l;
            this.docu = d;
            this.forStudents = a;
        }

    }

}
