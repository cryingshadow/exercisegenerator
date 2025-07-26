package exercisegenerator.structures.graphs.flownetwork;

import exercisegenerator.structures.graphs.*;

public record FlowNetworkProblem(
    GraphWithLayout<String, FlowAndCapacity, Integer> graphWithLayout,
    Vertex<String> source,
    Vertex<String> sink
) {}
