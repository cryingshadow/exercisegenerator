package exercisegenerator.structures.trees;

public class BinaryTreeTest {

    public static <T extends Comparable<T>> boolean isWellFormed(final BinaryTree<T> tree) {
        if (tree.isEmpty()) {
            return true;
        }
        final BinaryTreeNode<T> root = tree.root.get();
        if (root.parent.isPresent()) {
            return false;
        }
        return BinaryTreeTest.isWellFormed(root);
    }

    private static <T extends Comparable<T>> boolean isWellFormed(final BinaryTreeNode<T> node) {
        if (node.leftChild.isPresent()) {
            final BinaryTreeNode<T> left = node.leftChild.get();
            if (left.parent.isEmpty() || left.parent.get() != node) {
                return false;
            }
        }
        if (node.rightChild.isPresent()) {
            final BinaryTreeNode<T> right = node.rightChild.get();
            if (right.parent.isEmpty() || right.parent.get() != node) {
                return false;
            }
        }
        return true;
    }

}
