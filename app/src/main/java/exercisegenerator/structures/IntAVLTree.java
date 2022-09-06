package exercisegenerator.structures;

import java.io.*;

import exercisegenerator.*;
import exercisegenerator.io.*;

/**
 * Programm for creating solutions of exercises where elements have to be inserted into or deleted from an AVL-tree.
 */
public class IntAVLTree {

    /**
     * The root of the AVL-tree.
     */
    public AVLNode root;

    /**
     * The step counter which represents the number of printed trees in one exercise/solution.
     */
    private int stepCounter;

    /**
     * Creates an empty AVL-tree with the step counter being 0.
     */
    public IntAVLTree() {
       this.root = null;
       this.stepCounter = 0;
    }

    /**
     * Adds a node to the current tree with the specified value. If a writer is specified, the necessary steps are
     * printed to this writer.
     * @param val The value of the node to add.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public void add(final int val, final BufferedWriter writer) throws IOException {
        AVLNode current = this.root;
        AVLNode parent = null;
        while (current != null) {
            parent = current;
            if (val < current.value) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        // create the new node
        final AVLNode node = new AVLNode(val);
        node.father = parent;
        node.left = null;
        node.right = null;
        if (parent == null) {
            // tree is empty => new root
            this.root = node;
            if (writer != null) {
                this.print("f\\\"uge " + val + " ein", writer);
            }
        } else if (node.value < parent.value) {
            parent.left = node;
            if (writer != null) {
                this.print("f\\\"uge " + val + " ein", writer);
            }
            this.balance(parent, true, writer);
        } else {
            parent.right = node;
            if (writer != null) {
                this.print("f\\\"uge " + val + " ein", writer);
            }
            this.balance(parent, true, writer);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof IntAVLTree)) {
            return false;
        }
        final IntAVLTree tree = (IntAVLTree)o;
        if (tree.root == null) {
            return this.root == null;
        }
        return tree.root.equals(this.root);
    }

    /**
     * @param val The key to find.
     * @return The highest node with the given key in this AVL-tree or null if the given key does not occur.
     */
    public AVLNode find(final int val) {
        AVLNode currentNode = this.root;
        while (currentNode != null) {
            if (val < currentNode.value) {
                currentNode = currentNode.left;
            } else if (val > currentNode.value) {
                currentNode = currentNode.right;
            } else {
                // val == currentNode.value
                return currentNode;
            }
        }
        return null;
    }

    /**
     * @return The height of the AVL-tree (-1 if the tree is empty, 0 if the tree just consists of the root).
     */
    public int getHeight() {
        if (this.root == null) {
            return -1;
        } else {
            return this.root.height();
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.root == null ? 73 : this.root.hashCode() + 57;
    }

    /**
     * @return True if this tree is balanced (i.e., for each node in this tree, the heights of its two subtrees differ
     *         at most by one).
     */
    public boolean isBalanced() {
        if (this.root == null) {
            return true;
        }
        return this.root.isBalanced();
    }

    /**
     * @return True if the tree is empty. False otherwise.
     */
    public boolean isEmpty() {
        return this.root == null;
    }

    /**
     * @return True if each father link is correctly set in this tree. False otherwise.
     */
    public boolean isWellFormed() {
        if (this.root == null) {
            return true;
        }
        return this.root.isWellFormed(null);
    }

    /**
     * Prints this AVL-tree right under the given headline.
     * @param headline A headline.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public void print(final String headline, final BufferedWriter writer) throws IOException  {
        this.printVerticalSpace(writer);
        this.printSamePageBeginning(headline, writer);
        TikZUtils.printTikzBeginning(TikZStyle.TREE, writer);
        if (this.root == null) {
            writer.write("\\Tree [.\\phantom{0} ];");
        } else if (this.root.left == null && this.root.right == null ) {
            writer.write("\\Tree [." + this.root.value + " ];");
        } else {
            writer.write("\\Tree");
            writer.write(this.root.toString());
        }
        Main.newLine(writer);
        TikZUtils.printTikzEnd(writer);
        TikZUtils.printProtectedNewline(writer);
        this.printSamePageEnd(writer);
    }

    /**
     * Removes the specified node from this AVL-tree. The node must exist in this tree. If a writer is specified, all
     * necessary steps are printed to this writer.
     * @param node The node to remove.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public void remove(final AVLNode node, final BufferedWriter writer) throws IOException {
        final int value = node.value;
        if (this.root.left == null && this.root.right == null ) {
            assert (node == this.root) : "Tried to remove a node which does not exist in this AVL-tree!";
            this.root = null;
            if (writer != null) {
                this.print("entferne " + value, writer);
            }
        } else {
            final AVLNode tmp = this.delete(node);
            if (writer != null) {
                this.print("entferne " + value, writer);
            }
            // Balance the tree
            if (tmp != null) {
                this.balance(tmp, false, writer);
            }
        }
    }

    /**
     * Sets the step counter back to 0.
     */
    public void resetStepCounter() {
        this.stepCounter = 0;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.root == null ? "empty" : this.root.toString();
    }

    /**
     * Updates the height of the given node and its ancestors and balances the tree. The given node is possibly
     * part of an unbalanced subtree of this AVL-tree. This method checks for every node on the path from the given
     * node to the root, whether it is the root of an unbalanced subtree and then balances it. If the given flag
     * afterInsertion is false, it continues this until reaching the root. If a writer is specified, all necessary
     * steps are printed to that writer.
     * @param node The unbalanced node.
     * @param afterInsertion A flag indicating whether the balancing is applied after an insertion. In this case, this
     *                       method only needs to balance at most once in order to obtain a balanced tree. Otherwise,
     *                       balancing needs to continue up to the root.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void balance(final AVLNode node, final boolean afterInsertion, final BufferedWriter writer) throws IOException {
        AVLNode currentNode = node;
        while (currentNode != null) {
            if (currentNode.left == null && currentNode.right != null) {
                if (currentNode.right.height() > 0) {
                    currentNode = this.balanceRightToLeft(currentNode, writer);
                    if (afterInsertion) {
                        return;
                    }
                }
            } else if (currentNode.right == null && currentNode.left != null) {
                if (currentNode.left.height() > 0) {
                    currentNode = this.balanceLeftToRight(currentNode, writer);
                    if (afterInsertion) {
                        return;
                    }
                }
            } else if (currentNode.right != null && currentNode.left != null) {
                final int diff = currentNode.left.height() - currentNode.right.height();
                if (diff > 1) {
                    // left subtree is too high
                    currentNode = this.balanceLeftToRight(currentNode, writer);
                    if (afterInsertion) {
                        return;
                    }
                } else if (diff < -1) {
                    // right subtree is too high
                    currentNode = this.balanceRightToLeft(currentNode, writer);
                    if (afterInsertion) {
                        return;
                    }
                }
            }
            currentNode = currentNode.father;
        }
    }

    /**
     * Balances the tree at the given node and updates the heights of the nodes involved in the balancing. This is the
     * case where the difference between the left subtree's height and the right subtree's height is two.
     * @param node The unbalanced node.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     * @return The node at the original position of the specified node after balancing.
     */
    private AVLNode balanceLeftToRight(final AVLNode node, final BufferedWriter writer) throws IOException {
        // left subtree of node has at least height 1 (right might have a height of -1)
        final AVLNode l = node.left;
        if (l.right == null || (l.left != null && l.right.height() <= l.left.height())) {
            // left subtree of l is at least as high as the right subtree of l
            return this.rightRotate(node, writer);
        } else {
            // right subtree of l is higher
            this.leftRotate(l, writer);
            return this.rightRotate(node, writer);
        }
    }

    /**
     * Balances the tree at the given node and updates the heights of the nodes involved in the balancing. This is the
     * case where the difference between the right subtree's height and the left subtree's height is two.
     * @param node The unbalanced node.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     * @return The node at the original position of the specified node after balancing.
     */
    private AVLNode balanceRightToLeft(final AVLNode node, final BufferedWriter writer) throws IOException {
        // right subtree of node has at least height 1 (left might have a height of -1)
        final AVLNode r = node.right;
        if (r.left == null || (r.right != null && r.left.height() <= r.right.height())) {
            // right subtree of r is at least as high as the left subtree of r
            return this.leftRotate(node, writer);
        } else {
            // left subtree of r is higher
            this.rightRotate(r, writer);
            return this.leftRotate(node, writer);
        }
    }

    /**
     * Removes the specified node from this AVL-tree. The node must exist in this tree.
     * @param node The node to remove.
     * @return If the node which has been deleted structurally (this node might be different from the node to be
     *         deleted logically, but then their values will be swapped) is replaced by null, then the father of the
     *         structurally deleted node is returned. Otherwise, the node replacing the structurally deleted node is
     *         returned.
     */
    private AVLNode delete(final AVLNode node) {
        if (node.left != null && node.right != null) {
            // two children
            final AVLNode tmp = node.right.minimum();
            final AVLNode tmp2 = this.delete(tmp);
            // since tmp has been deleted, we can replace its links with that of node
            this.swap(node, tmp);
            return tmp2 == node ? tmp : tmp2;
        } else if (node.left != null) {
            // only left child
            return this.replace(node, node.left);
        } else {
            // only right child or no child (in the latter case we have node.right == null)
            return this.replace(node, node.right);
        }
    }

    /**
     * Performs a left-rotation on the given node of this AVL-tree. If a writer is specified, it also prints the
     * result to it.
     * @param node The node to rotate at.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     * @return The node at the original position of the specified node after rotation.
     */
    private AVLNode leftRotate(final AVLNode node, final BufferedWriter writer) throws IOException {
        final AVLNode r = node.right;
        // move left subtree of r to be the new right subtree of node
        node.right = r.left;
        if (node.right != null) {
            node.right.father = node;
        }
        // bring back r to the tree
        r.father = node.father;
        if (node.father == null) {
            // node was the root
            this.root = r;
        } else if (node == node.father.left) {
            // node was left child
            r.father.left = r;
        } else {
            // node was right child
            r.father.right = r;
        }
        // link node
        r.left = node;
        node.father = r;
        if (writer != null) {
            this.print("rotiere " + node.value + " nach links", writer);
        }
        return r;
    }

    /**
     * Prints the beginning of a samepage environment.
     * @param headline The headline of this environment.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void printSamePageBeginning(final String headline, final BufferedWriter writer) throws IOException {
        if (this.getHeight() <= 0) {
            writer.write("\\begin{minipage}[t]{0.2 \\columnwidth}");
            Main.newLine(writer);
        } else if (this.getHeight() < 9) {
            writer.write("\\begin{minipage}[t]{0." + (this.getHeight() + 1) + " \\columnwidth}");
            Main.newLine(writer);
        }
        if (headline != null && !"".equals(headline)) {
            writer.write(headline + "\\\\[-2ex]");
        }
        Main.newLine(writer);
        writer.write("\\begin{center}");
        Main.newLine(writer);
    }

    /**
     * Prints the end of a samepage environment.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void printSamePageEnd(final BufferedWriter writer) throws IOException {
        writer.write("\\end{center}");
        Main.newLine(writer);
        if (this.getHeight() < 9 ) {
            writer.write("\\end{minipage}");
            Main.newLine(writer);
        }
    }

    /**
     * Prints vertical space
     * @param step The next evaluation step.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void printVerticalSpace(final BufferedWriter writer) throws IOException {
        this.stepCounter += this.getHeight() + 1;
        if (this.stepCounter >= 10) {
            Main.newLine(writer);
            writer.write("~\\\\");
            Main.newLine(writer);
            Main.newLine(writer);
            if (this.getHeight() <= 0) {
                this.stepCounter = 2;
            } else {
                this.stepCounter = this.getHeight() + 1;
            }
        }
    }

    /**
     * Replaces the old node by the new node from the perspective of the old node's father, but does not update
     * the children of the new node.
     * @param oldNode The node to be replaced.
     * @param newNode The node by which oldNode is to be replaced.
     * @return If the new node is null, the father of the old node is returned. Otherwise, the new node is returned.
     */
    private AVLNode replace(final AVLNode oldNode, final AVLNode newNode) {
        // newNode could be null
        if (newNode != null) {
            newNode.father = oldNode.father;
        }
        if (oldNode.father == null) {
            // root
            this.root = newNode;
        } else if (oldNode == oldNode.father.left) {
            // oldNode was the left child of its father
            oldNode.father.left = newNode;
        } else {
            // oldNode was the right child of its father
            oldNode.father.right = newNode;
        }
        return newNode == null ? oldNode.father : newNode;
    }

    /**
     * Performs a right-rotation on the given node of this AVL-tree. If a writer is specified, it also prints the
     * result to it.
     * @param node The node to rotate at.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     * @return The node at the original position of the specified node after rotation.
     */
    private AVLNode rightRotate(final AVLNode node, final BufferedWriter writer) throws IOException {
        final AVLNode l = node.left;
        // move right subtree of l to be the new left subtree of node
        node.left = l.right;
        if (node.left != null) {
            node.left.father = node;
        }
        // bring back l to the tree
        l.father = node.father;
        if (node.father == null) {
            // node was the root
            this.root = l;
        } else if (node == node.father.left) {
            // node was left child
            l.father.left = l;
        } else {
            // node was right child
            l.father.right = l;
        }
        // link node
        l.right = node;
        node.father = l;
        if (writer != null) {
            this.print("rotiere " + node.value + " nach rechts", writer);
        }
        return l;
    }

    /**
     * Replaces the old node by the new node, including an update of the new node's children. Should only be
     * applied if the new node has no children or the new node has been deleted from the tree before.
     * @param oldNode The node to be replaced.
     * @param newNode The node by which oldNode is to be replaced.
     */
    private void swap(final AVLNode oldNode, final AVLNode newNode) {
        newNode.left = oldNode.left;
        if (newNode.left != null) {
            newNode.left.father = newNode;
        }
        newNode.right = oldNode.right;
        if (newNode.right != null) {
            newNode.right.father = newNode;
        }
        this.replace(oldNode, newNode);
    }

}
