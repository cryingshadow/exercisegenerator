package exercisegenerator.algorithms.graphs;

import exercisegenerator.structures.algebra.*;
import exercisegenerator.structures.graphs.petrinets.*;

public class PetriNetFarkasPlaceInvariantsAlgorithm extends PetriNetFarkasAlgorithm {

    public static final PetriNetFarkasPlaceInvariantsAlgorithm INSTANCE = new PetriNetFarkasPlaceInvariantsAlgorithm();

    private PetriNetFarkasPlaceInvariantsAlgorithm() {}

    @Override
    public String commandPrefix() {
        return "FarkasPlace";
    }

    @Override
    protected Matrix getIncidenceMatrix(final PetriNet net) {
        return net.toIncidenceMatrix();
    }

    @Override
    protected String kindOfInvariant() {
        return "P";
    }

}
