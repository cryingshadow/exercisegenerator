package exercisegenerator.algorithms.trees;

import java.io.*;
import java.util.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.trees.*;

public class RedBlackTreeAlgorithm implements AlgorithmImplementation {

    public static final RedBlackTreeAlgorithm INSTANCE = new RedBlackTreeAlgorithm();

    public static <T extends Comparable<T>> BinaryTreeSteps<T> redBlackTree(
        final RedBlackTree<T> tree,
        final Deque<TreeOperation<T>> tasks
    ) {
        return TreeAlgorithms.binaryTreeAlgorithm(tree, tasks);
    }

    private RedBlackTreeAlgorithm() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        TreeAlgorithms.treeAlgorithm(input, TreeAlgorithms.RED_BLACK_TREE_FACTORY, TreeAlgorithms::binaryTreeAlgorithm);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
