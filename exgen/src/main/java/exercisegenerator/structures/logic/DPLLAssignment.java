package exercisegenerator.structures.logic;

import java.util.*;

import exercisegenerator.*;

public record DPLLAssignment(PropositionalVariable variable, boolean value, DPLLNode successor) {

    public DPLLAssignment addLeftmost(final Optional<DPLLAssignment> assignment) {
        return new DPLLAssignment(this.variable, this.value, this.successor.addLeftmost(assignment));
    }

    public DPLLAssignment addRightToLeftmost(final DPLLAssignment assignment) {
        return new DPLLAssignment(this.variable, this.value, this.successor.addRightToLeftmost(assignment));
    }

    String toStringRecursive(final boolean left, final int level) {
        return String.format(
            "%s%s\\edge node[midway,%s] {$%s = \\code{%d}$};%s",
            Main.lineSeparator,
            "  ".repeat(level + 1),
            left ? "left" : "right",
            this.variable().name,
            this.value() ? 1 : 0,
            this.successor().toStringRecursive(level + 1)
        );
    }

}
