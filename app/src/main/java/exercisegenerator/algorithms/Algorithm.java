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
            "The flag -l specifies how many operations should be performed on the AVL-Tree for generated instances."
        },
        TreeAlgorithms::avltree
    ),

    /**
     * Insertion and deletion in B-trees with int values.
     */
    BTREE( //TODO extra flag for deletion
        "btree",
        IntBTree.NAME_OF_BTREE_WITH_DEGREE_2,
        new String[]{
            "Insertion and deletion of keys in a B-tree. The flag -d can be used to set the degree of the B-Tree "
            + "(an integer greater than 1, if not specified, the degree defaults to 2).",
            "The flag -l specifies how many operations should be performed on the B-Tree for generated instances."
        },
        TreeAlgorithms::btree
    ),

    /**
     * Bubblesort on Integer arrays.
     */
    BUBBLESORT(
        "bubblesort",
        "Bubblesort",
        new String[]{
            "Perform Bubblesort on an array of integers.",
            "The flag -l specifies the length of the array to sort for generated instances."
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
            "The flag -l specifies how many elements will be added to the hash table for generated instances."
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
            "The flag -l specifies how many nodes will be added to the graph for generated instances."
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
            "The flag -l specifies how many nodes will be added to the flow network in addition to source "
            + "and sink (for generated instances). Thus, the number of nodes in the network is l + 2."
        },
        GraphAlgorithms::fordFulkerson
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
        BinaryNumbers::fromFloat
    ),

    FROM_HUFFMAN(
        "fromhuff",
        "Huffman decoding",
        new String[] {
            "Decodes a text using the Huffman code.",
            "Needs the -o flag to specify the code book."
        },
        CodingAlgorithms::decodeHuffman
    ),

    FROM_ONES_COMPLEMENT(
        "fromonescompl",
        "One's Complement (from)",
        new String[] {
            "Transform numbers from binary representation using the one's complement to decimal representation.",
            "Needs the -c flag to specify the binary length.",
            "You can specify the number of tasks with the -l flag."
        },
        BinaryNumbers::fromOnesComplement
    ),

    FROM_TWOS_COMPLEMENT(
        "fromtwoscompl",
        "Two's Complement (from)",
        new String[] {
            "Transform numbers from binary representation using the two's complement to decimal representation.",
            "Needs the -c flag to specify the binary length.",
            "You can specify the number of tasks with the -l flag."
        },
        BinaryNumbers::fromTwosComplement
    ),

    FROM_VIGENERE(
        "fromvigenere",
        "Vigenere cipher (from)",
        new String[] {
            "Decode a text with the specified keyword using the Vigenere cipher."
        },
        Cryptography::vigenereDecode
    ),

    /**
     * Linked hashing on Integer arrays with the division method.
     */
    HASH_DIV(
        "hashDivision",
        "Hashing",
        new String[]{
            "Use the division method in combination with linking for hashing into integer arrays.",
            "The flag -l specifies how many elements will be added to the hash table for generated instances.",
            "Parameters for fixed instances are: m (size of the hashmap)"
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
            "The flag -l specifies how many elements will be added to the hash table for generated instances.",
            "Parameters for fixed instances are: m (size of the hashmap)"
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
            "The flag -l specifies how many elements will be added to the hash table for generated instances.",
            "Parameters for fixed instances are: m (size of the hashmap), c1 and c2 (constants for quadratic probing)"
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
            "The flag -l specifies how many elements will be added to the hash table for generated instances.",
            "Parameters for fixed instances are: m (size of the hashmap), c (constant between 0 and 1 for the "
            + "multiplication method)"
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
            "The flag -l specifies how many elements will be added to the hash table for generated instances.",
            "Parameters for fixed instances are: m (size of the hashmap), c (constant between 0 and 1 for the "
            + "multiplication method)"
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
            "The flag -l specifies how many elements will be added to the hash table for generated instances.",
            "Parameters for fixed instances are: m (size of the hashmap), c (constant between 0 and 1 for the "
            + "multiplication method), c1 and c2 (constants for quadratic probing)"
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
            "The flag -l specifies the length of the array to sort for generated instances."
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
            "The flag -l specifies the length of the array to sort for generated instances."
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
            "The flag -l specifies the number of points in the pointset for generated instances."
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
            "The flag -l specifies the length of the array to sort for generated instances."
        },
        Sorting::insertionsort
    ),

    /**
     * Dynamic programming based algorithm to find the maximum value of the items fitting into a knapsack with a
     * certain capacity.
     */
    KNAPSACK(
        "knapsack",
        "Knapsack Problem Solved With Dynamic programming",
        new String[]{
            "Knapsack problem solved with dynamic programming.",
            "The flag -l specifies how many items can be chosen to put into the bag for generated instances."
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
            "LCS problem solved with dynamic programming." // TODO
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
            "The flag -l specifies the length of the array to sort for generated instances."
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
            "The flag -l specifies the length of the array to sort for generated instances."
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
            "The flag -l specifies how many nodes will be added to the graph for generated instances."
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
            "The flag -l specifies the length of the array to sort for generated instances."
        },
        Sorting::quicksort
    ),

    /**
     * Insertion and deletion in Red-Black-trees with int values.
     */
    RBTREE( //TODO extra flag for deletion
        "rbtree",
        "Rot-Schwarz-Baum",
        new String[]{
            "Insertion and deletion of keys in a Red-Black-Tree.",
            "The flag -l specifies how many operations should be performed on the Red-Black-Tree for generated "
            + "instances."
        },
        TreeAlgorithms::rbtree
    ),

    /**
     * Detection of strongly connected components.
     */
    SCC(
        "scc",
        "Starke Zusammenhangskomponenten",
        new String[]{
            "Detection of strongly connected components." // TODO
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
            "The flag -l specifies the length of the array to sort for generated instances."
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
            "Detection of strongly connected components using Sharir's algorithm." // TODO
        },
        GraphAlgorithms::sharir
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
        BinaryNumbers::toFloat
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
        CodingAlgorithms::encodeHuffman
    ),

    TO_ONES_COMPLEMENT(
        "toonescompl",
        "One's Complement (to)",
        new String[] {
            "Transform numbers from decimal to binary representation using the one's complement.",
            "Needs the -c flag to specify the binary length.",
            "You can specify the number of tasks with the -l flag."
        },
        BinaryNumbers::toOnesComplement
    ),

    TO_TWOS_COMPLEMENT(
        "totwoscompl",
        "Two's Complement (to)",
        new String[] {
            "Transform numbers from decimal to binary representation using the two's complement.",
            "Needs the -c flag to specify the binary length.",
            "You can specify the number of tasks with the -l flag."
        },
        BinaryNumbers::toTwosComplement
    ),

    TO_VIGENERE(
        "tovigenere",
        "Vigenere cipher (to)",
        new String[] {
            "Encode a text with the specified keyword using the Vigenere cipher."
        },
        Cryptography::vigenereEncode
    ),

    /**
     * Topological sorting of a graph.
     */
    TOPOLOGICSORT(
        "topologicSort",
        "Topologisches Sortieren",
        new String[]{
            "Perform topological sort." // TODO
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
            "The flag -l specifies how many nodes will be added to the graph for generated instances."
        },
        GraphAlgorithms::warshall
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

    public static PreprintMode parsePreprintMode(final Parameters options) throws Exception {
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

    private static void assignmentBeginning(
        final String leftHandSide,
        final String longestLeftHandSide,
        final BufferedWriter writer
    ) throws IOException {
        final String leftHandSideText = Algorithm.toLeftHandSideText(leftHandSide);
        final String longestLeftHandSideText = Algorithm.toLeftHandSideText(longestLeftHandSide);
        TikZUtils.printMinipageBeginning(TikZUtils.widthOf(longestLeftHandSideText), writer);
        TikZUtils.printFlushRightBeginning(writer);
        writer.write(leftHandSideText);
        Main.newLine(writer);
        TikZUtils.printFlushRightEnd(writer);
        TikZUtils.printMinipageEnd(writer);
        TikZUtils.printMinipageBeginning(TikZUtils.widthOfComplement(longestLeftHandSideText), writer);
        TikZUtils.printTikzBeginning(TikZStyle.ARRAY, writer);
    }

    private static void assignmentEnd(final BufferedWriter writer) throws IOException {
        TikZUtils.printTikzEnd(writer);
        TikZUtils.printMinipageEnd(writer);
    }

    private static void assignmentExercise(
        final String task,
        final int solutionLength,
        final String longestTask,
        final int contentLength,
        final BufferedWriter writer
    ) throws IOException {
        Algorithm.assignmentBeginning(task, longestTask, writer);
        TikZUtils.printEmptyArrayAndReturnLeftmostNodesName(solutionLength, Optional.empty(), contentLength, writer);
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
        TikZUtils.printListAndReturnLeftmostNodesName(solution, Optional.empty(), contentLength, writer);
        Algorithm.assignmentEnd(writer);
    }

    private static String toLeftHandSideText(final String leftHandSide) {
        return String.format("$%s = {}$", leftHandSide);
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