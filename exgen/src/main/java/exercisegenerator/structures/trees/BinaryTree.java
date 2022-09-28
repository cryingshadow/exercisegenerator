package exercisegenerator.structures.trees;

import java.util.*;
import java.util.stream.*;

import exercisegenerator.structures.*;

public class BinaryTree<T extends Comparable<T>> implements Iterable<T> { //TODO rotations can destroy strict order

    public static <T extends Comparable<T>> BinaryTree<T> create() {
        return new BinaryTree<T>(Optional.empty());
    }

    public static <T extends Comparable<T>> BinaryTree<T> create(final Deque<Pair<T, Boolean>> construction) {
        BinaryTree<T> tree = BinaryTree.create();
        for (final Pair<T, Boolean> operation : construction) {
            if (operation.y) {
                tree = tree.add(operation.x);
            } else {
                tree = tree.remove(operation.x);
            }
        }
        return tree;
    }

    @SafeVarargs
    public static <T extends Comparable<T>> BinaryTree<T> create(final T... values) {

        return BinaryTree.create(
            Arrays.asList(values)
                .stream()
                .map(value -> new Pair<T, Boolean>(value, true))
                .collect(Collectors.toCollection(ArrayDeque::new))
        );
    }

    Optional<BinaryTreeNode<T>> root;

    private BinaryTree(final BinaryTreeNode<T> root) {
        this(Optional.of(root));
    }

    private BinaryTree(final Optional<BinaryTreeNode<T>> root) {
        this.root = root;
    }

    public BinaryTree<T> add(final T value) {
        final BinaryTreeSteps<T> steps = this.addWithSteps(value);
        if (steps.isEmpty()) {
            return this.copy();
        }
        return steps.get(steps.size() - 1).x;
    }

    public BinaryTreeSteps<T> addWithSteps(final T value) {
        if (this.isEmpty()) {
            return new BinaryTreeSteps<T>(
                new BinaryTree<T>(new BinaryTreeNode<T>(value)),
                new BinaryTreeStep<T>(BinaryTreeStepType.ADD, value)
            );
        }
        return this.root
            .get()
            .addWithStepsAndEmptyParent(value)
            .stream()
            .map(pair -> new BinaryTreeAndStep<T>(new BinaryTree<T>(pair.x), pair.y))
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

    public BinaryTree<T> copy() {
        return new BinaryTree<>(this.root.isEmpty() ? Optional.empty() : Optional.of(this.root.get().copyWithEmptyParent()));
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
            .map(pair -> new BinaryTreeAndStep<T>(new BinaryTree<T>(pair.x), pair.y))
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
