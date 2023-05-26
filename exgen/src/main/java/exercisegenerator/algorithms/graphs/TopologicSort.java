package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

public class TopologicSort implements AlgorithmImplementation {

    public static final TopologicSort INSTANCE = new TopologicSort();

    private TopologicSort() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        boolean fail;
        final GridGraph graph = new GridGraph();
        final int[][] sparseAdjacencyMatrix = GraphAlgorithms.parseOrGenerateGridGraph(input.options);
        do {
            try {
                fail = false;
                final boolean writeText = true; // TODO check whether this can be removed
                final Optional<BufferedWriter> optionalWriterSpace = Algorithm.getOptionalSpaceWriter(input);
                graph.createGraph(sparseAdjacencyMatrix);
                graph.printTopologicalOrder(optionalWriterSpace, input.solutionWriter, false, writeText);
            } catch (final IOException e) {
                //System.out.println("Caught cycle-exception.");
                fail = true;
                final Random gen = new Random();
                for (int i = 0; i < graph.numOfVerticesInSparseAdjacencyMatrix(); i++) {
                    for (int j = 0; j < graph.numOfNeighborsInSparseAdjacencyMatrix(); j++) {
                        if (graph.isNecessarySparseMatrixEntry(i,j) ) {
                            int entry = gen.nextInt(3);
                            entry = entry == 2 ? -1 : entry;
                            if (graph.isLegalEntryForSparseAdjacencyMatrix(entry)) {
                                sparseAdjacencyMatrix[i][j] = entry;
                            } else {
                                System.out.println("SHOULD NOT HAPPEN!");
                            }
                        } else {
                            sparseAdjacencyMatrix[i][j] = 0;
                        }
                    }
                }
            }
        } while (fail);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
