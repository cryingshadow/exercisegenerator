package exercisegenerator.structures.graphs;

import java.util.*;

public record PetriNetInput(
    List<PetriPlace> places,
    List<PetriTransition> transitions,
    Map<Integer, Integer> tokens,
    Integer limit
) {}
