package exercisegenerator.structures.trees;

public class TreeOperation<T extends Comparable<T>> {

    public final boolean add;
    public final T value;

    public TreeOperation(final T value, final boolean add) {
        this.value = value;
        this.add = add;
    }

}
