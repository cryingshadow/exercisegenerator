package exercisegenerator.structures.graphs.layout;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import exercisegenerator.*;
import exercisegenerator.structures.graphs.*;

public interface GraphLayout<V extends Comparable<V>, E extends Comparable<E>, T extends Number> {

    public static <E> String edgeFormat(
        final String style,
        final String fromID,
        final List<E> edgeLabels,
        final String toID
    ) {
        final String edgeNode =
            edgeLabels.isEmpty() ?
                "" :
                    String.format(
                        "node[auto] {%s} ",
                        edgeLabels.stream().map(l -> l.toString()).collect(Collectors.joining(","))
                    );
        if (fromID.equals(toID)) {
            return String.format(
                "\\draw%s ($(n%s.north)+(-0.1,0)$) .. controls +(-0.2,1) and +(0.2,1) .. %s($(n%s.north)+(0.1,0)$);%s",
                style,
                fromID,
                edgeNode,
                toID,
                Main.lineSeparator
            );
        }
        return String.format(
            "\\draw%s (n%s) to %s(n%s);%s",
            style,
            fromID,
            edgeNode,
            toID,
            Main.lineSeparator
        );
    }

    public static String startNodeDecoration(final String id) {
        return String.format(
            "%s\\draw[->,thick] ($(n%s.north west)+(-0.5,0.5)$) to (n%s);%s",
            Main.lineSeparator,
            id,
            id,
            Main.lineSeparator
        );
    }

    Coordinates2D<T> getPosition(Vertex<V> vertex);

    void printTikZ(Graph<V, E> graph, BufferedWriter writer) throws IOException;

}
