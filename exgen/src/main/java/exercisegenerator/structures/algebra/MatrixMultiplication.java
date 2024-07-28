package exercisegenerator.structures.algebra;

import org.apache.commons.math3.fraction.*;

public record MatrixMultiplication(MatrixTerm left, MatrixTerm right) implements MatrixTerm {

    @Override
    public boolean isCompound() {
        return true;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof MatrixMultiplication) {
            final MatrixMultiplication other = (MatrixMultiplication)o;
            return this.left.equals(other.left) && this.right.equals(other.right);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 3 + this.left.hashCode() * 2 + this.right.hashCode() * 3;
    }

    @Override
    public Matrix toMatrix() {
        final Matrix leftMatrix = this.left.toMatrix();
        final Matrix rightMatrix = this.right.toMatrix();
        if (
            leftMatrix.separatorIndex != leftMatrix.getNumberOfColumns()
            || rightMatrix.separatorIndex != rightMatrix.getNumberOfColumns()
        ) {
            throw new IllegalArgumentException("Matrices to multiply must not be split!");
        }
        if (leftMatrix.getNumberOfColumns() != rightMatrix.getNumberOfRows()) {
            throw new IllegalArgumentException("Dimensions of matrices to multiply are not compatible!");
        }
        final Matrix result =
            new Matrix(
                rightMatrix.getNumberOfColumns(),
                leftMatrix.getNumberOfRows(),
                rightMatrix.getNumberOfColumns()
            );
        for (int row = 0; row < leftMatrix.getNumberOfRows(); row++) {
            for (int column = 0; column < rightMatrix.getNumberOfColumns(); column++) {
                BigFraction coefficient = BigFraction.ZERO;
                for (int i = 0; i < leftMatrix.getNumberOfColumns(); i++) {
                    coefficient =
                        coefficient.add(
                            leftMatrix.getCoefficient(i, row).multiply(rightMatrix.getCoefficient(column, i))
                        );
                }
                result.setCoefficient(column, row, coefficient);
            }
        }
        return result;
    }

    @Override
    public boolean isDirectlyEvaluable() {
        return !this.left.isCompound() && !this.right.isCompound();
    }

    @Override
    public String toLaTeX() {
        return String.format("%s \\cdot %s", this.left.toCompoundLaTeX(), this.right.toCompoundLaTeX());
    }

}
