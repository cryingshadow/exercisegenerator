package exercisegenerator.structures.trees;

import java.util.*;

public class AVLTreeNode<T extends Comparable<T>> extends BinaryTreeNode<T> {

    final int height;
    
    AVLTreeNode(T value, AVLTreeNodeFactory<T> nodeFactory) {
        super(value, nodeFactory);
        this.height = 1;
    }

    AVLTreeNode(
        T value,
        Optional<? extends BinaryTreeNode<T>> leftChild,
        Optional<? extends BinaryTreeNode<T>> rightChild,
        AVLTreeNodeFactory<T> nodeFactory
    ) {
        super(value, leftChild, rightChild, nodeFactory);
        this.height = Math.max(BinaryTreeNode.height(leftChild), BinaryTreeNode.height(rightChild)) + 1;
    }

    int leftHeight() {
        return AVLTreeNode.height(this.leftChild);
    }
    
    int rightHeight() {
        return AVLTreeNode.height(this.rightChild);
    }
    
    @Override
    public int getHeight() {
        return this.height;
    }
    
    @Override
    public BinaryTreeNodeSteps<T> balanceWithSteps() {
        int diff = this.leftHeight() - this.rightHeight();
        if (diff < -1) {
            return this.balanceRightToLeft();
        } else if (diff > 1) {
            return this.balanceLeftToRight();
        } else {
            return new BinaryTreeNodeSteps<T>();
        }
    }

    private BinaryTreeNodeSteps<T> balanceLeftToRight() {
        @SuppressWarnings("unchecked")
        AVLTreeNode<T> left = (AVLTreeNode<T>)this.leftChild.get();
        if (
            left.rightChild.isEmpty() ||
            (left.leftChild.isPresent() && left.rightChild.get().getHeight() <= left.leftChild.get().getHeight())
        ) {
            return this.rotateRight();
        }
        BinaryTreeNodeSteps<T> result = left.rotateLeft();
        result.addAll(((AVLTreeNode<T>)this.setLeftChild(result.getLast().x)).rotateRight());
        return result;
    }

    private BinaryTreeNodeSteps<T> rotateLeft() {
        return new BinaryTreeNodeSteps<T>(
            this.rightChild.get().setLeftChild(this.setRightChild(this.rightChild.get().leftChild)),
            new BinaryTreeStep<T>(BinaryTreeStepType.ROTATE_LEFT, this.value)
        );
    }

    private BinaryTreeNodeSteps<T> rotateRight() {
        return new BinaryTreeNodeSteps<T>(
            this.leftChild.get().setRightChild(this.setLeftChild(this.leftChild.get().rightChild)),
            new BinaryTreeStep<T>(BinaryTreeStepType.ROTATE_RIGHT, this.value)
        );
    }

    private BinaryTreeNodeSteps<T> balanceRightToLeft() {
        @SuppressWarnings("unchecked")
        AVLTreeNode<T> right = (AVLTreeNode<T>)this.rightChild.get();
        if (
            right.leftChild.isEmpty() ||
            (right.rightChild.isPresent() && right.leftChild.get().getHeight() <= right.rightChild.get().getHeight())
        ) {
            return this.rotateLeft();
        }
        BinaryTreeNodeSteps<T> result = right.rotateRight();
        result.addAll(((AVLTreeNode<T>)this.setRightChild(result.getLast().x)).rotateLeft());
        return result;
    }
    
}
