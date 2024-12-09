package exercisegenerator.structures.trees;

import java.util.*;

public class SearchTreeSteps<T extends Comparable<T>> extends LinkedList<SearchTreeAndStep<T>> {

    private static final long serialVersionUID = 1094279806742181947L;

    public SearchTreeSteps() {}

    public SearchTreeSteps(final SearchTree<T> tree, final SearchTreeStep<T> step) {
        super(Collections.singleton(new SearchTreeAndStep<T>(tree, step)));
    }

}
