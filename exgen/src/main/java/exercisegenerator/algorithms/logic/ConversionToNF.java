package exercisegenerator.algorithms.logic;

import java.io.*;
import java.util.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.logic.*;

public abstract class ConversionToNF
implements AlgorithmImplementation<PropositionalFormula, List<PropositionalFormula>> {

    private final String normalForm;

    protected ConversionToNF(final String normalForm) {
        this.normalForm = normalForm;
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
        writer.write("Leiten Sie jeweils eine zu den folgenden aussagenlogischen Formeln ");
        writer.write("\\\"aquivalente aussagenlogische Formel in ");
        writer.write(this.normalForm);
        writer.write(" ab, indem Sie äquivalente Umformungsschritte aus der Vorlesung anwenden.\\\\");
        Main.newLine(writer);
    }

    @Override
    public void printBeforeSingleProblemInstance(
        final PropositionalFormula problem,
        final List<PropositionalFormula> solution,
        final Parameters<Flag> options,
        final BufferedWriter writer
    ) throws IOException {
        writer.write("Leiten Sie eine zur folgenden aussagenlogischen Formel ");
        writer.write("\\\"aquivalente aussagenlogische Formel in ");
        writer.write(this.normalForm);
        writer.write(" ab, indem Sie äquivalente Umformungsschritte aus der Vorlesung anwenden:\\\\");
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
