package exercisegenerator.structures.logic;

public record Literal(PropositionalVariable variable, boolean negative) {

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

}
