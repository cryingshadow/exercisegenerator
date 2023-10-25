package exercisegenerator.structures.trees;

import java.util.*;

public class BinaryTreeNodeSteps<T extends Comparable<T>> extends LinkedList<BinaryTreeNodeAndStep<T>> {

    private static final long serialVersionUID = 3045297711825057413L;

    public BinaryTreeNodeSteps() {

    }

    public BinaryTreeNodeSteps(final BinaryTreeNode<T> node, final BinaryTreeStep<T> step) {
        super(Collections.singleton(new BinaryTreeNodeAndStep<T>(node, step)));
    }

    public BinaryTreeNodeSteps(final Optional<? extends BinaryTreeNode<T>> node, final BinaryTreeStep<T> step) {
        super(Collections.singleton(new BinaryTreeNodeAndStep<T>(node, step)));
    }

}
