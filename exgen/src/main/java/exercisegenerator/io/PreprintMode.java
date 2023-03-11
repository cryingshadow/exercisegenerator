package exercisegenerator.io;

import java.io.*;

/**
 * Modes for printing solution space.
 */
public enum PreprintMode {

    /**
     * Always print solution space.
     */
    ALWAYS("always"),

    /**
     * Don't print solution space.
     */
    NEVER("never"),

    /**
     * Surround the solution space with the solutionSpace command.
     */
    SOLUTION_SPACE("solutionSpace");

    public static PreprintMode parsePreprintMode(final Parameters options) throws IOException {
        if (options.containsKey(Flag.PREPRINT_MODE)) {
            final String modeText = options.get(Flag.PREPRINT_MODE);
            for (final PreprintMode mode : PreprintMode.values()) {
                if (mode.text.equals(modeText)) {
                    return mode;
                }
            }
        }
        return ALWAYS;
    }

    public final String text;

    private PreprintMode(final String text) {
        this.text = text;
    }

}
