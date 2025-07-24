package exercisegenerator.structures.graphs.petrinets;

import java.io.*;

import exercisegenerator.*;
import exercisegenerator.structures.graphs.*;
import exercisegenerator.structures.graphs.layout.*;

public class CoverabilityGraph extends Graph<PetriMarking, String> {

    private final Vertex<PetriMarking> start;

    public CoverabilityGraph(final Vertex<PetriMarking> start) {
        super();
        this.addVertex(start);
        this.start = start;
    }

    @Override
    public void printTikZ(
        final GraphLayout<PetriMarking, String> layout,
        final BufferedWriter writer
    ) throws IOException {
        super.printTikZ(layout, writer);
        writer.write(String.format("\\draw[->,thick] ($(%s)+(-0.5,0.5)$) -- (%s);", this.start.id, this.start.id));
        Main.newLine(writer);
    }

}
