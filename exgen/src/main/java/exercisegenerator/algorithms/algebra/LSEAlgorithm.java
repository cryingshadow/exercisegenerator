package exercisegenerator.algorithms.algebra;

import java.io.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.algebra.*;

public class LSEAlgorithm implements AlgorithmImplementation<Matrix, List<Matrix>> {

    public static final LSEAlgorithm INSTANCE = new LSEAlgorithm();

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
        final int rank = LSEAlgorithm.computeRankForSolvedMatrix(solvedSystem);
        final BigFraction[] result = new BigFraction[rank];
        for (int row = 0; row < rank; row++) {
            result[row] = solvedSystem.getLastCoefficientOfRow(row);
        }
        return result;
    }

    private static int computeRankForSolvedMatrix(final Matrix matrix) {
        int rank = 0;
        while (rank < matrix.getNumberOfRows() && rank < matrix.separatorIndex && matrix.isOne(rank, rank)) {
            rank++;
        }
        return rank;
    }

    private static String[] parseRows(final String firstLine, final BufferedReader reader) throws IOException {
        if (firstLine.contains(";")) {
            return firstLine.split(";");
        }
        final List<String> lines = new LinkedList<String>();
        lines.add(firstLine);
        String line = reader.readLine();
        while (line != null) {
            if (!line.isBlank()) {
                lines.add(line);
            }
            line = reader.readLine();
        }
        final String[] result = new String[lines.size()];
        return lines.toArray(result);
    }

    private static String toParameterizedAssignment(final Matrix solvedSystem, final int variable) {
        if (solvedSystem.separatorIndex != solvedSystem.getIndexOfLastColumn()) {
            throw new IllegalArgumentException("Can only compute assignment for an assignment matrix!");
        }
        final StringBuilder result = new StringBuilder();
        result.append(String.format("x_{%d} = ", solvedSystem.columnPositions[variable] + 1));
        result.append(LaTeXUtils.toCoefficient(solvedSystem.getLastCoefficientOfRow(variable)));
        for (int column = variable + 1; column < solvedSystem.getIndexOfLastColumn(); column++) {
            if (!solvedSystem.isZero(column, variable)) {
                if (solvedSystem.isNegative(column, variable)) {
                    result.append(" + ");
                } else {
                    result.append(" - ");
                }
                result.append(LaTeXUtils.toCoefficient(solvedSystem.getCoefficient(column, variable).abs()));
                result.append(String.format("x_{%d}", solvedSystem.columnPositions[column] + 1));
            }
        }
        return result.toString();
    }

    private LSEAlgorithm() {}

    @Override
    public List<Matrix> apply(final Matrix problem) {
        return GaussJordanAlgorithm.gaussJordan(problem);
    }

    @Override
    public String commandPrefix() {
        return "Lse";
    }

    @Override
    public Matrix generateProblem(final Parameters<Flag> options) {
        final int numberOfVariables = AlgebraAlgorithms.parseOrGenerateNumberOfVariables(options);
        final int numberOfEquations = AlgebraAlgorithms.generateNumberOfInequalitiesOrEquations();
        final BigFraction[][] coefficients =
            AlgebraAlgorithms.generateInequalitiesOrEquations(numberOfEquations, numberOfVariables);
        return new Matrix(coefficients, numberOfVariables);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public List<Matrix> parseProblems(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        final String line = reader.readLine();
        final String[] rows = LSEAlgorithm.parseRows(line, reader);
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
        return List.of(new Matrix(coefficients, coefficients[0].length - 1));
    }

    @Override
    public void printAfterSingleProblemInstance(
        final Matrix problem,
        final List<Matrix> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("L\\\"osen Sie dieses lineare Gleichungssystem mithilfe des ");
        writer.write("\\emphasize{Gau\\ss{}-Jordan-Algorithmus}.");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<Matrix> problems,
        final List<List<Matrix>> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("L\\\"osen Sie die folgenden \\emphasize{linearen Gleichungssysteme} mithilfe des ");
        writer.write("\\emphasize{Gau\\ss{}-Jordan-Algorithmus}.");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeSingleProblemInstance(
        final Matrix problem,
        final List<Matrix> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Gegeben sei das folgende \\emphasize{lineare Gleichungssystem}:\\\\[2ex]");
        Main.newLine(writer);
    }

    @Override
    public void printProblemInstance(
        final Matrix problem,
        final List<Matrix> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        AlgebraAlgorithms.printMatrixAsInequalitiesOrEquations(problem, "=", writer);
    }

    @Override
    public void printSolutionInstance(
        final Matrix problem,
        final List<Matrix> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("{\\renewcommand{\\arraystretch}{1.2}");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("\\begin{enumerate}");
        Main.newLine(writer);
        for (final Matrix matrix : solution) {
            writer.write("\\item $");
            writer.write(matrix.toLaTeX());
            writer.write("$");
            Main.newLine(writer);
        }
        writer.write("\\end{enumerate}");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("\\renewcommand{\\arraystretch}{1}}");
        Main.newLine(writer);
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("Ergebnis: ");
        final Matrix lastMatrix = solution.getLast();
        final BigFraction[] assignment = LSEAlgorithm.computeAssignment(lastMatrix);
        if (LSEAlgorithm.assignmentHasConflict(assignment, lastMatrix)) {
            writer.write("unl\\\"osbar");
        } else {
            writer.write("$");
            writer.write(LSEAlgorithm.toParameterizedAssignment(lastMatrix, 0));
            final int rank = LSEAlgorithm.computeRankForSolvedMatrix(lastMatrix);
            for (int column = 1; column < rank; column++) {
                writer.write("$, $");
                writer.write(LSEAlgorithm.toParameterizedAssignment(lastMatrix, column));
            }
            writer.write("$");
        }
        Main.newLine(writer);
    }

    @Override
    public void printSolutionSpace(
        final Matrix problem,
        final List<Matrix> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

}
