package exercisegenerator.algorithms;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.algebra.*;
import exercisegenerator.algorithms.analysis.*;
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

public enum Algorithm {

    ARITHMETIC_SUM(
        "arithmeticsum",
        "Arithmetische Summe",
        new String[] {
            "Calculation of an arithmetic sum."
        },
        ArithmeticSumAlgorithm.INSTANCE
    ),

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
            "Breadth first search from a start vertex.",
            "The flag -l specifies how many vertices will be added to the graph for generated instances."
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

    COVERABILITY(
        "coverability",
        "Coverability-Graph",
        new String[] {
            "Computes the coverability graph for a given petri net."
        },
        PetriNetCoverabilityAlgorithm.INSTANCE
    ),

    DFS(
        "dfs",
        "Depth-First-Search",
        new String[] {
            "Depth first search from a start vertex.",
            "The flag -l specifies how many operations should be performed on the tree for generated instances."
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

    DPLL(
        "dpll",
        "DPLL-Algorithmus",
        new String[] {
            "DPLL algorithm to check satisfiability of a set of clauses.",
            "The flag -l specifies how many variables will be used in the generated set."
        },
        exercisegenerator.algorithms.logic.DPLL.INSTANCE
    ),

    FARKAS_PLACE(
        "farkasplace",
        "Farkas-Algorithmus",
        new String[] {
            "Uses the algorithm of Farkas to compute a minimal p-invariant base for a given petri net."
        },
        PetriNetFarkasPlaceInvariantsAlgorithm.INSTANCE
    ),

    FARKAS_TRANSITION(
        "farkastransition",
        "Farkas-Algorithmus",
        new String[] {
            "Uses the algorithm of Farkas to compute a minimal t-invariant base for a given petri net."
        },
        PetriNetFarkasTransitionInvariantsAlgorithm.INSTANCE
    ),

    FLOYD(
        "floyd",
        "Floyd Algorithmus",
        new String[] {
            "Floyd's algorithm to find all shortest paths to all other vertices.",
            "The flag -l specifies how many vertices will be added to the graph for generated instances."
        },
        FloydAlgorithm.INSTANCE
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

    GEOMETRIC_SERIES(
        "geometricseries",
        "Geometrische Reihe",
        new String[] {
            "Calculation of a geometric series."
        },
        GeometricSeriesAlgorithm.INSTANCE
    ),

    GRAHAMS_SCAN(
        "grahamsscan",
        "Graham's Scan",
        new String[] {
            "Calculate the convex hull of a given pointset according to Graham's Scan.",
            "The flag -l specifies the number of points in the pointset for generated instances."
        },
        GrahamsScan.INSTANCE
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

    KOSARAJU_SHARIR(
        "kosarajusharir",
        "Kosaraju-Sharir-Algorithmus",
        new String[] {
            "Detection of strongly connected components using the Kosaraju-Sharir algorithm."
        },
        KosarajuSharirAlgorithm.INSTANCE
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
            "LCS problem solved with dynamic programming.",
            "You can specify the (same) length of the two words with the -l flag."
        },
        LCSAlgorithm.INSTANCE
    ),

    LSE(
        "lse",
        "Linear System of Equations",
        new String[] {
            "Solves a linear system of equations over rational numbers using the Gauß-Jordan-Algorithm.",
            "You can specify the number of variables with the -l flag."
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
        MergeSortWithSplitting.INSTANCE
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

    SELECTIONSORT(
        "selectionsort",
        "Selectionsort",
        new String[] {
            "Perform Selectionsort on an array of integers.",
            "The flag -l specifies the length of the array to sort for generated instances."
        },
        SelectionSort.INSTANCE
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
            "Perform topological sort."
        },
        TopologicSort.INSTANCE
    ),

    UNION_FIND(
        "unionfind",
        "Union-Find",
        new String[] {
            "Operations on a union find data structure.",
            "The flag -l specifies how elements will be used for generated instances."
        },
        UnionFindAlgorithm.INSTANCE
    ),

    WARSHALL(
        "warshall",
        "Warshall Algorithmus",
        new String[] {
            "Warshall's algorithm to find the transitive hull.",
            "The flag -l specifies how many vertices will be added to the graph for generated instances."
        },
        WarshallAlgorithm.INSTANCE
    );

    public static final int DEFAULT_CONTENT_LENGTH = 2;

    public static void assignment(
        final String leftHandSide,
        final List<? extends ItemWithTikZInformation<?>> rightHandSide,
        final String longestLeftHandSide,
        final String relation,
        final int contentLength,
        final boolean exercise,
        final BufferedWriter writer
    ) throws IOException {
        Algorithm.assignmentBeginning(leftHandSide, longestLeftHandSide, relation, writer);
        if (exercise) {
            LaTeXUtils.printEmptyArrayAndReturnLeftmostNodesName(
                rightHandSide.size(),
                Optional.empty(),
                contentLength,
                writer
            );
        } else {
            LaTeXUtils.printListAndReturnLowestLeftmostNodesName(
                rightHandSide,
                Optional.empty(),
                contentLength,
                writer
            );
        }
        Algorithm.assignmentEnd(writer);
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

    public static List<Algorithm> getEnabledAlgorithms() {
        return Arrays.stream(Algorithm.values()).filter(algorithm -> algorithm.enabled).toList();
    }

    public static Set<Flag> getOptionalParameters(final Algorithm algorithm) {
        switch (algorithm) {
        case AVLTREE:
        case BELLMAN_FORD:
        case BFS:
        case BIN_SEARCH_TREE:
        case BUBBLESORT:
        case DFS:
        case DIJKSTRA:
        case DPLL:
        case FLOYD:
        case FORD_FULKERSON:
        case FROM_ASCII:
        case FROM_FLOAT:
        case FROM_HAMMING:
        case FROM_HUFFMAN:
        case FROM_ONES_COMPLEMENT:
        case FROM_TRUTH_TABLE:
        case FROM_TWOS_COMPLEMENT:
        case GRAHAMS_SCAN:
        case HASH_DIV:
        case HASH_DIV_LIN:
        case HASH_DIV_QUAD:
        case HASH_MULT:
        case HASH_MULT_LIN:
        case HASH_MULT_QUAD:
        case HEAPSORT:
        case HEAPSORT_TREE:
        case INSERTIONSORT:
        case KNAPSACK:
        case KRUSKAL:
        case LCS:
        case LSE:
        case MERGESORT:
        case MERGESORT_SPLIT:
        case PRIM:
        case QUICKSORT:
        case RED_BLACK_TREE:
        case SELECTIONSORT:
        case SIMPLEX:
        case TO_ASCII:
        case TO_CNF:
        case TO_DNF:
        case TO_FLOAT:
        case TO_HAMMING:
        case TO_ONES_COMPLEMENT:
        case TO_TRUTH_TABLE:
        case TO_TWOS_COMPLEMENT:
        case TO_VIGENERE:
        case WARSHALL:
            return Set.of(Flag.LENGTH);
        case BTREE:
        case MATRIX_ARITHMETIC:
            return Set.of(Flag.LENGTH, Flag.DEGREE);
        case BUCKETSORT:
        case COUNTINGSORT:
            return Set.of(Flag.LENGTH, Flag.CAPACITY);
        case MATRIX_INVERSION:
            return Set.of(Flag.DEGREE);
        case TO_HUFFMAN:
            return Set.of(Flag.LENGTH, Flag.DEGREE, Flag.OPERATIONS);
        default:
            return Set.of();
        }
    }

    public static Set<Flag> getRequiredParameters(final Algorithm algorithm) {
        switch (algorithm) {
        case FROM_FLOAT:
        case TO_FLOAT:
            return Set.of(Flag.CAPACITY, Flag.DEGREE);
        case FROM_HUFFMAN:
            return Set.of(Flag.OPERATIONS);
        case FROM_ONES_COMPLEMENT:
        case FROM_TWOS_COMPLEMENT:
        case TO_ONES_COMPLEMENT:
        case TO_TWOS_COMPLEMENT:
            return Set.of(Flag.CAPACITY);
        default:
            return Set.of();
        }
    }

    private static void assignmentBeginning(
        final String leftHandSide,
        final String longestLeftHandSide,
        final String relation,
        final BufferedWriter writer
    ) throws IOException {
        final String leftHandSideText = Algorithm.toLeftHandSideText(leftHandSide, relation);
        final String longestLeftHandSideText = Algorithm.toLeftHandSideText(longestLeftHandSide, relation);
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

    private static String toLeftHandSideText(final String leftHandSide, final String relation) {
        return String.format("$%s %s {}$", leftHandSide, relation);
    }

    public final String[] documentation;

    public final boolean enabled;

    public final AlgorithmImplementation<?, ?> implementation;

    public final String longName;

    public final String name;

    private Algorithm(
        final String name,
        final String longName,
        final String[] documentation,
        final AlgorithmImplementation<?, ?> implementation
    ) {
        this(name, longName, documentation, implementation, true);
    }

    private Algorithm(
        final String name,
        final String longName,
        final String[] documentation,
        final AlgorithmImplementation<?, ?> implementation,
        final boolean enabled
    ) {
        this.name = name;
        this.longName = longName;
        this.documentation = documentation;
        this.implementation = implementation;
        this.enabled = enabled;
    }

}