package exercisegenerator.structures.logic;

import java.util.*;

public class Implication extends PropositionalFormula {

    public final PropositionalFormula antecedence;

    public final PropositionalFormula consequence;

    public Implication(final PropositionalFormula antecedence, final PropositionalFormula consequence) {
        this.antecedence = antecedence;
        this.consequence = consequence;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof final Implication other) {
            return this.antecedence.equals(other.antecedence) && this.consequence.equals(other.consequence);
        }
        return false;
    }

    @Override
    public boolean evaluate(final PropositionalInterpretation interpretation) {
        return !this.antecedence.evaluate(interpretation) || this.consequence.evaluate(interpretation);
    }

    @Override
    public List<PropositionalFormula> getChildren() {
        return List.of(this.antecedence, this.consequence);
    }

    @Override
    public Set<String> getVariableNames() {
        final Set<String> result = new TreeSet<String>();
        result.addAll(this.antecedence.getVariableNames());
        result.addAll(this.consequence.getVariableNames());
        return result;
    }

    @Override
    public int hashCode() {
        return this.antecedence.hashCode() * 3 + this.consequence.hashCode() * 7;
    }

    @Override
    public boolean isImplication() {
        return true;
    }

    @Override
    public PropositionalFormula replaceChild(final int index, final PropositionalFormula newChild) {
        return index == 0 ? new Implication(newChild, this.consequence) : new Implication(this.antecedence, newChild);
    }

    @Override
    public String toString() {
        return String.format("(%s -> %s)", this.antecedence.toString(), this.consequence.toString());
    }

    @Override
    public <T> T visit(final FormulaVisitor<T> visitor) {
        return visitor.onImplication(this.antecedence.visit(visitor), this.consequence.visit(visitor));
    }

}
