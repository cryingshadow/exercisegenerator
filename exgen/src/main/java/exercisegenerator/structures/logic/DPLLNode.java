package exercisegenerator.structures.logic;

import java.util.*;

public record DPLLNode(Set<Clause> clauses, Optional<DPLLNode> left, Optional<DPLLNode> right) {

    public DPLLNode(final Set<Clause> clauses, final Optional<DPLLNode> left, final Optional<DPLLNode> right) {
        this.clauses = clauses;
        this.left = left;
        this.right = right;
    }

    public DPLLNode(final Set<Clause> clauses) {
        this(clauses, Optional.empty(), Optional.empty());
    }

    public DPLLNode(final Set<Clause> clauses, final Optional<DPLLNode> left) {
        this(clauses, left, Optional.empty());
    }

    public DPLLNode addLeftmost(final Optional<DPLLNode> node) {
        if (this.left.isEmpty()) {
            return new DPLLNode(this.clauses, node, this.right);
        }
        return new DPLLNode(this.clauses, Optional.of(this.left().get().addLeftmost(node)), this.right);
    }

    public DPLLNode addRightToLeftmost(final DPLLNode node) {
        if (this.left.isEmpty()) {
            return new DPLLNode(this.clauses, this.left, Optional.of(node));
        }
        return new DPLLNode(this.clauses, Optional.of(this.left().get().addRightToLeftmost(node)), this.right);
    }

    public Set<Clause> getLeftmostClauses() {
        if (this.left.isEmpty()) {
            return this.clauses;
        }
        return this.left.get().getLeftmostClauses();
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof DPLLNode) {
            final DPLLNode other = (DPLLNode)o;
            return this.clauses.equals(other.clauses) && this.left.equals(other.left) && this.right.equals(other.right);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.clauses.hashCode() * 3 + this.left.hashCode() * 5 + this.right.hashCode() * 7;
    }

    public boolean isSAT() {
        return this.clauses.isEmpty()
            || this.left.isPresent() && this.left.get().isSAT()
            || this.right.isPresent() && this.right.get().isSAT();
    }

}
