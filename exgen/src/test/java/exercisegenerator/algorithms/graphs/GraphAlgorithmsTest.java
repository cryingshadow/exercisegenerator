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

    private static final Vertex<String> A;

    private static final Vertex<String> B;

    private static final Vertex<String> C;

    private static final Vertex<String> D;

    private static final Vertex<String> E;

    private static final Vertex<String> F;

    private static final Vertex<String> G;

    private static final List<Graph<String, Integer>> GRAPHS;

    private static final Vertex<String> H;

    private static final Vertex<String> I;

    private static final Vertex<String> J;

    private static final List<PetriNetInput> PETRI_NETS;

    static {
        A = new Vertex<String>("A");
        B = new Vertex<String>("B");
        C = new Vertex<String>("C");
        D = new Vertex<String>("D");
        E = new Vertex<String>("E");
        F = new Vertex<String>("F");
        G = new Vertex<String>("G");
        H = new Vertex<String>("H");
        I = new Vertex<String>("I");
        J = new Vertex<String>("J");
        final AdjacencySets<String, Integer> adjacencySets1 = new AdjacencySets<String, Integer>();
        adjacencySets1.put(GraphAlgorithmsTest.A, Set.of());
        final AdjacencySets<String, Integer> adjacencySets2 = new AdjacencySets<String, Integer>();
        adjacencySets2.addEdge(GraphAlgorithmsTest.A, 0, GraphAlgorithmsTest.B);
        adjacencySets2.addEdge(GraphAlgorithmsTest.A, 0, GraphAlgorithmsTest.C);
        adjacencySets2.addEdge(GraphAlgorithmsTest.A, 0, GraphAlgorithmsTest.D);
        final AdjacencySets<String, Integer> adjacencySets3 = new AdjacencySets<String, Integer>();
        adjacencySets3.addEdge(GraphAlgorithmsTest.A, 1, GraphAlgorithmsTest.B);
        adjacencySets3.addEdge(GraphAlgorithmsTest.A, 7, GraphAlgorithmsTest.D);
        adjacencySets3.addEdge(GraphAlgorithmsTest.B, 6, GraphAlgorithmsTest.C);
        adjacencySets3.addEdge(GraphAlgorithmsTest.B, 1, GraphAlgorithmsTest.E);
        adjacencySets3.addEdge(GraphAlgorithmsTest.C, 1, GraphAlgorithmsTest.B);
        adjacencySets3.addEdge(GraphAlgorithmsTest.C, 1, GraphAlgorithmsTest.F);
        adjacencySets3.addEdge(GraphAlgorithmsTest.D, 1, GraphAlgorithmsTest.A);
        adjacencySets3.addEdge(GraphAlgorithmsTest.D, 5, GraphAlgorithmsTest.G);
        adjacencySets3.addEdge(GraphAlgorithmsTest.E, 2, GraphAlgorithmsTest.B);
        adjacencySets3.addEdge(GraphAlgorithmsTest.E, 3, GraphAlgorithmsTest.D);
        adjacencySets3.addEdge(GraphAlgorithmsTest.E, 4, GraphAlgorithmsTest.F);
        adjacencySets3.addEdge(GraphAlgorithmsTest.E, 9, GraphAlgorithmsTest.G);
        adjacencySets3.addEdge(GraphAlgorithmsTest.F, 1, GraphAlgorithmsTest.C);
        adjacencySets3.addEdge(GraphAlgorithmsTest.G, 1, GraphAlgorithmsTest.D);
        final AdjacencySets<String, Integer> adjacencySets4 = new AdjacencySets<String, Integer>();
        adjacencySets4.addEdge(GraphAlgorithmsTest.A, 0, GraphAlgorithmsTest.B);
        adjacencySets4.addEdge(GraphAlgorithmsTest.A, 0, GraphAlgorithmsTest.D);
        adjacencySets4.addEdge(GraphAlgorithmsTest.A, 0, GraphAlgorithmsTest.C);
        adjacencySets4.addEdge(GraphAlgorithmsTest.B, 0, GraphAlgorithmsTest.E);
        adjacencySets4.addEdge(GraphAlgorithmsTest.B, 0, GraphAlgorithmsTest.F);
        adjacencySets4.addEdge(GraphAlgorithmsTest.C, 0, GraphAlgorithmsTest.G);
        adjacencySets4.addEdge(GraphAlgorithmsTest.C, 0, GraphAlgorithmsTest.H);
        adjacencySets4.addEdge(GraphAlgorithmsTest.D, 0, GraphAlgorithmsTest.I);
        adjacencySets4.addEdge(GraphAlgorithmsTest.D, 0, GraphAlgorithmsTest.J);
        adjacencySets4.addEdge(GraphAlgorithmsTest.E, 0, GraphAlgorithmsTest.A);
        adjacencySets4.addEdge(GraphAlgorithmsTest.F, 0, GraphAlgorithmsTest.A);
        adjacencySets4.addEdge(GraphAlgorithmsTest.G, 0, GraphAlgorithmsTest.D);
        adjacencySets4.addEdge(GraphAlgorithmsTest.H, 0, GraphAlgorithmsTest.C);
        adjacencySets4.addEdge(GraphAlgorithmsTest.I, 0, GraphAlgorithmsTest.A);
        adjacencySets4.addEdge(GraphAlgorithmsTest.J, 0, GraphAlgorithmsTest.J);
        final AdjacencySets<String, Integer> adjacencySets5 = new AdjacencySets<String, Integer>();
        adjacencySets5.addEdge(GraphAlgorithmsTest.A, 1, GraphAlgorithmsTest.B);
        adjacencySets5.addEdge(GraphAlgorithmsTest.B, 1, GraphAlgorithmsTest.A);
        adjacencySets5.addEdge(GraphAlgorithmsTest.A, 7, GraphAlgorithmsTest.D);
        adjacencySets5.addEdge(GraphAlgorithmsTest.D, 7, GraphAlgorithmsTest.A);
        adjacencySets5.addEdge(GraphAlgorithmsTest.B, 6, GraphAlgorithmsTest.C);
        adjacencySets5.addEdge(GraphAlgorithmsTest.C, 6, GraphAlgorithmsTest.B);
        adjacencySets5.addEdge(GraphAlgorithmsTest.B, 2, GraphAlgorithmsTest.E);
        adjacencySets5.addEdge(GraphAlgorithmsTest.E, 2, GraphAlgorithmsTest.B);
        adjacencySets5.addEdge(GraphAlgorithmsTest.C, 1, GraphAlgorithmsTest.F);
        adjacencySets5.addEdge(GraphAlgorithmsTest.F, 1, GraphAlgorithmsTest.C);
        adjacencySets5.addEdge(GraphAlgorithmsTest.D, 3, GraphAlgorithmsTest.E);
        adjacencySets5.addEdge(GraphAlgorithmsTest.E, 3, GraphAlgorithmsTest.D);
        adjacencySets5.addEdge(GraphAlgorithmsTest.D, 5, GraphAlgorithmsTest.G);
        adjacencySets5.addEdge(GraphAlgorithmsTest.G, 5, GraphAlgorithmsTest.D);
        adjacencySets5.addEdge(GraphAlgorithmsTest.E, 4, GraphAlgorithmsTest.F);
        adjacencySets5.addEdge(GraphAlgorithmsTest.F, 4, GraphAlgorithmsTest.E);
        adjacencySets5.addEdge(GraphAlgorithmsTest.E, 9, GraphAlgorithmsTest.G);
        adjacencySets5.addEdge(GraphAlgorithmsTest.G, 9, GraphAlgorithmsTest.E);
        final AdjacencySets<String, Integer> adjacencySets6 = new AdjacencySets<String, Integer>();
        adjacencySets6.addEdge(GraphAlgorithmsTest.A, 1, GraphAlgorithmsTest.B);
        adjacencySets6.addEdge(GraphAlgorithmsTest.B, 1, GraphAlgorithmsTest.A);
        adjacencySets6.addEdge(GraphAlgorithmsTest.C, 1, GraphAlgorithmsTest.F);
        adjacencySets6.addEdge(GraphAlgorithmsTest.F, 1, GraphAlgorithmsTest.C);
        adjacencySets6.addEdge(GraphAlgorithmsTest.B, 2, GraphAlgorithmsTest.E);
        adjacencySets6.addEdge(GraphAlgorithmsTest.E, 2, GraphAlgorithmsTest.B);
        adjacencySets6.addEdge(GraphAlgorithmsTest.D, 3, GraphAlgorithmsTest.E);
        adjacencySets6.addEdge(GraphAlgorithmsTest.E, 3, GraphAlgorithmsTest.D);
        adjacencySets6.addEdge(GraphAlgorithmsTest.E, 4, GraphAlgorithmsTest.F);
        adjacencySets6.addEdge(GraphAlgorithmsTest.F, 4, GraphAlgorithmsTest.E);
        adjacencySets6.addEdge(GraphAlgorithmsTest.D, 5, GraphAlgorithmsTest.G);
        adjacencySets6.addEdge(GraphAlgorithmsTest.G, 5, GraphAlgorithmsTest.D);
        GRAPHS =
            List.of(
                Graph.create(adjacencySets1),
                Graph.create(adjacencySets2),
                Graph.create(adjacencySets3),
                Graph.create(adjacencySets4),
                Graph.create(adjacencySets5),
                Graph.create(adjacencySets6)
            );
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
        final List<Integer> one1Pure = List.of(1, 0, 0, 0);
        PETRI_NETS =
            List.of(
                new PetriNetInput(List.of(), List.of(), List.of()),
                new PetriNetInput(places, transitions, List.of(0, 0, 0, 0)),
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
                        GraphAlgorithmsTest.GRAPHS.get(2),
                        new DummyGraphLayout<String, Integer, Integer>()
                    ),
                    Optional.of(GraphAlgorithmsTest.E),
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
        return new Object[][] {
            {
                new GraphProblem(
                    new GraphWithLayout<String, Integer, Integer>(
                        GraphAlgorithmsTest.GRAPHS.get(0),
                        new DummyGraphLayout<String, Integer, Integer>()
                    ),
                    GraphAlgorithmsTest.A
                ),
                List.of("A")
            },
            {
                new GraphProblem(
                    new GraphWithLayout<String, Integer, Integer>(
                        GraphAlgorithmsTest.GRAPHS.get(1),
                        new DummyGraphLayout<String, Integer, Integer>()
                    ),
                    GraphAlgorithmsTest.A
                ),
                List.of("A", "B", "C", "D")
            },
            {
                new GraphProblem(
                    new GraphWithLayout<String, Integer, Integer>(
                        GraphAlgorithmsTest.GRAPHS.get(3),
                        new DummyGraphLayout<String, Integer, Integer>()
                    ),
                    GraphAlgorithmsTest.A
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
        final PetriMarking zero = PetriMarking.createZeroMarking(4);
        final PetriMarking one1 = PetriMarking.create(1, 0, 0, 0);
        final PetriMarking one2 = PetriMarking.create(0, 1, 0, 0);
        final PetriMarking one3 = PetriMarking.create(0, 0, 1, 0);
        final PetriMarking one4 = PetriMarking.create(0, 0, 0, 1);
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
        final PetriMarking inf1 = PetriMarking.create(Optional.empty(), Optional.of(0), Optional.of(0), Optional.of(0));
        final PetriMarking inf2 =
            PetriMarking.create(Optional.empty(), Optional.empty(), Optional.of(0), Optional.of(0));
        final PetriMarking inf3 =
            PetriMarking.create(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(0));
        final PetriMarking inf4 =
            PetriMarking.create(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
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
                new CoverabilityGraph(new Vertex<PetriMarking>(PetriMarking.createZeroMarking(0)))
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
        return new Object[][] {
            {
                new GraphProblem(
                    new GraphWithLayout<String, Integer, Integer>(
                        GraphAlgorithmsTest.GRAPHS.get(0),
                        new DummyGraphLayout<String, Integer, Integer>()
                    ),
                    GraphAlgorithmsTest.A
                ),
                List.of("A")
            },
            {
                new GraphProblem(
                    new GraphWithLayout<String, Integer, Integer>(
                        GraphAlgorithmsTest.GRAPHS.get(1),
                        new DummyGraphLayout<String, Integer, Integer>()
                    ),
                    GraphAlgorithmsTest.A
                ),
                List.of("A", "B", "C", "D")
            },
            {
                new GraphProblem(
                    new GraphWithLayout<String, Integer, Integer>(
                        GraphAlgorithmsTest.GRAPHS.get(3),
                        new DummyGraphLayout<String, Integer, Integer>()
                    ),
                    GraphAlgorithmsTest.A
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
        return new Object[][] {
            {
                new GraphProblem(
                    new GraphWithLayout<String, Integer, Integer>(
                        GraphAlgorithmsTest.GRAPHS.get(2),
                        new DummyGraphLayout<String, Integer, Integer>()
                    ),
                    GraphAlgorithmsTest.E
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
                new PetriNetInput(List.of(p1, p2, p3, p4, p5), List.of(t1, t2, t3, t4), List.of()),
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
                new PetriNetInput(List.of(p1, p2, p3, p4, p5), List.of(t1, t2, t3, t4), List.of()),
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
        final AdjacencySets<String, Integer> adjacencySets1 = new AdjacencySets<String, Integer>();
        adjacencySets1.addEdge(a, 1, b);
        adjacencySets1.addEdge(a, 7, d);
        adjacencySets1.addEdge(b, 6, c);
        adjacencySets1.addEdge(b, 1, e);
        adjacencySets1.addEdge(c, 1, b);
        adjacencySets1.addEdge(d, 1, a);
        adjacencySets1.addEdge(e, 2, b);
        adjacencySets1.addEdge(e, 3, d);
        return new Object[][] {
            {
                new GraphProblem(
                    new GraphWithLayout<String, Integer, Integer>(
                        Graph.create(adjacencySets1),
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
        final AdjacencySets<String, FlowAndCapacity> adjacencySets1 = new AdjacencySets<String, FlowAndCapacity>();
        adjacencySets1.addEdge(a, new FlowAndCapacity(0, 4), b);
        adjacencySets1.addEdge(a, new FlowAndCapacity(0, 3), c);
        adjacencySets1.addEdge(b, new FlowAndCapacity(0, 4), c);
        adjacencySets1.addEdge(c, new FlowAndCapacity(0, 4), d);
        adjacencySets1.addEdge(b, new FlowAndCapacity(0, 3), d);
        final AdjacencySets<String, Integer> adjacencySets1r = new AdjacencySets<String, Integer>();
        adjacencySets1r.addEdge(a, 4, b);
        adjacencySets1r.addEdge(a, 3, c);
        adjacencySets1r.addEdge(b, 4, c);
        adjacencySets1r.addEdge(c, 4, d);
        adjacencySets1r.addEdge(b, 3, d);
        final AdjacencySets<String, FlowAndCapacity> adjacencySets2 = new AdjacencySets<String, FlowAndCapacity>();
        adjacencySets2.addEdge(a, new FlowAndCapacity(3, 4), b);
        adjacencySets2.addEdge(a, new FlowAndCapacity(0, 3), c);
        adjacencySets2.addEdge(b, new FlowAndCapacity(0, 4), c);
        adjacencySets2.addEdge(c, new FlowAndCapacity(0, 4), d);
        adjacencySets2.addEdge(b, new FlowAndCapacity(3, 3), d);
        final AdjacencySets<String, Integer> adjacencySets2r = new AdjacencySets<String, Integer>();
        adjacencySets2r.addEdge(a, 1, b);
        adjacencySets2r.addEdge(b, 3, a);
        adjacencySets2r.addEdge(a, 3, c);
        adjacencySets2r.addEdge(b, 4, c);
        adjacencySets2r.addEdge(c, 4, d);
        adjacencySets2r.addEdge(d, 3, b);
        final AdjacencySets<String, FlowAndCapacity> adjacencySets3 = new AdjacencySets<String, FlowAndCapacity>();
        adjacencySets3.addEdge(a, new FlowAndCapacity(3, 4), b);
        adjacencySets3.addEdge(a, new FlowAndCapacity(3, 3), c);
        adjacencySets3.addEdge(b, new FlowAndCapacity(0, 4), c);
        adjacencySets3.addEdge(c, new FlowAndCapacity(3, 4), d);
        adjacencySets3.addEdge(b, new FlowAndCapacity(3, 3), d);
        final AdjacencySets<String, Integer> adjacencySets3r = new AdjacencySets<String, Integer>();
        adjacencySets3r.addEdge(a, 1, b);
        adjacencySets3r.addEdge(b, 3, a);
        adjacencySets3r.addEdge(c, 3, a);
        adjacencySets3r.addEdge(b, 4, c);
        adjacencySets3r.addEdge(c, 1, d);
        adjacencySets3r.addEdge(d, 3, c);
        adjacencySets3r.addEdge(d, 3, b);
        final AdjacencySets<String, FlowAndCapacity> adjacencySets4 = new AdjacencySets<String, FlowAndCapacity>();
        adjacencySets4.addEdge(a, new FlowAndCapacity(4, 4), b);
        adjacencySets4.addEdge(a, new FlowAndCapacity(3, 3), c);
        adjacencySets4.addEdge(b, new FlowAndCapacity(1, 4), c);
        adjacencySets4.addEdge(c, new FlowAndCapacity(4, 4), d);
        adjacencySets4.addEdge(b, new FlowAndCapacity(3, 3), d);
        final AdjacencySets<String, Integer> adjacencySets4r = new AdjacencySets<String, Integer>();
        adjacencySets4r.addEdge(b, 4, a);
        adjacencySets4r.addEdge(c, 3, a);
        adjacencySets4r.addEdge(b, 3, c);
        adjacencySets4r.addEdge(c, 1, b);
        adjacencySets4r.addEdge(d, 4, c);
        adjacencySets4r.addEdge(d, 3, b);
        final Graph<String, FlowAndCapacity> graph1 = Graph.create(adjacencySets1);
        final Graph<String, FlowAndCapacity> graph2 = Graph.create(adjacencySets2);
        final Graph<String, FlowAndCapacity> graph3 = Graph.create(adjacencySets3);
        final Graph<String, FlowAndCapacity> graph4 = Graph.create(adjacencySets4);
        final Graph<String, Integer> graph1r = Graph.create(adjacencySets1r);
        final Graph<String, Integer> graph2r = Graph.create(adjacencySets2r);
        final Graph<String, Integer> graph3r = Graph.create(adjacencySets3r);
        final Graph<String, Integer> graph4r = Graph.create(adjacencySets4r);
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
                            new FordFulkersonPathStep<String, FlowAndCapacity>(
                                a,
                                graph2.getEdges(a, b).iterator().next()
                            ),
                            new FordFulkersonPathStep<String, FlowAndCapacity>(
                                b,
                                graph2.getEdges(b, d).iterator().next()
                             )
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
                            new FordFulkersonPathStep<String, FlowAndCapacity>(
                                a,
                                graph3.getEdges(a, c).iterator().next()
                            ),
                            new FordFulkersonPathStep<String, FlowAndCapacity>(
                                c,
                                graph3.getEdges(c, d).iterator().next()
                            )
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
                            new FordFulkersonPathStep<String, FlowAndCapacity>(
                                a,
                                graph4.getEdges(a, b).iterator().next()
                            ),
                            new FordFulkersonPathStep<String, FlowAndCapacity>(
                                b,
                                graph4.getEdges(b, c).iterator().next()
                            ),
                            new FordFulkersonPathStep<String, FlowAndCapacity>(
                                c,
                                graph4.getEdges(c, d).iterator().next()
                            )
                        ),
                        new GraphWithLayout<String, Integer, Integer>(graph4r, residualLayout),
                        Collections.emptySet()
                    )
                )
            }
        };
    }

    @Test(dataProvider="kosarajuSharirData")
    public void kosarajuSharir(final GraphProblem problem, final KosarajuSharirResult expected) {
        Assert.assertEquals(KosarajuSharirAlgorithm.INSTANCE.apply(problem), expected);
    }

    @DataProvider
    public Object[][] kosarajuSharirData() {
        final GraphLayout<String, Integer, Integer> layout = new DummyGraphLayout<String, Integer, Integer>();
        return new Object[][] {
            {
                new GraphProblem(
                    new GraphWithLayout<String, Integer, Integer>(GraphAlgorithmsTest.GRAPHS.get(3), layout)
                ),
                new KosarajuSharirResult(
                    List.of("A", "C", "H", "G", "D", "J", "I", "B", "F", "E"),
                    Map.of(
                        "A", "A",
                        "B", "A",
                        "C", "A",
                        "D", "A",
                        "E", "A",
                        "F", "A",
                        "G", "A",
                        "H", "A",
                        "I", "A",
                        "J", "J"
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
        final List<UndirectedEdge<String, Integer>> result =
            List.of(
                new UndirectedEdge<String, Integer>(GraphAlgorithmsTest.A, 1, GraphAlgorithmsTest.B),
                new UndirectedEdge<String, Integer>(GraphAlgorithmsTest.C, 1, GraphAlgorithmsTest.F),
                new UndirectedEdge<String, Integer>(GraphAlgorithmsTest.B, 2, GraphAlgorithmsTest.E),
                new UndirectedEdge<String, Integer>(GraphAlgorithmsTest.D, 3, GraphAlgorithmsTest.E),
                new UndirectedEdge<String, Integer>(GraphAlgorithmsTest.E, 4, GraphAlgorithmsTest.F),
                new UndirectedEdge<String, Integer>(GraphAlgorithmsTest.D, 5, GraphAlgorithmsTest.G)
            );
        final GraphLayout<String, Integer, Integer> layout = new DummyGraphLayout<String, Integer, Integer>();
        return new Object[][] {
            {
                new GraphProblem(
                    new GraphWithLayout<String, Integer, Integer>(GraphAlgorithmsTest.GRAPHS.get(4), layout),
                    GraphAlgorithmsTest.A
                ),
                new KruskalResult<String>(
                    result,
                    new GraphWithLayout<String, Integer, Integer>(GraphAlgorithmsTest.GRAPHS.get(5), layout)
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
                        GraphAlgorithmsTest.GRAPHS.get(4),
                        layout
                    ),
                    GraphAlgorithmsTest.E
                ),
                new PrimResult<String>(
                    table,
                    new GraphWithLayout<String, Integer, Integer>(GraphAlgorithmsTest.GRAPHS.get(5), layout)
                )
            }
        };
    }

    @Test(dataProvider="topologicSortData")
    public void topologicSort(final GraphProblem problem, final List<String> expected) {
        Assert.assertEquals(TopologicSort.INSTANCE.apply(problem), expected);
    }

    @DataProvider
    public Object[][] topologicSortData() {
        return new Object[][] {
            {
                new GraphProblem(
                    new GraphWithLayout<String, Integer, Integer>(
                        GraphAlgorithmsTest.GRAPHS.get(1),
                        new DummyGraphLayout<String, Integer, Integer>()
                    )
                ),
                List.of("A", "D", "C", "B")
            },
            {
                new GraphProblem(
                    new GraphWithLayout<String, Integer, Integer>(
                        GraphAlgorithmsTest.GRAPHS.get(2),
                        new DummyGraphLayout<String, Integer, Integer>()
                    )
                ),
                null
            }
        };
    }

    @Test(dataProvider="unionFindData")
    public void unionFind(final UnionFindProblem problem, final UnionFind<Integer> expected) {
        Assert.assertEquals(UnionFindAlgorithm.INSTANCE.apply(problem), expected);
    }

    @DataProvider
    public Object[][] unionFindData() {
        return new Object[][] {
            {
                new UnionFindProblem(
                    new UnionFind<Integer>(),
                    List.of(
                        new FindOperation<Integer>(1),
                        new FindOperation<Integer>(2),
                        new FindOperation<Integer>(3)
                    )
                ),
                new UnionFind<Integer>(
                    Map.of(
                        1, 1,
                        2, 2,
                        3, 3
                    )
                )
            },
            {
                new UnionFindProblem(
                    new UnionFind<Integer>(
                        Map.of(
                            1, 1,
                            2, 1,
                            3, 2,
                            4, 4,
                            5, 4,
                            6, 5
                        )
                    ),
                    List.of(
                        new UnionOperation<Integer>(3,6)
                    )
                ),
                new UnionFind<Integer>(
                    Map.of(
                        1, 4,
                        2, 1,
                        3, 1,
                        4, 4,
                        5, 4,
                        6, 4
                    )
                )
            }
        };
    }

}
