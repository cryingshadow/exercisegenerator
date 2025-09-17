package exercisegenerator.algorithms.logic;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.logic.*;

public class ConversionToDNF implements AlgorithmImplementation<PropositionalFormula, List<PropositionalFormula>> {

    public static final ConversionToDNF INSTANCE = new ConversionToDNF();

    private ConversionToDNF() {}

    @Override
    public List<PropositionalFormula> apply(final PropositionalFormula formula) {
        return PropositionalLogic.toNF(formula, false);
    }

    @Override
    public String commandPrefix() {
        return "ToDnf";
    }

    @Override
    public PropositionalFormula generateProblem(final Parameters<Flag> options) {
        return PropositionalLogic.generateFormula(options);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
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
        final List<List<PropositionalFormula>> solutions,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Geben Sie jeweils eine zu den folgenden aussagenlogischen Formeln ");
        writer.write("\\\"aquivalente aussagenlogische Formel in DNF an.\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeSingleProblemInstance(
        final PropositionalFormula problem,
        final List<PropositionalFormula> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Geben Sie eine zur folgenden aussagenlogischen Formel ");
        writer.write("\\\"aquivalente aussagenlogische Formel in DNF an:\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printProblemInstance(
        final PropositionalFormula problem,
        final List<PropositionalFormula> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        PropositionalLogic.printGeneralFormula(problem, writer);
    }

    @Override
    public void printSolutionInstance(
        final PropositionalFormula problem,
        final List<PropositionalFormula> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        PropositionalLogic.printFormulaEquivalencesSolution(solution, writer);
    }

    @Override
    public void printSolutionSpace(
        final PropositionalFormula problem,
        final List<PropositionalFormula> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {}

}
