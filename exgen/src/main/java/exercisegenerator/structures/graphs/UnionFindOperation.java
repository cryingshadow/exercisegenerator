package exercisegenerator.structures.graphs;

public abstract class UnionFindOperation<E extends Comparable<E>> {

    public final E element;

    public UnionFindOperation(final E element) {
        this.element = element;
    }

    public abstract void apply(UnionFind<E> unionFind);

}
