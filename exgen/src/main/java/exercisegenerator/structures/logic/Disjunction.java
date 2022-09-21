package exercisegenerator.structures.logic;

import java.util.*;
import java.util.stream.*;

public class Disjunction extends PropositionalFormula {

    public final List<PropositionalFormula> children;

    public Disjunction(final List<PropositionalFormula> children) {
        this.children = children;
    }

    public Disjunction(final PropositionalFormula... children) {
        this(Arrays.asList(children));
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

}
