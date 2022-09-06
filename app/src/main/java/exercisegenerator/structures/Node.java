package exercisegenerator.structures;

import java.math.*;
import java.util.*;

/**
 * A labeled node.
 * @param <L> The type of the label.
 */
public class Node<L> {

    /**
     * Used to generate unique IDs (not synchronized).
     */
    private static BigInteger nextID = BigInteger.ONE;

    /**
     * This method is not synchronized.
     * @return A fresh unique ID.
     */
    public static BigInteger getNewID() {
        final BigInteger res = Node.nextID;
        Node.nextID = Node.nextID.add(BigInteger.ONE);
        return res;
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
    public Node() {
        this(Optional.empty());
    }

    /**
     * Creates a node with the specified label.
     * @param l The label of this node.
     */
    public Node(final Optional<L> label) {
        this.label = label;
        this.id = Node.getNewID();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (o instanceof Node) {
            return this.id.equals(((Node<?>)o).id);
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
