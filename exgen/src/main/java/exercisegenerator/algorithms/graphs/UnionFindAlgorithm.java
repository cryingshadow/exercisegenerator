package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.*;
import exercisegenerator.structures.graphs.layout.*;
import exercisegenerator.structures.graphs.layout.GridGraphLayout.*;

public class UnionFindAlgorithm implements AlgorithmImplementation<UnionFindProblem, UnionFind<Integer>>{

    public static final UnionFindAlgorithm INSTANCE = new UnionFindAlgorithm();

    private static int computeTreeWithIndentation(
        final Graph<Integer, Integer> graph,
        final Map<Integer, Integer> levels,
        final Map<Integer, Integer> indentations,
        final Vertex<Integer> current,
        final int level,
        final int indentation
    ) {
        final Integer element = current.label().get();
        levels.put(element, level);
        indentations.put(element, indentation);
        int width = 0;
        for (final Vertex<Integer> child : graph.getAdjacentVertices(current)) {
            if (child.equals(current)) {
                continue;
            }
            width +=
                UnionFindAlgorithm.computeTreeWithIndentation(
                    graph,
                    levels,
                    indentations,
                    child,
                    level + 1,
                    indentation + width
                );
        }
        return width == 0 ? 1 : width;
    }

    private static int parseOrGenerateLength(final Parameters<Flag> options) {
        return options.getAsIntOrDefault(Flag.LENGTH, 10);
    }

    private static GridGraphLayout<Integer, Integer> toGridLayout(
        final UnionFind<Integer> unionFind,
        final Graph<Integer, Integer> graph
    ) {
        if (unionFind.isEmpty()) {
            return GridGraphLayout.<Integer, Integer>builder().setDirected(true).build();
        }
        final Graph<Integer, Integer> transposed = graph.transpose();
        final Map<Integer, Integer> levels = new LinkedHashMap<Integer, Integer>();
        final Map<Integer, Integer> indentations = new LinkedHashMap<Integer, Integer>();
        int indentation = 0;
        for (final Integer root : unionFind.keySet().stream().filter(e -> unionFind.get(e).equals(e)).toList()) {
            indentation +=
                UnionFindAlgorithm.computeTreeWithIndentation(
                    transposed,
                    levels,
                    indentations,
                    transposed.getVertexWithUniqueLabel(root),
                    0,
                    indentation
                );
        }
        final int maxLevel = levels.values().stream().reduce(Math::max).get();
        for (final Map.Entry<Integer, Integer> entry : levels.entrySet()) {
            entry.setValue(maxLevel - entry.getValue());
        }
        final GridGraphLayoutBuilder<Integer, Integer> builder =
            GridGraphLayout.<Integer, Integer>builder().setDirected(true);
        for (final Vertex<Integer> vertex : graph.getVertices()) {
            final Integer element = vertex.label().get();
            builder.addVertex(vertex, new Coordinates2D<Integer>(indentations.get(element), levels.get(element)));
        }
        return builder.build();
    }

    private UnionFindAlgorithm() {}

    @Override
    public UnionFind<Integer> apply(final UnionFindProblem problem) {
        final UnionFind<Integer> result = new UnionFind<Integer>(problem.initialState());
        for (final UnionFindOperation<Integer> operation : problem.operations()) {
            operation.apply(result);
        }
        return result;
    }

    @Override
    public String commandPrefix() {
        return "UnionFind";
    }

    @Override
    public UnionFindProblem generateProblem(final Parameters<Flag> options) {
        final int length = UnionFindAlgorithm.parseOrGenerateLength(options);
        final UnionFind<Integer> state = new UnionFind<Integer>(IntStream.range(0, length).boxed().toList());
        for (int i = 0; i < 3; i++) {
            final int firstElement = Main.RANDOM.nextInt(length);
            int secondElement = Main.RANDOM.nextInt(length - 1);
            if (secondElement >= firstElement) {
                secondElement++;
            }
            state.union(firstElement, secondElement);
        }
        final int numOfOperations = options.getAsIntOrDefault(Flag.DEGREE, 3);
        final List<UnionFindOperation<Integer>> operations = new LinkedList<UnionFindOperation<Integer>>();
        for (int i = 0; i < numOfOperations; i++) {
            if (Main.RANDOM.nextInt(5) == 0) {
                operations.add(new FindOperation<Integer>(Main.RANDOM.nextInt(length)));
            } else {
                final int firstElement = Main.RANDOM.nextInt(length);
                int secondElement = Main.RANDOM.nextInt(length - 1);
                if (secondElement >= firstElement) {
                    secondElement++;
                }
                operations.add(new UnionOperation<Integer>(firstElement, secondElement));
            }
        }
        return new UnionFindProblem(state, operations);
    }

    @Override
    public String[] generateTestParameters() {
        return new String[] {"-l", "10"};
    }

    @Override
    public List<UnionFindProblem> parseProblems(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        final String elementsLine = reader.readLine();
        final String constructionLine = reader.readLine();
        final String operationsLine = reader.readLine();
        final UnionFind<Integer> state =
            new UnionFind<Integer>(Arrays.stream(elementsLine.split(";")).map(Integer::parseInt).toList());
        if (!constructionLine.isBlank()) {
            for (final String operationString : constructionLine.split(";")) {
                final String[] parts = operationString.split(",");
                if (parts.length == 1) {
                    state.find(Integer.parseInt(parts[0]));
                } else if (parts.length == 2) {
                    state.union(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                } else {
                    throw new IllegalArgumentException(
                        "Operations must be encoded as ;-separated list of integers "
                        + "or pairs of integers (separated by ,)."
                    );
                }
            }
        }
        final List<UnionFindOperation<Integer>> operations = new LinkedList<UnionFindOperation<Integer>>();
        for (final String operationString : operationsLine.split(";")) {
            final String[] parts = operationString.split(",");
            if (parts.length == 1) {
                operations.add(new FindOperation<Integer>(Integer.parseInt(parts[0])));
            } else if (parts.length == 2) {
                operations.add(new UnionOperation<Integer>(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
            } else {
                throw new IllegalArgumentException(
                    "Operations must be encoded as ;-separated list of integers or pairs of integers (separated by ,)."
                );
            }
        }
        return List.of(new UnionFindProblem(state, operations));
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<UnionFindProblem> problems,
        final List<UnionFind<Integer>> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Geben Sie jeweils den finalen Zustand der folgenden \\emphasize{Union-Find}-Datenstrukturen ");
        writer.write("an, nachdem jeweils alle darunter angegebenen Operationen ausgef\\\"uhrt wurden:\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeSingleProblemInstance(
        final UnionFindProblem problem,
        final UnionFind<Integer> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Geben Sie den finalen Zustand der folgenden \\emphasize{Union-Find}-Datenstruktur an, nachdem ");
        writer.write("alle darunter angegebenen Operationen ausgef\\\"uhrt wurden:\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printProblemInstance(
        final UnionFindProblem problem,
        final UnionFind<Integer> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printAdjustboxBeginning(writer);
        final Graph<Integer, Integer> graph = problem.initialState().toGraph();
        graph.printTikZ(UnionFindAlgorithm.toGridLayout(problem.initialState(), graph), writer);
        LaTeXUtils.printAdjustboxEnd(writer);
        Main.newLine(writer);
        writer.write("Operationen:\\\\");
        Main.newLine(writer);
        LaTeXUtils.printBeginning("enumerate", writer);
        for (final UnionFindOperation<Integer> operation : problem.operations()) {
            writer.write("\\item ");
            writer.write(operation.toString());
            Main.newLine(writer);
        }
        LaTeXUtils.printEnd("enumerate", writer);
    }

    @Override
    public void printSolutionInstance(
        final UnionFindProblem problem,
        final UnionFind<Integer> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printAdjustboxBeginning(writer);
        final Graph<Integer, Integer> graph = solution.toGraph();
        graph.printTikZ(UnionFindAlgorithm.toGridLayout(solution, graph), writer);
        LaTeXUtils.printAdjustboxEnd(writer);
    }

    @Override
    public void printSolutionSpace(
        final UnionFindProblem problem,
        final UnionFind<Integer> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

}
