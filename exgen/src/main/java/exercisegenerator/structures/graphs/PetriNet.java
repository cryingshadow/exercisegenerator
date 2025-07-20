package exercisegenerator.structures.graphs;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.io.*;

public class PetriNet {

    private final PetriPlace[] places;

    private final List<PetriTransition> transitions;

    public PetriNet(final PetriNetInput input) throws IOException {
        this.places = input.places().toArray(new PetriPlace[input.places().size()]);
        this.transitions = Collections.unmodifiableList(input.transitions());
    }

    public Optional<Map<Integer, Integer>> fireTransition(final Map<Integer, Integer> tokens) {
        final List<PetriTransition> activeTransitions = this.activeTransitions(tokens);
        if (activeTransitions.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(
            this.fireTransition(tokens, activeTransitions.get(Main.RANDOM.nextInt(activeTransitions.size())))
        );
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
            this.toTikz(Collections.emptyMap(), writer);
            writer.flush();
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
        return result.toString();
    }

    public void toTikz(final Map<Integer, Integer> tokens, final BufferedWriter writer) throws IOException {
        LaTeXUtils.printTikzBeginning(TikZStyle.EMPTY, writer);
        for (int placeIndex = 0; placeIndex < this.places.length; placeIndex++) {
            final PetriPlace place = this.places[placeIndex];
            writer.write(
                String.format(
                    "\\node[place,label=%d:%s,tokens=%d] (p%d) at (%d,%d) {};",
                    place.labelDegree(),
                    place.label(),
                    tokens.getOrDefault(placeIndex, 0),
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

    private List<PetriTransition> activeTransitions(final Map<Integer, Integer> tokens) {
        return this.transitions
            .stream()
            .filter(
                t -> t.from()
                .entrySet()
                .stream()
                .map(entry -> tokens.getOrDefault(entry.getKey(), 0) >= entry.getValue())
                .reduce(true, Boolean::logicalAnd)
            ).toList();
    }

    private Map<Integer, Integer> fireTransition(final Map<Integer, Integer> tokens, final PetriTransition transition) {
        final Map<Integer, Integer> result = new LinkedHashMap<Integer, Integer>(tokens);
        for (final Map.Entry<Integer, Integer> fromEntry : transition.from().entrySet()) {
            result.merge(fromEntry.getKey(), -fromEntry.getValue(), Integer::sum);
        }
        for (final Map.Entry<Integer, Integer> toEntry : transition.to().entrySet()) {
            result.merge(toEntry.getKey(), toEntry.getValue(), Integer::sum);
        }
        return result;
    }

}
