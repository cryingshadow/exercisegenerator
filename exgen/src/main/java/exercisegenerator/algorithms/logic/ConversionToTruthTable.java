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
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "3";
        return result; //TODO
    }

    @Override
    public PropositionalFormula parseOrGenerateProblem(final Parameters<Flag> options) throws IOException {
        return PropositionalLogic.parseOrGenerateFormula(options);
    }

    @Override
    public void printExercise(
        final PropositionalFormula problem,
        final TruthTable solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Geben Sie die Wahrheitstabelle zu der folgenden aussagenlogischen Formel an:\\\\");
        Main.newLine(writer);
        PropositionalLogic.printGeneralFormula(problem, writer);
        LaTeXUtils.printSolutionSpaceBeginning(Optional.empty(), options, writer);
        LaTeXUtils.printVerticalProtectedSpace("1ex", writer);
        PropositionalLogic.printTruthTable(solution, true, true, writer);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, writer);
    }

    @Override
    public void printSolution(
        final PropositionalFormula problem,
        final TruthTable solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printVerticalProtectedSpace("-6ex", writer);
        PropositionalLogic.printTruthTable(solution, false, true, writer);
        Main.newLine(writer);
    }

}
