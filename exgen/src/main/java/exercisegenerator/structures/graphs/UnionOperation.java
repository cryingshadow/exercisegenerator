package exercisegenerator.structures.graphs;

public class UnionOperation<E extends Comparable<E>> extends UnionFindOperation<E> {

    public final E secondElement;

    public UnionOperation(final E firstElement, final E secondElement) {
        super(firstElement);
        this.secondElement = secondElement;
    }

    @Override
    public void apply(final UnionFind<E> unionFind) {
        unionFind.union(this.element, this.secondElement);
    }

    @Override
    public String toString() {
        return String.format("UNION %s, %s", this.element.toString(), this.secondElement.toString());
    }

}
