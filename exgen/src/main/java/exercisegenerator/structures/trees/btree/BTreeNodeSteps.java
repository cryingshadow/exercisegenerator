package exercisegenerator.structures.trees.btree;

import java.util.*;

public class BTreeNodeSteps<T extends Comparable<T>> extends LinkedList<BTreeNodeAndStep<T>> {

    private static final long serialVersionUID = 378438538788637590L;

    public BTreeNodeSteps() {

    }

    public BTreeNodeSteps(final BTreeNode<T> node, final BTreeStep<T> step) {
        super(Collections.singleton(new BTreeNodeAndStep<T>(node, step)));
    }

    public BTreeNodeSteps(final Optional<BTreeNode<T>> node, final BTreeStep<T> step) {
        super(Collections.singleton(new BTreeNodeAndStep<T>(node, step)));
    }

}
