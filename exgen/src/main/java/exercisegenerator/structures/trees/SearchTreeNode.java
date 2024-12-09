package exercisegenerator.structures.trees;

import java.util.*;
import java.util.stream.*;

public interface SearchTreeNode<T extends Comparable<T>> {

    static <T extends Comparable<T>> int height(final Optional<? extends SearchTreeNode<T>> node) {
        return node.isEmpty() ? 0 : node.get().getHeight();
    }

    SearchTreeNodeSteps<T> addWithSteps(final T value);

    boolean containsAll(final Collection<? extends T> values);

    int getHeight();

    default T getMin() {
        return this.stream().findFirst().get();
    }

    SearchTreeNodeSteps<T> removeWithSteps(final T value);

    int size();

    Stream<T> stream();

    String toTikZ();

}
