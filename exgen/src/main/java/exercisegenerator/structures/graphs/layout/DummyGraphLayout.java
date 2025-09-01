package exercisegenerator.structures.graphs.layout;

import java.io.*;

import exercisegenerator.structures.graphs.*;

public final class DummyGraphLayout<V extends Comparable<V>, E extends Comparable<E>, T extends Number>
implements GraphLayout<V, E, T> {

    @Override
    public Coordinates2D<T> getPosition(final Vertex<V> vertex) {
        return null;
    }

    @Override
    public void printTikZ(final Graph<V, E> graph, final BufferedWriter writer) throws IOException {
        // do nothing
    }

}
