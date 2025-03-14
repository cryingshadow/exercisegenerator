package exercisegenerator.structures.logic;

public record Literal(PropositionalVariable variable, boolean negative) implements Comparable<Literal> {

    @Override
    public boolean equals(final Object o) {
        if (o instanceof Literal) {
            final Literal other = (Literal)o;
            return this.variable.equals(other.variable) && this.negative == other.negative;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.variable.hashCode() * 7 + (this.negative ? 101 : 23);
    }

    @Override
    public String toString() {
        return this.negative() ? "\\neg " + this.variable().name : this.variable().name;
    }

    @Override
    public int compareTo(Literal o) {
        int result = this.variable().name.compareTo(o.variable().name);
        if (result != 0) {
            return result;
        }
        if (this.negative()) {
            return o.negative() ? 0 : 1;
        }
        return o.negative() ? -1 : 0;
    }

}
