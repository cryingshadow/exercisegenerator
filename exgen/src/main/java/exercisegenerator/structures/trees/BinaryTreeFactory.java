package exercisegenerator.structures.trees;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import exercisegenerator.structures.*;

public class BinaryTreeFactory<T extends Comparable<T>> {
    
    public BinaryTree<T> create() {
        return new BinaryTree<T>(Optional.empty(), new BinaryTreeNodeFactory<T>());
    }
    
    @SuppressWarnings("unchecked")
    static <T extends BinaryTree<V>, V extends Comparable<V>> T create(
        Supplier<T> creator,
        final Deque<Pair<V, Boolean>> construction
    ) {
        T tree = creator.get();
        for (final Pair<V, Boolean> operation : construction) {
            if (operation.y) {
                tree = (T)tree.add(operation.x);
            } else {
                tree = (T)tree.remove(operation.x);
            }
        }
        return tree;
    }

    public BinaryTree<T> create(final Deque<Pair<T, Boolean>> construction) {
        return BinaryTreeFactory.create(this::create, construction);
    }

    @SafeVarargs
    static <T extends Comparable<T>> Deque<Pair<T, Boolean>> toConstruction(final T... values) {
        return Arrays.asList(values)
            .stream()
            .map(value -> new Pair<T, Boolean>(value, true))
            .collect(Collectors.toCollection(ArrayDeque::new));
    }
    
    @SuppressWarnings("unchecked")
    public BinaryTree<T> create(final T... values) {
        return this.create(BinaryTreeFactory.toConstruction(values));
    }

}
