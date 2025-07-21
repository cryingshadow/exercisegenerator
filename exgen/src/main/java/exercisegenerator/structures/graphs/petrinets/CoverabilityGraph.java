package exercisegenerator.structures.graphs.petrinets;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.*;

public class CoverabilityGraph extends Graph<PetriMarking, String> {

    private final Vertex<PetriMarking> start;

    public CoverabilityGraph(final Vertex<PetriMarking> start) {
        super();
        this.addVertex(start);
        this.start = start;
    }

    @Override
    public void printTikZ(
        final GraphPrintMode printMode,
        final double multiplier,
        final Set<FordFulkersonPathStep<PetriMarking, String>> toHighlight,
        final BufferedWriter writer
    ) throws IOException {
        super.printTikZ(printMode, multiplier, toHighlight, writer);
        writer.write(String.format("\\draw[->,thick] ($(%s)+(-0.5,0.5)$) -- (%s);", this.start.id, this.start.id));
        Main.newLine(writer);
    }

}
