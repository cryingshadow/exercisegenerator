package exercisegenerator.structures.logic;

import java.util.*;

public class True extends PropositionalFormula {

    public static final True TRUE = new True();

    private True() {

    }

    @Override
    public boolean evaluate(final PropositionalInterpretation interpretation) {
        return true;
    }

    @Override
    public List<String> getVariableNames() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "TRUE";
    }

}
