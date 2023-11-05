package exercisegenerator.structures.graphs;
import java.util.*;

public class UnionFind<E> {

    private final Map<E, E> representedBy;

    public UnionFind() {
        this.representedBy = new LinkedHashMap<E, E>();
    }

    public boolean connected(final E first, final E second) {
        return this.find(first).equals(this.find(second));
    }

    public E find(final E element) {
        if (this.representedBy.containsKey(element)) {
            final E res = this.find(this.representedBy.get(element));
            this.representedBy.put(element, res);
            return res;
        } else {
            return element;
        }
    }

    public void union(final E first, final E second) {
        this.representedBy.put(this.find(first), this.find(second));
    }

}
