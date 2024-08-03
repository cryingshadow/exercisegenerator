package exercisegenerator.algorithms.logic;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.logic.*;

public class ConversionToCNF implements AlgorithmImplementation {

    public static final ConversionToCNF INSTANCE = new ConversionToCNF();

    public static List<PropositionalFormula> toCNF(final PropositionalFormula formula) {
        return PropositionalLogic.toNF(formula, true);
    }

    private static void printToCNFExercise(
        final PropositionalFormula formula,
        final Parameters options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Geben Sie eine zur folgenden aussagenlogischen Formel ");
        writer.write("\\\"aquivalente aussagenlogische Formel in CNF an:\\\\");
        Main.newLine(writer);
        PropositionalLogic.printGeneralFormula(formula, writer);
        Main.newLine(writer);
    }

    private ConversionToCNF() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final PropositionalFormula formula =
            new ParserAndGenerator<PropositionalFormula>(
                PropositionalLogic::parseFormula,
                PropositionalLogic::generateFormula
            ).getResult(input.options);
        final List<PropositionalFormula> steps = ConversionToCNF.toCNF(formula);
        ConversionToCNF.printToCNFExercise(formula, input.options, input.exerciseWriter);
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
