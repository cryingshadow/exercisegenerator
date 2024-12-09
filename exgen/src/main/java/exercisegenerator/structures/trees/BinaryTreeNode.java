package exercisegenerator.structures.trees;

import java.util.*;
import java.util.stream.*;

public class BinaryTreeNode<T extends Comparable<T>> implements SearchTreeNode<T> {

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> void addBalanceSteps(final SearchTreeNodeSteps<T> steps) {
        final Optional<? extends SearchTreeNode<T>> lastNode = steps.getLast().node();
        steps.addAll(
            lastNode.isEmpty() ? Collections.emptyList() : ((BinaryTreeNode<T>)lastNode.get()).balanceWithSteps()
        );
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

    @Override
    public SearchTreeNodeSteps<T> addWithSteps(final T value) {
        if (this.value.compareTo(value) < 0) {
            if (this.rightChild.isEmpty()) {
                final BinaryTreeNode<T> resultingNode = this.setRightChild(this.nodeFactory.create(value));
                return new SearchTreeNodeSteps<T>(resultingNode, new SearchTreeStep<T>(SearchTreeStepType.ADD, value));
            }
            final SearchTreeNodeSteps<T> result = this.asRightChildren(this.rightChild.get().addWithSteps(value));
            BinaryTreeNode.addBalanceSteps(result);
            return result;
        }
        if (this.leftChild.isEmpty()) {
            final BinaryTreeNode<T> resultingNode = this.setLeftChild(this.nodeFactory.create(value));
            return new SearchTreeNodeSteps<T>(resultingNode, new SearchTreeStep<T>(SearchTreeStepType.ADD, value));
        }
        final SearchTreeNodeSteps<T> result = this.asLeftChildren(this.leftChild.get().addWithSteps(value));
        BinaryTreeNode.addBalanceSteps(result);
        return result;
    }

    @Override
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

    @Override
    public int getHeight() {
        return Math.max(SearchTreeNode.height(this.leftChild), SearchTreeNode.height(this.rightChild)) + 1;
    }

    @Override
    public int hashCode() {
        return this.value.hashCode() * 5
            + this.leftChild.hashCode() * 3
            + this.rightChild.hashCode() * 2;
    }

    public BinaryTreeNodeFactory<T> nodeFactory() {
        return this.nodeFactory;
    }

    @Override
    public SearchTreeNodeSteps<T> removeWithSteps(final T value) {
        final SearchTreeNodeSteps<T> result = new SearchTreeNodeSteps<T>();
        final int comparison = this.value.compareTo(value);
        if (comparison == 0) {
            if (this.leftChild.isEmpty()) {
                if (this.rightChild.isEmpty()) {
                    return
                        new SearchTreeNodeSteps<T>(
                            this.removeToEmpty(),
                            new SearchTreeStep<T>(SearchTreeStepType.REMOVE, value)
                        );
                }
                result.add(
                    new SearchTreeNodeAndStep<T>(
                        Optional.of(this.removeByReplace(this.rightChild.get())),
                        new SearchTreeStep<T>(SearchTreeStepType.REMOVE, value)
                    )
                );
                BinaryTreeNode.addBalanceSteps(result);
                return result;
            }
            if (this.rightChild.isEmpty()) {
                result.add(
                    new SearchTreeNodeAndStep<T>(
                        Optional.of(this.removeByReplace(this.leftChild.get())),
                        new SearchTreeStep<T>(SearchTreeStepType.REMOVE, value)
                    )
                );
                BinaryTreeNode.addBalanceSteps(result);
                return result;
            }
            final T min = this.rightChild.get().getMin();
            final SearchTreeNodeSteps<T> minSteps = this.rightChild.get().removeWithSteps(min);
            @SuppressWarnings("unchecked")
            final Optional<? extends BinaryTreeNode<T>> minRightChild =
                (Optional<? extends BinaryTreeNode<T>>)minSteps.getLast().node();
            result.addAll(this.asRightChildren(minSteps));
            result.add(
                new SearchTreeNodeAndStep<T>(
                    Optional.of(this.replace(min, this.leftChild, minRightChild)),
                    new SearchTreeStep<T>(SearchTreeStepType.REPLACE, value)
                )
            );
            BinaryTreeNode.addBalanceSteps(result);
            return result;
        }
        if (comparison < 0) {
            if (this.rightChild.isEmpty()) {
                return new SearchTreeNodeSteps<T>();
            }
            result.addAll(this.asRightChildren(this.rightChild.get().removeWithSteps(value)));
            BinaryTreeNode.addBalanceSteps(result);
            return result;
        }
        if (this.leftChild.isEmpty()) {
            return new SearchTreeNodeSteps<T>();
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

    @Override
    public int size() {
        return
            (this.leftChild.isEmpty() ? 0 : this.leftChild.get().size())
            + (this.rightChild.isEmpty() ? 0 : this.rightChild.get().size())
            + 1;
    }

    @Override
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
            this.valueToString(),
            this.rightChild.isEmpty() ? "" : this.rightChild.get().toString()
        );
    }

    @Override
    public String toTikZ() {
        final StringBuilder result = new StringBuilder();
        if (this.leftChild.isEmpty() && this.rightChild.isEmpty()) {
            result.append(" ");
            result.append(this.valueToTikZ());
        } else {
            result.append(" [.");
            result.append(this.valueToTikZ());
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

    SearchTreeNodeSteps<T> asLeftChildren(final SearchTreeNodeSteps<T> steps) {
        @SuppressWarnings("unchecked")
        final SearchTreeNodeSteps<T> result =
            steps
            .stream()
            .map(nodeAndStep ->
                new SearchTreeNodeAndStep<T>(
                    Optional.of(
                        this.setLeftChild((Optional<? extends BinaryTreeNode<T>>)nodeAndStep.node())
                    ),
                    nodeAndStep.step()
                )
            ).collect(Collectors.toCollection(SearchTreeNodeSteps::new));
        return result;
    }

    SearchTreeNodeSteps<T> asRightChildren(final SearchTreeNodeSteps<T> steps) {
        @SuppressWarnings("unchecked")
        final SearchTreeNodeSteps<T> result =
            steps
            .stream()
            .map(nodeAndStep ->
                new SearchTreeNodeAndStep<T>(
                    Optional.of(
                        this.setRightChild((Optional<? extends BinaryTreeNode<T>>)nodeAndStep.node())
                    ),
                    nodeAndStep.step()
                )
            ).collect(Collectors.toCollection(SearchTreeNodeSteps::new));
        return result;
    }

    SearchTreeNodeSteps<T> balanceWithSteps() {
        return new SearchTreeNodeSteps<T>();
    }

    BinaryTreeNode<T> removeByReplace(final BinaryTreeNode<T> replace) {
        return replace;
    }

    Optional<BinaryTreeNode<T>> removeToEmpty() {
        return Optional.empty();
    }

    BinaryTreeNode<T> replace(
        final T value,
        final Optional<? extends BinaryTreeNode<T>> leftChild,
        final Optional<? extends BinaryTreeNode<T>> rightChild
    ) {
        return this.nodeFactory.create(value, leftChild, rightChild);
    }

    SearchTreeNodeSteps<T> rotateLeft() {
        return new SearchTreeNodeSteps<T>(
            this.rotationLeft(),
            new SearchTreeStep<T>(SearchTreeStepType.ROTATE_LEFT, this.value)
        );
    }

    SearchTreeNodeSteps<T> rotateRight() {
        return new SearchTreeNodeSteps<T>(
            this.rotationRight(),
            new SearchTreeStep<T>(SearchTreeStepType.ROTATE_RIGHT, this.value)
        );
    }

    BinaryTreeNode<T> rotationLeft() {
        return this.rightChild.get().setLeftChild(this.setRightChild(this.rightChild.get().leftChild));
    }

    BinaryTreeNode<T> rotationRight() {
        return this.leftChild.get().setRightChild(this.setLeftChild(this.leftChild.get().rightChild));
    }

    String valueToString() {
        return this.value.toString();
    }

    String valueToTikZ() {
        return this.value.toString();
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
