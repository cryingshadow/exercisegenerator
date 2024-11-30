package exercisegenerator.algorithms;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.algebra.*;
import exercisegenerator.algorithms.binary.*;
import exercisegenerator.algorithms.coding.*;
import exercisegenerator.algorithms.cryptography.*;
import exercisegenerator.algorithms.geometry.*;
import exercisegenerator.algorithms.graphs.*;
import exercisegenerator.algorithms.hashing.*;
import exercisegenerator.algorithms.logic.*;
import exercisegenerator.algorithms.optimization.*;
import exercisegenerator.algorithms.sorting.*;
import exercisegenerator.algorithms.trees.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;

public enum Algorithm {

    AVLTREE(
        "avltree",
        "AVL-Baum",
        new String[] {
            "Insertion and deletion of keys in an AVL-Tree.",
            "The flag -l specifies how many operations should be performed on the AVL-Tree for generated instances."
        },
        AVLTreeAlgorithm.INSTANCE
    ),

    BELLMAN_FORD(
        "bellmanford",
        "Bellman-Ford-Algorithmus",
        new String[] {
            "The algorithm of Bellman and Ford to find the shortest paths from a single source to all other vertices.",
            "The flag -l specifies how many vertices will be added to the graph for generated instances."
        },
        BellmanFordAlgorithm.INSTANCE
    ),

    BFS(
        "bfs",
        "Breadth-First-Search",
        new String[] {
            "Breadth first search from a start vertex."
        },
        BreadthFirstSearch.INSTANCE
    ),

    BIN_SEARCH_TREE(
        "binsearchtree",
        "Binary Search Tree",
        new String[] {
            "Insertion and deletion in a binary search tree.",
            "The flag -l specifies how many operations should be performed on the tree for generated instances."
        },
        BinarySearchTreeAlgorithm.INSTANCE
    ),

    BTREE(
        "btree",
        "B-Baum",
        new String[] {
            "Insertion and deletion of keys in a B-tree. The flag -d can be used to set the degree of the B-Tree "
            + "(an integer greater than 1, if not specified, the degree defaults to 2).",
            "The flag -l specifies how many operations should be performed on the B-Tree for generated instances."
        },
        BTreeAlgorithm.INSTANCE
    ),

    BUBBLESORT(
        "bubblesort",
        "Bubblesort",
        new String[] {
            "Perform Bubblesort on an array of integers.",
            "The flag -l specifies the length of the array to sort for generated instances."
        },
        BubbleSort.INSTANCE
    ),

    BUCKETSORT(
        "bucketsort",
        "Bucketsort",
        new String[] {
            "Perform Bucketsort on an array of integers.",
            "The flag -l specifies the length of the array to sort for generated instances.",
            "Specify the range of integers and the number of buckets in the form start;end;buckets (both start and end "
            + "inclusive) with the -c flag."
        },
        BucketSort.INSTANCE
    ),

    COUNTINGSORT(
        "countingsort",
        "Countingsort",
        new String[] {
            "Perform Countingsort on an array of integers.",
            "The flag -l specifies the length of the array to sort for generated instances.",
            "Specify the range of integers in the form start;end (both inclusive) with the -c flag."
        },
        CountingSort.INSTANCE
    ),

    DFS(
        "dfs",
        "Depth-First-Search",
        new String[] {
            "Depth first search from a start vertex."
        },
        DepthFirstSearch.INSTANCE
    ),

    DIJKSTRA(
        "dijkstra",
        "Dijkstra Algorithmus",
        new String[] {
            "Dijkstra's algorithm to find the shortest paths from a single source to all other vertices.",
            "The flag -l specifies how many vertices will be added to the graph for generated instances."
        },
        DijkstraAlgorithm.INSTANCE
    ),

    FLOYD(
        "floyd",
        "Floyd Algorithmus",
        new String[] {
            "Floyd's algorithm to find all shortest paths to all other vertices.",
            "The flag -l specifies how many vertices will be added to the graph for generated instances."
        },
        FloydWarshallAlgorithm.INSTANCE
    ),

    FORD_FULKERSON(
        "fordfulkerson",
        "Ford-Fulkerson",
        new String[] {
            "Perform Ford-Fulkerson (Edmonds-Karp) on a flow network.",
            "The flag -l specifies how many vertices will be added to the flow network in addition to source "
            + "and sink (for generated instances). Thus, the number of vertices in the network is l + 2."
        },
        FordFulkersonAlgorithm.INSTANCE
    ),

    FROM_ASCII(
        "fromascii",
        "ASCII (from)",
        new String[] {
            "Transform ASCII character to their binary representation.",
            "You can specify the number of tasks with the -l flag."
        },
        ConversionFromASCII.INSTANCE
    ),

    FROM_FLOAT(
        "fromfloat",
        "Float (from)",
        new String[] {
            "Transform binary float numbers to decimal rational numbers.",
            "Needs the -c flag to specify the length of the mantissa "
            + "and the -d flag to specify the length of the exponent.",
            "You can specify the number of tasks with the -l flag."
        },
        ConversionFromFloat.INSTANCE
    ),

    FROM_HAMMING(
        "fromhamming",
        "Decode a Hamming code",
        new String[] {
            "Decode a Hamming code possibly fixing 1-bit errors.",
            "You can specify the length of the generated code with the -l flag."
        },
        HammingDecoding.INSTANCE
    ),

    FROM_HUFFMAN(
        "fromhuff",
        "Huffman decoding",
        new String[] {
            "Decodes a text using the Huffman code.",
            "Needs the -o flag to specify the code book."
        },
        HuffmanDecoding.INSTANCE
    ),

    FROM_ONES_COMPLEMENT(
        "fromonescompl",
        "One's Complement (from)",
        new String[] {
            "Transform numbers from binary representation using the one's complement to decimal representation.",
            "Needs the -c flag to specify the binary length.",
            "You can specify the number of tasks with the -l flag."
        },
        ConversionFromOnesComplement.INSTANCE
    ),

    FROM_TRUTH_TABLE(
        "fromtruthtable",
        "Truth table to formula",
        new String[] {
            "Compute a formula matching the specified truth table.",
            "You can specify the number of variables with the -l flag."
        },
        ConversionFromTruthTable.INSTANCE
    ),

    FROM_TWOS_COMPLEMENT(
        "fromtwoscompl",
        "Two's Complement (from)",
        new String[] {
            "Transform numbers from binary representation using the two's complement to decimal representation.",
            "Needs the -c flag to specify the binary length.",
            "You can specify the number of tasks with the -l flag."
        },
        ConversionFromTwosComplement.INSTANCE
    ),

    FROM_VIGENERE(
        "fromvigenere",
        "Vigenere cipher (from)",
        new String[] {
            "Decode a text with the specified keyword using the Vigenere cipher."
        },
        VigenereDecryption.INSTANCE
    ),

    HASH_DIV(
        "hashDivision",
        "Hashing",
        new String[] {
            "Use the division method in combination with linking for hashing into integer arrays.",
            "The flag -l specifies how many elements will be added to the hash table for generated instances.",
            "Parameters for fixed instances are: m (size of the hashmap)"
        },
        HashingDivisionOpen.INSTANCE
    ),

    HASH_DIV_LIN(
        "hashDivisionLinear",
        "Hashing",
        new String[] {
            "Use the division method in combination with linear probing for hashing into integer arrays.",
            "The flag -l specifies how many elements will be added to the hash table for generated instances.",
            "Parameters for fixed instances are: m (size of the hashmap)"
        },
        HashingDivisionLinear.INSTANCE
    ),

    HASH_DIV_QUAD(
        "hashDivisionQuadratic",
        "Hashing",
        new String[] {
            "Use the division method in combination with quadratic probing for hashing into integer arrays.",
            "The flag -l specifies how many elements will be added to the hash table for generated instances.",
            "Parameters for fixed instances are: m (size of the hashmap), c1 and c2 (constants for quadratic probing)"
        },
        HashingDivisionQuadratic.INSTANCE
    ),

    HASH_MULT(
        "hashMultiplication",
        "Hashing",
        new String[] {
            "Use the multiplication method in combination with linking for hashing into integer arrays.",
            "The flag -l specifies how many elements will be added to the hash table for generated instances.",
            "Parameters for fixed instances are: m (size of the hashmap), c (constant between 0 and 1 for the "
            + "multiplication method)"
        },
        HashingMultiplicationOpen.INSTANCE
    ),

    HASH_MULT_LIN(
        "hashMultiplicationLinear",
        "Hashing",
        new String[] {
            "Use the multiplication method in combination with linear probing for hashing into integer arrays.",
            "The flag -l specifies how many elements will be added to the hash table for generated instances.",
            "Parameters for fixed instances are: m (size of the hashmap), c (constant between 0 and 1 for the "
            + "multiplication method)"
        },
        HashingMultiplicationLinear.INSTANCE
    ),

    HASH_MULT_QUAD(
        "hashMultiplicationQuadratic",
        "Hashing",
        new String[] {
            "Use the multiplication method in combination with quadratic probing for hashing into integer arrays.",
            "The flag -l specifies how many elements will be added to the hash table for generated instances.",
            "Parameters for fixed instances are: m (size of the hashmap), c (constant between 0 and 1 for the "
            + "multiplication method), c1 and c2 (constants for quadratic probing)"
        },
        HashingMultiplicationQuadratic.INSTANCE
    ),

    HEAPSORT(
        "heapsort",
        "Heapsort",
        new String[] {
            "Perform Heapsort on an array of integers.",
            "The flag -l specifies the length of the array to sort for generated instances."
        },
        HeapSort.INSTANCE
    ),

    HEAPSORT_TREE(
        "heapsortWithTrees",
        "Heapsort",
        new String[] {
            "Perform Heapsort on an array of integers. Additionally output the heap interpretation of each array "
            + "in the solution as trees.",
            "The flag -l specifies the length of the array to sort for generated instances."
        },
        HeapSort.INSTANCE
    ),

    HULL(
        "hull",
        "Convex Hull",
        new String[] {
            "Calculate the convex hull of a given pointset according to Grahams' Scan.",
            "The flag -l specifies the number of points in the pointset for generated instances."
        },
        ConvexHullAlgorithm.INSTANCE
    ),

    INSERTIONSORT(
        "insertionsort",
        "Insertionsort",
        new String[] {
            "Perform Insertionsort on an array of integers.",
            "The flag -l specifies the length of the array to sort for generated instances."
        },
        InsertionSort.INSTANCE
    ),

    KNAPSACK(
        "knapsack",
        "Knapsack Problem Solved With Dynamic programming",
        new String[] {
            "Knapsack problem solved with dynamic programming.",
            "The flag -l specifies how many items can be chosen to put into the bag for generated instances."
        },
        KnapsackAlgorithm.INSTANCE
    ),

    KRUSKAL(
        "kruskal",
        "Kruskal Algorithmus",
        new String[] {
            "Kruskal's algorithm to find the minimum spanning tree.",
            "The flag -l specifies how many vertices will be added to the graph for generated instances."
        },
        KruskalAlgorithm.INSTANCE
    ),

    LCS(
        "lcs",
        "LCS Problem Solved With Dynamic programming",
        new String[] {
            "LCS problem solved with dynamic programming."
        },
        LCSAlgorithm.INSTANCE
    ),

    LSE(
        "lse",
        "Linear System of Equations",
        new String[] {
            "Solves a linear system of equations over rational numbers using the Gauß-Jordan-Algorithm."
        },
        LSEAlgorithm.INSTANCE
    ),

    MATRIX_ARITHMETIC(
        "matrixarithmetic",
        "Matrix Arithmetic",
        new String[] {
            "Addition and multiplication of matrices."
        },
        MatrixArithmeticAlgorithm.INSTANCE
    ),

    MATRIX_INVERSION(
        "matrixinversion",
        "Matrix Inversion",
        new String[] {
            "Inverts a matrix using the Gauß-Jordan-Algorithm."
        },
        MatrixInversionAlgorithm.INSTANCE
    ),

    MERGESORT(
        "mergesort",
        "Mergesort",
        new String[] {
            "Perform Mergesort on an array of integers.",
            "The flag -l specifies the length of the array to sort for generated instances."
        },
        MergeSort.INSTANCE
    ),

    MERGESORT_SPLIT(
        "mergesortWithSplitting",
        "Mergesort",
        new String[] {
            "Perform Mergesort on an array of integers. Additionally output the split operations in the solution, "
            + "although they do not change the array content.",
            "The flag -l specifies the length of the array to sort for generated instances."
        },
        MergeSort.INSTANCE
    ),

    PRIM(
        "prim",
        "Prim Algorithmus",
        new String[] {
            "Prim's algorithm to find the minimum spanning tree.",
            "The flag -l specifies how many vertices will be added to the graph for generated instances."
        },
        PrimAlgorithm.INSTANCE
    ),

    QUICKSORT(
        "quicksort",
        "Quicksort",
        new String[] {
            "Perform Quicksort on an array of integers.",
            "The flag -l specifies the length of the array to sort for generated instances."
        },
        QuickSort.INSTANCE
    ),

    RED_BLACK_TREE(
        "redblacktree",
        "Rot-Schwarz-Baum",
        new String[] {
            "Insertion and deletion of keys in a Red-Black-Tree.",
            "The flag -l specifies how many operations should be performed on the Red-Black-Tree for generated "
            + "instances."
        },
        RedBlackTreeAlgorithm.INSTANCE
    ),

    SCC(
        "scc",
        "Starke Zusammenhangskomponenten",
        new String[] {
            "Detection of strongly connected components." // TODO
        },
        SCCAlgorithm.INSTANCE
    ),

    SELECTIONSORT(
        "selectionsort",
        "Selectionsort",
        new String[] {
            "Perform Selectionsort on an array of integers.",
            "The flag -l specifies the length of the array to sort for generated instances."
        },
        SelectionSort.INSTANCE
    ),

    SHARIR(
        "sharir",
        "Starke Zusammenhangskomponenten finden mit Sharir's Algorithmus",
        new String[] {
            "Detection of strongly connected components using Sharir's algorithm." // TODO
        },
        SharirAlgorithm.INSTANCE
    ),

    SIMPLEX(
        "simplex",
        "Primal simplex algorithm",
        new String[] {
            "Simplex algorithm to solve linear programs in standard maximum form.",
            "The flag -l specifies how many decision variables are used (minimum is 2)."
        },
        SimplexAlgorithm.INSTANCE
    ),

    TO_ASCII(
        "toascii",
        "ASCII (to)",
        new String[] {
            "Transform the binary ASCII representation to the corresponding character.",
            "You can specify the number of tasks with the -l flag."
        },
        ConversionToASCII.INSTANCE
    ),

    TO_CNF(
        "tocnf",
        "Formula to CNF",
        new String[] {
            "Compute an equivalent propositional formula in CNF for a given propositional formula.",
            "You can specify the number of variables with the -l flag."
        },
        ConversionToCNF.INSTANCE
    ),

    TO_DNF(
        "todnf",
        "Formula to DNF",
        new String[] {
            "Compute an equivalent propositional formula in DNF for a given propositional formula.",
            "You can specify the number of variables with the -l flag."
        },
        ConversionToDNF.INSTANCE
    ),

    TO_FLOAT(
        "tofloat",
        "Float (to)",
        new String[] {
            "Transform rational numbers from decimal to binary float representation.",
            "Needs the -c flag to specify the length of the mantissa "
            + "and the -d flag to specify the length of the exponent.",
            "You can specify the number of tasks with the -l flag."
        },
        ConversionToFloat.INSTANCE
    ),

    TO_HAMMING(
        "tohamming",
        "Encode Hamming code",
        new String[] {
            "Encode a binary message to the corresponding Hamming code.",
            "You can specify the length of the generated message with the -l flag."
        },
        HammingEncoding.INSTANCE
    ),

    TO_HUFFMAN(
        "tohuff",
        "Huffman encoding",
        new String[] {
            "Encodes a text using the Huffman code.",
            "You can specify the target alphabet with the -o flag (defaults to 01 if not specified).",
            "You can furthermore specify the number of different letters used for the source text with the -d flag and "
            + "its length with the -l flag."
        },
        HuffmanEncoding.INSTANCE
    ),

    TO_ONES_COMPLEMENT(
        "toonescompl",
        "One's Complement (to)",
        new String[] {
            "Transform numbers from decimal to binary representation using the one's complement.",
            "Needs the -c flag to specify the binary length.",
            "You can specify the number of tasks with the -l flag."
        },
        ConversionToOnesComplement.INSTANCE
    ),

    TO_TRUTH_TABLE(
        "totruthtable",
        "Formula to truth table",
        new String[] {
            "Compute the truth table for a given propositional formula.",
            "You can specify the number of variables with the -l flag."
        },
        ConversionToTruthTable.INSTANCE
    ),

    TO_TWOS_COMPLEMENT(
        "totwoscompl",
        "Two's Complement (to)",
        new String[] {
            "Transform numbers from decimal to binary representation using the two's complement.",
            "Needs the -c flag to specify the binary length.",
            "You can specify the number of tasks with the -l flag."
        },
        ConversionToTwosComplement.INSTANCE
    ),

    TO_VIGENERE(
        "tovigenere",
        "Vigenere cipher (to)",
        new String[] {
            "Encode a text with the specified keyword using the Vigenere cipher."
        },
        VigenereEncryption.INSTANCE
    ),

    TOPOLOGICSORT(
        "topologicSort",
        "Topologisches Sortieren",
        new String[] {
            "Perform topological sort." // TODO
        },
        TopologicSort.INSTANCE
    ),

    WARSHALL(
        "warshall",
        "Warshall Algorithmus",
        new String[] {
            "Warshall's algorithm to find the transitive hull.",
            "The flag -l specifies how many vertices will be added to the graph for generated instances."
        },
        FloydWarshallAlgorithm.INSTANCE
    );

    public static final int DEFAULT_CONTENT_LENGTH = 2;

    public static void assignment(
        final String leftHandSide,
        final List<? extends ItemWithTikZInformation<?>> rightHandSide,
        final String longestLeftHandSide,
        final int contentLength,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        Algorithm.assignmentExercise(
            leftHandSide,
            rightHandSide.size(),
            longestLeftHandSide,
            contentLength,
            exerciseWriter
        );
        Algorithm.assignmentSolution(
            leftHandSide,
            rightHandSide,
            longestLeftHandSide,
            contentLength,
            solutionWriter
        );
    }

    public static Optional<Algorithm> forName(final String name) {
        for (final Algorithm alg : Algorithm.values()) {
            if (!alg.enabled) {
                continue;
            }
            if (alg.name.equals(name)) {
                return Optional.of(alg);
            }
        }
        return Optional.empty();
    }

    public static Optional<BufferedWriter> getOptionalSpaceWriter(final AlgorithmInput input) {
        return input.options.containsKey(Flag.EXERCISE) ? Optional.of(input.exerciseWriter) : Optional.empty();
    }

    private static void assignmentBeginning(
        final String leftHandSide,
        final String longestLeftHandSide,
        final BufferedWriter writer
    ) throws IOException {
        final String leftHandSideText = Algorithm.toLeftHandSideText(leftHandSide);
        final String longestLeftHandSideText = Algorithm.toLeftHandSideText(longestLeftHandSide);
        LaTeXUtils.printMinipageBeginning(LaTeXUtils.widthOf(longestLeftHandSideText), writer);
        LaTeXUtils.printFlushRightBeginning(writer);
        writer.write(leftHandSideText);
        Main.newLine(writer);
        LaTeXUtils.printFlushRightEnd(writer);
        LaTeXUtils.printMinipageEnd(writer);
        LaTeXUtils.printMinipageBeginning(LaTeXUtils.widthOfComplement(longestLeftHandSideText), writer);
        LaTeXUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
    }

    private static void assignmentEnd(final BufferedWriter writer) throws IOException {
        LaTeXUtils.printTikzEnd(writer);
        LaTeXUtils.printMinipageEnd(writer);
    }

    private static void assignmentExercise(
        final String task,
        final int solutionLength,
        final String longestTask,
        final int contentLength,
        final BufferedWriter writer
    ) throws IOException {
        Algorithm.assignmentBeginning(task, longestTask, writer);
        LaTeXUtils.printEmptyArrayAndReturnLeftmostNodesName(solutionLength, Optional.empty(), contentLength, writer);
        Algorithm.assignmentEnd(writer);
    }

    private static void assignmentSolution(
        final String task,
        final List<? extends ItemWithTikZInformation<?>> solution,
        final String longestTask,
        final int contentLength,
        final BufferedWriter writer
    ) throws IOException {
        Algorithm.assignmentBeginning(task, longestTask, writer);
        LaTeXUtils.printListAndReturnLowestLeftmostNodesName(solution, Optional.empty(), contentLength, writer);
        Algorithm.assignmentEnd(writer);
    }

    private static String toLeftHandSideText(final String leftHandSide) {
        return String.format("$%s = {}$", leftHandSide);
    }

    /**
     * The documentation for this algorithm.
     */
    public final String[] documentation;

    /**
     * Flag indicating whether the algorithm is enabled in student mode.
     */
    public final boolean enabled;

    public final AlgorithmImplementation implementation;

    /**
     * The name of the algorithm for text processing.
     */
    public final String longName;

    /**
     * The name of the algorithm.
     */
    public final String name;

    /**
     * Creates an enabled Algorithm.
     */
    private Algorithm(
        final String name,
        final String longName,
        final String[] documentation,
        final AlgorithmImplementation implementation
    ) {
        this(name, longName, documentation, implementation, true);
    }

    private Algorithm(
        final String name,
        final String longName,
        final String[] documentation,
        final AlgorithmImplementation implementation,
        final boolean enabled
    ) {
        this.name = name;
        this.longName = longName;
        this.documentation = documentation;
        this.implementation = implementation;
        this.enabled = enabled;
    }

}