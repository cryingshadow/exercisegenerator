package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;
import java.util.Map.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

abstract class GraphAlgorithms {

    /**
     * The default value being probably close to the edge values.
     */
    static final int DEFAULT_EDGE_ROOT;

    private static final Set<String> GRAPH_ALGORITHMS_WITH_START_VERTEX;

    private static final Set<String> UNDIRECTED_GRAPH_ALGORITHMS;

    static {
        DEFAULT_EDGE_ROOT = 3;
        GRAPH_ALGORITHMS_WITH_START_VERTEX = GraphAlgorithms.initGraphAlgorithmsWithStartVertex();
        UNDIRECTED_GRAPH_ALGORITHMS = GraphAlgorithms.initUndirectedGraphAlgorithms();
    }

    /**
     * Adds a vertex to the graph and updates the grid layout accordingly.
     * @param vertex The vertex to add.
     * @param graph The graph to add the vertex to.
     * @param pos The vertex's position in the grid layout.
     * @param grid The grid.
     * @param positions zThe grid layout positions.
     * @return The grid layout position of the added vertex.
     */
    static VertexGridPosition addVertex(
        final Vertex<String> vertex,
        final Graph<String, ?> graph,
        final Pair<GridCoordinates, Boolean> pos,
        final Map<GridCoordinates, Vertex<String>> grid,
        final Map<Vertex<String>, VertexGridPosition> positions
    ) {
        graph.addVertex(vertex);
        grid.put(pos.x, vertex);
        final int x = pos.x.x;
        final int y = pos.x.y;
        final VertexGridPosition gridPos = new VertexGridPosition(pos.x.x, pos.x.y, pos.y);
        positions.put(vertex, gridPos);
        GridCoordinates nextPos = new GridCoordinates(x, y - 1);
        Vertex<String> nextVertex = grid.get(nextPos);
        if (nextVertex != null) {
            final VertexGridPosition nextGridPos = positions.get(nextVertex);
            gridPos.north = nextGridPos;
            nextGridPos.south = gridPos;
        }
        nextPos = new GridCoordinates(x + 1, y);
        nextVertex = grid.get(nextPos);
        if (nextVertex != null) {
            final VertexGridPosition nextGridPos = positions.get(nextVertex);
            gridPos.east = nextGridPos;
            nextGridPos.west = gridPos;
        }
        nextPos = new GridCoordinates(x, y + 1);
        nextVertex = grid.get(nextPos);
        if (nextVertex != null) {
            final VertexGridPosition nextGridPos = positions.get(nextVertex);
            gridPos.south = nextGridPos;
            nextGridPos.north = gridPos;
        }
        nextPos = new GridCoordinates(x - 1, y);
        nextVertex = grid.get(nextPos);
        if (nextVertex != null) {
            final VertexGridPosition nextGridPos = positions.get(nextVertex);
            gridPos.west = nextGridPos;
            nextGridPos.east = gridPos;
        }
        if (pos.y) {
            nextPos = new GridCoordinates(x + 1, y - 1);
            nextVertex = grid.get(nextPos);
            if (nextVertex != null) {
                final VertexGridPosition nextGridPos = positions.get(nextVertex);
                gridPos.northeast = nextGridPos;
                nextGridPos.southwest = gridPos;
            }
            nextPos = new GridCoordinates(x + 1, y + 1);
            nextVertex = grid.get(nextPos);
            if (nextVertex != null) {
                final VertexGridPosition nextGridPos = positions.get(nextVertex);
                gridPos.southeast = nextGridPos;
                nextGridPos.northwest = gridPos;
            }
            nextPos = new GridCoordinates(x - 1, y + 1);
            nextVertex = grid.get(nextPos);
            if (nextVertex != null) {
                final VertexGridPosition nextGridPos = positions.get(nextVertex);
                gridPos.southwest = nextGridPos;
                nextGridPos.northeast = gridPos;
            }
            nextPos = new GridCoordinates(x - 1, y - 1);
            nextVertex = grid.get(nextPos);
            if (nextVertex != null) {
                final VertexGridPosition nextGridPos = positions.get(nextVertex);
                gridPos.northwest = nextGridPos;
                nextGridPos.southeast = gridPos;
            }
        }
        return gridPos;
    }

    static <V> List<Vertex<V>> getSortedListOfVertices(
        final Graph<V, Integer> graph,
        final Comparator<Vertex<V>> comparator
    ) {
        final List<Vertex<V>> vertices = new ArrayList<Vertex<V>>(graph.getVertices());
        if (comparator != null) {
            Collections.sort(vertices, comparator);
        }
        return vertices;
    }

    static double parseDistanceFactor(final Parameters options) {
        return Double.parseDouble(options.getOrDefault(Flag.DEGREE, "1.0"));
    }

    static Pair<Graph<String, Integer>, Vertex<String>> parseOrGenerateGraph(final Parameters options)
    throws IOException {
        return new ParserAndGenerator<Pair<Graph<String, Integer>, Vertex<String>>>(
            GraphAlgorithms::parseGraph,
            GraphAlgorithms::generateGraph
        ).getResult(options);
    }

    static int[][] parseOrGenerateGridGraph(final Parameters options) throws IOException {
        return new ParserAndGenerator<int[][]>(
            GraphAlgorithms::parseGridGraph,
            GraphAlgorithms::generateGridGraph
        ).getResult(options);
    }

    static <V> void printGraphExercise(
        final Graph<V, Integer> graph,
        final String task,
        final double distanceFactor,
        final GraphPrintMode mode,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Betrachten Sie den folgenden Graphen:\\\\");
        Main.newLine(writer);
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        graph.printTikZ(mode, distanceFactor, null, writer);
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
        Main.newLine(writer);
        writer.write(task);
        Main.newLine(writer);
    }

    /**
     * @param root A value which is probably close to the result.
     * @return A random non-negative edge value.
     */
    static int randomEdgeValue(final int root) {
        int value = root;
        if (Main.RANDOM.nextInt(3) > 0) {
            while (Main.RANDOM.nextInt(3) > 0) {
                value++;
            }
        } else {
            while (value > 1 && Main.RANDOM.nextInt(4) == 0) {
                value--;
            }
        }
        return value;
    }

    static String searchTask(final String search, final String start) {
        return String.format(
            "F\\\"uhren Sie eine \\emphasize{%s} auf diesem Graphen mit dem \\emphasize{Startknoten %s} aus. Geben Sie dazu die Knoten in der Reihenfolge an, in der sie durch die %s gefunden werden. Nehmen Sie an, dass der Algorithmus die Kanten in der alphabetischen Reihenfolge ihrer Zielknoten durchl\\\"auft.",
            search,
            start,
            search
        );
    }

    /**
     * @param num A non-negative number.
     * @return A String representation of this number. 0 is A, 1 is B, 26 is AA, 27 is AB, and so on.
     */
    static String toStringLabel(final int num) {
        int val = num;
        int rem = val % 26;
        String res = "" + (char)(65 + rem);
        val -= rem;
        while (val > 0) {
            val = val / 26;
            rem = val % 26;
            res = ((char)(65 + rem)) + res;
            val -= rem;
        }
        return res;
    }

    /**
     * @param numOfVertices The number of vertices in the returned graph.
     * @param undirected Should the graph be undirected?
     * @return A random graph with <code>numOfVertices</code> vertices labeled with Strings (each vertex has a unique
     *         label and there is a vertex with label A) and edges labeled with Integers.
     */
    private static Graph<String, Integer> createRandomGraph(final int numOfVertices, final boolean undirected) {
        if (numOfVertices < 0) {
            throw new IllegalArgumentException("Number of vertices must not be negative!");
        }
        final Graph<String, Integer> graph = new Graph<String, Integer>();
        final Grid<String> grid = new Grid<String>();
        if (numOfVertices == 0) {
            graph.setGrid(grid);
            return graph;
        }
        final Map<Vertex<String>, VertexGridPosition> positions =
            new LinkedHashMap<Vertex<String>, VertexGridPosition>();
        final Vertex<String> start = new Vertex<String>(Optional.of("A"));
        final GridCoordinates startPos = new GridCoordinates(0, 0);
        final boolean startDiagonal = Main.RANDOM.nextBoolean();
        GraphAlgorithms.addVertex(
            start,
            graph,
            new Pair<GridCoordinates, Boolean>(startPos, startDiagonal),
            grid,
            positions
        );
        final List<Vertex<String>> verticesWithFreeNeighbors = new ArrayList<Vertex<String>>();
        verticesWithFreeNeighbors.add(start);
        for (int letter = 1; letter < numOfVertices; letter++) {
            final Vertex<String> nextVertex =
                verticesWithFreeNeighbors.get(Main.RANDOM.nextInt(verticesWithFreeNeighbors.size()));
            final VertexGridPosition nextPos = positions.get(nextVertex);
            final Pair<GridCoordinates, Boolean> toAddPos = nextPos.randomFreePosition();
            final Vertex<String> toAddVertex =
                new Vertex<String>(Optional.of(GraphAlgorithms.toStringLabel(letter)));
            final VertexGridPosition gridPos = GraphAlgorithms.addVertex(toAddVertex, graph, toAddPos, grid, positions);
            final int value = GraphAlgorithms.randomEdgeValue(GraphAlgorithms.DEFAULT_EDGE_ROOT);
            graph.addEdge(nextVertex, Optional.of(value), toAddVertex);
            if (undirected) {
                graph.addEdge(toAddVertex, Optional.of(value), nextVertex);
            }
            final List<Pair<GridCoordinates, Boolean>> existing = gridPos.getExistingPositions();
            final List<Pair<Vertex<String>, Vertex<String>>> freeVertexPairs =
                new ArrayList<Pair<Vertex<String>, Vertex<String>>>();
            for (final Pair<GridCoordinates, Boolean> other : existing) {
                final Vertex<String> otherVertex = grid.get(other.x);
                if (otherVertex.equals(nextVertex)) {
                    if (!undirected) {
                        freeVertexPairs.add(new Pair<Vertex<String>, Vertex<String>>(toAddVertex, otherVertex));
                    }
                } else {
                    freeVertexPairs.add(new Pair<Vertex<String>, Vertex<String>>(toAddVertex, otherVertex));
                    if (!undirected) {
                        freeVertexPairs.add(new Pair<Vertex<String>, Vertex<String>>(otherVertex, toAddVertex));
                    }
                }
            }
            for (
                int numEdges = GraphAlgorithms.randomNumOfEdges(freeVertexPairs.size());
                numEdges > 0;
                numEdges--
            ) {
                final int pairIndex = Main.RANDOM.nextInt(freeVertexPairs.size());
                final Pair<Vertex<String>, Vertex<String>> pair = freeVertexPairs.remove(pairIndex);
                final int nextValue = GraphAlgorithms.randomEdgeValue(GraphAlgorithms.DEFAULT_EDGE_ROOT);
                graph.addEdge(pair.x, Optional.of(nextValue), pair.y);
                if (undirected) {
                    graph.addEdge(pair.y, Optional.of(nextValue), pair.x);
                }
            }
            for (final Pair<GridCoordinates, Boolean> neighborPos : existing) {
                final Vertex<String> neighborVertex = grid.get(neighborPos.x);
                if (!positions.get(neighborVertex).hasFreePosition()) {
                    verticesWithFreeNeighbors.remove(neighborVertex);
                }
            }
            if (gridPos.hasFreePosition()) {
                verticesWithFreeNeighbors.add(toAddVertex);
            }
        }
        // adjust grid (non-negative coordinates, sum of coordinates even -> has diagonals
        int minX = 0;
        int minY = 0;
        for (final GridCoordinates pair : grid.keySet()) {
            minX = Math.min(minX, pair.x);
            minY = Math.min(minY, pair.y);
        }
        if (
            (startDiagonal && (startPos.x - minX + startPos.y - minY) % 2 == 1)
            || (!startDiagonal && (startPos.x - minX + startPos.y - minY) % 2 == 0)
        ) {
            minX--;
        }
        final Grid<String> newGrid = new Grid<String>();
        for (final Entry<GridCoordinates, Vertex<String>> entry : grid.entrySet()) {
            final GridCoordinates key = entry.getKey();
            newGrid.put(new GridCoordinates(key.x - minX, key.y - minY), entry.getValue());
        }
        graph.setGrid(newGrid);
        return graph;
    }

    private static Pair<Graph<String, Integer>, Vertex<String>> generateGraph(final Parameters options) {
        final String alg = options.get(Flag.ALGORITHM);
        final int numOfVertices;
        if (options.containsKey(Flag.LENGTH)) {
            numOfVertices = Integer.parseInt(options.get(Flag.LENGTH));
        } else {
            numOfVertices = Main.RANDOM.nextInt(16) + 5;
        }
        final Graph<String, Integer> graph =
            GraphAlgorithms.createRandomGraph(
                numOfVertices,
                GraphAlgorithms.UNDIRECTED_GRAPH_ALGORITHMS.contains(alg)
            );
        return new Pair<Graph<String, Integer>, Vertex<String>>(
            graph,
            GraphAlgorithms.generateStartVertex(graph, options)
        );
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
    private static int[][] generateGridGraph(final Parameters options) {
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

    private static Vertex<String> generateStartVertex(final Graph<String, Integer> graph, final Parameters options) {
        final Set<Vertex<String>> vertices = graph.getVerticesWithLabel("A");
        if (!vertices.isEmpty()) {
            return vertices.iterator().next();
        }
        return graph.getVertices().iterator().next();
    }

    private static Set<String> initGraphAlgorithmsWithStartVertex() {
        final Set<String> res = new LinkedHashSet<String>();
        if (Algorithm.DIJKSTRA.enabled) {
            res.add(Algorithm.DIJKSTRA.name);
        }
        if (Algorithm.PRIM.enabled) {
            res.add(Algorithm.PRIM.name);
        }
        return res;
    }

    /**
     * @return The set of (in student mode only enabled) graph algorithms working on undirected graphs.
     */
    private static Set<String> initUndirectedGraphAlgorithms() {
        final Set<String> res = new LinkedHashSet<String>();
        if (Algorithm.PRIM.enabled) {
            res.add(Algorithm.PRIM.name);
        }
        return res;
    }

    private static Pair<Graph<String, Integer>, Vertex<String>> parseGraph(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        final Graph<String, Integer> graph = Graph.create(reader, new StringLabelParser(), new IntLabelParser());
        return new Pair<Graph<String, Integer>, Vertex<String>>(
            graph,
            GraphAlgorithms.parseOrGenerateStartVertex(graph, options)
        );
    }

    private static int[][] parseGridGraph(final BufferedReader reader, final Parameters options) throws IOException {
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

    private static Vertex<String> parseOrGenerateStartVertex(
        final Graph<String, Integer> graph,
        final Parameters options
    ) throws IOException {
        Vertex<String> vertex = null;
        if (options.containsKey(Flag.OPERATIONS)) {
            final String operations = options.get(Flag.OPERATIONS);
            try (
                BufferedReader operationsReader =
                    new BufferedReader(
                        options.containsKey(Flag.INPUT) ? new StringReader(operations) : new FileReader(operations)
                    )
            ) {
                final Set<Vertex<String>> vertices = graph.getVerticesWithLabel(operationsReader.readLine().trim());
                if (!vertices.isEmpty()) {
                    vertex = vertices.iterator().next();
                }
            }
        } else if (options.containsAtLeastOne(Flag.INPUT, Flag.SOURCE)) {
            final String label =
                new ParserAndGenerator<String>(
                    (reader, params) -> {
                        final String line = reader.readLine();
                        if (line.startsWith("!")) {
                            return line.substring(1);
                        }
                        return null;
                    },
                    (params) -> null
                ).getResult(options);
            if (label != null) {
                final Set<Vertex<String>> vertices = graph.getVerticesWithLabel(label);
                if (!vertices.isEmpty()) {
                    vertex = vertices.iterator().next();
                }
            }
        } else if (
            !options.containsAtLeastOne(Flag.SOURCE, Flag.INPUT)
            && GraphAlgorithms.GRAPH_ALGORITHMS_WITH_START_VERTEX.contains(options.get(Flag.ALGORITHM))
        ) {
            return GraphAlgorithms.generateStartVertex(graph, options);
        }
        return vertex;
    }

    /**
     * @param max The maximum number of additional edges.
     * @return A random number between 0 and max, most likely to be max / 2.
     */
    private static int randomNumOfEdges(final int max) {
        int res = max / 2;
        if (Main.RANDOM.nextBoolean()) {
            while (res < max && Main.RANDOM.nextInt(3) == 0) {
                res++;
            }
        } else {
            while (res > 0 && Main.RANDOM.nextInt(3) == 0) {
                res--;
            }
        }
        return res;
    }

}
