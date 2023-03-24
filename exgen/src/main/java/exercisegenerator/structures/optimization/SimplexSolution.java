package exercisegenerator.structures.optimization;

import java.util.*;
import java.util.stream.*;

public class SimplexSolution {

    public final SimplexAnswer answer;

    public final List<SimplexTableau> tableaus;

    public SimplexSolution(final List<SimplexTableau> tableaus, final SimplexAnswer answer) {
        this.tableaus = tableaus;
        this.answer = answer;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof SimplexSolution) {
            final SimplexSolution other = (SimplexSolution)o;
            return this.answer.equals(other.answer) && this.tableaus.equals(other.tableaus);
        }
        return false;
    }

    public double[] getOptimalAssignment() {
        if (this.answer == SimplexAnswer.SOLVED) {

        }
        return null;
    }

    @Override
    public int hashCode() {
        return 3 * this.answer.hashCode() + 2 * this.tableaus.hashCode() + 23;
    }

    @Override
    public String toString() {
        return String.format(
            "%s\n\n%s",
            this.tableaus.stream().map(SimplexTableau::toString).collect(Collectors.joining("\n\n")),
            this.answer.toString()
        );
    }

}
