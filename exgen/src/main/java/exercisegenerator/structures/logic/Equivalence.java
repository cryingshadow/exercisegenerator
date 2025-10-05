package exercisegenerator.structures.logic;

import java.util.*;

public class Equivalence extends PropositionalFormula {

    public final PropositionalFormula left;

    public final PropositionalFormula right;

    public Equivalence(final PropositionalFormula left, final PropositionalFormula right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof final Equivalence other) {
            return this.left.equals(other.left) && this.right.equals(other.right);
        }
        return false;
    }

    @Override
    public boolean evaluate(final PropositionalInterpretation interpretation) {
        return this.left.evaluate(interpretation) == this.right.evaluate(interpretation);
    }

    @Override
    public List<PropositionalFormula> getChildren() {
        return List.of(this.left, this.right);
    }

    @Override
    public Set<String> getVariableNames() {
        final Set<String> result = new TreeSet<String>();
        result.addAll(this.left.getVariableNames());
        result.addAll(this.right.getVariableNames());
        return result;
    }

    @Override
    public int hashCode() {
        return this.left.hashCode() * 5 + this.right.hashCode() * 11;
    }

    @Override
    public boolean isEquivalence() {
        return true;
    }

    @Override
    public PropositionalFormula replaceChild(final int index, final PropositionalFormula newChild) {
        return index == 0 ? new Equivalence(newChild, this.right) : new Equivalence(this.left, newChild);
    }

    @Override
    public String toString() {
        return String.format("(%s <-> %s)", this.left.toString(), this.right.toString());
    }

    @Override
    public <T> T visit(final FormulaVisitor<T> visitor) {
        return visitor.onEquivalence(this.left.visit(visitor), this.right.visit(visitor));
    }

}
