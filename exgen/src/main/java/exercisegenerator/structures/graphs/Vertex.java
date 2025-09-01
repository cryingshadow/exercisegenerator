package exercisegenerator.structures.graphs;

import java.math.*;
import java.util.*;

public record Vertex<L extends Comparable<L>>(BigInteger id, Optional<L> label) implements Comparable<Vertex<L>> {

    private static BigInteger nextID = BigInteger.ONE;

    public static BigInteger getNewID() {
        final BigInteger res = Vertex.nextID;
        Vertex.nextID = Vertex.nextID.add(BigInteger.ONE);
        return res;
    }

    public static void resetIDs() {
        Vertex.nextID = BigInteger.ONE;
    }

    public Vertex() {
        this(Optional.empty());
    }

    public Vertex(final L label) {
        this(Optional.of(label));
    }

    public Vertex(final Optional<L> label) {
        this(Vertex.getNewID(), label);
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof Vertex) {
            return this.id.equals(((Vertex<?>)o).id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    public boolean logicallyEquals(final Vertex<L> otherVertex) {
        return this.label.equals(otherVertex.label);
    }

    @Override
    public String toString() {
        return String.format(
            "Vertex %s%s",
            this.id.toString(),
            this.label.isEmpty() ? "" : ": " + this.label.get().toString()
        );
    }

    @Override
    public int compareTo(final Vertex<L> o) {
        if (this.label().isEmpty()) {
            if (o.label().isEmpty()) {
                return this.id().compareTo(o.id());
            }
            return -1;
        }
        if (o.label().isEmpty()) {
            return 1;
        }
        return this.label().get().compareTo(o.label().get());
    }

}
