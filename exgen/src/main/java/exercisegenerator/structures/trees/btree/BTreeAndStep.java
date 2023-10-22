package exercisegenerator.structures.trees.btree;

import exercisegenerator.structures.*;

public class BTreeAndStep<T extends Comparable<T>> extends Pair<BTree<T>, BTreeStep<T>> {

    private static final long serialVersionUID = -8915940715461088539L;

    public BTreeAndStep(final BTree<T> tree, final BTreeStep<T> step) {
        super(tree, step);
    }

}
