package exercisegenerator.structures;

/**
 * A node in an AVL-tree with an int value.
 * @author Florian Corzilius, Thomas Stroeder
 * @version 1.1.0
 */
public class AVLNode {

    /**
     * The father node.
     */
    public AVLNode father;

    /**
     * The left child.
     */
    public AVLNode left;

    /**
     * The right child.
     */
    public AVLNode right;

    /**
     * The value.
     */
    public final int value;

    /**
     * Creates a node with the specified value and neither children nor a father.
     * @param val The node's value.
     */
    public AVLNode(final int val) {
        this.father = null;
        this.left = null;
        this.right = null;
        this.value = val;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof AVLNode)) {
            return false;
        }
        final AVLNode node = (AVLNode)o;
        if (this.value != node.value) {
            return false;
        }
        if (node.left == null) {
            if (this.left != null) {
                return false;
            }
        } else if (!node.left.equals(this.left)) {
            return false;
        }
        if (node.right == null) {
            if (this.right != null) {
                return false;
            }
        } else if (!node.right.equals(this.right)) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return
            3 * this.value
            + 7 * (this.left == null ? 0 : this.left.hashCode())
            + 5 * (this.right == null ? 0 : this.right.hashCode());
    }

    /**
     * @return The height of this node in the corresponding tree.
     */
    public int height() {
        final int leftHeight = this.left == null ? -1 : this.left.height();
        final int rightHeight = this.right == null ? -1 : this.right.height();
        if (leftHeight > rightHeight) {
            return leftHeight + 1;
        } else {
            return rightHeight + 1;
        }
    }

    /**
     * @return True if the tree rooted in this node is balanced. False otherwise.
     */
    public boolean isBalanced() {
        if (this.left != null) {
            if (!this.left.isBalanced()) {
                return false;
            }
        }
        if (this.right != null) {
            if (!this.right.isBalanced()) {
                return false;
            }
        }
        return
            Math.abs(
                (this.left == null ? -1 : this.left.height()) - (this.right == null ? -1 : this.right.height())
            ) < 2;
    }

    /**
     * @param fatherLink The node (same reference) that should be the father of this node.
     * @return True if each father link in the tree rooted in this node is correctly set.
     */
    public boolean isWellFormed(final AVLNode fatherLink) {
        if (this.father != fatherLink) {
            return false;
        }
        if (this.left != null) {
            if (!this.left.isWellFormed(this)) {
                return false;
            }
        }
        if (this.right != null) {
            if (!this.right.isWellFormed(this)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return A node with the smallest key in the tree rooted in the current node. The returned node is the
     *         left-most node in the tree rooted in the current node.
     */
    public AVLNode minimum() {
        AVLNode tmp = this;
        while (tmp.left != null) {
            tmp = tmp.left;
        }
        return tmp;
    }

    /**
     * @return A String representing the heights of all nodes in the tree rooted in the current node.
     */
    public String printHeights() {
        String result = new String("");
        if (this.left == null && this.right == null) {
            result += " " + this.height();
        } else {
            result += " [." + this.height();
            if (this.left != null) {
                result += this.left.printHeights();
            } else {
                result += " .";
            }
            if (this.right != null) {
                result += this.right.printHeights();
            } else {
                result += " .";
            }
            result += " ]";
        }
        return result;
    }

    /**
     * Gives the string representation of this AVL-tree starting at
     * the given node. The representation is a valid latex-qtree.
     * @return The string representation of this AVL-tree
     */
    @Override
    public String toString() {
        String result = new String("");
        if (this.left == null && this.right == null) {
            result += " " + this.value;
        } else {
            result += " [." + this.value;
            if (this.left != null) {
                result += this.left.toString();
            } else {
                result += " \\edge[draw=none];\\node[draw=none]{};";
            }
            if (this.right != null) {
                result += this.right.toString();
            } else {
                result += " \\edge[draw=none];\\node[draw=none]{};";
            }
            result += " ]";
        }
        return result;
    }

}