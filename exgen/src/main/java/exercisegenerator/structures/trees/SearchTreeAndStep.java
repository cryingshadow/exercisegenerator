package exercisegenerator.structures.trees;

public record SearchTreeAndStep<T extends Comparable<T>>(SearchTree<T> tree, SearchTreeStep<T> step) {}
