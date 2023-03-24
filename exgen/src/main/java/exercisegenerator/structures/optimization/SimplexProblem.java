package exercisegenerator.structures.optimization;

import java.util.*;

public class SimplexProblem {

    public final double[][] matrix;
    public final double[] target;

    public SimplexProblem(final double[] target, final double[][] matrix) {
        this.target = target;
        this.matrix = matrix;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof SimplexProblem) {
            final SimplexProblem other = (SimplexProblem)o;
            return Arrays.deepEquals(this.matrix, other.matrix) && Arrays.equals(this.target, other.target);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.matrix.hashCode() * 3 + this.target.hashCode() * 5 + 101;
    }

    @Override
    public String toString() {
        final StringBuilder matrixString = new StringBuilder();
        for (int row = 0; row < this.matrix.length; row++) {
            boolean first = true;
            for (int col = 0; col < this.matrix[row].length; col++) {
                if (first) {
                    first = false;
                } else {
                    matrixString.append(", ");
                }
                matrixString.append(this.matrix[row][col]);
            }
            matrixString.append("\n");
        }
        return String.format("Target: %s\nMatrix: %s", Arrays.toString(this.target), matrixString.toString());
    }

}
