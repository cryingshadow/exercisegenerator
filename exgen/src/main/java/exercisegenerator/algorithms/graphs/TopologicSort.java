package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

public class TopologicSort implements GraphAlgorithm<List<String>> {

    public static final TopologicSort INSTANCE = new TopologicSort();

    private TopologicSort() {}

    @Override
    public List<String> apply(final GraphProblem graph) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        boolean fail;
        final GridGraph graph = new GridGraph();
        final int[][] sparseAdjacencyMatrix = new int[0][0]; //GridGraphAlgorithm.parseOrGenerateGridGraph(input.options);
        do {
            try {
                fail = false;
                final boolean writeText = true; // TODO check whether this can be removed
                graph.createGraph(sparseAdjacencyMatrix);
                graph.printTopologicalOrder(input.exerciseWriter, input.solutionWriter, false, writeText);
            } catch (final IOException e) {
                //System.out.println("Caught cycle-exception.");
                fail = true;
                for (int i = 0; i < graph.numOfVerticesInSparseAdjacencyMatrix(); i++) {
                    for (int j = 0; j < graph.numOfNeighborsInSparseAdjacencyMatrix(); j++) {
                        if (graph.isNecessarySparseMatrixEntry(i,j) ) {
                            int entry = Main.RANDOM.nextInt(3);
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

    @Override
    public void printExercise(
        final GraphProblem problem,
        final List<String> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub
    }

    @Override
    public void printSolution(
        final GraphProblem problem,
        final List<String> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub
    }

}
