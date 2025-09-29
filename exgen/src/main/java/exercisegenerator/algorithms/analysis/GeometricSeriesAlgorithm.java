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

public class GeometricSeriesAlgorithm implements AlgorithmImplementation<GeometricSeries, GeometricSeries> {

    public static final GeometricSeriesAlgorithm INSTANCE = new GeometricSeriesAlgorithm();

    @Override
    public GeometricSeries apply(final GeometricSeries problem) {
        return problem;
    }

    @Override
    public String commandPrefix() {
        return "GeometricSeries";
    }

    @Override
    public GeometricSeries generateProblem(final Parameters<Flag> options) {
        return new GeometricSeries(
            new BigFraction(Main.RANDOM.nextInt(10001) + 1),
            new BigFraction(Main.RANDOM.nextInt(202) - 101, Main.RANDOM.nextInt(100) + 1)
        );
    }

    @Override
    public String[] generateTestParameters() {
        return new String[] {};
    }

    @Override
    public List<GeometricSeries> parseProblems(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        final String[] split = reader.readLine().split(";");
        if (split.length != 2) {
            throw new IOException("Exactly two parameters are expected!");
        }
        return List.of(
            new GeometricSeries(
                AlgebraAlgorithms.parseRationalNumber(split[0]),
                AlgebraAlgorithms.parseRationalNumber(split[1])
            )
        );
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<GeometricSeries> problems,
        final List<GeometricSeries> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write(
            "Berechnen Sie den Wert der folgenden Reihen oder begr\\\"unden Sie, warum sie jeweils nicht konvergieren."
        );
        Main.newLine(writer);
    }

    @Override
    public void printBeforeSingleProblemInstance(
        final GeometricSeries problem,
        final GeometricSeries solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Berechnen Sie den Wert der Reihe oder begr\\\"unden Sie, warum sie nicht konvergiert:");
        Main.newLine(writer);
    }

    @Override
    public void printProblemInstance(
        final GeometricSeries problem,
        final GeometricSeries solution,
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
        final GeometricSeries problem,
        final GeometricSeries solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        if (problem.converges()) {
            writer.write("\\[");
            writer.write(problem.toString());
            writer.write(" = ");
            writer.write(problem.sumTerm().get());
            writer.write(" = ");
            writer.write(LaTeXUtils.toCoefficient(problem.sumValue().get()));
            writer.write("\\]");
        } else {
            writer.write("Die Reihe divergiert");
            if (problem.base().compareTo(BigFraction.ZERO) > 0) {
                writer.write(" bestimmt gegen $");
                writer.write(problem.factor().compareTo(BigFraction.ZERO) < 0 ? "-" : "");
                writer.write("\\infty$");
            }
            writer.write(", da $\\lim\\limits_{i \\to \\infty} ");
            writer.write(problem.summand());
            writer.write(" \\neq 0$.");
        }
        Main.newLine(writer);
    }

    @Override
    public void printSolutionSpace(
        final GeometricSeries problem,
        final GeometricSeries solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

}
