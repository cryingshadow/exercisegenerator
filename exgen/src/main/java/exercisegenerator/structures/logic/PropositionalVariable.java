package exercisegenerator.structures.logic;

import java.util.*;

public class PropositionalVariable extends PropositionalFormula {

    public final String name;

    public PropositionalVariable(final String name) {
        this.name = name;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof PropositionalVariable)) {
            return false;
        }
        final PropositionalVariable other = (PropositionalVariable)o;
        return this.name.equals(other.name);
    }

    @Override
    public boolean evaluate(final PropositionalInterpretation interpretation) {
        if (!interpretation.containsKey(this.name)) {
            throw new IllegalArgumentException(
                String.format("Interpretation does not interpret variable %s!", this.name)
            );
        }
        return interpretation.get(this.name);
    }

    @Override
    public List<PropositionalFormula> getChildren() {
        return List.of();
    }

    @Override
    public Set<String> getVariableNames() {
        return Collections.singleton(this.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() * 3;
    }

    @Override
    public boolean isVariable() {
        return true;
    }

    @Override
    public PropositionalFormula replaceChild(final int index, final PropositionalFormula newChild) {
        throw new IllegalStateException("One cannot replace a child of a variable!");
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public <T> T visit(final FormulaVisitor<T> visitor) {
        return visitor.onVariable(this.name);
    }

}
