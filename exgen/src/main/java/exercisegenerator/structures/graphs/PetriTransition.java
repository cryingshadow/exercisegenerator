package exercisegenerator.structures.graphs;

import java.util.*;

public record PetriTransition(String label, int x, int y, Map<Integer, Integer> from, Map<Integer, Integer> to) {}
