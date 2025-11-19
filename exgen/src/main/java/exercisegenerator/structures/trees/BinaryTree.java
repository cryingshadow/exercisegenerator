package exercisegenerator.structures.trees;

import java.util.*;
import java.util.stream.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.io.*;

public class BinaryTree<T extends Comparable<T>> implements SearchTree<T> {

    final Optional<? extends BinaryTreeNode<T>> root;

    final BinaryTreeFactory<T> treeFactory;

    BinaryTree(final Optional<? extends BinaryTreeNode<T>> root, final BinaryTreeFactory<T> treeFactory) {
        this.root = root;
        this.treeFactory = treeFactory;
    }

    @Override
    public SearchTreeSteps<T> addWithSteps(final T value) {
        if (this.isEmpty()) {
            final BinaryTree<T> tree = this.treeFactory.create(value);
            final SearchTreeSteps<T> result =
                new SearchTreeSteps<T>(tree, new SearchTreeStep<T>(SearchTreeStepType.ADD, value));
            result.addAll(this.toBinaryTreeSteps(tree.root.get().balanceWithSteps()));
            return result;
        }
        return this.toBinaryTreeSteps(this.root.get().addWithSteps(value));
    }

    @Override
    public boolean contains(final T value) {
        return this.containsAll(Collections.singleton(value));
    }

    @Override
    public boolean containsAll(final Collection<? extends T> values) {
        if (this.isEmpty()) {
            return values.isEmpty();
        }
        return this.root.get().containsAll(values);
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof BinaryTree)) {
            return false;
        }
        return this.root.equals(((BinaryTree<?>)o).root);
    }

    @Override
    public int getHeight() {
        return this.root.isEmpty() ? 0 : this.root.get().getHeight();
    }

    @Override
    public BigFraction getHorizontalFillingDegree() {
        final int height = this.getHeight();
        if (height < 5) {
            return BigFraction.ONE_HALF;
        }
        return BigFraction.ONE;
    }

    @Override
    public Optional<T> getMax() {
        return this.stream().reduce((x,y) -> y);
    }

    @Override
    public Optional<T> getMin() {
        return this.stream().findFirst();
    }

    @Override
    public String getName(boolean genitive) {
        return "Bin\\\"ar-Suchbaum" + (genitive ? "s" : "");
    }

    @Override
    public String getOperations() {
        return "\\emphasize{Einf\\\"uge-}, \\emphasize{L\\\"osch-} und \\emphasize{Ersetzungs-}Operation";
    }

    @Override
    public Optional<String> getSamePageWidth() {
        final int height = this.getHeight();
        if (height < 5) {
            return Optional.of("7cm");
        }
        return Optional.empty();
    }

    @Override
    public TikZStyle getTikZStyle() {
        return TikZStyle.TREE;
    }

    @Override
    public List<T> getValues() {
        return this.stream().toList();
    }

    @Override
    public int hashCode() {
        return 2 * this.root.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return this.root.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return this.stream().iterator();
    }

    @Override
    public BinaryTree<T> remove(final T value) {
        final SearchTreeSteps<T> steps = this.removeWithSteps(value);
        return (BinaryTree<T>)steps.get(steps.size() - 1).tree();
    }

    @Override
    public SearchTreeSteps<T> removeWithSteps(final T value) {
        if (this.isEmpty()) {
            return new SearchTreeSteps<T>(this, new SearchTreeStep<T>(SearchTreeStepType.REMOVE, value));
        }
        return this.toBinaryTreeSteps(this.root.get().removeWithSteps(value));
    }

    @Override
    public Optional<? extends SearchTreeNode<T>> root() {
        return this.root;
    }

    @Override
    public int size() {
        if (this.isEmpty()) {
            return 0;
        }
        return this.root.get().size();
    }

    @Override
    public Stream<T> stream() {
        if (this.isEmpty()) {
            return Stream.empty();
        }
        return this.root.get().stream();
    }

    @Override
    public String toString() {
        return this.root.isEmpty() ? "" : this.root.get().toString();
    }

    @Override
    public String toTikZ() {
        if (this.root.isEmpty()) {
            return "\\Tree [.\\phantom{0} ];";
        } else if (this.getHeight() == 1) {
            return String.format("\\Tree [.%s ];", this.root.get().valueToTikZ());
        } else {
            return "\\Tree" + this.root.get().toTikZ();
        }
    }

    public void visit(final BinaryTreeVisitor<T> visitor) {
        if (this.isEmpty()) {
            visitor.onEmptyRoot();
        } else {
            final BinaryTreeNode<T> rootNode = this.root.get();
            visitor.onRoot(rootNode.value);
            rootNode.visit(visitor);
        }
        visitor.onBackFromRoot();
    }

    private SearchTreeSteps<T> toBinaryTreeSteps(final SearchTreeNodeSteps<T> steps) {
        return steps
            .stream()
            .map(nodeAndStep ->
                new SearchTreeAndStep<T>(
                    this.treeFactory.create(nodeAndStep.node()),
                    nodeAndStep.step()
                )
            ).collect(Collectors.toCollection(SearchTreeSteps::new));
    }

}
