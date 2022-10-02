package exercisegenerator.algorithms;

import java.util.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.structures.graphs.*;

public class GraphAlgorithmsTest {

    @DataProvider
    public Object[][] bfsData() {
        final Vertex<String> a = new Vertex<String>("A");
        final Vertex<String> b = new Vertex<String>("B");
        final Vertex<String> c = new Vertex<String>("C");
        final Vertex<String> d = new Vertex<String>("D");
        final Vertex<String> e = new Vertex<String>("E");
        final Vertex<String> f = new Vertex<String>("F");
        final Vertex<String> g = new Vertex<String>("G");
        final Vertex<String> h = new Vertex<String>("H");
        final Vertex<String> i = new Vertex<String>("I");
        final Vertex<String> j = new Vertex<String>("J");
        final AdjacencyLists<String, Integer> adjacencyLists1 = new AdjacencyLists<String, Integer>();
        adjacencyLists1.put(a, Collections.emptyList());
        final AdjacencyLists<String, Integer> adjacencyLists2 = new AdjacencyLists<String, Integer>();
        adjacencyLists2.addEdge(a, 0, b);
        adjacencyLists2.addEdge(a, 0, c);
        adjacencyLists2.addEdge(a, 0, d);
        final AdjacencyLists<String, Integer> adjacencyLists3 = new AdjacencyLists<String, Integer>();
        adjacencyLists3.addEdge(a, 0, b);
        adjacencyLists3.addEdge(a, 0, d);
        adjacencyLists3.addEdge(a, 0, c);
        adjacencyLists3.addEdge(b, 0, e);
        adjacencyLists3.addEdge(b, 0, f);
        adjacencyLists3.addEdge(c, 0, g);
        adjacencyLists3.addEdge(c, 0, h);
        adjacencyLists3.addEdge(d, 0, i);
        adjacencyLists3.addEdge(d, 0, j);
        adjacencyLists3.addEdge(e, 0, a);
        adjacencyLists3.addEdge(f, 0, a);
        adjacencyLists3.addEdge(g, 0, d);
        adjacencyLists3.addEdge(h, 0, c);
        adjacencyLists3.addEdge(i, 0, a);
        adjacencyLists3.addEdge(j, 0, j);
        return new Object[][] {
            {Graph.create(adjacencyLists1), a, List.of("A")},
            {Graph.create(adjacencyLists2), a, List.of("A", "B", "C", "D")},
            {Graph.create(adjacencyLists3), a, List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")}
        };
    }

    @Test(dataProvider="bfsData")
    public void breadthFirstSearch(
        final Graph<String, Integer> graph,
        final Vertex<String> start,
        final List<String> expected
    ) {
        Assert.assertEquals(GraphAlgorithms.breadthFirstSearch(graph, start, new StringVertexComparator()), expected);
    }

    @Test(dataProvider="dfsData")
    public void depthFirstSearch(
        final Graph<String, Integer> graph,
        final Vertex<String> start,
        final List<String> expected
    ) {
        Assert.assertEquals(GraphAlgorithms.depthFirstSearch(graph, start, new StringVertexComparator()), expected);
    }

    @DataProvider
    public Object[][] dfsData() {
        final Vertex<String> a = new Vertex<String>("A");
        final Vertex<String> b = new Vertex<String>("B");
        final Vertex<String> c = new Vertex<String>("C");
        final Vertex<String> d = new Vertex<String>("D");
        final Vertex<String> e = new Vertex<String>("E");
        final Vertex<String> f = new Vertex<String>("F");
        final Vertex<String> g = new Vertex<String>("G");
        final Vertex<String> h = new Vertex<String>("H");
        final Vertex<String> i = new Vertex<String>("I");
        final Vertex<String> j = new Vertex<String>("J");
        final AdjacencyLists<String, Integer> adjacencyLists1 = new AdjacencyLists<String, Integer>();
        adjacencyLists1.put(a, Collections.emptyList());
        final AdjacencyLists<String, Integer> adjacencyLists2 = new AdjacencyLists<String, Integer>();
        adjacencyLists2.addEdge(a, 0, b);
        adjacencyLists2.addEdge(a, 0, c);
        adjacencyLists2.addEdge(a, 0, d);
        final AdjacencyLists<String, Integer> adjacencyLists3 = new AdjacencyLists<String, Integer>();
        adjacencyLists3.addEdge(a, 0, b);
        adjacencyLists3.addEdge(a, 0, d);
        adjacencyLists3.addEdge(a, 0, c);
        adjacencyLists3.addEdge(b, 0, e);
        adjacencyLists3.addEdge(b, 0, f);
        adjacencyLists3.addEdge(c, 0, g);
        adjacencyLists3.addEdge(c, 0, h);
        adjacencyLists3.addEdge(d, 0, i);
        adjacencyLists3.addEdge(d, 0, j);
        adjacencyLists3.addEdge(e, 0, a);
        adjacencyLists3.addEdge(f, 0, a);
        adjacencyLists3.addEdge(g, 0, d);
        adjacencyLists3.addEdge(h, 0, c);
        adjacencyLists3.addEdge(i, 0, a);
        adjacencyLists3.addEdge(j, 0, j);
        return new Object[][] {
            {Graph.create(adjacencyLists1), a, List.of("A")},
            {Graph.create(adjacencyLists2), a, List.of("A", "B", "C", "D")},
            {Graph.create(adjacencyLists3), a, List.of("A", "B", "E", "F", "C", "G", "D", "I", "J", "H")}
        };
    }

}
