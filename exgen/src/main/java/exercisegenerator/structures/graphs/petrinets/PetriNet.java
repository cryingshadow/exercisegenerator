package exercisegenerator.structures.graphs.petrinets;

import java.io.*;
import java.util.*;

import org.apache.commons.math3.fraction.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.algebra.*;
import exercisegenerator.structures.graphs.*;

public class PetriNet {

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
                .map(entry -> tokens.get(entry.getKey()).orElse(entry.getValue()) >= entry.getValue())
                .reduce(true, Boolean::logicalAnd)
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
        final PetriMarking result = PetriMarking.create(tokens);
        for (final Map.Entry<Integer, Integer> fromEntry : transition.from().entrySet()) {
            result.omegaMerge(fromEntry.getKey(), Optional.of(-fromEntry.getValue()));
        }
        for (final Map.Entry<Integer, Integer> toEntry : transition.to().entrySet()) {
            result.omegaMerge(toEntry.getKey(), Optional.of(toEntry.getValue()));
        }
        return result;
    }

    public PetriMarking getZeroMarking() {
        return PetriMarking.createZeroMarking(this.places.length);
    }

    public GraphWithVertexMappings toGraphWithVertexMappings() {
        final Graph<String, Integer> graph = new Graph<String, Integer>();
        final Map<Vertex<String>, Object> backwardsMapping = new LinkedHashMap<Vertex<String>, Object>();
        final Map<Vertex<String>, Integer> placeIndex = new LinkedHashMap<Vertex<String>, Integer>();
        final List<Vertex<String>> placeVertices = new ArrayList<Vertex<String>>();

        for (int index = 0; index < this.places.length; index++) {
            final PetriPlace place = this.places[index];
            final Vertex<String> vertex = new Vertex<String>(place.label());
            backwardsMapping.put(vertex, place);
            placeIndex.put(vertex, index);
            graph.addVertex(vertex);
            placeVertices.add(vertex);
        }
        for (final PetriTransition transition : this.transitions) {
            final Vertex<String> vertex = new Vertex<String>(transition.label());
            backwardsMapping.put(vertex, transition);
            graph.addVertex(vertex);
            for (final Map.Entry<Integer, Integer> entry : transition.from().entrySet()) {
                graph.addEdge(placeVertices.get(entry.getKey()), Optional.of(entry.getValue()), vertex);
            }
            for (final Map.Entry<Integer, Integer> entry : transition.to().entrySet()) {
                graph.addEdge(vertex, Optional.of(entry.getValue()), placeVertices.get(entry.getKey()));
            }
        }
        return new GraphWithVertexMappings(graph, backwardsMapping, placeIndex);
    }

    public Matrix toIncidenceMatrix() {
        final int numberOfTransitions = this.transitions.size();
        final Matrix result = new Matrix(numberOfTransitions, this.places.length, numberOfTransitions);
        int column = 0;
        for (final PetriTransition transition : this.transitions) {
            for (int row = 0; row < this.places.length; row++) {
                result.setCoefficient(
                    column,
                    row,
                    new BigFraction(transition.to().getOrDefault(row, 0) - transition.from().getOrDefault(row, 0))
                );
            }
            column++;
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
            this.toTikz(this.getZeroMarking(), writer);
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
                    Locale.US,
                    "\\node[place,label=%d:%s,tokens=%d] (p%d) at (%.2f,%.2f) {};",
                    place.labelDegree(),
                    place.label(),
                    tokens.get(placeIndex).orElseGet(() -> 0),
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
                    Locale.US,
                    "\\node[transition] (t%d) at (%.2f,%.2f) {%s}",
                    transitionIndex,
                    transition.x(),
                    transition.y(),
                    transition.label()
                )
            );
            for (final Map.Entry<Integer, Integer> fromEntry : transition.from().entrySet()) {
                Main.newLine(writer);
                writer.write(
                    String.format(
                        "  edge[pre%s] node[auto] {%d} (p%d)",
                        transition.to().containsKey(fromEntry.getKey()) ? ", bend left = 10" : "",
                        fromEntry.getValue(),
                        fromEntry.getKey()
                    )
                );
            }
            for (final Map.Entry<Integer, Integer> toEntry : transition.to().entrySet()) {
                Main.newLine(writer);
                writer.write(
                    String.format(
                        "  edge[post%s] node[auto,swap] {%d} (p%d)",
                        transition.from().containsKey(toEntry.getKey()) ? ", bend right = 10" : "",
                        toEntry.getValue(),
                        toEntry.getKey()
                    )
                );
            }
            writer.write(";");
            Main.newLine(writer);
            transitionIndex++;
        }
        LaTeXUtils.printTikzEnd(writer);
    }

}
