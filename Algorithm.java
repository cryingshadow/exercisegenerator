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
                DSALExercises.STUDENT_MODE ?
                    "The flag -l specifies how many operations should be performed on the AVL-Tree." :
                        "TODO"
            )
        },
        DSALExercisesTest.set(true, "avltree")
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
        DSALExercisesTest.set(true, "btree")
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
                DSALExercises.STUDENT_MODE ?
                    "The flag -l specifies the length of the array to sort." :
                        "TODO"
            )
        },
        DSALExercisesTest.set(true, "bubblesort")
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
        DSALExercisesTest.set(true, "dijkstra")
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
        DSALExercisesTest.set(true, "floyd")
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
        DSALExercisesTest.set(true, "fordfulkerson")
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
        DSALExercisesTest.set(true, "hashDivision")
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
        DSALExercisesTest.set(true, "hashDivisionLinear")
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
        DSALExercisesTest.set(true, "hashDivisionQuadratic")
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
        DSALExercisesTest.set(true, "hashMultiplication")
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
        DSALExercisesTest.set(true, "hashMultiplicationLinear")
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
        DSALExercisesTest.set(true, "hashMultiplicationQuadratic")
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
        DSALExercisesTest.set(true, "heapsort")
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
        DSALExercisesTest.set(true, "heapsortWithTrees")
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
          DSALExercisesTest.set(true, "hull")
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
        DSALExercisesTest.set(true, "insertionsort")
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
        DSALExercisesTest.set(true, "knapsack")
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
        DSALExercisesTest.set(false, "lcs")
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
        DSALExercisesTest.set(true, "mergesort")
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
        DSALExercisesTest.set(true, "mergesortWithSplitting")
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
        DSALExercisesTest.set(true, "prim")
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
        DSALExercisesTest.set(true, "quicksort")
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
        DSALExercisesTest.set(true, "rbtree")
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
                DSALExercises.STUDENT_MODE ?
                    "" :
                        "TODO"
            )
        },
        DSALExercisesTest.set(true, "scc")
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
        DSALExercisesTest.set(true, "selectionsort")
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
        DSALExercisesTest.set(true, "sharir")
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
                    "" :
                        "TODO"
            )
        },
        DSALExercisesTest.set(true, "topologicSort")
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
        DSALExercisesTest.set(true, "warshall")
    );

    /**
     * The documentation for this algorithm.
     */
    public final String[] docu;

    /**
     * Flag indicating whether the algorithm is enabled in student mode.
     */
    public final boolean enabled;

    /**
     * The name of the algorithm for text processing.
     */
    public final String longname;

    /**
     * The name of the algorithm.
     */
    public final String name;
    
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