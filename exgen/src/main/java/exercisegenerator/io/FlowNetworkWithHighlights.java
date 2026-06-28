package exercisegenerator.io;

import java.util.*;

import exercisegenerator.structures.graphs.*;
import exercisegenerator.structures.graphs.flownetwork.*;

public record FlowNetworkWithHighlights(
    Graph<String, FlowAndCapacity> graph,
    Set<FordFulkersonPathStep<String, FlowAndCapacity>> highlights
) {

}
