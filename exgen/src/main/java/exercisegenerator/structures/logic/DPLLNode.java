package exercisegenerator.structures.logic;

import java.util.*;
import java.util.stream.*;

import exercisegenerator.*;

public record DPLLNode(ClauseSet clauses, Optional<DPLLAssignment> left, Optional<DPLLAssignment> right) {

    public DPLLNode(
        final ClauseSet clauses,
        final Optional<DPLLAssignment> left,
        final Optional<DPLLAssignment> right
    ) {
        this.clauses = clauses;
        this.left = left;
        this.right = right;
    }

    public DPLLNode(final ClauseSet clauses) {
        this(clauses, Optional.empty(), Optional.empty());
    }

    public DPLLNode(final ClauseSet clauses, final Optional<DPLLAssignment> left) {
        this(clauses, left, Optional.empty());
    }

    public DPLLNode addLeftmost(final Optional<DPLLAssignment> assignment) {
        if (this.left.isEmpty()) {
            return new DPLLNode(this.clauses, assignment, this.right);
        }
        return new DPLLNode(this.clauses, Optional.of(this.left().get().addLeftmost(assignment)), this.right);
    }

    public DPLLNode addRightToLeftmost(final DPLLAssignment node) {
        if (this.left.isEmpty()) {
            return new DPLLNode(this.clauses, this.left, Optional.of(node));
        }
        return new DPLLNode(this.clauses, Optional.of(this.left().get().addRightToLeftmost(node)), this.right);
    }

    public ClauseSet getLeftmostClauses() {
        if (this.left.isEmpty()) {
            return this.clauses;
        }
        return this.left.get().successor().getLeftmostClauses();
    }

    public boolean isSAT() {
        return this.clauses.isEmpty()
            || this.left.isPresent() && this.left.get().successor().isSAT()
            || this.right.isPresent() && this.right.get().successor().isSAT();
    }

    String toStringRecursive(final int level) {
        final StringBuilder result = new StringBuilder();
        result.append(Main.lineSeparator);
        result.append("  ".repeat(level));
        if (this.left.isEmpty() && this.right.isEmpty()) {
            result.append(this.clausesToString());
        } else {
            result.append("[.");
            result.append(this.clausesToString());
            if (this.left.isPresent()) {
                result.append(this.left.get().toStringRecursive(true, level));
            }
            if (this.right.isPresent()) {
                result.append(this.right.get().toStringRecursive(false, level));
            }
            result.append(Main.lineSeparator);
            result.append("  ".repeat(level));
            result.append("]");
        }
        return result.toString();
    }

    private String clausesToString() {
        return String.format(
            "$\\{%s\\}$",
            this.clauses().stream().map(Clause::toString).collect(Collectors.joining(","))
        );
    }

    @Override
    public String toString() {
        if (this.left.isEmpty() && this.right.isEmpty()) {
            return String.format("\\Tree [.%s ];", this.clausesToString());
        } else {
            return "\\Tree" + this.toStringRecursive(1);
        }
    }

}
