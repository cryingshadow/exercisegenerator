package exercisegenerator.structures.trees;

import java.util.*;

import exercisegenerator.structures.*;

public class AVLTreeFactory<T extends Comparable<T>> extends BinaryTreeFactory<T> {

    public AVLTreeFactory(AVLTreeNodeFactory<T> nodeFactory) {
        super(nodeFactory);
    }

    @Override
    public BinaryTree<T> create() {
        return new AVLTree<T>(Optional.empty(), this);
    }
    
    @Override
    public BinaryTree<T> create(final Deque<Pair<T, Boolean>> construction) {
        return BinaryTreeFactory.create(this::create, construction);
    }

    @Override
    @SuppressWarnings("unchecked")
    public BinaryTree<T> create(final T... values) {
        return this.create(BinaryTreeFactory.toConstruction(values));
    }

}
