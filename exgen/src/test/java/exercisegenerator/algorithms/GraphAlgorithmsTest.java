package exercisegenerator.algorithms;

import java.util.*;

import org.testng.*;
import org.testng.annotations.Test;

import exercisegenerator.structures.graphs.*;

public class GraphAlgorithmsTest {

    @Test
    public void depthFirstSearch() {
        final Vertex<String> start = new Vertex<String>("A");
        final AdjacencyLists<String, Integer> adjacencyLists = new AdjacencyLists<String, Integer>();
        adjacencyLists.put(start, Collections.emptyList());
        final Graph<String, Integer> graph = Graph.create(adjacencyLists);
        Assert.assertEquals(
            GraphAlgorithms.depthFirstSearch(graph, start, new StringVertexComparator()),
            List.of("A")
        );
    }

}
