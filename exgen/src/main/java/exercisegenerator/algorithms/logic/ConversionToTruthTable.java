package exercisegenerator.algorithms.logic;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.logic.*;

public class ConversionToTruthTable implements AlgorithmImplementation {

    public static final ConversionToTruthTable INSTANCE = new ConversionToTruthTable();

    public static TruthTable toTruthTable(final PropositionalFormula formula) {
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

    private static void printToTruthTableExercise(
        final PropositionalFormula formula,
        final TruthTable truthTable,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Geben Sie die Wahrheitstabelle zu der folgenden aussagenlogischen Formel an:\\\\");
        Main.newLine(writer);
        PropositionalLogic.printGeneralFormula(formula, writer);
        LaTeXUtils.printSolutionSpaceBeginning(Optional.empty(), options, writer);
        LaTeXUtils.printVerticalProtectedSpace("1ex", writer);
        PropositionalLogic.printTruthTable(truthTable, true, true, writer);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), options, writer);
    }

    private static void printToTruthTableSolution(
        final TruthTable truthTable,
        final BufferedWriter writer
    ) throws IOException {
        LaTeXUtils.printVerticalProtectedSpace("-6ex", writer);
        PropositionalLogic.printTruthTable(truthTable, false, true, writer);
        Main.newLine(writer);
    }

    private ConversionToTruthTable() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final PropositionalFormula formula =
            new ParserAndGenerator<PropositionalFormula>(
                PropositionalLogic::parseFormula,
                PropositionalLogic::generateFormula
            ).getResult(input.options);
        final TruthTable truthTable = ConversionToTruthTable.toTruthTable(formula);
        ConversionToTruthTable.printToTruthTableExercise(formula, truthTable, input.options, input.exerciseWriter);
        ConversionToTruthTable.printToTruthTableSolution(truthTable, input.solutionWriter);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "3";
        return result; //TODO
    }

}
