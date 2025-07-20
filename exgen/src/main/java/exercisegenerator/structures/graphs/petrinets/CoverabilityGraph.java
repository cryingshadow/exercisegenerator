package exercisegenerator.structures.graphs.petrinets;

import exercisegenerator.structures.graphs.*;

public class CoverabilityGraph extends Graph<PetriMarking, String> {

    private final Vertex<PetriMarking> start;

    public CoverabilityGraph(final Vertex<PetriMarking> start) {
        super();
        this.addVertex(start);
        this.start = start;
    }

}
