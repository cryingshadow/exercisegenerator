package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

public class SCCAlgorithm implements AlgorithmImplementation {

    public static final SCCAlgorithm INSTANCE = new SCCAlgorithm();

    private SCCAlgorithm() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final Optional<BufferedWriter> optionalWriterSpace = Algorithm.getOptionalSpaceWriter(input);
        final GridGraph graph = new GridGraph();
        graph.createGraph(GraphAlgorithms.parseOrGenerateGridGraph(input.options));
        if (optionalWriterSpace.isPresent()) {
            final BufferedWriter writerSpace = optionalWriterSpace.get();
            writerSpace.write("Geben Sie alle \\emphasize{starken Zusammenhangskomponenten} im folgenden Graph an. ");
            writerSpace.write("F\\\"ur jede dieser starken Zusammenhangskomponenten reicht es die Menge der Knoten ");
            writerSpace.write("anzugeben, die darin auftreten.");
            Main.newLine(writerSpace);
            graph.printGraph(writerSpace, false);
        }
        graph.printSCCs(input.solutionWriter, false, false);
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
