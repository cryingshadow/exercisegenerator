package exercisegenerator.structures.trees;

import java.util.*;

public class AVLTreeNode<T extends Comparable<T>> extends BinaryTreeNode<T> {

    AVLTreeNode(
        T value,
        Optional<? extends BinaryTreeNode<T>> leftChild,
        Optional<? extends BinaryTreeNode<T>> rightChild,
        AVLTreeNodeFactory<T> nodeFactory
    ) {
        super(value, leftChild, rightChild, nodeFactory);
    }

    AVLTreeNode(T value, AVLTreeNodeFactory<T> nodeFactory) {
        super(value, nodeFactory);
    }

}
