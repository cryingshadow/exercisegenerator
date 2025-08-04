package exercisegenerator.algorithms.graphs;

import java.util.*;
import java.util.Optional;

import org.apache.commons.math3.fraction.*;
import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.structures.algebra.*;
import exercisegenerator.structures.graphs.*;
import exercisegenerator.structures.graphs.flownetwork.*;
import exercisegenerator.structures.graphs.layout.*;
import exercisegenerator.structures.graphs.petrinets.*;

public class GraphAlgorithmsTest {

    private static final List<PetriNetInput> PETRI_NETS;

    static {
        final PetriPlace place1 = new PetriPlace("Spring", 0, 0, 135);
        final PetriPlace place2 = new PetriPlace("Summer", 2, 0, 135);
        final PetriPlace place3 = new PetriPlace("Fall", 2, 2, 135);
        final PetriPlace place4 = new PetriPlace("Winter", 0, 2, 135);
        final List<PetriPlace> places = List.of(place1, place2, place3, place4);
        final PetriTransition transition1 = new PetriTransition("t1", 1, 0, Map.of(0,1), Map.of(1,1));
        final PetriTransition transition2 = new PetriTransition("t2", 2, 1, Map.of(1,1), Map.of(2,1));
        final PetriTransition transition3 = new PetriTransition("t3", 1, 2, Map.of(2,1), Map.of(3,1));
        final PetriTransition transition4 = new PetriTransition("t4", 0, 1, Map.of(3,1), Map.of(0,1));
        final PetriTransition transition4b = new PetriTransition("t4", 0, 1, Map.of(3,1), Map.of(0,2));
        final List<PetriTransition> transitions = List.of(transition1, transition2, transition3, transition4);
        final List<PetriTransition> transitionsb = List.of(transition1, transition2, transition3, transition4b);
        final Map<Integer, Integer> empty = new LinkedHashMap<Integer, Integer>();
        final Map<Integer, Integer> zeroPure = new LinkedHashMap<Integer, Integer>();
        zeroPure.put(0, 0);
        zeroPure.put(1, 0);
        zeroPure.put(2, 0);
        zeroPure.put(3, 0);
        final Map<Integer, Integer> one1Pure = new LinkedHashMap<Integer, Integer>();
        one1Pure.put(0, 1);
        one1Pure.put(1, 0);
        one1Pure.put(2, 0);
        one1Pure.put(3, 0);
        PETRI_NETS =
            List.of(
                new PetriNetInput(List.of(), List.of(), empty),
                new PetriNetInput(places, transitions, zeroPure),
                new PetriNetInput(places, transitions, one1Pure),
                new PetriNetInput(places, transitionsb, one1Pure)
            );
    }

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
                    new GraphWithLayout<String, Integer, Integer>(
                        Graph.create(adjacencyLists1),
                        new DummyGraphLayout<String, Integer, Integer>()
                    ),
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
            {
                new GraphProblem(
                    new GraphWithLayout<String, Integer, Integer>(
                        Graph.create(adjacencyLists1),
                        new DummyGraphLayout<String, Integer, Integer>()
                    ),
                    a,
                    StringVertexComparator.INSTANCE
                ),
                List.of("A")
            },
            {
                new GraphProblem(
                    new GraphWithLayout<String, Integer, Integer>(
                        Graph.create(adjacencyLists2),
                        new DummyGraphLayout<String, Integer, Integer>()
                    ),
                    a,
                    StringVertexComparator.INSTANCE
                ),
                List.of("A", "B", "C", "D")
            },
            {
                new GraphProblem(
                    new GraphWithLayout<String, Integer, Integer>(
                        Graph.create(adjacencyLists3),
                        new DummyGraphLayout<String, Integer, Integer>()
                    ),
                    a,
                    StringVertexComparator.INSTANCE
                ),
                List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
            }
        };
    }

    @Test(dataProvider="bfsData")
    public void breadthFirstSearch(final GraphProblem problem, final List<String> expected) {
        Assert.assertEquals(BreadthFirstSearch.INSTANCE.apply(problem), expected);
    }

    @Test(dataProvider="coverabilityGraphData")
    public void coverabilityGraph(final PetriNetInput input, final CoverabilityGraph expected) {
        Assert.assertTrue(PetriNetCoverabilityAlgorithm.INSTANCE.apply(input).logicallyEquals(expected));
    }

    @DataProvider
    public Object[][] coverabilityGraphData() {
        final Map<Integer, Integer> empty = new LinkedHashMap<Integer, Integer>();
        final Map<Integer, Integer> zeroPure = new LinkedHashMap<Integer, Integer>();
        zeroPure.put(0, 0);
        zeroPure.put(1, 0);
        zeroPure.put(2, 0);
        zeroPure.put(3, 0);
        final PetriMarking zero = new PetriMarking();
        zero.put(0, Optional.of(0));
        zero.put(1, Optional.of(0));
        zero.put(2, Optional.of(0));
        zero.put(3, Optional.of(0));
        final PetriMarking one1 = new PetriMarking();
        one1.put(0, Optional.of(1));
        one1.put(1, Optional.of(0));
        one1.put(2, Optional.of(0));
        one1.put(3, Optional.of(0));
        final PetriMarking one2 = new PetriMarking();
        one2.put(0, Optional.of(0));
        one2.put(1, Optional.of(1));
        one2.put(2, Optional.of(0));
        one2.put(3, Optional.of(0));
        final PetriMarking one3 = new PetriMarking();
        one3.put(0, Optional.of(0));
        one3.put(1, Optional.of(0));
        one3.put(2, Optional.of(1));
        one3.put(3, Optional.of(0));
        final PetriMarking one4 = new PetriMarking();
        one4.put(0, Optional.of(0));
        one4.put(1, Optional.of(0));
        one4.put(2, Optional.of(0));
        one4.put(3, Optional.of(1));
        final Vertex<PetriMarking> vertex1 = new Vertex<PetriMarking>(one1);
        final Vertex<PetriMarking> vertex2 = new Vertex<PetriMarking>(one2);
        final Vertex<PetriMarking> vertex3 = new Vertex<PetriMarking>(one3);
        final Vertex<PetriMarking> vertex4 = new Vertex<PetriMarking>(one4);
        final CoverabilityGraph graph1 = new CoverabilityGraph(vertex1);
        graph1.addVertex(vertex2);
        graph1.addVertex(vertex3);
        graph1.addVertex(vertex4);
        graph1.addEdge(vertex1, Optional.of("t1"), vertex2);
        graph1.addEdge(vertex2, Optional.of("t2"), vertex3);
        graph1.addEdge(vertex3, Optional.of("t3"), vertex4);
        graph1.addEdge(vertex4, Optional.of("t4"), vertex1);
        final PetriMarking inf1 = new PetriMarking();
        inf1.put(0, Optional.empty());
        inf1.put(1, Optional.of(0));
        inf1.put(2, Optional.of(0));
        inf1.put(3, Optional.of(0));
        final PetriMarking inf2 = new PetriMarking();
        inf2.put(0, Optional.empty());
        inf2.put(1, Optional.empty());
        inf2.put(2, Optional.of(0));
        inf2.put(3, Optional.of(0));
        final PetriMarking inf3 = new PetriMarking();
        inf3.put(0, Optional.empty());
        inf3.put(1, Optional.empty());
        inf3.put(2, Optional.empty());
        inf3.put(3, Optional.of(0));
        final PetriMarking inf4 = new PetriMarking();
        inf4.put(0, Optional.empty());
        inf4.put(1, Optional.empty());
        inf4.put(2, Optional.empty());
        inf4.put(3, Optional.empty());
        final Vertex<PetriMarking> vertex5 = new Vertex<PetriMarking>(inf1);
        final Vertex<PetriMarking> vertex6 = new Vertex<PetriMarking>(inf2);
        final Vertex<PetriMarking> vertex7 = new Vertex<PetriMarking>(inf3);
        final Vertex<PetriMarking> vertex8 = new Vertex<PetriMarking>(inf4);
        final CoverabilityGraph graph2 = new CoverabilityGraph(vertex1);
        graph2.addVertex(vertex2);
        graph2.addVertex(vertex3);
        graph2.addVertex(vertex4);
        graph2.addVertex(vertex5);
        graph2.addVertex(vertex6);
        graph2.addVertex(vertex7);
        graph2.addVertex(vertex8);
        graph2.addEdge(vertex1, Optional.of("t1"), vertex2);
        graph2.addEdge(vertex2, Optional.of("t2"), vertex3);
        graph2.addEdge(vertex3, Optional.of("t3"), vertex4);
        graph2.addEdge(vertex4, Optional.of("t4"), vertex5);
        graph2.addEdge(vertex5, Optional.of("t1"), vertex6);
        graph2.addEdge(vertex6, Optional.of("t1"), vertex6);
        graph2.addEdge(vertex6, Optional.of("t2"), vertex7);
        graph2.addEdge(vertex7, Optional.of("t1"), vertex7);
        graph2.addEdge(vertex7, Optional.of("t2"), vertex7);
        graph2.addEdge(vertex7, Optional.of("t3"), vertex8);
        graph2.addEdge(vertex8, Optional.of("t1"), vertex8);
        graph2.addEdge(vertex8, Optional.of("t2"), vertex8);
        graph2.addEdge(vertex8, Optional.of("t3"), vertex8);
        graph2.addEdge(vertex8, Optional.of("t4"), vertex8);
        return new Object[][] {
            {
                GraphAlgorithmsTest.PETRI_NETS.get(0),
                new CoverabilityGraph(new Vertex<PetriMarking>(PetriMarking.create(empty)))
            },
            {GraphAlgorithmsTest.PETRI_NETS.get(1), new CoverabilityGraph(new Vertex<PetriMarking>(zero))},
            {GraphAlgorithmsTest.PETRI_NETS.get(2), graph1},
            {GraphAlgorithmsTest.PETRI_NETS.get(3), graph2}
        };
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
            {
                new GraphProblem(
                    new GraphWithLayout<String, Integer, Integer>(
                        Graph.create(adjacencyLists1),
                        new DummyGraphLayout<String, Integer, Integer>()
                    ),
                    a,
                    StringVertexComparator.INSTANCE
                ),
                List.of("A")
            },
            {
                new GraphProblem(
                    new GraphWithLayout<String, Integer, Integer>(
                        Graph.create(adjacencyLists2),
                        new DummyGraphLayout<String, Integer, Integer>()
                    ),
                    a,
                    StringVertexComparator.INSTANCE
                ),
                List.of("A", "B", "C", "D")
            },
            {
                new GraphProblem(
                    new GraphWithLayout<String, Integer, Integer>(
                        Graph.create(adjacencyLists3),
                        new DummyGraphLayout<String, Integer, Integer>()
                    ),
                    a,
                    StringVertexComparator.INSTANCE
                ),
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
                    new GraphWithLayout<String, Integer, Integer>(
                        Graph.create(adjacencyLists1),
                        new DummyGraphLayout<String, Integer, Integer>()
                    ),
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

    @Test(dataProvider="farkasPlaceData")
    public void farkasPlace(final PetriNetInput input, final List<Matrix> expected) {
        Assert.assertEquals(PetriNetFarkasPlaceInvariantsAlgorithm.INSTANCE.apply(input), expected);
    }

    @DataProvider
    public Object[][] farkasPlaceData() {
        final PetriPlace p1 = new PetriPlace("p1", 0, 4, 180);
        final PetriPlace p2 = new PetriPlace("p2", 8, 4, 0);
        final PetriPlace p3 = new PetriPlace("p3", 8, 8, 90);
        final PetriPlace p4 = new PetriPlace("p4", 4, 0, 270);
        final PetriPlace p5 = new PetriPlace("p5", 4, 8, 90);
        final PetriTransition t1 = new PetriTransition("t1", 2, 6, Map.of(0, 1, 4, 1), Map.of(1, 1, 3, 1));
        final PetriTransition t2 = new PetriTransition("t2", 2, 2, Map.of(1, 1), Map.of(0, 1));
        final PetriTransition t3 = new PetriTransition("t3", 6, 6, Map.of(1, 1), Map.of(0, 1, 2, 1));
        final PetriTransition t4 = new PetriTransition("t4", 6, 2, Map.of(0, 1, 3, 1), Map.of(1, 1, 4, 1));
        return new Object[][] {
            {GraphAlgorithmsTest.PETRI_NETS.get(0), List.of(new Matrix(0, 0, 0))},
            {
                GraphAlgorithmsTest.PETRI_NETS.get(1),
                List.of(
                    new Matrix(
                        new int[][] {
                            {-1, 0, 0, 1, 1, 0, 0, 0},
                            { 1,-1, 0, 0, 0, 1, 0, 0},
                            { 0, 1,-1, 0, 0, 0, 1, 0},
                            { 0, 0, 1,-1, 0, 0, 0, 1}
                        },
                        4
                    ),
                    new Matrix(
                        new int[][] {
                            { 0, 1,-1, 0, 0, 0, 1, 0},
                            { 0, 0, 1,-1, 0, 0, 0, 1},
                            { 0,-1, 0, 1, 1, 1, 0, 0}
                        },
                        4
                    ),
                    new Matrix(
                        new int[][] {
                            { 0, 0, 1,-1, 0, 0, 0, 1},
                            { 0, 0,-1, 1, 1, 1, 1, 0}
                        },
                        4
                    ),
                    new Matrix(
                        new int[][] {
                            { 0, 0, 0, 0, 1, 1, 1, 1}
                        },
                        4
                    ),
                    new Matrix(
                        new int[][] {
                            { 0, 0, 0, 0, 1, 1, 1, 1}
                        },
                        4
                    ),
                    new Matrix(
                        new int[][] {
                            { 1, 1, 1, 1}
                        },
                        4
                    )
                )
            },
            {
                GraphAlgorithmsTest.PETRI_NETS.get(3),
                List.of(
                    new Matrix(
                        new int[][] {
                            {-1, 0, 0, 2, 1, 0, 0, 0},
                            { 1,-1, 0, 0, 0, 1, 0, 0},
                            { 0, 1,-1, 0, 0, 0, 1, 0},
                            { 0, 0, 1,-1, 0, 0, 0, 1}
                        },
                        4
                    ),
                    new Matrix(
                        new int[][] {
                            { 0, 1,-1, 0, 0, 0, 1, 0},
                            { 0, 0, 1,-1, 0, 0, 0, 1},
                            { 0,-1, 0, 2, 1, 1, 0, 0}
                        },
                        4
                    ),
                    new Matrix(
                        new int[][] {
                            { 0, 0, 1,-1, 0, 0, 0, 1},
                            { 0, 0,-1, 2, 1, 1, 1, 0}
                        },
                        4
                    ),
                    new Matrix(
                        new int[][] {
                            { 0, 0, 0, 1, 1, 1, 1, 1}
                        },
                        4
                    ),
                    new Matrix(
                        new BigFraction[][] {},
                        new int[] {0, 1, 2, 3, 4, 5, 6, 7},
                        4
                    )
                )
            },
            {
                new PetriNetInput(List.of(p1, p2, p3, p4, p5), List.of(t1, t2, t3, t4), Map.of()),
                List.of(
                    new Matrix(
                        new int[][] {
                            {-1, 1, 1,-1, 1, 0, 0, 0, 0},
                            { 1,-1,-1, 1, 0, 1, 0, 0, 0},
                            { 0, 0, 1, 0, 0, 0, 1, 0, 0},
                            { 1, 0, 0,-1, 0, 0, 0, 1, 0},
                            {-1, 0, 0, 1, 0, 0, 0, 0, 1}
                        },
                        4
                    ),
                    new Matrix(
                        new int[][] {
                            { 0, 0, 1, 0, 0, 0, 1, 0, 0},
                            { 0, 0, 0, 0, 1, 1, 0, 0, 0},
                            { 0, 1, 1,-2, 1, 0, 0, 1, 0},
                            { 0,-1,-1, 2, 0, 1, 0, 0, 1},
                            { 0, 0, 0, 0, 0, 0, 0, 1, 1}
                        },
                        4
                    ),
                    new Matrix(
                        new int[][] {
                            { 0, 0, 1, 0, 0, 0, 1, 0, 0},
                            { 0, 0, 0, 0, 1, 1, 0, 0, 0},
                            { 0, 0, 0, 0, 0, 0, 0, 1, 1},
                            { 0, 0, 0, 0, 1, 1, 0, 1, 1}
                        },
                        4
                    ),
                    new Matrix(
                        new int[][] {
                            { 0, 0, 0, 0, 1, 1, 0, 0, 0},
                            { 0, 0, 0, 0, 0, 0, 0, 1, 1},
                            { 0, 0, 0, 0, 1, 1, 0, 1, 1}
                        },
                        4
                    ),
                    new Matrix(
                        new int[][] {
                            { 0, 0, 0, 0, 1, 1, 0, 0, 0},
                            { 0, 0, 0, 0, 0, 0, 0, 1, 1},
                            { 0, 0, 0, 0, 1, 1, 0, 1, 1}
                        },
                        4
                    ),
                    new Matrix(
                        new int[][] {
                            {1, 1, 0, 0, 0},
                            {0, 0, 0, 1, 1}
                        },
                        5
                    )
                )
            }
        };
    }

    @Test(dataProvider="farkasTransitionData")
    public void farkasTransition(final PetriNetInput input, final List<Matrix> expected) {
        Assert.assertEquals(PetriNetFarkasTransitionInvariantsAlgorithm.INSTANCE.apply(input), expected);
    }

    @DataProvider
    public Object[][] farkasTransitionData() {
        final PetriPlace p1 = new PetriPlace("p1", 0, 4, 180);
        final PetriPlace p2 = new PetriPlace("p2", 8, 4, 0);
        final PetriPlace p3 = new PetriPlace("p3", 8, 8, 90);
        final PetriPlace p4 = new PetriPlace("p4", 4, 0, 270);
        final PetriPlace p5 = new PetriPlace("p5", 4, 8, 90);
        final PetriTransition t1 = new PetriTransition("t1", 2, 6, Map.of(0, 1, 4, 1), Map.of(1, 1, 3, 1));
        final PetriTransition t2 = new PetriTransition("t2", 2, 2, Map.of(1, 1), Map.of(0, 1));
        final PetriTransition t3 = new PetriTransition("t3", 6, 6, Map.of(1, 1), Map.of(0, 1, 2, 1));
        final PetriTransition t4 = new PetriTransition("t4", 6, 2, Map.of(0, 1, 3, 1), Map.of(1, 1, 4, 1));
        return new Object[][] {
            {GraphAlgorithmsTest.PETRI_NETS.get(0), List.of(new Matrix(0, 0, 0))},
            {
                GraphAlgorithmsTest.PETRI_NETS.get(1),
                List.of(
                    new Matrix(
                        new int[][] {
                            {-1, 1, 0, 0, 1, 0, 0, 0},
                            { 0,-1, 1, 0, 0, 1, 0, 0},
                            { 0, 0,-1, 1, 0, 0, 1, 0},
                            { 1, 0, 0,-1, 0, 0, 0, 1}
                        },
                        4
                    ),
                    new Matrix(
                        new int[][] {
                            { 0,-1, 1, 0, 0, 1, 0, 0},
                            { 0, 0,-1, 1, 0, 0, 1, 0},
                            { 0, 1, 0,-1, 1, 0, 0, 1}
                        },
                        4
                    ),
                    new Matrix(
                        new int[][] {
                            { 0, 0,-1, 1, 0, 0, 1, 0},
                            { 0, 0, 1,-1, 1, 1, 0, 1}
                        },
                        4
                    ),
                    new Matrix(
                        new int[][] {
                            { 0, 0, 0, 0, 1, 1, 1, 1}
                        },
                        4
                    ),
                    new Matrix(
                        new int[][] {
                            { 0, 0, 0, 0, 1, 1, 1, 1}
                        },
                        4
                    ),
                    new Matrix(
                        new int[][] {
                            { 1, 1, 1, 1}
                        },
                        4
                    )
                )
            },
            {
                GraphAlgorithmsTest.PETRI_NETS.get(3),
                List.of(
                    new Matrix(
                        new int[][] {
                            {-1, 1, 0, 0, 1, 0, 0, 0},
                            { 0,-1, 1, 0, 0, 1, 0, 0},
                            { 0, 0,-1, 1, 0, 0, 1, 0},
                            { 2, 0, 0,-1, 0, 0, 0, 1}
                        },
                        4
                    ),
                    new Matrix(
                        new int[][] {
                            { 0,-1, 1, 0, 0, 1, 0, 0},
                            { 0, 0,-1, 1, 0, 0, 1, 0},
                            { 0, 2, 0,-1, 2, 0, 0, 1}
                        },
                        4
                    ),
                    new Matrix(
                        new int[][] {
                            { 0, 0,-1, 1, 0, 0, 1, 0},
                            { 0, 0, 2,-1, 2, 2, 0, 1}
                        },
                        4
                    ),
                    new Matrix(
                        new int[][] {
                            { 0, 0, 0, 1, 2, 2, 2, 1}
                        },
                        4
                    ),
                    new Matrix(
                        new BigFraction[][] {},
                        new int[] {0, 1, 2, 3, 4, 5, 6, 7},
                        4
                    )
                )
            },
            {
                new PetriNetInput(List.of(p1, p2, p3, p4, p5), List.of(t1, t2, t3, t4), Map.of()),
                List.of(
                    new Matrix(
                        new int[][] {
                            {-1, 1, 0, 1,-1, 1, 0, 0, 0},
                            { 1,-1, 0, 0, 0, 0, 1, 0, 0},
                            { 1,-1, 1, 0, 0, 0, 0, 1, 0},
                            {-1, 1, 0,-1, 1, 0, 0, 0, 1}
                        },
                        5
                    ),
                    new Matrix(
                        new int[][] {
                            { 0, 0, 0, 1,-1, 1, 1, 0, 0},
                            { 0, 0, 1, 1,-1, 1, 0, 1, 0},
                            { 0, 0, 0,-1, 1, 0, 1, 0, 1},
                            { 0, 0, 1,-1, 1, 0, 0, 1, 1}
                        },
                        5
                    ),
                    new Matrix(
                        new int[][] {
                            { 0, 0, 0, 1,-1, 1, 1, 0, 0},
                            { 0, 0, 1, 1,-1, 1, 0, 1, 0},
                            { 0, 0, 0,-1, 1, 0, 1, 0, 1},
                            { 0, 0, 1,-1, 1, 0, 0, 1, 1}
                        },
                        5
                    ),
                    new Matrix(
                        new int[][] {
                            { 0, 0, 0, 1,-1, 1, 1, 0, 0},
                            { 0, 0, 0,-1, 1, 0, 1, 0, 1}
                        },
                        5
                    ),
                    new Matrix(
                        new int[][] {
                            { 0, 0, 0, 0, 0, 1, 2, 0, 1}
                        },
                        5
                    ),
                    new Matrix(
                        new int[][] {
                            { 0, 0, 0, 0, 0, 1, 2, 0, 1}
                        },
                        5
                    ),
                    new Matrix(
                        new int[][] {
                            {1, 2, 0, 1}
                        },
                        4
                    )
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
                new GraphProblem(
                    new GraphWithLayout<String, Integer, Integer>(
                        Graph.create(adjacencyLists1),
                        new DummyGraphLayout<String, Integer, Integer>()
                    ),
                    null,
                    StringVertexComparator.INSTANCE
                ),
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
        final AdjacencyLists<String, FlowAndCapacity> adjacencyLists1 = new AdjacencyLists<String, FlowAndCapacity>();
        adjacencyLists1.addEdge(a, new FlowAndCapacity(0, 4), b);
        adjacencyLists1.addEdge(a, new FlowAndCapacity(0, 3), c);
        adjacencyLists1.addEdge(b, new FlowAndCapacity(0, 4), c);
        adjacencyLists1.addEdge(c, new FlowAndCapacity(0, 4), d);
        adjacencyLists1.addEdge(b, new FlowAndCapacity(0, 3), d);
        final AdjacencyLists<String, Integer> adjacencyLists1r = new AdjacencyLists<String, Integer>();
        adjacencyLists1r.addEdge(a, 4, b);
        adjacencyLists1r.addEdge(a, 3, c);
        adjacencyLists1r.addEdge(b, 4, c);
        adjacencyLists1r.addEdge(c, 4, d);
        adjacencyLists1r.addEdge(b, 3, d);
        final AdjacencyLists<String, FlowAndCapacity> adjacencyLists2 = new AdjacencyLists<String, FlowAndCapacity>();
        adjacencyLists2.addEdge(a, new FlowAndCapacity(3, 4), b);
        adjacencyLists2.addEdge(a, new FlowAndCapacity(0, 3), c);
        adjacencyLists2.addEdge(b, new FlowAndCapacity(0, 4), c);
        adjacencyLists2.addEdge(c, new FlowAndCapacity(0, 4), d);
        adjacencyLists2.addEdge(b, new FlowAndCapacity(3, 3), d);
        final AdjacencyLists<String, Integer> adjacencyLists2r = new AdjacencyLists<String, Integer>();
        adjacencyLists2r.addEdge(a, 1, b);
        adjacencyLists2r.addEdge(b, 3, a);
        adjacencyLists2r.addEdge(a, 3, c);
        adjacencyLists2r.addEdge(b, 4, c);
        adjacencyLists2r.addEdge(c, 4, d);
        adjacencyLists2r.addEdge(d, 3, b);
        final AdjacencyLists<String, FlowAndCapacity> adjacencyLists3 = new AdjacencyLists<String, FlowAndCapacity>();
        adjacencyLists3.addEdge(a, new FlowAndCapacity(3, 4), b);
        adjacencyLists3.addEdge(a, new FlowAndCapacity(3, 3), c);
        adjacencyLists3.addEdge(b, new FlowAndCapacity(0, 4), c);
        adjacencyLists3.addEdge(c, new FlowAndCapacity(3, 4), d);
        adjacencyLists3.addEdge(b, new FlowAndCapacity(3, 3), d);
        final AdjacencyLists<String, Integer> adjacencyLists3r = new AdjacencyLists<String, Integer>();
        adjacencyLists3r.addEdge(a, 1, b);
        adjacencyLists3r.addEdge(b, 3, a);
        adjacencyLists3r.addEdge(c, 3, a);
        adjacencyLists3r.addEdge(b, 4, c);
        adjacencyLists3r.addEdge(c, 1, d);
        adjacencyLists3r.addEdge(d, 3, c);
        adjacencyLists3r.addEdge(d, 3, b);
        final AdjacencyLists<String, FlowAndCapacity> adjacencyLists4 = new AdjacencyLists<String, FlowAndCapacity>();
        adjacencyLists4.addEdge(a, new FlowAndCapacity(4, 4), b);
        adjacencyLists4.addEdge(a, new FlowAndCapacity(3, 3), c);
        adjacencyLists4.addEdge(b, new FlowAndCapacity(1, 4), c);
        adjacencyLists4.addEdge(c, new FlowAndCapacity(4, 4), d);
        adjacencyLists4.addEdge(b, new FlowAndCapacity(3, 3), d);
        final AdjacencyLists<String, Integer> adjacencyLists4r = new AdjacencyLists<String, Integer>();
        adjacencyLists4r.addEdge(b, 4, a);
        adjacencyLists4r.addEdge(c, 3, a);
        adjacencyLists4r.addEdge(b, 3, c);
        adjacencyLists4r.addEdge(c, 1, b);
        adjacencyLists4r.addEdge(d, 4, c);
        adjacencyLists4r.addEdge(d, 3, b);
        final Graph<String, FlowAndCapacity> graph1 = Graph.create(adjacencyLists1);
        final Graph<String, FlowAndCapacity> graph2 = Graph.create(adjacencyLists2);
        final Graph<String, FlowAndCapacity> graph3 = Graph.create(adjacencyLists3);
        final Graph<String, FlowAndCapacity> graph4 = Graph.create(adjacencyLists4);
        final Graph<String, Integer> graph1r = Graph.create(adjacencyLists1r);
        final Graph<String, Integer> graph2r = Graph.create(adjacencyLists2r);
        final Graph<String, Integer> graph3r = Graph.create(adjacencyLists3r);
        final Graph<String, Integer> graph4r = Graph.create(adjacencyLists4r);
        final GridGraphLayout<String, FlowAndCapacity> layout =
            GridGraphLayout.<String, FlowAndCapacity>builder().build();
        final GraphLayout<String, Integer, Integer> residualLayout = layout.convertEdgeLabelType();
        return new Object[][] {
            {
                new FlowNetworkProblem(new GraphWithLayout<>(graph1, layout), a, d),
                List.of(
                    new FordFulkersonDoubleStep(
                        new GraphWithLayout<String, FlowAndCapacity, Integer>(graph1, layout),
                        Collections.emptySet(),
                        new GraphWithLayout<String, Integer, Integer>(graph1r, residualLayout),
                        Set.of(
                            new FordFulkersonPathStep<String, Integer>(a, graph1r.getEdges(a, b).iterator().next()),
                            new FordFulkersonPathStep<String, Integer>(b, graph1r.getEdges(b, d).iterator().next())
                        )
                    ),
                    new FordFulkersonDoubleStep(
                        new GraphWithLayout<String, FlowAndCapacity, Integer>(graph2, layout),
                        Set.of(
                            new FordFulkersonPathStep<String, FlowAndCapacity>(a, graph2.getEdges(a, b).iterator().next()),
                            new FordFulkersonPathStep<String, FlowAndCapacity>(b, graph2.getEdges(b, d).iterator().next())
                        ),
                        new GraphWithLayout<String, Integer, Integer>(graph2r, residualLayout),
                        Set.of(
                            new FordFulkersonPathStep<String, Integer>(a, graph2r.getEdges(a, c).iterator().next()),
                            new FordFulkersonPathStep<String, Integer>(c, graph2r.getEdges(c, d).iterator().next())
                        )
                    ),
                    new FordFulkersonDoubleStep(
                        new GraphWithLayout<String, FlowAndCapacity, Integer>(graph3, layout),
                        Set.of(
                            new FordFulkersonPathStep<String, FlowAndCapacity>(a, graph3.getEdges(a, c).iterator().next()),
                            new FordFulkersonPathStep<String, FlowAndCapacity>(c, graph3.getEdges(c, d).iterator().next())
                        ),
                        new GraphWithLayout<String, Integer, Integer>(graph3r, residualLayout),
                        Set.of(
                            new FordFulkersonPathStep<String, Integer>(a, graph3r.getEdges(a, b).iterator().next()),
                            new FordFulkersonPathStep<String, Integer>(b, graph3r.getEdges(b, c).iterator().next()),
                            new FordFulkersonPathStep<String, Integer>(c, graph3r.getEdges(c, d).iterator().next())
                        )
                    ),
                    new FordFulkersonDoubleStep(
                        new GraphWithLayout<String, FlowAndCapacity, Integer>(graph4, layout),
                        Set.of(
                            new FordFulkersonPathStep<String, FlowAndCapacity>(a, graph4.getEdges(a, b).iterator().next()),
                            new FordFulkersonPathStep<String, FlowAndCapacity>(b, graph4.getEdges(b, c).iterator().next()),
                            new FordFulkersonPathStep<String, FlowAndCapacity>(c, graph4.getEdges(c, d).iterator().next())
                        ),
                        new GraphWithLayout<String, Integer, Integer>(graph4r, residualLayout),
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
        final GraphLayout<String, Integer, Integer> layout = new DummyGraphLayout<String, Integer, Integer>();
        return new Object[][] {
            {
                new GraphProblem(
                    new GraphWithLayout<String, Integer, Integer>(
                        Graph.create(adjacencyLists1),
                        layout
                    ),
                    a,
                    StringVertexComparator.INSTANCE
                ),
                new KruskalResult<String>(
                    result,
                    new GraphWithLayout<String, Integer, Integer>(Graph.create(adjacencyLists2), layout)
                )
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
            final GraphLayout<String, Integer, Integer> layout = new DummyGraphLayout<String, Integer, Integer>();
        return new Object[][] {
            {
                new GraphProblem(
                    new GraphWithLayout<String, Integer, Integer>(
                        Graph.create(adjacencyLists1),
                        layout
                    ),
                    e,
                    StringVertexComparator.INSTANCE
                ),
                new PrimResult<String>(
                    table,
                    new GraphWithLayout<String, Integer, Integer>(Graph.create(adjacencyLists2), layout)
                )
            }
        };
    }

}
