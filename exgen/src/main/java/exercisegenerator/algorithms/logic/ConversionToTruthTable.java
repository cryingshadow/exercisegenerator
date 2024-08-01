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

    private static void printToTruthTableExerciseAndSolution(
        final PropositionalFormula formula,
        final TruthTable truthTable,
        final AlgorithmInput input
    ) throws IOException {
        final BufferedWriter exWriter = input.exerciseWriter;
        final BufferedWriter solWriter = input.solutionWriter;
        exWriter.write("Geben Sie die Wahrheitstabelle zu der folgenden aussagenlogischen Formel an:\\\\");
        Main.newLine(exWriter);
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), input.options, exWriter);
        PropositionalLogic.printGeneralFormula(formula, exWriter);
        PropositionalLogic.printTruthTable(truthTable, true, true, exWriter);
        PropositionalLogic.printGeneralFormula(formula, solWriter);
        PropositionalLogic.printTruthTable(truthTable, false, true, solWriter);
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), input.options, exWriter);
        Main.newLine(solWriter);
    }

    private ConversionToTruthTable() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final PropositionalFormula formula =
            new ParserAndGenerator<PropositionalFormula>(
                PropositionalLogic::parseFormula,
                PropositionalLogic::generateFormula
            ).getResult(input.options);
        ConversionToTruthTable.printToTruthTableExerciseAndSolution(formula, ConversionToTruthTable.toTruthTable(formula), input);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "3";
        return result; //TODO
    }

}
