package exercisegenerator.structures.graphs.flownetwork;

import exercisegenerator.structures.graphs.*;

public record FlowNetworkProblem(
    GraphWithLayout<String, FlowAndCapacity> graphWithLayout,
    Vertex<String> source,
    Vertex<String> sink
) {}
