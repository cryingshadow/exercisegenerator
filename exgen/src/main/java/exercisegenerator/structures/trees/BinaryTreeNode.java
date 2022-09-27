package exercisegenerator.structures.trees;

import java.util.*;
import java.util.stream.*;

import exercisegenerator.structures.*;

public class BinaryTreeNode<T extends Comparable<T>> {

    private static <T extends Comparable<T>> List<Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>> addAllWithSteps(
        final BinaryTreeNode<T> parent,
        final Optional<BinaryTreeNode<T>> child,
        final LinkedList<? extends T> values
    ) {
        if (values.isEmpty()) {
            return Collections.emptyList();
        }
        if (child.isEmpty()) {
            final List<Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>> result =
                new ArrayList<Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>>();
            final BinaryTreeNode<T> node =
                new BinaryTreeNode<T>(values.poll(), Optional.of(parent), Optional.empty(), Optional.empty());
            result.add(new Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>(Optional.of(node), BinaryTreeStep.ADD));
            result.addAll(node.addAllWithSteps(values));
            return result;
        }
        return child.get().addAllWithSteps(values);
    }

    public final Optional<BinaryTreeNode<T>> leftChild;

    public final Optional<BinaryTreeNode<T>> parent;

    public final Optional<BinaryTreeNode<T>> rightChild;

    public final T value;

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
    }

    public BinaryTreeNode<T> addAll(final Collection<? extends T> values) {
        final List<Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>> steps = this.addAllWithSteps(values);
        return steps.isEmpty() ? this : steps.get(steps.size() - 1).x.get();
    }

    public List<Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>> addAllWithSteps(
        final Collection<? extends T> values
    ) {
        if (values.isEmpty()) {
            return Collections.emptyList();
        }
        final List<Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>> leftSteps =
            BinaryTreeNode.addAllWithSteps(this, this.leftChild, this.getLeft(values));
        final Optional<BinaryTreeNode<T>> lastLeft =
            leftSteps.isEmpty() ? this.leftChild : leftSteps.get(leftSteps.size() - 1).x;
        final List<Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>> rightSteps =
            BinaryTreeNode.addAllWithSteps(this, this.rightChild, this.getRight(values));
        final List<Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>> result =
            new LinkedList<Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>>();
        result.addAll(this.asLeftChildren(leftSteps));
        result.addAll(this.asRightChildren(rightSteps, lastLeft));
        return result;
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
        final
        BinaryTreeNode<T> other = (BinaryTreeNode<T>)o;
        return this.value.equals(other.value)
            && this.leftChild.equals(other.leftChild)
            && this.rightChild.equals(other.rightChild);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode() * 5
            + this.leftChild.hashCode() * 3
            + this.rightChild.hashCode() * 2;
    }

    public Optional<BinaryTreeNode<T>> removeAll(final Collection<? extends T> values) {
        final List<Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>> steps = this.removeAllWithSteps(values);
        return steps.isEmpty() ? Optional.of(this) : steps.get(steps.size() - 1).x;
    }

    public List<Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>> removeAllWithSteps(
        final Collection<? extends T> values
    ) {
        final List<Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>> result =
            new LinkedList<Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>>();
        final List<T> cWithoutValue = new LinkedList<T>(values);
        final boolean removeThis = cWithoutValue.remove(this.value);
        final List<Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>> leftSteps =
            this.leftChild.isEmpty() ?
                Collections.emptyList() :
                    this.leftChild.get().removeAllWithSteps(this.getLeft(cWithoutValue));
        final Optional<BinaryTreeNode<T>> newLeftChild =
            leftSteps.isEmpty() ? this.leftChild : leftSteps.get(leftSteps.size() - 1).x;
        final List<Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>> rightSteps =
            this.rightChild.isEmpty() ?
                Collections.emptyList() :
                    this.rightChild.get().removeAllWithSteps(this.getRight(cWithoutValue));
        final Optional<BinaryTreeNode<T>> newRightChild =
            rightSteps.isEmpty() ? this.rightChild : rightSteps.get(rightSteps.size() - 1).x;
        result.addAll(this.asLeftChildren(leftSteps));
        result.addAll(this.asRightChildren(rightSteps, newLeftChild));
        if (removeThis) {
            if (newLeftChild.isEmpty()) {
                if (newRightChild.isEmpty()) {
                    result.add(
                        new Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>(Optional.empty(), BinaryTreeStep.REMOVE)
                    );
                    return result;
                }
                result.add(this.removeAndSetParent(newRightChild.get()));
                return result;
            }
            if (newRightChild.isEmpty()) {
                result.add(this.removeAndSetParent(newLeftChild.get()));
                return result;
            }
            final T min = newRightChild.get().getMin();
            final List<Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>> minSteps =
                newRightChild.get().removeAllWithSteps(Collections.singleton(min));
            final Optional<BinaryTreeNode<T>> minRightChild = minSteps.get(minSteps.size() - 1).x;
            result.addAll(this.asRightChildren(minSteps, newLeftChild));
            result.add(
                new Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>(
                    Optional.of(new BinaryTreeNode<T>(min, this.parent, newLeftChild, minRightChild)),
                    BinaryTreeStep.REPLACE
                )
            );
        }
        return result;
    }

    public Optional<BinaryTreeNode<T>> retainAll(final Collection<? extends T> values) {
        if (!values.contains(this.value)) {
            if (this.leftChild.isPresent()) {
                final Optional<BinaryTreeNode<T>> newLeftChild = this.leftChild.get().retainAll(this.getLeft(values));
                if (newLeftChild.isEmpty()) {
                    if (this.rightChild.isPresent()) {
                        return this.rightChild.get().retainAll(this.getRight(values));
                    }
                    return Optional.empty();
                }
                if (this.rightChild.isEmpty()) {
                    return newLeftChild;
                }
                final Optional<BinaryTreeNode<T>> newRightChild =
                    this.rightChild.get().retainAll(this.getRight(values));
                if (newRightChild.isEmpty()) {
                    return newLeftChild;
                }
                final T min = newRightChild.get().getMin();
                return Optional.of(
                    new BinaryTreeNode<T>(
                        min,
                        this.parent,
                        newLeftChild,
                        newRightChild.get().removeAll(Collections.singleton(min))
                    )
                );
            }
            if (this.rightChild.isPresent()) {
                return this.rightChild.get().retainAll(this.getRight(values));
            }
            return Optional.empty();
        }
        if (this.leftChild.isEmpty() && this.rightChild.isEmpty()) {
            return Optional.of(this);
        }
        return Optional.of(
            new BinaryTreeNode<T>(
                this.value,
                this.parent,
                this.leftChild.isPresent() ? this.leftChild.get().retainAll(this.getLeft(values)) : Optional.empty(),
                this.rightChild.isPresent() ? this.rightChild.get().retainAll(this.getRight(values)) : Optional.empty()
            )
        );
    }

    public BinaryTreeNode<T> setLeftChild(final Optional<BinaryTreeNode<T>> leftChild) {
        return new BinaryTreeNode<T>(this.value, this.parent, leftChild, this.rightChild);
    }

    public BinaryTreeNode<T> setParent(final Optional<BinaryTreeNode<T>> parent) {
        return new BinaryTreeNode<T>(this.value, parent, this.leftChild, this.rightChild);
    }

    public BinaryTreeNode<T> setRightChild(final Optional<BinaryTreeNode<T>> rightChild) {
        return new BinaryTreeNode<T>(this.value, this.parent, this.leftChild, rightChild);
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

    private List<Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>> asLeftChildren(
        final List<Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>> leftSteps
    ) {
        return leftSteps.stream()
            .map(
                pair -> new Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>(
                    Optional.of(this.setLeftChild(pair.x)),
                    pair.y
                )
            ).toList();
    }

    private List<Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>> asRightChildren(
        final List<Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>> rightSteps,
        final Optional<BinaryTreeNode<T>> newLeftChild
    ) {
        return rightSteps.stream()
            .map(
                pair -> new Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>(
                    Optional.of(this.setRightChild(pair.x).setLeftChild(newLeftChild)),
                    pair.y
                )
            ).toList();
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

    private Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep> removeAndSetParent(final BinaryTreeNode<T> result) {
        return new Pair<Optional<BinaryTreeNode<T>>, BinaryTreeStep>(
            Optional.of(result.setParent(this.parent)),
            BinaryTreeStep.REMOVE
        );
    }

}
