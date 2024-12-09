package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;
import java.util.Map.*;
import java.util.stream.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

public interface GraphAlgorithm<S> extends AlgorithmImplementation<GraphProblem, S> {

    static final int DEFAULT_EDGE_ROOT = 3;

    static VertexGridPosition addVertexWithGridLayout(
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

    static void printGraphExercise(
        final Graph<String, Integer> graph,
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

    static String toLetterLabel(final int num) {
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
        GraphAlgorithm.addVertexWithGridLayout(
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
                new Vertex<String>(Optional.of(GraphAlgorithm.toLetterLabel(letter)));
            final VertexGridPosition gridPos = GraphAlgorithm.addVertexWithGridLayout(toAddVertex, graph, toAddPos, grid, positions);
            final int value = GraphAlgorithm.randomEdgeValue(GraphAlgorithm.DEFAULT_EDGE_ROOT);
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
                int numEdges = GraphAlgorithm.randomNumOfEdges(freeVertexPairs.size());
                numEdges > 0;
                numEdges--
            ) {
                final int pairIndex = Main.RANDOM.nextInt(freeVertexPairs.size());
                final Pair<Vertex<String>, Vertex<String>> pair = freeVertexPairs.remove(pairIndex);
                final int nextValue = GraphAlgorithm.randomEdgeValue(GraphAlgorithm.DEFAULT_EDGE_ROOT);
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

    private static GraphProblem generateGraphProblem(final Parameters options) {
        final String algorithmName = options.get(Flag.ALGORITHM);
        final int numOfVertices;
        if (options.containsKey(Flag.LENGTH)) {
            numOfVertices = Integer.parseInt(options.get(Flag.LENGTH));
        } else {
            numOfVertices = Main.RANDOM.nextInt(16) + 5;
        }
        final Graph<String, Integer> graph =
            GraphAlgorithm.createRandomGraph(
                numOfVertices,
                GraphAlgorithm.isUndirectedGraphAlgorithm(algorithmName)
            );
        return new GraphProblem(
            graph,
            GraphAlgorithm.generateStartVertex(graph, options),
            StringVertexComparator.INSTANCE
        );
    }

    private static Vertex<String> generateStartVertex(final Graph<String, Integer> graph, final Parameters options) {
        final Set<Vertex<String>> vertices = graph.getVerticesWithLabel("A");
        if (!vertices.isEmpty()) {
            return vertices.iterator().next();
        }
        return graph.getVertices().iterator().next();
    }

    private static boolean isGraphAlgorithmWithStartVertex(final String algorithmName) {
        return Stream.of(
            Algorithm.DIJKSTRA,
            Algorithm.PRIM
        ).filter(algorithm -> algorithm.enabled).map(algorithm -> algorithm.name).toList().contains(algorithmName);
    }

    private static boolean isUndirectedGraphAlgorithm(final String algorithmName) {
        return Stream.of(
            Algorithm.PRIM
        ).filter(algorithm -> algorithm.enabled).map(algorithm -> algorithm.name).toList().contains(algorithmName);
    }

    private static GraphProblem parseGraphProblem(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        final Graph<String, Integer> graph = Graph.create(reader, new StringLabelParser(), new IntLabelParser());
        return new GraphProblem(
            graph,
            GraphAlgorithm.parseOrGenerateStartVertex(graph, options),
            StringVertexComparator.INSTANCE
        );
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
            && GraphAlgorithm.isGraphAlgorithmWithStartVertex(options.get(Flag.ALGORITHM))
        ) {
            return GraphAlgorithm.generateStartVertex(graph, options);
        }
        return vertex;
    }

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

    @Override
    default public GraphProblem parseOrGenerateProblem(final Parameters options) throws IOException {
        return new ParserAndGenerator<GraphProblem>(
            GraphAlgorithm::parseGraphProblem,
            GraphAlgorithm::generateGraphProblem
        ).getResult(options);
    }

}
