package exercisegenerator.io;

/**
 * Modes for printing solution space.
 * @author Thomas Stroeder
 * @version 1.0
 */
public enum PreprintMode {

    /**
     * Always print solution space.
     */
    ALWAYS,

    /**
     * Don't print solution space.
     */
    NEVER,

    /**
     * Surround the solution space with the solutionSpace command.
     */
    SOLUTION_SPACE

}
