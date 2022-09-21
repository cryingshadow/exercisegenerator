package exercisegenerator.algorithms;

import java.math.*;
import java.util.*;

import exercisegenerator.structures.logic.*;

public class PropositionalLogic {

    public static TruthTable toTruthTable(final PropositionalFormula formula) {
        final List<String> variables = formula.getVariableNames();
        final List<PropositionalInterpretation> interpretations =
            PropositionalLogic.computeAllInterpretations(variables);
        final boolean[] truthValues = new boolean[interpretations.size()];
        int i = 0;
        for (final PropositionalInterpretation interpretation : interpretations) {
            truthValues[i++] = formula.evaluate(interpretation);
        }
        return new TruthTable(variables, truthValues);
    }

    private static List<PropositionalInterpretation> computeAllInterpretations(final List<String> variables) {
        final List<PropositionalInterpretation> result = new LinkedList<PropositionalInterpretation>();
        final BigInteger size = BigInteger.TWO.pow(variables.size());
        BigInteger current = BigInteger.ZERO;
        while (current.compareTo(size) < 0) {
            result.add(PropositionalLogic.toInterpretation(current, variables));
            current = current.add(BigInteger.ONE);
        }
        return result;
    }

    private static PropositionalInterpretation toInterpretation(
        final BigInteger current,
        final List<String> variables
    ) {
        final PropositionalInterpretation result = new PropositionalInterpretation();
        int i = variables.size() - 1;
        for (final String variable : variables) {
            result.put(variable, current.testBit(i--));
        }
        return result;
    }

}
