package exercisegenerator.structures.graphs;

import java.util.*;
import java.util.stream.*;

public class BellmanFordStep<V> {

    public final Map<V, Integer> distances;

    public final Map<V, V> predecessors;

    public BellmanFordStep(final Map<V, Integer> distances, final Map<V, V> predecessors) {
        this.distances = new LinkedHashMap<V, Integer>(distances);
        this.predecessors = new LinkedHashMap<V, V>(predecessors);
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof BellmanFordStep) {
            @SuppressWarnings("unchecked")
            final
            BellmanFordStep<V> other = (BellmanFordStep<V>)o;
            return this.distances.equals(other.distances) && this.predecessors.equals(other.predecessors);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 43 + 2 * this.distances.hashCode() + 3 * this.predecessors.hashCode();
    }

    @Override
    public String toString() {
        return this
            .distances
            .keySet()
            .stream()
            .map(key -> key + "=" + this.distances.get(key))
            .collect(Collectors.joining(", ", "{", "}"));
    }

}