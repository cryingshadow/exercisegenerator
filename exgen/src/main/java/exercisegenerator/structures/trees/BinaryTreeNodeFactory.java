package exercisegenerator.structures.trees;

import java.util.*;

public class BinaryTreeNodeFactory<T extends Comparable<T>> {

    public BinaryTreeNode<T> create(T value) {
        return new BinaryTreeNode<T>(value, this);
    }

    public BinaryTreeNode<T> create(
        T value,
        Optional<? extends BinaryTreeNode<T>> leftChild,
        Optional<? extends BinaryTreeNode<T>> rightChild
    ) {
        return new BinaryTreeNode<T>(value, leftChild, rightChild, this);
    }
    
}
