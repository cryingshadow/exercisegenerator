package exercisegenerator.structures.graphs.layout;

import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.*;

public interface GraphLayout<V, E> {

    TikZStyle graphStyle();

    String toTikZ(Vertex<V> vertex);

    String toTikZ(Vertex<V> from, Edge<E, V> edge);

}
