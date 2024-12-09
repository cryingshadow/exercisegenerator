package exercisegenerator.structures.trees;

import java.util.*;

public record SearchTreeProblem(
    SearchTree<Integer> tree,
    Deque<TreeOperation<Integer>> operations,
    SearchTreeFactory<Integer> factory
) {}
