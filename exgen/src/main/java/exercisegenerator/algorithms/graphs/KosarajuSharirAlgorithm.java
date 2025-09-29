package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.*;

public class KosarajuSharirAlgorithm implements GraphAlgorithm<KosarajuSharirResult> {

    public static final KosarajuSharirAlgorithm INSTANCE = new KosarajuSharirAlgorithm();

    private static void assign(
        final Vertex<String> vertex,
        final String root,
        final Graph<String, Integer> graph,
        final Map<String, String> assignment
    ) {
        final String vertexLabel = vertex.label().get();
        if (!assignment.containsKey(vertexLabel)) {
            assignment.put(vertexLabel, root);
            for (final Vertex<String> neighbour : graph.getAdjacentVertices(vertex)) {
                KosarajuSharirAlgorithm.assign(neighbour, root, graph, assignment);
            }
        }
    }

    private static List<ItemWithTikZInformation<String>> toTikZList(final Collection<String> collection) {
        return collection.stream().map(text -> new ItemWithTikZInformation<String>(Optional.of(text))).toList();
    }

    private static void visit(
        final Vertex<String> vertex,
        final Graph<String, Integer> graph,
        final Set<String> visited,
        final Deque<String> stack
    ) {
        final String vertexLabel = vertex.label().get();
        if (!visited.contains(vertexLabel)) {
            visited.add(vertexLabel);
            for (final Vertex<String> neighbour : graph.getAdjacentVertices(vertex)) {
                KosarajuSharirAlgorithm.visit(neighbour, graph, visited, stack);
            }
            stack.push(vertexLabel);
        }
    }

    private KosarajuSharirAlgorithm() {}

    @Override
    public KosarajuSharirResult apply(final GraphProblem problem) {
        final Graph<String, Integer> graph = problem.graphWithLayout().graph();
        final Set<Vertex<String>> vertices = graph.getVertices();
        final Deque<String> stack = new ArrayDeque<String>(vertices.size());
        final Set<String> visited = new LinkedHashSet<String>();
        for (final Vertex<String> vertex : vertices) {
            KosarajuSharirAlgorithm.visit(vertex, graph, visited, stack);
        }
        final List<String> stackResult = new ArrayList<String>(stack);
        final Map<String, String> assignment = new LinkedHashMap<String, String>();
        final Graph<String, Integer> transposed = graph.transpose();
        while (!stack.isEmpty()) {
            final String vertexLabel = stack.pop();
            final Vertex<String> vertex = transposed.getVerticesWithLabel(vertexLabel).iterator().next();
            KosarajuSharirAlgorithm.assign(vertex, vertexLabel, transposed, assignment);
        }
        return new KosarajuSharirResult(stackResult, assignment);
    }

    @Override
    public String commandPrefix() {
        return "KosarajuSharir";
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<GraphProblem> problems,
        final List<KosarajuSharirResult> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Wenden Sie den \\emphasize{Kosaraju-Sharir-Algorithmus} an, um die starken ");
        writer.write("Zusammenhangskomponenten der folgenden Graphen zu finden. Geben Sie dazu jeweils den Stack ");
        writer.write("nach Abschluss der ersten Phase und die Zuordnung der Knoten zu Repr\\\"asentanten der ");
        writer.write("Zusammenhangskomponenten nach der zweiten Phase an.\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeSingleProblemInstance(
        final GraphProblem problem,
        final KosarajuSharirResult solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Wenden Sie den \\emphasize{Kosaraju-Sharir-Algorithmus} an, um die starken ");
        writer.write("Zusammenhangskomponenten des folgenden Graphen zu finden. Geben Sie dazu den Stack ");
        writer.write("nach Abschluss der ersten Phase und die Zuordnung der Knoten zu Repr\\\"asentanten der ");
        writer.write("Zusammenhangskomponenten nach der zweiten Phase an.\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printSolutionInstance(
        final GraphProblem problem,
        final KosarajuSharirResult solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        final int contentLength = solution.computeContentLength();
        writer.write("Stack:\\\\");
        Main.newLine(writer);
        LaTeXUtils.printTikzBeginning(TikZStyle.GRID, writer);
        LaTeXUtils.printListAndReturnLowestLeftmostNodesName(
            KosarajuSharirAlgorithm.toTikZList(solution.stack()),
            Optional.empty(),
            contentLength,
            writer
        );
        LaTeXUtils.printTikzEnd(writer);
        Main.newLine(writer);
        writer.write("Zuordnung:\\\\");
        Main.newLine(writer);
        LaTeXUtils.printTikzBeginning(TikZStyle.GRID, writer);
        final List<String> vertices = solution.assignment().keySet().stream().sorted().toList();
        final String left = LaTeXUtils.printListAndReturnLowestLeftmostNodesName(
            KosarajuSharirAlgorithm.toTikZList(vertices),
            Optional.empty(),
            contentLength,
            writer
        );
        LaTeXUtils.printListAndReturnLowestLeftmostNodesName(
            KosarajuSharirAlgorithm.toTikZList(vertices.stream().map(vertex -> solution.assignment().get(vertex)).toList()),
            Optional.of(left),
            contentLength,
            writer
        );
        LaTeXUtils.printTikzEnd(writer);
    }

    @Override
    public void printSolutionSpace(
        final GraphProblem problem,
        final KosarajuSharirResult solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        final int contentLength = solution.computeContentLength();
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), options, writer);
        writer.write("Stack:\\\\");
        Main.newLine(writer);
        LaTeXUtils.printTikzBeginning(TikZStyle.GRID, writer);
        LaTeXUtils.printEmptyArrayAndReturnLeftmostNodesName(
            solution.stack().size(),
            Optional.empty(),
            contentLength,
            writer
        );
        LaTeXUtils.printTikzEnd(writer);
        Main.newLine(writer);
        writer.write("Zuordnung:\\\\");
        Main.newLine(writer);
        final List<String> vertices = solution.assignment().keySet().stream().sorted().toList();
        LaTeXUtils.printTikzBeginning(TikZStyle.GRID, writer);
        final String left = LaTeXUtils.printListAndReturnLowestLeftmostNodesName(
            KosarajuSharirAlgorithm.toTikZList(vertices),
            Optional.empty(),
            contentLength,
            writer
        );
        LaTeXUtils.printEmptyArrayAndReturnLeftmostNodesName(vertices.size(), Optional.of(left), contentLength, writer);
        LaTeXUtils.printTikzEnd(writer);
        LaTeXUtils.printSolutionSpaceEnd(Optional.empty(), options, writer);
    }

}
