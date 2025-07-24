package exercisegenerator.structures.graphs.layout;

import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.*;

public final class DummyGraphLayout<V, E> implements GraphLayout<V, E> {

    @Override
    public TikZStyle graphStyle() {
        return TikZStyle.EMPTY;
    }

    @Override
    public String toTikZ(final Vertex<V> vertex) {
        return "";
    }

    @Override
    public String toTikZ(final Vertex<V> from, final Edge<E, V> edge) {
        return "";
    }

}
