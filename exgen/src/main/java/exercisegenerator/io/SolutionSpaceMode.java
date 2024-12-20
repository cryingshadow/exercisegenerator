package exercisegenerator.io;

import java.io.*;

import clit.*;

public enum SolutionSpaceMode {

    ALWAYS("always"),

    NEVER("never"),

    SOLUTION_SPACE("solutionSpace");

    public static SolutionSpaceMode parsePreprintMode(final Parameters<Flag> options) throws IOException {
        if (options.containsKey(Flag.PREPRINT_MODE)) {
            final String modeText = options.get(Flag.PREPRINT_MODE);
            for (final SolutionSpaceMode mode : SolutionSpaceMode.values()) {
                if (mode.text.equals(modeText)) {
                    return mode;
                }
            }
        }
        return ALWAYS;
    }

    public final String text;

    private SolutionSpaceMode(final String text) {
        this.text = text;
    }

}
