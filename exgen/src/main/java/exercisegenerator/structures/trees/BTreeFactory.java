package exercisegenerator.structures.trees;

import java.util.*;

public class BTreeFactory<T extends Comparable<T>> implements SearchTreeFactory<T> {

    private final int degree;

    public BTreeFactory(final int degree) {
        this.degree = degree;
    }

    @SuppressWarnings("unchecked")
    @Override
    public BTree<T> create(final Optional<? extends SearchTreeNode<T>> root) {
        return new BTree<T>(this.degree, (Optional<BTreeNode<T>>)root);
    }

}
