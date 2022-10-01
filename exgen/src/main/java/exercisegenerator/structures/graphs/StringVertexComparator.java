package exercisegenerator.structures.graphs;

import java.util.*;

import exercisegenerator.structures.*;

/**
 * String node comparator based on String.compareTo.
 */
public class StringVertexComparator implements Comparator<Vertex<String>> {

    @Override
    public int compare(final Vertex<String> o1, final Vertex<String> o2) {
        return o1.label.orElse("").compareTo(o2.label.orElse(""));
    }

}
