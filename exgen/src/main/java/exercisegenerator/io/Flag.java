package exercisegenerator.io;

import exercisegenerator.*;

public enum Flag implements clit.Parameter {

    ALGORITHM(
        "a",
        "Algorithm",
        "The algorithm (must be specified). Currently, the supported algorithms are: "
        + Main.algorithmNames()
        + ". To see detailed help for one of the supported algorithms, call this program with \"-h <alg>\" where "
        + "<alg> is the name of the algorithm."
    ),

    BATCH(
        "b",
        "Batch",
        "Batch processing of the specified parameter lists (new batch must always start with -a parameter)."
    ),

    CAPACITY(
        "c",
        "Capacity",
        "Used to specify the capacity (a certain limit which should not be exceeded), e.g., the capacity of a "
        + "knapsack. Its use depends on the chosen algorithm."
    ),

    DEGREE("d", "Degree", "Used to specify the degree, e.g., of a B-tree. Not relevant for all algorithms."),

    EXECUTION_MODE(
        "x",
        "Execution mode",
        "Should the resulting files contain all command definitions (standalone) or should they be part of a bigger "
        + "LaTeX document where the commands are defined somewhere else (embedded). If not specified, the default is "
        + "standalone."
    ),

    EXERCISE("e", "Exercise file", "Path to the file where to store the exercise text in LaTeX code."),

    INPUT("i", "Input", "Specify the input for an algorithm from the command line."),

    KEYVALUE(
        "k",
        "KeyValue",
        "Used to specify additional parameters as comma-separated key-value pairs. Its use depends on the chosen algorithm."
    ),

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

    OPERATIONS(
        "o",
        "Operations for start structure",
        "Specify special operations for the algorithm (e.g., construction operations for a binary tree)."
    ),

    PREPRINT_MODE(
        "p",
        "Preprint mode",
        "Sets the preprint mode for some algorithms (print the exercise with or without a skeleton for the solution)."
    ),

    SOURCE("s", "Source file", "Specify the input for an algorithm in the given file."),

    TARGET("t", "Target file", "Path to the file where to store the solution text in LaTeX code."),

    VARIANT(
        "v",
        "Variant of the algorithm",
        "Triggers the variant of the given algorithm. The following variants of algorithms are available: \n"
            + "    -a [avltree|btree|rbtree] -v 0: Generates examples where nodes get inserted and deleted.\n"
            + "    -a [avltree|btree|rbtree] -v 1: Generates examples where nodes get only inserted.\n"
            + "    -a simplex -v 0: Generates examples without integral conditions.\n"
            + "    -a simplex -v 1: Generates examples with integral conditions.\n"
            + "    -a fordfulkerson -v 0: Parses flow networks as grid graphs.\n"
            + "    -a fordfulkerson -v 1: Parses flow networks as positioned graphs.\n"
    ),

    WINDOWS("w", "Windows line separators", "Forced use of Windows (true) or Unix (false) line separators.");

    public static final int LOAD_ME = 0;

    public final String docu;

    public final String longName;

    public final String shortName;

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