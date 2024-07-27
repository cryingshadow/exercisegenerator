package exercisegenerator.algorithms.algebra;

import java.io.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.algebra.*;

public class GaussJordanAlgorithm implements AlgorithmImplementation {

    public static final GaussJordanAlgorithm INSTANCE = new GaussJordanAlgorithm();

    public static List<Matrix> gaussJordan(final Matrix problem) {
        final List<Matrix> solution = new LinkedList<Matrix>();
        solution.add(problem);
        Matrix current = problem;
        while (!GaussJordanAlgorithm.isSolved(current)) {
            current = GaussJordanAlgorithm.gaussJordanStep(current);
            solution.add(current);
        }
        return solution;
    }

    private static boolean assignmentHasConflict(
        final BigFraction[] assignment,
        final Matrix solvedSystem
    ) {
        for (int row = assignment.length; row < solvedSystem.getNumberOfRows(); row++) {
            BigFraction left = BigFraction.ZERO;
            for (int column = 0; column < solvedSystem.getIndexOfLastColumn(); column++) {
                if (!solvedSystem.isZero(column, row)) {
                    left = left.add(solvedSystem.getCoefficient(column, row).multiply(assignment[column]));
                }
            }
            if (left.compareTo(solvedSystem.getLastCoefficientOfRow(row)) != 0) {
                return true;
            }
        }
        return false;
    }

    private static BigFraction[] computeAssignment(final Matrix solvedSystem) {
        if (solvedSystem.separatorIndex != solvedSystem.getIndexOfLastColumn()) {
            throw new IllegalArgumentException("Can only compute assignment for an assignment matrix!");
        }
        final int rank = GaussJordanAlgorithm.computeRankForSolvedMatrix(solvedSystem);
        final BigFraction[] result = new BigFraction[rank];
        for (int row = 0; row < rank; row++) {
            result[row] = solvedSystem.getLastCoefficientOfRow(row);
        }
        return result;
    }

    private static int computeMinimumDimension(final Matrix matrix) {
        return Math.min(matrix.getNumberOfRows(), matrix.separatorIndex);
    }

    private static int computeRankForSolvedMatrix(final Matrix matrix) {
        int rank = 0;
        while (rank < matrix.getNumberOfRows() && rank < matrix.separatorIndex && matrix.isOne(rank, rank)) {
            rank++;
        }
        return rank;
    }

    private static Matrix gaussJordanStep(final Matrix matrix) {
        final int minDimension = GaussJordanAlgorithm.computeMinimumDimension(matrix);
        for (int column = 0; column < minDimension; column++) {
            if (!matrix.isOne(column, column)) {
                if (!matrix.isZero(column, column)) {
                    return matrix.multiplyRow(column, matrix.getCoefficient(column, column).reciprocal());
                }
                for (int row = column + 1; row < matrix.getNumberOfRows(); row++) {
                    if (!matrix.isZero(column, row)) {
                        return matrix.swapRows(column, row);
                    }
                }
                for (int secondColumn = column + 1; secondColumn < matrix.separatorIndex; secondColumn++) {
                    for (int row = column; row < matrix.getNumberOfRows(); row++) {
                        if (!matrix.isZero(secondColumn, row)) {
                            return matrix.swapColumns(column, secondColumn);
                        }
                    }
                }
                throw new IllegalStateException("Solved system should not be solved further!");
            }
            for (int row = 0; row < matrix.getNumberOfRows(); row++) {
                if (row != column) {
                    if (!matrix.isZero(column, row)) {
                        return matrix.addRow(row, column, matrix.getCoefficient(column, row).negate());
                    }
                }
            }
        }
        throw new IllegalStateException("Solved system should not be solved further!");
    }

    private static Matrix generateLinearSystemOfEquations(final Parameters options) {
        final int numberOfVariables = AlgebraAlgorithms.parseOrGenerateNumberOfVariables(options);
        final int numberOfEquations = AlgebraAlgorithms.generateNumberOfInequalitiesOrEquations();
        final BigFraction[][] coefficients =
            AlgebraAlgorithms.generateInequalitiesOrEquations(numberOfEquations, numberOfVariables);
        return new Matrix(coefficients, numberOfVariables);
    }

    private static boolean isSolved(final Matrix matrix) {
        final int minDimension = GaussJordanAlgorithm.computeMinimumDimension(matrix);
        int rank = 0;
        while (rank < minDimension) {
            if (matrix.isOne(rank, rank)) {
                rank++;
            } else if (matrix.isZero(rank, rank)) {
                break;
            } else {
                return false;
            }
        }
        return matrix.isIdentityMatrix(0, rank) && GaussJordanAlgorithm.isSolvedBelowIdentity(matrix, rank);
    }

    private static boolean isSolvedBelowIdentity(final Matrix matrix, final int from) {
        for (int row = from; row < matrix.getNumberOfRows(); row++) {
            for (int column = 0; column < matrix.separatorIndex; column++) {
                if (!matrix.isZero(column, row)) {
                    return false;
                }
            }
            if (!matrix.isZeroOnTheRight(row)) {
                return true;
            }
        }
        return true;
    }

    private static Matrix parseLinearSystemOfEquations(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        final String line = reader.readLine();
        final String[] rows = line.split(";");
        final BigFraction[][] coefficients =
            new BigFraction[rows.length][((int)rows[0].chars().filter(c -> c == ',').count()) + 1];
        for (int row = 0; row < coefficients.length; row++) {
            final String[] numbers = rows[row].split(",");
            if (numbers.length != coefficients[row].length) {
                throw new IOException("The rows of the matrix must all have the same length!");
            }
            for (int column = 0; column < numbers.length; column++) {
                coefficients[row][column] = AlgebraAlgorithms.parseRationalNumber(numbers[column]);
            }
        }
        return new Matrix(coefficients, coefficients[0].length - 1);
    }

    private static Matrix parseOrGenerateLinearSystemOfEquations(final Parameters options)
    throws IOException {
        return new ParserAndGenerator<Matrix>(
            GaussJordanAlgorithm::parseLinearSystemOfEquations,
            GaussJordanAlgorithm::generateLinearSystemOfEquations
        ).getResult(options);
    }

    private static void printGaussJordanExercise(
        final Matrix problem,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Gegeben sei das folgende \\emphasize{lineare Gleichungssystem}:\\\\[2ex]");
        Main.newLine(writer);
        AlgebraAlgorithms.printMatrixAsInequalitiesOrEquations(problem, "=", writer);
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write(
            "L\\\"osen Sie dieses lineare Gleichungssystem mithilfe des \\emphasize{Gau\\ss{}-Jordan-Algorithmus}."
        );
        Main.newLine(writer);
        Main.newLine(writer);
    }

    private static void printGaussJordanSolution(
        final List<Matrix> solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("{\\renewcommand{\\arraystretch}{1.2}");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("\\begin{enumerate}");
        Main.newLine(writer);
        Matrix lastMatrix = null;
        for (final Matrix matrix : solution) {
            writer.write(String.format("\\item $\\left(\\begin{array}{*{%d}c|c}", matrix.getIndexOfLastColumn()));
            Main.newLine(writer);
            for (int row = 0; row < matrix.getNumberOfRows(); row++) {
                writer.write(AlgebraAlgorithms.toCoefficient(matrix.getCoefficient(0, row)));
                for (int column = 1; column < matrix.getNumberOfColumns(); column++) {
                    writer.write(" & ");
                    writer.write(AlgebraAlgorithms.toCoefficient(matrix.getCoefficient(column, row)));
                }
                writer.write("\\\\");
                Main.newLine(writer);
            }
            writer.write("\\end{array}\\right)$");
            Main.newLine(writer);
            lastMatrix = matrix;
        }
        writer.write("\\end{enumerate}");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("\\renewcommand{\\arraystretch}{1}}");
        Main.newLine(writer);
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("Ergebnis: ");
        final BigFraction[] assignment = GaussJordanAlgorithm.computeAssignment(lastMatrix);
        if (GaussJordanAlgorithm.assignmentHasConflict(assignment, lastMatrix)) {
            writer.write("unl\\\"osbar");
        } else {
            writer.write("$");
            writer.write(GaussJordanAlgorithm.toParameterizedAssignment(lastMatrix, 0));
            final int rank = GaussJordanAlgorithm.computeRankForSolvedMatrix(lastMatrix);
            for (int column = 1; column < rank; column++) {
                writer.write("$, $");
                writer.write(GaussJordanAlgorithm.toParameterizedAssignment(lastMatrix, column));
            }
            writer.write("$");
        }
        Main.newLine(writer);
        Main.newLine(writer);
    }

    private static String toParameterizedAssignment(final Matrix solvedSystem, final int variable) {
        if (solvedSystem.separatorIndex != solvedSystem.getIndexOfLastColumn()) {
            throw new IllegalArgumentException("Can only compute assignment for an assignment matrix!");
        }
        final StringBuilder result = new StringBuilder();
        result.append(String.format("x_{%d} = ", solvedSystem.columnPositions[variable] + 1));
        result.append(AlgebraAlgorithms.toCoefficient(solvedSystem.getLastCoefficientOfRow(variable)));
        for (int column = variable + 1; column < solvedSystem.getIndexOfLastColumn(); column++) {
            if (!solvedSystem.isZero(column, variable)) {
                if (solvedSystem.isNegative(column, variable)) {
                    result.append(" + ");
                } else {
                    result.append(" - ");
                }
                result.append(AlgebraAlgorithms.toCoefficient(solvedSystem.getCoefficient(column, variable).abs()));
                result.append(String.format("x_{%d}", solvedSystem.columnPositions[column] + 1));
            }
        }
        return result.toString();
    }

    private GaussJordanAlgorithm() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final Matrix problem =
            GaussJordanAlgorithm.parseOrGenerateLinearSystemOfEquations(input.options);
        final List<Matrix> solution = GaussJordanAlgorithm.gaussJordan(problem);
        GaussJordanAlgorithm.printGaussJordanExercise(problem, input.options, input.exerciseWriter);
        GaussJordanAlgorithm.printGaussJordanSolution(solution, input.options, input.solutionWriter);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
