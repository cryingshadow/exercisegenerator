package exercisegenerator.structures.trees;

import java.util.*;

public class RedBlackTreeFactory<T extends Comparable<T>> extends BinaryTreeFactory<T> {

    public RedBlackTreeFactory(final RedBlackTreeNodeFactory<T> nodeFactory) {
        super(nodeFactory);
    }

    @Override
    @SuppressWarnings("unchecked")
    public BinaryTree<T> create(final Optional<? extends SearchTreeNode<T>> root) {
        return new RedBlackTree<T>((Optional<RedBlackTreeNode<T>>)root, this);
    }

    @Override
    public BinaryTree<T> create(final T rootValue) {
        return this.create(((RedBlackTreeNode<T>)this.nodeFactory.create(rootValue)).toggleRoot());
    }

}
