package exercisegenerator.algorithms.trees;

import java.io.*;
import java.util.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.trees.*;

public class AVLTreeAlgorithm implements AlgorithmImplementation {

    public static final AVLTreeAlgorithm INSTANCE = new AVLTreeAlgorithm();

    public static <T extends Comparable<T>> BinaryTreeSteps<T> avltree(
        final AVLTree<T> tree,
        final Deque<Pair<T, Boolean>> tasks
    ) {
        final BinaryTreeSteps<T> result = new BinaryTreeSteps<T>();
        AVLTree<T> currentTree = tree;
        for (final Pair<T, Boolean> task : tasks) {
            result.addAll(
                task.y ? currentTree.addWithSteps(task.x) : currentTree.removeWithSteps(task.x)
            );
            currentTree = (AVLTree<T>)result.getLast().x;
        }
        return result;
    }

    private AVLTreeAlgorithm() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        TreeAlgorithms.treeAlgorithm(
            input,
            TreeAlgorithms.AVL_TREE_FACTORY,
            (tree, tasks) -> AVLTreeAlgorithm.avltree((AVLTree<Integer>)tree, tasks)
        );
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
