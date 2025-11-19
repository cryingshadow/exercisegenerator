package exercisegenerator.structures.trees;

import java.util.*;

public class AVLTree<T extends Comparable<T>> extends BinaryTree<T> {

    private static final List<String> OPERATIONS =
        List.of(
            "Einf\\\"ugeoperation",
            "Ersetzung",
            "L\\\"oschoperation",
            "Rotation"
        );

    AVLTree(final Optional<AVLTreeNode<T>> root, final AVLTreeFactory<T> treeFactory) {
        super(root, treeFactory);
    }

    @Override
    public String getName(boolean genitive) {
        return "AVL-Baum" + (genitive ? "s" : "");
    }

    @Override
    public String getOperations() {
        return SearchTree.formatOperations(AVLTree.OPERATIONS);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + 1;
    }

}
