package exercisegenerator.structures.optimization;

import exercisegenerator.io.*;

public class LCSProblem {

    public final String word1;

    public final String word2;

    public LCSProblem(final String word1, final String word2) {
        this.word1 = word1;
        this.word2 = word2;
    }

    public String columnHeading(final int index) {
        return index == 0 ? "" : LaTeXUtils.bold(String.valueOf(this.word2.charAt(index - 1)));
    }

    public String rowHeading(final int index) {
        return index == 0 ? "" : LaTeXUtils.bold(String.valueOf(this.word1.charAt(index - 1)));
    }

}
