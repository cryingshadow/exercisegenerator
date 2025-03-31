package exercisegenerator.structures.optimization;

import java.math.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.structures.*;

public record SimplexTableau(
    SimplexProblem problem,
    int[] baseVariables,
    BigFraction[] quotients,
    int pivotRow,
    int pivotColumn
) {

    @Override
    public boolean equals(final Object o) {
        if (o instanceof SimplexTableau) {
            final SimplexTableau other = (SimplexTableau)o;
            if (this.quotients() == null) {
                if (other.quotients() != null) {
                    return false;
                }
            } else if (!Arrays.equals(this.quotients(), other.quotients())) {
                return false;
            }
            return this.pivotColumn() == other.pivotColumn()
                && this.pivotRow() == other.pivotRow()
                && this.problem().equals(other.problem())
                && Arrays.equals(this.baseVariables(), other.baseVariables());
        }
        return false;
    }

    public Optional<Pair<Integer, BigFraction>> getIntegralViolation() {
        for (final Integer index : this.problem().integral()) {
            final BigFraction currentValue = this.getCurrentValue(index);
            if (!SimplexTableau.isIntegral(currentValue)) {
                return Optional.of(new Pair<Integer, BigFraction>(index, currentValue));
            }
        }
        return Optional.empty();
    }

    private static boolean isIntegral(final BigFraction value) {
        return value.getDenominator().equals(BigInteger.ONE);
    }

    @Override
    public int hashCode() {
        return 5
            + this.pivotColumn() * 17
            + this.pivotRow() * 23
            + this.baseVariables().hashCode() * 7
            + (this.quotients() == null ? 0 : this.quotients().hashCode() * 5)
            + this.problem().hashCode() * 3;
    }

    public Optional<List<BigFraction>> getResult() {
        if (!this.problem().showsFeasibleSolution()) {
            return Optional.empty();
        }
        final List<BigFraction> result = new ArrayList<BigFraction>();
        for (int i = 0; i < this.problem().target().length; i++) {
            result.add(this.getCurrentValue(i));
        }
        result.add(this.problem().getCurrentTargetValue(this.baseVariables()));
        return Optional.of(result);
    }

    public BigFraction getCurrentValue(final int variableIndex) {
        return this.problem().getCurrentValue(variableIndex, this.baseVariables());
    }

    @Override
    public String toString() {
        return String.format(
            "Problem: %s\nBasis: %s\nQuotients: %s\nPivot column: %d\nPivot row: %d",
            this.problem().toString(),
            Arrays.toString(this.baseVariables()),
            Arrays.toString(this.quotients()),
            this.pivotColumn(),
            this.pivotRow()
        );
    }

}
