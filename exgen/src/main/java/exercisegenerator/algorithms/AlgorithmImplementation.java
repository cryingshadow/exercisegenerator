package exercisegenerator.algorithms;

import java.io.*;
import java.util.function.*;

import exercisegenerator.io.*;
import exercisegenerator.structures.*;

public interface AlgorithmImplementation<P, S> extends Function<P, S> {

    default void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final P problem = this.parseOrGenerateProblem(input.options);
        final S solution = this.apply(problem);
        this.printExercise(problem, solution, input.options, input.exerciseWriter);
        this.printSolution(problem, solution, input.options, input.solutionWriter);
    }

    String[] generateTestParameters();

    P parseOrGenerateProblem(final Parameters options) throws IOException;

    void printExercise(
        final P problem,
        final S solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException;

    void printSolution(
        final P problem,
        final S solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException;

}
