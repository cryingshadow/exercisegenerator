package exercisegenerator.structures.optimization;

import java.util.*;
import java.util.stream.*;
import org.apache.commons.math3.fraction.*;

public class Matrix {

    public final BigFraction[][] coefficients;

    public final int[] columnPositions;

    public final int separatorIndex;

    public Matrix(final BigFraction[][] coefficients, final int separatorIndex) {
        this(coefficients, IntStream.range(0, coefficients[0].length - 1).toArray(), separatorIndex);
    }

    public Matrix(final BigFraction[][] coefficients, final int[] columnPositions, final int separatorIndex) {
        this.coefficients = coefficients;
        this.columnPositions = columnPositions;
        this.separatorIndex = separatorIndex;
    }

    public Matrix(final int[][] coefficients, final int separatorIndex) {
        this(
            Arrays
            .stream(coefficients)
            .map(row -> Arrays.stream(row).mapToObj(BigFraction::new).toArray(BigFraction[]::new))
            .toArray(BigFraction[][]::new),
            separatorIndex
        );
    }

    public Matrix addRow(final int rowIndex, final int rowToAdd, final BigFraction factor) {
        final BigFraction[][] coefficients = new BigFraction[this.getNumberOfRows()][this.getNumberOfColumns()];
        for (int row = 0; row < this.getNumberOfRows(); row++) {
            for (int col = 0; col < this.getNumberOfColumns(); col++) {
                if (row == rowIndex) {
                    coefficients[row][col] =
                        this.coefficients[row][col].add(this.coefficients[rowToAdd][col].multiply(factor));
                } else {
                    coefficients[row][col] = this.coefficients[row][col];
                }
            }
        }
        return new Matrix(coefficients, this.columnPositions, this.separatorIndex);
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof Matrix) {
            final Matrix other = (Matrix)o;
            return Arrays.equals(this.columnPositions, other.columnPositions)
                && Arrays.deepEquals(this.coefficients, other.coefficients);
        }
        return false;
    }

    public int getNumberOfColumns() {
        return this.coefficients[0].length;
    }

    public int getNumberOfRows() {
        return this.coefficients.length;
    }

    @Override
    public int hashCode() {
        return 666 + this.coefficients.hashCode() * 2 + this.columnPositions.hashCode() * 3;
    }

    public Matrix multiplyRow(final int rowIndex, final BigFraction factor) {
        final BigFraction[][] coefficients = new BigFraction[this.getNumberOfRows()][this.getNumberOfColumns()];
        for (int row = 0; row < this.getNumberOfRows(); row++) {
            for (int col = 0; col < this.getNumberOfColumns(); col++) {
                if (row == rowIndex) {
                    coefficients[row][col] = this.coefficients[row][col].multiply(factor);
                } else {
                    coefficients[row][col] = this.coefficients[row][col];
                }
            }
        }
        return new Matrix(coefficients, this.columnPositions, this.separatorIndex);
    }

    public Matrix swapColumns(final int col1, final int col2) {
        final BigFraction[][] coefficients = new BigFraction[this.getNumberOfRows()][this.getNumberOfColumns()];
        for (int row = 0; row < this.getNumberOfRows(); row++) {
            for (int col = 0; col < this.getNumberOfColumns(); col++) {
                if (col == col1) {
                    coefficients[row][col2] = this.coefficients[row][col];
                } else if (col == col2) {
                    coefficients[row][col1] = this.coefficients[row][col];
                } else {
                    coefficients[row][col] = this.coefficients[row][col];
                }
            }
        }
        final int[] columnPositions = new int[this.columnPositions.length];
        System.arraycopy(this.columnPositions, 0, columnPositions, 0, this.columnPositions.length);
        columnPositions[col1] = this.columnPositions[col2];
        columnPositions[col2] = this.columnPositions[col1];
        return new Matrix(coefficients, columnPositions, this.separatorIndex);
    }

    public Matrix swapRows(final int row1, final int row2) {
        final BigFraction[][] coefficients = new BigFraction[this.getNumberOfRows()][this.getNumberOfColumns()];
        for (int row = 0; row < this.getNumberOfRows(); row++) {
            for (int col = 0; col < this.getNumberOfColumns(); col++) {
                if (row == row1) {
                    coefficients[row2][col] = this.coefficients[row][col];
                } else if (row == row2) {
                    coefficients[row1][col] = this.coefficients[row][col];
                } else {
                    coefficients[row][col] = this.coefficients[row][col];
                }
            }
        }
        return new Matrix(coefficients, this.columnPositions, this.separatorIndex);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        for (int row = 0; row < this.getNumberOfRows(); row++) {
            boolean first = true;
            for (int col = 0; col < this.separatorIndex; col++) {
                if (first) {
                    first = false;
                } else {
                    res.append(", ");
                }
                res.append(this.coefficients[row][col].toString());
            }
            if (this.separatorIndex == this.getNumberOfColumns()) {
                res.append("\n");
                continue;
            }
            res.append(" | ");
            first = true;
            for (int col = this.separatorIndex; col < this.getNumberOfColumns(); col++) {
                if (first) {
                    first = false;
                } else {
                    res.append(", ");
                }
                res.append(this.coefficients[row][col].toString());
            }
            res.append("\n");
        }
        return res.toString();
    }

}
