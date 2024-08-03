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

    static void printFormulaEquivalencesSolution(
        final List<PropositionalFormula> steps,
        final BufferedWriter writer
    ) throws IOException {
        boolean first = true;
        for (final PropositionalFormula step : steps) {
            if (first) {
                first = false;
            } else {
                writer.write("\\[\\equiv\\]");
                Main.newLine(writer);
            }
            PropositionalLogic.printGeneralFormula(step, writer);
        }
        Main.newLine(writer);
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

    static List<PropositionalFormula> toNF(final PropositionalFormula formula, final boolean shouldBeCNF) {
        final List<PropositionalFormula> result = new LinkedList<PropositionalFormula>();
        result.add(formula);
        PropositionalFormula current = formula;
        while (!PropositionalLogic.isInNF(current, shouldBeCNF)) {
            final Optional<PropositionalFormula> simplified = PropositionalLogic.simplify(current);
            if (simplified.isPresent()) {
                current = simplified.get();
            } else {
                final Optional<PropositionalFormula> negationMoreInside =
                    PropositionalLogic.moveNegationToLiterals(current);
                if (negationMoreInside.isPresent()) {
                    current = negationMoreInside.get();
                } else {
                    final Optional<PropositionalFormula> deMorgan =
                        PropositionalLogic.applyDeMorgan(current, shouldBeCNF);
                    if (deMorgan.isPresent()) {
                        current = deMorgan.get();
                    } else {
                        current = null;
                    }
                }
            }
            if (current != null) {
                result.add(current);
            }
        }
        Optional<PropositionalFormula> simplified = PropositionalLogic.simplify(current);
        while (simplified.isPresent()) {
            result.add(simplified.get());
            simplified = PropositionalLogic.simplify(simplified.get());
        }
        return result;
    }

    private static Optional<PropositionalFormula> applyDeMorgan(
        final PropositionalFormula formula,
        final boolean shouldBeCNF
    ) {
        if (formula.isConjunction()) {
            final Conjunction conjunction = (Conjunction)formula;
            for (int i = 0; i < conjunction.children.size(); i++) {
                final PropositionalFormula child = conjunction.children.get(i);
                if (!shouldBeCNF && child.isDisjunction()) {
                    final Disjunction disjunction = (Disjunction)child;
                    final List<PropositionalFormula> conjuncts = new LinkedList<PropositionalFormula>();
                    for (int j = 0; j < conjunction.children.size(); j++) {
                        if (i != j) {
                            conjuncts.add(conjunction.children.get(j));
                        }
                    }
                    final PropositionalFormula conjunct = Conjunction.createConjunction(conjuncts);
                    return Optional.of(
                        Disjunction.createDisjunction(
                            disjunction.children
                            .stream()
                            .map(disjunct -> Conjunction.createConjunction(disjunct, conjunct))
                        )
                    );
                }
                final Optional<PropositionalFormula> changedChild =
                    PropositionalLogic.applyDeMorgan(child, shouldBeCNF);
                if (changedChild.isPresent()) {
                    final List<PropositionalFormula> conjuncts =
                        new ArrayList<PropositionalFormula>(conjunction.children);
                    conjuncts.set(i, changedChild.get());
                    return Optional.of(Conjunction.createConjunction(conjuncts));
                }
            }
        }
        if (formula.isDisjunction()) {
            final Disjunction disjunction = (Disjunction)formula;
            for (int i = 0; i < disjunction.children.size(); i++) {
                final PropositionalFormula child = disjunction.children.get(i);
                if (shouldBeCNF && child.isConjunction()) {
                    final Conjunction conjunction = (Conjunction)child;
                    final List<PropositionalFormula> disjuncts = new LinkedList<PropositionalFormula>();
                    for (int j = 0; j < disjunction.children.size(); j++) {
                        if (i != j) {
                            disjuncts.add(disjunction.children.get(j));
                        }
                    }
                    final PropositionalFormula disjunct = Disjunction.createDisjunction(disjuncts);
                    return Optional.of(
                        Conjunction.createConjunction(
                            conjunction.children
                            .stream()
                            .map(conjunct -> Disjunction.createDisjunction(conjunct, disjunct))
                        )
                    );
                }
                final Optional<PropositionalFormula> changedChild =
                    PropositionalLogic.applyDeMorgan(child, shouldBeCNF);
                if (changedChild.isPresent()) {
                    final List<PropositionalFormula> disjuncts =
                        new ArrayList<PropositionalFormula>(disjunction.children);
                    disjuncts.set(i, changedChild.get());
                    return Optional.of(Disjunction.createDisjunction(disjuncts));
                }
            }
        }
        return Optional.empty();
    }

    private static boolean isInNF(final PropositionalFormula formula, final boolean shouldBeCNF) {
        if (formula.isDisjunction()) {
            final Disjunction disjunction = (Disjunction)formula;
            for (final PropositionalFormula child : disjunction.children) {
                if (shouldBeCNF) {
                    if (!PropositionalLogic.isJunctionOfLiterals(child, false)) {
                        return false;
                    }
                } else {
                    if (!PropositionalLogic.isInNF(child, shouldBeCNF)) {
                        return false;
                    }
                }
            }
            return true;
        }
        if (formula.isConjunction()) {
            final Conjunction conjunction = (Conjunction)formula;
            for (final PropositionalFormula child : conjunction.children) {
                if (shouldBeCNF) {
                    if (!PropositionalLogic.isInNF(child, shouldBeCNF)) {
                        return false;
                    }
                } else {
                    if (!PropositionalLogic.isJunctionOfLiterals(child, true)) {
                        return false;
                    }
                }
            }
            return true;
        }
        if (formula.isNegation()) {
            final Negation negation = (Negation)formula;
            return negation.child.isConstant() || negation.child.isVariable();
        }
        return true;
    }

    private static boolean isJunctionOfLiterals(final PropositionalFormula formula, final boolean shouldBeConjunction) {
        if (formula.isDisjunction()) {
            if (shouldBeConjunction) {
                return false;
            }
            final Disjunction disjunction = (Disjunction)formula;
            for (final PropositionalFormula child : disjunction.children) {
                if (!PropositionalLogic.isJunctionOfLiterals(child, shouldBeConjunction)) {
                    return false;
                }
            }
            return true;
        }
        if (formula.isConjunction()) {
            if (!shouldBeConjunction) {
                return false;
            }
            final Conjunction conjunction = (Conjunction)formula;
            for (final PropositionalFormula child : conjunction.children) {
                if (!PropositionalLogic.isJunctionOfLiterals(child, shouldBeConjunction)) {
                    return false;
                }
            }
            return true;
        }
        if (formula.isNegation()) {
            final Negation negation = (Negation)formula;
            return negation.child.isConstant() || negation.child.isVariable();
        }
        return true;
    }

    private static Optional<PropositionalFormula> moveNegationToLiterals(final PropositionalFormula formula) {
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

    private static Optional<PropositionalFormula> simplify(final PropositionalFormula formula) {
        if (formula.isConjunction()) {
            final Conjunction conjunction = (Conjunction)formula;
            if (conjunction.children.isEmpty()) {
                return Optional.of(True.TRUE);
            }
            if (conjunction.children.size() == 1) {
                return Optional.of(conjunction.children.get(0));
            }
            final Set<PropositionalFormula> distinct = new LinkedHashSet<PropositionalFormula>();
            final Set<PropositionalFormula> positive = new LinkedHashSet<PropositionalFormula>();
            final Set<PropositionalFormula> negative = new LinkedHashSet<PropositionalFormula>();
            for (int i = 0; i < conjunction.children.size(); i++) {
                final PropositionalFormula child = conjunction.children.get(i);
                if (child.isConstant()) {
                    if (True.TRUE.equals(child)) {
                        final List<PropositionalFormula> changedChildren =
                            new ArrayList<PropositionalFormula>(conjunction.children);
                        changedChildren.remove(i);
                        return Optional.of(Conjunction.createConjunction(changedChildren));
                    }
                    return Optional.of(False.FALSE);
                }
                final Optional<PropositionalFormula> changedChild = PropositionalLogic.simplify(child);
                if (changedChild.isPresent()) {
                    final List<PropositionalFormula> changedChildren =
                        new ArrayList<PropositionalFormula>(conjunction.children);
                    changedChildren.set(i, changedChild.get());
                    return Optional.of(Conjunction.createConjunction(changedChildren));
                }
                distinct.add(child);
                if (child.isNegation()) {
                    negative.add(((Negation)child).child);
                } else {
                    positive.add(child);
                }
            }
            positive.retainAll(negative);
            if (!positive.isEmpty()) {
                return Optional.of(False.FALSE);
            }
            if (distinct.size() < conjunction.children.size()) {
                return Optional.of(Conjunction.createConjunction(distinct.stream()));
            }
        }
        if (formula.isDisjunction()) {
            final Disjunction disjunction = (Disjunction)formula;
            if (disjunction.children.isEmpty()) {
                return Optional.of(False.FALSE);
            }
            if (disjunction.children.size() == 1) {
                return Optional.of(disjunction.children.get(0));
            }
            final Set<PropositionalFormula> distinct = new LinkedHashSet<PropositionalFormula>();
            final Set<PropositionalFormula> positive = new LinkedHashSet<PropositionalFormula>();
            final Set<PropositionalFormula> negative = new LinkedHashSet<PropositionalFormula>();
            for (int i = 0; i < disjunction.children.size(); i++) {
                final PropositionalFormula child = disjunction.children.get(i);
                if (child.isConstant()) {
                    if (True.TRUE.equals(child)) {
                        return Optional.of(True.TRUE);
                    }
                    final List<PropositionalFormula> changedChildren =
                        new ArrayList<PropositionalFormula>(disjunction.children);
                    changedChildren.remove(i);
                    return Optional.of(Disjunction.createDisjunction(changedChildren));
                }
                final Optional<PropositionalFormula> changedChild = PropositionalLogic.simplify(child);
                if (changedChild.isPresent()) {
                    final List<PropositionalFormula> changedChildren =
                        new ArrayList<PropositionalFormula>(disjunction.children);
                    changedChildren.set(i, changedChild.get());
                    return Optional.of(Disjunction.createDisjunction(changedChildren));
                }
                distinct.add(child);
                if (child.isNegation()) {
                    negative.add(((Negation)child).child);
                } else {
                    positive.add(child);
                }
            }
            positive.retainAll(negative);
            if (!positive.isEmpty()) {
                return Optional.of(True.TRUE);
            }
            if (distinct.size() < disjunction.children.size()) {
                return Optional.of(Disjunction.createDisjunction(distinct.stream()));
            }
        }
        if (formula.isNegation()) {
            final Negation negation = (Negation)formula;
            if (negation.child.isConstant()) {
                return Optional.of(True.TRUE.equals(negation.child) ? False.FALSE : True.TRUE);
            }
            if (negation.child.isNegation()) {
                return Optional.of(((Negation)negation.child).child);
            }
            final Optional<PropositionalFormula> changedChild = PropositionalLogic.simplify(negation.child);
            if (changedChild.isPresent()) {
                return Optional.of(changedChild.get().negate());
            }
        }
        return Optional.empty();
    }

}
