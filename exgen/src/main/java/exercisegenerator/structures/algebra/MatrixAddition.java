package exercisegenerator.structures.algebra;

public record MatrixAddition(MatrixTerm left, MatrixTerm right) implements MatrixTerm {

    @Override
    public boolean isCompound() {
        return true;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof MatrixAddition) {
            final MatrixAddition other = (MatrixAddition)o;
            return this.left.equals(other.left) && this.right.equals(other.right);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 5 + this.left.hashCode() * 2 + this.right.hashCode() * 3;
    }

    @Override
    public Matrix toMatrix() {
        final Matrix leftMatrix = this.left.toMatrix();
        final Matrix rightMatrix = this.right.toMatrix();
        if (
            leftMatrix.separatorIndex != leftMatrix.getNumberOfColumns()
            || rightMatrix.separatorIndex != rightMatrix.getNumberOfColumns()
        ) {
            throw new IllegalArgumentException("Matrices to add must not be split!");
        }
        if (
            leftMatrix.getNumberOfColumns() != rightMatrix.getNumberOfColumns()
            || leftMatrix.getNumberOfRows() != rightMatrix.getNumberOfRows()
        ) {
            throw new IllegalArgumentException("Dimensions of matrices to add do not match!");
        }
        final Matrix result =
            new Matrix(leftMatrix.getNumberOfColumns(), leftMatrix.getNumberOfRows(), leftMatrix.getNumberOfColumns());
        for (int row = 0; row < leftMatrix.getNumberOfRows(); row++) {
            for (int column = 0; column < leftMatrix.getNumberOfColumns(); column++) {
                result.setCoefficient(
                    column,
                    row,
                    leftMatrix.getCoefficient(column, row).add(rightMatrix.getCoefficient(column, row))
                );
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
        return String.format("%s + %s", this.left.toCompoundLaTeX(), this.right.toCompoundLaTeX());
    }

}
