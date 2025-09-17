package exercisegenerator.algorithms.logic;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.structures.logic.*;

abstract class PropositionalLogic {

    private static interface CompositeSimplification
    extends BiFunction<List<? extends PropositionalFormula>, Boolean, Optional<PropositionalFormula>> {}

    private static final List<CompositeSimplification> COMPOSITE_SIMPLIFICATION_RULES =
        List.of(
            PropositionalLogic::simplifyEmptyChildren,
            PropositionalLogic::simplifySingleChild,
            PropositionalLogic::simplifyCollapsingConstants,
            PropositionalLogic::simplifyContradiction,
            PropositionalLogic::simplifyDistribution,
            PropositionalLogic::simplifyRemovableConstants,
            PropositionalLogic::simplifyDuplicates,
            PropositionalLogic::simplifyChildRecursively
        );

    static PropositionalFormula generateFormula(final Parameters<Flag> options) {
        final List<String> variables = PropositionalLogic.generateVariables(options);
        final List<PropositionalFormula> formulas = new ArrayList<PropositionalFormula>();
        for (final String name : variables) {
            final PropositionalVariable var = new PropositionalVariable(name);
            formulas.add(Main.RANDOM.nextBoolean() ? var : var.negate());
        }
        final int additional = Main.RANDOM.nextInt(3 * variables.size());
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

    static List<String> generateVariables(final Parameters<Flag> options) {
        final List<String> variables = new ArrayList<String>();
        final int size = AlgorithmImplementation.parseOrGenerateLength(3, 4, options);
        if (size > 26) {
            throw new IllegalArgumentException("Formulas with more than 26 variables are overkill, really!");
        }
        for (int i = 0; i < size; i++) {
            variables.add(String.valueOf((char)(65 + i)));
        }
        return variables;
    }

    static List<PropositionalFormula> parseFormulas(
        final BufferedReader reader,
        final Parameters<Flag> options
    ) throws IOException {
        final List<PropositionalFormula> result = new ArrayList<PropositionalFormula>();
        try {
            String line = reader.readLine();
            while (line != null) {
                if (!line.isBlank()) {
                    result.add(PropositionalFormula.parse(line));
                }
                line = reader.readLine();
            }
        } catch (final PropositionalFormulaParseException e) {
            throw new IOException(e);
        }
        return result;
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
        for (final Map.Entry<PropositionalInterpretation, Boolean> entry : values.entrySet()) {
            column = 0;
            final PropositionalInterpretation interpretation = entry.getKey();
            for (final String name : table.variables) {
                tableForLaTeX[column++][row] = interpretation.get(name) ? "\\code{1}" : "\\code{0}";
            }
            if (!empty) {
                tableForLaTeX[column][row] = entry.getValue() ? "\\code{1}" : "\\code{0}";
            }
            row++;
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

    static Set<Clause> toClauses(final PropositionalFormula formula) {
        final PropositionalFormula cnf = PropositionalLogic.toNF(formula, true).getLast();
        if (True.TRUE.equals(cnf)) {
            return Collections.emptySet();
        }
        if (False.FALSE.equals(cnf)) {
            return Set.of(new Clause());
        }
        if (cnf.isConjunction()) {
            return ((Conjunction)cnf).children.stream().map(PropositionalLogic::toClause).collect(Collectors.toSet());
        }
        return Set.of(PropositionalLogic.toClause(cnf));
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
                        PropositionalLogic.applyDistributiveLaw(current, shouldBeCNF);
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

    private static Optional<PropositionalFormula> applyDistributiveLaw(
        final List<? extends PropositionalFormula> childrenOfFormula,
        final int indexOfSuitableChild,
        final PropositionalFormula suitableChild,
        final boolean formulaIsConjunction
    ) {
        final List<PropositionalFormula> innerChildren = new ArrayList<PropositionalFormula>(childrenOfFormula);
        innerChildren.remove(indexOfSuitableChild);
        final PropositionalFormula factor =
            formulaIsConjunction ?
                Conjunction.createConjunction(innerChildren) :
                    Disjunction.createDisjunction(innerChildren);
        final Function<PropositionalFormula, PropositionalFormula> mapper =
            formulaIsConjunction ?
                x -> Conjunction.createConjunction(x, factor) :
                    x -> Disjunction.createDisjunction(x, factor);
        final Stream<PropositionalFormula> outerChildren =
            PropositionalLogic.getChildren(suitableChild).get().stream().map(mapper);
        return Optional.of(
            formulaIsConjunction ?
                Disjunction.createDisjunction(outerChildren) :
                    Conjunction.createConjunction(outerChildren)
        );
    }

    private static Optional<PropositionalFormula> applyDistributiveLaw(
        final PropositionalFormula formula,
        final boolean shouldBeCNF
    ) {
        final Optional<List<? extends PropositionalFormula>> optionalChildren = PropositionalLogic.getChildren(formula);
        if (optionalChildren.isEmpty()) {
            return Optional.empty();
        }
        final List<? extends PropositionalFormula> children = optionalChildren.get();
        final boolean conjunction = formula.isConjunction();
        final boolean needsDistribution = shouldBeCNF != conjunction;
        final Predicate<PropositionalFormula> suitableChild =
            conjunction ? PropositionalFormula::isDisjunction : PropositionalFormula::isConjunction;
        for (int i = 0; i < children.size(); i++) {
            final PropositionalFormula child = children.get(i);
            if (needsDistribution && suitableChild.test(child)) {
                return PropositionalLogic.applyDistributiveLaw(children, i, child, conjunction);
            }
            final Optional<PropositionalFormula> changedChild =
                PropositionalLogic.applyDistributiveLaw(child, shouldBeCNF);
            if (changedChild.isPresent()) {
                final List<PropositionalFormula> changedChildren = new ArrayList<PropositionalFormula>(children);
                changedChildren.set(i, changedChild.get());
                return Optional.of(
                    conjunction ?
                        Conjunction.createConjunction(changedChildren) :
                            Disjunction.createDisjunction(changedChildren)
                );
            }
        }
        return Optional.empty();
    }

    private static Optional<List<? extends PropositionalFormula>> getChildren(final PropositionalFormula formula) {
        if (formula.isConjunction()) {
            return Optional.of(((Conjunction)formula).children);
        }
        if (formula.isDisjunction()) {
            return Optional.of(((Disjunction)formula).children);
        }
        return Optional.empty();
    }

    private static boolean isInNF(final PropositionalFormula formula, final boolean shouldBeCNF) {
        if (formula.isNegation()) {
            final Negation negation = (Negation)formula;
            return negation.child.isConstant() || negation.child.isVariable();
        }
        final Optional<List<? extends PropositionalFormula>> optionalChildren = PropositionalLogic.getChildren(formula);
        if (optionalChildren.isEmpty()) {
            return true;
        }
        final List<? extends PropositionalFormula> children = optionalChildren.get();
        for (final PropositionalFormula child : children) {
            if (shouldBeCNF == formula.isConjunction()) {
                if (!PropositionalLogic.isInNF(child, shouldBeCNF)) {
                    return false;
                }
            } else if (!PropositionalLogic.isJunctionOfLiterals(child, !shouldBeCNF)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isJunctionOfLiterals(final PropositionalFormula formula, final boolean shouldBeConjunction) {
        if (formula.isNegation()) {
            final Negation negation = (Negation)formula;
            return negation.child.isConstant() || negation.child.isVariable();
        }
        final Optional<List<? extends PropositionalFormula>> optionalChildren = PropositionalLogic.getChildren(formula);
        if (optionalChildren.isEmpty()) {
            return true;
        }
        final List<? extends PropositionalFormula> children = optionalChildren.get();
        if (shouldBeConjunction != formula.isConjunction()) {
            return false;
        }
        for (final PropositionalFormula child : children) {
            if (!PropositionalLogic.isJunctionOfLiterals(child, shouldBeConjunction)) {
                return false;
            }
        }
        return true;
    }

    private static Optional<PropositionalFormula> moveNegationToLiterals(final PropositionalFormula formula) {
        if (formula.isNegation()) {
            final Negation negation = (Negation)formula;
            if (negation.child.isNegation()) {
                return Optional.of(((Negation)negation.child).child);
            }
            final Optional<List<? extends PropositionalFormula>> optionalChildren =
                PropositionalLogic.getChildren(negation.child);
            if (optionalChildren.isPresent()) {
                final Stream<PropositionalFormula> negated =
                    optionalChildren.get().stream().map(PropositionalFormula::negate);
                return Optional.of(
                    negation.child.isConjunction() ?
                        Disjunction.createDisjunction(negated) :
                            Conjunction.createConjunction(negated)
                );
            }
            return Optional.empty();
        }
        final Optional<List<? extends PropositionalFormula>> optionalChildren = PropositionalLogic.getChildren(formula);
        if (optionalChildren.isPresent()) {
            final List<? extends PropositionalFormula> children = optionalChildren.get();
            for (int i = 0; i < children.size(); i++) {
                final Optional<PropositionalFormula> changedChild =
                    PropositionalLogic.moveNegationToLiterals(children.get(i));
                if (changedChild.isPresent()) {
                    final List<PropositionalFormula> changedChildren =
                        new ArrayList<PropositionalFormula>(children);
                    changedChildren.set(i, changedChild.get());
                    return Optional.of(
                        formula.isConjunction() ?
                            Conjunction.createConjunction(changedChildren) :
                                Disjunction.createDisjunction(changedChildren)
                    );
                }
            }
        }
        return Optional.empty();
    }

    private static Optional<PropositionalFormula> simplify(final PropositionalFormula formula) {
        final Optional<PropositionalFormula> simplifiedNegation = PropositionalLogic.simplifyNegation(formula);
        if (simplifiedNegation.isPresent()) {
            return simplifiedNegation;
        }
        final Optional<List<? extends PropositionalFormula>> optionalChildren = PropositionalLogic.getChildren(formula);
        if (optionalChildren.isEmpty()) {
            return Optional.empty();
        }
        final List<? extends PropositionalFormula> children = optionalChildren.get();
        final Boolean conjunction = formula.isConjunction();
        for (
            final CompositeSimplification simplificationRule :
                PropositionalLogic.COMPOSITE_SIMPLIFICATION_RULES
        ) {
            final Optional<PropositionalFormula> simplified = simplificationRule.apply(children, conjunction);
            if (simplified.isPresent()) {
                return simplified;
            }
        }
        return Optional.empty();
    }

    private static Optional<PropositionalFormula> simplifyChildRecursively(
        final List<? extends PropositionalFormula> children,
        final Boolean conjunction
    ) {
        for (int i = 0; i < children.size(); i++) {
            final PropositionalFormula child = children.get(i);
            final Optional<PropositionalFormula> simplified = PropositionalLogic.simplify(child);
            if (simplified.isPresent()) {
                final List<PropositionalFormula> newChildren = new ArrayList<PropositionalFormula>(children);
                newChildren.set(i, simplified.get());
                return Optional.of(
                    conjunction ?
                        Conjunction.createConjunction(newChildren) :
                            Disjunction.createDisjunction(newChildren)
                );
            }
        }
        return Optional.empty();
    }

    private static Optional<PropositionalFormula> simplifyCollapsingConstants(
        final List<? extends PropositionalFormula> children,
        final Boolean conjunction
    ) {
        final PropositionalFormula collapsingConstant = conjunction ? False.FALSE : True.TRUE;
        if (children.contains(collapsingConstant)) {
            return Optional.of(collapsingConstant);
        }
        return Optional.empty();
    }

    private static Optional<PropositionalFormula> simplifyContradiction(
        final List<? extends PropositionalFormula> children,
        final Boolean conjunction
    ) {
        final Pair<Set<PropositionalFormula>, Set<PropositionalFormula>> posNeg =
            PropositionalLogic.splitChildrenBySign(children);
        posNeg.x.retainAll(posNeg.y);
        if (!posNeg.x.isEmpty()) {
            return Optional.of(conjunction ? False.FALSE : True.TRUE);
        }
        return Optional.empty();
    }

    private static Optional<PropositionalFormula> simplifyDistribution(
        final List<? extends PropositionalFormula> children,
        final Boolean conjunction
    ) {
        for (int i = 0; i < children.size(); i++) {
            final PropositionalFormula child = children.get(i);
            if (conjunction ? !child.isDisjunction() : !child.isConjunction()) {
                continue;
            }
            final List<? extends PropositionalFormula> childChildren = PropositionalLogic.getChildren(child).get();
            for (final PropositionalFormula otherChild : children) {
                if (child.equals(otherChild)) {
                    continue;
                }
                final List<? extends PropositionalFormula> otherChildChildren =
                    conjunction ?
                        (otherChild.isDisjunction() ? ((Disjunction)otherChild).children : List.of(otherChild)) :
                            (otherChild.isConjunction() ? ((Conjunction)otherChild).children : List.of(otherChild));
                if (childChildren.containsAll(otherChildChildren)) {
                    final List<PropositionalFormula> changedChildren = new ArrayList<PropositionalFormula>(children);
                    changedChildren.remove(i);
                    return Optional.of(
                        conjunction ?
                            Conjunction.createConjunction(changedChildren) :
                                Disjunction.createDisjunction(changedChildren)
                    );
                }
            }
        }
        return Optional.empty();
    }

    private static Optional<PropositionalFormula> simplifyDuplicates(
        final List<? extends PropositionalFormula> children,
        final Boolean conjunction
    ) {
        final Set<PropositionalFormula> distinct = new LinkedHashSet<PropositionalFormula>(children);
        if (distinct.size() < children.size()) {
            return Optional.of(
                conjunction ?
                    Conjunction.createConjunction(distinct.stream()) :
                        Disjunction.createDisjunction(distinct.stream())
            );
        }
        return Optional.empty();
    }

    private static Optional<PropositionalFormula> simplifyEmptyChildren(
        final List<? extends PropositionalFormula> children,
        final Boolean conjunction
    ) {
        if (children.isEmpty()) {
            return Optional.of(conjunction ? False.FALSE : True.TRUE);
        }
        return Optional.empty();
    }

    private static Optional<PropositionalFormula> simplifyNegation(final PropositionalFormula formula) {
        if (!formula.isNegation()) {
            return Optional.empty();
        }
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
        return Optional.empty();
    }

    private static Optional<PropositionalFormula> simplifyRemovableConstants(
        final List<? extends PropositionalFormula> children,
        final Boolean conjunction
    ) {
        final PropositionalFormula removableConstant = conjunction ? True.TRUE : False.FALSE;
        if (children.contains(removableConstant)) {
            final Stream<? extends PropositionalFormula> reduced =
                children.stream().filter(child -> !removableConstant.equals(child));
            return Optional.of(
                conjunction ? Conjunction.createConjunction(reduced) : Disjunction.createDisjunction(reduced)
            );
        }
        return Optional.empty();
    }

    private static Optional<PropositionalFormula> simplifySingleChild(
        final List<? extends PropositionalFormula> children,
        final Boolean conjunction
    ) {
        if (children.size() == 1) {
            return Optional.of(children.get(0));
        }
        return Optional.empty();
    }

    private static Pair<Set<PropositionalFormula>, Set<PropositionalFormula>> splitChildrenBySign(
        final List<? extends PropositionalFormula> children
    ) {
        final Set<PropositionalFormula> positive = new LinkedHashSet<PropositionalFormula>();
        final Set<PropositionalFormula> negative = new LinkedHashSet<PropositionalFormula>();
        for (final PropositionalFormula child : children) {
            if (child.isNegation()) {
                negative.add(((Negation)child).child);
            } else {
                positive.add(child);
            }
        }
        return new Pair<Set<PropositionalFormula>, Set<PropositionalFormula>>(positive, negative);
    }

    private static Clause toClause(final PropositionalFormula simplifiedPurelyDisjunctiveFormula) {
        if (simplifiedPurelyDisjunctiveFormula.isDisjunction()) {
            return new Clause(
                ((Disjunction)simplifiedPurelyDisjunctiveFormula).children.stream().map(PropositionalLogic::toLiteral)
            );
        }
        return new Clause(PropositionalLogic.toLiteral(simplifiedPurelyDisjunctiveFormula));
    }

    private static Literal toLiteral(final PropositionalFormula nonConstantLiteral) {
        if (nonConstantLiteral.isNegation()) {
            return new Literal((PropositionalVariable)((Negation)nonConstantLiteral).child, true);
        }
        return new Literal((PropositionalVariable)nonConstantLiteral, false);
    }

}
