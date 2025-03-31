package exercisegenerator.structures.optimization;

import java.util.*;
import java.util.stream.*;

import org.apache.commons.math3.fraction.*;

public record SimplexSolution(List<List<SimplexTableau>> branches, SimplexAnswer answer) {

    @Override
    public boolean equals(final Object o) {
        if (o instanceof SimplexSolution) {
            final SimplexSolution other = (SimplexSolution)o;
            return this.answer().equals(other.answer()) && this.branches().equals(other.branches());
        }
        return false;
    }

    public Optional<List<BigFraction>> getOptimalResult() {
        Optional<List<BigFraction>> bestResult = Optional.empty();
        for (final List<SimplexTableau> branch : this.branches()) {
            final Optional<List<BigFraction>> currentResult = branch.getLast().getResult();
            if (currentResult.isPresent()) {
                if (bestResult.isEmpty() || bestResult.get().getLast().compareTo(currentResult.get().getLast()) < 0) {
                    bestResult = currentResult;
                }
            }
        }
        return bestResult;
    }

    @Override
    public int hashCode() {
        return 3 * this.answer().hashCode() + 2 * this.branches().hashCode() + 23;
    }

    @Override
    public String toString() {
        return String.format(
            "%s\n\n%s",
            this.branches().stream()
            .map(branch -> branch.stream().map(SimplexTableau::toString).collect(Collectors.joining("\n\n")))
            .collect(Collectors.joining("\n\n----------\n\n")),
            this.answer().toString()
        );
    }

}
