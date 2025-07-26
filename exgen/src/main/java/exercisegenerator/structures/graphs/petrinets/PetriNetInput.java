package exercisegenerator.structures.graphs.petrinets;

import java.util.*;

public record PetriNetInput(
    List<PetriPlace> places,
    List<PetriTransition> transitions,
    Map<Integer, Integer> tokens
) {}
