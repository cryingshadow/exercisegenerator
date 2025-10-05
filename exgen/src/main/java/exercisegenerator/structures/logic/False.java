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
    public List<PropositionalFormula> getChildren() {
        return List.of();
    }

    @Override
    public Set<String> getVariableNames() {
        return Set.of();
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public PropositionalFormula replaceChild(final int index, final PropositionalFormula newChild) {
        throw new IllegalStateException("One cannot replace a child of a constant!");
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
