package exercisegenerator.structures.graphs;

import java.util.*;

public class StringVertexComparator implements Comparator<Vertex<String>> {

    public static StringVertexComparator INSTANCE = new StringVertexComparator();

    private StringVertexComparator() {}

    @Override
    public int compare(final Vertex<String> o1, final Vertex<String> o2) {
        return o1.label.orElse("").compareTo(o2.label.orElse(""));
    }

}
