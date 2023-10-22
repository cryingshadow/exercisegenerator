package exercisegenerator.structures.trees.btree;

import java.util.*;

import exercisegenerator.structures.*;

public class BTreeNodeAndStep<T extends Comparable<T>> extends Pair<Optional<BTreeNode<T>>, BTreeStep<T>> {

    private static final long serialVersionUID = 140276646597968356L;

    public BTreeNodeAndStep(final BTreeNode<T> node, final BTreeStep<T> step) {
        super(Optional.of(node), step);
    }

    public BTreeNodeAndStep(final Optional<BTreeNode<T>> node, final BTreeStep<T> step) {
        super(node, step);
    }

}
