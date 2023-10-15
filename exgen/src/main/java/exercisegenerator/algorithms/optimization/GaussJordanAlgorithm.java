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

    public static List<LinearSystemOfEquations> gaussJordan(final LinearSystemOfEquations problem) {
        final List<LinearSystemOfEquations> solution = new LinkedList<LinearSystemOfEquations>();
        solution.add(problem);
        LinearSystemOfEquations current = problem;
        while (!GaussJordanAlgorithm.isSolved(current)) {
            current = GaussJordanAlgorithm.gaussJordanStep(current);
            solution.add(current);
        }
        return solution;
    }

    private static boolean assignmentHasConflict(
        final Fraction[] assignment,
        final LinearSystemOfEquations solvedSystem
    ) {
        if (solvedSystem.numberOfRows <= assignment.length) {
            return false;
        }
        for (int row = assignment.length; row < solvedSystem.numberOfRows; row++) {
            Fraction left = Fraction.ZERO;
            for (int col = 0; col < solvedSystem.numberOfColumns - 1; col++) {
                if (solvedSystem.matrix[row][col].compareTo(Fraction.ZERO) != 0) {
                    left = left.add(solvedSystem.matrix[row][col].multiply(assignment[col]));
                }
            }
            if (left.compareTo(solvedSystem.matrix[row][solvedSystem.numberOfColumns - 1]) != 0) {
                return true;
            }
        }
        return false;
    }

    private static Fraction[] computeAssignment(final LinearSystemOfEquations solvedSystem) {
        final int rank = GaussJordanAlgorithm.computeRankForSolvedMatrix(solvedSystem.matrix);
        final Fraction[] result = new Fraction[rank];
        for (int row = 0; row < rank; row++) {
            result[row] = solvedSystem.matrix[row][solvedSystem.numberOfColumns - 1];
        }
        return result;
    }

    private static int computeMinimumDimension(final LinearSystemOfEquations system) {
        return Math.min(system.numberOfRows, system.numberOfColumns - 1);
    }

    private static int computeRankForSolvedMatrix(final Fraction[][] matrix) {
        int rank = 0;
        while (
            rank < matrix.length && rank < matrix[rank].length - 1 && matrix[rank][rank].compareTo(Fraction.ONE) == 0
        ) {
            rank++;
        }
        return rank;
    }

    private static LinearSystemOfEquations gaussJordanStep(final LinearSystemOfEquations system) {
        final int minDimension = GaussJordanAlgorithm.computeMinimumDimension(system);
        for (int col = 0; col < minDimension; col++) {
            if (system.matrix[col][col].compareTo(Fraction.ONE) != 0) {
                if (system.matrix[col][col].compareTo(Fraction.ZERO) != 0) {
                    return system.multiplyRow(col, system.matrix[col][col].reciprocal());
                }
                for (int row = col + 1; row < system.numberOfRows; row++) {
                    if (system.matrix[row][col].compareTo(Fraction.ZERO) != 0) {
                        return system.swapRows(col, row);
                    }
                }
                for (int secondCol = col + 1; secondCol < system.numberOfColumns - 1; secondCol++) {
                    for (int row = col; row < system.numberOfRows; row++) {
                        if (system.matrix[row][col].compareTo(Fraction.ZERO) != 0) {
                            return system.swapColumns(col, secondCol);
                        }
                    }
                }
                throw new IllegalStateException("Solved system should not be solved further!");
            }
            for (int row = 0; row < system.numberOfRows; row++) {
                if (row != col) {
                    if (system.matrix[row][col].compareTo(Fraction.ZERO) != 0) {
                        return system.addRow(row, col, system.matrix[row][col].negate());
                    }
                }
            }
        }
        throw new IllegalStateException("Solved system should not be solved further!");
    }

    private static LinearSystemOfEquations generateLinearSystemOfEquations(final Parameters options) {
        final int numberOfVariables = OptimizationAlgorithms.parseOrGenerateNumberOfVariables(options);
        final int numberOfEquations = OptimizationAlgorithms.generateNumberOfInequalitiesOrEquations();
        final Fraction[][] matrix =
            OptimizationAlgorithms.generateInequalitiesOrEquations(numberOfEquations, numberOfVariables);
        return new LinearSystemOfEquations(matrix);
    }

    private static boolean isIdentityMatrix(final Fraction[][] matrix, final int from, final int to) {
        for (int row = from; row < to; row++) {
            for (int col = from; col < to; col++) {
                if (row == col) {
                    if (matrix[row][col].compareTo(Fraction.ONE) != 0) {
                        return false;
                    }
                } else if (matrix[row][col].compareTo(Fraction.ZERO) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isSolved(final LinearSystemOfEquations system) {
        final int minDimension = GaussJordanAlgorithm.computeMinimumDimension(system);
        int rank = 0;
        while (rank < minDimension) {
            if (system.matrix[rank][rank].compareTo(Fraction.ONE) == 0) {
                rank++;
            } else if (system.matrix[rank][rank].compareTo(Fraction.ZERO) == 0) {
                break;
            } else {
                return false;
            }
        }
        return GaussJordanAlgorithm.isIdentityMatrix(system.matrix, 0, rank)
            && GaussJordanAlgorithm.isSolvedBelowIdentity(
                system.matrix,
                rank,
                system.numberOfRows,
                system.numberOfColumns
            );
    }

    private static boolean isSolvedBelowIdentity(
        final Fraction[][] matrix,
        final int from,
        final int numberOfRows,
        final int numberOfColumns
    ) {
        for (int row = from; row < numberOfRows; row++) {
            for (int col = 0; col < numberOfColumns - 1; col++) {
                if (matrix[row][col].compareTo(Fraction.ZERO) != 0) {
                    return false;
                }
            }
            if (matrix[row][numberOfColumns - 1].compareTo(Fraction.ZERO) != 0) {
                return true;
            }
        }
        return true;
    }

    private static LinearSystemOfEquations parseLinearSystemOfEquations(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        final String line = reader.readLine();
        final String[] rows = line.split(";");
        final Fraction[][] matrix = new Fraction[rows.length][((int)rows[0].chars().filter(c -> c == ',').count()) + 1];
        for (int row = 0; row < matrix.length; row++) {
            final String[] numbers = rows[row].split(",");
            if (numbers.length != matrix[row].length) {
                throw new IOException("The rows of the matrix must all have the same length!");
            }
            for (int col = 0; col < numbers.length; col++) {
                matrix[row][col] = OptimizationAlgorithms.parseRationalNumber(numbers[col]);
            }
        }
        return new LinearSystemOfEquations(matrix);
    }

    private static LinearSystemOfEquations parseOrGenerateLinearSystemOfEquations(final Parameters options)
    throws IOException {
        return new ParserAndGenerator<LinearSystemOfEquations>(
            GaussJordanAlgorithm::parseLinearSystemOfEquations,
            GaussJordanAlgorithm::generateLinearSystemOfEquations
        ).getResult(options);
    }

    private static void printGaussJordanExercise(
        final LinearSystemOfEquations problem,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Gegeben sei das folgende \\emphasize{lineare Gleichungssystem}:\\\\[2ex]");
        Main.newLine(writer);
        OptimizationAlgorithms.printMatrixAsInequalitiesOrEquations(
            problem.matrix,
            problem.numberOfColumns,
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
        final List<LinearSystemOfEquations> solution,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("{\\renewcommand{\\arraystretch}{1.2}");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("\\begin{enumerate}");
        Main.newLine(writer);
        LinearSystemOfEquations lastSystem = null;
        for (final LinearSystemOfEquations system : solution) {
            writer.write(String.format("\\item $\\left(\\begin{array}{*{%d}c|c}", system.numberOfColumns - 1));
            Main.newLine(writer);
            for (int row = 0; row < system.numberOfRows; row++) {
                writer.write(OptimizationAlgorithms.toCoefficient(system.matrix[row][0]));
                for (int col = 1; col < system.numberOfColumns; col++) {
                    writer.write(" & ");
                    writer.write(OptimizationAlgorithms.toCoefficient(system.matrix[row][col]));
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
        final Fraction[] assignment = GaussJordanAlgorithm.computeAssignment(lastSystem);
        if (GaussJordanAlgorithm.assignmentHasConflict(assignment, lastSystem)) {
            writer.write("unl\\\"osbar");
        } else {
            writer.write("$");
            writer.write(GaussJordanAlgorithm.toParameterizedAssignment(lastSystem, 0));
            final int rank = GaussJordanAlgorithm.computeRankForSolvedMatrix(lastSystem.matrix);
            for (int col = 1; col < rank; col++) {
                writer.write("$, $");
                writer.write(GaussJordanAlgorithm.toParameterizedAssignment(lastSystem, col));
            }
            writer.write("$");
        }
        Main.newLine(writer);
        Main.newLine(writer);
    }

    private static String toParameterizedAssignment(final LinearSystemOfEquations solvedSystem, final int column) {
        final StringBuilder result = new StringBuilder();
        result.append(String.format("x_{%d} = ", solvedSystem.columnPositions[column] + 1));
        result.append(
            OptimizationAlgorithms.toCoefficient(solvedSystem.matrix[column][solvedSystem.numberOfColumns - 1])
        );
        for (int col = column + 1; col < solvedSystem.numberOfColumns - 1; col++) {
            if (solvedSystem.matrix[column][col].compareTo(Fraction.ZERO) != 0) {
                if (solvedSystem.matrix[column][col].compareTo(Fraction.ZERO) < 0) {
                    result.append(" + ");
                } else {
                    result.append(" - ");
                }
                result.append(OptimizationAlgorithms.toCoefficient(solvedSystem.matrix[column][col].abs()));
                result.append(String.format("x_{%d}", solvedSystem.columnPositions[col] + 1));
            }
        }
        return result.toString();
    }

    private GaussJordanAlgorithm() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final LinearSystemOfEquations problem =
            GaussJordanAlgorithm.parseOrGenerateLinearSystemOfEquations(input.options);
        final List<LinearSystemOfEquations> solution = GaussJordanAlgorithm.gaussJordan(problem);
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
