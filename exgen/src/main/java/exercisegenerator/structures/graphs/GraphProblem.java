package exercisegenerator.structures.graphs;

import java.util.*;

public record GraphProblem(
    GraphWithLayout<String, Integer, Integer> graphWithLayout,
    Vertex<String> startNode,
    Comparator<Vertex<String>> comparator
) {}
