import java.io.*;
import java.util.*;

class AVLNode {
    private AVLNode mFather;
    private AVLNode mLeft;
    private AVLNode mRight;
    private int mValue;
    private int mHeight;
    
    AVLNode(int _value) {
        this.mFather = null;
        this.mLeft = null;
        this.mRight = null;
        this.mValue = _value;
        this.mHeight = 0;
    }
    
    int getValue() {
        return this.mValue;
    }
    
    void setValue(int _value) {
        this.mValue = _value;
    }
    
    AVLNode getFather() {
        return this.mFather;
    }
    
    void setFather(AVLNode _father) {
        this.mFather = _father;
    }
    
    AVLNode getLeft() {
        return this.mLeft;
    }
    
    void setLeft(AVLNode _left) {
        this.mLeft = _left;
        this.updateHeight();
    }
    
    AVLNode getRight() {
        return this.mRight;
    }
    
    void setRight(AVLNode _right) {
        this.mRight = _right;
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
}

/**
 * Programm for creating solutions of exercises where elements have to be inserted into an AVL-tree.
 * @author Thomas Ströder
 * @author Florian Corzilius
 * @version $Id$
 */
public class IntAVLTree {
    /// The root of the AVL-tree this exercise considers.
    private AVLNode mRoot;
    /// The step counter, which represents the number of printed trees in one exercise/solution.
    private int mStepCounter;
    
    /**
     * Creates an AVL-tree exercise with an empty tree and the step counter being initially 1.
     */
    public IntAVLTree() {
       this.mRoot = null;
       this.mStepCounter = 0;
    }
    
    public boolean isEmpty() {
        return this.mRoot == null;
    }
    
    public AVLNode root() {
        return this.mRoot;
    }
    
    public int getHeight() {
        if (this.mRoot == null) {
            return -1;
        } else {
            return this.mRoot.getHeight();
        }
    }
    
    /**
     * Sets the step counter back to 1.
     */
    public void resetStepCounter() {
        this.mStepCounter = 0;
    }
    
    /**
     * @param _value The key to find. 
     * @return The highest node with the given key in this AVL-tree or null if the given key does not occur.
     */
    public AVLNode find(int _value) {
        AVLNode currentNode = this.mRoot;
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
    public AVLNode minimum(AVLNode _rootOfSubtree) {
        AVLNode tmp = _rootOfSubtree;
        while (tmp.getLeft() != null) {
            tmp = tmp.getLeft();
        }
        return tmp;
    }   
    
    public void insert(int _value, BufferedWriter writer, boolean write) throws IOException {
        AVLNode root = this.mRoot;
        AVLNode parent = null; 
        while (root != null) {
            parent = root;
            if (_value < root.getValue()) {
                root = root.getLeft(); 
            } else {
                root = root.getRight();
            }
        } // Einfuegen
        AVLNode node = new AVLNode(_value);
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
        if (write) {
            this.print("f\\\"uge " + _value + " ein", writer);
        }
        // Balanciere den Baum
        this.balance(node, true, writer, write);
    }
    
    /**
     * Replaces the first node by the second node.
     * @param _oldNode The node to replace.
     * @param _newNode The node to replace by.
     */
    AVLNode replace (AVLNode _oldNode, AVLNode _newNode) {
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
        return _newNode;
    }
    
    void swap (AVLNode _oldNode, AVLNode _newNode) {
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
    
    public void del(AVLNode _node, BufferedWriter writer, boolean write) throws IOException {
        int value = _node.getValue();
        AVLNode father = _node.getFather();
        if (this.mRoot.getLeft() == null && this.mRoot.getRight() == null ) {
            if (_node == this.mRoot) {
                this.mRoot = null;
                if (write) {
                    this.print("entferne " + value, writer);
                }
            }
        } else {
            AVLNode tmp = this.remove(_node);
            if (write) {
                this.print("entferne " + value, writer);
            }
            // Balance the tree
            if (tmp != null) {
                this.balance(tmp, false, writer, write);
            } else if (father != null) {
                this.balance(father, false, writer, write);
            }
        }
    }
    
    public AVLNode remove(AVLNode _node) throws IOException {
        if (_node.getLeft() != null && _node.getRight() != null) { // zwei Kinder 
            AVLNode tmp = this.minimum(_node.getRight());
            AVLNode tmp2 = this.remove(tmp);
            this.swap(_node, tmp);
            return tmp2;
        } else {
            if (_node.getLeft() != null) { // ein Kind, links 
                return this.replace(_node, _node.getLeft());
            } else { // ein Kind, oder kein Kind (node.right == null) 
                return this.replace(_node, _node.getRight());
            }
        }
    }
    
    /**
     * Balances the tree. The given node is a leaf and possibly part of an unbalanced subtree of this AVL-tree.
     * This method checks for every node on the path from the given node to the root, whether it is the root
     * of an unbalanced subtree and then balances it. If the given Boolean _afterInsertion is false, it continues
     * this until reaching the root. 
     * @param _node The unbalanced node.
     * @param _afterInsertion A Boolean, which is true if this method only need to balance at most once in order
     *                        to obtain a balanced tree.
     * @param writer The writer to send the output to.
     * @param writer A Boolean, which is true if the AVL-tree has to be printed after each rotation.
     * @throws IOException If some error occurs during output.
     * @return The node which is now at the position where the given node has been.
     */
    private void balance(AVLNode _node, boolean _afterInsertion, BufferedWriter writer, boolean write) throws IOException {
        AVLNode currentNode = _node;
        while (true) {
            if (currentNode.getLeft() == null && currentNode.getRight() != null && currentNode.getRight().getHeight() > 0) {
                currentNode = this.balanceRightToLeft(currentNode, writer, write);
                if (_afterInsertion) {
                    return;
                }
            } else if (currentNode.getRight() == null && currentNode.getLeft() != null && currentNode.getLeft().getHeight() > 0) {
                currentNode = this.balanceLeftToRight(currentNode, writer, write);
                if (_afterInsertion) {
                    return;
                }
            } else if (currentNode.getRight() != null && currentNode.getLeft() != null) {
                int diff = currentNode.getLeft().getHeight() - currentNode.getRight().getHeight();
                if (java.lang.Math.abs(diff) > 1) {
                    if (diff > 0) { // Linker Teilbaum groesser
                        currentNode = this.balanceLeftToRight(currentNode, writer, write);
                    } else { // Rechter Teilbaum groesser
                        currentNode = this.balanceRightToLeft(currentNode, writer, write);
                    }
                    if (_afterInsertion) {
                        return;
                    }
                }
            }
            if (currentNode != this.mRoot) {
                currentNode = currentNode.getFather();
            } else {
                return;
            }
        }
    }
    
    /**
     * Balances the tree at the given node. This is the case, where the difference between the right subtree's 
     * height and the left subtree's height is two.
     * @param _node The unbalanced node.
     * @param writer The writer to send the output to.
     * @param writer A Boolean, which is true if the AVL-tree has to be printed after each rotation.
     * @throws IOException If some error occurs during output.
     * @return The node which is now at the position where the given node has been.
     */
    private AVLNode balanceRightToLeft(AVLNode _node, BufferedWriter writer, boolean write) throws IOException {
        // Rechter Teilbaum von _node hat mindestens die Hoehe 1
        AVLNode b = _node.getRight(); 
        // Rechter Teilbaum von b ist groesser
        if (b.getLeft() == null || (b.getRight() != null && b.getLeft().getHeight() < b.getRight().getHeight())) {
            return this.leftRotate(_node, writer, write);
        } else { // Linker Teilbaum von b ist groesser (oder gleich?)
            this.rightRotate(b, writer, write);
            return this.leftRotate(_node, writer, write);
        }
    }
    
    /**
     * Balances the tree at the given node. This is the case, where the difference between the left subtree's 
     * height and the right subtree's height is two.
     * @param _node The unbalanced node.
     * @param writer The writer to send the output to.
     * @param writer A Boolean, which is true if the AVL-tree has to be printed after each rotation.
     * @throws IOException If some error occurs during output.
     * @return The node which is now at the position where the given node has been.
     */
    private AVLNode balanceLeftToRight(AVLNode _node, BufferedWriter writer, boolean write) throws IOException {
        // Linker Teilbaum von _node hat mindestens die Hoehe 1
        AVLNode b = _node.getLeft();
        // Linker Teilbaum von b ist groesser
        if (b.getRight() == null || (b.getLeft() != null && b.getRight().getHeight() < b.getLeft().getHeight())) {
            return this.rightRotate(_node, writer, write);
        } else { // Linker Teilbaum von b ist groesser (oder gleich?)
            this.leftRotate(b, writer, write);
            return this.rightRotate(_node, writer, write);
        }
    }
    
    /**
     * Performs a left-rotation on the given node of this AVL-tree. If the given Boolean
     * is true, it also sends the result to the given buffered-writer.
     * @param _node The node to rotate at.
     * @param writer The writer to send the output to.
     * @param write A Boolean, which is true if the AVL-tree has to be printed after rotation.
     * @throws IOException If some error occurs during output.
     * @return The node which is now at the position where the given node has been.
     */
    private AVLNode leftRotate(AVLNode _node, BufferedWriter writer, boolean write) throws IOException {
        AVLNode node2 = _node.getRight();
        // Baum B verschieben
        _node.setRight(node2.getLeft());
        if (_node.getRight() != null) {
            _node.getRight().setFather(_node);
        }
        // node2 wieder einhängen
        node2.setFather(_node.getFather());
        if (_node.getFather() == null) { // _node war die Wurzel
            this.mRoot = node2;
        } else if (_node == _node.getFather().getLeft()) { // war linkes Kind
            node2.getFather().setLeft(node2);
        } else { // war rechtes Kind
            node2.getFather().setRight(node2);
        }
        // _node einhängen
        node2.setLeft(_node);
        _node.setFather(node2);
        if (write) {
            this.print("rotiere " + _node.getValue() + " nach links", writer);
        }
        return node2;
    }
    
    /**
     * Performs a right-rotation on the given node of this AVL-tree. If the given Boolean
     * is true, it also sends the result to the given buffered-writer.
     * @param _node The node to rotate at.
     * @param writer The writer to send the output to.
     * @param write A Boolean, which is true if the AVL-tree has to be printed after rotation.
     * @throws IOException If some error occurs during output.
     * @return The node which is now at the position where the given node has been.
     */
    private AVLNode rightRotate(AVLNode _node, BufferedWriter writer, boolean write) throws IOException {
        AVLNode node2 = _node.getLeft();
        // Baum B verschieben
        _node.setLeft(node2.getRight());
        if (_node.getLeft() != null) {
            _node.getLeft().setFather(_node);
        }
        // node2 wieder einhängen
        node2.setFather(_node.getFather());
        if (_node.getFather() == null) { // _node war die Wurzel
            this.mRoot = node2;
        } else if (_node == _node.getFather().getLeft()) { // war linkes Kind
            node2.getFather().setLeft(node2);
        } else { // war rechtes Kind
            node2.getFather().setRight(node2);
        }
        // _node einhängen
        node2.setRight(_node);
        _node.setFather(node2);
        if (write) {
            this.print("rotiere " + _node.getValue() + " nach rechts", writer);
        }
        return node2;
    }
    
    /**
     * Gives the string representation of this AVL-tree starting at
     * the given node. The representation is a valid latex-qtree.
     * @return The string representation of this AVL-tree
     */
    private String toString(AVLNode _node) {
        String result = new String("");
        if (_node == null) {
            return result;
        } 
        if (_node.getLeft() == null && _node.getRight() == null) {
            result += " " + _node.getValue();
        } else {
            result += " [." + _node.getValue();
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
     * Prints this AVL-tree right under the given headline.
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
        if (this.getHeight() <= 0) {
            writer.write("\\begin{minipage}[t]{0.2 \\columnwidth}");
            writer.newLine();
        } else if (this.getHeight() < 9) {
            writer.write("\\begin{minipage}[t]{0." + (this.getHeight()+1) + " \\columnwidth}");
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
        if( this.getHeight() < 9 ) {
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
        writer.write("[every tree node/.style={circle,draw=black,thick,inner sep=5pt}, ");
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
        this.mStepCounter += this.getHeight()+1;
        if (this.mStepCounter >= 10) {
            writer.newLine();
            writer.write("~\\\\");
            writer.newLine();
            writer.newLine();
            if (this.getHeight() <= 0) {
                this.mStepCounter = 2;
            } else {
                this.mStepCounter = this.getHeight()+1;
            }
        }
    }
    
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
                tree.insert(operation.x, writer, false);
            } else {
                //System.out.println("remove " + operation.x + " in:");
                AVLNode toRemove = tree.find(operation.x);
                if (toRemove != null) {
                    tree.del(toRemove, writer, false);
                }
            }
            //System.out.println("results in:");
            //System.out.println(tree.toString(tree.mRoot));
        }
        if (writerSpace != null) {
            if (ops.size() > 1) {
                if (tree.isEmpty()) {
                    writerSpace.write(
                        "F\\\"uhren Sie folgenden Operationen beginnend mit einem anfangs leeren"
                        + " AVL-Baum aus und geben Sie die entstehenden B\\\"aume nach jeder Einf\\\"uge- "
                        + "und L\\\"oschoperation sowie jeder Rotation an:\\\\\\\\"
                    );
                    writerSpace.newLine();
                } else {
                    writerSpace.write(
                        "Betrachten Sie den folgenden AVL-Baum:\\\\[2ex]"
                    );
                    writerSpace.newLine();
                    writerSpace.newLine();
                    tree.print("", writerSpace);
                    writerSpace.newLine();
                    writerSpace.newLine();
                    writerSpace.write("\\vspace*{1ex}");
                    writerSpace.newLine();
                    writerSpace.write(
                        "F\\\"uhren Sie beginnend mit diesem Baum die folgenden Operationen aus "
                        + "und geben Sie die entstehenden B\\\"aume nach jeder Einf\\\"uge- "
                        + "und L\\\"oschoperation sowie jeder Rotation an:\\\\\\\\"
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
                            + " in einen leeren AVL-Baum ein und geben Sie die entstehenden B\\\"aume "
                            + "nach jeder Einf\\\"uge- und L\\\"oschoperation sowie jeder Rotation an."
                        );
                    } else {
                        // this case is nonsense 
                        return;
                    }
                } else {
                    if (op.y) {
                        writerSpace.write(
                            "F\\\"ugen Sie den Wert "
                            + op.x
                            + " in den folgenden AVL-Baum ein und geben Sie die entstehenden B\\\"aume "
                            + "nach jeder Einf\\\"uge- und L\\\"oschoperation sowie jeder Rotation an:\\\\[2ex]"
                        );
                    } else {
                        writerSpace.write(
                            "L\\\"oschen Sie den Wert "
                            + op.x
                            + " aus dem folgenden AVL-Baum und geben Sie die entstehenden B\\\"aume "
                            + "nach jeder Einf\\\"uge- und L\\\"oschoperation sowie jeder Rotation an:\\\\[2ex]"
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
                //System.out.println("insert " + operation.x + " in:");
                //System.out.println(tree.toString(tree.root()));
                tree.insert(operation.x, writer, true);
            } else {
                //System.out.println("remove " + operation.x + " in:");
                //System.out.println(tree.toString(tree.root()));
                AVLNode toRemove = tree.find(operation.x);
                if (toRemove != null) {
                    tree.del(toRemove, writer, true);
                } else {
                    tree.print(operation.x + " kommt nicht vor", writer);
                }
            }
            //System.out.println("results in:");
            //System.out.println(tree.toString(tree.mRoot));
        }
    }
}
