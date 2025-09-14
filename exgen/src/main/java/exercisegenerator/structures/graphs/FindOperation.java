package exercisegenerator.structures.graphs;

public class FindOperation<E extends Comparable<E>> extends UnionFindOperation<E> {

    public FindOperation(final E element) {
        super(element);
    }

    @Override
    public void apply(final UnionFind<E> unionFind) {
        unionFind.find(this.element);
    }

    @Override
    public String toString() {
        return String.format("FIND %s", this.element.toString());
    }

}
