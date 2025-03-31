package exercisegenerator.io;

import exercisegenerator.*;

/**
 * Option flags for the program arguments.
 */
public enum Flag implements clit.Parameter {

    /**
     * The algorithm to apply to the input. Must be specified.
     */
    ALGORITHM(
        "a",
        "Algorithm",
        "The algorithm (must be specified). Currently, the supported algorithms are: "
        + Main.algorithmNames()
        + ". To see detailed help for one of the supported algorithms, call this program with \"-h <alg>\" where "
        + "<alg> is the name of the algorithm."
    ),

    /**
     * The capacity used for several purposes. Its use depends on the algorithm.
     */
    CAPACITY(
        "c",
        "Capacity",
        "Used to specify the capacity (a certain limit which should not be exceeded), e.g., the capacity of a "
        + "knapsack. Its use depends on the chosen algorithm."
    ),

    /**
     * Degree (e.g., of a B-tree).
     */
    DEGREE("d", "Degree", "Used to specify the degree, e.g., of a B-tree. Not relevant for all algorithms."),

    /**
     * Should the resulting files contain all command definitions (standalone) or should they be part of a bigger
     * LaTeX document where the commands are defined somewhere else (embedded). If not specified, the default is
     * standalone.
     */
    EXECUTION_MODE(
        "x",
        "Execution mode",
        "Should the resulting files contain all command definitions (standalone) or should they be part of a bigger "
        + "LaTeX document where the commands are defined somewhere else (embedded). If not specified, the default is "
        + "standalone."
    ),

    /**
     * File to store LaTeX code for the exercise. E.g., for sorting this might be the
     * input array followed by a number of empty arrays. If not set, no exercise will be generated.
     */
    EXERCISE("e", "Exercise file", "Path to the file where to store the exercise text in LaTeX code."),

    /**
     * Input directly specified as a String. Must not be specified together with -s, but one of them must be
     * specified.
     */
    INPUT("i", "Input", "Specify the input for an algorithm from the command line."),

    /**
     * Length used for several purposes. Its use depends on the algorithm.
     */
    LENGTH(
        "l",
        "Length",
        "Used to specify a length, e.g., of an array. Its use depends on the chosen algorithm."
    ),

    NUMBER(
        "n",
        "Number of randomly generated exercises",
        "Specify the number of randomly generated exercises in one document. Only compatible with flags a, e, t, and w."
    ),

    /**
     * File containing operations used to construct a start structure.
     */
    OPERATIONS(
        "o",
        "Operations for start structure",
        "Specify special operations for the algorithm (e.g., construction operations for a binary tree)."
    ),

    /**
     * Preprint mode (should preprints be given always, never, or just not in solution sheets?).
     */
    PREPRINT_MODE(
        "p",
        "Preprint mode",
        "Sets the preprint mode for some algorithms (print the exercise with or without a skeleton for the solution)."
    ),

    /**
     * Source file containing the input. Must not be specified together with -i, but one of them must be specified.
     */
    SOURCE("s", "Source file", "Specify the input for an algorithm in the given file."),

    /**
     * Target file to store the LaTeX code in. If not specified, the solution is sent to the standard output.
     */
    TARGET("t", "Target file", "Path to the file where to store the solution text in LaTeX code."),

    VARIANT(
        "v",
        "Variant of the algorithm",
        "Triggers the variant of the given algorithm. The following variants of algorithms are available: \n"
            + "    -a [avltree|btree|rbtree] -v 0: Generates examples where nodes get inserted and deleted.\n"
            + "    -a [avltree|btree|rbtree] -v 1: Generates examples where nodes get only inserted.\n"
            + "    -a simplex -v 0: Generates examples without integral conditions.\n"
            + "    -a simplex -v 1: Generates examples with integral conditions.\n"
    ),

    WINDOWS("w", "Windows line separators", "Forced use of Windows (true) or Unix (false) line separators.");

    /**
     * The docu for this flag.
     */
    public final String docu;

    /**
     * The name of the option flag in readable form.
     */
    public final String longName;

    /**
     * The name of the option flag as to be given by the user.
     */
    public final String shortName;

    /**
     * Creates an option flag with the specified short and long names.
     * @param shortName The short name.
     * @param longName The long name.
     * @param documentation The documentation.
     */
    private Flag(final String shortName, final String longName, final String documentation) {
        this.shortName = shortName;
        this.longName = longName;
        this.docu = documentation;
    }

    @Override
    public String description() {
        return this.docu;
    }

    @Override
    public String longName() {
        return this.longName;
    }

    @Override
    public String shortName() {
        return this.shortName;
    }

}