package exercisegenerator.algorithms.trees;

import clit.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.trees.*;

public class BinarySearchTreeAlgorithm implements SearchTreeAlgorithm {

    public static final BinarySearchTreeAlgorithm INSTANCE = new BinarySearchTreeAlgorithm();

    static final BinaryTreeFactory<Integer> BINARY_TREE_FACTORY =
        new BinaryTreeFactory<Integer>(new BinaryTreeNodeFactory<Integer>());

    private BinarySearchTreeAlgorithm() {}

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public BinaryTreeFactory<Integer> parseOrGenerateTreeFactory(final Parameters<Flag> options) {
        return BinarySearchTreeAlgorithm.BINARY_TREE_FACTORY;
    }

}
