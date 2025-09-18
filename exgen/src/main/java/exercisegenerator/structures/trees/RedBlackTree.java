package exercisegenerator.structures.trees;

import java.util.*;

import exercisegenerator.io.*;

public class RedBlackTree<T extends Comparable<T>> extends BinaryTree<T> {

    private static final List<String> OPERATIONS =
        List.of(
            "Einf\\\"ugeoperation",
            "Ersetzung",
            "L\\\"oschoperation",
            "Rotation",
            "Umf\\\"arbung"
        );

    RedBlackTree(final Optional<RedBlackTreeNode<T>> root, final RedBlackTreeFactory<T> treeFactory) {
        super(root, treeFactory);
    }

    @Override
    public String getName() {
        return "Rot-Schwarz-Baum";
    }

    @Override
    public String getOperations() {
        return SearchTree.formatOperations(RedBlackTree.OPERATIONS);
    }

    @Override
    public TikZStyle getTikZStyle() {
        return TikZStyle.RED_BLACK_TREE;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + 7;
    }

}
