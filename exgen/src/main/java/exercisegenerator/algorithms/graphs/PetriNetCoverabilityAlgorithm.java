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
        final PetriMarking firstTokens = net.getZeroMarking();
        for (final Map.Entry<Integer, Integer> entry : input.tokens().entrySet()) {
            firstTokens.merge(entry.getKey(), Optional.of(entry.getValue()), PetriMarking::omegaSum);
        }
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
    public String[] generateTestParameters() {
        return new String[] {};
    }

    @Override
    public void printExercise(
        final PetriNetInput problem,
        final CoverabilityGraph solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Betrachten Sie das folgende Petrinetz $N$:\\\\[2ex]");
        Main.newLine(writer);
        final PetriNet net = new PetriNet(problem);
        LaTeXUtils.printDefaultAdjustboxBeginning(writer);
        net.toTikz(PetriMarking.create(problem.tokens()), writer);
        LaTeXUtils.printAdjustboxEnd(writer);
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("Geben Sie einen Abdeckungsgraphen zu $N$ an.");
        Main.newLine(writer);
        Main.newLine(writer);
    }

    @Override
    public void printSolution(
        final PetriNetInput problem,
        final CoverabilityGraph solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printDefaultAdjustboxBeginning(writer);
        solution.printTikZ(
            new ForceGraphLayout<PetriMarking, String>(solution, TikZStyle.COVERABILITY_GRAPH, 4, 1, 12, 12),
            writer
        );
        LaTeXUtils.printAdjustboxEnd(writer);
        Main.newLine(writer);
    }

    private PetriMarking addOmegas(
        final PetriMarking tokens,
        final PetriTransition transition,
        final PetriMarking nextTokens,
        final List<PetriMarking> used,
        final CoverabilityGraph graph
    ) {
        final PetriMarking result = new PetriMarking(nextTokens);
        for (final PetriMarking existingTokens : used) {
            if (result.strictlyCovers(existingTokens) && this.isReachable(existingTokens, tokens, graph)) {
                for (final Map.Entry<Integer, Optional<Integer>> entry : existingTokens.entrySet()) {
                    final Optional<Integer> resultValue = result.get(entry.getKey());
                    if (resultValue.isPresent() && resultValue.get() > entry.getValue().get()) {
                        result.put(entry.getKey(), Optional.empty());
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
                final PetriMarking tokens = next.label.get();
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
