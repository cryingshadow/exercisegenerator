package exercisegenerator.structures.trees;

import java.util.*;

public interface OperationsMirror {

    <T extends Comparable<T>> SearchTreeNodeSteps<T> asInnerChildren(
        final BinaryTreeNode<T> node,
        final SearchTreeNodeSteps<T> steps
    );

    <T extends Comparable<T>> SearchTreeNodeSteps<T> asOuterChildren(
        final BinaryTreeNode<T> node,
        final SearchTreeNodeSteps<T> steps
    );

    <T extends Comparable<T>> Optional<? extends BinaryTreeNode<T>> getInnerChild(final BinaryTreeNode<T> node);

    <T extends Comparable<T>> Optional<? extends BinaryTreeNode<T>> getOuterChild(final BinaryTreeNode<T> node);

    <T extends Comparable<T>> SearchTreeNodeSteps<T> innerRotate(final BinaryTreeNode<T> node);

    <T extends Comparable<T>> SearchTreeNodeSteps<T> innerRotate(final BinaryTreeNode<T> node, final String annotation);

    <T extends Comparable<T>> SearchTreeNodeSteps<T> outerRotate(final BinaryTreeNode<T> node);

    <T extends Comparable<T>> SearchTreeNodeSteps<T> outerRotate(final BinaryTreeNode<T> node, final String annotation);

    <T extends Comparable<T>> BinaryTreeNode<T> setInnerChild(
        final BinaryTreeNode<T> node,
        final BinaryTreeNode<T> child
    );

    <T extends Comparable<T>> BinaryTreeNode<T> setInnerChild(
        final BinaryTreeNode<T> node,
        final Optional<? extends BinaryTreeNode<T>> child
    );

    <T extends Comparable<T>> BinaryTreeNode<T> setOuterChild(
        final BinaryTreeNode<T> node,
        final BinaryTreeNode<T> child
    );

    <T extends Comparable<T>> BinaryTreeNode<T> setOuterChild(
        final BinaryTreeNode<T> node,
        final Optional<? extends BinaryTreeNode<T>> child
    );

}
