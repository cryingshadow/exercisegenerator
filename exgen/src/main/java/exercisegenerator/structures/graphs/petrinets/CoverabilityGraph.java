package exercisegenerator.structures.graphs.petrinets;

import exercisegenerator.structures.graphs.*;

public class CoverabilityGraph extends Graph<PetriMarking, String> {

    public CoverabilityGraph(final Vertex<PetriMarking> start) {
        super();
        this.addVertex(start);
        this.setStartVertex(start);
    }

}
