package exercisegenerator.algorithms;

import java.io.*;
import java.util.*;
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

    String commandPrefix();

    default void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final List<P> problems = this.parseOrGenerateProblems(input.options);
        final List<S> solutions = problems.stream().map(this::apply).toList();
        this.printExercise(problems, solutions, input.options, input.exerciseWriter);
        this.printSolution(problems, solutions, input.options, input.solutionWriter);
    }

    P generateProblem(final Parameters<Flag> options);

    default List<P> generateProblems(final Parameters<Flag> options) {
        final int numberOfProblems = options.getAsIntOrDefault(Flag.NUMBER, 1);
        final List<P> result = new LinkedList<P>();
        for (int i = 0; i < numberOfProblems; i++) {
            result.add(this.generateProblem(options));
        }
        return result;
    }

    String[] generateTestParameters();

    default List<P> parseOrGenerateProblems(final Parameters<Flag> options) throws IOException {
        return new ParserAndGenerator<List<P>>(
            this::parseProblems,
            this::generateProblems
        ).getResult(options);
    }

    List<P> parseProblems(final BufferedReader reader, final Parameters<Flag> options) throws IOException;

    default void printAfterEachOfMultipleProblemInstances(
        final P problem,
        final S solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        if (options.hasKeySetToValue(Flag.EXECUTION_MODE, Main.EMBEDDED_EXAM)) {
            writer.write("}");
            Main.newLine(writer);
        }
    }

    default void printAfterEachOfMultipleSolutionInstances(
        final P problem,
        final S solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        if (options.hasKeySetToValue(Flag.EXECUTION_MODE, Main.EMBEDDED_EXAM)) {
            writer.write("}");
            Main.newLine(writer);
        }
    }

    default void printAfterMultipleProblemInstances(
        final List<P> problems,
        final List<S> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        if (!options.hasKeySetToValue(Flag.EXECUTION_MODE, Main.EMBEDDED_EXAM)) {
            LaTeXUtils.printEnd("enumerate", writer);
        }
    }

    default void printAfterMultipleSolutionInstances(
        final List<P> problems,
        final List<S> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        if (!options.hasKeySetToValue(Flag.EXECUTION_MODE, Main.EMBEDDED_EXAM)) {
            LaTeXUtils.printEnd("enumerate", writer);
        }
    }

    default void printAfterSingleProblemInstance(
        final P problem,
        final S solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

    default void printAfterSingleSolutionInstance(
        final P problem,
        final S solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

    default void printBeforeEachOfMultipleProblemInstances(
        final int number,
        final P problem,
        final S solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        if (options.hasKeySetToValue(Flag.EXECUTION_MODE, Main.EMBEDDED_EXAM)) {
            writer.write("\\newcommand{\\exercise");
            writer.write(this.commandPrefix());
            writer.write(LaTeXUtils.toRomanNumeral(number));
            writer.write("}{%");
            Main.newLine(writer);
        } else {
            writer.write("\\item ");
        }
    }

    default void printBeforeEachOfMultipleSolutionInstances(
        final int number,
        final P problem,
        final S solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        if (options.hasKeySetToValue(Flag.EXECUTION_MODE, Main.EMBEDDED_EXAM)) {
            writer.write("\\newcommand{\\solution");
            writer.write(this.commandPrefix());
            writer.write(LaTeXUtils.toRomanNumeral(number));
            writer.write("}{%");
            Main.newLine(writer);
        } else {
            writer.write("\\item ");
        }
    }

    void printBeforeMultipleProblemInstances(
        final List<P> problems,
        final List<S> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException;

    default void printBeforeMultipleSolutionInstances(
        final List<P> problems,
        final List<S> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

    void printBeforeSingleProblemInstance(
        final P problem,
        final S solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException;

    default void printBeforeSingleSolutionInstance(
        final P problem,
        final S solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

    default void printExercise(
        final List<P> problems,
        final List<S> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        if (problems.size() == 1) {
            final P problem = problems.getFirst();
            final S solution = solutions.getFirst();
            this.printBeforeSingleProblemInstance(problem, solution, options, writer);
            this.printProblemInstance(problem, solution, options, writer);
            this.printAfterSingleProblemInstance(problem, solution, options, writer);
            this.printSolutionSpace(problem, solution, options, writer);
        } else {
            this.printBeforeMultipleProblemInstances(problems, solutions, options, writer);
            this.printStartOfMultipleProblemInstances(problems, solutions, options, writer);
            for (int i = 0; i < problems.size(); i++) {
                final P problem = problems.get(i);
                final S solution = solutions.get(i);
                this.printBeforeEachOfMultipleProblemInstances(i + 1, problem, solution, options, writer);
                this.printProblemInstance(problem, solution, options, writer);
                this.printAfterEachOfMultipleProblemInstances(problem, solution, options, writer);
                this.printSolutionSpace(problem, solution, options, writer);
            }
            this.printAfterMultipleProblemInstances(problems, solutions, options, writer);
        }
        Main.newLine(writer);
    }

    void printProblemInstance(
        final P problem,
        final S solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException;

    default void printSolution(
        final List<P> problems,
        final List<S> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        if (problems.size() == 1) {
            final P problem = problems.getFirst();
            final S solution = solutions.getFirst();
            this.printBeforeSingleSolutionInstance(problem, solution, options, writer);
            this.printSolutionInstance(problem, solution, options, writer);
            this.printAfterSingleSolutionInstance(problem, solution, options, writer);
        } else {
            this.printBeforeMultipleSolutionInstances(problems, solutions, options, writer);
            this.printStartOfMultipleSolutionInstances(problems, solutions, options, writer);
            for (int i = 0; i < problems.size(); i++) {
                final P problem = problems.get(i);
                final S solution = solutions.get(i);
                this.printBeforeEachOfMultipleSolutionInstances(i + 1, problem, solution, options, writer);
                this.printSolutionInstance(problem, solution, options, writer);
                this.printAfterEachOfMultipleSolutionInstances(problem, solution, options, writer);
            }
            this.printAfterMultipleSolutionInstances(problems, solutions, options, writer);
        }
        Main.newLine(writer);
    }

    void printSolutionInstance(
        final P problem,
        final S solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException;

    void printSolutionSpace(
        final P problem,
        final S solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException;

    default void printStartOfMultipleProblemInstances(
        final List<P> problems,
        final List<S> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        if (!options.hasKeySetToValue(Flag.EXECUTION_MODE, Main.EMBEDDED_EXAM)) {
            LaTeXUtils.printBeginning("enumerate", writer);
        }
    }

    default void printStartOfMultipleSolutionInstances(
        final List<P> problems,
        final List<S> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        if (!options.hasKeySetToValue(Flag.EXECUTION_MODE, Main.EMBEDDED_EXAM)) {
            LaTeXUtils.printBeginning("enumerate", writer);
        }
    }

}
