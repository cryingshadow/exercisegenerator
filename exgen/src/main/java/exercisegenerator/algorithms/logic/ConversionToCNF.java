package exercisegenerator.algorithms.logic;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.logic.*;

public class ConversionToCNF implements AlgorithmImplementation<PropositionalFormula, List<PropositionalFormula>> {

    public static final ConversionToCNF INSTANCE = new ConversionToCNF();

    private ConversionToCNF() {}

    @Override
    public List<PropositionalFormula> apply(final PropositionalFormula formula) {
        return PropositionalLogic.toNF(formula, true);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

    @Override
    public PropositionalFormula parseOrGenerateProblem(final Parameters<Flag> options) throws IOException {
        return PropositionalLogic.parseOrGenerateFormula(options);
    }

    @Override
    public void printExercise(
        final PropositionalFormula problem,
        final List<PropositionalFormula> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Geben Sie eine zur folgenden aussagenlogischen Formel ");
        writer.write("\\\"aquivalente aussagenlogische Formel in CNF an:\\\\");
        Main.newLine(writer);
        PropositionalLogic.printGeneralFormula(problem, writer);
        Main.newLine(writer);
    }

    @Override
    public void printSolution(
        final PropositionalFormula problem,
        final List<PropositionalFormula> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        PropositionalLogic.printFormulaEquivalencesSolution(solution, writer);
    }

}
