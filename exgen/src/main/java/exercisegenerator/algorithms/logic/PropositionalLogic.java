package exercisegenerator.algorithms.logic;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.logic.*;

abstract class PropositionalLogic {

    static PropositionalFormula generateFormula(final Parameters options) {
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
        return formulas.get(0);
    }

    static List<String> generateVariables(final Parameters options) {
        final List<String> variables = new ArrayList<String>();
        final int size = Integer.parseInt(options.getOrDefault(Flag.LENGTH, "3"));
        if (size > 26) {
            throw new IllegalArgumentException("Formulas with more than 26 variables are overkill, really!");
        }
        for (int i = 0; i < size; i++) {
            variables.add(String.valueOf((char)(65 + i)));
        }
        return variables;
    }

    static PropositionalFormula parseFormula(
        final BufferedReader reader,
        final Parameters options
    ) throws IOException {
        try {
            return PropositionalFormula.parse(reader.readLine());
        } catch (final PropositionalFormulaParseException e) {
            throw new IOException(e);
        }
    }

    static void printGeneralFormula(
        final PropositionalFormula formula,
        final BufferedWriter writer
    ) throws IOException {
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

    static void printTruthTable(
        final TruthTable table,
        final boolean empty,
        final boolean large,
        final BufferedWriter writer
    ) throws IOException {
        final Map<PropositionalInterpretation, Boolean> values = table.getAllInterpretationsAndValues();
        final String[][] tableForLaTeX = new String[table.variables.size() + 1][values.size() + 1];
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
        LaTeXUtils.printBeginning(LaTeXUtils.CENTER, writer);
        if (large) {
            writer.write("{\\Large");
            Main.newLine(writer);
        }
        LaTeXUtils.printTable(
            tableForLaTeX,
            Optional.empty(),
            cols -> cols > 1 ? String.format("|*{%d}{C{1em}|}C{4em}|", cols - 1) : "|C{4em}|",
            false,
            0,
            writer
        );
        if (large) {
            writer.write("}");
            Main.newLine(writer);
        }
        LaTeXUtils.printEnd(LaTeXUtils.CENTER, writer);
    }

    static Optional<PropositionalFormula> moveNegationToLiterals(final PropositionalFormula formula) {
        if (formula.isNegation()) {
            final Negation negation = (Negation)formula;
            if (negation.child.isNegation()) {
                return Optional.of(((Negation)negation.child).child);
            }
            if (negation.child.isConjunction()) {
                return Optional.of(
                    Disjunction.createDisjunction(
                        ((Conjunction)negation.child).children
                        .stream()
                        .map(PropositionalFormula::negate)
                    )
                );
            }
            if (negation.child.isDisjunction()) {
                return Optional.of(
                    Conjunction.createConjunction(
                        ((Disjunction)negation.child).children
                        .stream()
                        .map(PropositionalFormula::negate)
                    )
                );
            }
            return Optional.empty();
        }
        if (formula.isConjunction()) {
            final Conjunction conjunction = (Conjunction)formula;
            for (int i = 0; i < conjunction.children.size(); i++) {
                final Optional<PropositionalFormula> changedChild =
                    PropositionalLogic.moveNegationToLiterals(conjunction.children.get(i));
                if (changedChild.isPresent()) {
                    final List<PropositionalFormula> changedChildren =
                        new ArrayList<PropositionalFormula>(conjunction.children);
                    changedChildren.set(i, changedChild.get());
                    return Optional.of(Conjunction.createConjunction(changedChildren));
                }
            }
            return Optional.empty();
        }
        if (formula.isDisjunction()) {
            final Disjunction disjunction = (Disjunction)formula;
            for (int i = 0; i < disjunction.children.size(); i++) {
                final Optional<PropositionalFormula> changedChild =
                    PropositionalLogic.moveNegationToLiterals(disjunction.children.get(i));
                if (changedChild.isPresent()) {
                    final List<PropositionalFormula> changedChildren =
                        new ArrayList<PropositionalFormula>(disjunction.children);
                    changedChildren.set(i, changedChild.get());
                    return Optional.of(Disjunction.createDisjunction(changedChildren));
                }
            }
            return Optional.empty();
        }
        return Optional.empty();
    }

}
