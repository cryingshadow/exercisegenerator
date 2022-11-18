package exercisegenerator.structures.trees;

import java.util.*;

public class AVLTree<T extends Comparable<T>> extends BinaryTree<T> {

    AVLTree(final Optional<AVLTreeNode<T>> root, final AVLTreeFactory<T> treeFactory) {
        super(root, treeFactory);
    }

    @Override
    public String getName() {
        return "AVL-Baum";
    }

    @Override
    public String getOperations() {
        return "\\emphasize{Einf\\\"uge-}, \\emphasize{L\\\"osch-}, \\emphasize{Ersetzungs-} und \\emphasize{Rotations-}Operation";
    }

    @Override
    public int hashCode() {
        return super.hashCode() + 1;
    }

}
