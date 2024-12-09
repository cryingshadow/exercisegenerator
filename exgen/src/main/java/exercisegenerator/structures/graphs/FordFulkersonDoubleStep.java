package exercisegenerator.structures.graphs;

import java.util.*;

public record FordFulkersonDoubleStep(
    Graph<String, FlowPair> flowNetwork,
    Set<FordFulkersonPathStep<String, FlowPair>> flowHighlights,
    Graph<String, Integer> residualGraph,
    Set<FordFulkersonPathStep<String, Integer>> residualHighlights
) {

    @Override
    public boolean equals(final Object o) {
        if (o instanceof FordFulkersonDoubleStep) {
            final FordFulkersonDoubleStep other = (FordFulkersonDoubleStep)o;
            boolean result = this.flowNetwork().logicallyEquals(other.flowNetwork());
            result &= this.residualGraph().logicallyEquals(other.residualGraph());
            result &= this.flowHighlights().containsAll(other.flowHighlights());
            result &= other.flowHighlights().containsAll(this.flowHighlights());
            result &= this.residualHighlights().containsAll(other.residualHighlights());
            result &= other.residualHighlights().containsAll(this.residualHighlights());
            return result;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.flowNetwork().hashCode() * 3
            + this.residualGraph().hashCode() * 5
            + this.flowHighlights().hashCode() * 7
            + this.residualHighlights().hashCode() * 11;
    }

}
