package exercisegenerator.structures.logic;

import java.util.*;

public class False extends PropositionalFormula {

    public static final False FALSE = new False();

    private False() {

    }

    @Override
    public boolean evaluate(final PropositionalInterpretation interpretation) {
        return false;
    }

    @Override
    public List<String> getVariableNames() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "FALSE";
    }

}
