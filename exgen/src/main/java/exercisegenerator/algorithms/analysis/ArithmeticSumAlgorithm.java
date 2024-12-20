package exercisegenerator.algorithms.analysis;

import java.io.*;

import org.apache.commons.math3.fraction.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.algebra.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.analysis.*;

public class ArithmeticSumAlgorithm implements AlgorithmImplementation<ArithmeticSum, ArithmeticSum> {

    public static final ArithmeticSumAlgorithm INSTANCE = new ArithmeticSumAlgorithm();

    private static ArithmeticSum generateProblem(final Parameters<Flag> options) throws IOException {
        return new ArithmeticSum(
            new BigFraction(Main.RANDOM.nextInt(2002) - 1001),
            new BigFraction(Main.RANDOM.nextInt(2002) - 1001, Main.RANDOM.nextInt(100) + 1),
            100 * (Main.RANDOM.nextInt(10) + 1)
        );
    }

    private static ArithmeticSum parseProblem(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        final String[] split = reader.readLine().split(";");
        if (split.length != 3) {
            throw new IOException("Exactly 3 parameters are expected!");
        }
        return new ArithmeticSum(
            AlgebraAlgorithms.parseRationalNumber(split[0]),
            AlgebraAlgorithms.parseRationalNumber(split[1]),
            Integer.parseInt(split[2])
        );
    }

    @Override
    public ArithmeticSum apply(final ArithmeticSum problem) {
        return problem;
    }

    @Override
    public String[] generateTestParameters() {
        return new String[] {};
    }

    @Override
    public ArithmeticSum parseOrGenerateProblem(final Parameters<Flag> options) throws IOException {
        return new ParserAndGenerator<ArithmeticSum>(
            ArithmeticSumAlgorithm::parseProblem,
            ArithmeticSumAlgorithm::generateProblem
        ).getResult(options);
    }

    @Override
    public void printExercise(
        final ArithmeticSum problem,
        final ArithmeticSum solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Berechnen Sie:");
        Main.newLine(writer);
        writer.write("\\[");
        writer.write(problem.toString());
        writer.write("\\]");
        Main.newLine(writer);
        Main.newLine(writer);
    }

    @Override
    public void printSolution(
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
        Main.newLine(writer);
    }

}
