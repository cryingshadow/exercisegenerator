package exercisegenerator.structures.graphs;

import java.math.*;
import java.util.*;

public class LabeledNode<L> {

    /**
     * Used to generate unique IDs (not synchronized).
     */
    private static BigInteger nextID = BigInteger.ONE;

    /**
     * This method is not synchronized.
     * @return A fresh unique ID.
     */
    public static BigInteger getNewID() {
        final BigInteger res = LabeledNode.nextID;
        LabeledNode.nextID = LabeledNode.nextID.add(BigInteger.ONE);
        return res;
    }

    public static void resetIDs() {
        LabeledNode.nextID = BigInteger.ONE;
    }

    /**
     * The unique ID of this node.
     */
    public final BigInteger id;

    /**
     * The label of this node.
     */
    public final Optional<L> label;

    /**
     * Creates a node without a label.
     */
    public LabeledNode() {
        this(Optional.empty());
    }

    /**
     * Creates a node with the specified label.
     * @param l The label of this node.
     */
    public LabeledNode(final Optional<L> label) {
        this.label = label;
        this.id = LabeledNode.getNewID();
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof LabeledNode) {
            return this.id.equals(((LabeledNode<?>)o).id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return String.format(
            "Node %s%s",
            this.id.toString(),
            this.label.isEmpty() ? "" : ": " + this.label.get().toString()
        );
    }

}
