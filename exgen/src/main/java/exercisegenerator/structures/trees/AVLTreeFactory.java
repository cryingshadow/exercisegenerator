package exercisegenerator.structures.trees;

import java.util.*;

public class AVLTreeFactory<T extends Comparable<T>> extends BinaryTreeFactory<T> {

    public AVLTreeFactory(final AVLTreeNodeFactory<T> nodeFactory) {
        super(nodeFactory);
    }

    @Override
    @SuppressWarnings("unchecked")
    public BinaryTree<T> create(final Optional<? extends SearchTreeNode<T>> root) {
        return new AVLTree<T>((Optional<AVLTreeNode<T>>)root, this);
    }

}
