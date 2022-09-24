package exercisegenerator.structures.logic;

import java.util.*;
import java.util.stream.*;

public class Disjunction extends PropositionalFormula {

    public static PropositionalFormula createDisjunction(final List<? extends PropositionalFormula> children) {
        if (children.isEmpty()) {
            return False.FALSE;
        }
        if (children.size() == 1) {
            return children.get(0);
        }
        return new Disjunction(children);
    }

    public static PropositionalFormula createDisjunction(final PropositionalFormula... children) {
        return Disjunction.createDisjunction(Arrays.asList(children));
    }

    public final List<? extends PropositionalFormula> children;

    private Disjunction(final List<? extends PropositionalFormula> children) {
        this.children = children;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Disjunction)) {
            return false;
        }
        final Disjunction other = (Disjunction)o;
        return this.children.equals(other.children);
    }

    @Override
    public boolean evaluate(final PropositionalInterpretation interpretation) {
        for (final PropositionalFormula child : this.children) {
            if (child.evaluate(interpretation)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> getVariableNames() {
        final Set<String> variables = new LinkedHashSet<String>();
        this.children
            .stream()
            .map(PropositionalFormula::getVariableNames)
            .forEach(list -> variables.addAll(list));
        final List<String> result = new ArrayList<String>(variables);
        Collections.sort(result);
        return result;
    }

    @Override
    public int hashCode() {
        return this.children.hashCode() * 31;
    }

    @Override
    public boolean isDisjunction() {
        return true;
    }

    public Disjunction prepend(final PropositionalFormula firstChild) {
        final LinkedList<PropositionalFormula> newChildren = new LinkedList<PropositionalFormula>(this.children);
        newChildren.addFirst(firstChild);
        return new Disjunction(newChildren);
    }

    @Override
    public String toString() {
        return String.format(
            "(%s)",
            this.children.stream().map(PropositionalFormula::toString).collect(Collectors.joining(" || "))
        );
    }

    @Override
    public <T> T visit(final FormulaVisitor<T> visitor) {
        return visitor.onDisjunction(this.children.stream().map(child -> child.visit(visitor)).toList());
    }

}
