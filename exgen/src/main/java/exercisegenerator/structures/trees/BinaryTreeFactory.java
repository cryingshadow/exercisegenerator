package exercisegenerator.structures.trees;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class BinaryTreeFactory<T extends Comparable<T>> {

    @SuppressWarnings("unchecked")
    static <T extends BinaryTree<V>, V extends Comparable<V>> T create(
        final Supplier<T> creator,
        final Deque<TreeOperation<V>> construction
    ) {
        T tree = creator.get();
        for (final TreeOperation<V> operation : construction) {
            if (operation.add) {
                tree = (T)tree.add(operation.value);
            } else {
                tree = (T)tree.remove(operation.value);
            }
        }
        return tree;
    }

    @SafeVarargs
    static <T extends Comparable<T>> Deque<TreeOperation<T>> toConstruction(final T... values) {
        return Arrays.asList(values)
            .stream()
            .map(value -> new TreeOperation<T>(value, true))
            .collect(Collectors.toCollection(ArrayDeque::new));
    }

    final BinaryTreeNodeFactory<T> nodeFactory;

    public BinaryTreeFactory(final BinaryTreeNodeFactory<T> nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    public BinaryTree<T> create() {
        return new BinaryTree<T>(Optional.empty(), this);
    }

    public BinaryTree<T> create(final BinaryTreeNode<T> root) {
        return this.create(Optional.of(root));
    }

    public BinaryTree<T> create(final Deque<TreeOperation<T>> construction) {
        return BinaryTreeFactory.create(this::create, construction);
    }

    public BinaryTree<T> create(final Optional<? extends BinaryTreeNode<T>> root) {
        return new BinaryTree<T>(root, this);
    }

    @SuppressWarnings("unchecked")
    public BinaryTree<T> create(final T... values) {
        return this.create(BinaryTreeFactory.toConstruction(values));
    }

    public BinaryTree<T> create(final T rootValue) {
        return this.create(this.nodeFactory.create(rootValue));
    }

}
