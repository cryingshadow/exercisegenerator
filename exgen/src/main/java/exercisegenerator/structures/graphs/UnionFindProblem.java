package exercisegenerator.structures.graphs;

import java.util.*;

public record UnionFindProblem(UnionFind<Integer> initialState, List<UnionFindOperation<Integer>> operations) {}
