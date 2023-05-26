package exercisegenerator.algorithms;

import java.io.*;

import exercisegenerator.structures.*;

public interface AlgorithmImplementation {

    void executeAlgorithm(final AlgorithmInput input) throws IOException;

    String[] generateTestParameters();

}
