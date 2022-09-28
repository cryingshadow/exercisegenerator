package exercisegenerator.structures.trees;

import java.util.*;

import exercisegenerator.structures.*;

public class BinaryTreeNodeAndStep<T extends Comparable<T>> extends Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep> {

    private static final long serialVersionUID = 4717977696974699847L;

    public BinaryTreeNodeAndStep(final BinaryTreeNode<T> node, final BinaryTreeStep step) {
        super(Optional.of(node), step);
    }

    public BinaryTreeNodeAndStep(final Optional<BinaryTreeNode<T>> node, final BinaryTreeStep step) {
        super(node, step);
    }

}
