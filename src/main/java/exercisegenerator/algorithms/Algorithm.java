package exercisegenerator.algorithms;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.util.*;

/**
 * Algorithms supported by the current version. Can be used to switch on/off certain algorithms.
 * @author Thomas Stroeder
 * @version 1.1.1
 */
public enum Algorithm {

    /**
     * Insertion and deletion in AVL-trees with int values.
     */
    AVLTREE(
        "avltree",
        "AVL-Baum",
        new String[]{
            "Insertion and deletion of keys in an AVL-Tree.",
            (
                Main.STUDENT_MODE ?
                    "The flag -l specifies how many operations should be performed on the AVL-Tree." :
                        "TODO"
            )
        },
        TreeAlgorithms::avltree
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
                Main.STUDENT_MODE ?
                    "The flag -l specifies how many operations should be performed on the B-Tree." :
                        "TODO"
            )
        },
        TreeAlgorithms::btree
    ), //TODO extra flag for deletion

    /**
     * Bubblesort on Integer arrays.
     */
    BUBBLESORT(
        "bubblesort",
        "Bubblesort",
        new String[]{
            "Perform Bubblesort on an array of integers.",
            (
                Main.STUDENT_MODE ?
                    "The flag -l specifies the length of the array to sort." :
                        "TODO"
            )
        },
        Sorting::bubblesort
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
                Main.STUDENT_MODE ?
                    "The flag -l specifies how many elements will be added to the hash table." :
                        "Parameters are: m (size of the hashmap)"
            )
        },
        GraphAlgorithms::dijkstra
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
                Main.STUDENT_MODE ?
                    "The flag -l specifies how many nodes will be added to the graph." :
                        "TODO"
            )
        },
        GraphAlgorithms::floyd
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
                Main.STUDENT_MODE ?
                    "The flag -l specifies how many nodes will be added to the flow network in addition to source "
                    + "and sink. Thus, the number of nodes in the network is l + 2." :
                        "TODO"
            )
        },
        GraphAlgorithms::fordFulkerson
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
                Main.STUDENT_MODE ?
                    "The flag -l specifies how many elements will be added to the hash table." :
                        "Parameters are: m (size of the hashmap)"
            )
        },
        Hashing::hashDiv
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
                Main.STUDENT_MODE ?
                    "The flag -l specifies how many elements will be added to the hash table." :
                        "Parameters are: m (size of the hashmap)"
            )
        },
        Hashing::hashDivLin
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
                Main.STUDENT_MODE ?
                    "The flag -l specifies how many elements will be added to the hash table." :
                        "Parameters are: m (size of the hashmap), c1 and c2 (constants for quadratic probing)"
            )
        },
        Hashing::hashDivQuad
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
                Main.STUDENT_MODE ?
                    "The flag -l specifies how many elements will be added to the hash table." :
                        "Parameters are: m (size of the hashmap), c (constant between 0 and 1 for the "
                        + "multiplication method)"
            )
        },
        Hashing::hashMult
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
                Main.STUDENT_MODE ?
                    "The flag -l specifies how many elements will be added to the hash table." :
                        "Parameters are: m (size of the hashmap), c (constant between 0 and 1 for the "
                        + "multiplication method)"
            )
        },
        Hashing::hashMultLin
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
                Main.STUDENT_MODE ?
                    "The flag -l specifies how many elements will be added to the hash table." :
                        "Parameters are: m (size of the hashmap), c (constant between 0 and 1 for the "
                        + "multiplication method), c1 and c2 (constants for quadratic probing)"
            )
        },
        Hashing::hashMultQuad
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
                Main.STUDENT_MODE ?
                    "The flag -l specifies the length of the array to sort." :
                        "TODO"
            )
        },
        Sorting::heapsort
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
                Main.STUDENT_MODE ?
                    "The flag -l specifies the length of the array to sort." :
                        "TODO"
            )
        },
        Sorting::heapsortWithTrees
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
                Main.STUDENT_MODE ?
                    "The flag -l specifies the number of points in the pointset." :
                        "TODO"
            )
        },
        GeometricAlgorithms::hull
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
                Main.STUDENT_MODE ?
                    "The flag -l specifies the length of the array to sort." :
                        "TODO"
            )
        },
        Sorting::insertionsort
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
            Main.STUDENT_MODE ?
               "The flag -l specifies how many items can be chosen to put into the bag." :
                    "TODO"
            )
        },
        DynamicProgramming::knapsack
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
            Main.STUDENT_MODE ?
               "TODO" :
                    "TODO"
            )
        },
        DynamicProgramming::lcs,
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
                Main.STUDENT_MODE ?
                    "The flag -l specifies the length of the array to sort." :
                        "TODO"
            )
        },
        Sorting::mergesort
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
                Main.STUDENT_MODE ?
                    "The flag -l specifies the length of the array to sort." :
                        "TODO"
            )
        },
        Sorting::mergesortSplit
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
                Main.STUDENT_MODE ?
                    "The flag -l specifies how many nodes will be added to the graph." :
                        "TODO"
            )
        },
        GraphAlgorithms::prim
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
                Main.STUDENT_MODE ?
                    "The flag -l specifies the length of the array to sort." :
                        "TODO"
            )
        },
        Sorting::quicksort
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
                Main.STUDENT_MODE ?
                    "The flag -l specifies how many operations should be performed on the Red-Black-Tree." :
                        "TODO"
            )
        },
        TreeAlgorithms::rbtree
    ), //TODO extra flag for deletion

    /**
     * Detection of strongly connected components.
     */
    SCC(
        "scc",
        "Starke Zusammenhangskomponenten",
        new String[]{
            "Detection of strongly connected components.",
            (
                Main.STUDENT_MODE ?
                    "" :
                        "TODO"
            )
        },
        GraphAlgorithms::scc
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
                Main.STUDENT_MODE ?
                    "The flag -l specifies the length of the array to sort." :
                        "TODO"
            )
        },
        Sorting::selectionsort
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
                Main.STUDENT_MODE ?
                    "" :
                        "TODO"
            )
        },
        GraphAlgorithms::sharir
    ),

    TO_ONES_COMPLEMENT(
        "toonescompl",
        "One's Complement (to)",
        new String[] {
            "Transform numbers from decimal to binary representation using the one's complement.",
            "Needs the -l flag to specify the binary length."
        },
        BinaryNumbers::toOnesComplement
    ),

    TO_TWOS_COMPLEMENT(
        "totwoscompl",
        "Two's Complement (to)",
        new String[] {
            "Transform numbers from decimal to binary representation using the two's complement.",
            "Needs the -l flag to specify the binary length."
        },
        BinaryNumbers::toTwosComplement
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
                Main.STUDENT_MODE ?
                    "" :
                        "TODO"
            )
        },
        GraphAlgorithms::topologicsort
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
            Main.STUDENT_MODE ?
                "The flag -l specifies how many nodes will be added to the graph." :
                    "TODO"
            )
        },
        GraphAlgorithms::warshall
    );

    public static final int DEFAULT_CONTENT_LENGTH = 2;

    public static Optional<Algorithm> forName(final String name) {
        for (final Algorithm alg : Algorithm.values()) {
            if (Main.STUDENT_MODE && !alg.enabled) {
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

    public static PreprintMode parsePreprintMode(final Map<Flag, String> options) throws Exception {
        if (options.containsKey(Flag.PREPRINT_MODE)) {
            switch (options.get(Flag.PREPRINT_MODE)) {
                case "always":
                    return PreprintMode.ALWAYS;
                case "solutionSpace":
                    return PreprintMode.SOLUTION_SPACE;
                case "never":
                    return PreprintMode.NEVER;
                default:
                    throw new Exception("Unknown preprint mode!");
            }
        }
        return PreprintMode.ALWAYS;
    }

    public final CheckedConsumer<AlgorithmInput, ? extends Exception> algorithm;

    /**
     * The documentation for this algorithm.
     */
    public final String[] documentation;

    /**
     * Flag indicating whether the algorithm is enabled in student mode.
     */
    public final boolean enabled;

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
        final CheckedConsumer<AlgorithmInput, ? extends Exception> algorithm
    ) {
        this(name, longName, documentation, algorithm, true);
    }

    private Algorithm(
        final String name,
        final String longName,
        final String[] documentation,
        final CheckedConsumer<AlgorithmInput, ? extends Exception> algorithm,
        final boolean enabled
    ) {
        this.name = name;
        this.longName = longName;
        this.documentation = documentation;
        this.algorithm = algorithm;
        this.enabled = enabled;
    }

}