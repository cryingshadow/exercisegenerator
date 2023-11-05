package exercisegenerator.structures.graphs;

import java.math.*;
import java.util.*;

public class Vertex<L> {

    /**
     * Used to generate unique IDs (not synchronized).
     */
    private static BigInteger nextID = BigInteger.ONE;

    /**
     * This method is not synchronized.
     * @return A fresh unique ID.
     */
    public static BigInteger getNewID() {
        final BigInteger res = Vertex.nextID;
        Vertex.nextID = Vertex.nextID.add(BigInteger.ONE);
        return res;
    }

    public static void resetIDs() {
        Vertex.nextID = BigInteger.ONE;
    }

    public final BigInteger id;

    public final Optional<L> label;

    public Vertex() {
        this(Optional.empty());
    }

    public Vertex(final L label) {
        this(Optional.of(label));
    }

    public Vertex(final Optional<L> label) {
        this.label = label;
        this.id = Vertex.getNewID();
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

}
