package exercisegenerator.algorithms.logic;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.logic.*;

public class ConversionToDNF implements AlgorithmImplementation {

    public static final ConversionToDNF INSTANCE = new ConversionToDNF();

    public static List<PropositionalFormula> toDNF(final PropositionalFormula formula) {
        return PropositionalLogic.toNF(formula, false);
    }

    private static void printToDNFExercise(
        final PropositionalFormula formula,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Geben Sie eine zur folgenden aussagenlogischen Formel ");
        writer.write("\\\"aquivalente aussagenlogische Formel in DNF an:\\\\");
        Main.newLine(writer);
        PropositionalLogic.printGeneralFormula(formula, writer);
        Main.newLine(writer);
    }

    private ConversionToDNF() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final PropositionalFormula formula =
            new ParserAndGenerator<PropositionalFormula>(
                PropositionalLogic::parseFormula,
                PropositionalLogic::generateFormula
            ).getResult(input.options);
        final List<PropositionalFormula> steps = ConversionToDNF.toDNF(formula);
        ConversionToDNF.printToDNFExercise(formula, input.options, input.exerciseWriter);
        PropositionalLogic.printFormulaEquivalencesSolution(steps, input.solutionWriter);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
