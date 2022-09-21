package exercisegenerator.structures.logic;

import java.util.*;

public class False extends PropositionalFormula {

    public static final False FALSE = new False();

    private False() {
    }

    @Override
    public boolean equals(final Object o) {
        return this == o;
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
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "FALSE";
    }

    @Override
    public <T> T visit(final FormulaVisitor<T> visitor) {
        return visitor.onFalse();
    }

}
