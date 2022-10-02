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

    @Test(dataProvider="dijkstraData")
    public void dijkstra(
        final Graph<String, Integer> graph,
        final Vertex<String> start,
        final DijkstraTables expected
    ) {
        Assert.assertEquals(GraphAlgorithms.dijkstra(graph, start, new StringVertexComparator()), expected);
    }

    @DataProvider
    public Object[][] dijkstraData() {
        final Vertex<String> a = new Vertex<String>("A");
        final Vertex<String> b = new Vertex<String>("B");
        final Vertex<String> c = new Vertex<String>("C");
        final Vertex<String> d = new Vertex<String>("D");
        final Vertex<String> e = new Vertex<String>("E");
        final Vertex<String> f = new Vertex<String>("F");
        final Vertex<String> g = new Vertex<String>("G");
        final AdjacencyLists<String, Integer> adjacencyLists1 = new AdjacencyLists<String, Integer>();
        adjacencyLists1.addEdge(a, 1, b);
        adjacencyLists1.addEdge(a, 7, d);
        adjacencyLists1.addEdge(b, 6, c);
        adjacencyLists1.addEdge(b, 1, e);
        adjacencyLists1.addEdge(c, 1, b);
        adjacencyLists1.addEdge(c, 1, f);
        adjacencyLists1.addEdge(d, 1, a);
        adjacencyLists1.addEdge(d, 5, g);
        adjacencyLists1.addEdge(e, 2, b);
        adjacencyLists1.addEdge(e, 3, d);
        adjacencyLists1.addEdge(e, 4, f);
        adjacencyLists1.addEdge(e, 9, g);
        adjacencyLists1.addEdge(f, 1, c);
        adjacencyLists1.addEdge(g, 1, d);
        return new Object[][] {
            {
                Graph.create(adjacencyLists1),
                e,
                new DijkstraTables(
                    new String[][] {
                        {"\\textbf{Knoten}", "\\textbf{E}", "", "", "", "", ""},
                        {"\\textbf{A}", "", "", "", "", "", ""},
                        {"\\textbf{B}", "", "", "", "", "", ""},
                        {"\\textbf{C}", "", "", "", "", "", ""},
                        {"\\textbf{D}", "", "", "", "", "", ""},
                        {"\\textbf{F}", "", "", "", "", "", ""},
                        {"\\textbf{G}", "", "", "", "", "", ""}
                    },
                    new String[][] {
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null}
                    },
                    new String[][] {
                        {"\\textbf{Knoten}", "\\textbf{E}", "\\textbf{B}", "\\textbf{D}", "\\textbf{A}", "\\textbf{F}", "\\textbf{C}"},
                        {"\\textbf{A}", "$\\infty$",    "$\\infty$",            "4", "\\textbf{--}", "\\textbf{--}", "\\textbf{--}"},
                        {"\\textbf{B}",         "2", "\\textbf{--}", "\\textbf{--}", "\\textbf{--}", "\\textbf{--}", "\\textbf{--}"},
                        {"\\textbf{C}", "$\\infty$",            "8",            "8",            "8",            "5", "\\textbf{--}"},
                        {"\\textbf{D}",         "3",            "3", "\\textbf{--}", "\\textbf{--}", "\\textbf{--}", "\\textbf{--}"},
                        {"\\textbf{F}",         "4",            "4",            "4",            "4", "\\textbf{--}", "\\textbf{--}"},
                        {"\\textbf{G}",         "9",            "9",            "8",            "8",            "8",            "8"}
                    },
                    new String[][] {
                        {null,       null,       null,       null,       null,       null,       null},
                        {null,       null,       null, "black!20",       null,       null,       null},
                        {null, "black!20",       null,       null,       null,       null,       null},
                        {null,       null,       null,       null,       null, "black!20",       null},
                        {null,       null, "black!20",       null,       null,       null,       null},
                        {null,       null,       null,       null, "black!20",       null,       null},
                        {null,       null,       null,       null,       null,       null, "black!20"}
                    },
                    true
                )
            }
        };
    }

}
