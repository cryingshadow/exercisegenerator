package exercisegenerator.algorithms.algebra;

import java.util.*;
import java.util.function.*;

import exercisegenerator.structures.algebra.*;

public class GaussJordanAlgorithm implements Function<Matrix, List<Matrix>> {

    public static GaussJordanAlgorithm INSTANCE = new GaussJordanAlgorithm();

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

    private static int computeMinimumDimension(final Matrix matrix) {
        return Math.min(matrix.getNumberOfRows(), matrix.separatorIndex);
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

    private GaussJordanAlgorithm() {}

    @Override
    public List<Matrix> apply(final Matrix problem) {
        return GaussJordanAlgorithm.gaussJordan(problem);
    }

}
