package exercisegenerator.structures;

import java.io.*;

import exercisegenerator.io.*;

public class AlgorithmInput {

    public final BufferedWriter exerciseWriter;

    public final Parameters options;

    public final BufferedWriter solutionWriter;

    public AlgorithmInput(
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter,
        final Parameters options
    ) {
        this.exerciseWriter = exerciseWriter;
        this.solutionWriter = solutionWriter;
        this.options = options;
    }

}
