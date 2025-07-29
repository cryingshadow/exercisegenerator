package exercisegenerator.structures.algebra;

import java.util.*;
import java.util.stream.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.util.*;

public class Matrix implements MatrixTerm {

    public final int[] columnPositions;

    public final int separatorIndex;

    private final BigFraction[][] coefficients;

    public Matrix(final BigFraction[][] coefficients, final int separatorIndex) {
        this(
            coefficients,
            IntStream.range(0, coefficients.length == 0 ? 0 : coefficients[0].length).toArray(),
            separatorIndex
        );
    }

    public Matrix(final BigFraction[][] coefficients, final int[] columnPositions, final int separatorIndex) {
        this.coefficients = coefficients;
        this.columnPositions = columnPositions;
        this.separatorIndex = separatorIndex;
    }

    public Matrix(final int numberOfColumns, final int numberOfRows, final int separatorIndex) {
        this(new BigFraction[numberOfRows][numberOfColumns], separatorIndex);
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

    public Matrix copy() {
        return new Matrix(
            ArrayUtils.copy(this.coefficients),
            ArrayUtils.copy(this.columnPositions),
            this.separatorIndex
        );
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

    public BigFraction getCoefficient(final int column, final int row) {
        return this.coefficients[row][column];
    }

    public int getIndexOfLastColumn() {
        return this.getNumberOfColumns() - 1;
    }

    public int getIndexOfLastRow() {
        return this.getNumberOfRows() - 1;
    }

    public BigFraction getLastCoefficientOfColumn(final int column) {
        return this.getCoefficient(column, this.getIndexOfLastRow());
    }

    public BigFraction getLastCoefficientOfRow(final int row) {
        return this.getCoefficient(this.getIndexOfLastColumn(), row);
    }

    public int getNumberOfColumns() {
        return this.coefficients.length == 0 ? 0 : this.coefficients[0].length;
    }

    public int getNumberOfRows() {
        return this.coefficients.length;
    }

    @Override
    public int hashCode() {
        return 666 + this.coefficients.hashCode() * 2 + this.columnPositions.hashCode() * 3;
    }

    public Matrix insertColumnsAtIndex(final List<BigFraction[]> columns, final int firstIndex) {
        final int additionalColumns = columns.size();
        final BigFraction[][] coefficients =
            new BigFraction[this.getNumberOfRows()][this.getNumberOfColumns() + additionalColumns];
        for (int column = 0; column < this.getNumberOfColumns(); column++) {
            final int columnIndex = column < firstIndex ? column : column + additionalColumns;
            for (int row = 0; row < this.getNumberOfRows(); row++) {
                coefficients[row][columnIndex] = this.getCoefficient(column, row);
            }
        }
        for (int columnIndex = firstIndex; columnIndex < additionalColumns + firstIndex; columnIndex++) {
            final BigFraction[] column = columns.get(columnIndex - firstIndex);
            for (int row = 0; row < this.getNumberOfRows(); row++) {
                coefficients[row][columnIndex] = column[row];
            }
        }
        final int[] columnPositions = new int[coefficients.length == 0 ? 0 : coefficients[0].length];
        for (int column = 0; column < columnPositions.length; column++) {
            if (column < firstIndex) {
                columnPositions[column] =
                    this.columnPositions[column] < firstIndex ?
                        this.columnPositions[column] :
                            this.columnPositions[column] + additionalColumns;
            } else if (column > firstIndex + additionalColumns) {
                columnPositions[column] =
                    this.columnPositions[column - additionalColumns] < firstIndex ?
                        this.columnPositions[column - additionalColumns] :
                            this.columnPositions[column - additionalColumns] + additionalColumns;
            } else {
                columnPositions[column] = column;
            }
        }
        return new Matrix(
            coefficients,
            columnPositions,
            firstIndex < this.separatorIndex ? this.separatorIndex + additionalColumns: this.separatorIndex
        );
    }

    public Matrix insertRowsAtIndex(final List<BigFraction[]> rows, final int firstIndex) {
        final int additionalRows = rows.size();
        final BigFraction[][] coefficients =
            new BigFraction[this.getNumberOfRows() + additionalRows][this.getNumberOfColumns()];
        for (int row = 0; row < this.getNumberOfRows(); row++) {
            final int rowIndex = row < firstIndex ? row : row + additionalRows;
            for (int column = 0; column < this.getNumberOfColumns(); column++) {
                coefficients[rowIndex][column] = this.getCoefficient(column, row);
            }
        }
        for (int rowIndex = firstIndex; rowIndex < additionalRows + firstIndex; rowIndex++) {
            final BigFraction[] row = rows.get(rowIndex - firstIndex);
            for (int column = 0; column < this.getNumberOfColumns(); column++) {
                coefficients[rowIndex][column] = row[column];
            }
        }
        return new Matrix(coefficients, this.columnPositions, this.separatorIndex);
    }

    @Override
    public boolean isCompound() {
        return false;
    }

    @Override
    public boolean isDirectlyEvaluable() {
        return false;
    }

    public boolean isIdentityMatrix(final int from, final int to) {
        for (int row = from; row < to; row++) {
            for (int column = from; column < to; column++) {
                if (row == column) {
                    if (!this.isOne(column, row)) {
                        return false;
                    }
                } else if (!this.isZero(column, row)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isNegative(final int column, final int row) {
        return this.getCoefficient(column, row).compareTo(BigFraction.ZERO) < 0;
    }

    public boolean isNonNegative(final int column, final int row) {
        return this.getCoefficient(column, row).compareTo(BigFraction.ZERO) >= 0;
    }

    public boolean isNonPositive(final int column, final int row) {
        return this.getCoefficient(column, row).compareTo(BigFraction.ZERO) <= 0;
    }

    public boolean isOne(final int column, final int row) {
        return this.getCoefficient(column, row).compareTo(BigFraction.ONE) == 0;
    }

    public boolean isPositive(final int column, final int row) {
        return this.getCoefficient(column, row).compareTo(BigFraction.ZERO) > 0;
    }

    public boolean isZero(final int column, final int row) {
        return this.getCoefficient(column, row).compareTo(BigFraction.ZERO) == 0;
    }

    public boolean isZeroOnTheRight(final int row) {
        for (int column = this.separatorIndex; column < this.getNumberOfColumns(); column++) {
            if (!this.isZero(column, row)) {
                return false;
            }
        }
        return true;
    }

    public boolean isZeroRow(final int row) {
        for (int column = 0; column < this.getNumberOfColumns(); column++) {
            if (this.getCoefficient(column, row).compareTo(BigFraction.ZERO) != 0) {
                return false;
            }
        }
        return true;
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

    public Matrix reduceToBase() {
        Matrix result = this;
        int rank = 0;
        for (int column = 0; column < result.getNumberOfColumns(); column++) {
            final int firstNonZeroRow = result.findFirstNonZeroEntryFromIndex(rank, column);
            if (firstNonZeroRow < 0) {
                continue;
            }
            if (firstNonZeroRow > rank) {
                result = result.swapRows(rank, firstNonZeroRow);
            }
            result = result.multiplyRow(rank, result.getCoefficient(column, rank).reciprocal());
            for (int row = rank + 1; row < result.getNumberOfRows(); row++) {
                result = result.addRow(row, rank, result.getCoefficient(column, row).negate());
            }
            rank++;
        }
        for (int row = result.getNumberOfRows() - 1; row >= 1; row--) {
            if (result.isZeroRow(row)) {
                result = result.removeRowsFromIndex(1, row);
            }
        }
        return result;
    }

    public Matrix removeColumnsFromIndex(final int numberOfColumns, final int firstIndex) {
        final BigFraction[][] coefficients =
            new BigFraction[this.getNumberOfRows()][this.getNumberOfColumns() - numberOfColumns];
        final int newNumberOfColumns = coefficients.length == 0 ? 0 : coefficients[0].length;
        for (int column = 0; column < newNumberOfColumns; column++) {
            final int columnIndex = column < firstIndex ? column : column + numberOfColumns;
            for (int row = 0; row < this.getNumberOfRows(); row++) {
                coefficients[row][column] = this.getCoefficient(columnIndex, row);
            }
        }
        final int[] columnPositions = new int[newNumberOfColumns];
        final List<Integer> removedPositions = new LinkedList<Integer>();
        for (int index = firstIndex; index < firstIndex + numberOfColumns; index++) {
            removedPositions.add(this.columnPositions[index]);
        }
        for (int column = 0; column < columnPositions.length; column++) {
            if (column < firstIndex) {
                columnPositions[column] = this.columnPositions[column];
            } else {
                columnPositions[column] = this.columnPositions[column + numberOfColumns];
            }
            int reduce = 0;
            for (final Integer removed : removedPositions) {
                if (removed <= columnPositions[column]) {
                    reduce++;
                }
            }
            columnPositions[column] -= reduce;
        }
        return new Matrix(coefficients, columnPositions, this.separatorIndex - numberOfColumns);
    }

    public Matrix removeRowsFromIndex(final int numberOfRows, final int firstIndex) {
        final BigFraction[][] coefficients =
            new BigFraction[this.getNumberOfRows() - numberOfRows][this.getNumberOfColumns()];
        for (int row = 0; row < coefficients.length; row++) {
            final int rowIndex = row < firstIndex ? row : row + numberOfRows;
            for (int column = 0; column < this.getNumberOfColumns(); column++) {
                coefficients[row][column] = this.getCoefficient(column, rowIndex);
            }
        }
        return new Matrix(coefficients, this.columnPositions, this.separatorIndex);
    }

    public void setCoefficient(final int column, final int row, final BigFraction coefficient) {
        this.coefficients[row][column] = coefficient;
    }

    public Matrix setSeparatorIndex(final int separatorIndex) {
        return new Matrix(this.coefficients, this.columnPositions, separatorIndex);
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
    public String toLaTeX() {
        final StringBuilder result = new StringBuilder();
        result.append("\\left(\\begin{array}{*{");
        result.append(String.valueOf(this.separatorIndex));
        result.append("}c");
        if (this.separatorIndex < this.getNumberOfColumns()) {
            result.append("|");
            result.append("*{");
            result.append(String.valueOf(this.getNumberOfColumns() - this.separatorIndex));
            result.append("}c");
        }
        result.append("}");
        result.append(Main.lineSeparator);
        for (int row = 0; row < this.getNumberOfRows(); row++) {
            boolean first = true;
            for (int column = 0; column < this.getNumberOfColumns(); column++) {
                if (first) {
                    first = false;
                } else {
                    result.append(" & ");
                }
                result.append(LaTeXUtils.toCoefficient(this.getCoefficient(column, row)));
            }
            result.append("\\\\");
            result.append(Main.lineSeparator);
        }
        result.append("\\end{array}\\right)");
        return result.toString();
    }

    @Override
    public Matrix toMatrix() {
        return this;
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

    private int findFirstNonZeroEntryFromIndex(final int rank, final int column) {
        for (int row = rank; row < this.getNumberOfRows(); row++) {
            if (this.getCoefficient(column, row).compareTo(BigFraction.ZERO) != 0) {
                return row;
            }
        }
        return -1;
    }

}
