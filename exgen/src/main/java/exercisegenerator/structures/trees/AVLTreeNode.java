package exercisegenerator.structures.trees;

import java.util.*;

public class AVLTreeNode<T extends Comparable<T>> extends BinaryTreeNode<T> {

    final int height;

    AVLTreeNode(final T value, final AVLTreeNodeFactory<T> nodeFactory) {
        super(value, nodeFactory);
        this.height = 1;
    }

    AVLTreeNode(
        final T value,
        final Optional<? extends BinaryTreeNode<T>> leftChild,
        final Optional<? extends BinaryTreeNode<T>> rightChild,
        final AVLTreeNodeFactory<T> nodeFactory
    ) {
        super(value, leftChild, rightChild, nodeFactory);
        this.height = Math.max(SearchTreeNode.height(leftChild), SearchTreeNode.height(rightChild)) + 1;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    SearchTreeNodeSteps<T> balanceWithSteps() {
        final int diff = this.leftHeight() - this.rightHeight();
        if (diff < -1) {
            return this.balanceRightToLeft();
        } else if (diff > 1) {
            return this.balanceLeftToRight();
        } else {
            return new SearchTreeNodeSteps<T>();
        }
    }

    int leftHeight() {
        return SearchTreeNode.height(this.leftChild);
    }

    int rightHeight() {
        return SearchTreeNode.height(this.rightChild);
    }

    @SuppressWarnings("unchecked")
    private SearchTreeNodeSteps<T> balanceLeftToRight() {
        final AVLTreeNode<T> left = (AVLTreeNode<T>)this.leftChild.get();
        if (
            left.rightChild.isEmpty() ||
            (left.leftChild.isPresent() && left.rightChild.get().getHeight() <= left.leftChild.get().getHeight())
        ) {
            return this.rotateRight();
        }
        final SearchTreeNodeSteps<T> result = this.asLeftChildren(left.rotateLeft());
        result.addAll(((AVLTreeNode<T>)result.getLast().node().get()).rotateRight());
        return result;
    }

    @SuppressWarnings("unchecked")
    private SearchTreeNodeSteps<T> balanceRightToLeft() {
        final AVLTreeNode<T> right = (AVLTreeNode<T>)this.rightChild.get();
        if (
            right.leftChild.isEmpty() ||
            (right.rightChild.isPresent() && right.leftChild.get().getHeight() <= right.rightChild.get().getHeight())
        ) {
            return this.rotateLeft();
        }
        final SearchTreeNodeSteps<T> result = this.asRightChildren(right.rotateRight());
        result.addAll(((AVLTreeNode<T>)result.getLast().node().get()).rotateLeft());
        return result;
    }

}
