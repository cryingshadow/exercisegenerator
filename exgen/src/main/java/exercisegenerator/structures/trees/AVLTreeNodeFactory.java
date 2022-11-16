package exercisegenerator.structures.trees;

import java.util.*;

public class AVLTreeNodeFactory<T extends Comparable<T>> extends BinaryTreeNodeFactory<T> {

    @Override
    public BinaryTreeNode<T> create(T value) {
        return new AVLTreeNode<T>(value, this);
    }

    @Override
    public BinaryTreeNode<T> create(
        T value,
        Optional<? extends BinaryTreeNode<T>> leftChild,
        Optional<? extends BinaryTreeNode<T>> rightChild
    ) {
        return new AVLTreeNode<T>(value, leftChild, rightChild, this);
    }
    
}
