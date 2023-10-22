package exercisegenerator.structures.trees.btree;

import java.util.*;
import java.util.stream.*;

class BTreeNode<T extends Comparable<T>> {

    private final List<BTreeNode<T>> children;
    private final int degree;
    private final List<T> values;

    BTreeNode(final int degree, final List<T> values, final List<BTreeNode<T>> children) {
        this.degree = degree;
        this.values = values;
        this.children = children;
    }

    BTreeNode(final int degree, final T value) {
        this(degree, List.of(value), List.of());
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof BTreeNode) {
            @SuppressWarnings("rawtypes")
            final BTreeNode other = (BTreeNode)o;
            return
                this.degree == other.degree && this.values.equals(other.values) && this.children.equals(other.children);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.degree * this.values.hashCode() + 2 * this.degree * this.children.hashCode();
    }

    @Override
    public String toString() {
        if (this.isLeaf()) {
            return this.valuesToString();
        }
        return String.format(
            "[.%s %s ]",
            this.valuesToString(),
            this.children.stream().map(BTreeNode::toString).collect(Collectors.joining(" "))
        );
    }

    BTreeNodeSteps<T> add(final T value) {
        if (this.isLeaf()) {
            return new BTreeNodeSteps<T>(
                new BTreeNode<T>(this.degree, BTree.insertSorted(this.values, value), this.children),
                new BTreeStep<T>(BTreeStepType.ADD, value)
            );
        }
        final int childIndex = this.getChildIndexForValue(value);
        final BTreeNode<T> child = this.children.get(childIndex);
        if (child.isFull()) {
            final List<BTreeNode<T>> newChildren = new ArrayList<BTreeNode<T>>(this.children);
            final SplitResult<T> split = child.split();
            final List<T> newValues = new ArrayList<T>(this.values);
            newValues.add(childIndex, split.value);
            newChildren.set(childIndex, split.rightChild);
            newChildren.add(childIndex, split.leftChild);
            final BTreeNode<T> splitNode = new BTreeNode<T>(this.degree, newValues, newChildren);
            final BTreeNodeSteps<T> result = new BTreeNodeSteps<T>();
            result.add(new BTreeNodeAndStep<T>(splitNode, new BTreeStep<T>(BTreeStepType.SPLIT, split.value)));
            if (value.compareTo(split.value) > 0) {
                result.addAll(splitNode.asChild(childIndex + 1, split.rightChild.add(value)));
            } else {
                result.addAll(splitNode.asChild(childIndex, split.leftChild.add(value)));
            }
            return result;
        }
        return this.asChild(childIndex, child.add(value));
    }

    boolean isFull() {
        return this.getFillingDegree() >= this.degree * 2 - 1;
    }

    boolean isLeaf() {
        return this.children.isEmpty();
    }

    BTreeNodeSteps<T> remove(final T value) {
        final int childIndex = this.getChildIndexForValue(value);
        if (childIndex < this.values.size() && this.values.get(childIndex).compareTo(value) == 0) {
            return this.removeInCurrentNode(value, childIndex);
        }
        if (this.isLeaf()) {
            return new BTreeNodeSteps<T>(this, new BTreeStep<T>(BTreeStepType.REMOVE, value));
        }
        final BTreeNode<T> child = this.children.get(childIndex);
        final List<BTreeNode<T>> newChildren = new ArrayList<BTreeNode<T>>(this.children);
        if (child.isSparse()) {
            final List<T> newValues = new ArrayList<T>(this.values);
            if (childIndex > 0 && !this.children.get(childIndex - 1).isSparse()) {
                final BTreeNode<T> rightNode = this.rotateRightAndReturnRight(newValues, newChildren, childIndex - 1);
                newChildren.set(childIndex, rightNode);
                final BTreeNode<T> newNode = new BTreeNode<T>(this.degree, newValues, newChildren);
                final BTreeNodeSteps<T> result = new BTreeNodeSteps<T>();
                result.add(
                    new BTreeNodeAndStep<T>(
                        newNode,
                        new BTreeStep<T>(BTreeStepType.ROTATE_RIGHT, this.values.get(childIndex - 1))
                    )
                );
                result.addAll(newNode.asChild(childIndex, rightNode.remove(value)));
                return result;
            }
            if (childIndex < this.getFillingDegree() && !this.children.get(childIndex + 1).isSparse()) {
                final BTreeNode<T> leftNode = this.rotateLeftAndReturnLeft(newValues, newChildren, childIndex);
                newChildren.set(childIndex, leftNode);
                final BTreeNode<T> newNode = new BTreeNode<T>(this.degree, newValues, newChildren);
                final BTreeNodeSteps<T> result = new BTreeNodeSteps<T>();
                result.add(
                    new BTreeNodeAndStep<T>(
                        newNode,
                        new BTreeStep<T>(BTreeStepType.ROTATE_LEFT, this.values.get(childIndex))
                    )
                );
                result.addAll(newNode.asChild(childIndex, leftNode.remove(value)));
                return result;
            }
            if (childIndex < this.getFillingDegree()) {
                final BTreeNode<T> merged =
                    child.merge(
                        newValues.remove(childIndex),
                        this.children.get(childIndex + 1)
                    );
                newChildren.remove(childIndex);
                newChildren.set(childIndex, merged);
                final BTreeNode<T> nodeAfterMerge =
                    newValues.isEmpty() ? newChildren.get(0) : new BTreeNode<T>(this.degree, newValues, newChildren);
                final BTreeNodeSteps<T> result = new BTreeNodeSteps<T>();
                result.add(
                    new BTreeNodeAndStep<T>(
                        nodeAfterMerge,
                        new BTreeStep<T>(BTreeStepType.MERGE, this.values.get(childIndex))
                    )
                );
                result.addAll(nodeAfterMerge.remove(value));
                return result;
            }
            final BTreeNode<T> merged =
                this.children.get(childIndex - 1).merge(newValues.remove(childIndex - 1), child);
            newChildren.remove(childIndex - 1);
            newChildren.set(childIndex - 1, merged);
            final BTreeNode<T> nodeAfterMerge =
                newValues.isEmpty() ? newChildren.get(0) : new BTreeNode<T>(this.degree, newValues, newChildren);
            final BTreeNodeSteps<T> result = new BTreeNodeSteps<T>();
            result.add(
                new BTreeNodeAndStep<T>(
                    nodeAfterMerge,
                    new BTreeStep<T>(BTreeStepType.MERGE, this.values.get(childIndex - 1))
                )
            );
            result.addAll(nodeAfterMerge.remove(value));
            return result;
        }
        return this.asChild(childIndex, child.remove(value));
    }

    SplitResult<T> split() {
        final int middle = this.degree - 1;
        final List<T> valuesLeft = new ArrayList<T>();
        final List<BTreeNode<T>> childrenLeft = this.isLeaf() ? List.of() : new ArrayList<BTreeNode<T>>();
        final List<T> valuesRight = new ArrayList<T>();
        final List<BTreeNode<T>> childrenRight = this.isLeaf() ? List.of() : new ArrayList<BTreeNode<T>>();
        for (int i = 0; i < middle; i++) {
            valuesLeft.add(this.values.get(i));
            if (!this.isLeaf()) {
                childrenLeft.add(this.children.get(i));
            }
        }
        if (!this.isLeaf()) {
            childrenLeft.add(this.children.get(middle));
        }
        for (int i = middle + 1; i < this.values.size(); i++) {
            valuesRight.add(this.values.get(i));
            if (!this.isLeaf()) {
                childrenRight.add(this.children.get(i));
            }
        }
        if (!this.isLeaf()) {
            childrenRight.add(this.children.get(this.values.size()));
        }
        return new SplitResult<T>(
            this.values.get(middle),
            new BTreeNode<T>(this.degree, valuesLeft, childrenLeft),
            new BTreeNode<T>(this.degree, valuesRight, childrenRight)
        );
    }

    private BTreeNodeSteps<T> asChild(final int childIndex, final BTreeNodeSteps<T> steps) {
        return steps
            .stream()
            .map(pair -> {
                final List<BTreeNode<T>> newChildren = new ArrayList<BTreeNode<T>>(this.children);
                newChildren.set(childIndex, pair.x.get());
                return new BTreeNodeAndStep<T>(new BTreeNode<T>(this.degree, this.values, newChildren), pair.y);
            }).collect(Collectors.toCollection(BTreeNodeSteps::new));
    }

    private int getChildIndexForValue(final T value) {
        for (int i = 0; i < this.values.size(); i++) {
            if (this.values.get(i).compareTo(value) >= 0) {
                return i;
            }
        }
        return this.values.size();
    }

    private int getFillingDegree() {
        return this.values.size();
    }

    private boolean isSparse() {
        return this.getFillingDegree() < this.degree;
    }

    private T max() {
        if (this.isLeaf()) {
            return this.values.get(this.getFillingDegree() - 1);
        }
        return this.children.get(this.getFillingDegree()).max();
    }

    private BTreeNode<T> merge(final T value, final BTreeNode<T> right) {
        final List<T> values = new ArrayList<T>(this.values);
        final List<BTreeNode<T>> children = new ArrayList<BTreeNode<T>>(this.children);
        values.add(value);
        values.addAll(right.values);
        children.addAll(right.children);
        return new BTreeNode<T>(this.degree, values, children);
    }

    private T min() {
        if (this.isLeaf()) {
            return this.values.get(0);
        }
        return this.children.get(0).min();
    }

    private BTreeNodeSteps<T> removeInCurrentNode(final T value, final int childIndex) {
        final List<T> newValues = new ArrayList<T>(this.values);
        if (this.isLeaf()) {
            newValues.remove(childIndex);
            return new BTreeNodeSteps<T>(
                newValues.isEmpty() ?
                    Optional.empty() :
                        Optional.of(new BTreeNode<T>(this.degree, newValues, this.children)),
                new BTreeStep<T>(BTreeStepType.REMOVE, value)
            );
        }
        final BTreeNode<T> leftChild = this.children.get(childIndex);
        final BTreeNode<T> rightChild = this.children.get(childIndex + 1);
        if (leftChild.isSparse()) {
            if (rightChild.isSparse()) {
                final BTreeNode<T> merged = leftChild.merge(value, rightChild);
                newValues.remove(childIndex);
                final List<BTreeNode<T>> newChildren = new ArrayList<BTreeNode<T>>(this.children);
                newChildren.remove(childIndex);
                newChildren.set(childIndex, merged);
                final BTreeNode<T> nodeAfterMerge =
                    newValues.isEmpty() ? newChildren.get(0) : new BTreeNode<T>(this.degree, newValues, newChildren);
                final BTreeNodeSteps<T> result = new BTreeNodeSteps<T>();
                result.add(new BTreeNodeAndStep<T>(nodeAfterMerge, new BTreeStep<T>(BTreeStepType.MERGE, value)));
                result.addAll(nodeAfterMerge.remove(value));
                return result;
            }
            return this.steal(true, childIndex, rightChild);
        }
        return this.steal(false, childIndex, leftChild);
    }

    private BTreeNode<T> rotateLeftAndReturnLeft(
        final List<T> values,
        final List<BTreeNode<T>> children,
        final int index
    ) {
        final BTreeNode<T> leftChild = children.get(index);
        final List<T> leftValues = new ArrayList<T>(leftChild.values);
        final BTreeNode<T> rightChild = children.get(index + 1);
        final List<T> rightValues = new ArrayList<T>(rightChild.values);
        final T rightValue = rightValues.remove(0);
        leftValues.add(values.set(index, rightValue));
        if (leftChild.isLeaf()) {
            children.set(index, new BTreeNode<T>(this.degree, leftValues, leftChild.children));
            children.set(index + 1, new BTreeNode<T>(this.degree, rightValues, rightChild.children));
            return children.get(index);
        }
        final List<BTreeNode<T>> leftChildren = new ArrayList<BTreeNode<T>>(leftChild.children);
        final List<BTreeNode<T>> rightChildren = new ArrayList<BTreeNode<T>>(rightChild.children);
        final BTreeNode<T> rightGrandChild = rightChildren.remove(0);
        leftChildren.add(rightGrandChild);
        children.set(index, new BTreeNode<T>(this.degree, leftValues, leftChildren));
        children.set(index + 1, new BTreeNode<T>(this.degree, rightValues, rightChildren));
        return children.get(index);
    }

    private BTreeNode<T> rotateRightAndReturnRight(
        final List<T> values,
        final List<BTreeNode<T>> children,
        final int index
    ) {
        final BTreeNode<T> leftChild = children.get(index);
        final List<T> leftValues = new ArrayList<T>(leftChild.values);
        final T leftValue = leftValues.remove(leftChild.getFillingDegree() - 1);
        final BTreeNode<T> rightChild = children.get(index + 1);
        final List<T> rightValues = new ArrayList<T>(rightChild.values);
        rightValues.add(0, values.set(index, leftValue));
        if (leftChild.isLeaf()) {
            children.set(index, new BTreeNode<T>(this.degree, leftValues, leftChild.children));
            children.set(index + 1, new BTreeNode<T>(this.degree, rightValues, rightChild.children));
            return children.get(index + 1);
        }
        final List<BTreeNode<T>> leftChildren = new ArrayList<BTreeNode<T>>(leftChild.children);
        final BTreeNode<T> leftGrandChild = leftChildren.remove(leftChild.getFillingDegree());
        final List<BTreeNode<T>> rightChildren = new ArrayList<BTreeNode<T>>(rightChild.children);
        rightChildren.add(0, leftGrandChild);
        children.set(index, new BTreeNode<T>(this.degree, leftValues, leftChildren));
        children.set(index + 1, new BTreeNode<T>(this.degree, rightValues, rightChildren));
        return children.get(index + 1);
    }

    private BTreeNodeSteps<T> steal(final boolean right, final int childIndex, final BTreeNode<T> child) {
        final T toSteal = right ? child.min() : child.max();
        final BTreeStepType type = right ? BTreeStepType.STEAL_RIGHT : BTreeStepType.STEAL_LEFT;
        final List<T> newValues = new ArrayList<T>(this.values);
        newValues.set(childIndex, toSteal);
        final BTreeNode<T> stolen = new BTreeNode<T>(this.degree, newValues, this.children);
        final BTreeNodeSteps<T> result = new BTreeNodeSteps<T>();
        result.add(new BTreeNodeAndStep<T>(stolen, new BTreeStep<T>(type, this.values.get(childIndex))));
        result.addAll(stolen.asChild(right ? childIndex + 1 : childIndex, child.remove(toSteal)));
        return result;
    }

    private String valuesToString() {
        return String.format("{%s}", this.values.stream().map(v -> v.toString()).collect(Collectors.joining(",")));
    }

}
