package exercisegenerator.structures.trees.btree;

import java.util.*;
import java.util.stream.*;

public class BTree<T extends Comparable<T>> {

    static <T> List<T> insertSorted(final List<T> values, final T value) {
        return Stream.concat(values.stream(), Stream.of(value)).sorted().toList();
    }

    public final int degree;

    private final Optional<BTreeNode<T>> root;

    public BTree(final int degree) {
        this(degree, Optional.empty());
    }

    public BTree(final int degree, final Optional<BTreeNode<T>> root) {
        if (degree < 1) {
            throw new IllegalArgumentException("Degree must be greater than 1!");
        }
        this.degree = degree;
        this.root = root;
    }

    public BTree<T> add(final T value) {
        return this.addWithSteps(value).getLast().x;
    }

    public BTree<T> addAll(final Collection<T> values) {
        BTree<T> result = this;
        for (final T value : values) {
            result = result.add(value);
        }
        return result;
    }

    public BTreeSteps<T> addWithSteps(final T value) {
        if (this.isEmpty()) {
            return new BTreeSteps<T>(
                new BTree<T>(this.degree, Optional.of(new BTreeNode<T>(this.degree, value))),
                new BTreeStep<T>(BTreeStepType.ADD, value)
            );
        }
        final BTreeNode<T> rootNode = this.root.get();
        if (rootNode.isFull()) {
            final BTreeSteps<T> result = new BTreeSteps<T>();
            final SplitResult<T> split = rootNode.split();
            final BTree<T> newTree =
                new BTree<T>(
                    this.degree,
                    Optional.of(
                        new BTreeNode<T>(
                            this.degree,
                            List.of(split.value),
                            List.of(split.leftChild, split.rightChild)
                        )
                    )
                );
            result.add(new BTreeAndStep<T>(newTree, new BTreeStep<T>(BTreeStepType.SPLIT, split.value)));
            result.addAll(newTree.addWithSteps(value));
            return result;
        }
        return rootNode
            .add(value)
            .stream()
            .map(pair -> new BTreeAndStep<T>(new BTree<T>(this.degree, pair.x), pair.y))
            .collect(Collectors.toCollection(BTreeSteps::new));
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof BTree) {
            @SuppressWarnings("rawtypes")
            final
            BTree other = (BTree)o;
            return this.degree == other.degree && this.root.equals(other.root);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.degree * this.root.hashCode();
    }

    public boolean hasJustRoot() {
        return !this.isEmpty() && this.root.get().isLeaf();
    }

    public boolean isEmpty() {
        return this.root.isEmpty();
    }

    public BTree<T> remove(final T value) {
        return this.removeWithSteps(value).getLast().x;
    }

    public BTreeSteps<T> removeWithSteps(final T value) {
        if (this.isEmpty()) {
            return new BTreeSteps<T>(this, new BTreeStep<T>(BTreeStepType.REMOVE, value));
        }
        return this
            .root
            .get()
            .remove(value)
            .stream()
            .map(pair -> new BTreeAndStep<T>(new BTree<T>(this.degree, pair.x), pair.y))
            .collect(Collectors.toCollection(BTreeSteps::new));
    }

    @Override
    public String toString() {
        if (this.root.isEmpty()) {
            return "leer";
        }
        return this.root.get().toString();
    }

}
