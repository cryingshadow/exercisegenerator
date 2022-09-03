package exercisegenerator.io;

import exercisegenerator.*;

/**
 * Option flags for the program arguments.
 * @author cryingshadow
 * @version $Id$
 */
public enum Flag {

    /**
     * The algorithm to apply to the input. Must be specified.
     */
    ALGORITHM(
        "-a",
        "Algorithm",
        "The algorithm (must be specified). Currently, the supported algorithms are: "
        + Main.algorithmNames()
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
        "Used to specify the capacity (a certain limit which should not be exceeded), e.g., the capacity of a "
        + "knapsack. Its use depends on the chosen algorithm.",
        false
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
     * Preprint mode (should preprints be given always, never, or just not in solution sheets?).
     */
    PREPRINT_MODE("-p", "Preprint mode", "TODO", false),

    /**
     * Source file containing the input. Must not be specified together with -i, but one of them must be specified.
     */
    SOURCE("-s", "Source file", "TODO", false),

    /**
     * Target file to store the LaTeX code in. If not specified, the solution is sent to the standard output.
     */
    TARGET("-t", "Target file", "Path to the file where to store the solution text in LaTeX code.", true),

    /**
     * Target file to store the LaTeX code in. If not specified, the solution is sent to the standard output.
     */
    VARIANT(
        "-v",
        "Variant of the algorithm",
        "Triggers the variant of the given algorithm. The following variants of algorithms are available: \n"
        + "    -a [avltree|btree|rbtree] -v 0: Generates examples where nodes get inserted and deleted.\n"
        + "    -a [avltree|btree|rbtree] -v 1: Generates examples where nodes get only inserted.\n",
        true
    ),

    WINDOWS("-w", "Windows line separators", "Forced use of Windows (true) or Unix (false) line separators.", true);

    /**
     * The docu for this flag.
     */
    public final String docu;

    /**
     * Flag indicating whether this option is available for students.
     */
    public final boolean forStudents;

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
     * @param forStudents Flag indicating whether this option is available for students.
     */
    private Flag(final String shortName, final String longName, final String documentation, final boolean forStudents) {
        this.shortName = shortName;
        this.longName = longName;
        this.docu = documentation;
        this.forStudents = forStudents;
    }

}