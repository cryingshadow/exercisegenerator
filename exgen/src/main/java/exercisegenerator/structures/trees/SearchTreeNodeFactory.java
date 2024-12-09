package exercisegenerator.structures.trees;

public interface SearchTreeNodeFactory<T extends Comparable<T>> {

    SearchTreeNode<T> create(T value);

}
