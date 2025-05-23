package exercisegenerator.algorithms.algebra;

import java.io.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.algebra.*;

public class MatrixInversionAlgorithm implements AlgorithmImplementation<Matrix, List<Matrix>> {

    public static final MatrixInversionAlgorithm INSTANCE = new MatrixInversionAlgorithm();

    private static Matrix extractInvertedMatrix(final Matrix matrix) {
        final int dimension = matrix.getNumberOfRows();
        final Matrix result = new Matrix(dimension, dimension, dimension);
        for (int row = 0; row < dimension; row++) {
            for (int column = 0; column < dimension; column++) {
                result.setCoefficient(column, row, matrix.getCoefficient(column + dimension, row));
            }
        }
        return result;
    }

    private static Matrix generateMatrix(final Parameters<Flag> options) {
        return AlgebraAlgorithms.generateQuadraticMatrix(AlgebraAlgorithms.parseOrGenerateDimensionOfMatrices(options));
    }

    private static boolean hasIdentityMatrixLeft(final Matrix matrix) {
        final int dimension = matrix.getNumberOfRows();
        for (int row = 0; row < dimension; row++) {
            for (int column = 0; column < dimension; column++) {
                if (
                    (row == column && !matrix.isOne(column, row))
                    || (row != column && !matrix.isZero(column, row))
                ) {
                    return false;
                }
            }
        }
        return true;
    }

    private static Matrix parseMatrix(final BufferedReader reader, final Parameters<Flag> options) throws IOException {
        final List<String> text = new LinkedList<String>();
        String line = reader.readLine();
        while (line != null && !line.isBlank()) {
            text.add(line);
            line = reader.readLine();
        }
        return AlgebraAlgorithms.parseMatrix(text);
    }

    private MatrixInversionAlgorithm() {}

    @Override
    public List<Matrix> apply(final Matrix problem) {
        if (problem.getNumberOfColumns() != problem.getNumberOfRows()) {
            throw new IllegalArgumentException("Inversion is only applicable to quadratic matrices!");
        }
        return GaussJordanAlgorithm.gaussJordan(this.addIdentityMatrix(problem));
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-d";
        result[1] = "4";
        return result;
    }

    @Override
    public Matrix parseOrGenerateProblem(final Parameters<Flag> options) throws IOException {
        return new ParserAndGenerator<Matrix>(
            MatrixInversionAlgorithm::parseMatrix,
            MatrixInversionAlgorithm::generateMatrix
        ).getResult(options);
    }

    @Override
    public void printExercise(
        final Matrix problem,
        final List<Matrix> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Invertieren Sie die folgende Matrix mithilfe des \\emphasize{Gau\\ss{}-Jordan-Algorithmus} oder");
        writer.write(" bestimmen Sie durch diesen Algorithmus, dass sich die Matrix nicht invertieren ");
        writer.write("l\\\"asst:\\\\[2ex]");
        Main.newLine(writer);
        writer.write("$");
        writer.write(problem.toLaTeX());
        writer.write("$");
        Main.newLine(writer);
        Main.newLine(writer);
    }

    @Override
    public void printSolution(
        final Matrix problem,
        final List<Matrix> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        for (final Matrix matrix : solution) {
            writer.write("$");
            writer.write(matrix.toLaTeX());
            writer.write("$\\\\");
            Main.newLine(writer);
        }
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("Ergebnis:\\\\");
        Main.newLine(writer);
        final Matrix result = solution.get(solution.size() - 1);
        if (MatrixInversionAlgorithm.hasIdentityMatrixLeft(result)) {
            writer.write("$");
            writer.write(MatrixInversionAlgorithm.extractInvertedMatrix(result).toLaTeX());
            writer.write("$");
        } else {
            writer.write("nicht invertierbar");
        }
        Main.newLine(writer);
        Main.newLine(writer);
    }

    private Matrix addIdentityMatrix(final Matrix problem) {
        final int dimension = problem.getNumberOfColumns();
        final Matrix result = new Matrix(dimension * 2, dimension, dimension);
        for (int row = 0; row < dimension; row++) {
            for (int column = 0; column < dimension; column++) {
                result.setCoefficient(column, row, problem.getCoefficient(column, row));
                result.setCoefficient(column + dimension, row, column == row ? BigFraction.ONE : BigFraction.ZERO);
            }
        }
        return result;
    }

}
