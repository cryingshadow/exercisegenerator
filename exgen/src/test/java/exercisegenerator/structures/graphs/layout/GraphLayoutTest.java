package exercisegenerator.structures.graphs.layout;

import java.util.Optional;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.*;
import exercisegenerator.structures.graphs.petrinets.*;

public class GraphLayoutTest {

    @Test
    public void layoutTest() {
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
        final GraphLayout<PetriMarking, String, Double> layout1 =
            new ForceGraphLayout<PetriMarking, String>(graph1, TikZStyle.FORCE_GRAPH, 1, 1, 12, 12);
        final GraphLayout<PetriMarking, String, Double> layout2 =
            new ForceGraphLayout<PetriMarking, String>(graph2, TikZStyle.FORCE_GRAPH, 1, 1, 12, 12);
        final Coordinates2D<Double> coordinates1 = layout1.getPosition(vertex4);
        Assert.assertEquals(coordinates1.x(), 2.0996, 0.001);
        Assert.assertEquals(coordinates1.y(), 5.4024, 0.001);
        final Coordinates2D<Double> coordinates2 = layout2.getPosition(vertex8);
        Assert.assertEquals(coordinates2.x(), 0.8066, 0.001);
        Assert.assertEquals(coordinates2.y(), 0.0, 0.001);
    }

}
