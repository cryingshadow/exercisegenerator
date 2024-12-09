package exercisegenerator.structures.graphs;

public record FlowNetworkProblem(Graph<String, FlowPair> graph, Vertex<String> source, Vertex<String> sink) {}
