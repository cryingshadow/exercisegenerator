package exercisegenerator.structures.trees;

import java.util.*;
import java.util.stream.*;

public class BinaryTreeNode<T extends Comparable<T>> {

    private static <T extends Comparable<T>> Optional<BinaryTreeNode<T>> copyWithEmptyParent(
        final Optional<BinaryTreeNode<T>> node
    ) {
        return node.isEmpty() ? Optional.empty() : Optional.of(node.get().copyWithEmptyParent());
    }

    Optional<BinaryTreeNode<T>> leftChild;

    Optional<BinaryTreeNode<T>> parent;

    Optional<BinaryTreeNode<T>> rightChild;

    T value;

    public BinaryTreeNode(final T value) {
        this(value, Optional.empty(), Optional.empty(), Optional.empty());
    }

    public BinaryTreeNode(
        final T value,
        final BinaryTreeNode<T> parent,
        final BinaryTreeNode<T> leftChild,
        final BinaryTreeNode<T> rightChild
    ) {
        this(value, Optional.of(parent), Optional.of(leftChild), Optional.of(rightChild));
    }

    public BinaryTreeNode(
        final T value,
        final Optional<BinaryTreeNode<T>> parent,
        final Optional<BinaryTreeNode<T>> leftChild,
        final Optional<BinaryTreeNode<T>> rightChild
    ) {
        this.value = value;
        this.parent = parent;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        if (leftChild.isPresent()) {
            this.leftChild.get().setParent(this);
        }
        if (rightChild.isPresent()) {
            this.rightChild.get().setParent(this);
        }
    }

    public BinaryTreeNodeSteps<T> addWithStepsAndEmptyParent(final T value) {
        if (this.value.compareTo(value) < 0) {
            if (this.rightChild.isEmpty()) {
                final BinaryTreeNode<T> resultingNode = this.setRightChildAndEmptyParent(new BinaryTreeNode<T>(value));
                return new BinaryTreeNodeSteps<T>(resultingNode, BinaryTreeStep.ADD);
            }
            return this.asRightChildren(this.rightChild.get().addWithStepsAndEmptyParent(value));
        }
        if (this.leftChild.isEmpty()) {
            final BinaryTreeNode<T> resultingNode = this.setLeftChildAndEmptyParent(new BinaryTreeNode<T>(value));
            return new BinaryTreeNodeSteps<T>(resultingNode, BinaryTreeStep.ADD);
        }
        return this.asLeftChildren(this.leftChild.get().addWithStepsAndEmptyParent(value));
    }

    public boolean containsAll(final Collection<? extends T> values) {
        if (values.isEmpty()) {
            return true;
        }
        final List<? extends T> cWithoutValue = values.stream().filter(x -> x.compareTo(this.value) != 0).toList();
        final LinkedList<? extends T> left = this.getLeft(cWithoutValue);
        final LinkedList<? extends T> right = this.getRight(cWithoutValue);
        return (left.isEmpty() || this.leftChild.isPresent() && this.leftChild.get().containsAll(left))
            && (right.isEmpty() || this.rightChild.isPresent() && this.rightChild.get().containsAll(right));
    }

    public BinaryTreeNode<T> copyWithEmptyParent() {
        return
            new BinaryTreeNode<T>(
                this.value,
                Optional.empty(),
                this.leftChild.isEmpty() ? Optional.empty() : Optional.of(this.leftChild.get().copyWithEmptyParent()),
                this.rightChild.isEmpty() ? Optional.empty() : Optional.of(this.rightChild.get().copyWithEmptyParent())
            );
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof BinaryTreeNode)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        final
        BinaryTreeNode<T> other = (BinaryTreeNode<T>)o;
        return this.value.equals(other.value)
            && this.leftChild.equals(other.leftChild)
            && this.rightChild.equals(other.rightChild);
    }

    public int getHeight() {
        return 1 + Math.max(
            this.leftChild.isEmpty() ? 0 : this.leftChild.get().getHeight(),
            this.rightChild.isEmpty() ? 0 : this.rightChild.get().getHeight()
        );
    }

    @Override
    public int hashCode() {
        return this.value.hashCode() * 5
            + this.leftChild.hashCode() * 3
            + this.rightChild.hashCode() * 2;
    }

    public BinaryTreeNodeSteps<T> removeWithStepsAndEmptyParent(final T value) {
        final int comparison = this.value.compareTo(value);
        if (comparison == 0) {
            if (this.leftChild.isEmpty()) {
                if (this.rightChild.isEmpty()) {
                    return new BinaryTreeNodeSteps<T>(Optional.empty(), BinaryTreeStep.REMOVE);
                }
                return new BinaryTreeNodeSteps<T>(this.rightChild.get().copyWithEmptyParent(), BinaryTreeStep.REMOVE);
            }
            if (this.rightChild.isEmpty()) {
                return new BinaryTreeNodeSteps<T>(this.leftChild.get().copyWithEmptyParent(), BinaryTreeStep.REMOVE);
            }
            final BinaryTreeNodeSteps<T> result = new BinaryTreeNodeSteps<T>();
            final T min = this.rightChild.get().getMin();
            final BinaryTreeNodeSteps<T> minSteps = this.rightChild.get().removeWithStepsAndEmptyParent(min);
            final Optional<BinaryTreeNode<T>> minRightChild = minSteps.get(minSteps.size() - 1).x;
            result.addAll(this.asRightChildren(minSteps));
            result.add(
                new BinaryTreeNodeAndStep<T>(
                    new BinaryTreeNode<T>(
                        min,
                        Optional.empty(),
                        BinaryTreeNode.copyWithEmptyParent(this.leftChild),
                        minRightChild
                    ),
                    BinaryTreeStep.REPLACE
                )
            );
            return result;
        }
        if (comparison < 0) {
            if (this.rightChild.isEmpty()) {
                return new BinaryTreeNodeSteps<T>();
            }
            return this.asRightChildren(this.rightChild.get().removeWithStepsAndEmptyParent(value));
        }
        if (this.leftChild.isEmpty()) {
            return new BinaryTreeNodeSteps<T>();
        }
        return this.asLeftChildren(this.leftChild.get().removeWithStepsAndEmptyParent(value));
    }

    public BinaryTreeNode<T> setLeftChildAndEmptyParent(final BinaryTreeNode<T> leftChild) {
        return this.setLeftChildAndEmptyParent(Optional.of(leftChild));
    }

    public BinaryTreeNode<T> setLeftChildAndEmptyParent(final Optional<BinaryTreeNode<T>> leftChild) {
        final BinaryTreeNode<T> result = this.copyWithEmptyParent();
        result.leftChild = BinaryTreeNode.copyWithEmptyParent(leftChild);
        if (result.leftChild.isPresent()) {
            result.leftChild.get().parent = Optional.of(result);
        }
        return result;
    }

    public BinaryTreeNode<T> setRightChildAndEmptyParent(final BinaryTreeNode<T> rightChild) {
        return this.setRightChildAndEmptyParent(Optional.of(rightChild));
    }

    public BinaryTreeNode<T> setRightChildAndEmptyParent(final Optional<BinaryTreeNode<T>> rightChild) {
        final BinaryTreeNode<T> result = this.copyWithEmptyParent();
        result.rightChild = BinaryTreeNode.copyWithEmptyParent(rightChild);
        if (result.rightChild.isPresent()) {
            result.rightChild.get().parent = Optional.of(result);
        }
        return result;
    }

    public BinaryTreeNode<T> setValue(final T value) {
        return new BinaryTreeNode<T>(value, this.parent, this.leftChild, this.rightChild);
    }

    public int size() {
        return
            (this.leftChild.isEmpty() ? 0 : this.leftChild.get().size())
            + (this.rightChild.isEmpty() ? 0 : this.rightChild.get().size())
            + 1;
    }

    public Stream<T> stream() {
        final Stream<T> left = this.leftChild.isPresent() ? this.leftChild.get().stream() : Stream.empty();
        final Stream<T> right = this.rightChild.isPresent() ? this.rightChild.get().stream() : Stream.empty();
        return Stream.concat(Stream.concat(left, Stream.of(this.value)), right);
    }

    @Override
    public String toString() {
        return String.format(
            "(%s,%s,%s)",
            this.leftChild.isEmpty() ? "" : this.leftChild.get().toString(),
            this.value.toString(),
            this.rightChild.isEmpty() ? "" : this.rightChild.get().toString()
        );
    }

    public String toTikZ() {
        final StringBuilder result = new StringBuilder();
        if (this.leftChild.isEmpty() && this.rightChild.isEmpty()) {
            result.append(" ");
            result.append(this.value);
        } else {
            result.append(" [.");
            result.append(this.value);
            if (this.leftChild.isPresent()) {
                result.append(this.leftChild.get().toTikZ());
            } else {
                result.append(" \\edge[draw=none];\\node[draw=none]{};");
            }
            if (this.rightChild.isPresent()) {
                result.append(this.rightChild.get().toTikZ());
            } else {
                result.append(" \\edge[draw=none];\\node[draw=none]{};");
            }
            result.append("]");
        }
        return result.toString();
    }

    public void visit(final BinaryTreeVisitor<T> visitor) {
        if (this.leftChild.isEmpty()) {
            visitor.onEmptyLeft();
        } else {
            final BinaryTreeNode<T> left = this.leftChild.get();
            visitor.onLeft(left.value);
            left.visit(visitor);
        }
        visitor.onBackFromLeft(this.value);
        if (this.rightChild.isEmpty()) {
            visitor.onEmptyRight();
        } else {
            final BinaryTreeNode<T> right = this.rightChild.get();
            visitor.onRight(right.value);
            right.visit(visitor);
        }
        visitor.onBackFromRight(this.value);
    }

    private BinaryTreeNodeSteps<T> asLeftChildren(final BinaryTreeNodeSteps<T> steps) {
        return steps.stream()
            .map(pair -> new BinaryTreeNodeAndStep<T>(Optional.of(this.setLeftChildAndEmptyParent(pair.x)), pair.y))
            .collect(Collectors.toCollection(BinaryTreeNodeSteps::new));
    }

    private BinaryTreeNodeSteps<T> asRightChildren(final BinaryTreeNodeSteps<T> steps) {
        return steps.stream()
            .map(
                pair -> new BinaryTreeNodeAndStep<T>(
                    Optional.of(this.setRightChildAndEmptyParent(pair.x)),
                    pair.y
                )
            ).collect(Collectors.toCollection(BinaryTreeNodeSteps::new));
    }

    private LinkedList<? extends T> getLeft(final Collection<? extends T> values) {
        return values.stream()
            .filter(x -> x.compareTo(this.value) <= 0)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    private T getMin() {
        return this.stream().findFirst().get();
    }

    private LinkedList<? extends T> getRight(final Collection<? extends T> values) {
        return values.stream()
            .filter(x -> x.compareTo(this.value) > 0)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    private BinaryTreeNode<T> setParent(final BinaryTreeNode<T> parent) {
        this.parent = Optional.of(parent);
        return this;
    }

}
