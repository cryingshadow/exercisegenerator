package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

public class SCCAlgorithm implements GridGraphAlgorithm<List<List<String>>> {

    public static final SCCAlgorithm INSTANCE = new SCCAlgorithm();

    private SCCAlgorithm() {}

    @Override
    public List<List<String>> apply(final GridGraph graph) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final GridGraph graph = this.parseOrGenerateProblem(null);
        input.exerciseWriter.write("Geben Sie alle \\emphasize{starken Zusammenhangskomponenten} im folgenden Graph an. ");
        input.exerciseWriter.write("F\\\"ur jede dieser starken Zusammenhangskomponenten reicht es die Menge der Knoten ");
        input.exerciseWriter.write("anzugeben, die darin auftreten.");
        Main.newLine(input.exerciseWriter);
        graph.printGraph(input.exerciseWriter, false);
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

    @Override
    public void printExercise(
        final GridGraph problem,
        final List<List<String>> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub
    }

    @Override
    public void printSolution(
        final GridGraph problem,
        final List<List<String>> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        // TODO Auto-generated method stub
    }

}
