package exercisegenerator.structures.graphs.petrinets;

import java.util.*;

public record PetriTransition(String label, double x, double y, Map<Integer, Integer> from, Map<Integer, Integer> to) {}
