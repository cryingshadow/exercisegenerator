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
        final GraphLayout<PetriMarking, String, Double> layout1 =
            new ForceGraphLayout<PetriMarking, String>(graph1, TikZStyle.FORCE_GRAPH, 1, 1, 12, 12);
        final GraphLayout<PetriMarking, String, Double> layout2 =
            new ForceGraphLayout<PetriMarking, String>(graph2, TikZStyle.FORCE_GRAPH, 1, 1, 12, 12);
        final Coordinates2D<Double> coordinates1 = layout1.getPosition(vertex4);
        Assert.assertEquals(coordinates1.x(), 5.4016, 0.001);
        Assert.assertEquals(coordinates1.y(), 2.0997, 0.001);
        Assert.assertEquals(layout2.getPosition(vertex8), new Coordinates2D<Double>(0.0, 0.0));
    }

}
