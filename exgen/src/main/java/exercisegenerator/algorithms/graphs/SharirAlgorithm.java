package exercisegenerator.algorithms.graphs;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.graphs.*;

public class SharirAlgorithm implements GridGraphAlgorithm<List<List<String>>> {

    public static final SharirAlgorithm INSTANCE = new SharirAlgorithm();

    private SharirAlgorithm() {}

    @Override
    public List<List<String>> apply(final GridGraph graph) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final GridGraph graph = this.parseOrGenerateProblem(input.options);
        input.exerciseWriter.write("Wenden Sie \\emphasize{Sharir's Algorithmus} an (siehe Folien zur Vorlesung) um die ");
        input.exerciseWriter.write("starken Zusammenhangskomponenten des folgenden Graphen zu finden. Geben Sie das Array ");
        input.exerciseWriter.write("\\texttt{color} und den Stack \\texttt{S} nach jeder Schleifeniteration der ersten ");
        input.exerciseWriter.write("und zweiten Phase (also nach Zeile 17 und nach Zeile 22) an, falls \\texttt{DFS1} ");
        input.exerciseWriter.write("bzw. \\texttt{DFS2} ausgef\\\"uhrt wurde. Geben Sie zudem das Array \\texttt{scc} ");
        input.exerciseWriter.write("nach jeder Schleifeniteration der zweiten Phase (also nach Zeile 22) an, falls ");
        input.exerciseWriter.write("\\texttt{DFS2} ausgef\\\"uhrt wurde. Nehmen Sie hierbei an, dass \\texttt{scc} ");
        input.exerciseWriter.write("initial mit Nullen gef\\\"ullt ist und der Knoten mit Schl\\\"ussel $i$ in der ");
        input.exerciseWriter.write("Adjazenzliste den $(i-1)$-ten Eintrag hat, also der Knoten mit Schl\\\"ussel $1$ vom ");
        input.exerciseWriter.write("Algorithmus als erstes ber\"ucksichtig wird usw.");
        Main.newLine(input.exerciseWriter);
        graph.printGraph(input.exerciseWriter, false);
        graph.printSCCs(input.solutionWriter, false, true);
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
