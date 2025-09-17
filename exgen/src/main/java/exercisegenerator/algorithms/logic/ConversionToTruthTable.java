package exercisegenerator.algorithms.logic;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.logic.*;

public class ConversionToTruthTable implements AlgorithmImplementation<PropositionalFormula, TruthTable> {

    public static final ConversionToTruthTable INSTANCE = new ConversionToTruthTable();

    private ConversionToTruthTable() {}

    @Override
    public TruthTable apply(final PropositionalFormula formula) {
        final List<String> variables = formula.getVariableNames();
        final List<PropositionalInterpretation> interpretations =
            TruthTable.computeAllInterpretations(variables);
        final boolean[] truthValues = new boolean[interpretations.size()];
        int i = 0;
        for (final PropositionalInterpretation interpretation : interpretations) {
            truthValues[i++] = formula.evaluate(interpretation);
        }
        return new TruthTable(variables, truthValues);
    }

    @Override
    public String commandPrefix() {
        return "ToTruthTable";
    }

    @Override
    public PropositionalFormula generateProblem(final Parameters<Flag> options) {
        return PropositionalLogic.generateFormula(options);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "3";
        return result; //TODO
    }

    @Override
    public List<PropositionalFormula> parseProblems(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        return PropositionalLogic.parseFormulas(reader, options);
    }

    @Override
    public void printBeforeMultipleProblemInstances(
        final List<PropositionalFormula> problems,
        final List<TruthTable> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Geben Sie die jeweilige Wahrheitstabelle zu den folgenden aussagenlogischen Formeln an.\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeSingleProblemInstance(
        final PropositionalFormula problem,
        final TruthTable solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Geben Sie die Wahrheitstabelle zu der folgenden aussagenlogischen Formel an:\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printProblemInstance(
        final PropositionalFormula problem,
        final TruthTable solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        PropositionalLogic.printGeneralFormula(problem, writer);
    }

    @Override
    public void printSolutionInstance(
        final PropositionalFormula problem,
        final TruthTable solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printVerticalProtectedSpace("-6ex", writer);
        PropositionalLogic.printTruthTable(solution, false, true, writer);
    }

    @Override
    public void printSolutionSpace(
        final PropositionalFormula problem,
        final TruthTable solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printSolutionSpaceBeginning(Optional.empty(), options, writer);
        LaTeXUtils.printVerticalProtectedSpace("1ex", writer);
        PropositionalLogic.printTruthTable(solution, true, true, writer);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, writer);
    }

}
