package exercisegenerator.structures.trees;

import exercisegenerator.structures.*;

public class BinaryTreeAndStep<T extends Comparable<T>> extends Pair<BinaryTree<T>, BinaryTreeStep<T>> {

    private static final long serialVersionUID = -6432707995959624885L;

    public BinaryTreeAndStep(final BinaryTree<T> tree, final BinaryTreeStep<T> step) {
        super(tree, step);
    }

}
