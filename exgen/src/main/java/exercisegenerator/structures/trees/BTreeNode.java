package exercisegenerator.structures.trees;

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

    BTreeNode<T> add(final T value) {
        if (this.isLeaf()) {
            return new BTreeNode<T>(this.degree, BTree.insertSorted(this.values, value), this.children);
        }
        final int childIndex = this.getChildIndexForValue(value);
        final BTreeNode<T> child = this.children.get(childIndex);
        final List<BTreeNode<T>> newChildren = new ArrayList<BTreeNode<T>>(this.children);
        if (child.isFull()) {
            final SplitResult<T> split = child.split();
            final BTreeNode<T> leftChild;
            final BTreeNode<T> rightChild;
            if (value.compareTo(split.value) > 0) {
                leftChild = split.leftChild;
                rightChild = split.rightChild.add(value);
            } else {
                leftChild = split.leftChild.add(value);
                rightChild = split.rightChild;
            }
            final List<T> newValues = new ArrayList<T>(this.values);
            newValues.add(childIndex, split.value);
            newChildren.set(childIndex, rightChild);
            newChildren.add(childIndex, leftChild);
            return new BTreeNode<T>(this.degree, newValues, newChildren);
        }
        newChildren.set(childIndex, child.add(value));
        return new BTreeNode<T>(this.degree, this.values, newChildren);
    }

    int getChildIndexForValue(final T value) {
        for (int i = 0; i < this.values.size(); i++) {
            if (this.values.get(i).compareTo(value) >= 0) {
                return i;
            }
        }
        return this.values.size();
    }

    int getFillingDegree() {
        return this.values.size();
    }

    boolean isFull() {
        return this.getFillingDegree() >= this.degree * 2 - 1;
    }

    boolean isLeaf() {
        return this.children.isEmpty();
    }

    boolean isSparse() {
        return this.getFillingDegree() < this.degree;
    }

    T max() {
        if (this.isLeaf()) {
            return this.values.get(this.getFillingDegree() - 1);
        }
        return this.children.get(this.getFillingDegree()).max();
    }

    BTreeNode<T> merge(final T value, final BTreeNode<T> right) {
        final List<T> values = new ArrayList<T>(this.values);
        final List<BTreeNode<T>> children = new ArrayList<BTreeNode<T>>(this.children);
        values.add(value);
        values.addAll(right.values);
        children.addAll(right.children);
        return new BTreeNode<T>(this.degree, values, children);
    }

    T min() {
        if (this.isLeaf()) {
            return this.values.get(0);
        }
        return this.children.get(0).min();
    }

    Optional<BTreeNode<T>> remove(final T value) {
        final int childIndex = this.getChildIndexForValue(value);
        if (childIndex < this.values.size() && this.values.get(childIndex).compareTo(value) == 0) {
            return this.removeInCurrentNode(value, childIndex);
        }
        if (this.isLeaf()) {
            return Optional.of(this);
        }
        final BTreeNode<T> child = this.children.get(childIndex);
        final List<BTreeNode<T>> newChildren = new ArrayList<BTreeNode<T>>(this.children);
        if (child.isSparse()) {
            final List<T> newValues = new ArrayList<T>(this.values);
            if (childIndex > 0 && !this.children.get(childIndex - 1).isSparse()) {
                newChildren.set(
                    childIndex,
                    this.rotateRightAndReturnRight(newValues, newChildren, childIndex - 1).remove(value).get()
                );
                return Optional.of(new BTreeNode<T>(this.degree, newValues, newChildren));
            }
            if (childIndex < this.getFillingDegree() && !this.children.get(childIndex + 1).isSparse()) {
                newChildren.set(
                    childIndex,
                    this.rotateLeftAndReturnLeft(newValues, newChildren, childIndex).remove(value).get()
                );
                return Optional.of(new BTreeNode<T>(this.degree, newValues, newChildren));
            }
            if (childIndex < this.getFillingDegree()) {
                final BTreeNode<T> merge =
                    child.merge(
                        newValues.remove(childIndex),
                        this.children.get(childIndex + 1)
                    ).remove(value).get();
                newChildren.remove(childIndex);
                newChildren.set(childIndex, merge);
                if (newValues.isEmpty()) {
                    return Optional.of(newChildren.get(0));
                }
                return Optional.of(new BTreeNode<T>(this.degree, newValues, newChildren));
            }
            final BTreeNode<T> merge =
                this
                .children
                .get(childIndex - 1)
                .merge(newValues.remove(childIndex - 1), child)
                .remove(value)
                .get();
            newChildren.remove(childIndex - 1);
            newChildren.set(childIndex - 1, merge);
            if (newValues.isEmpty()) {
                return Optional.of(newChildren.get(0));
            }
            return Optional.of(new BTreeNode<T>(this.degree, newValues, newChildren));
        }
        newChildren.set(childIndex, child.remove(value).get());
        return Optional.of(new BTreeNode<T>(this.degree, this.values, newChildren));
    }

    Optional<BTreeNode<T>> removeInCurrentNode(final T value, final int childIndex) {
        final List<T> newValues = new ArrayList<T>(this.values);
        if (this.isLeaf()) {
            if (this.values.size() == 1) {
                return Optional.empty();
            }
            newValues.remove(childIndex);
            return Optional.of(new BTreeNode<T>(this.degree, newValues, this.children));
        }
        final BTreeNode<T> leftChild = this.children.get(childIndex);
        final BTreeNode<T> rightChild = this.children.get(childIndex + 1);
        if (leftChild.isSparse()) {
            if (rightChild.isSparse()) {
                final BTreeNode<T> merge = leftChild.merge(value, rightChild).remove(value).get();
                newValues.remove(childIndex);
                final List<BTreeNode<T>> newChildren = new ArrayList<BTreeNode<T>>(this.children);
                newChildren.remove(childIndex);
                newChildren.set(childIndex, merge);
                if (newValues.isEmpty()) {
                    return Optional.of(newChildren.get(0));
                }
                return Optional.of(new BTreeNode<T>(this.degree, newValues, newChildren));
            }
            return this.steal(true, childIndex, rightChild);
        }
        return this.steal(false, childIndex, leftChild);
    }

    BTreeNode<T> rotateLeftAndReturnLeft(
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

    BTreeNode<T> rotateRightAndReturnRight(
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

    Optional<BTreeNode<T>> steal(final boolean right, final int childIndex, final BTreeNode<T> child) {
        final T toSteal = right ? child.min() : child.max();
        final List<T> newValues = new ArrayList<T>(this.values);
        newValues.set(childIndex, toSteal);
        final ArrayList<BTreeNode<T>> newChildren = new ArrayList<BTreeNode<T>>(this.children);
        newChildren.set(right ? childIndex + 1 : childIndex, child.remove(toSteal).get());
        return Optional.of(new BTreeNode<T>(this.degree, newValues, newChildren));
    }

    String valuesToString() {
        return String.format("{%s}", this.values.stream().map(v -> v.toString()).collect(Collectors.joining(",")));
    }

}
