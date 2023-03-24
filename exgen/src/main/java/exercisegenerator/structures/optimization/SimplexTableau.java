package exercisegenerator.structures.optimization;

import java.util.*;

public class SimplexTableau {

    public final int[] basicVariables;
    public final int pivotColumn;
    public final int pivotRow;
    public final SimplexProblem problem;
    public final Double[] quotients;

    public SimplexTableau(
        final SimplexProblem problem,
        final int[] basicVariables,
        final Double[] quotients,
        final int pivotRow,
        final int pivotColumn
    ) {
        this.problem = problem;
        this.basicVariables = basicVariables;
        this.quotients = quotients;
        this.pivotRow = pivotRow;
        this.pivotColumn = pivotColumn;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof SimplexTableau) {
            final SimplexTableau other = (SimplexTableau)o;
            if (this.quotients == null) {
                if (other.quotients != null) {
                    return false;
                }
            } else if (!Arrays.equals(this.quotients, other.quotients)) {
                return false;
            }
            return this.pivotColumn == other.pivotColumn
                && this.pivotRow == other.pivotRow
                && this.problem.equals(other.problem)
                && Arrays.equals(this.basicVariables, other.basicVariables);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 5
            + this.pivotColumn * 17
            + this.pivotRow * 23
            + this.basicVariables.hashCode() * 7
            + (this.quotients == null ? 0 : this.quotients.hashCode() * 5)
            + this.problem.hashCode() * 3;
    }

    @Override
    public String toString() {
        return String.format(
            "Problem: %s\nBasis: %s\nQuotients: %s\nPivot column: %d\nPivot row: %d",
            this.problem.toString(),
            Arrays.toString(this.basicVariables),
            Arrays.toString(this.quotients),
            this.pivotColumn,
            this.pivotRow
        );
    }

}
