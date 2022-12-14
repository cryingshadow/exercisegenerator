package exercisegenerator.structures.trees;

import java.io.*;

import exercisegenerator.*;

/**
 * Programm for creating solutions of exercises where elements have to be inserted into an Red-Black-Tree.
 */
public class IntRBTree {

    /**
     * Prints a protected whitespace and a line terminator to the specified writer.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private static void printProtectedNewline(final BufferedWriter writer) throws IOException {
        writer.write("~\\\\*\\vspace*{1ex}");
        Main.newLine(writer);
    }

    /**
     * Prints the end of the TikZ picture environment to the specified writer.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private static void printTikzEnd(final BufferedWriter writer) throws IOException {
        writer.write("\\end{tikzpicture}");
        Main.newLine(writer);
    }

    /**
     * The root of the Red-Black-Tree this exercise considers.
     */
    private RBNode root;

    /**
     * The step counter, which represents the number of printed trees in one exercise/solution.
     */
    private int stepCounter;

    /**
     * Creates an Red-Black-Tree exercise with an empty tree and the step counter being initially 1.
     */
    public IntRBTree() {
       this.root = null;
       this.stepCounter = 0;
    }

    /**
     * @param value The key to find.
     * @return The highest node with the given key in this Red-Black-Tree or null if the given key does not occur.
     */
    public RBNode find(final int value) {
        RBNode currentNode = this.root;
        while (currentNode != null) {
            if (value < currentNode.getValue()) {
                currentNode = currentNode.getLeft();
            } else if (value > currentNode.getValue()) {
                currentNode = currentNode.getRight();
            } else {
                // value == currentNode.getValue()
                return currentNode;
            }
        }
        return null;
    }

    public RBNode insert(final int value) {
        RBNode current = this.root;
        RBNode parent = null;
        while (current != null) {
            parent = current;
            if (value < current.getValue()) {
                current = current.getLeft();
            } else {
                current = current.getRight();
            }
        }
        // Einfuegen
        final RBNode node = new RBNode(value);
        node.setFather(parent);
        node.setLeft(null);
        node.setRight(null);
        if (parent == null) {
            // t war leer => neue Wurzel
            this.root = node;
        } else if (node.getValue() < parent.getValue()) {
            // richtige Seite ...
            parent.setLeft(node);
        } else {
            parent.setRight(node);
        }
        return node;
    }

    /**
     * @return True iff this tree is empty.
     */
    public boolean isEmpty() {
        return this.root == null;
    }

    /**
     * @param rootOfSubtree The root of the subtree to find the node in with the smallest key.
     * @return The node with the smallest key in the subtree starting in the given node. If the key
     *          occurs more than once, the one and only leave having this key is returned.
     */
    public RBNode minimum(final RBNode rootOfSubtree) {
        RBNode tmp = rootOfSubtree;
        while (tmp.getLeft() != null) {
            tmp = tmp.getLeft();
        }
        return tmp;
    }

    /**
     * Prints this Red-Black-Tree right under the given headline.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public void print(final String headline, final BufferedWriter writer) throws IOException  {
        this.printVerticalSpace(writer);
        this.printSamePageBeginning(headline, writer);
        this.printTikzBeginning(writer);
        if (this.root == null) {
            writer.write("\\Tree [.\\phantom{0} ];");
        } else if (this.root.getLeft() == null && this.root.getRight() == null ) {
            writer.write("\\Tree [." + this.root.getValue() + " ];");
        } else {
            writer.write("\\Tree");
            writer.write(this.toString(this.root));
        }
        Main.newLine(writer);
        IntRBTree.printTikzEnd(writer);
        IntRBTree.printProtectedNewline(writer);
        this.printSamePageEnd(writer);
    }

    /**
     * Adds a node to the Red-Black-Tree.
     * @param value The value of the new node to add.
     * @param writer The writer to send the output to.
     * @param write A Boolean, which is true if the Red-Black-Tree has to be printed after each rotation.
     */
    public void rbInsert(final int value, final BufferedWriter writer, final boolean write) throws IOException {
        final RBNode node = this.insert(value);
        if (node == this.root) {
            // If it is the first added node, make it black.
            node.setBlack(true);
            if (write) {
                this.print("f\\\"uge " + value + " ein", writer);
            }
        } else {
            // first, the added node is red
            node.setBlack(false);
            if (write) {
                this.print("f\\\"uge " + value + " ein", writer);
            }
            // restore the red-black-property
            this.balanceAfterInsert(node, writer, write);
        }
    }

    public void remove(final RBNode node, final BufferedWriter writer, final boolean write) throws IOException {
        if (node.getLeft() != null && node.getRight() != null) {
            // zwei Kinder
            final RBNode tmp = this.minimum(node.getRight());
            final int tmpValue = tmp.getValue();
            if (write) {
                Main.newLine(writer);
                Main.newLine(writer);
                writer.write("Wir l\\\"oschen den Knoten mit dem n\\\"achst gr\\\"o\\ss eren Wert " + tmpValue);
                writer.write(" und f\\\"ugen diesen Wert dann in den zu l\\\"oschenden Knoten ein.");
                Main.newLine(writer);
                Main.newLine(writer);
            }
            this.remove(tmp, writer, write);
            this.swap(node, tmp);
            tmp.setBlack(node.isBlack());
            if (write) {
                this.print("f\\\"uge " + tmpValue + " in den zu l\\\"oschenden Knoten ein", writer);
            }
        } else {
            if (node.getLeft() != null) {
                // ein Kind, links
                this.balanceAfterRemove(node, node.getLeft(), writer, write);
                final int value = node.getValue();
                this.replace(node, node.getLeft());
                if (write) {
                    this.print("ersetze " + value + " durch linkes Kind", writer);
                }
            } else {
                // ein Kind, oder kein Kind (node.right == null)
                if (node.getRight() == null) {
                    this.balanceAfterRemove(node, node, writer, write);
                } else {
                    this.balanceAfterRemove(node, node.getRight(), writer, write);
                }
                final int value = node.getValue();
                this.replace(node, node.getRight());
                if (write) {
                    if (this.isEmpty()) {
                        this.print("der Baum ist nun leer.", writer);
                    } else {
                        this.print("ersetze " + value + " durch rechtes Kind", writer);
                    }
                }
            }
        }
    }

    /**
     * Sets the step counter back to 1.
     */
    public void resetStepCounter() {
        this.stepCounter = 0;
    }

    /**
     * Adjusts the tree, which is supposed to violate the red-black-property on the left subtree.
     * @param node The node, which is contained by a subtree to balance. (If it is already balanced, nothing happens)
     * @param writer The writer to send the output to.
     * @param write A boolean, which is true if the Red-Black-Tree has to be printed after each rotation.
     */
    private RBNode adjust(final boolean isLeft, final RBNode node, final BufferedWriter writer, final boolean write) throws IOException {
        RBNode current = node;
        final RBNode uncle = current.getFather().getFather().getSucc(!isLeft);
        if (uncle != null && !uncle.isBlack()) {
            // grandfather
            current.getFather().getFather().setBlack(false);
            // father
            current.getFather().setBlack(true);
            // uncle
            uncle.setBlack(true);
            if (write) {
                this.print("Fall 1: umf\\\"arben", writer);
            }
            // checks red-red further to the top
            return current.getFather().getFather();
        } else {
            if (current == current.getFather().getSucc(!isLeft)) {
                // case 2
                current = this.rotate(isLeft, current.getFather(), writer, write, new String("Fall 2: "));
            } else {
                current = current.getFather();
            }
            // case 3
            current = this.rotate(!isLeft, current.getFather(), writer, write, new String("Fall 3: "));
            current.setBlack(true);
            current.getSucc(!isLeft).setBlack(false);
            if (write) {
                this.print("Fall 3: umf\\\"arben", writer);
            }
            // ready, node.getFather().isBlack() == true
            return current.getSucc(isLeft);
        }
    }

    /**
     * Balances the smallest subtree containing the given node such that it fulfills the red-black-property.
     * @param node The node, which is contained by a subtree to balance. (If it is already balanced, nothing happens)
     * @param writer The writer to send the output to.
     * @param write A boolean, which is true if the Red-Black-Tree has to be printed after each rotation.
     */
    private void balanceAfterInsert(final RBNode node, final BufferedWriter writer, final boolean write) throws IOException {
        RBNode current = node;
        while (current.getFather() != null && !current.getFather().isBlack()) {
            if (current.getFather() == current.getFather().getFather().getLeft()) {
                current = this.adjust(true, current, writer, write);
            } else {
                current = this.adjust(false, current, writer, write);
            }
        }
        if (!this.root.isBlack()) {
            this.root.setBlack(true);
            if (write) {
                this.print("Wurzel schwarz f\\\"arben", writer);
            }
        }
    }

    /**
     * Balances the smallest subtree containing the given node such that it fulfills the red-black-property.
     * @param node The node, which is contained by a subtree to balance. (If it is already balanced, nothing happens)
     * @param writer The writer to send the output to.
     */
    private void balanceAfterRemove(final RBNode old, final RBNode node, final BufferedWriter writer, final boolean write) throws IOException {
        if (!old.isBlack()) {
            return;
        }
        if (!node.isBlack()) {
            node.setBlack(true);
            if (write) {
                this.print("Knoten schwarz f\\\"arben", writer);
            }
        } else {
            RBNode current = old;
            while (current.getFather() != null && current.isBlack()) {
                if (current == current.getFather().getLeft()) {
                    current = this.rmAdjust(true, current, writer, write);
                } else {
                    current = this.rmAdjust(false, current, writer, write);
                }
            }
            if (!current.isBlack()) {
                current.setBlack(true);
                if (write) {
                    this.print("Knoten schwarz f\\\"arben", writer);
                }
            }
        }
    }

    /**
     * Prints the beginning of a samepage environment.
     * @param step The current evaluation step.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void printSamePageBeginning(final String headline, final BufferedWriter writer) throws IOException {
        if (this.root == null) {
            final int minipagewidth = 1;
            writer.write("\\begin{minipage}[t]{0." + minipagewidth + " \\columnwidth}");
            Main.newLine(writer);
        } else if (this.root.getHeight() < 9) {
            int minipagewidth = this.root.getHeight()+1;
            if (this.root == null || this.root.getHeight() == 0) {
                minipagewidth++;
            }
            writer.write("\\begin{minipage}[t]{0." + minipagewidth + " \\columnwidth}");
            Main.newLine(writer);
        }
        if (!headline.equals("")) {
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
        if (this.root == null || this.root.getHeight() < 9) {
            writer.write("\\end{minipage}");
            Main.newLine(writer);
        }
    }

    /**
     * Prints the beginning of the TikZ picture environment to the specified writer, including style settings for
     * arrays or trees.
     * @param arrayStyle Flag indicating whether to use the array or tree style.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void printTikzBeginning(final BufferedWriter writer) throws IOException {
        writer.write("\\begin{tikzpicture}");
        Main.newLine(writer);
        if (this.root == null || this.root.isBlack()) {
            writer.write("[every tree node/.style={rectangle,draw=black,thick,inner sep=5pt}");
        } else {
            writer.write("[every tree node/.style={circle,draw=gray,thick,inner sep=5pt}");
        }
        writer.write(", b/.style={rectangle,draw=black,thick,inner sep=5pt}, ");
        writer.write("r/.style={circle,draw=gray,thick,inner sep=5pt}, sibling distance=10pt, level distance=30pt, ");
        writer.write("edge from parent/.style={draw,edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]");
        Main.newLine(writer);
    }

    /**
     * Prints vertical space
     * @param step The next evaluation step.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void printVerticalSpace(final BufferedWriter writer) throws IOException {
        if (this.root == null) {
            this.stepCounter = 0;
        } else {
            this.stepCounter += this.root.getHeight()+1;
            if (this.root.getHeight() == 0) {
                this.stepCounter++;
            }
            if (this.stepCounter >= 10) {
                Main.newLine(writer);
                writer.write("~\\\\");
                Main.newLine(writer);
                Main.newLine(writer);
                this.stepCounter = this.root.getHeight()+1;
                if (this.root.getHeight() == 0) {
                    this.stepCounter++;
                }
            }
        }
    }

    /**
     * Replaces the first node by the second node.
     * @param oldNode The node to replace.
     * @param newNode The node to replace by.
     */
    private void replace(final RBNode oldNode, final RBNode newNode) {
        if (newNode != null) {
            // _newNode could be null
            newNode.setFather(oldNode.getFather());
        }
        if (oldNode.getFather() == null) {
            // root
            this.root = newNode;
        } else if (oldNode == oldNode.getFather().getLeft()) {
            // left child
            oldNode.getFather().setLeft(newNode);
        } else {
            // right child
            oldNode.getFather().setRight(newNode);
        }
    }

    /**
     * Adjusts the tree, which is supposed to violate the red-black-property on the left subtree.
     * @param node The node, which is contained by a subtree to balance. (If it is already balanced, nothing happens)
     * @param writer The writer to send the output to.
     */
    private RBNode rmAdjust(final boolean isLeft, final RBNode node, final BufferedWriter writer, final boolean write) throws IOException {
        RBNode brother = node.getFather().getSucc(!isLeft);
        if (!brother.isBlack()) {
            brother.setBlack(true);
            node.getFather().setBlack(false);
            if (write) {
                this.print("Fall 1: umf\\\"arben", writer);
            }
            this.rotate(isLeft, node.getFather(), writer, write, "Fall 1: ");
            brother = node.getFather().getSucc(!isLeft);
        }
        if (
            (brother.getLeft() == null || brother.getLeft().isBlack())
            && (brother.getRight() == null || brother.getRight().isBlack())
        ) {
            brother.setBlack(false);
            if (write) {
                this.print("Fall 2: umf\\\"arben", writer);
            }
            return node.getFather();
        } else {
            if (brother.getSucc(!isLeft) == null || brother.getSucc(!isLeft).isBlack()) {
                // case 3
                brother.getSucc(isLeft).setBlack(true);
                brother.setBlack(false);
                if (write) {
                    this.print("Fall 3: umf\\\"arben", writer);
                }
                this.rotate(!isLeft, brother, writer, write, "Fall 3: ");
                brother = node.getFather().getSucc(!isLeft);
            }
            // case 4
            brother.setBlack(node.getFather().isBlack());
            node.getFather().setBlack(true);
            brother.getSucc(!isLeft).setBlack(true);
            if (write) {
                this.print("Fall 4: umf\\\"arben", writer);
            }
            this.rotate(isLeft, node.getFather(), writer, write, "Fall 4: ");
            return this.root;
        }
    }

    /**
     * Performs a left-rotation on the given node of this Red-Black-Tree, if the first argument
     * of this function is true, and a right-rotation otherwise. If the given Boolean (last argument)
     * is true, it also sends the result to the given buffered-writer.
     * @param node The node to rotate at.
     * @param writer The writer to send the output to.
     * @param write A Boolean, which is true if the Red-Black-Tree has to be printed after rotation.
     * @throws IOException If some error occurs during output.
     * @return The node which is now at the position where the given node has been.
     */
    private RBNode rotate(final boolean isLeft, final RBNode node, final BufferedWriter writer, final boolean write, final String prefix)
    throws IOException {
        final RBNode succ = node.getSucc(!isLeft);
        // Baum B verschieben
        node.setSucc(!isLeft, succ.getSucc(isLeft));
        if (node.getSucc(!isLeft) != null) {
            node.getSucc(!isLeft).setFather(node);
        }
        // node2 wieder einhaengen
        succ.setFather(node.getFather());
        if (node.getFather() == null) {
            // node war die Wurzel
            this.root = succ;
        } else if (node == node.getFather().getSucc(true)) {
            // war linkes Kind
            succ.getFather().setSucc(true, succ);
        } else {
            // war rechtes Kind
            succ.getFather().setSucc(false, succ);
        }
        // node einhaengen
        succ.setSucc(isLeft, node);
        node.setFather(succ);
        if (write) {
            if (isLeft) {
                this.print(prefix + "rotiere " + node.getValue() + " nach links", writer);
            } else {
                this.print(prefix + "rotiere " + node.getValue() + " nach rechts", writer);
            }
        }
        return succ;
    }

    private void swap(final RBNode oldNode, final RBNode newNode) {
        // copy the left sub-tree
        newNode.setLeft(oldNode.getLeft());
        if (newNode.getLeft() != null) {
            newNode.getLeft().setFather(newNode);
        }
        // copy the right sub-tree
        newNode.setRight(oldNode.getRight());
        if (newNode.getRight() != null) {
            newNode.getRight().setFather(newNode);
        }
        this.replace(oldNode, newNode);
    }

    /**
     * Gives the string representation of this Red-Black-Tree starting at
     * the given node. The representation is a valid latex-qtree.
     * @return The string representation of this Red-Black-Tree
     */
    private String toString(final RBNode node) {
        String result = new String("");
        if (node.getLeft() == null && node.getRight() == null) {
            if (node.isBlack()) {
                result += " \\node[b]{";
            } else {
                result += " \\node[r]{";
            }
            result += " " + node.getValue() + " };";
        } else {
            if (node.isBlack()) {
                result += " [.\\node[b]{";
            } else {
                result += " [.\\node[r]{";
            }
            result += node.getValue() + " };";
            if (node.getLeft() != null) {
                result += this.toString(node.getLeft());
            } else {
                result += " \\edge[draw=none];\\node[draw=none]{};";
            }
            if (node.getRight() != null) {
                result += this.toString(node.getRight());
            } else {
                result += " \\edge[draw=none];\\node[draw=none]{};";
            }
            result += " ]";
        }
        return result;
    }

}
