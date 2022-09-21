package exercisegenerator.structures.logic;

import java.util.*;
import java.util.stream.*;

public class Conjunction extends PropositionalFormula {

    public final List<? extends PropositionalFormula> children;

    public Conjunction(final List<? extends PropositionalFormula> children) {
        this.children = children;
    }

    public Conjunction(final PropositionalFormula... children) {
        this(Arrays.asList(children));
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Conjunction)) {
            return false;
        }
        final Conjunction other = (Conjunction)o;
        return this.children.equals(other.children);
    }

    @Override
    public boolean evaluate(final PropositionalInterpretation interpretation) {
        for (final PropositionalFormula child : this.children) {
            if (!child.evaluate(interpretation)) {
                return false;
            }
        }
        return true;
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
        return this.children.hashCode() * 29;
    }

    @Override
    public boolean isConjunction() {
        return true;
    }

    public Conjunction prepend(final PropositionalFormula firstChild) {
        final LinkedList<PropositionalFormula> newChildren;
        if (firstChild.isConjunction()) {
            newChildren = new LinkedList<PropositionalFormula>(((Conjunction)firstChild).children);
            newChildren.addAll(this.children);
        } else {
            newChildren = new LinkedList<PropositionalFormula>(this.children);
            newChildren.addFirst(firstChild);
        }
        return new Conjunction(newChildren);
    }

    @Override
    public String toString() {
        return String.format(
            "(%s)",
            this.children.stream().map(PropositionalFormula::toString).collect(Collectors.joining(" && "))
        );
    }

}
