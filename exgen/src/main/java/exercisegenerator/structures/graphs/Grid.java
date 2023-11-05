package exercisegenerator.structures.graphs;

import java.util.*;

public class Grid<V> extends LinkedHashMap<GridCoordinates, Vertex<V>> {

    private static final long serialVersionUID = -482672823105338439L;

    public boolean logicallyEquals(final Grid<V> other) {
        if (!this.keySet().equals(other.keySet())) {
            return false;
        }
        for (final Map.Entry<GridCoordinates, Vertex<V>> entry : this.entrySet()) {
            final GridCoordinates coordinates = entry.getKey();
            final Vertex<V> vertex = entry.getValue();
            final Vertex<V> otherVertex = other.get(coordinates);
            if (!vertex.logicallyEquals(otherVertex)) {
                return false;
            }
        }
        return true;
    }

}
