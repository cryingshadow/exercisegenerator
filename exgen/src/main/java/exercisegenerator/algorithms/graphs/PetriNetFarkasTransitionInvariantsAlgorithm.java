package exercisegenerator.algorithms.graphs;

import exercisegenerator.structures.algebra.*;
import exercisegenerator.structures.graphs.petrinets.*;

public class PetriNetFarkasTransitionInvariantsAlgorithm extends PetriNetFarkasAlgorithm {

    public static final PetriNetFarkasTransitionInvariantsAlgorithm INSTANCE =
        new PetriNetFarkasTransitionInvariantsAlgorithm();

    private PetriNetFarkasTransitionInvariantsAlgorithm() {}

    @Override
    public String commandPrefix() {
        return "FarkasTransition";
    }

    @Override
    protected Matrix getIncidenceMatrix(final PetriNet net) {
        return net.toIncidenceMatrix().transpose();
    }

    @Override
    protected String kindOfInvariant() {
        return "T";
    }

}
