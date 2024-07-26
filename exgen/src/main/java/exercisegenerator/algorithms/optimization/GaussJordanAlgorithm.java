package exercisegenerator.algorithms.optimization;

import java.io.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.optimization.*;

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
        if (solvedSystem.getNumberOfRows() <= assignment.length) {
            return false;
        }
        for (int row = assignment.length; row < solvedSystem.getNumberOfRows(); row++) {
            BigFraction left = BigFraction.ZERO;
            for (int col = 0; col < solvedSystem.getNumberOfColumns() - 1; col++) {
                if (solvedSystem.coefficients[row][col].compareTo(BigFraction.ZERO) != 0) {
                    left = left.add(solvedSystem.coefficients[row][col].multiply(assignment[col]));
                }
            }
            if (left.compareTo(solvedSystem.coefficients[row][solvedSystem.getNumberOfColumns() - 1]) != 0) {
                return true;
            }
        }
        return false;
    }

    private static BigFraction[] computeAssignment(final Matrix solvedSystem) {
        final int rank = GaussJordanAlgorithm.computeRankForSolvedMatrix(solvedSystem.coefficients);
        final BigFraction[] result = new BigFraction[rank];
        for (int row = 0; row < rank; row++) {
            result[row] = solvedSystem.coefficients[row][solvedSystem.getNumberOfColumns() - 1];
        }
        return result;
    }

    private static int computeMinimumDimension(final Matrix system) {
        return Math.min(system.getNumberOfRows(), system.getNumberOfColumns() - 1);
    }

    private static int computeRankForSolvedMatrix(final BigFraction[][] matrix) {
        int rank = 0;
        while (
            rank < matrix.length && rank < matrix[rank].length - 1 && matrix[rank][rank].compareTo(BigFraction.ONE) == 0
        ) {
            rank++;
        }
        return rank;
    }

    private static Matrix gaussJordanStep(final Matrix system) {
        final int minDimension = GaussJordanAlgorithm.computeMinimumDimension(system);
        for (int col = 0; col < minDimension; col++) {
            if (system.coefficients[col][col].compareTo(BigFraction.ONE) != 0) {
                if (system.coefficients[col][col].compareTo(BigFraction.ZERO) != 0) {
                    return system.multiplyRow(col, system.coefficients[col][col].reciprocal());
                }
                for (int row = col + 1; row < system.getNumberOfRows(); row++) {
                    if (system.coefficients[row][col].compareTo(BigFraction.ZERO) != 0) {
                        return system.swapRows(col, row);
                    }
                }
                for (int secondCol = col + 1; secondCol < system.getNumberOfColumns() - 1; secondCol++) {
                    for (int row = col; row < system.getNumberOfRows(); row++) {
                        if (system.coefficients[row][secondCol].compareTo(BigFraction.ZERO) != 0) {
                            return system.swapColumns(col, secondCol);
                        }
                    }
                }
                throw new IllegalStateException("Solved system should not be solved further!");
            }
            for (int row = 0; row < system.getNumberOfRows(); row++) {
                if (row != col) {
                    if (system.coefficients[row][col].compareTo(BigFraction.ZERO) != 0) {
                        return system.addRow(row, col, system.coefficients[row][col].negate());
                    }
                }
            }
        }
        throw new IllegalStateException("Solved system should not be solved further!");
    }

    private static Matrix generateLinearSystemOfEquations(final Parameters options) {
        final int numberOfVariables = OptimizationAlgorithms.parseOrGenerateNumberOfVariables(options);
        final int numberOfEquations = OptimizationAlgorithms.generateNumberOfInequalitiesOrEquations();
        final BigFraction[][] coefficients =
            OptimizationAlgorithms.generateInequalitiesOrEquations(numberOfEquations, numberOfVariables);
        return new Matrix(coefficients, numberOfVariables);
    }

    private static boolean isIdentityMatrix(final BigFraction[][] matrix, final int from, final int to) {
        for (int row = from; row < to; row++) {
            for (int col = from; col < to; col++) {
                if (row == col) {
                    if (matrix[row][col].compareTo(BigFraction.ONE) != 0) {
                        return false;
                    }
                } else if (matrix[row][col].compareTo(BigFraction.ZERO) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isSolved(final Matrix system) {
        final int minDimension = GaussJordanAlgorithm.computeMinimumDimension(system);
        int rank = 0;
        while (rank < minDimension) {
            if (system.coefficients[rank][rank].compareTo(BigFraction.ONE) == 0) {
                rank++;
            } else if (system.coefficients[rank][rank].compareTo(BigFraction.ZERO) == 0) {
                break;
            } else {
                return false;
            }
        }
        return GaussJordanAlgorithm.isIdentityMatrix(system.coefficients, 0, rank)
            && GaussJordanAlgorithm.isSolvedBelowIdentity(
                system.coefficients,
                rank,
                system.getNumberOfRows(),
                system.getNumberOfColumns()
            );
    }

    private static boolean isSolvedBelowIdentity(
        final BigFraction[][] matrix,
        final int from,
        final int numberOfRows,
        final int numberOfColumns
    ) {
        for (int row = from; row < numberOfRows; row++) {
            for (int col = 0; col < numberOfColumns - 1; col++) {
                if (matrix[row][col].compareTo(BigFraction.ZERO) != 0) {
                    return false;
                }
            }
            if (matrix[row][numberOfColumns - 1].compareTo(BigFraction.ZERO) != 0) {
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
            for (int col = 0; col < numbers.length; col++) {
                coefficients[row][col] = OptimizationAlgorithms.parseRationalNumber(numbers[col]);
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
        OptimizationAlgorithms.printMatrixAsInequalitiesOrEquations(
            problem.coefficients,
            problem.getNumberOfColumns(),
            "=",
            writer
        );
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
        Matrix lastSystem = null;
        for (final Matrix system : solution) {
            writer.write(String.format("\\item $\\left(\\begin{array}{*{%d}c|c}", system.getNumberOfColumns() - 1));
            Main.newLine(writer);
            for (int row = 0; row < system.getNumberOfRows(); row++) {
                writer.write(OptimizationAlgorithms.toCoefficient(system.coefficients[row][0]));
                for (int col = 1; col < system.getNumberOfColumns(); col++) {
                    writer.write(" & ");
                    writer.write(OptimizationAlgorithms.toCoefficient(system.coefficients[row][col]));
                }
                writer.write("\\\\");
                Main.newLine(writer);
            }
            writer.write("\\end{array}\\right)$");
            Main.newLine(writer);
            lastSystem = system;
        }
        writer.write("\\end{enumerate}");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("\\renewcommand{\\arraystretch}{1}}");
        Main.newLine(writer);
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("Ergebnis: ");
        final BigFraction[] assignment = GaussJordanAlgorithm.computeAssignment(lastSystem);
        if (GaussJordanAlgorithm.assignmentHasConflict(assignment, lastSystem)) {
            writer.write("unl\\\"osbar");
        } else {
            writer.write("$");
            writer.write(GaussJordanAlgorithm.toParameterizedAssignment(lastSystem, 0));
            final int rank = GaussJordanAlgorithm.computeRankForSolvedMatrix(lastSystem.coefficients);
            for (int col = 1; col < rank; col++) {
                writer.write("$, $");
                writer.write(GaussJordanAlgorithm.toParameterizedAssignment(lastSystem, col));
            }
            writer.write("$");
        }
        Main.newLine(writer);
        Main.newLine(writer);
    }

    private static String toParameterizedAssignment(final Matrix solvedSystem, final int column) {
        final StringBuilder result = new StringBuilder();
        result.append(String.format("x_{%d} = ", solvedSystem.columnPositions[column] + 1));
        result.append(
            OptimizationAlgorithms.toCoefficient(solvedSystem.coefficients[column][solvedSystem.getNumberOfColumns() - 1])
        );
        for (int col = column + 1; col < solvedSystem.getNumberOfColumns() - 1; col++) {
            if (solvedSystem.coefficients[column][col].compareTo(BigFraction.ZERO) != 0) {
                if (solvedSystem.coefficients[column][col].compareTo(BigFraction.ZERO) < 0) {
                    result.append(" + ");
                } else {
                    result.append(" - ");
                }
                result.append(OptimizationAlgorithms.toCoefficient(solvedSystem.coefficients[column][col].abs()));
                result.append(String.format("x_{%d}", solvedSystem.columnPositions[col] + 1));
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
