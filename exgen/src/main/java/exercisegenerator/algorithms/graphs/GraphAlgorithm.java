package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;
import exercisegenerator.structures.graphs.layout.*;

public interface GraphAlgorithm<S> extends AlgorithmImplementation<GraphProblem, S> {

    static final int DEFAULT_EDGE_ROOT = 3;

//    static VertexGridPosition addVertexWithGridLayout(
//        final Vertex<String> vertex,
//        final Graph<String, ?> graph,
//        final Pair<GridCoordinates, Boolean> pos,
//        final Map<GridCoordinates, Vertex<String>> grid,
//        final Map<Vertex<String>, VertexGridPosition> positions
//    ) {
//        graph.addVertex(vertex);
//        grid.put(pos.x, vertex);
//        final int x = pos.x.x;
//        final int y = pos.x.y;
//        final VertexGridPosition gridPos = new VertexGridPosition(pos.x.x, pos.x.y, pos.y);
//        positions.put(vertex, gridPos);
//        GridCoordinates nextPos = new GridCoordinates(x, y - 1);
//        Vertex<String> nextVertex = grid.get(nextPos);
//        if (nextVertex != null) {
//            final VertexGridPosition nextGridPos = positions.get(nextVertex);
//            gridPos.north = nextGridPos;
//            nextGridPos.south = gridPos;
//        }
//        nextPos = new GridCoordinates(x + 1, y);
//        nextVertex = grid.get(nextPos);
//        if (nextVertex != null) {
//            final VertexGridPosition nextGridPos = positions.get(nextVertex);
//            gridPos.east = nextGridPos;
//            nextGridPos.west = gridPos;
//        }
//        nextPos = new GridCoordinates(x, y + 1);
//        nextVertex = grid.get(nextPos);
//        if (nextVertex != null) {
//            final VertexGridPosition nextGridPos = positions.get(nextVertex);
//            gridPos.south = nextGridPos;
//            nextGridPos.north = gridPos;
//        }
//        nextPos = new GridCoordinates(x - 1, y);
//        nextVertex = grid.get(nextPos);
//        if (nextVertex != null) {
//            final VertexGridPosition nextGridPos = positions.get(nextVertex);
//            gridPos.west = nextGridPos;
//            nextGridPos.east = gridPos;
//        }
//        if (pos.y) {
//            nextPos = new GridCoordinates(x + 1, y - 1);
//            nextVertex = grid.get(nextPos);
//            if (nextVertex != null) {
//                final VertexGridPosition nextGridPos = positions.get(nextVertex);
//                gridPos.northeast = nextGridPos;
//                nextGridPos.southwest = gridPos;
//            }
//            nextPos = new GridCoordinates(x + 1, y + 1);
//            nextVertex = grid.get(nextPos);
//            if (nextVertex != null) {
//                final VertexGridPosition nextGridPos = positions.get(nextVertex);
//                gridPos.southeast = nextGridPos;
//                nextGridPos.northwest = gridPos;
//            }
//            nextPos = new GridCoordinates(x - 1, y + 1);
//            nextVertex = grid.get(nextPos);
//            if (nextVertex != null) {
//                final VertexGridPosition nextGridPos = positions.get(nextVertex);
//                gridPos.southwest = nextGridPos;
//                nextGridPos.northeast = gridPos;
//            }
//            nextPos = new GridCoordinates(x - 1, y - 1);
//            nextVertex = grid.get(nextPos);
//            if (nextVertex != null) {
//                final VertexGridPosition nextGridPos = positions.get(nextVertex);
//                gridPos.northwest = nextGridPos;
//                nextGridPos.southeast = gridPos;
//            }
//        }
//        return gridPos;
//    }

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

    static double parseDistanceFactor(final Parameters<Flag> options) {
        return Double.parseDouble(options.getOrDefault(Flag.DEGREE, "1.0"));
    }

    static void printGraphExercise(
        final GraphWithLayout<String, Integer, Integer> graphWithLayout,
        final String task,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Betrachten Sie den folgenden Graphen:\\\\");
        Main.newLine(writer);
        LaTeXUtils.printAdjustboxBeginning(writer, "max width=\\columnwidth", "center");
        graphWithLayout.graph().printTikZ(graphWithLayout.layout(), writer);
        LaTeXUtils.printAdjustboxEnd(writer);
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

    static GraphWithLayout<String, Integer, Integer> stretch(
        final GraphWithLayout<String, Integer, Integer> graphWithLayout,
        final double factor
    ) {
        if (factor == 1.0) {
            return graphWithLayout;
        }
        return new GraphWithLayout<String, Integer, Integer>(
            graphWithLayout.graph(),
            ((GridGraphLayout<String, Integer>)graphWithLayout.layout()).stretch(factor)
        );
    }

    static <V, E> GridGraphLayout<V, E> stretch(
        final GridGraphLayout<V, E> graphLayout,
        final double factor
    ) {
        if (factor == 1.0) {
            return graphLayout;
        }
        return graphLayout.stretch(factor);
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

    private static GraphWithLayout<String, Integer, Integer> createRandomGraphWithGridLayout(
        final int numOfVertices,
        final boolean directed
    ) {
        if (numOfVertices < 0) {
            throw new IllegalArgumentException("Number of vertices must not be negative!");
        }
        final Graph<String, Integer> graph = new Graph<String, Integer>();
        final GridGraphLayout.GridGraphLayoutBuilder<String, Integer> layoutBuilder =
            GridGraphLayout.<String, Integer>builder().setDirected(directed);
        if (numOfVertices == 0) {
            return new GraphWithLayout<String, Integer, Integer>(graph, layoutBuilder.build());
        }
        final Vertex<String> start = new Vertex<String>(Optional.of("A"));
        graph.addVertex(start);
        layoutBuilder.addVertex(start, new Coordinates2D<Integer>(0, 0));
        final List<Vertex<String>> verticesWithFreeNeighbors = new ArrayList<Vertex<String>>();
        verticesWithFreeNeighbors.add(start);
        for (int letter = 1; letter < numOfVertices; letter++) {
            final Vertex<String> nextVertex =
                verticesWithFreeNeighbors.get(Main.RANDOM.nextInt(verticesWithFreeNeighbors.size()));
            final Vertex<String> toAddVertex =
                new Vertex<String>(Optional.of(GraphAlgorithm.toLetterLabel(letter)));
            graph.addVertex(toAddVertex);
            final List<Coordinates2D<Integer>> free = layoutBuilder.getFreePositions(nextVertex);
            final Coordinates2D<Integer> toAddPos = free.get(Main.RANDOM.nextInt(free.size()));
            layoutBuilder.addVertex(toAddVertex, toAddPos);
            final int value = GraphAlgorithm.randomEdgeValue(GraphAlgorithm.DEFAULT_EDGE_ROOT);
            graph.addEdge(nextVertex, Optional.of(value), toAddVertex);
            if (!directed) {
                graph.addEdge(toAddVertex, Optional.of(value), nextVertex);
            }
            final List<Pair<Vertex<String>, Vertex<String>>> freeVertexPairs =
                new ArrayList<Pair<Vertex<String>, Vertex<String>>>();
            final List<Vertex<String>> neighbours = layoutBuilder.getSurroundingVertices(toAddPos);
            for (final Vertex<String> neighbour : neighbours) {
                if (neighbour.equals(nextVertex)) {
                    if (directed) {
                        freeVertexPairs.add(new Pair<Vertex<String>, Vertex<String>>(toAddVertex, neighbour));
                    }
                } else {
                    freeVertexPairs.add(new Pair<Vertex<String>, Vertex<String>>(toAddVertex, neighbour));
                    if (directed) {
                        freeVertexPairs.add(new Pair<Vertex<String>, Vertex<String>>(neighbour, toAddVertex));
                    }
                }
                if (layoutBuilder.getFreePositions(neighbour).isEmpty()) {
                    verticesWithFreeNeighbors.remove(neighbour);
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
                if (!directed) {
                    graph.addEdge(pair.y, Optional.of(nextValue), pair.x);
                }
            }
            if (!layoutBuilder.getFreePositions(toAddVertex).isEmpty()) {
                verticesWithFreeNeighbors.add(toAddVertex);
            }
        }
        return new GraphWithLayout<String, Integer, Integer>(graph, layoutBuilder.build());
    }

    private static GraphProblem generateGraphProblem(final Parameters<Flag> options) {
        final String algorithmName = options.get(Flag.ALGORITHM);
        final int numOfVertices = AlgorithmImplementation.parseOrGenerateLength(5, 20, options);
        final GraphWithLayout<String, Integer, Integer> graphWithLayout =
            GraphAlgorithm.createRandomGraphWithGridLayout(
                numOfVertices,
                !GraphAlgorithm.isUndirectedGraphAlgorithm(algorithmName)
            );
        return new GraphProblem(
            graphWithLayout,
            GraphAlgorithm.generateStartVertex(graphWithLayout.graph(), options),
            StringVertexComparator.INSTANCE
        );
    }

    private static Vertex<String> generateStartVertex(
        final Graph<String, Integer> graph,
        final Parameters<Flag> options
    ) {
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
        final Parameters<Flag> options
    ) throws IOException {
        final GraphWithLayout<String, Integer, Integer> graphWithLayout =
            Graph.create(reader, new StringLabelParser(), new IntLabelParser());
        return new GraphProblem(
            graphWithLayout,
            GraphAlgorithm.parseOrGenerateStartVertex(graphWithLayout.graph(), options),
            StringVertexComparator.INSTANCE
        );
    }

    private static Vertex<String> parseOrGenerateStartVertex(
        final Graph<String, Integer> graph,
        final Parameters<Flag> options
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
    default public GraphProblem parseOrGenerateProblem(final Parameters<Flag> options) throws IOException {
        return new ParserAndGenerator<GraphProblem>(
            GraphAlgorithm::parseGraphProblem,
            GraphAlgorithm::generateGraphProblem
        ).getResult(options);
    }

}
