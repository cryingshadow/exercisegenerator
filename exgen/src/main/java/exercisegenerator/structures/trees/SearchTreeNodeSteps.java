package exercisegenerator.structures.trees;

import java.util.*;

public class SearchTreeNodeSteps<T extends Comparable<T>> extends LinkedList<SearchTreeNodeAndStep<T>> {

    private static final long serialVersionUID = 3045297711825057413L;

    public SearchTreeNodeSteps() {}

    public SearchTreeNodeSteps(final Optional<? extends SearchTreeNode<T>> node, final SearchTreeStep<T> step) {
        super(Collections.singleton(new SearchTreeNodeAndStep<T>(node, step)));
    }

    public SearchTreeNodeSteps(final SearchTreeNode<T> node, final SearchTreeStep<T> step) {
        super(Collections.singleton(new SearchTreeNodeAndStep<T>(Optional.of(node), step)));
    }

}
