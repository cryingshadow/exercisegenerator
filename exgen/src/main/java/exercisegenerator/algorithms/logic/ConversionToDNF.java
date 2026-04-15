package exercisegenerator.algorithms.logic;

import java.util.*;

import exercisegenerator.structures.logic.*;

public class ConversionToDNF extends ConversionToNF {

    public static final ConversionToDNF INSTANCE = new ConversionToDNF();

    private ConversionToDNF() {
        super("DNF");
    }

    @Override
    public List<PropositionalFormula> apply(final PropositionalFormula formula) {
        return PropositionalLogic.toNF(formula, false);
    }

    @Override
    public String commandPrefix() {
        return "ToDnf";
    }

}
