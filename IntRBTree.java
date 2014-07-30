import java.io.*;
import java.util.*;

class RBNode {
    private RBNode mFather;
    private RBNode mLeft;
    private RBNode mRight;
    private int mValue;
    private int mHeight;
    private boolean mIsBlack;
    
    RBNode(int _value) {
        this.mFather = null;
        this.mLeft = null;
        this.mRight = null;
        this.mValue = _value;
        this.mHeight = 0;
        this.mIsBlack = true;
    }
    
    int getValue() {
        return this.mValue;
    }
    
    void setValue(int _value) {
        this.mValue = _value;
    }
    
    RBNode getFather() {
        return this.mFather;
    }
    
    void setFather(RBNode _father) {
        this.mFather = _father;
    }
    
    RBNode getLeft() {
        return this.mLeft;
    }
    
    void setLeft(RBNode _left) {
        this.mLeft = _left;
        this.updateHeight();
    }
    
    RBNode getRight() {
        return this.mRight;
    }
    
    void setRight(RBNode _right) {
        this.mRight = _right;
        this.updateHeight();
    }
    
    RBNode getSucc(boolean _left) {
        if (_left) {
            return this.mLeft;
        } else {
            return this.mRight;
        }
    }
    
    void setSucc(boolean _left, RBNode _node) {
        if (_left) {
            this.mLeft = _node;
        } else {
            this.mRight = _node;
        }
        this.updateHeight();
    }
    
    int getHeight() {
        return this.mHeight;
    }
    
    void updateHeight() {
        if (this.mLeft == null && this.mRight == null) {
            this.mHeight = 0;
        } else if (this.mLeft != null && this.mRight == null) {
            this.mHeight = this.mLeft.getHeight() + 1;
        } else if (this.mLeft == null && this.mRight != null) {
            this.mHeight = this.mRight.getHeight() + 1;
        } else if (this.mLeft.getHeight() > this.mRight.getHeight()) {
            this.mHeight = this.mLeft.getHeight() + 1;
        } else {
            this.mHeight = this.mRight.getHeight() + 1;
        }
        if (this.mFather != null) {
            this.mFather.updateHeight();
        }
    }
    
    boolean isBlack() {
        return this.mIsBlack;
    }
    
    void setBlack(boolean _black) {
        this.mIsBlack = _black;
    }
}

/**
 * Programm for creating solutions of exercises where elements have to be inserted into an Red-Black-Tree.
 * @author Thomas Ströder
 * @author Florian Corzilius
 * @version $Id$
 */
public class IntRBTree {
    /// The root of the Red-Black-Tree this exercise considers.
    private RBNode mRoot;
    /// The step counter, which represents the number of printed trees in one exercise/solution.
    private int mStepCounter;
    
    public boolean isEmpty() {
        return this.mRoot == null;
    }
    
    /**
     * Creates an Red-Black-Tree exercise with an empty tree and the step counter being initially 1.
     */
    public IntRBTree() {
       this.mRoot = null;
       this.mStepCounter = 0;
    }
    
    /**
     * Sets the step counter back to 1.
     */
    public void resetStepCounter() {
        this.mStepCounter = 0;
    }
    
    public RBNode insert(int _value) {
        RBNode root = this.mRoot;
        RBNode parent = null; 
        while (root != null) {
            parent = root;
            if (_value < root.getValue()) {
                root = root.getLeft(); 
            } else {
                root = root.getRight();
            }
        } // Einfuegen
        RBNode node = new RBNode(_value);
        node.setFather(parent); 
        node.setLeft(null); 
        node.setRight(null); 
        if (parent == null) { // t war leer => neue Wurzel
            this.mRoot = node;
        } else if (node.getValue() < parent.getValue()) { // richtige Seite ...
            parent.setLeft(node);
        } else {
            parent.setRight(node);
        }
        return node;
    }
    
    /**
     * Adds a node to the Red-Black-Tree.
     * @param The node to add.
     * @param writer The writer to send the output to.
     * @param writer A Boolean, which is true if the Red-Black-Tree has to be printed after each rotation.
     */
    void rbInsert(int _value, BufferedWriter writer, boolean write) throws IOException {
        RBNode node = this.insert(_value);
        if (node == this.mRoot) {
            node.setBlack(true); // If it is the first added node, make it black.
            if (write) {
                this.print("f\\\"uge " + _value + " ein", writer);
            }
        } else {
            node.setBlack(false); // first, the added node is red
            if (write) {
                this.print("f\\\"uge " + _value + " ein", writer);
            }
            // restore the red-black-property 
            this.balanceAfterInsert(node, writer, write);
        }
    }
    
    /**
     * Balances the smallest subtree containing the given node such that it fulfills the red-black-property.
     * @param The node, which is contained by a subtree to balance. (If it is already balanced, nothing happens)
     * @param writer The writer to send the output to.
     * @param writer A Boolean, which is true if the Red-Black-Tree has to be printed after each rotation.
     */
    void balanceAfterInsert(RBNode _node, BufferedWriter writer, boolean write) throws IOException {
        RBNode node = _node;
        while (node.getFather() != null && !node.getFather().isBlack()) {
            if (node.getFather() == node.getFather().getFather().getLeft()) {
                node = this.adjust(true, node, writer, write);
            } else {
                node = this.adjust(false, node, writer, write);
            }
        }
        if( !this.mRoot.isBlack() ) {
            this.mRoot.setBlack(true);
            if (write) {
                this.print("Wurzel schwarz f\\\"arben", writer);
            }
        }
    }
    
    /**
     * Adjusts the tree, which is supposed to violate the red-black-property on the left subtree.
     * @param The node, which is contained by a subtree to balance. (If it is already balanced, nothing happens)
     * @param writer The writer to send the output to.
     * @param writer A Boolean, which is true if the Red-Black-Tree has to be printed after each rotation.
     */
    RBNode adjust(boolean _left, RBNode _node, BufferedWriter writer, boolean write) throws IOException {
        RBNode node = _node;
        RBNode uncle = node.getFather().getFather().getSucc(!_left);
        if (uncle != null && !uncle.isBlack()) {
            node.getFather().getFather().setBlack(false); // grandfather
            node.getFather().setBlack(true); // father
            uncle.setBlack(true); // uncle
            if (write) {
                this.print("Fall 1: umf\\\"arben", writer);
            }
            return node.getFather().getFather(); // checks red-red further to the top 
        } else {
            if (node == node.getFather().getSucc(!_left)) {
                // case 2
                node = this.rotate(_left, node.getFather(), writer, write, new String("Fall 2: "));
            }
            else
            {
                node = node.getFather();
            }
            // case 3
            node = this.rotate(!_left, node.getFather(), writer, write, new String("Fall 3: "));
            node.setBlack(true);
            node.getSucc(!_left).setBlack(false);
            if (write) {
                this.print("Fall 3: umf\\\"arben", writer);
            }
            return node.getSucc(_left); // ready, node.getFather().isBlack() == true
        }
    }
    
    /**
     * @param _value The key to find. 
     * @return The highest node with the given key in this Red-Black-Tree or null if the given key does not occur.
     */
    public RBNode find(int _value) {
        RBNode currentNode = this.mRoot;
        while (currentNode != null) {
            if (_value < currentNode.getValue()) {
                currentNode = currentNode.getLeft();
            } else if (_value > currentNode.getValue()) {
                currentNode = currentNode.getRight();
            } else { // _value == currentNode.getValue()
                return currentNode;
            }
        }
        return null;
    }
    
    /**
     * @param The root of the subtree to find the node in with the smallest key.
     * @return The node with the smallest key in the subtree starting in the given node. If the key
     *          occurs more than once, the one and only leave having this key is returned.
     */
    public RBNode minimum(RBNode _rootOfSubtree) {
        RBNode tmp = _rootOfSubtree;
        while (tmp.getLeft() != null) {
            tmp = tmp.getLeft();
        }
        return tmp;
    }   
    
    /**
     * Replaces the first node by the second node.
     * @param _oldNode The node to replace.
     * @param _newNode The node to replace by.
     */
    void replace(RBNode _oldNode, RBNode _newNode) {
        if (_newNode != null) { // _newNode could be null
            _newNode.setFather(_oldNode.getFather());
        }
        if (_oldNode.getFather() == null) { // root
            this.mRoot = _newNode;
        } else if (_oldNode == _oldNode.getFather().getLeft()) {
            // left child
            _oldNode.getFather().setLeft(_newNode);
        } else { // right child
            _oldNode.getFather().setRight(_newNode);
        }
    }
    
    void swap (RBNode _oldNode, RBNode _newNode) {
        _newNode.setLeft(_oldNode.getLeft()); // copy the left sub-tree
        if (_newNode.getLeft() != null) {
            _newNode.getLeft().setFather(_newNode);
        }
        //
        _newNode.setRight(_oldNode.getRight());
        if (_newNode.getRight() != null) {
            _newNode.getRight().setFather(_newNode);
        }
        this.replace(_oldNode, _newNode);
    }
    
    /**
     * Adjusts the tree, which is supposed to violate the red-black-property on the left subtree.
     * @param The node, which is contained by a subtree to balance. (If it is already balanced, nothing happens)
     * @param writer The writer to send the output to.
     */
    RBNode rmAdjust(boolean _left, RBNode _node, BufferedWriter writer, boolean write) throws IOException {
        RBNode node = _node;
        RBNode brother = node.getFather().getSucc(!_left);
        if (!brother.isBlack()) {
            brother.setBlack(true);
            node.getFather().setBlack(false);
            if (write) {
                this.print("Fall 1: umf\\\"arben", writer);
            }
            this.rotate(_left, node.getFather(), writer, write, "Fall 1: ");
            brother = node.getFather().getSucc(!_left);
        }
        if ((brother.getLeft() == null || brother.getLeft().isBlack()) && (brother.getRight() == null || brother.getRight().isBlack()) ) {
            brother.setBlack(false);
            if (write) {
                this.print("Fall 2: umf\\\"arben", writer);
            }
            return node.getFather();
        } else {
            if (brother.getSucc(!_left) == null || brother.getSucc(!_left).isBlack()) {
                // case 3
                brother.getSucc(_left).setBlack(true);
                brother.setBlack(false);
                if (write) {
                    this.print("Fall 3: umf\\\"arben", writer);
                }
                this.rotate(!_left, brother, writer, write, "Fall 3: ");
                brother = node.getFather().getSucc(!_left);
            }
            // case 4
            brother.setBlack(node.getFather().isBlack());
            node.getFather().setBlack(true);
            brother.getSucc(!_left).setBlack(true);
            if (write) {
                this.print("Fall 4: umf\\\"arben", writer);
            }
            this.rotate(_left, node.getFather(), writer, write, "Fall 4: ");
            return this.mRoot;
        }
    }
    
    /**
     * Balances the smallest subtree containing the given node such that it fulfills the red-black-property.
     * @param The node, which is contained by a subtree to balance. (If it is already balanced, nothing happens)
     * @param writer The writer to send the output to.
     */
    void balanceAfterRemove(RBNode _old, RBNode _node, BufferedWriter writer, boolean write) throws IOException {
        if (!_old.isBlack()) { return; }
        if (!_node.isBlack()) {
            _node.setBlack( true );
            if (write) {
                this.print("Knoten schwarz f\\\"arben", writer);
            }
        } else {
            RBNode node = _old;
            while (node.getFather() != null && node.isBlack()) {
                if (node == node.getFather().getLeft()) {
                    node = this.rmAdjust(true, node, writer, write);
                } else {
                    node = this.rmAdjust(false, node, writer, write);
                }
            }
            if (!node.isBlack()) {
                node.setBlack( true );
                if (write) {
                    this.print("Knoten schwarz f\\\"arben", writer);
                }
            }
        }
    }
    
    public void remove(RBNode _node, BufferedWriter writer, boolean write) throws IOException {
        RBNode node = _node;
        if (node.getLeft() != null && node.getRight() != null) { // zwei Kinder 
            RBNode tmp = this.minimum(node.getRight());
            int tmpValue = tmp.getValue();
            if (write) {
                writer.newLine();
                writer.newLine();
                writer.write("Wir l\\\"oschen den Knoten mit dem n\\\"achst gr\\\"o\\ss eren Wert " + tmpValue);
                writer.write(" und f\\\"ugen diesen Wert dann in den zu l\\\"oschenden Knoten ein.");
                writer.newLine();
                writer.newLine();
            }
            this.remove(tmp, writer, write);
            this.swap(node, tmp);
            tmp.setBlack(node.isBlack());
            if (write) {
                this.print("f\\\"uge " + tmpValue + " in den zu l\\\"oschenden Knoten ein", writer);
            }
        } else {
            if (node.getLeft() != null) { // ein Kind, links 
                this.balanceAfterRemove(node, node.getLeft(), writer, write);
                int value = node.getValue();
                this.replace(node, node.getLeft());
                if (write) {
                    this.print("ersetze " + value + " durch linkes Kind", writer);
                }
            } else { // ein Kind, oder kein Kind (node.right == null) 
                if (node.getRight() == null) {
                    this.balanceAfterRemove(node, node, writer, write);
                } else {
                    this.balanceAfterRemove(node, node.getRight(), writer, write);
                }
                int value = node.getValue();
                this.replace(node, node.getRight());
                if (write) {
                    if(this.isEmpty()) {
                        this.print("der Baum ist nun leer.", writer);
                    } else {
                        this.print("ersetze " + value + " durch rechtes Kind", writer);
                    }
                }
            }
        }
    }
    
    /**
     * Performs a left-rotation on the given node of this Red-Black-Tree, if the first argument
     * of this function is true, and a right-rotation otherwise. If the given Boolean (last argument)
     * is true, it also sends the result to the given buffered-writer.
     * @param _node The node to rotate at.
     * @param writer The writer to send the output to.
     * @param write A Boolean, which is true if the Red-Black-Tree has to be printed after rotation.
     * @throws IOException If some error occurs during output.
     * @return The node which is now at the position where the given node has been.
     */
    private RBNode rotate(boolean _left, RBNode _node, BufferedWriter writer, boolean write, String _prefix) throws IOException {
        RBNode node2 = _node.getSucc(!_left);
        // Baum B verschieben
        _node.setSucc(!_left, node2.getSucc(_left));
        if (_node.getSucc(!_left) != null) {
            _node.getSucc(!_left).setFather(_node);
        }
        // node2 wieder einhängen
        node2.setFather(_node.getFather());
        if (_node.getFather() == null) { // _node war die Wurzel
            this.mRoot = node2;
        } else if (_node == _node.getFather().getSucc(true)) { // war linkes Kind
            node2.getFather().setSucc(true, node2);
        } else { // war rechtes Kind
            node2.getFather().setSucc(false, node2);
        }
        // _node einhängen
        node2.setSucc(_left, _node);
        _node.setFather(node2);
        if (write) {
            if (_left) {
                this.print(_prefix + "rotiere " + _node.getValue() + " nach links", writer);
            } else {
                this.print(_prefix + "rotiere " + _node.getValue() + " nach rechts", writer);
            }
        }
        return node2;
    }
    
    /**
     * Gives the string representation of this Red-Black-Tree starting at
     * the given node. The representation is a valid latex-qtree.
     * @return The string representation of this Red-Black-Tree
     */
    private String toString(RBNode _node) {
        String result = new String("");
        if (_node.getLeft() == null && _node.getRight() == null) {
            if (_node.isBlack()) {
                result += " \\node[b]{";
            } else {
                result += " \\node[r]{";
            }
            result += " " + _node.getValue() + " };";
        } else {
            if (_node.isBlack()) {
                result += " [.\\node[b]{";
            } else {
                result += " [.\\node[r]{";
            }
            result += _node.getValue() + " };";
            if (_node.getLeft() != null) {
                result += this.toString(_node.getLeft());
            } else {
                result += " \\edge[draw=none];\\node[draw=none]{};";
            }
            if (_node.getRight() != null) {
                result += this.toString(_node.getRight());
            } else {
                result += " \\edge[draw=none];\\node[draw=none]{};";
            }
            result += " ]";
        }
        return result;
    }
    
    /**
     * Prints this Red-Black-Tree right under the given headline.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void print(String _headline, BufferedWriter writer) throws IOException  {
        this.printVerticalSpace(writer);
        this.printSamePageBeginning(_headline, writer);
        this.printTikzBeginning(writer);
        if (this.mRoot == null) {
            writer.write("\\Tree [.\\phantom{0} ];");
        } else if (this.mRoot.getLeft() == null && this.mRoot.getRight() == null ) {
            writer.write("\\Tree [." + this.mRoot.getValue() + " ];");
        } else {
            writer.write("\\Tree");
            writer.write(this.toString(this.mRoot));
        }
        writer.newLine();
        this.printTikzEnd(writer);
        this.printProtectedNewline(writer);
        this.printSamePageEnd(writer);
    }
    
    /**
     * Prints a protected whitespace and a line terminator to the specified writer.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void printProtectedNewline(BufferedWriter writer) throws IOException {
        writer.write("~\\\\*\\vspace*{1ex}");
        writer.newLine();
    }

    /**
     * Prints the beginning of a samepage environment.
     * @param step The current evaluation step.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void printSamePageBeginning(String _headline, BufferedWriter writer) throws IOException {
        if (this.mRoot == null) {
            int minipagewidth = 1;
            writer.write("\\begin{minipage}[t]{0." + minipagewidth + " \\columnwidth}");
            writer.newLine();
        } else if (this.mRoot.getHeight() < 9) {
            int minipagewidth = this.mRoot.getHeight()+1;
            if (this.mRoot == null || this.mRoot.getHeight() == 0) {
                minipagewidth++;
            }
            writer.write("\\begin{minipage}[t]{0." + minipagewidth + " \\columnwidth}");
            writer.newLine();
        } 
        if (!_headline.equals("")) {
            writer.write(_headline + "\\\\[-2ex]");
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
        if (this.mRoot == null || this.mRoot.getHeight() < 9) {
            writer.write("\\end{minipage}");
            writer.newLine();
        }
    }

    /**
     * Prints the beginning of the TikZ picture environment to the specified writer, including style settings for 
     * arrays or trees.
     * @param arrayStyle Flag indicating whether to use the array or tree style.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void printTikzBeginning(BufferedWriter writer) throws IOException {
        writer.write("\\begin{tikzpicture}");
        writer.newLine();
        if (this.mRoot == null || this.mRoot.isBlack()) {
            writer.write("[every tree node/.style={rectangle,draw=black,thick,inner sep=5pt}");
        } else {
            writer.write("[every tree node/.style={circle,draw=gray,thick,inner sep=5pt}");
        }
        writer.write(", b/.style={rectangle,draw=black,thick,inner sep=5pt}, r/.style={circle,draw=gray,thick,inner sep=5pt}, ");
        writer.write("sibling distance=10pt, level distance=30pt, edge from parent/.style=");
        writer.write("{draw, edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]");
        writer.newLine();
    }

    /**
     * Prints the end of the TikZ picture environment to the specified writer.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void printTikzEnd(BufferedWriter writer) throws IOException {
        writer.write("\\end{tikzpicture}");
        writer.newLine();
    }

    /**
     * Prints vertical space
     * @param step The next evaluation step.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void printVerticalSpace(BufferedWriter writer) throws IOException {
        if (this.mRoot == null) {
            this.mStepCounter = 0;
        } else {
            this.mStepCounter += this.mRoot.getHeight()+1;
            if (this.mRoot.getHeight() == 0) {
                this.mStepCounter++;
            }
            if (this.mStepCounter >= 10) {
                writer.newLine();
                writer.write("~\\\\");
                writer.newLine();
                writer.newLine();
                this.mStepCounter = this.mRoot.getHeight()+1;
                if (this.mRoot.getHeight() == 0) {
                    this.mStepCounter++;
                }
            }
        }
    }
    
    /**
     * Performs the operations specified by <code>construction</code> and <code>ops</code> on the specified RB-tree and
     * prints the results to the specified writer. The <code>construction</code> operations are not displayed.
     * @param tree The RB-tree.
     * @param ops The operations.
     * @param construction The operations used to construct the start structure.
     * @param writer The writer for the solution.
     * @param writerSpace The writer for the tree to start with (the one reached after the <code>construction</code> 
     *                    operations). May be null if this tree should not be displayed separately.
     * @throws IOException If some error occurs during output.
     */
    public static void rbtree(
        IntRBTree tree,
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
                tree.rbInsert(operation.x, writer, false);
            } else {
                RBNode toRemove = tree.find(operation.x);
                if (toRemove != null) {
                    tree.remove(toRemove, writer, false);
                }
            }
        }
        if (writerSpace != null) {
            if (ops.size() > 1) {
                if (tree.isEmpty()) {
                    writerSpace.write(
                        "F\\\"uhren Sie die folgenden Operationen beginnend mit einem anfangs leeren"
                        + " Rot-Schwarz-Baum aus und geben Sie die entstehenden B\\\"aume nach jeder \\emphasize{Einf\\\"uge-"
                        + " und L\\\"oschoperation}, jeder \\emphasize{Rotation} und jeder \\emphasize{Umf\\\"arbung} an. Beachten Sie, dass rote Knoten rund"
                        + " und schwarze Knoten eckig dargestellt werden.\\\\\\\\"
                    );
                    writerSpace.newLine();
                } else {
                    writerSpace.write(
                        "Betrachten Sie den folgenden Rot-Schwarz-Baum:\\\\[2ex]"
                    );
                    writerSpace.newLine();
                    writerSpace.newLine();
                    tree.print("", writerSpace);
                    writerSpace.newLine();
                    writerSpace.newLine();
                    writerSpace.write("\\vspace*{1ex}");
                    writerSpace.newLine();
                    writerSpace.write(
                        "F\\\"uhren Sie beginnend mit diesem Rot-Schwarz-Baum die folgenden Operationen aus "
                        + "und geben Sie die entstehenden B\\\"aume nach jeder \\emphasize{Einf\\\"uge- "
                        + "und L\\\"oschoperation}, jeder \\emphasize{Rotation} und jeder \\emphasize{Umf\\\"arbung} an. Beachten Sie, dass rote Knoten rund"
                        + " und schwarze Knoten eckig dargestellt werden.\\\\\\\\"
                    );
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
                        writerSpace.write(
                            "F\\\"ugen Sie den Wert "
                            + op.x
                            + " in einen leeren \\textbf{Rot-Schwarz-Baum} ein und geben Sie die entstehenden B\\\"aume "
                            + "nach");
                        writerSpace.newLine(); 
                        writerSpace.write("\\begin{itemize}");
                        writerSpace.newLine(); 
                        writerSpace.write("    \\item jeder \\textbf{Einf\\\"ugeoperation},");
                        writerSpace.newLine(); 
                        writerSpace.write("    \\item jeder \\textbf{Rotation} sowie");
                        writerSpace.newLine(); 
                        writerSpace.write("    \\item jeder \\textbf{Umf\\\"arbung} an.");
                        writerSpace.newLine(); 
                        writerSpace.write("\\end{itemize}");
                        writerSpace.newLine(); 
                        writerSpace.write(" Beachten Sie, dass rote Knoten rund und schwarze Knoten eckig dargestellt werden.");
                    } else {
                        // this case is nonsense 
                        return;
                    }
                } else {
                    if (op.y) {
                        writerSpace.write(
                            "F\\\"ugen Sie den Wert "
                            + op.x
                            + " in den folgenden \\textbf{Rot-Schwarz-Baum} ein und geben Sie die entstehenden B\\\"aume "
                            + "nach");
                            writerSpace.newLine(); 
                            writerSpace.write("\\begin{itemize}");
                            writerSpace.newLine(); 
                            writerSpace.write("    \\item jeder \\textbf{Einf\\\"ugeoperation},");
                            writerSpace.newLine(); 
                            writerSpace.write("    \\item jeder \\textbf{Rotation} sowie");
                            writerSpace.newLine(); 
                            writerSpace.write("    \\item jeder \\textbf{Umf\\\"arbung} an.");
                            writerSpace.newLine(); 
                            writerSpace.write("\\end{itemize}");
                            writerSpace.newLine(); 
                            writerSpace.write(" Beachten Sie, dass rote Knoten rund und schwarze Knoten eckig dargestellt werden.\\\\[2ex]"
                        );
                    } else {
                        writerSpace.write(
                            "L\\\"oschen Sie den Wert "
                            + op.x
                            + " aus dem folgenden \\textbf{Rot-Schwarz-Baum} und geben Sie die entstehenden B\\\"aume "
                            + "nach");
                            writerSpace.newLine(); 
                            writerSpace.write("\\begin{itemize}");
                            writerSpace.newLine(); 
                            writerSpace.write("    \\item jeder \\textbf{L\\\"oschoperation},");
                            writerSpace.newLine(); 
                            writerSpace.write("    \\item jeder \\textbf{Rotation} sowie");
                            writerSpace.newLine(); 
                            writerSpace.write("    \\item jeder \\textbf{Umf\\\"arbung} an.");
                            writerSpace.newLine(); 
                            writerSpace.write("\\end{itemize}");
                            writerSpace.newLine(); 
                            writerSpace.write(" Beachten Sie, dass rote Knoten rund und schwarze Knoten eckig dargestellt werden.\\\\[2ex]"
                        );
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
                tree.rbInsert(operation.x, writer, true);
            } else {
                RBNode toRemove = tree.find(operation.x);
                if (toRemove != null) {
                    tree.remove(toRemove, writer, true);
                } else {
                    tree.print(operation.x + " kommt nicht vor", writer);
                }
            }
        }
    }
}
