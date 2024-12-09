package exercisegenerator.structures.trees;

import java.util.*;

public record SearchTreeNodeAndStep<T extends Comparable<T>>(
    Optional<? extends SearchTreeNode<T>> node,
    SearchTreeStep<T> step
) {}
