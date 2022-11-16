package exercisegenerator.structures.trees;

import java.util.*;

public class BinaryTreeSteps<T extends Comparable<T>> extends LinkedList<BinaryTreeAndStep<T>> {

    private static final long serialVersionUID = 1094279806742181947L;

    public BinaryTreeSteps() {

    }

    public BinaryTreeSteps(final BinaryTree<T> tree, final BinaryTreeStep<T> step) {
        super(Collections.singleton(new BinaryTreeAndStep<T>(tree, step)));
    }

}
