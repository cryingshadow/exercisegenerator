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

    private static List<PropositionalFormula> generateFormulas(final Parameters options) {
        final List<String> variables = PropositionalLogic.generateVariables(options);
        final List<PropositionalFormula> formulas = new ArrayList<PropositionalFormula>();
        for (final String name : variables) {
            final PropositionalVariable var = new PropositionalVariable(name);
            formulas.add(Main.RANDOM.nextBoolean() ? var : var.negate());
        }
        final int additional = Main.RANDOM.nextInt(10);
        for (int i = 0; i < additional; i++) {
            final PropositionalVariable var =
                new PropositionalVariable(variables.get(Main.RANDOM.nextInt(variables.size())));
            formulas.add(Main.RANDOM.nextBoolean() ? var : var.negate());
        }
        while (formulas.size() > 3) {
            final int number = Main.RANDOM.nextInt(formulas.size() - 1) + 1;
            if (number == 1) {
                final PropositionalFormula formula = formulas.remove(Main.RANDOM.nextInt(formulas.size()));
                formulas.add(formula.negate());
            } else {
                final List<PropositionalFormula> children = new LinkedList<PropositionalFormula>();
                for (int i = 0; i < number; i++) {
                    children.add(formulas.remove(Main.RANDOM.nextInt(formulas.size())));
                }
                formulas.add(
                    Main.RANDOM.nextBoolean() ?
                        Conjunction.createConjunction(children) :
                            Disjunction.createDisjunction(children)
                );
            }
        }
        while (formulas.size() > 1) {
            final int number = formulas.size() > 2 ? Main.RANDOM.nextInt(formulas.size() - 2) + 2 : 2;
            final List<PropositionalFormula> children = new LinkedList<PropositionalFormula>();
            for (int i = 0; i < number; i++) {
                children.add(formulas.remove(Main.RANDOM.nextInt(formulas.size())));
            }
            formulas.add(
                Main.RANDOM.nextBoolean() ?
                    Conjunction.createConjunction(children) :
                        Disjunction.createDisjunction(children)
            );
        }
        return formulas;
    }

    private static List<PropositionalFormula> parseFormulas(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        final List<PropositionalFormula> result = new ArrayList<PropositionalFormula>();
        String line = reader.readLine();
        while (line != null) {
            if (!line.isBlank()) {
                try {
                    result.add(PropositionalFormula.parse(line));
                } catch (final PropositionalFormulaParseException e) {
                    throw new IOException(e);
                }
            }
            line = reader.readLine();
        }
        return result;
    }

    private static void printGeneralFormula(final PropositionalFormula formula, final BufferedWriter writer)
    throws IOException {
        writer.write("\\[");
        writer.write(
            formula.visit(
                new FormulaVisitor<String>() {

                    @Override
                    public String onConjunction(final List<String> children) {
                        return String.format("(%s)", String.join(" \\wedge ", children));
                    }

                    @Override
                    public String onDisjunction(final List<String> children) {
                        return String.format("(%s)", String.join(" \\vee ", children));
                    }

                    @Override
                    public String onFalse() {
                        return "\\code{0}";
                    }

                    @Override
                    public String onNegation(final String child) {
                        return "\\neg" + child;
                    }

                    @Override
                    public String onTrue() {
                        return "\\code{1}";
                    }

                    @Override
                    public String onVariable(final String name) {
                        return String.format("\\var{%s}", name);
                    }

                }
            )
        );
        writer.write("\\]");
        Main.newLine(writer);
    }

    private static void printToTruthTableExerciseAndSolution(
        final List<PropositionalFormula> formulas,
        final List<TruthTable> truthTables,
        final AlgorithmInput input
    ) throws IOException {
        final BufferedWriter exWriter = input.exerciseWriter;
        final BufferedWriter solWriter = input.solutionWriter;
        final int size = formulas.size();
        if (size == 1) {
            exWriter.write("Geben Sie die Wahrheitstabelle zu der folgenden aussagenlogischen Formel an:\\\\");
        } else {
            exWriter.write(
                "Geben Sie die jeweiligen Wahrheitstabellen zu den folgenden aussagenlogischen Formeln an:\\\\"
            );
        }
        Main.newLine(exWriter);
        LaTeXUtils.printSolutionSpaceBeginning(Optional.of("-3ex"), input.options, exWriter);
        boolean first = true;
        for (int i = 0; i < size; i++) {
            if (first) {
                first = false;
            } else {
                LaTeXUtils.printVerticalProtectedSpace(exWriter);
                LaTeXUtils.printVerticalProtectedSpace(solWriter);
           }
            final PropositionalFormula formula = formulas.get(i);
            final TruthTable table = truthTables.get(i);
            ConversionToTruthTable.printGeneralFormula(formula, exWriter);
            PropositionalLogic.printTruthTable(table, true, true, exWriter);
            ConversionToTruthTable.printGeneralFormula(formula, solWriter);
            PropositionalLogic.printTruthTable(table, false, true, solWriter);
        }
        LaTeXUtils.printSolutionSpaceEnd(Optional.of("1ex"), input.options, exWriter);
        Main.newLine(solWriter);
    }

    private ConversionToTruthTable() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final List<PropositionalFormula> formulas =
            new ParserAndGenerator<List<PropositionalFormula>>(
                ConversionToTruthTable::parseFormulas,
                ConversionToTruthTable::generateFormulas
            ).getResult(input.options);
        final List<TruthTable> truthTables = formulas.stream().map(ConversionToTruthTable::toTruthTable).toList();
        ConversionToTruthTable.printToTruthTableExerciseAndSolution(formulas, truthTables, input);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "3";
        return result; //TODO
    }

}
