package exercisegenerator.structures.trees;

import java.util.*;

public class AVLTree<T extends Comparable<T>> extends BinaryTree<T> {

    AVLTree(Optional<AVLTreeNode<T>> root, AVLTreeNodeFactory<T> nodeFactory) {
        super(root, nodeFactory);
    }
    
}
