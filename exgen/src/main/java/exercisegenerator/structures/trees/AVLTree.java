package exercisegenerator.structures.trees;

import java.util.*;

public class AVLTree<T extends Comparable<T>> extends BinaryTree<T> {

    AVLTree(Optional<AVLTreeNode<T>> root, AVLTreeFactory<T> treeFactory) {
        super(root, treeFactory);
    }
    
}
