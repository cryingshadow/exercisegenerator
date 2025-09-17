package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.*;
import exercisegenerator.structures.graphs.layout.*;
import exercisegenerator.structures.graphs.petrinets.*;

public class PetriNetCoverabilityAlgorithm extends PetriNetAlgorithm<CoverabilityGraph> {

    public static final PetriNetCoverabilityAlgorithm INSTANCE = new PetriNetCoverabilityAlgorithm();

    private PetriNetCoverabilityAlgorithm() {}

    @Override
    public CoverabilityGraph apply(final PetriNetInput input) {
        final PetriNet net = new PetriNet(input);
        final PetriMarking firstTokens = PetriMarking.create(input.tokens());
        final CoverabilityGraph result = new CoverabilityGraph(new Vertex<PetriMarking>(firstTokens));
        final List<PetriMarking> used = new LinkedList<PetriMarking>();
        used.add(firstTokens);
        final Queue<PetriMarking> queue = new LinkedList<PetriMarking>();
        queue.offer(firstTokens);
        while (!queue.isEmpty()) {
            final PetriMarking tokens = queue.poll();
            for (final PetriTransition transition : net.activeTransitions(tokens)) {
                final PetriMarking nextTokens = net.fireTransition(tokens, transition);
                final PetriMarking omegaTokens = this.addOmegas(tokens, transition, nextTokens, used, result);
                if (!used.contains(omegaTokens)) {
                    result.addVertex(new Vertex<PetriMarking>(omegaTokens));
                    used.add(omegaTokens);
                    queue.offer(omegaTokens);
                }
                final Vertex<PetriMarking> from = result.getVerticesWithLabel(tokens).iterator().next();
                final Vertex<PetriMarking> to = result.getVerticesWithLabel(omegaTokens).iterator().next();
                result.addEdge(from, Optional.of(transition.label()), to);
            }
        }
        return result;
    }

    @Override
    public String commandPrefix() {
        return "Coverability";
    }

    @Override
    public String[] generateTestParameters() {
        return new String[] {};
    }

    @Override
    public void printAfterSingleProblemInstance(
        final PetriNetInput problem,
        final CoverabilityGraph solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("Geben Sie einen \\emphasize{Abdeckungsgraphen} zu $N$ an.");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<PetriNetInput> problems,
        final List<CoverabilityGraph> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Geben Sie jeweils einen \\emphasize{Abdeckungsgraphen} zu den folgenden ");
        writer.write("\\emphasize{Petrinetzen} an.\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printProblemInstance(
        final PetriNetInput problem,
        final CoverabilityGraph solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printAdjustboxBeginning(writer);
        new PetriNet(problem).toTikz(PetriMarking.create(problem.tokens()), writer);
        LaTeXUtils.printAdjustboxEnd(writer);
    }

    @Override
    public void printSolutionInstance(
        final PetriNetInput problem,
        final CoverabilityGraph solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printAdjustboxBeginning(writer);
        solution.printTikZ(
            new ForceGraphLayout<PetriMarking, String>(solution, TikZStyle.COVERABILITY_GRAPH, 4, 1, 12, 12),
            writer
        );
        LaTeXUtils.printAdjustboxEnd(writer);
    }

    @Override
    public void printSolutionSpace(
        final PetriNetInput problem,
        final CoverabilityGraph solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

    private PetriMarking addOmegas(
        final PetriMarking tokens,
        final PetriTransition transition,
        final PetriMarking nextTokens,
        final List<PetriMarking> used,
        final CoverabilityGraph graph
    ) {
        final PetriMarking result = PetriMarking.create(nextTokens);
        for (final PetriMarking existingTokens : used) {
            if (result.strictlyCovers(existingTokens) && this.isReachable(existingTokens, tokens, graph)) {
                for (int i = 0; i < existingTokens.size(); i++) {
                    final Optional<Integer> resultValue = result.get(i);
                    if (resultValue.isPresent() && resultValue.get() > existingTokens.get(i).get()) {
                        result.set(i, Optional.empty());
                    }
                }
            }
        }
        return result;
    }

    private boolean isReachable(
        final PetriMarking from,
        final PetriMarking to,
        final CoverabilityGraph graph
    ) {
        if (from.equals(to)) {
            return true;
        }
        final Vertex<PetriMarking> start = graph.getVerticesWithLabel(from).iterator().next();
        final List<PetriMarking> used = new LinkedList<PetriMarking>();
        final Queue<Vertex<PetriMarking>> queue = new LinkedList<Vertex<PetriMarking>>();
        queue.offer(start);
        used.add(from);
        while (!queue.isEmpty()) {
            final Vertex<PetriMarking> current = queue.poll();
            for (final Vertex<PetriMarking> next : graph.getAdjacentVertices(current)) {
                final PetriMarking tokens = next.label().get();
                if (used.contains(tokens)) {
                    continue;
                }
                if (to.equals(tokens)) {
                    return true;
                }
                used.add(tokens);
                queue.offer(next);
            }
        }
        return false;
    }

}
