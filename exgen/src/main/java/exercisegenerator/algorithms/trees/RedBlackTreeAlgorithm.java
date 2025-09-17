package exercisegenerator.algorithms.trees;

import clit.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.trees.*;

public class RedBlackTreeAlgorithm implements SearchTreeAlgorithm {

    public static final RedBlackTreeAlgorithm INSTANCE = new RedBlackTreeAlgorithm();

    static final RedBlackTreeFactory<Integer> RED_BLACK_TREE_FACTORY =
        new RedBlackTreeFactory<Integer>(new RedBlackTreeNodeFactory<Integer>());

    private RedBlackTreeAlgorithm() {}

    @Override
    public String commandPrefix() {
        return "RedBlackTree";
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public RedBlackTreeFactory<Integer> parseOrGenerateTreeFactory(final Parameters<Flag> options) {
        return RedBlackTreeAlgorithm.RED_BLACK_TREE_FACTORY;
    }

}
