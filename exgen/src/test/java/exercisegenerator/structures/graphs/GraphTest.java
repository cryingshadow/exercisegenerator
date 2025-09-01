package exercisegenerator.structures.graphs;

import java.util.*;
import java.util.Optional;

import org.testng.*;
import org.testng.annotations.*;

public class GraphTest {

    private static final Vertex<String> A;

    private static final Vertex<String> B;

    private static final Vertex<String> C;

    private static final List<Graph<String, Integer>> GRAPHS;

    static {
        A = new Vertex<String>("A");
        B = new Vertex<String>("B");
        C = new Vertex<String>("C");
        GRAPHS =
            List.of(
                Graph.create(Map.of()),
                Graph.create(Map.of(GraphTest.A, Set.of())),
                Graph.create(Map.of(GraphTest.A, Set.of(new Edge<Integer, String>(Optional.of(1), GraphTest.B)))),
                Graph.create(
                    Map.of(
                        GraphTest.A, Set.of(new Edge<Integer, String>(Optional.of(1), GraphTest.B)),
                        GraphTest.B, Set.of(new Edge<Integer, String>(Optional.of(2), GraphTest.C)),
                        GraphTest.C, Set.of(new Edge<Integer, String>(Optional.of(-1), GraphTest.A))
                    )
                )
            );
    }

    @DataProvider
    public Object[][] getAllUndirectedEdgesData() {
        return new Object[][] {
            {GraphTest.GRAPHS.get(0), Set.of()},
            {GraphTest.GRAPHS.get(1), Set.of()},
            {GraphTest.GRAPHS.get(2), Set.of(new UndirectedEdge<String, Integer>(GraphTest.A, 1, GraphTest.B))},
            {
                GraphTest.GRAPHS.get(3),
                Set.of(
                    new UndirectedEdge<String, Integer>(GraphTest.A, 1, GraphTest.B),
                    new UndirectedEdge<String, Integer>(GraphTest.B, 2, GraphTest.C),
                    new UndirectedEdge<String, Integer>(GraphTest.C, -1, GraphTest.A)
                )
            }
        };
    }

    @Test(dataProvider="getAllUndirectedEdgesData")
    public void getAllUndirectedEdgesTest(
        final Graph<String, Integer> graph,
        final Set<UndirectedEdge<String, Integer>> expected
    ) {
        Assert.assertEquals(graph.getAllUndirectedEdges(), expected);
    }

    @DataProvider
    public Object[][] getVerticesData() {
        return new Object[][] {
            {GraphTest.GRAPHS.get(0), Set.of()},
            {GraphTest.GRAPHS.get(1), Set.of(GraphTest.A)},
            {GraphTest.GRAPHS.get(2), Set.of(GraphTest.A, GraphTest.B)},
            {GraphTest.GRAPHS.get(3), Set.of(GraphTest.A, GraphTest.B, GraphTest.C)}
        };
    }

    @Test(dataProvider="getVerticesData")
    public void getVerticesTest(final Graph<String, Integer> graph, final Set<Vertex<String>> expected) {
        Assert.assertEquals(graph.getVertices(), expected);
    }

    @DataProvider
    public Object[][] transposeData() {
        return new Object[][] {
            {GraphTest.GRAPHS.get(0), GraphTest.GRAPHS.get(0)},
            {GraphTest.GRAPHS.get(1), GraphTest.GRAPHS.get(1)},
            {
                GraphTest.GRAPHS.get(2),
                Graph.create(Map.of(GraphTest.B, Set.of(new Edge<Integer, String>(Optional.of(1), GraphTest.A)))),
            },
            {
                GraphTest.GRAPHS.get(3),
                Graph.create(
                    Map.of(
                        GraphTest.B, Set.of(new Edge<Integer, String>(Optional.of(1), GraphTest.A)),
                        GraphTest.C, Set.of(new Edge<Integer, String>(Optional.of(2), GraphTest.B)),
                        GraphTest.A, Set.of(new Edge<Integer, String>(Optional.of(-1), GraphTest.C))
                    )
                )
            }
        };
    }

    @Test(dataProvider="transposeData")
    public void transposeTest(final Graph<String, Integer> graph, final Graph<String, Integer> expected) {
        Assert.assertTrue(graph.transpose().logicallyEquals(expected));
    }

}
