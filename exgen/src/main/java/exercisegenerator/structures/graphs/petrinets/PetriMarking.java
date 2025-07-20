package exercisegenerator.structures.graphs.petrinets;

import java.util.*;

public class PetriMarking extends LinkedHashMap<Integer, Optional<Integer>> {

    private static final long serialVersionUID = 1L;

    public PetriMarking() {
        super();
    }

    public PetriMarking(final Map<Integer, Optional<Integer>> marking) {
        super(marking);
    }

}
