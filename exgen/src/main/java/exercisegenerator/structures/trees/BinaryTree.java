package exercisegenerator.structures.trees;

import java.util.*;
import java.util.stream.*;

public class BinaryTree<T extends Comparable<T>> implements Iterable<T> {

    final Optional<? extends BinaryTreeNode<T>> root;
    
    final BinaryTreeFactory<T> treeFactory;

    BinaryTree(final Optional<? extends BinaryTreeNode<T>> root, final BinaryTreeFactory<T> treeFactory) {
        this.root = root;
        this.treeFactory = treeFactory;
    }

    public BinaryTree<T> add(final T value) {
        final BinaryTreeSteps<T> steps = this.addWithSteps(value);
        if (steps.isEmpty()) {
            return this;
        }
        return steps.get(steps.size() - 1).x;
    }

    public BinaryTreeSteps<T> addWithSteps(final T value) {
        if (this.isEmpty()) {
            return new BinaryTreeSteps<T>(
                this.treeFactory.create(value),
                new BinaryTreeStep<T>(BinaryTreeStepType.ADD, value)
            );
        }
        return this.root
            .get()
            .addWithSteps(value)
            .stream()
            .map(pair -> new BinaryTreeAndStep<T>(this.treeFactory.create(pair.x), pair.y))
            .collect(Collectors.toCollection(BinaryTreeSteps::new));
    }

    public boolean contains(final T value) {
        return this.containsAll(Collections.singleton(value));
    }

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

    public int getHeight() {
        return this.root.isEmpty() ? 0 : this.root.get().getHeight();
    }

    public Optional<T> getMax() {
        return this.stream().reduce((x,y) -> y);
    }

    public Optional<T> getMin() {
        return this.stream().findFirst();
    }

    public String getName() {
        return "Bin\\\"ar-Suchbaum";
    }

    public String getOperations() {
        return "\\emphasize{Einf\\\"uge-}, \\emphasize{L\\\"osch-} und \\emphasize{Ersetzungs-}Operation";
    }

    public List<T> getValues() {
        return this.stream().toList();
    }

    @Override
    public int hashCode() {
        return 2 * this.root.hashCode();
    }

    public boolean isEmpty() {
        return this.root.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return this.stream().iterator();
    }

    public BinaryTree<T> remove(final T value) {
        final BinaryTreeSteps<T> steps = this.removeWithSteps(value);
        return steps.get(steps.size() - 1).x;
    }

    public BinaryTreeSteps<T> removeWithSteps(final T value) {
        if (this.isEmpty()) {
            return new BinaryTreeSteps<T>();
        }
        return this.root
            .get()
            .removeWithStepsAndEmptyParent(value)
            .stream()
            .map(pair -> new BinaryTreeAndStep<T>(this.treeFactory.create(pair.x), pair.y))
            .collect(Collectors.toCollection(BinaryTreeSteps::new));
    }

    public int size() {
        if (this.isEmpty()) {
            return 0;
        }
        return this.root.get().size();
    }

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

    public String toTikZ() {
        if (this.root.isEmpty()) {
            return "\\Tree [.\\phantom{0} ];";
        } else if (this.getHeight() == 1) {
            return String.format("\\Tree [.%s ];", this.root.get().value);
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

}
