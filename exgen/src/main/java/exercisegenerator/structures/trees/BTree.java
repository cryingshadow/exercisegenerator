package exercisegenerator.structures.trees;

import java.util.*;
import java.util.stream.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.io.*;

public class BTree<T extends Comparable<T>> implements SearchTree<T> {

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

    @Override
    public BTree<T> add(final T value) {
        return (BTree<T>)this.addWithSteps(value).getLast().tree();
    }

    public BTree<T> addAll(final Collection<T> values) {
        BTree<T> result = this;
        for (final T value : values) {
            result = result.add(value);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SearchTreeSteps<T> addWithSteps(final T value) {
        if (this.isEmpty()) {
            return new SearchTreeSteps<T>(
                new BTree<T>(this.degree, Optional.of(new BTreeNode<T>(this.degree, value))),
                new SearchTreeStep<T>(SearchTreeStepType.ADD, value)
            );
        }
        final BTreeNode<T> rootNode = this.root.get();
        if (rootNode.isFull()) {
            final SearchTreeSteps<T> result = new SearchTreeSteps<T>();
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
            result.add(new SearchTreeAndStep<T>(newTree, new SearchTreeStep<T>(SearchTreeStepType.SPLIT, split.value)));
            result.addAll(newTree.addWithSteps(value));
            return result;
        }
        return rootNode
            .addWithSteps(value)
            .stream()
            .map(nodeAndStep ->
                new SearchTreeAndStep<T>(
                    new BTree<T>(this.degree, (Optional<BTreeNode<T>>)nodeAndStep.node()),
                    nodeAndStep.step()
                )
            ).collect(Collectors.toCollection(SearchTreeSteps::new));
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
    public BigFraction getHorizontalFillingDegree() {
        return BigFraction.ONE;
    }

    @Override
    public String getName() {
        return String.format("B-Baum mit Grad $t = %d$", this.degree);
    }

    @Override
    public String getOperations() {
        return "\\begin{itemize}\\item \\emphasize{Aufteilung}\\item \\emphasize{Diebstahloperation}\\item \\emphasize{Einf\\\"ugeoperation}\\item \\emphasize{L\\\"oschoperation}\\item \\emphasize{Rotation}\\item \\emphasize{Verschmelzung}\\end{itemize}";
    }

    @Override
    public Optional<String> getSamePageWidth() {
        return Optional.empty();
    }

    @Override
    public TikZStyle getTikZStyle() {
        return TikZStyle.BTREE;
    }

    @Override
    public int hashCode() {
        return this.degree * this.root.hashCode();
    }

    public boolean hasJustRoot() {
        return !this.isEmpty() && this.root.get().isLeaf();
    }

    @Override
    public boolean isEmpty() {
        return this.root.isEmpty();
    }

    @Override
    public BTree<T> remove(final T value) {
        return (BTree<T>)this.removeWithSteps(value).getLast().tree();
    }

    @SuppressWarnings("unchecked")
    @Override
    public SearchTreeSteps<T> removeWithSteps(final T value) {
        if (this.isEmpty()) {
            return new SearchTreeSteps<T>(this, new SearchTreeStep<T>(SearchTreeStepType.REMOVE, value));
        }
        return this
            .root
            .get()
            .removeWithSteps(value)
            .stream()
            .map(nodeAndStep ->
                new SearchTreeAndStep<T>(
                    new BTree<T>(this.degree, (Optional<BTreeNode<T>>)nodeAndStep.node()),
                    nodeAndStep.step()
                )
            ).collect(Collectors.toCollection(SearchTreeSteps::new));
    }

    @Override
    public Optional<? extends SearchTreeNode<T>> root() {
        return this.root;
    }

    @Override
    public String toString() {
        if (this.root.isEmpty()) {
            return "leer";
        }
        return this.root.get().toString();
    }

    @Override
    public String toTikZ() {
        if (this.root.isEmpty()) {
            return "\\Tree [.\\phantom{0} ];";
        } else if (this.getHeight() == 1) {
            return String.format("\\Tree [.%s ];", this.root.get().toString());
        }
        return "\\Tree " + this.root.get().toString() + ";";
    }

}
