package exercisegenerator.structures.graphs.petrinets;

import java.util.*;

import exercisegenerator.structures.graphs.*;

public record GraphWithVertexMappings(
    Graph<String, Integer> graph,
    Map<Vertex<String>, Object> backwardsMapping,
    Map<Vertex<String>, Integer> placeIndex
) {

}
