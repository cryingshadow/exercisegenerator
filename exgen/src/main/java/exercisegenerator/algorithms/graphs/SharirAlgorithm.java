package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

public class SharirAlgorithm implements AlgorithmImplementation {

    public static final SharirAlgorithm INSTANCE = new SharirAlgorithm();

    private SharirAlgorithm() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final Optional<BufferedWriter> optionalWriterSpace = Algorithm.getOptionalSpaceWriter(input);
        final GridGraph graph = new GridGraph();
        graph.createGraph(GraphAlgorithms.parseOrGenerateGridGraph(input.options));
        if (optionalWriterSpace.isPresent()) {
            final BufferedWriter writerSpace = optionalWriterSpace.get();
            writerSpace.write("Wenden Sie \\emphasize{Sharir's Algorithmus} an (siehe Folien zur Vorlesung) um die ");
            writerSpace.write("starken Zusammenhangskomponenten des folgenden Graphen zu finden. Geben Sie das Array ");
            writerSpace.write("\\texttt{color} und den Stack \\texttt{S} nach jeder Schleifeniteration der ersten ");
            writerSpace.write("und zweiten Phase (also nach Zeile 17 und nach Zeile 22) an, falls \\texttt{DFS1} ");
            writerSpace.write("bzw. \\texttt{DFS2} ausgef\\\"uhrt wurde. Geben Sie zudem das Array \\texttt{scc} ");
            writerSpace.write("nach jeder Schleifeniteration der zweiten Phase (also nach Zeile 22) an, falls ");
            writerSpace.write("\\texttt{DFS2} ausgef\\\"uhrt wurde. Nehmen Sie hierbei an, dass \\texttt{scc} ");
            writerSpace.write("initial mit Nullen gef\\\"ullt ist und der Knoten mit Schl\\\"ussel $i$ in der ");
            writerSpace.write("Adjazenzliste den $(i-1)$-ten Eintrag hat, also der Knoten mit Schl\\\"ussel $1$ vom ");
            writerSpace.write("Algorithmus als erstes ber\"ucksichtig wird usw.");
            Main.newLine(writerSpace);
            graph.printGraph(writerSpace, false);
        }
        graph.printSCCs(input.solutionWriter, false, true) ;
        Main.newLine(input.solutionWriter);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
