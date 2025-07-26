package exercisegenerator.structures.graphs.flownetwork;

import java.util.*;

import exercisegenerator.structures.graphs.*;

public record FordFulkersonDoubleStep(
    GraphWithLayout<String, FlowAndCapacity, Integer> flowNetworkWithLayout,
    Set<FordFulkersonPathStep<String, FlowAndCapacity>> flowHighlights,
    GraphWithLayout<String, Integer, Integer> residualGraphWithLayout,
    Set<FordFulkersonPathStep<String, Integer>> residualHighlights
) {

    @Override
    public boolean equals(final Object o) {
        if (o instanceof FordFulkersonDoubleStep) {
            final FordFulkersonDoubleStep other = (FordFulkersonDoubleStep)o;
            return this.flowNetworkWithLayout().graph().logicallyEquals(other.flowNetworkWithLayout().graph())
                && this.residualGraphWithLayout().graph().logicallyEquals(other.residualGraphWithLayout().graph())
                && this.flowHighlights().containsAll(other.flowHighlights())
                && other.flowHighlights().containsAll(this.flowHighlights())
                && this.residualHighlights().containsAll(other.residualHighlights())
                && other.residualHighlights().containsAll(this.residualHighlights());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.flowNetworkWithLayout().graph().hashCode() * 3
            + this.residualGraphWithLayout().graph().hashCode() * 5
            + this.flowHighlights().hashCode() * 7
            + this.residualHighlights().hashCode() * 11;
    }

}
