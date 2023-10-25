package exercisegenerator.algorithms.trees;

import java.io.*;
import java.util.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.trees.*;

public class BinarySearchTreeAlgorithm implements AlgorithmImplementation {

    public static final BinarySearchTreeAlgorithm INSTANCE = new BinarySearchTreeAlgorithm();

    public static <T extends Comparable<T>> BinaryTreeSteps<T> bstree(
        final BinaryTree<T> tree,
        final Deque<TreeOperation<T>> tasks
    ) {
        return TreeAlgorithms.binaryTreeAlgorithm(tree, tasks);
    }

    private BinarySearchTreeAlgorithm() {

    }

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        TreeAlgorithms.treeAlgorithm(input, TreeAlgorithms.BINARY_TREE_FACTORY, BinarySearchTreeAlgorithm::bstree);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
