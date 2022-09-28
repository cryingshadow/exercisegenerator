package exercisegenerator.structures.trees;

import java.util.*;
import java.util.stream.*;

import exercisegenerator.structures.*;

public class BinaryTree<T extends Comparable<T>> implements Iterable<T> { //TODO rotations can destroy strict order

    public static <T extends Comparable<T>> BinaryTree<T> create() {
        return new BinaryTree<T>(Optional.empty());
    }

    public static <T extends Comparable<T>> BinaryTree<T> create(final Collection<? extends T> values) {
        return BinaryTree.<T>create().addAll(values);
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
        return BinaryTree.create(List.of(values));
    }

    public final Optional<BinaryTreeNode<T>> root;

    private BinaryTree(final BinaryTreeNode<T> root) {
        this(Optional.of(root));
    }

    private BinaryTree(final Optional<BinaryTreeNode<T>> root) {
        this.root = root;
    }

    public BinaryTree<T> add(final T value) {
        return this.addAll(Collections.singleton(value));
    }

    public BinaryTree<T> addAll(final Collection<? extends T> values) {
        final List<Pair<BinaryTree<T>, BinaryTreeStep>> trees = this.addAllWithSteps(values);
        return trees.get(trees.size() - 1).x;
    }

    public List<Pair<BinaryTree<T>, BinaryTreeStep>> addAllWithSteps(final Collection<? extends T> values) {
        if (values.isEmpty()) {
            return Collections.emptyList();
        }
        if (this.isEmpty()) {
            final LinkedList<T> list = new LinkedList<T>(values);
            final List<Pair<BinaryTree<T>, BinaryTreeStep>> result =
                new ArrayList<Pair<BinaryTree<T>, BinaryTreeStep>>();
            final BinaryTree<T> tree = new BinaryTree<T>(new BinaryTreeNode<T>(list.poll()));
            result.add(new Pair<BinaryTree<T>, BinaryTreeStep>(tree, BinaryTreeStep.ADD));
            result.addAll(tree.addAllWithSteps(list));
            return result;
        }
        return this.root
            .get()
            .addAllWithSteps(values)
            .stream()
            .map(pair -> new Pair<BinaryTree<T>, BinaryTreeStep>(new BinaryTree<T>(pair.x), pair.y))
            .toList();
    }

    public List<Pair<BinaryTree<T>, BinaryTreeStep>> addWithSteps(final T value) {
        return this.addAllWithSteps(Collections.singleton(value));
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
        return this.removeAll(Collections.singleton(value));
    }

    public BinaryTree<T> removeAll(final Collection<? extends T> values) {
        final List<Pair<BinaryTree<T>, BinaryTreeStep>> trees = this.removeAllWithSteps(values);
        return trees.get(trees.size() - 1).x;
    }

    public List<Pair<BinaryTree<T>, BinaryTreeStep>> removeAllWithSteps(final Collection<? extends T> values) {
        if (this.isEmpty()) {
            return Collections.emptyList();
        }
        return this.root
            .get()
            .removeAllWithSteps(values)
            .stream()
            .map(pair -> new Pair<BinaryTree<T>, BinaryTreeStep>(new BinaryTree<T>(pair.x), pair.y))
            .toList();
    }

    public List<Pair<BinaryTree<T>, BinaryTreeStep>> removeWithSteps(final T value) {
        return this.removeAllWithSteps(Collections.singleton(value));
    }

    public BinaryTree<T> retainAll(final Collection<? extends T> values) {
        if (this.isEmpty()) {
            return this;
        }
        return new BinaryTree<T>(this.root.get().retainAll(values));
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
