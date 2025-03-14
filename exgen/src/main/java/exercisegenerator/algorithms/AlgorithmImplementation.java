package exercisegenerator.algorithms;

import java.io.*;
import java.util.function.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;

public interface AlgorithmImplementation<P, S> extends Function<P, S> {

    static int parseOrGenerateLength(final int lowest, final int highest, final Parameters<Flag> options) {
        if (options.containsKey(Flag.LENGTH)) {
            return Integer.parseInt(options.get(Flag.LENGTH));
        }
        return Main.RANDOM.nextInt(highest - lowest + 1) + lowest;
    }

    default void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final P problem = this.parseOrGenerateProblem(input.options);
        final S solution = this.apply(problem);
        this.printExercise(problem, solution, input.options, input.exerciseWriter);
        this.printSolution(problem, solution, input.options, input.solutionWriter);
    }

    String[] generateTestParameters();

    P parseOrGenerateProblem(final Parameters<Flag> options) throws IOException;

    void printExercise(
        final P problem,
        final S solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException;

    void printSolution(
        final P problem,
        final S solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException;

}
