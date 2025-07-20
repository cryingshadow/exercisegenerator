package exercisegenerator.algorithms.graphs;

import java.util.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.structures.graphs.*;

public class GraphAlgorithmsTest {

    @Test(dataProvider="bellmanFordData")
    public void bellmanFord(final GraphProblem problem, final List<BellmanFordStep<String>> expected) {
        Assert.assertEquals(BellmanFordAlgorithm.INSTANCE.apply(problem), expected);
    }

    @DataProvider
    public Object[][] bellmanFordData() {
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
        final BellmanFordStep<String> lastStep =
            new BellmanFordStep<String>(
                Map.of(
                    "E", 0,
                    "A", 4,
                    "B", 2,
                    "C", 5,
                    "D", 3,
                    "F", 4,
                    "G", 8
                ),
                Map.of(
                    "A", "D",
                    "B", "E",
                    "C", "F",
                    "D", "E",
                    "F", "E",
                    "G", "D"
                )
            );
        return new Object[][] {
            {
                new GraphProblem(
                    Graph.create(adjacencyLists1),
                    e,
                    StringVertexComparator.INSTANCE
                ),
                List.of(
                    new BellmanFordStep<String>(Map.of("E", 0), Map.of()),
                    new BellmanFordStep<String>(
                        Map.of(
                            "E", 0,
                            "B", 2,
                            "C", 5,
                            "D", 3,
                            "F", 4,
                            "G", 9
                        ),
                        Map.of(
                            "B", "E",
                            "C", "F",
                            "D", "E",
                            "F", "E",
                            "G", "E"
                        )
                    ),
                    lastStep,
                    lastStep
                )
            }
        };
    }

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
            {new GraphProblem(Graph.create(adjacencyLists1), a, StringVertexComparator.INSTANCE), List.of("A")},
            {
                new GraphProblem(Graph.create(adjacencyLists2), a, StringVertexComparator.INSTANCE),
                List.of("A", "B", "C", "D")
            },
            {
                new GraphProblem(Graph.create(adjacencyLists3), a, StringVertexComparator.INSTANCE),
                List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
            }
        };
    }

    @Test(dataProvider="bfsData")
    public void breadthFirstSearch(final GraphProblem problem, final List<String> expected) {
        Assert.assertEquals(BreadthFirstSearch.INSTANCE.apply(problem), expected);
    }

    @Test(dataProvider="dfsData")
    public void depthFirstSearch(final GraphProblem problem, final List<String> expected) {
        Assert.assertEquals(DepthFirstSearch.INSTANCE.apply(problem), expected);
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
            {new GraphProblem(Graph.create(adjacencyLists1), a, StringVertexComparator.INSTANCE), List.of("A")},
            {
                new GraphProblem(Graph.create(adjacencyLists2), a, StringVertexComparator.INSTANCE),
                List.of("A", "B", "C", "D")
            },
            {
                new GraphProblem(Graph.create(adjacencyLists3), a, StringVertexComparator.INSTANCE),
                List.of("A", "B", "E", "F", "C", "G", "D", "I", "J", "H")
            }
        };
    }

    @Test(dataProvider="dijkstraData")
    public void dijkstra(final GraphProblem problem, final DijkstraTables expected) {
        Assert.assertEquals(DijkstraAlgorithm.INSTANCE.apply(problem), expected);
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
                new GraphProblem(
                    Graph.create(adjacencyLists1),
                    e,
                    StringVertexComparator.INSTANCE
                ),
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

    @Test(dataProvider="floydData")
    public void floyd(final GraphProblem problem, final Integer[][][] expected) {
        Assert.assertEquals(FloydAlgorithm.INSTANCE.apply(problem), expected);
    }

    @DataProvider
    public Object[][] floydData() {
        final Vertex<String> a = new Vertex<String>("A");
        final Vertex<String> b = new Vertex<String>("B");
        final Vertex<String> c = new Vertex<String>("C");
        final Vertex<String> d = new Vertex<String>("D");
        final Vertex<String> e = new Vertex<String>("E");
        final AdjacencyLists<String, Integer> adjacencyLists1 = new AdjacencyLists<String, Integer>();
        adjacencyLists1.addEdge(a, 1, b);
        adjacencyLists1.addEdge(a, 7, d);
        adjacencyLists1.addEdge(b, 6, c);
        adjacencyLists1.addEdge(b, 1, e);
        adjacencyLists1.addEdge(c, 1, b);
        adjacencyLists1.addEdge(d, 1, a);
        adjacencyLists1.addEdge(e, 2, b);
        adjacencyLists1.addEdge(e, 3, d);
        return new Object[][] {
            {
                new GraphProblem(Graph.create(adjacencyLists1), null, StringVertexComparator.INSTANCE),
                new Integer[][][] {
                    {
                        {   0,   1,null,   7,null},
                        {null,   0,   6,null,   1},
                        {null,   1,   0,null,null},
                        {   1,null,null,   0,null},
                        {null,   2,null,   3,   0}
                    },
                    {
                        {   0,   1,null,   7,null},
                        {null,   0,   6,null,   1},
                        {null,   1,   0,null,null},
                        {   1,   2,null,   0,null},
                        {null,   2,null,   3,   0}
                    },
                    {
                        {   0,   1,   7,   7,   2},
                        {null,   0,   6,null,   1},
                        {null,   1,   0,null,   2},
                        {   1,   2,   8,   0,   3},
                        {null,   2,   8,   3,   0}
                    },
                    {
                        {   0,   1,   7,   7,   2},
                        {null,   0,   6,null,   1},
                        {null,   1,   0,null,   2},
                        {   1,   2,   8,   0,   3},
                        {null,   2,   8,   3,   0}
                    },
                    {
                        {   0,   1,   7,   7,   2},
                        {null,   0,   6,null,   1},
                        {null,   1,   0,null,   2},
                        {   1,   2,   8,   0,   3},
                        {   4,   2,   8,   3,   0}
                    },
                    {
                        {   0,   1,   7,   5,   2},
                        {   5,   0,   6,   4,   1},
                        {   6,   1,   0,   5,   2},
                        {   1,   2,   8,   0,   3},
                        {   4,   2,   8,   3,   0}
                    }
                }
            }
        };
    }

    @Test(dataProvider="fordFulkersonData")
    public void fordFulkerson(
        final FlowNetworkProblem problem,
        final List<FordFulkersonDoubleStep> expected
    ) {
        final List<FordFulkersonDoubleStep> solution = FordFulkersonAlgorithm.INSTANCE.apply(problem);
        Assert.assertEquals(solution, expected);
    }

    @DataProvider
    public Object[][] fordFulkersonData() {
        final Vertex<String> a = new Vertex<String>("A");
        final Vertex<String> b = new Vertex<String>("B");
        final Vertex<String> c = new Vertex<String>("C");
        final Vertex<String> d = new Vertex<String>("D");
        final AdjacencyLists<String, FlowPair> adjacencyLists1 = new AdjacencyLists<String, FlowPair>();
        adjacencyLists1.addEdge(a, new FlowPair(0, 4), b);
        adjacencyLists1.addEdge(a, new FlowPair(0, 3), c);
        adjacencyLists1.addEdge(b, new FlowPair(0, 4), c);
        adjacencyLists1.addEdge(c, new FlowPair(0, 4), d);
        adjacencyLists1.addEdge(b, new FlowPair(0, 3), d);
        final AdjacencyLists<String, Integer> adjacencyLists1r = new AdjacencyLists<String, Integer>();
        adjacencyLists1r.addEdge(a, 4, b);
        adjacencyLists1r.addEdge(a, 3, c);
        adjacencyLists1r.addEdge(b, 4, c);
        adjacencyLists1r.addEdge(c, 4, d);
        adjacencyLists1r.addEdge(b, 3, d);
        final AdjacencyLists<String, FlowPair> adjacencyLists2 = new AdjacencyLists<String, FlowPair>();
        adjacencyLists2.addEdge(a, new FlowPair(3, 4), b);
        adjacencyLists2.addEdge(a, new FlowPair(0, 3), c);
        adjacencyLists2.addEdge(b, new FlowPair(0, 4), c);
        adjacencyLists2.addEdge(c, new FlowPair(0, 4), d);
        adjacencyLists2.addEdge(b, new FlowPair(3, 3), d);
        final AdjacencyLists<String, Integer> adjacencyLists2r = new AdjacencyLists<String, Integer>();
        adjacencyLists2r.addEdge(a, 1, b);
        adjacencyLists2r.addEdge(b, 3, a);
        adjacencyLists2r.addEdge(a, 3, c);
        adjacencyLists2r.addEdge(b, 4, c);
        adjacencyLists2r.addEdge(c, 4, d);
        adjacencyLists2r.addEdge(d, 3, b);
        final AdjacencyLists<String, FlowPair> adjacencyLists3 = new AdjacencyLists<String, FlowPair>();
        adjacencyLists3.addEdge(a, new FlowPair(3, 4), b);
        adjacencyLists3.addEdge(a, new FlowPair(3, 3), c);
        adjacencyLists3.addEdge(b, new FlowPair(0, 4), c);
        adjacencyLists3.addEdge(c, new FlowPair(3, 4), d);
        adjacencyLists3.addEdge(b, new FlowPair(3, 3), d);
        final AdjacencyLists<String, Integer> adjacencyLists3r = new AdjacencyLists<String, Integer>();
        adjacencyLists3r.addEdge(a, 1, b);
        adjacencyLists3r.addEdge(b, 3, a);
        adjacencyLists3r.addEdge(c, 3, a);
        adjacencyLists3r.addEdge(b, 4, c);
        adjacencyLists3r.addEdge(c, 1, d);
        adjacencyLists3r.addEdge(d, 3, c);
        adjacencyLists3r.addEdge(d, 3, b);
        final AdjacencyLists<String, FlowPair> adjacencyLists4 = new AdjacencyLists<String, FlowPair>();
        adjacencyLists4.addEdge(a, new FlowPair(4, 4), b);
        adjacencyLists4.addEdge(a, new FlowPair(3, 3), c);
        adjacencyLists4.addEdge(b, new FlowPair(1, 4), c);
        adjacencyLists4.addEdge(c, new FlowPair(4, 4), d);
        adjacencyLists4.addEdge(b, new FlowPair(3, 3), d);
        final AdjacencyLists<String, Integer> adjacencyLists4r = new AdjacencyLists<String, Integer>();
        adjacencyLists4r.addEdge(b, 4, a);
        adjacencyLists4r.addEdge(c, 3, a);
        adjacencyLists4r.addEdge(b, 3, c);
        adjacencyLists4r.addEdge(c, 1, b);
        adjacencyLists4r.addEdge(d, 4, c);
        adjacencyLists4r.addEdge(d, 3, b);
        final Graph<String, FlowPair> graph1 = Graph.create(adjacencyLists1);
        final Graph<String, FlowPair> graph2 = Graph.create(adjacencyLists2);
        final Graph<String, FlowPair> graph3 = Graph.create(adjacencyLists3);
        final Graph<String, FlowPair> graph4 = Graph.create(adjacencyLists4);
        final Graph<String, Integer> graph1r = Graph.create(adjacencyLists1r);
        final Graph<String, Integer> graph2r = Graph.create(adjacencyLists2r);
        final Graph<String, Integer> graph3r = Graph.create(adjacencyLists3r);
        final Graph<String, Integer> graph4r = Graph.create(adjacencyLists4r);
        return new Object[][] {
            {
                new FlowNetworkProblem(graph1, a, d),
                List.of(
                    new FordFulkersonDoubleStep(
                        graph1,
                        Collections.emptySet(),
                        graph1r,
                        Set.of(
                            new FordFulkersonPathStep<String, Integer>(a, graph1r.getEdges(a, b).iterator().next()),
                            new FordFulkersonPathStep<String, Integer>(b, graph1r.getEdges(b, d).iterator().next())
                        )
                    ),
                    new FordFulkersonDoubleStep(
                        graph2,
                        Set.of(
                            new FordFulkersonPathStep<String, FlowPair>(a, graph2.getEdges(a, b).iterator().next()),
                            new FordFulkersonPathStep<String, FlowPair>(b, graph2.getEdges(b, d).iterator().next())
                        ),
                        graph2r,
                        Set.of(
                            new FordFulkersonPathStep<String, Integer>(a, graph2r.getEdges(a, c).iterator().next()),
                            new FordFulkersonPathStep<String, Integer>(c, graph2r.getEdges(c, d).iterator().next())
                        )
                    ),
                    new FordFulkersonDoubleStep(
                        graph3,
                        Set.of(
                            new FordFulkersonPathStep<String, FlowPair>(a, graph3.getEdges(a, c).iterator().next()),
                            new FordFulkersonPathStep<String, FlowPair>(c, graph3.getEdges(c, d).iterator().next())
                        ),
                        graph3r,
                        Set.of(
                            new FordFulkersonPathStep<String, Integer>(a, graph3r.getEdges(a, b).iterator().next()),
                            new FordFulkersonPathStep<String, Integer>(b, graph3r.getEdges(b, c).iterator().next()),
                            new FordFulkersonPathStep<String, Integer>(c, graph3r.getEdges(c, d).iterator().next())
                        )
                    ),
                    new FordFulkersonDoubleStep(
                        graph4,
                        Set.of(
                            new FordFulkersonPathStep<String, FlowPair>(a, graph4.getEdges(a, b).iterator().next()),
                            new FordFulkersonPathStep<String, FlowPair>(b, graph4.getEdges(b, c).iterator().next()),
                            new FordFulkersonPathStep<String, FlowPair>(c, graph4.getEdges(c, d).iterator().next())
                        ),
                        graph4r,
                        Collections.emptySet()
                    )
                )
            }
        };
    }

    @Test(dataProvider="kruskalData")
    public void kruskal(final GraphProblem problem, final KruskalResult<String> expected) {
        Assert.assertEquals(KruskalAlgorithm.INSTANCE.apply(problem), expected);
    }

    @DataProvider
    public Object[][] kruskalData() {
        final Vertex<String> a = new Vertex<String>("A");
        final Vertex<String> b = new Vertex<String>("B");
        final Vertex<String> c = new Vertex<String>("C");
        final Vertex<String> d = new Vertex<String>("D");
        final Vertex<String> e = new Vertex<String>("E");
        final Vertex<String> f = new Vertex<String>("F");
        final Vertex<String> g = new Vertex<String>("G");
        final AdjacencyLists<String, Integer> adjacencyLists1 = new AdjacencyLists<String, Integer>();
        adjacencyLists1.addEdge(a, 1, b);
        adjacencyLists1.addEdge(b, 1, a);
        adjacencyLists1.addEdge(a, 7, d);
        adjacencyLists1.addEdge(d, 7, a);
        adjacencyLists1.addEdge(b, 6, c);
        adjacencyLists1.addEdge(c, 6, b);
        adjacencyLists1.addEdge(b, 2, e);
        adjacencyLists1.addEdge(e, 2, b);
        adjacencyLists1.addEdge(c, 1, f);
        adjacencyLists1.addEdge(f, 1, c);
        adjacencyLists1.addEdge(d, 3, e);
        adjacencyLists1.addEdge(e, 3, d);
        adjacencyLists1.addEdge(d, 5, g);
        adjacencyLists1.addEdge(g, 5, d);
        adjacencyLists1.addEdge(e, 4, f);
        adjacencyLists1.addEdge(f, 4, e);
        adjacencyLists1.addEdge(e, 9, g);
        adjacencyLists1.addEdge(g, 9, e);
        final AdjacencyLists<String, Integer> adjacencyLists2 = new AdjacencyLists<String, Integer>();
        adjacencyLists2.addEdge(a, 1, b);
        adjacencyLists2.addEdge(b, 1, a);
        adjacencyLists2.addEdge(c, 1, f);
        adjacencyLists2.addEdge(f, 1, c);
        adjacencyLists2.addEdge(b, 2, e);
        adjacencyLists2.addEdge(e, 2, b);
        adjacencyLists2.addEdge(d, 3, e);
        adjacencyLists2.addEdge(e, 3, d);
        adjacencyLists2.addEdge(e, 4, f);
        adjacencyLists2.addEdge(f, 4, e);
        adjacencyLists2.addEdge(d, 5, g);
        adjacencyLists2.addEdge(g, 5, d);
        final List<UndirectedEdge<String, Integer>> result =
            List.of(
                new UndirectedEdge<String, Integer>(a, 1, b),
                new UndirectedEdge<String, Integer>(c, 1, f),
                new UndirectedEdge<String, Integer>(b, 2, e),
                new UndirectedEdge<String, Integer>(d, 3, e),
                new UndirectedEdge<String, Integer>(e, 4, f),
                new UndirectedEdge<String, Integer>(d, 5, g)
            );
        return new Object[][] {
            {
                new GraphProblem(Graph.create(adjacencyLists1), a, StringVertexComparator.INSTANCE),
                new KruskalResult<String>(result, Graph.create(adjacencyLists2))
            }
        };
    }

    @Test(dataProvider="petriPlaceIndexData")
    public void petriPlaceIndex(final int transitionIndex, final boolean from, final int expected) {
        Assert.assertEquals(PetriNetAlgorithm.computePlaceIndex(transitionIndex, from), expected);
    }

    @Test(dataProvider="petriPlaceIndexCoordinatesData")
    public void petriPlaceIndexCoordinates(final int placeIndex, final int expectedX, final int expectedY) {
        Assert.assertEquals(PetriNetAlgorithm.computeXforPlace(placeIndex), expectedX);
        Assert.assertEquals(PetriNetAlgorithm.computeYforPlace(placeIndex), expectedY);
    }

    @DataProvider
    public Object[][] petriPlaceIndexCoordinatesData() {
        return new Object[][] {
            {0, 0, 8},
            {1, 4, 8},
            {2, 4, 4},
            {3, 0, 4},
            {4, 8, 8},
            {5, 8, 4},
            {6, 8, 0},
            {7, 4, 0},
            {8, 0, 0}
        };
    }

    @DataProvider
    public Object[][] petriPlaceIndexData() {
        return new Object[][] {
            {0, true, 0},
            {0, false, 1},
            {1, true, 1},
            {1, false, 0},
            {2, true, 0},
            {2, false, 3},
            {3, true, 3},
            {3, false, 0},
            {4, true, 1},
            {4, false, 2},
            {5, true, 2},
            {5, false, 1},
            {6, true, 2},
            {6, false, 3},
            {7, true, 3},
            {7, false, 2},
            {8, true, 1},
            {8, false, 4},
            {9, true, 4},
            {9, false, 1},
            {10, true, 2},
            {10, false, 5},
            {11, true, 5},
            {11, false, 2},
            {12, true, 2},
            {12, false, 7},
            {13, true, 7},
            {13, false, 2},
            {14, true, 3},
            {14, false, 8},
            {15, true, 8},
            {15, false, 3},
            {16, true, 4},
            {16, false, 5},
            {17, true, 5},
            {17, false, 4},
            {18, true, 5},
            {18, false, 6},
            {19, true, 6},
            {19, false, 5},
            {20, true, 6},
            {20, false, 7},
            {21, true, 7},
            {21, false, 6},
            {22, true, 7},
            {22, false, 8},
            {23, true, 8},
            {23, false, 7},
            {24, true, 4},
            {24, false, 9}
        };
    }

    @Test(dataProvider="primData")
    public void prim(final GraphProblem problem, final PrimResult<String> expected) {
        Assert.assertEquals(PrimAlgorithm.INSTANCE.apply(problem), expected);
    }

    @DataProvider
    public Object[][] primData() {
        final Vertex<String> a = new Vertex<String>("A");
        final Vertex<String> b = new Vertex<String>("B");
        final Vertex<String> c = new Vertex<String>("C");
        final Vertex<String> d = new Vertex<String>("D");
        final Vertex<String> e = new Vertex<String>("E");
        final Vertex<String> f = new Vertex<String>("F");
        final Vertex<String> g = new Vertex<String>("G");
        final AdjacencyLists<String, Integer> adjacencyLists1 = new AdjacencyLists<String, Integer>();
        adjacencyLists1.addEdge(a, 1, b);
        adjacencyLists1.addEdge(b, 1, a);
        adjacencyLists1.addEdge(a, 7, d);
        adjacencyLists1.addEdge(d, 7, a);
        adjacencyLists1.addEdge(b, 6, c);
        adjacencyLists1.addEdge(c, 6, b);
        adjacencyLists1.addEdge(b, 2, e);
        adjacencyLists1.addEdge(e, 2, b);
        adjacencyLists1.addEdge(c, 1, f);
        adjacencyLists1.addEdge(f, 1, c);
        adjacencyLists1.addEdge(d, 3, e);
        adjacencyLists1.addEdge(e, 3, d);
        adjacencyLists1.addEdge(d, 5, g);
        adjacencyLists1.addEdge(g, 5, d);
        adjacencyLists1.addEdge(e, 4, f);
        adjacencyLists1.addEdge(f, 4, e);
        adjacencyLists1.addEdge(e, 9, g);
        adjacencyLists1.addEdge(g, 9, e);
        final AdjacencyLists<String, Integer> adjacencyLists2 = new AdjacencyLists<String, Integer>();
        adjacencyLists2.addEdge(e, 2, b);
        adjacencyLists2.addEdge(b, 2, e);
        adjacencyLists2.addEdge(b, 1, a);
        adjacencyLists2.addEdge(a, 1, b);
        adjacencyLists2.addEdge(e, 3, d);
        adjacencyLists2.addEdge(d, 3, e);
        adjacencyLists2.addEdge(e, 4, f);
        adjacencyLists2.addEdge(f, 4, e);
        adjacencyLists2.addEdge(f, 1, c);
        adjacencyLists2.addEdge(c, 1, f);
        adjacencyLists2.addEdge(d, 5, g);
        adjacencyLists2.addEdge(g, 5, d);
        final PrimEntry[][] table =
            new PrimEntry[][] {
                {
                    PrimAlgorithm.INFINITY,
                    PrimAlgorithm.INFINITY,
                    PrimAlgorithm.INFINITY,
                    PrimAlgorithm.INFINITY,
                    new PrimEntry(0).toDone(),
                    PrimAlgorithm.INFINITY,
                    PrimAlgorithm.INFINITY
                },
                {
                    PrimAlgorithm.INFINITY,
                    new PrimEntry(2).toDone(),
                    PrimAlgorithm.INFINITY,
                    new PrimEntry(3),
                    null,
                    new PrimEntry(4),
                    new PrimEntry(9)
                },
                {
                    new PrimEntry(1).toDone(),
                    null,
                    new PrimEntry(6),
                    new PrimEntry(3),
                    null,
                    new PrimEntry(4),
                    new PrimEntry(9)
                },
                {null, null, new PrimEntry(6), new PrimEntry(3).toDone(), null, new PrimEntry(4), new PrimEntry(9)},
                {null, null, new PrimEntry(6), null, null, new PrimEntry(4).toDone(), new PrimEntry(5)},
                {null, null, new PrimEntry(1).toDone(), null, null, null, new PrimEntry(5)},
                {null, null, null, null, null, null, new PrimEntry(5).toDone()},
            };
        return new Object[][] {
            {
                new GraphProblem(Graph.create(adjacencyLists1), e, StringVertexComparator.INSTANCE),
                new PrimResult<String>(table, Graph.create(adjacencyLists2))
            }
        };
    }

}
