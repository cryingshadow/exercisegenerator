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
        final List<PropositionalFormula> result = new LinkedList<PropositionalFormula>();
        result.add(formula);
        PropositionalFormula current = formula;
        while (!ConversionToDNF.isInDNF(current)) {
            final Optional<PropositionalFormula> negationMoreInside =
                PropositionalLogic.moveNegationToLiterals(current);
            if (negationMoreInside.isPresent()) {
                current = negationMoreInside.get();
            } else {
                current = ConversionToDNF.applyDeMorganTowardsDNF(current).get();
            }
            result.add(current);
        }
        return result;
    }

    private static Optional<PropositionalFormula> applyDeMorganTowardsDNF(final PropositionalFormula formula) {
        if (formula.isConjunction()) {
            final Conjunction conjunction = (Conjunction)formula;
            for (int i = 0; i < conjunction.children.size(); i++) {
                final PropositionalFormula child = conjunction.children.get(i);
                if (child.isDisjunction()) {
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
                final Optional<PropositionalFormula> changedChild = ConversionToDNF.applyDeMorganTowardsDNF(child);
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
                final Optional<PropositionalFormula> changedChild = ConversionToDNF.applyDeMorganTowardsDNF(child);
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

    private static boolean isConjunctionOfLiterals(final PropositionalFormula formula) {
        if (formula.isDisjunction()) {
            return false;
        }
        if (formula.isConjunction()) {
            final Conjunction conjunction = (Conjunction)formula;
            for (final PropositionalFormula child : conjunction.children) {
                if (!ConversionToDNF.isConjunctionOfLiterals(child)) {
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

    private static boolean isInDNF(final PropositionalFormula formula) {
        if (formula.isDisjunction()) {
            final Disjunction disjunction = (Disjunction)formula;
            for (final PropositionalFormula child : disjunction.children) {
                if (!ConversionToDNF.isInDNF(child)) {
                    return false;
                }
            }
            return true;
        }
        if (formula.isConjunction()) {
            final Conjunction conjunction = (Conjunction)formula;
            for (final PropositionalFormula child : conjunction.children) {
                if (!ConversionToDNF.isConjunctionOfLiterals(child)) {
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

    private static void printToDNFSolution(
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
        ConversionToDNF.printToDNFSolution(steps, input.solutionWriter);
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
