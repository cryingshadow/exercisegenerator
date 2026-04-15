package exercisegenerator.algorithms.logic;

import java.util.*;

import exercisegenerator.structures.logic.*;

public class ConversionToCNF extends ConversionToNF {

    public static final ConversionToCNF INSTANCE = new ConversionToCNF();

    private ConversionToCNF() {
        super("CNF");
    }

    @Override
    public List<PropositionalFormula> apply(final PropositionalFormula formula) {
        return PropositionalLogic.toNF(formula, true);
    }

    @Override
    public String commandPrefix() {
        return "ToCnf";
    }

}
