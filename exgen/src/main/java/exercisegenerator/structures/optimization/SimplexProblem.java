package exercisegenerator.structures.optimization;

import java.util.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.structures.algebra.*;

public class SimplexProblem {

    public final Matrix conditions;
    public final BigFraction[] target;

    public SimplexProblem(final BigFraction[] target, final Matrix conditions) {
        this.target = target;
        this.conditions = conditions;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof SimplexProblem) {
            final SimplexProblem other = (SimplexProblem)o;
            return this.conditions.equals(other.conditions) && Arrays.equals(this.target, other.target);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.conditions.hashCode() * 3 + this.target.hashCode() * 5 + 101;
    }

    @Override
    public String toString() {
        final StringBuilder matrixString = new StringBuilder();
        for (int row = 0; row < this.conditions.getNumberOfRows(); row++) {
            boolean first = true;
            for (int column = 0; column < this.conditions.getNumberOfColumns(); column++) {
                if (first) {
                    first = false;
                } else {
                    matrixString.append(", ");
                }
                matrixString.append(this.conditions.getCoefficient(column, row));
            }
            matrixString.append("\n");
        }
        return String.format("Target: %s\nMatrix: %s", Arrays.toString(this.target), matrixString.toString());
    }

}
