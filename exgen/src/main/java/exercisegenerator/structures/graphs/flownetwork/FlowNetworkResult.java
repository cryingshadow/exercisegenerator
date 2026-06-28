package exercisegenerator.structures.graphs.flownetwork;

import java.util.*;

import exercisegenerator.structures.graphs.*;

public record FlowNetworkResult(List<FordFulkersonDoubleStep> steps, Set<Vertex<String>> sourcePartition) {

}
