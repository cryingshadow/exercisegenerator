package exercisegenerator.structures.graphs.petrinets;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.io.*;

public class PetriNet {

    private static Optional<Integer> omegaSum(final Optional<Integer> oldValue, final Optional<Integer> newValue) {
        if (oldValue.isEmpty() || newValue.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(oldValue.get() + newValue.get());
    }

    private final PetriPlace[] places;

    private final List<PetriTransition> transitions;

    public PetriNet(final PetriNetInput input) {
        this.places = input.places().toArray(new PetriPlace[input.places().size()]);
        this.transitions = Collections.unmodifiableList(input.transitions());
    }

    public List<PetriTransition> activeTransitions(final PetriMarking tokens) {
        return this.transitions
            .stream()
            .filter(
                t -> t.from()
                .entrySet()
                .stream()
                .map(
                    entry -> tokens.getOrDefault(entry.getKey(), Optional.of(0))
                    .orElse(entry.getValue()) >= entry.getValue()
                ).reduce(true, Boolean::logicalAnd)
            ).toList();
    }

    public Optional<PetriMarking> fireTransition(final PetriMarking tokens) {
        final List<PetriTransition> activeTransitions = this.activeTransitions(tokens);
        if (activeTransitions.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(
            this.fireTransition(tokens, activeTransitions.get(Main.RANDOM.nextInt(activeTransitions.size())))
        );
    }

    public PetriMarking fireTransition(final PetriMarking tokens, final PetriTransition transition) {
        final PetriMarking result = new PetriMarking(tokens);
        for (final Map.Entry<Integer, Integer> fromEntry : transition.from().entrySet()) {
            result.merge(fromEntry.getKey(), Optional.of(-fromEntry.getValue()), PetriNet::omegaSum);
        }
        for (final Map.Entry<Integer, Integer> toEntry : transition.to().entrySet()) {
            result.merge(toEntry.getKey(), Optional.of(toEntry.getValue()), PetriNet::omegaSum);
        }
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        final BufferedWriter writer =
            new BufferedWriter(
                new Writer() {

                    @Override
                    public void close() throws IOException {
                    }

                    @Override
                    public void flush() throws IOException {
                    }

                    @Override
                    public void write(final char[] cbuf, final int off, final int len) throws IOException {
                        result.append(cbuf, off, len);
                    }

                }
            );
        try {
            this.toTikz(new PetriMarking(), writer);
            writer.flush();
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
        return result.toString();
    }

    public void toTikz(final PetriMarking tokens, final BufferedWriter writer) throws IOException {
        LaTeXUtils.printTikzBeginning(TikZStyle.EMPTY, writer);
        for (int placeIndex = 0; placeIndex < this.places.length; placeIndex++) {
            final PetriPlace place = this.places[placeIndex];
            writer.write(
                String.format(
                    "\\node[place,label=%d:%s,tokens=%d] (p%d) at (%d,%d) {};",
                    place.labelDegree(),
                    place.label(),
                    tokens.getOrDefault(placeIndex, Optional.of(0)).orElseGet(() -> 0),
                    placeIndex,
                    place.x(),
                    place.y()
                )
            );
            Main.newLine(writer);
        }
        int transitionIndex = 0;
        for (final PetriTransition transition : this.transitions) {
            writer.write(
                String.format(
                    "\\node[transition] (t%d) at (%d,%d) {%s}",
                    transitionIndex,
                    transition.x(),
                    transition.y(),
                    transition.label()
                )
            );
            for (final Map.Entry<Integer, Integer> fromEntry : transition.from().entrySet()) {
                Main.newLine(writer);
                writer.write(
                    String.format("  edge[pre] node[auto] {%d} (p%d)", fromEntry.getValue(), fromEntry.getKey())
                );
            }
            for (final Map.Entry<Integer, Integer> toEntry : transition.to().entrySet()) {
                Main.newLine(writer);
                writer.write(
                    String.format("  edge[post] node[auto,swap] {%d} (p%d)", toEntry.getValue(), toEntry.getKey())
                );
            }
            writer.write(";");
            Main.newLine(writer);
            transitionIndex++;
        }
        LaTeXUtils.printTikzEnd(writer);
    }

}
