package exercisegenerator.structures;

/**
 * A node in a red-black-tree with an int value.
 */
public class RBNode {

    private RBNode father;
    private int height;
    private boolean isBlack;
    private RBNode left;
    private RBNode right;
    private int value;

    RBNode(final int val) {
        this.father = null;
        this.left = null;
        this.right = null;
        this.value = val;
        this.height = 0;
        this.isBlack = true;
    }

    public RBNode getFather() {
        return this.father;
    }

    public int getHeight() {
        return this.height;
    }

    public RBNode getLeft() {
        return this.left;
    }

    public RBNode getRight() {
        return this.right;
    }

    public RBNode getSucc(final boolean isLeft) {
        if (isLeft) {
            return this.left;
        } else {
            return this.right;
        }
    }

    public int getValue() {
        return this.value;
    }

    public boolean isBlack() {
        return this.isBlack;
    }

    public void setBlack(final boolean black) {
        this.isBlack = black;
    }

    public void setFather(final RBNode newFather) {
        this.father = newFather;
    }

    public void setLeft(final RBNode newLeft) {
        this.left = newLeft;
        this.updateHeight();
    }

    public void setRight(final RBNode newRight) {
        this.right = newRight;
        this.updateHeight();
    }

    public void setSucc(final boolean isLeft, final RBNode node) {
        if (isLeft) {
            this.left = node;
        } else {
            this.right = node;
        }
        this.updateHeight();
    }

    public void setValue(final int val) {
        this.value = val;
    }

    public void updateHeight() {
        if (this.left == null && this.right == null) {
            this.height = 0;
        } else if (this.left != null && this.right == null) {
            this.height = this.left.getHeight() + 1;
        } else if (this.left == null && this.right != null) {
            this.height = this.right.getHeight() + 1;
        } else if (this.left.getHeight() > this.right.getHeight()) {
            this.height = this.left.getHeight() + 1;
        } else {
            this.height = this.right.getHeight() + 1;
        }
        if (this.father != null) {
            this.father.updateHeight();
        }
    }

}