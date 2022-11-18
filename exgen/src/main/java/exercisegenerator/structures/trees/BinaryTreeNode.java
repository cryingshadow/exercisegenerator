package exercisegenerator.structures.trees;

import java.util.*;
import java.util.stream.*;

public class BinaryTreeNode<T extends Comparable<T>> {

    static <T extends Comparable<T>> void addBalanceSteps(final BinaryTreeNodeSteps<T> steps) {
        final Optional<BinaryTreeNode<T>> lastNode = steps.getLast().x;
        steps.addAll(lastNode.isEmpty() ? Collections.emptyList() : lastNode.get().balanceWithSteps());
    }

    static <T extends Comparable<T>> int height(final Optional<? extends BinaryTreeNode<T>> node) {
        return node.isEmpty() ? 0 : node.get().getHeight();
    }

    final Optional<? extends BinaryTreeNode<T>> leftChild;

    final BinaryTreeNodeFactory<T> nodeFactory;

    final Optional<? extends BinaryTreeNode<T>> rightChild;

    final T value;

    BinaryTreeNode(
        final T value,
        final BinaryTreeNode<T> leftChild,
        final BinaryTreeNode<T> rightChild,
        final BinaryTreeNodeFactory<T> nodeFactory
    ) {
        this(value, Optional.of(leftChild), Optional.of(rightChild), nodeFactory);
    }

    BinaryTreeNode(final T value, final BinaryTreeNodeFactory<T> nodeFactory) {
        this(value, Optional.empty(), Optional.empty(), nodeFactory);
    }

    BinaryTreeNode(
        final T value,
        final Optional<? extends BinaryTreeNode<T>> leftChild,
        final Optional<? extends BinaryTreeNode<T>> rightChild,
        final BinaryTreeNodeFactory<T> nodeFactory
    ) {
        this.value = value;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.nodeFactory = nodeFactory;
    }

    public BinaryTreeNodeSteps<T> addWithSteps(final T value) {
        if (this.value.compareTo(value) < 0) {
            if (this.rightChild.isEmpty()) {
                final BinaryTreeNode<T> resultingNode = this.setRightChild(this.nodeFactory.create(value));
                return new BinaryTreeNodeSteps<T>(resultingNode, new BinaryTreeStep<T>(BinaryTreeStepType.ADD, value));
            }
            final BinaryTreeNodeSteps<T> result = this.asRightChildren(this.rightChild.get().addWithSteps(value));
            BinaryTreeNode.addBalanceSteps(result);
            return result;
        }
        if (this.leftChild.isEmpty()) {
            final BinaryTreeNode<T> resultingNode = this.setLeftChild(this.nodeFactory.create(value));
            return new BinaryTreeNodeSteps<T>(resultingNode, new BinaryTreeStep<T>(BinaryTreeStepType.ADD, value));
        }
        final BinaryTreeNodeSteps<T> result = this.asLeftChildren(this.leftChild.get().addWithSteps(value));
        BinaryTreeNode.addBalanceSteps(result);
        return result;
    }

    public BinaryTreeNodeSteps<T> balanceWithSteps() {
        return new BinaryTreeNodeSteps<T>();
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

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof BinaryTreeNode)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        final BinaryTreeNode<T> other = (BinaryTreeNode<T>)o;
        return this.value.equals(other.value)
            && this.leftChild.equals(other.leftChild)
            && this.rightChild.equals(other.rightChild);
    }

    public int getHeight() {
        return Math.max(BinaryTreeNode.height(this.leftChild), BinaryTreeNode.height(this.rightChild)) + 1;
    }

    @Override
    public int hashCode() {
        return this.value.hashCode() * 5
            + this.leftChild.hashCode() * 3
            + this.rightChild.hashCode() * 2;
    }

    public BinaryTreeNodeSteps<T> removeWithSteps(final T value) {
        final BinaryTreeNodeSteps<T> result = new BinaryTreeNodeSteps<T>();
        final int comparison = this.value.compareTo(value);
        if (comparison == 0) {
            if (this.leftChild.isEmpty()) {
                if (this.rightChild.isEmpty()) {
                    result.add(
                        new BinaryTreeNodeAndStep<T>(
                            Optional.empty(),
                            new BinaryTreeStep<T>(BinaryTreeStepType.REMOVE, value)
                        )
                    );
                    BinaryTreeNode.addBalanceSteps(result);
                    return result;
                }
                result.add(
                    new BinaryTreeNodeAndStep<T>(
                        this.rightChild.get(),
                        new BinaryTreeStep<T>(BinaryTreeStepType.REMOVE, value)
                    )
                );
                BinaryTreeNode.addBalanceSteps(result);
                return result;
            }
            if (this.rightChild.isEmpty()) {
                result.add(
                    new BinaryTreeNodeAndStep<T>(
                        this.leftChild.get(),
                        new BinaryTreeStep<T>(BinaryTreeStepType.REMOVE, value)
                    )
                );
                BinaryTreeNode.addBalanceSteps(result);
                return result;
            }
            final T min = this.rightChild.get().getMin();
            final BinaryTreeNodeSteps<T> minSteps = this.rightChild.get().removeWithSteps(min);
            final Optional<BinaryTreeNode<T>> minRightChild = minSteps.get(minSteps.size() - 1).x;
            result.addAll(this.asRightChildren(minSteps));
            result.add(
                new BinaryTreeNodeAndStep<T>(
                    this.nodeFactory.create(min, this.leftChild, minRightChild),
                    new BinaryTreeStep<T>(BinaryTreeStepType.REPLACE, value)
                )
            );
            BinaryTreeNode.addBalanceSteps(result);
            return result;
        }
        if (comparison < 0) {
            if (this.rightChild.isEmpty()) {
                return new BinaryTreeNodeSteps<T>();
            }
            result.addAll(this.asRightChildren(this.rightChild.get().removeWithSteps(value)));
            BinaryTreeNode.addBalanceSteps(result);
            return result;
        }
        if (this.leftChild.isEmpty()) {
            return new BinaryTreeNodeSteps<T>();
        }
        result.addAll(this.asLeftChildren(this.leftChild.get().removeWithSteps(value)));
        BinaryTreeNode.addBalanceSteps(result);
        return result;
    }

    public BinaryTreeNode<T> setLeftChild(final BinaryTreeNode<T> leftChild) {
        return this.setLeftChild(Optional.of(leftChild));
    }

    public BinaryTreeNode<T> setLeftChild(final Optional<? extends BinaryTreeNode<T>> leftChild) {
        return this.nodeFactory.create(this.value, leftChild, this.rightChild);
    }

    public BinaryTreeNode<T> setRightChild(final BinaryTreeNode<T> rightChild) {
        return this.setRightChild(Optional.of(rightChild));
    }

    public BinaryTreeNode<T> setRightChild(final Optional<? extends BinaryTreeNode<T>> rightChild) {
        return this.nodeFactory.create(this.value, this.leftChild, rightChild);
    }

    public BinaryTreeNode<T> setValue(final T value) {
        return this.nodeFactory.create(value, this.leftChild, this.rightChild);
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
            result.append(" ]");
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

    BinaryTreeNodeSteps<T> asLeftChildren(final BinaryTreeNodeSteps<T> steps) {
        final BinaryTreeNodeSteps<T> result =
            steps
            .stream()
            .map(pair -> new BinaryTreeNodeAndStep<T>(Optional.of(this.setLeftChild(pair.x)), pair.y))
            .collect(Collectors.toCollection(BinaryTreeNodeSteps::new));
        return result;
    }

    BinaryTreeNodeSteps<T> asRightChildren(final BinaryTreeNodeSteps<T> steps) {
        final BinaryTreeNodeSteps<T> result =
            steps
            .stream()
            .map(pair -> new BinaryTreeNodeAndStep<T>(Optional.of(this.setRightChild(pair.x)), pair.y))
            .collect(Collectors.toCollection(BinaryTreeNodeSteps::new));
        return result;
    }

    T getMin() {
        return this.stream().findFirst().get();
    }

    private LinkedList<? extends T> getLeft(final Collection<? extends T> values) {
        return values.stream()
            .filter(x -> x.compareTo(this.value) <= 0)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    private LinkedList<? extends T> getRight(final Collection<? extends T> values) {
        return values.stream()
            .filter(x -> x.compareTo(this.value) > 0)
            .collect(Collectors.toCollection(LinkedList::new));
    }

}
