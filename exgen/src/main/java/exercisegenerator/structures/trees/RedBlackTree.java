package exercisegenerator.structures.trees;

import java.util.*;

import exercisegenerator.io.*;

public class RedBlackTree<T extends Comparable<T>> extends BinaryTree<T> {

    RedBlackTree(final Optional<RedBlackTreeNode<T>> root, final RedBlackTreeFactory<T> treeFactory) {
        super(root, treeFactory);
    }

    @Override
    public String getName() {
        return "Rot-Schwarz-Baum";
    }

    @Override
    public String getOperations() {
        return "\\emphasize{Einf\\\"uge-}, \\emphasize{L\\\"osch-}, \\emphasize{Umf\\\"arbe-}, \\emphasize{Ersetzungs-} und \\emphasize{Rotations-}Operation";
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
