package exercisegenerator.algorithms.analysis;

import java.io.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.algebra.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.analysis.*;

public class ArithmeticSumAlgorithm implements AlgorithmImplementation<ArithmeticSum, ArithmeticSum> {

    public static final ArithmeticSumAlgorithm INSTANCE = new ArithmeticSumAlgorithm();

    @Override
    public ArithmeticSum apply(final ArithmeticSum problem) {
        return problem;
    }

    @Override
    public String commandPrefix() {
        return "ArithmeticSum";
    }

    @Override
    public ArithmeticSum generateProblem(final Parameters<Flag> options) {
        return new ArithmeticSum(
            new BigFraction(Main.RANDOM.nextInt(2002) - 1001),
            new BigFraction(Main.RANDOM.nextInt(2002) - 1001, Main.RANDOM.nextInt(100) + 1),
            100 * (Main.RANDOM.nextInt(10) + 1)
        );
    }

    @Override
    public String[] generateTestParameters() {
        return new String[] {};
    }

    @Override
    public List<ArithmeticSum> parseProblems(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        final String[] split = reader.readLine().split(";");
        if (split.length != 3) {
            throw new IOException("Exactly 3 parameters are expected!");
        }
        return List.of(
            new ArithmeticSum(
                AlgebraAlgorithms.parseRationalNumber(split[0]),
                AlgebraAlgorithms.parseRationalNumber(split[1]),
                Integer.parseInt(split[2])
            )
        );
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<ArithmeticSum> problems,
        final List<ArithmeticSum> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Berechnen Sie die folgenden Summen.");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeSingleProblemInstance(
        final ArithmeticSum problem,
        final ArithmeticSum solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Berechnen Sie:");
        Main.newLine(writer);
    }

    @Override
    public void printProblemInstance(
        final ArithmeticSum problem,
        final ArithmeticSum solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("\\[");
        writer.write(problem.toString());
        writer.write("\\]");
        Main.newLine(writer);
    }

    @Override
    public void printSolutionInstance(
        final ArithmeticSum problem,
        final ArithmeticSum solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("\\[");
        writer.write(problem.toString());
        writer.write(" = ");
        writer.write(problem.sumTerm());
        writer.write(" = ");
        writer.write(LaTeXUtils.toCoefficient(problem.sumValue()));
        writer.write("\\]");
        Main.newLine(writer);
    }

    @Override
    public void printSolutionSpace(
        final ArithmeticSum problem,
        final ArithmeticSum solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

}
