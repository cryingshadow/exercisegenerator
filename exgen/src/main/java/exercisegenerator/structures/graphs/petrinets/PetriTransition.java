package exercisegenerator.structures.graphs.petrinets;

import java.util.*;

public record PetriTransition(String label, int x, int y, Map<Integer, Integer> from, Map<Integer, Integer> to) {}
