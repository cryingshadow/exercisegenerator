package exercisegenerator.structures.trees;

import java.util.*;

public class BinaryTreeFactory<T extends Comparable<T>> implements SearchTreeFactory<T> {

    final BinaryTreeNodeFactory<T> nodeFactory;

    public BinaryTreeFactory(final BinaryTreeNodeFactory<T> nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    public BinaryTree<T> create(final BinaryTreeNode<T> root) {
        return this.create(Optional.of(root));
    }

    @Override
    @SuppressWarnings("unchecked")
    public BinaryTree<T> create(final Optional<? extends SearchTreeNode<T>> root) {
        return new BinaryTree<T>((Optional<? extends BinaryTreeNode<T>>)root, this);
    }

    public BinaryTree<T> create(final T rootValue) {
        return this.create(this.nodeFactory.create(rootValue));
    }

}
