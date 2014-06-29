import java.math.*;

/**
 * A labeled node.
 * @author cryingshadow
 * @version $Id$
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
        BigInteger res = Node.nextID;
        Node.nextID = Node.nextID.add(BigInteger.ONE);
        return res;
    }

    /**
     * The unique ID of this node.
     */
    private final BigInteger id;

    /**
     * The label of this node.
     */
    private L label;

    /**
     * @return The label of this node.
     */
    public L getLabel() {
        return this.label;
    }

    /**
     * Sets the label of this node.
     * @param l The label of this node.
     */
    public void setLabel(L l) {
        this.label = l;
    }

    /**
     * @return The unique ID of this node.
     */
    public BigInteger getID() {
        return this.id;
    }

    /**
     * Creates a node with the specified label.
     * @param l The label of this node.
     */
    public Node(L l) {
        this.label = l;
        this.id = Node.getNewID();
    }

    /**
     * Creates a node without a label.
     */
    public Node() {
        this(null);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Node) {
            return this.id.equals(((Node<?>)o).id);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Node " + this.id.toString() + (this.label == null ? "" : ": " + this.label.toString());
    }

}
