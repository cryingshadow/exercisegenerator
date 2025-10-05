package exercisegenerator.structures.logic;

import java.util.*;
import java.util.stream.*;

public class Conjunction extends PropositionalFormula {

    public static PropositionalFormula createConjunction(final List<? extends PropositionalFormula> children) {
        return Conjunction.createConjunction(children.stream());
    }

    public static PropositionalFormula createConjunction(final PropositionalFormula... children) {
        return Conjunction.createConjunction(Arrays.asList(children));
    }

    public static PropositionalFormula createConjunction(final Stream<? extends PropositionalFormula> stream) {
        final List<PropositionalFormula> children =
            stream
            .flatMap(child -> child.isConjunction() ? ((Conjunction)child).children.stream() : Stream.of(child))
            .toList();
        if (children.isEmpty()) {
            return True.TRUE;
        }
        if (children.size() == 1) {
            return children.get(0);
        }
        return new Conjunction(children);
    }

    public final List<PropositionalFormula> children;

    private Conjunction(final List<PropositionalFormula> children) {
        this.children = children;
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
    public List<PropositionalFormula> getChildren() {
        return this.children;
    }

    @Override
    public Set<String> getVariableNames() {
        return this.children
            .stream()
            .map(PropositionalFormula::getVariableNames)
            .flatMap(Set::stream)
            .collect(Collectors.toCollection(TreeSet::new));
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
    public PropositionalFormula replaceChild(final int index, final PropositionalFormula newChild) {
        final List<PropositionalFormula> newChildren = new ArrayList<PropositionalFormula>(this.children);
        newChildren.set(index, newChild);
        return Conjunction.createConjunction(newChildren);
    }

    @Override
    public String toString() {
        return String.format(
            "(%s)",
            this.children.stream().map(PropositionalFormula::toString).collect(Collectors.joining(" && "))
        );
    }

    @Override
    public <T> T visit(final FormulaVisitor<T> visitor) {
        return visitor.onConjunction(this.children.stream().map(child -> child.visit(visitor)).toList());
    }

}
