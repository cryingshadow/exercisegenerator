package exercisegenerator.structures.logic;

import java.util.*;
import java.util.function.*;

import exercisegenerator.structures.*;

public abstract class PropositionalFormula {

    public static PropositionalFormula parse(final String formula) throws PropositionalFormulaParseException {
        final Pair<PropositionalFormula, String> parsed = PropositionalFormula.parse(formula, Function.identity());
        if (parsed.y.isBlank()) {
            return parsed.x;
        }
        return PropositionalFormula.parseInfix(parsed.x, parsed.y.strip());
    }

    private static int findClosingParanthesis(final String formula) {
        final char[] chars = formula.toCharArray();
        int open = 1;
        for (int i = 1; i < chars.length; i++) {
            switch (chars[i]) {
            case '(':
                open++;
                break;
            case ')':
                open--;
                if (open == 0) {
                    return i;
                }
                break;
            }
        }
        return -1;
    }

    private static int findFirstNonWordCharacterIndex(final String formula) {
        final char[] chars = formula.toCharArray();
        int i = 0;
        while (i < chars.length && String.valueOf(chars[i]).matches("\\w")) {
            i++;
        }
        return i;
    }

    private static Pair<PropositionalFormula, String> parse(
        final String formula,
        final Function<PropositionalFormula, PropositionalFormula> transformer
    ) throws PropositionalFormulaParseException {
        final String strippedFormula = formula.strip();
        final char firstChar = strippedFormula.charAt(0);
        if (firstChar == '(') {
            final int index = PropositionalFormula.findClosingParanthesis(strippedFormula);
            if (index < 1) {
                throw new PropositionalFormulaParseException();
            }
            return new Pair<PropositionalFormula, String>(
                transformer.apply(PropositionalFormula.parse(strippedFormula.substring(1, index))),
                strippedFormula.substring(index + 1)
            );
        } else if (firstChar == '!') {
            final Pair<PropositionalFormula, String> parsedNegation =
                PropositionalFormula.parse(strippedFormula.substring(1), Negation::new);
            return new Pair<PropositionalFormula, String>(transformer.apply(parsedNegation.x), parsedNegation.y);
        } else if (String.valueOf(firstChar).matches("\\w")) {
            final Pair<PropositionalFormula, String> parsedVariable =
                PropositionalFormula.parseVariableOrConstant(strippedFormula);
            return new Pair<PropositionalFormula, String>(transformer.apply(parsedVariable.x), parsedVariable.y);
        }
        throw new PropositionalFormulaParseException();
    }

    private static PropositionalFormula parseDisjunction(
        final PropositionalFormula firstDisjunct,
        final String formula
    ) throws PropositionalFormulaParseException {
        final PropositionalFormula secondDisjunct = PropositionalFormula.parse(formula);
        if (secondDisjunct.isDisjunction()) {
            return ((Disjunction)secondDisjunct).prepend(firstDisjunct);
        }
        return new Disjunction(firstDisjunct, secondDisjunct);
    }

    private static PropositionalFormula parseInfix(
        final PropositionalFormula leftChild,
        final String formula
    ) throws PropositionalFormulaParseException {
        if (formula.startsWith("&&")) {
            final Pair<PropositionalFormula, String> parsedConjunction =
                PropositionalFormula.parse(
                    formula.substring(2),
                    rightChild -> rightChild.isConjunction() ?
                        ((Conjunction)rightChild).prepend(leftChild) :
                            new Conjunction(leftChild, rightChild)
                );
            final String remainingFormula = parsedConjunction.y.strip();
            if (remainingFormula.isEmpty()) {
                return parsedConjunction.x;
            }
            if (remainingFormula.startsWith("||")) {
                return PropositionalFormula.parseDisjunction(parsedConjunction.x, remainingFormula.substring(2));
            }
        } else if (formula.startsWith("||")) {
            return PropositionalFormula.parseDisjunction(leftChild, formula.substring(2));
        }
        throw new PropositionalFormulaParseException();
    }

    private static Pair<PropositionalFormula, String> parseVariableOrConstant(final String formula) {
        final int index = PropositionalFormula.findFirstNonWordCharacterIndex(formula);
        final String name = formula.substring(0, index);
        if ("true".equals(name.toLowerCase())) {
            return new Pair<PropositionalFormula, String>(
                True.TRUE,
                formula.substring(index)
            );
        }
        if ("false".equals(name.toLowerCase())) {
            return new Pair<PropositionalFormula, String>(
                False.FALSE,
                formula.substring(index)
            );
        }
        return new Pair<PropositionalFormula, String>(
            new PropositionalVariable(name),
            formula.substring(index)
        );
    }

    public abstract boolean evaluate(PropositionalInterpretation interpretation);

    public abstract List<String> getVariableNames();

    public boolean isConjunction() {
        return false;
    }

    public boolean isDisjunction() {
        return false;
    }

    public boolean isNegation() {
        return false;
    }

    public boolean isVariable() {
        return false;
    }

}
