package exercisegenerator.structures.trees;

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
        if (this.isEmpty()) {
            return new BTree<T>(this.degree, Optional.of(new BTreeNode<T>(this.degree, value)));
        }
        final BTreeNode<T> rootNode = this.root.get();
        if (rootNode.isFull()) {
            final SplitResult<T> split = rootNode.split();
            return new BTree<T>(
                this.degree,
                Optional.of(
                    new BTreeNode<T>(
                        this.degree,
                        List.of(split.value),
                        List.of(split.leftChild, split.rightChild)
                    ).add(value)
                )
            );
        }
        return new BTree<T>(this.degree, Optional.of(rootNode.add(value)));
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
        if (this.isEmpty()) {
            return this;
        }
        return new BTree<T>(this.degree, this.root.get().remove(value));
    }

    @Override
    public String toString() {
        if (this.root.isEmpty()) {
            return "leer";
        }
        return this.root.get().toString();
    }

}
