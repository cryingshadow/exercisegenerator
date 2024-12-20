package exercisegenerator.algorithms.trees;

import clit.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.trees.*;

public class AVLTreeAlgorithm implements SearchTreeAlgorithm {

    public static final AVLTreeAlgorithm INSTANCE = new AVLTreeAlgorithm();

    static final AVLTreeFactory<Integer> AVL_TREE_FACTORY =
        new AVLTreeFactory<Integer>(new AVLTreeNodeFactory<Integer>());

    private AVLTreeAlgorithm() {}

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public AVLTreeFactory<Integer> parseOrGenerateTreeFactory(final Parameters<Flag> options) {
        return AVLTreeAlgorithm.AVL_TREE_FACTORY;
    }

}
