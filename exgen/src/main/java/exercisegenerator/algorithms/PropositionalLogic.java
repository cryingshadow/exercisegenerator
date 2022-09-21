package exercisegenerator.algorithms;

import java.math.*;
import java.util.*;

import exercisegenerator.structures.logic.*;

public class PropositionalLogic {

    public static PropositionalFormula fromTruthTable(final TruthTable table) {
        final List<Conjunction> disjuncts = table.getModels().stream().map(PropositionalLogic::toConjunction).toList();
        return disjuncts.size() == 1 ? disjuncts.get(0) : new Disjunction(disjuncts);
    }

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
            result.add(TruthTable.toInterpretation(current, variables));
            current = current.add(BigInteger.ONE);
        }
        return result;
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
