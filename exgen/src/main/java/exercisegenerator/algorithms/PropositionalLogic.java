package exercisegenerator.algorithms;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.logic.*;

public class PropositionalLogic {

    public static PropositionalFormula fromTruthTable(final TruthTable table) {
        final List<Conjunction> disjuncts = table.getModels().stream().map(PropositionalLogic::toConjunction).toList();
        return disjuncts.size() == 1 ? disjuncts.get(0) : new Disjunction(disjuncts);
    }

    public static String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "3";
        return result; //TODO
    }

    public static void toTruthTable(final AlgorithmInput input) throws IOException {
        final List<PropositionalFormula> formulas =
            new ParserAndGenerator<List<PropositionalFormula>>(
                PropositionalLogic::parseFormulas,
                PropositionalLogic::generateFormulas
            ).getResult(input.options);
        final List<TruthTable> truthTables =
            formulas.stream().map(PropositionalLogic::toTruthTable).toList();
        PropositionalLogic.printToTruthTableExerciseAndSolution(formulas, truthTables, input);
    }

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
        final Random gen = new Random();
        final List<String> variables = new ArrayList<String>();
        final int size = Integer.parseInt(options.getOrDefault(Flag.LENGTH, "3"));
        if (size > 26) {
            throw new IllegalArgumentException("Formulas with more than 26 variables are overkill, really!");
        }
        for (int i = 0; i < size; i++) {
            variables.add(String.valueOf((char)(65 + i)));
        }
        final List<PropositionalFormula> formulas = new ArrayList<PropositionalFormula>();
        for (final String name : variables) {
            final PropositionalVariable var = new PropositionalVariable(name);
            formulas.add(gen.nextBoolean() ? var : var.negate());
        }
        final int additional = gen.nextInt(10);
        for (int i = 0; i < additional; i++) {
            final PropositionalVariable var = new PropositionalVariable(variables.get(gen.nextInt(variables.size())));
            formulas.add(gen.nextBoolean() ? var : var.negate());
        }
        while (formulas.size() > 3) {
            final int number = gen.nextInt(formulas.size() - 1) + 1;
            if (number == 1) {
                final PropositionalFormula formula = formulas.remove(gen.nextInt(formulas.size()));
                formulas.add(formula.negate());
            } else {
                final List<PropositionalFormula> children = new LinkedList<PropositionalFormula>();
                for (int i = 0; i < number; i++) {
                    children.add(formulas.remove(gen.nextInt(formulas.size())));
                }
                formulas.add(gen.nextBoolean() ? new Conjunction(children) : new Disjunction(children));
            }
        }
        while (formulas.size() > 1) {
            final int number = formulas.size() > 2 ? gen.nextInt(formulas.size() - 2) + 2 : 2;
            final List<PropositionalFormula> children = new LinkedList<PropositionalFormula>();
            for (int i = 0; i < number; i++) {
                children.add(formulas.remove(gen.nextInt(formulas.size())));
            }
            formulas.add(gen.nextBoolean() ? new Conjunction(children) : new Disjunction(children));
        }
        return formulas;
    }

    private static List<PropositionalFormula> parseFormulas(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        final List<PropositionalFormula> result = new LinkedList<PropositionalFormula>();
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

    private static void printFormula(final PropositionalFormula formula, final BufferedWriter writer)
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
            exWriter.write("Geben Sie die Wahrheitstabelle zu der folgenden aussagenlogischen Formel an:\\\\[2ex]");
        } else {
            exWriter.write(
                "Geben Sie die jeweiligen Wahrheitstabellen zu den folgenden aussagenlogischen Formeln an:\\\\[2ex]"
            );
        }
        Main.newLine(exWriter);
        LaTeXUtils.printSolutionSpaceBeginning(input.options, exWriter);
        for (int i = 0; i < size; i++) {
            final PropositionalFormula formula = formulas.get(i);
            final TruthTable table = truthTables.get(i);
            PropositionalLogic.printFormula(formula, exWriter);
            PropositionalLogic.printTruthTable(table, true, exWriter);
            PropositionalLogic.printFormula(formula, solWriter);
            PropositionalLogic.printTruthTable(table, false, solWriter);
        }
        LaTeXUtils.printSolutionSpaceEnd(input.options, exWriter);
        Main.newLine(solWriter);
    }

    private static void printTruthTable(final TruthTable table, final boolean empty, final BufferedWriter writer)
    throws IOException {
        final Map<PropositionalInterpretation, Boolean> values = table.getAllInterpretationsAndValues();
        final String[][] tableForLaTeX = new String[table.variables.size() + 1][values.size() + 1];
        final String[][] color = new String[table.variables.size() + 1][values.size() + 1];
        int column = 0;
        for (final String name : table.variables) {
            tableForLaTeX[column++][0] = String.format("\\var{%s}", name);
        }
        tableForLaTeX[column][0] = "\\textit{Formel}";
        int row = 1;
        if (!empty) {
            for (final Map.Entry<PropositionalInterpretation, Boolean> entry : values.entrySet()) {
                column = 0;
                final PropositionalInterpretation interpretation = entry.getKey();
                for (final String name : table.variables) {
                    tableForLaTeX[column++][row] = interpretation.get(name) ? "\\code{1}" : "\\code{0}";
                }
                tableForLaTeX[column][row] = entry.getValue() ? "\\code{1}" : "\\code{0}";
                row++;
            }
        }
        LaTeXUtils.printTable(tableForLaTeX, color, "2em", writer, false, 0);
    }

    private static Conjunction toConjunction(final PropositionalInterpretation model) {
        final List<String> names = new ArrayList<String>(model.keySet());
        Collections.sort(names);
        return new Conjunction(
            names.stream()
                .map(name -> new PropositionalVariable(name))
                .map(var -> model.get(var.name) ? var : var.negate())
                .toList()
        );
    }

}
