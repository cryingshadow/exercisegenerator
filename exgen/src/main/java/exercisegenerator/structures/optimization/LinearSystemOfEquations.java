package exercisegenerator.structures.optimization;

import java.util.*;
import java.util.stream.*;
import org.apache.commons.math3.fraction.*;

public class LinearSystemOfEquations {

    public final int[] columnPositions;

    public final BigFraction[][] matrix;

    public final int numberOfColumns;

    public final int numberOfRows;

    public LinearSystemOfEquations(final BigFraction[][] matrix) {
        this(matrix, IntStream.range(0, matrix[0].length - 1).toArray());
    }

    public LinearSystemOfEquations(final BigFraction[][] matrix, final int[] columnPositions) {
        this.matrix = matrix;
        this.numberOfRows = matrix.length;
        this.numberOfColumns = matrix[0].length;
        this.columnPositions = columnPositions;
    }

    public LinearSystemOfEquations(final int[][] matrix) {
        this(
            Arrays
            .stream(matrix)
            .map(row -> Arrays.stream(row).mapToObj(BigFraction::new).toArray(BigFraction[]::new))
            .toArray(BigFraction[][]::new)
        );
    }

    public LinearSystemOfEquations addRow(final int rowIndex, final int rowToAdd, final BigFraction factor) {
        final BigFraction[][] matrix = new BigFraction[this.numberOfRows][this.numberOfColumns];
        for (int row = 0; row < this.numberOfRows; row++) {
            for (int col = 0; col < this.numberOfColumns; col++) {
                if (row == rowIndex) {
                    matrix[row][col] = this.matrix[row][col].add(this.matrix[rowToAdd][col].multiply(factor));
                } else {
                    matrix[row][col] = this.matrix[row][col];
                }
            }
        }
        return new LinearSystemOfEquations(matrix, this.columnPositions);
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof LinearSystemOfEquations) {
            final LinearSystemOfEquations other = (LinearSystemOfEquations)o;
            return Arrays.equals(this.columnPositions, other.columnPositions)
                && Arrays.deepEquals(this.matrix, other.matrix);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 666 + this.matrix.hashCode() * 2 + this.columnPositions.hashCode() * 3;
    }

    public LinearSystemOfEquations multiplyRow(final int rowIndex, final BigFraction factor) {
        final BigFraction[][] matrix = new BigFraction[this.numberOfRows][this.numberOfColumns];
        for (int row = 0; row < this.numberOfRows; row++) {
            for (int col = 0; col < this.numberOfColumns; col++) {
                if (row == rowIndex) {
                    matrix[row][col] = this.matrix[row][col].multiply(factor);
                } else {
                    matrix[row][col] = this.matrix[row][col];
                }
            }
        }
        return new LinearSystemOfEquations(matrix, this.columnPositions);
    }

    public LinearSystemOfEquations swapColumns(final int col1, final int col2) {
        final BigFraction[][] matrix = new BigFraction[this.numberOfRows][this.numberOfColumns];
        for (int row = 0; row < this.numberOfRows; row++) {
            for (int col = 0; col < this.numberOfColumns; col++) {
                if (col == col1) {
                    matrix[row][col2] = this.matrix[row][col];
                } else if (col == col2) {
                    matrix[row][col1] = this.matrix[row][col];
                } else {
                    matrix[row][col] = this.matrix[row][col];
                }
            }
        }
        final int[] columnPositions = new int[this.columnPositions.length];
        System.arraycopy(this.columnPositions, 0, columnPositions, 0, this.columnPositions.length);
        columnPositions[col1] = this.columnPositions[col2];
        columnPositions[col2] = this.columnPositions[col1];
        return new LinearSystemOfEquations(matrix, columnPositions);
    }

    public LinearSystemOfEquations swapRows(final int row1, final int row2) {
        final BigFraction[][] matrix = new BigFraction[this.numberOfRows][this.numberOfColumns];
        for (int row = 0; row < this.numberOfRows; row++) {
            for (int col = 0; col < this.numberOfColumns; col++) {
                if (row == row1) {
                    matrix[row2][col] = this.matrix[row][col];
                } else if (row == row2) {
                    matrix[row1][col] = this.matrix[row][col];
                } else {
                    matrix[row][col] = this.matrix[row][col];
                }
            }
        }
        return new LinearSystemOfEquations(matrix, this.columnPositions);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        for (int row = 0; row < this.numberOfRows; row++) {
            boolean first = true;
            for (int col = 0; col < this.numberOfColumns - 1; col++) {
                if (first) {
                    first = false;
                } else {
                    res.append(", ");
                }
                res.append(this.matrix[row][col].toString());
            }
            res.append(" | ");
            res.append(this.matrix[row][this.numberOfColumns - 1].toString());
            res.append("\n");
        }
        return res.toString();
    }

}
