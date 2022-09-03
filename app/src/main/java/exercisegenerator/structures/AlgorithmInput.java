package exercisegenerator.structures;

import java.io.*;
import java.util.*;

import exercisegenerator.io.*;

public class AlgorithmInput {

    public final BufferedWriter exerciseWriter;

    public final Map<Flag, String> options;

    public final BufferedWriter solutionWriter;

    public AlgorithmInput(
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter,
        final Map<Flag, String> options
    ) {
        this.exerciseWriter = exerciseWriter;
        this.solutionWriter = solutionWriter;
        this.options = options;
    }

}
