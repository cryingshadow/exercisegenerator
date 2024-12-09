package exercisegenerator.structures.trees;

import java.util.*;

public class BinaryTreeNodeFactory<T extends Comparable<T>> implements SearchTreeNodeFactory<T> {

    @Override
    public BinaryTreeNode<T> create(final T value) {
        return new BinaryTreeNode<T>(value, this);
    }

    public BinaryTreeNode<T> create(
        final T value,
        final Optional<? extends BinaryTreeNode<T>> leftChild,
        final Optional<? extends BinaryTreeNode<T>> rightChild
    ) {
        return new BinaryTreeNode<T>(value, leftChild, rightChild, this);
    }

}
