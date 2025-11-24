package exercisegenerator.structures.logic;

import java.util.*;

public class Converse extends PropositionalFormula {

    public final PropositionalFormula antecedence;

    public final PropositionalFormula consequence;

    public Converse(final PropositionalFormula consequence, final PropositionalFormula antecedence) {
        this.antecedence = antecedence;
        this.consequence = consequence;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof final Converse other) {
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
        return List.of(this.consequence, this.antecedence);
    }

    @Override
    public Set<String> getVariableNames() {
        final Set<String> result = new TreeSet<String>();
        result.addAll(this.consequence.getVariableNames());
        result.addAll(this.antecedence.getVariableNames());
        return result;
    }

    @Override
    public int hashCode() {
        return this.antecedence.hashCode() * 5 + this.consequence.hashCode() * 2;
    }

    @Override
    public boolean isConverse() {
        return true;
    }

    @Override
    public PropositionalFormula replaceChild(final int index, final PropositionalFormula newChild) {
        return index == 0 ? new Converse(newChild, this.antecedence) : new Converse(this.consequence, newChild);
    }

    @Override
    public String toString() {
        return String.format("(%s <- %s)", this.consequence.toString(), this.antecedence.toString());
    }

    @Override
    public <T> T visit(final FormulaVisitor<T> visitor) {
        return visitor.onConverse(this.consequence.visit(visitor), this.antecedence.visit(visitor));
    }

}
