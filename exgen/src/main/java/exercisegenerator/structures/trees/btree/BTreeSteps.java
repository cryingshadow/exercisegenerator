package exercisegenerator.structures.trees.btree;

import java.util.*;

public class BTreeSteps<T extends Comparable<T>> extends LinkedList<BTreeAndStep<T>> {

    private static final long serialVersionUID = 2334537989567026390L;

    public BTreeSteps() {

    }

    public BTreeSteps(final BTree<T> tree, final BTreeStep<T> step) {
        super(Collections.singleton(new BTreeAndStep<T>(tree, step)));
    }

}
