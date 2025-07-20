package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.*;
import exercisegenerator.structures.graphs.petrinets.*;

public class PetriNetCoverabilityAlgorithm extends PetriNetAlgorithm<CoverabilityGraph> {

    @Override
    public CoverabilityGraph apply(final PetriNetInput input) {
        final PetriNet net = new PetriNet(input);
        final CoverabilityGraph result = new CoverabilityGraph(new Vertex<PetriMarking>(input.tokens()));
        final List<PetriMarking> used = new LinkedList<PetriMarking>();
        used.add(input.tokens());
        final Queue<PetriMarking> queue = new LinkedList<PetriMarking>();
        queue.offer(input.tokens());
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
        writer.write("Betrachten Sie das folgende Petrinetz:\\[2ex]");
        Main.newLine(writer);
        final PetriNet net = new PetriNet(problem);
        net.toTikz(problem.tokens(), writer);
        LaTeXUtils.printVerticalProtectedSpace(writer);
        writer.write("Geben Sie einen Abdeckungsgraphen zu diesem Petrinetz an.");
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
        solution.printTikZ(GraphPrintMode.ALL, 1, null, writer);
    }

    private PetriMarking addOmegas(
        final PetriMarking tokens,
        final PetriTransition transition,
        final PetriMarking nextTokens,
        final List<PetriMarking> used,
        final CoverabilityGraph result
    ) {
        // TODO Auto-generated method stub
        return null;
    }

}
