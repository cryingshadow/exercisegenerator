package exercisegenerator.structures.trees;

import exercisegenerator.util.*;

/**
 * B-tree with ints as keys.
 */
public class IntBTree {

//    public static void main(String[] args) {
//        IntBTree tree = new IntBTree(2);
//        java.util.Random gen = new java.util.Random();
//        System.out.println(tree);
//        java.util.List<Integer> keys = new java.util.ArrayList<Integer>();
//        for (int i = 0; i < 50; i++) {
//            if (keys.isEmpty() || gen.nextInt(3) > 0) {
//                int key = gen.nextInt(100);
//                System.out.println("Add " + key);
//                tree.add(key);
//                keys.add(key);
//            } else {
//                int key = keys.remove(gen.nextInt(keys.size()));
//                System.out.println("Remove " + key);
//                if (!tree.remove(key)) {
//                    System.out.println("ERROR!");
//                }
//            }
//            System.out.println(tree);
//        }
//    }

    /**
     * A node in a B-tree of ints.
     * @author cryingshadow
     * @version $Id$
     */
    private class IntBTreeNode {

        /**
         * The number of keys stored in this node.
         */
        private int filled;

        /**
         * The keys stored in this node. Entries from <code>filled</code> to the end of the array are garbage.
         */
        private final Integer[] keys;

        /**
         * Flag indicating whether this node is a leaf.
         */
        private boolean leaf;

        /**
         * The successor nodes of this node. The index of a key corresponds to the left successor index of that key.
         * The right-most successor has index <code>filled</code>.
         */
        private final IntBTreeNode[] nodes;

        /**
         * Creates an empty non-leaf node where the arrays are large enough to store 2 * <code>fillingDegree</code>
         * successor nodes.
         */
        private IntBTreeNode() {
            this.keys = new Integer[2 * IntBTree.this.fillingDegree - 1];
            this.nodes = new IntBTreeNode[2 * IntBTree.this.fillingDegree];
            this.filled = 0;
            this.leaf = false;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            final StringBuilder res = new StringBuilder();
            if (this.leaf) {
                res.append("{");
                res.append(this.keys[0]);
                for (int i = 1; i < this.filled; i++) {
                    res.append(",");
                    res.append(this.keys[i]);
                }
                res.append("}");
                return res.toString();
            }
            res.append("[.{");
            res.append(this.keys[0]);
            for (int i = 1; i < this.filled; i++) {
                res.append(",");
                res.append(this.keys[i]);
            }
            res.append("}");
            for (int i = 0; i <= this.filled; i++) {
                res.append(" ");
                res.append(this.nodes[i]);
            }
            res.append(" ]");
            return res.toString();
        }

        /**
         * Adds the specified key to the B-tree rooted in this node.
         * @param key The key to add.
         * @param father The father node of this node. Needed for split operations.
         * @param fatherIndex The index of this node within the father node. Also needed for split operations.
         */
        private void add(final int key, final IntBTreeNode father, final int fatherIndex) {
            if (this.filled == 2 * IntBTree.this.fillingDegree - 1) {
                final int length = IntBTree.this.fillingDegree - 1;
                final IntBTreeNode right = new IntBTreeNode();
                System.arraycopy(this.keys, length + 1, right.keys, 0, length);
                System.arraycopy(this.nodes, length + 1, right.nodes, 0, length + 1);
                right.filled = length;
                if (father == null) {
                    final IntBTreeNode left = new IntBTreeNode();
                    System.arraycopy(this.keys, 0, left.keys, 0, length);
                    System.arraycopy(this.nodes, 0, left.nodes, 0, length + 1);
                    left.filled = length;
                    if (this.leaf) {
                        left.leaf = true;
                        right.leaf = true;
                        this.leaf = false;
                    }
                    this.keys[0] = this.keys[length];
                    this.nodes[0] = left;
                    this.nodes[1] = right;
                    this.filled = 1;
                    // fall through to add key to this node
                } else {
                    if (this.leaf) {
                        right.leaf = true;
                    }
                    this.filled = length;
                    for (int i = father.filled; i > fatherIndex; i--) {
                        father.keys[i] = father.keys[i - 1];
                    }
                    father.keys[fatherIndex] = this.keys[length];
                    for (int i = father.filled + 1; i > fatherIndex + 1; i--) {
                        father.nodes[i] = father.nodes[i - 1];
                    }
                    father.nodes[fatherIndex + 1] = right;
                    father.filled++;
                    final int index = ArrayUtils.binarySearch(father.keys, key, 0, father.filled - 1);
                    father.nodes[index].add(key, father, index);
                    return;
                }
            }
            final int index = ArrayUtils.binarySearch(this.keys, key, 0, this.filled - 1);
            if (this.leaf) {
                for (int i = this.filled; i > index; i--) {
                    this.keys[i] = this.keys[i - 1];
                }
                this.keys[index] = key;
                this.filled++;
            } else {
                this.nodes[index].add(key, this, index);
            }
        }

        /**
         * @return The maximum key in the B-tree rooted at this node.
         */
        private int max() {
            if (this.leaf) {
                return this.keys[this.filled - 1];
            }
            return this.nodes[this.filled].max();
        }

        /**
         * Merges the specified neighboring successor nodes with the key at the specified index. The left node is kept
         * as the merged node while the right node is dropped.
         * @param left The left successor node.
         * @param right The right successor node.
         * @param index The index of the key to merge the nodes with.
         * @param tree Link to the overall tree structure if this node is the root node (then merging might lead to a
         *             deletion of the root node such that the overall tree structure needs to be updated). Null
         *             otherwise.
         * @return True if the original root node has been deleted and the left node is the new root node. False
         *         otherwise.
         */
        private boolean merge(final IntBTreeNode left, final IntBTreeNode right, final int index, final IntBTree tree) {
            left.keys[left.filled++] = this.keys[index];
            for (int i = 0; i < right.filled; i++) {
                left.keys[left.filled] = right.keys[i];
                left.nodes[left.filled++] = right.nodes[i];
            }
            left.nodes[left.filled] = right.nodes[right.filled];
            if (tree != null && this.filled == 1) {
                // root node vanishes
                tree.root = left;
                return true;
            }
            for (int i = index; i < this.filled - 1; i++) {
                this.keys[i] = this.keys[i + 1];
                this.nodes[i + 1] = this.nodes[i + 2];
            }
            this.filled--;
            return false;
        }

        /**
         * @return The minimum key in the B-tree rooted at this node.
         */
        private int min() {
            if (this.leaf) {
                return this.keys[0];
            }
            return this.nodes[0].min();
        }

        /**
         * Removes one occurrence of the specified key from the B-tree rooted at this node if at least one occurrence
         * exists.
         * @param key The key to remove.
         * @param tree Link to the overall tree structure if this node is the root node (then merging might lead to a
         *             deletion of the root node such that the overall tree structure needs to be updated). Null
         *             otherwise.
         * @return True if at least one occurrence of the specified key did exist. False otherwise.
         */
        private boolean remove(final int key, final IntBTree tree) {
            final int index = ArrayUtils.binarySearch(this.keys, key, 0, this.filled - 1);
            if (this.leaf) {
                if (this.keys[index] != key) {
                    return false;
                }
                if (tree != null && this.filled == 1) {
                    tree.root = null;
                    return true;
                }
                for (int i = index; i < this.filled - 1; i++) {
                    this.keys[i] = this.keys[i + 1];
                }
                this.filled--;
                return true;
            } else if (index < this.filled && this.keys[index] == key) {
                final IntBTreeNode left = this.nodes[index];
                if (left.filled >= IntBTree.this.fillingDegree) {
                    // steal left max
                    final int max = left.max();
                    this.keys[index] = max;
                    return left.remove(max, null);
                }
                final IntBTreeNode right = this.nodes[index + 1];
                if (right.filled >= IntBTree.this.fillingDegree) {
                    // steal right min
                    final int min = right.min();
                    this.keys[index] = min;
                    return right.remove(min, null);
                }
                // otherwise merge into left node, throw away right node
                if (this.merge(left, right, index, tree)) {
                    return left.remove(key, tree);
                }
                return left.remove(key, null);
            } else {
                final IntBTreeNode next = this.nodes[index];
                if (next.filled >= IntBTree.this.fillingDegree) {
                    return next.remove(key, null);
                }
                final IntBTreeNode left = index > 0 ? this.nodes[index - 1] : null;
                if (left != null && left.filled >= IntBTree.this.fillingDegree) {
                    this.rotateRight(left, next, index - 1);
                    return next.remove(key, null);
                }
                final IntBTreeNode right = index < this.filled ? this.nodes[index + 1] : null;
                if (right != null && right.filled >= IntBTree.this.fillingDegree) {
                    this.rotateLeft(right, next, index);
                    return next.remove(key, null);
                }
                if (right != null) {
                    // merge into next node, throw away right node
                    if (this.merge(next, right, index, tree)) {
                        return next.remove(key, tree);
                    }
                    return next.remove(key, null);
                } else {
                    assert(left != null) :
                        "Left and right neighbor of this node do not exist - this should be impossible...";
                    // merge into left node, throw away next node
                    if (this.merge(left, next, index - 1, tree)) {
                        return left.remove(key, tree);
                    }
                    return left.remove(key, null);
                }
            }
        }

        /**
         * Rotates the key at the specified index to the left.
         * @param right The node right from <code>next</code>.
         * @param next The node to rotate the key in.
         * @param index The index of the key to rotate in the current node.
         */
        private void rotateLeft(final IntBTreeNode right, final IntBTreeNode next, final int index) {
            next.keys[next.filled++] = this.keys[index];
            next.nodes[next.filled] = right.nodes[0];
            this.keys[index] = right.keys[0];
            for (int i = 0; i < right.filled - 1; i++) {
                right.keys[i] = right.keys[i + 1];
                right.nodes[i] = right.nodes[i + 1];
            }
            right.filled--;
            right.nodes[right.filled] = right.nodes[right.filled + 1];
        }

        /**
         * Rotates the key at the specified index to the right.
         * @param left The node left from <code>next</code>.
         * @param next The node to rotate the key in.
         * @param index The index of the key to rotate in the current node.
         */
        private void rotateRight(final IntBTreeNode left, final IntBTreeNode next, final int index) {
            next.nodes[next.filled + 1] = next.nodes[next.filled];
            for (int i = next.filled++; i > 0; i--) {
                next.keys[i] = next.keys[i - 1];
                next.nodes[i] = next.nodes[i - 1];
            }
            next.keys[0] = this.keys[index];
            next.nodes[0] = left.nodes[left.filled];
            this.keys[index] = left.keys[--left.filled];
        }

    }

    /**
     * Since there are three different names for B-trees of degree 2, this String can be used to customize the output
     * for a lecture.
     */
    public static final String NAME_OF_BTREE_WITH_DEGREE_2 = "2-3-4-Baum";

    /**
     * Filling degree of this B-tree. Must be greater than 1.
     */
    private final int fillingDegree;

    /**
     * Root node of this B-tree.
     */
    private IntBTreeNode root;

    /**
     * Creates an empty B-tree with the specified filling degree.
     * @param t The filling degree. Must be greater than 1.
     */
    public IntBTree(final int t) {
        assert(t > 1) : "Filling degree must be greater than 1!";
        this.fillingDegree = t;
        this.root = null;
    }

    /**
     * Adds the specified key to this B-tree.
     * @param key The key to add.
     */
    public void add(final int key) {
        if (this.root == null) {
            this.root = new IntBTreeNode();
            this.root.leaf = true;
        }
        this.root.add(key, null, 0);
    }

    /**
     * @return The filling degree of this B-tree.
     */
    public int getDegree() {
        return this.fillingDegree;
    }

    /**
     * @return True if this B-tree just consists of the root node.
     */
    public boolean hasJustRoot() {
        return !this.isEmpty() && this.root.leaf;
    }

    /**
     * @return True if this B-tree is empty. False otherwise.
     */
    public boolean isEmpty() {
        return this.root == null;
    }

    /**
     * Removes one occurrence of the specified key from this B-tree if at least one occurrence exists.
     * @param key The key to remove.
     * @return True if at least one occurrence of the specified key did exist. False otherwise.
     */
    public boolean remove(final int key) {
        if (this.root == null) {
            return false;
        }
        return this.root.remove(key, this);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (this.root == null) {
            return "leer";
        }
        return this.root.toString();
    }

}
