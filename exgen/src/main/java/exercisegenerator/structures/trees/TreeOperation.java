package exercisegenerator.structures.trees;

public class TreeOperation<T extends Comparable<T>> {

    public final boolean add;
    public final T value;

    public TreeOperation(final T value, final boolean add) {
        this.value = value;
        this.add = add;
    }

    @Override
    public String toString() {
        return String.format("%s%s", this.value.toString(), this.add ? "+" : "-");
    }

}
