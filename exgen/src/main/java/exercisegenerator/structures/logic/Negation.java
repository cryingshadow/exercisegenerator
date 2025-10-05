package exercisegenerator.structures.logic;

import java.util.*;

public class Negation extends PropositionalFormula {

    public final PropositionalFormula child;

    public Negation(final PropositionalFormula child) {
        this.child = child;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Negation)) {
            return false;
        }
        final Negation other = (Negation)o;
        return this.child.equals(other.child);
    }

    @Override
    public boolean evaluate(final PropositionalInterpretation interpretation) {
        return !this.child.evaluate(interpretation);
    }

    @Override
    public List<PropositionalFormula> getChildren() {
        return List.of(this.child);
    }

    @Override
    public Set<String> getVariableNames() {
        return this.child.getVariableNames();
    }

    @Override
    public int hashCode() {
        return -this.child.hashCode();
    }

    @Override
    public boolean isNegation() {
        return true;
    }

    @Override
    public PropositionalFormula replaceChild(final int index, final PropositionalFormula newChild) {
        return newChild.negate();
    }

    @Override
    public String toString() {
        return String.format("!%s", this.child.toString());
    }

    @Override
    public <T> T visit(final FormulaVisitor<T> visitor) {
        return visitor.onNegation(this.child.visit(visitor));
    }

}
