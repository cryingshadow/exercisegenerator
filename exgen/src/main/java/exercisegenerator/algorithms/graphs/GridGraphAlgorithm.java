package exercisegenerator.algorithms.graphs;

import java.io.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.*;

interface GridGraphAlgorithm<S> extends AlgorithmImplementation<GridGraph, S> {

    static int[][] parseOrGenerateGridGraph(final Parameters<Flag> options) throws IOException {
        return new ParserAndGenerator<int[][]>(
            GridGraphAlgorithm::parseGridGraph,
            GridGraphAlgorithm::generateGridGraph
        ).getResult(options);
    }

    /**
     * TODO this comment is taken from another method, which has been deleted - it is kept here for reference of grid
     * graphs
     *
     * Takes a sparse version of the adjacency matrix and constructs the according graph, which has not more than 35
     * vertices and each vertex has a degree not greater than 8. The tex-code to present the resulting graph is then
     * written to the file given by the fourth argument. The results from applying the given operation on this graph
     * are written to the file given by the last argument.
     *
     * @param graph An empty graph object.
     * @param sparseAdjacencyMatrix A sparse version of the adjacency matrix in order to construct the according graph
     *            (elements in a row are separated by "," and rows are separated by line breaks). Sparse means, that if
     *            we consider the vertices being ordered in a 5x7 grid, we only store every second vertex, starting
     *            from the first vertex in the first row and traversing row wise (i.e., then the 3. vertex in the first
     *            row, then the 5. vertex in the first row, then the 7. vertex in the first row, then the 2. vertex in
     *            the second row, ...). Furthermore, sparse means, that we only store adjacencies to not more than 6
     *            neighbors (according to the grid) of a vertex, more precisely, those six which are positioned north,
     *            east, south, southwest, west and northwest of the vertex. If the entry in the sparse version of the
     *            adjacency matrix is
     *                * 1, then there is an outgoing edge
     *                * -1, then there is an ingoing edge
     *                * 2, then there is an outgoing and ingoing edge
     *                * 0, no edge
     *            to the corresponding vertex.
     *
     *            Example: "x,2,2,x,x,x
     *                      x,2,2,2,2,x
     *                      x,2,2,2,2,x
     *                      x,x,2,2,2,x
     *                      2,2,2,2,2,2
     *                      2,2,2,2,2,2
     *                      2,2,2,2,2,2
     *                      2,2,2,x,x,x
     *                      2,2,2,2,2,2
     *                      2,2,2,2,2,2
     *                      2,x,2,2,2,2
     *                      2,2,2,2,2,2
     *                      2,2,2,2,2,2
     *                      2,2,2,2,2,2
     *                      2,2,x,x,x,x
     *                      2,2,x,x,2,2
     *                      2,2,x,x,2,2
     *                      2,x,x,x,2,2"
     *            This is the graph where every of the 35 vertices in the grid is connected to all it's neighbors. At
     *            the positions with "x" the values do not influence the graph as they belong to potential neighbors
     *            not being in the grid.
     * @param operation The name of the operation to apply to the graph.
     * @param writer The writer for the solution.
     * @param writerSpace The writer for the tree to start with (the one reached after the <code>construction</code>
     *                    operations). May be null if this tree should not be displayed separately.
     */
    private static int[][] generateGridGraph(final Parameters<Flag> options) {
        final GridGraph graph = new GridGraph();
        final int[][] sparseAdjacencyMatrix =
            new int[graph.numOfVerticesInSparseAdjacencyMatrix()][graph.numOfNeighborsInSparseAdjacencyMatrix()];
        final String errorMessage =
            new String(
                "You need to provide "
                + graph.numOfVerticesInSparseAdjacencyMatrix()
                + " lines and each line has to carry "
                + graph.numOfNeighborsInSparseAdjacencyMatrix()
                + " numbers being either 0, -1, 1 or 2, which are separated by ','!\n"
                + "Example:\n"
                + "x,0,0,x,x,x\nx,0,0,0,0,x\nx,0,0,0,0,x\nx,x,0,0,0,x\n0,2,0,1,1,0\n0,0,2,1,1,0\n0,0,0,0,0,0\n2,-1,0,x,x,x\n0,1,2,1,-1,1\n"
                + "0,0,0,0,0,0\n0,x,0,0,0,0\n1,2,0,0,0,1\n0,0,0,0,0,0\n0,0,0,0,0,0\n0,0,x,x,x,x\n0,0,x,x,0,0\n0,0,x,x,0,0\n0,x,x,x,0,0\n\n"
                + "where x can be anything and will not affect the resulting graph."
            );
        if (Algorithm.SHARIR.name.equals(options.get(Flag.ALGORITHM))) {
            final int[] numbers = new int[18];
            for (int i = 0; i < numbers.length; i++) {
                final int rndNumber = Main.RANDOM.nextInt(9);
                if (rndNumber < 3) {
                    numbers[i] = -1;
                } else if (rndNumber < 4) {
                    numbers[i] = 0;
                } else if (rndNumber < 7) {
                    numbers[i] = 1;
                } else {
                    numbers[i] = 2;
                }
            }
            sparseAdjacencyMatrix[4][1] = numbers[0];
            sparseAdjacencyMatrix[4][2] = numbers[1];

            sparseAdjacencyMatrix[5][2] = numbers[2];
            sparseAdjacencyMatrix[5][3] = numbers[3];
            sparseAdjacencyMatrix[5][4] = numbers[4];

            sparseAdjacencyMatrix[8][0] = numbers[5];
            sparseAdjacencyMatrix[8][1] = numbers[6];
            sparseAdjacencyMatrix[8][2] = numbers[7];
            sparseAdjacencyMatrix[8][4] = numbers[8];
            sparseAdjacencyMatrix[8][5] = numbers[9];

            sparseAdjacencyMatrix[9][0] = numbers[10];
            sparseAdjacencyMatrix[9][2] = numbers[11];
            sparseAdjacencyMatrix[9][3] = numbers[12];
            sparseAdjacencyMatrix[9][4] = numbers[13];

            sparseAdjacencyMatrix[12][0] = numbers[14];
            sparseAdjacencyMatrix[12][1] = numbers[15];
            sparseAdjacencyMatrix[12][4] = numbers[16];
            sparseAdjacencyMatrix[12][5] = numbers[17];
        } else {
            for (int i = 0; i < graph.numOfVerticesInSparseAdjacencyMatrix(); i++) {
                for (int j = 0; j < graph.numOfNeighborsInSparseAdjacencyMatrix(); j++) {
                    if (graph.isNecessarySparseMatrixEntry(i,j) ) {
                        final int rndNumber = Main.RANDOM.nextInt(18);
                        int entry = 0;
                        if (rndNumber >= 10 && rndNumber < 13) {
                            entry = -1;
                        } else if (rndNumber >= 13 && rndNumber < 16) {
                            entry = 1;
                        } else if (rndNumber >= 16) {
                            entry = 2;
                        }
                        if (graph.isLegalEntryForSparseAdjacencyMatrix(entry)) {
                            sparseAdjacencyMatrix[i][j] = entry;
                        } else {
                            System.out.println(errorMessage);
                            return null;
                        }
                    } else {
                        sparseAdjacencyMatrix[i][j] = 0;
                    }
                }
            }
        }
        return sparseAdjacencyMatrix;
    }

    private static int[][] parseGridGraph(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        final GridGraph graph = new GridGraph();
        final int[][] sparseAdjacencyMatrix =
            new int[graph.numOfVerticesInSparseAdjacencyMatrix()][graph.numOfNeighborsInSparseAdjacencyMatrix()];
        final String errorMessage =
            new String(
                "You need to provide "
                + graph.numOfVerticesInSparseAdjacencyMatrix()
                + " lines and each line has to carry "
                + graph.numOfNeighborsInSparseAdjacencyMatrix()
                + " numbers being either 0, -1, 1 or 2, which are separated by ','!\n"
                + "Example:\n"
                + "x,0,0,x,x,x\nx,0,0,0,0,x\nx,0,0,0,0,x\nx,x,0,0,0,x\n0,2,0,1,1,0\n0,0,2,1,1,0\n0,0,0,0,0,0\n2,-1,0,x,x,x\n0,1,2,1,-1,1\n"
                + "0,0,0,0,0,0\n0,x,0,0,0,0\n1,2,0,0,0,1\n0,0,0,0,0,0\n0,0,0,0,0,0\n0,0,x,x,x,x\n0,0,x,x,0,0\n0,0,x,x,0,0\n0,x,x,x,0,0\n\n"
                + "where x can be anything and will not affect the resulting graph."
            );
        String line = null;
        int rowNum = 0;
        while ((line = reader.readLine()) != null) {
            final String[] vertices = line.split(",");
            if (vertices.length != graph.numOfNeighborsInSparseAdjacencyMatrix()) {
                System.out.println(errorMessage);
                return null;
            }
            for (int i = 0; i < graph.numOfNeighborsInSparseAdjacencyMatrix(); i++) {
                if (graph.isNecessarySparseMatrixEntry(rowNum,i) ) {
                    final int entry = Integer.parseInt(vertices[i].trim());
                    if (graph.isLegalEntryForSparseAdjacencyMatrix(entry)) {
                        sparseAdjacencyMatrix[rowNum][i] = entry;
                    } else {
                        System.out.println(errorMessage);
                        return null;
                    }
                } else {
                    sparseAdjacencyMatrix[rowNum][i] = 0;
                }
            }
            rowNum++;
        }
        return sparseAdjacencyMatrix;
    }

    @Override
    default public GridGraph parseOrGenerateProblem(final Parameters<Flag> options) throws IOException {
        final GridGraph graph = new GridGraph();
        graph.createGraph(GridGraphAlgorithm.parseOrGenerateGridGraph(options));
        return graph;
    }

}
