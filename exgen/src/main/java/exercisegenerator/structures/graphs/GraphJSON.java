package exercisegenerator.structures.graphs;

import java.util.*;

import exercisegenerator.structures.graphs.layout.*;

public record GraphJSON(
    Map<String, Coordinates2D<Integer>> nodes,
    List<EdgeJSON> edges
) {

}
