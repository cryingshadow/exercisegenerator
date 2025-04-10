package exercisegenerator.structures.optimization;

import java.util.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.structures.algebra.*;

public record SimplexProblem(BigFraction[] target, Matrix conditions, List<Integer> integral) {

    @Override
    public boolean equals(final Object o) {
        if (o instanceof SimplexProblem) {
            final SimplexProblem other = (SimplexProblem)o;
            return this.conditions().equals(other.conditions())
                && Arrays.equals(this.target(), other.target())
                && this.integral().equals(other.integral());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.conditions().hashCode() * 3 + this.target().hashCode() * 5 + this.integral().hashCode() * 7 + 101;
    }

    @Override
    public String toString() {
        final StringBuilder matrixString = new StringBuilder();
        matrixString.append(this.conditions().toString());
        if (!this.integral().isEmpty()) {
            matrixString.append(String.format("Integral indices: %s\n", this.integral().toString()));
        }
        return String.format("Target: %s\nMatrix: %s", Arrays.toString(this.target()), matrixString.toString());
    }

    public boolean showsFeasibleSolution() {
        for (int row = 0; row < this.conditions().getNumberOfRows(); row++) {
            if (this.conditions().getLastCoefficientOfRow(row).compareTo(BigFraction.ZERO) < 0) {
                return false;
            }
        }
        return true;
    }

    public BigFraction getCurrentTargetValue(final int[] basicVariables) {
        BigFraction result = BigFraction.ZERO;
        for (int index = 0; index < this.target().length; index++) {
            result = result.add(this.target()[index].multiply(this.getCurrentValue(index, basicVariables)));
        }
        return result;
    }

    public BigFraction getCurrentValue(final int variableIndex, final int[] basicVariables) {
        int rowOfVariable = -1;
        for (int row = 0; row < basicVariables.length; row++) {
            if (basicVariables[row] == variableIndex) {
                rowOfVariable = row;
                break;
            }
        }
        if (rowOfVariable < 0) {
            return BigFraction.ZERO;
        }
        return this.conditions().getLastCoefficientOfRow(rowOfVariable);
    }

}
