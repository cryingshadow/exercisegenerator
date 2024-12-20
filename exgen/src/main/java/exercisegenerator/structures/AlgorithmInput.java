package exercisegenerator.structures;

import java.io.*;

import clit.*;
import exercisegenerator.io.Flag;

public class AlgorithmInput {

    public final BufferedWriter exerciseWriter;

    public final Parameters<Flag> options;

    public final BufferedWriter solutionWriter;

    public AlgorithmInput(
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter,
        final Parameters<Flag> options
    ) {
        this.exerciseWriter = exerciseWriter;
        this.solutionWriter = solutionWriter;
        this.options = options;
    }

    public AlgorithmInput setOptions(final Parameters<Flag> options) {
        return new AlgorithmInput(this.exerciseWriter, this.solutionWriter, options);
    }

}
