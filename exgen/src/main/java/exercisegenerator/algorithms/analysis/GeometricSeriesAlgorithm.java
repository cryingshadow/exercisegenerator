package exercisegenerator.algorithms.analysis;

import java.io.*;

import org.apache.commons.math3.fraction.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.algebra.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.analysis.*;

public class GeometricSeriesAlgorithm implements AlgorithmImplementation<GeometricSeries, GeometricSeries> {

    public static final GeometricSeriesAlgorithm INSTANCE = new GeometricSeriesAlgorithm();

    private static GeometricSeries generateProblem(final Parameters<Flag> options) throws IOException {
        return new GeometricSeries(
            new BigFraction(Main.RANDOM.nextInt(10001) + 1),
            new BigFraction(Main.RANDOM.nextInt(202) - 101, Main.RANDOM.nextInt(100) + 1)
        );
    }

    private static GeometricSeries parseProblem(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        final String[] split = reader.readLine().split(";");
        if (split.length != 2) {
            throw new IOException("Exactly two parameters are expected!");
        }
        return new GeometricSeries(
            AlgebraAlgorithms.parseRationalNumber(split[0]),
            AlgebraAlgorithms.parseRationalNumber(split[1])
        );
    }

    @Override
    public GeometricSeries apply(final GeometricSeries problem) {
        return problem;
    }

    @Override
    public String[] generateTestParameters() {
        return new String[] {};
    }

    @Override
    public GeometricSeries parseOrGenerateProblem(final Parameters<Flag> options) throws IOException {
        return new ParserAndGenerator<GeometricSeries>(
            GeometricSeriesAlgorithm::parseProblem,
            GeometricSeriesAlgorithm::generateProblem
        ).getResult(options);
    }

    @Override
    public void printExercise(
        final GeometricSeries problem,
        final GeometricSeries solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Berechnen Sie den Wert der Reihe oder begründen Sie, warum sie nicht konvergiert:");
        Main.newLine(writer);
        writer.write("\\[");
        writer.write(problem.toString());
        writer.write("\\]");
        Main.newLine(writer);
        Main.newLine(writer);
    }

    @Override
    public void printSolution(
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
        Main.newLine(writer);
    }

}
