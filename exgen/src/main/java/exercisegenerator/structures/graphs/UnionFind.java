package exercisegenerator.structures.graphs;
import java.util.*;

public class UnionFind<E extends Comparable<E>> extends TreeMap<E, E> {

    private static final long serialVersionUID = 1L;

    public UnionFind() {
        super();
    }

    public UnionFind(final Collection<E> elements) {
        super();
        for (final E element : elements) {
            this.put(element, element);
        }
    }

    public UnionFind(final Map<E, E> representations) {
        super(representations);
    }

    public boolean connected(final E first, final E second) {
        return this.find(first).equals(this.find(second));
    }

    public E find(final E element) {
        if (this.containsKey(element)) {
            final E representedBy = this.get(element);
            if (representedBy.equals(element)) {
                return representedBy;
            }
            final E res = this.find(representedBy);
            this.put(element, res);
            return res;
        } else {
            this.put(element, element);
            return element;
        }
    }

    public Graph<E, Integer> toGraph() {
        final Set<E> added = new LinkedHashSet<E>();
        final Graph<E, Integer> result = new Graph<E, Integer>();
        for (final Map.Entry<E, E> entry : this.entrySet()) {
            if (!added.contains(entry.getKey())) {
                result.addVertex(new Vertex<E>(entry.getKey()));
                added.add(entry.getKey());
            }
            if (!added.contains(entry.getValue())) {
                result.addVertex(new Vertex<E>(entry.getValue()));
                added.add(entry.getValue());
            }
            result.addEdge(
                result.getVerticesWithLabel(entry.getKey()).iterator().next(),
                Optional.empty(),
                result.getVerticesWithLabel(entry.getValue()).iterator().next()
            );
        }
        return result;
    }

    public void union(final E first, final E second) {
        this.put(this.find(first), this.find(second));
    }

}
