package exercisegenerator.structures.logic;

import java.util.*;
import java.util.stream.*;

public class Disjunction extends PropositionalFormula {

    public static PropositionalFormula createDisjunction(final List<PropositionalFormula> children) {
        return Disjunction.createDisjunction(children.stream());
    }

    public static PropositionalFormula createDisjunction(final PropositionalFormula... children) {
        return Disjunction.createDisjunction(Arrays.stream(children));
    }

    public static PropositionalFormula createDisjunction(final Stream<PropositionalFormula> stream) {
        final List<PropositionalFormula> children =
            stream
            .flatMap(child -> child.isDisjunction() ? ((Disjunction)child).children.stream() : Stream.of(child))
            .toList();
        if (children.isEmpty()) {
            return False.FALSE;
        }
        if (children.size() == 1) {
            return children.get(0);
        }
        return new Disjunction(children);
    }

    public final List<PropositionalFormula> children;

    private Disjunction(final List<PropositionalFormula> children) {
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
    public PropositionalFormula replaceChild(final int index, final PropositionalFormula newChild) {
        final List<PropositionalFormula> newChildren = new ArrayList<PropositionalFormula>(this.children);
        newChildren.set(index, newChild);
        return Disjunction.createDisjunction(newChildren);
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
