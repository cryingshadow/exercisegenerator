package exercisegenerator.structures.trees;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public interface SearchTreeFactory<T extends Comparable<T>> {

    @SuppressWarnings("unchecked")
    static <T extends SearchTree<V>, V extends Comparable<V>> T create(
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

    default SearchTree<T> create() {
        return this.create(Optional.empty());
    }

    default SearchTree<T> create(final Deque<TreeOperation<T>> construction) {
        return SearchTreeFactory.create(this::create, construction);
    }

    SearchTree<T> create(final Optional<? extends SearchTreeNode<T>> root);

    default SearchTree<T> create(final SearchTreeNode<T> root) {
        return this.create(Optional.of(root));
    }

    @SuppressWarnings("unchecked")
    default SearchTree<T> create(final T... values) {
        return this.create(SearchTreeFactory.toConstruction(values));
    }

}
