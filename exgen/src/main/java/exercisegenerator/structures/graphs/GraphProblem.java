package exercisegenerator.structures.graphs;

import java.util.*;

public record GraphProblem(
    Graph<String, Integer> graph,
    Vertex<String> startNode,
    Comparator<Vertex<String>> comparator
) {}
