package exercisegenerator.algorithms.trees;

import clit.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.trees.*;

public class BTreeAlgorithm implements SearchTreeAlgorithm {

    public static final BTreeAlgorithm INSTANCE = new BTreeAlgorithm();

    private BTreeAlgorithm() {}

    @Override
    public String commandPrefix() {
        return "Btree";
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public BTreeFactory<Integer> parseOrGenerateTreeFactory(final Parameters<Flag> options) {
        return new BTreeFactory<Integer>(
            options.containsKey(Flag.DEGREE) ? Integer.parseInt(options.get(Flag.DEGREE)) : 2
        );
    }

}
