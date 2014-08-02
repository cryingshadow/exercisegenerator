import java.io.*;
import java.util.*;

/**
 * Programm for creating solutions of exercises where elements have to be inserted into or deleted from an AVL-tree.
 * @author Florian Corzilius, Thomas Stroeder
 * @version 1.1.0
 */
public class IntAVLTree {

    /**
     * Performs the operations specified by <code>construction</code> and <code>ops</code> on the specified AVL-tree and
     * prints the results to the specified writer. The <code>construction</code> operations are not displayed.
     * @param tree The AVL-tree.
     * @param ops The operations.
     * @param construction The operations used to construct the start structure.
     * @param writer The writer for the solution.
     * @param writerSpace The writer for the tree to start with (the one reached after the <code>construction</code> 
     *                    operations). May be null if this tree should not be displayed separately.
     * @throws IOException If some error occurs during output.
     */
    public static void avltree(
        IntAVLTree tree,
        Deque<Pair<Integer, Boolean>> ops,
        Deque<Pair<Integer, Boolean>> construction,
        BufferedWriter writer,
        BufferedWriter writerSpace
    ) throws IOException {
        if (ops.isEmpty()) {
            return;
        }
        while (!construction.isEmpty()) {
            Pair<Integer, Boolean> operation = construction.poll();
            if (operation.y) {
                //System.out.println("insert " + operation.x + " in:");
                //System.out.println(tree.toString(tree.root()));
                tree.add(operation.x, null);
            } else {
                //System.out.println("remove " + operation.x + " in:");
                AVLNode toRemove = tree.find(operation.x);
                if (toRemove != null) {
                    tree.remove(toRemove, null);
                }
            }
            //System.out.println("results in:");
            //System.out.println(tree.toString(tree.mRoot));
        }
        if (writerSpace != null) {
            if (ops.size() > 1) {
                if (tree.isEmpty()) {
                    writerSpace.write("F\\\"uhren Sie folgenden Operationen beginnend mit einem anfangs leeren ");
                    writerSpace.write("\\emphasize{AVL-Baum} aus und geben Sie die entstehenden B\\\"aume nach jeder ");
                    writerSpace.write("\\emphasize{Einf\\\"uge-} und \\emphasize{L\\\"oschoperation} sowie jeder ");
                    writerSpace.write("\\emphasize{Rotation} an:\\\\\\\\");
                    writerSpace.newLine();
                } else {
                    writerSpace.write("Betrachten Sie den folgenden \\emphasize{AVL-Baum}:\\\\[2ex]");
                    writerSpace.newLine();
                    writerSpace.newLine();
                    tree.print("", writerSpace);
                    writerSpace.newLine();
                    writerSpace.newLine();
                    writerSpace.write("\\vspace*{1ex}");
                    writerSpace.newLine();
                    writerSpace.write("F\\\"uhren Sie beginnend mit diesem Baum die folgenden Operationen aus und ");
                    writerSpace.write("geben Sie die entstehenden B\\\"aume nach jeder \\emphasize{Einf\\\"uge-} und ");
                    writerSpace.write("\\emphasize{L\\\"oschoperation} sowie jeder \\emphasize{Rotation} an:\\\\\\\\");
                    writerSpace.newLine();
                }
                TikZUtils.printBeginning(TikZUtils.ENUMERATE, writerSpace);
                for (Pair<Integer, Boolean> op : ops) {
                    if (op.y) {
                        writerSpace.write(TikZUtils.ITEM + " " + op.x + " einf\\\"ugen\\\\");
                    } else {
                        writerSpace.write(TikZUtils.ITEM + " " + op.x + " l\\\"oschen\\\\");
                    }
                    writerSpace.newLine();
                }
                TikZUtils.printEnd(TikZUtils.ENUMERATE, writerSpace);
            } else {
                Pair<Integer, Boolean> op = ops.peek();
                if (tree.isEmpty()) {
                    if (op.y) {
                        writerSpace.write("F\\\"ugen Sie den Wert " + op.x);
                        writerSpace.write(" in einen leeren \\emphasize{AVL-Baum} ein und geben Sie die entstehenden ");
                        writerSpace.write("B\\\"aume nach jeder \\emphasize{Einf\\\"ugeoperation} sowie jeder ");
                        writerSpace.write("\\emphasize{Rotation} an.");
                    } else {
                        // this case is nonsense 
                        return;
                    }
                } else {
                    if (op.y) {
                        writerSpace.write("F\\\"ugen Sie den Wert " + op.x);
                        writerSpace.write(" in den folgenden \\emphasize{AVL-Baum} ein und geben Sie die entstehenden");
                        writerSpace.write(" B\\\"aume nach jeder \\emphasize{Einf\\\"ugeoperation} sowie jeder ");
                        writerSpace.write("\\emphasize{Rotation} an:\\\\[2ex]");
                    } else {
                        writerSpace.write("L\\\"oschen Sie den Wert " + op.x);
                        writerSpace.write(" aus dem folgenden \\emphasize{AVL-Baum} und geben Sie die entstehenden ");
                        writerSpace.write("B\\\"aume nach jeder \\emphasize{L\\\"oschoperation} sowie jeder ");
                        writerSpace.write("\\emphasize{Rotation} an:\\\\[2ex]");
                    }
                    writerSpace.newLine();
                    writerSpace.newLine();
                    tree.print("", writerSpace);
                    writerSpace.newLine();
                }
            }
        }
        tree.resetStepCounter();
        while (!ops.isEmpty()) {
            Pair<Integer, Boolean> operation = ops.poll();
            if (operation.y) {
                //System.out.println("insert " + operation.x + " in:");
                //System.out.println(tree.toString(tree.root()));
                tree.add(operation.x, writer);
            } else {
                //System.out.println("remove " + operation.x + " in:");
                //System.out.println(tree.toString(tree.root()));
                if (operation.x == 49) {
                    System.out.println("foo!");
                }
                AVLNode toRemove = tree.find(operation.x);
                if (toRemove != null) {
                    tree.remove(toRemove, writer);
                } else {
                    tree.print(operation.x + " kommt nicht vor", writer);
                }
            }
            //System.out.println("results in:");
            //System.out.println(tree.toString(tree.mRoot));
        }
    }

    /**
     * The root of the AVL-tree.
     */
    private AVLNode root;

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
    public void add(int val, BufferedWriter writer) throws IOException {
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
        AVLNode node = new AVLNode(val);
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

    /**
     * @return The height of the AVL-tree (-1 if the tree is empty, 0 if the tree just consists of the root).
     */
    public int getHeight() {
        if (this.root == null) {
            return -1;
        } else {
            return this.root.height;
        }
    }

    /**
     * @return True if the tree is empty. False otherwise.
     */
    public boolean isEmpty() {
        return this.root == null;
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
    private void balance(AVLNode node, boolean afterInsertion, BufferedWriter writer) throws IOException {
        AVLNode currentNode = node;
        while (currentNode != null) {
            if (currentNode.left == null && currentNode.right != null) {
                if (currentNode.right.height > 0) {
                    currentNode = this.balanceRightToLeft(currentNode, writer);
                    if (afterInsertion) {
                        return;
                    }
                } else {
                    currentNode.height = 1;
                }
            } else if (currentNode.right == null && currentNode.left != null) {
                if (currentNode.left.height > 0) {
                    currentNode = this.balanceLeftToRight(currentNode, writer);
                    if (afterInsertion) {
                        return;
                    }
                } else {
                    currentNode.height = 1;
                }
            } else if (currentNode.right != null && currentNode.left != null) {
                int diff = currentNode.left.height - currentNode.right.height;
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
                } else if (diff == -1) {
                    currentNode.height = currentNode.right.height + 1;
                } else {
                    currentNode.height = currentNode.left.height + 1;
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
    private AVLNode balanceLeftToRight(AVLNode node, BufferedWriter writer) throws IOException {
        // left subtree of node has at least height 1 (right might have a height of -1)
        AVLNode l = node.left;
        if (l.right == null || (l.left != null && l.right.height <= l.left.height)) {
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
    private AVLNode balanceRightToLeft(AVLNode node, BufferedWriter writer) throws IOException {
        // right subtree of node has at least height 1 (left might have a height of -1)
        AVLNode r = node.right; 
        if (r.left == null || (r.right != null && r.left.height <= r.right.height)) {
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
    private AVLNode delete(AVLNode node) {
        if (node.left != null && node.right != null) {
            // two children
            AVLNode tmp = node.right.minimum();
            AVLNode tmp2 = this.delete(tmp);
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
     * @param val The key to find. 
     * @return The highest node with the given key in this AVL-tree or null if the given key does not occur.
     */
    private AVLNode find(int val) {
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
     * Performs a left-rotation on the given node of this AVL-tree. If a writer is specified, it also prints the 
     * result to it.
     * @param node The node to rotate at.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     * @return The node at the original position of the specified node after rotation.
     */
    private AVLNode leftRotate(AVLNode node, BufferedWriter writer) throws IOException {
        AVLNode r = node.right;
        // move left subtree of r to be the new right subtree of node
        node.right = r.left;
        if (node.right != null) {
            node.right.father = node;
        }
        node.updateHeight();
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
        r.updateHeight();
        if (writer != null) {
            this.print("rotiere " + node.value + " nach links", writer);
        }
        return r;
    }

    /**
     * Prints this AVL-tree right under the given headline.
     * @param headline A headline.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void print(String headline, BufferedWriter writer) throws IOException  {
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
        writer.newLine();
        TikZUtils.printTikzEnd(writer);
        TikZUtils.printProtectedNewline(writer);
        this.printSamePageEnd(writer);
    }

    /**
     * Prints the beginning of a samepage environment.
     * @param headline The headline of this environment.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void printSamePageBeginning(String headline, BufferedWriter writer) throws IOException {
        if (this.getHeight() <= 0) {
            writer.write("\\begin{minipage}[t]{0.2 \\columnwidth}");
            writer.newLine();
        } else if (this.getHeight() < 9) {
            writer.write("\\begin{minipage}[t]{0." + (this.getHeight() + 1) + " \\columnwidth}");
            writer.newLine();
        } 
        if (headline != null && !"".equals(headline)) {
            writer.write(headline + "\\\\[-2ex]");
        }
        writer.newLine();
        writer.write("\\begin{center}");
        writer.newLine();
    }

    /**
     * Prints the end of a samepage environment.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void printSamePageEnd(BufferedWriter writer) throws IOException {
        writer.write("\\end{center}");
        writer.newLine();
        if (this.getHeight() < 9 ) {
            writer.write("\\end{minipage}");
            writer.newLine();
        }
    }

    /**
     * Prints vertical space
     * @param step The next evaluation step.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void printVerticalSpace(BufferedWriter writer) throws IOException {
        this.stepCounter += this.getHeight() + 1;
        if (this.stepCounter >= 10) {
            writer.newLine();
            writer.write("~\\\\");
            writer.newLine();
            writer.newLine();
            if (this.getHeight() <= 0) {
                this.stepCounter = 2;
            } else {
                this.stepCounter = this.getHeight() + 1;
            }
        }
    }

    /**
     * Removes the specified node from this AVL-tree. The node must exist in this tree. If a writer is specified, all 
     * necessary steps are printed to this writer.
     * @param node The node to remove.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void remove(AVLNode node, BufferedWriter writer) throws IOException {
        int value = node.value;
        if (this.root.left == null && this.root.right == null ) {
            assert (node == this.root) : "Tried to remove a node which does not exist in this AVL-tree!";
            this.root = null;
            if (writer != null) {
                this.print("entferne " + value, writer);
            }
        } else {
            AVLNode tmp = this.delete(node);
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
     * Replaces the old node by the new node from the perspective of the old node's father, but does not update 
     * the children of the new node.
     * @param oldNode The node to be replaced.
     * @param newNode The node by which oldNode is to be replaced.
     * @return If the new node is null, the father of the old node is returned. Otherwise, the new node is returned.
     */
    private AVLNode replace(AVLNode oldNode, AVLNode newNode) {
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
     * Sets the step counter back to 0.
     */
    private void resetStepCounter() {
        this.stepCounter = 0;
    }

    /**
     * Performs a right-rotation on the given node of this AVL-tree. If a writer is specified, it also prints the 
     * result to it.
     * @param node The node to rotate at.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     * @return The node at the original position of the specified node after rotation.
     */
    private AVLNode rightRotate(AVLNode node, BufferedWriter writer) throws IOException {
        AVLNode l = node.left;
        // move right subtree of l to be the new left subtree of node
        node.left = l.right;
        if (node.left != null) {
            node.left.father = node;
        }
        node.updateHeight();
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
        l.updateHeight();
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
    private void swap(AVLNode oldNode, AVLNode newNode) {
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

    /**
     * A node in an AVL-tree with an int value.
     * @author Florian Corzilius, Thomas Stroeder
     * @version 1.1.0
     */
    private static class AVLNode {

        /**
         * The father node.
         */
        private AVLNode father;

        /**
         * Cache for the height of this node.
         */
        private int height;

        /**
         * The left child.
         */
        private AVLNode left;

        /**
         * The right child.
         */
        private AVLNode right;

        /**
         * The value.
         */
        private int value;

        /**
         * Creates a node with the specified value and neither children nor a father.
         * @param val The node's value.
         */
        private AVLNode(int val) {
            this.father = null;
            this.left = null;
            this.right = null;
            this.value = val;
            this.height = 0;
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

        /**
         * Updates the height of the current node based on its children, but does not adapt the height of further 
         * ancestors.
         */
        public void updateHeight() {
            if (this.left == null && this.right == null) {
                this.height = 0;
            } else if (this.left != null && this.right == null) {
                this.height = this.left.height + 1;
            } else if (this.left == null && this.right != null) {
                this.height = this.right.height + 1;
            } else if (this.left.height > this.right.height) {
                this.height = this.left.height + 1;
            } else {
                this.height = this.right.height + 1;
            }
        }   

        /**
         * @return A node with the smallest key in the tree rooted in the current node. The returned node is the 
         *         left-most node in the tree rooted in the current node.
         */
        private AVLNode minimum() {
            AVLNode tmp = this;
            while (tmp.left != null) {
                tmp = tmp.left;
            }
            return tmp;
        }

    }

}
