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

    public AlgorithmInput setOptions(final Parameters options) {
        return new AlgorithmInput(this.exerciseWriter, this.solutionWriter, options);
    }

}
