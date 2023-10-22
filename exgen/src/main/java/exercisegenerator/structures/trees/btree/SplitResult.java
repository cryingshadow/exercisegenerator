package exercisegenerator.structures.trees.btree;

class SplitResult<T extends Comparable<T>> {

    final BTreeNode<T> leftChild;
    final BTreeNode<T> rightChild;
    final T value;

    SplitResult(final T value, final BTreeNode<T> leftChild, final BTreeNode<T> rightChild) {
        this.value = value;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

}
